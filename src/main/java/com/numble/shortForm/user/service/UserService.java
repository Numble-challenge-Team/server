package com.numble.shortForm.user.service;

import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.entity.Authority;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.jwt.JwtTokenProvider;
import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional(readOnly = true)
@Service
@Slf4j
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;

    @Transactional
    public ResponseEntity<?> signUp(UserRequestDto.SignUp signUpDto) {

        usersRepository.findByEmail(signUpDto.getEmail()).ifPresent(user ->{
            throw new CustomException(ErrorCode.EXIST_EMAIL_ERROR);
        });

        Users user = Users.builder()
                .email(signUpDto.getEmail())
                .nickname(signUpDto.getNickname())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .roles(Collections.singletonList(Authority.ROLE_USER.name()))
                .build();

        usersRepository.save(user);
        return Response.success("회원가입에 성공했습니다.");
    }


    public UserResponseDto.TokenInfo login(UserRequestDto.Login loginDto) {
        Users users = usersRepository.findByEmail(loginDto.getEmail()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER));

        // email,password를 사용해 authenticationToken 생성
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        // authenticate 메서드가 실행될때 customUserDetailService에서 만든 loadUserByUsername 메서드가 실행
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // 인증 정보를 기반으로 JWT 토큰 생성
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // refreshToken redis 저장
        redisTemplate.opsForValue()
                .set("RT:" +authentication.getName(),tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);;

      return tokenInfo;
    }

    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissueDto) {

//        if (jwtTokenProvider.validationToken(reissueDto.getRefreshToken())) {
//            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM,"Refresh Token 정보가 유효하지 않습니다.");
//        }
        Authentication authentication = jwtTokenProvider.getAuthentication(reissueDto.getAccessToken());
        log.info("authentication getPrincipal {}",authentication.getPrincipal());
        log.info("authentication getname {}",authentication.getName());
        String refreshToken =(String) redisTemplate.opsForValue().get("RT:"+authentication.getName());

        if (ObjectUtils.isEmpty(refreshToken)) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM);
        }
        if(!refreshToken.equals(reissueDto.getRefreshToken())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM,"Refresh Token 정보가 일치하지 않습니다.");
        }

        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        return Response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);
    }

    public ResponseEntity<?> logout(UserRequestDto.Logout logoutDto) {

        if (!jwtTokenProvider.validationToken(logoutDto.getAccessToken())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM,"유효한 토큰이 아닙니다.");
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(logoutDto.getAccessToken());

        // Refresh Token 삭제
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:"+authentication.getName());
        }
        // 해당 accesstoken 유효시간 가지고  blackList로 저장하기
        Long expiration =jwtTokenProvider.getExpiration(logoutDto.getAccessToken());
        redisTemplate.opsForValue()
                .set(logoutDto.getAccessToken(),"logout",expiration,TimeUnit.MILLISECONDS);

        return Response.success("로그아웃 되었습니다.");
    }

//    public ResponseEntity<?> change(UserRequestDto.Change changeDto) {
//        return Response.success("회원정보 수정 되었습니다.");
//
//    }
    @Transactional
    public void signOut(String usersEmail) {
        Users users = usersRepository.findByEmail(usersEmail).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER));
        log.info("users {}",users);
         usersRepository.delete(users);
    }



}
