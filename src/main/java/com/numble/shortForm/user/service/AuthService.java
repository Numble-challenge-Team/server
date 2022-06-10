package com.numble.shortForm.user.service;

import com.numble.shortForm.config.cookie.CookieUtil;
import com.numble.shortForm.config.security.AuthenticationFacade;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.jwt.JwtTokenProvider;
import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UsersRepository usersRepository;
    private final RedisTemplate redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private static final int ACCESS_COOKIE_MAX_AGE = 1000*30;
    private static final int REFRESH_COOKIE_MAX_AGE = 1000*30*7;

    public UserResponseDto.TokenInfo login(UserRequestDto.Login loginDto, HttpServletResponse response) {

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
                .set("RT:" +authentication.getName(),tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        CookieUtil.addCookie(response,"refreshToken",tokenInfo.getRefreshToken(),REFRESH_COOKIE_MAX_AGE);
        CookieUtil.addCookie(response,"accessToken",tokenInfo.getAccessToken(),ACCESS_COOKIE_MAX_AGE);

        return tokenInfo;

    }

        public ResponseEntity<?> reissue(HttpServletRequest request,HttpServletResponse response) {

            String refreshToken_cookie = CookieUtil.getCookie(request, "refreshToken");
            String accessToken = CookieUtil.getCookie(request, "accessToken");

            if (!jwtTokenProvider.refreshValidation(refreshToken_cookie)) {
                throw new CustomException(ErrorCode.NOT_VALID_REFRESH,"Refresh Token 정보가 유효하지 않습니다.");
            }


            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);


            String refreshToken =(String) redisTemplate.opsForValue().get("RT:"+authentication.getName());

            if (ObjectUtils.isEmpty(refreshToken)) {
                log.info("Refresh toekn이 존재하지 않습니다.");
                throw new CustomException(ErrorCode.NOT_VALID_REFRESH,"Refresh toekn이 존재하지 않습니다.");
            }


            if(!refreshToken.equals(refreshToken_cookie)) {
                log.info("Refresh Token 정보가 it is not correct.");
                throw new CustomException(ErrorCode.NOT_VALID_REFRESH,"Refresh Token 정보가 일치하지 않습니다.");
            }

            log.info("token info  생성 직전 ");
            UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
            log.info("token info {}",tokenInfo);

            redisTemplate.opsForValue()
                    .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
            log.info("redis tmeplate 토큰 저장");

            CookieUtil.addCookie(response,"refreshToken",tokenInfo.getRefreshToken(),REFRESH_COOKIE_MAX_AGE);
            CookieUtil.addCookie(response,"accessToken",tokenInfo.getAccessToken(),ACCESS_COOKIE_MAX_AGE);
            return Response.success(tokenInfo, "Token 정보가 갱신되었습니다.", HttpStatus.OK);

        }

        public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {

            String accessToken = CookieUtil.getCookie(request, "accessToken");

            if (!jwtTokenProvider.validationToken(accessToken)){
                throw new CustomException(ErrorCode.BAD_REQUEST_PARAM,"유효한 토큰이 아닙니다.");
            }
            Authentication authentication = jwtTokenProvider.getAuthentication(accessToken);

            // Refresh Token 삭제
            if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
                redisTemplate.delete("RT:"+authentication.getName());
            }
            // 해당 accesstoken 유효시간 가지고  blackList로 저장하기
            Long expiration =jwtTokenProvider.getExpiration(accessToken);
            redisTemplate.opsForValue()
                    .set(accessToken,"logout",expiration,TimeUnit.MILLISECONDS);
            CookieUtil.deleteCookie(response,"accessToken");
            CookieUtil.deleteCookie(response,"refreshToken");
            return Response.success("로그아웃 되었습니다.");
        }

}
