package com.numble.shortForm.user.service;

import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.request.PageDto;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.dto.request.UpdateUserRequestDto;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserProfileDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.entity.Authority;
import com.numble.shortForm.user.entity.ProfileImg;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.jwt.JwtTokenProvider;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.entity.Thumbnail;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
import org.springframework.web.multipart.MultipartFile;

import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Transactional
@Service
@Slf4j
public class UserService {

    private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;
    private final S3Uploader s3Uploader;

    private static final String basic_url ="https://oz-s3-bucket.s3.ap-northeast-2.amazonaws.com/profile/basic.png";
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
                .profileImg(new ProfileImg("basic.png",basic_url))
                .build();

        usersRepository.save(user);
        return Response.success("??????????????? ??????????????????.");
    }


    public UserResponseDto.TokenInfo login(UserRequestDto.Login loginDto) {
        Users users = usersRepository.findByEmail(loginDto.getEmail()).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER));

        // email,password??? ????????? authenticationToken ??????
        UsernamePasswordAuthenticationToken authenticationToken = loginDto.toAuthentication();

        // authenticate ???????????? ???????????? customUserDetailService?????? ?????? loadUserByUsername ???????????? ??????
        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // ?????? ????????? ???????????? JWT ?????? ??????
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);

        // refreshToken redis ??????
        redisTemplate.opsForValue()
                .set("RT:" +authentication.getName(),tokenInfo.getRefreshToken(),tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);;

      return tokenInfo;
    }

    public ResponseEntity<?> reissue(UserRequestDto.Reissue reissueDto) {

        if (!jwtTokenProvider.refreshValidation(reissueDto.getRefreshToken())) {
            throw new CustomException(ErrorCode.NOT_VALID_REFRESH,"Refresh Token ????????? ???????????? ????????????.");
        }
//        log.info("refresh token ?????? ??????");

        Authentication authentication = jwtTokenProvider.getAuthentication(reissueDto.getAccessToken());


        String refreshToken =(String) redisTemplate.opsForValue().get("RT:"+authentication.getName());

        if (ObjectUtils.isEmpty(refreshToken)) {
            log.info("Refresh toekn??? ???????????? ????????????.");
            throw new CustomException(ErrorCode.NOT_VALID_REFRESH,"Refresh toekn??? ???????????? ????????????.");
        }


        if(!refreshToken.equals(reissueDto.getRefreshToken())) {
            log.info("Refresh Token ????????? it is not correct.");
            throw new CustomException(ErrorCode.NOT_VALID_REFRESH,"Refresh Token ????????? ???????????? ????????????.");
        }

        log.info("token info  ?????? ?????? ");
        UserResponseDto.TokenInfo tokenInfo = jwtTokenProvider.generateToken(authentication);
        log.info("token info {}",tokenInfo);

        redisTemplate.opsForValue()
                .set("RT:" + authentication.getName(), tokenInfo.getRefreshToken(), tokenInfo.getRefreshTokenExpirationTime(), TimeUnit.MILLISECONDS);
        log.info("redis tmeplate ?????? ??????");
        return Response.success(tokenInfo, "Token ????????? ?????????????????????.", HttpStatus.OK);

    }

    public ResponseEntity<?> logout(UserRequestDto.Logout logoutDto) {

        if (!jwtTokenProvider.validationToken(logoutDto.getAccessToken())) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM,"????????? ????????? ????????????.");
        }
        Authentication authentication = jwtTokenProvider.getAuthentication(logoutDto.getAccessToken());

        // Refresh Token ??????
        if (redisTemplate.opsForValue().get("RT:" + authentication.getName()) != null) {
            redisTemplate.delete("RT:"+authentication.getName());
        }
        // ?????? accesstoken ???????????? ?????????  blackList??? ????????????
        Long expiration =jwtTokenProvider.getExpiration(logoutDto.getAccessToken());
        redisTemplate.opsForValue()
                .set(logoutDto.getAccessToken(),"logout",expiration,TimeUnit.MILLISECONDS);

        return Response.success("???????????? ???????????????.");
    }

//    public ResponseEntity<?> change(UserRequestDto.Change changeDto) {
//        return Response.success("???????????? ?????? ???????????????.");
//
//    }
    @Transactional
    public void signOut(Long userId) {
        Users users = usersRepository.findById(userId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER));
        log.info("users {}",users);
         usersRepository.delete(users);
    }


    public UserProfileDto getProfile(Long userId) {
        return usersRepository.getProfile(userId);
    }

    public void updateProfile(UpdateUserRequestDto updateUserRequestDto,Long userId) throws IOException {

        Users users = usersRepository.findById(userId).orElseThrow(()->
                new CustomException(ErrorCode.NOT_FOUND_USER));

        MultipartFile img = updateUserRequestDto.getImg();
        ProfileImg profileImg=null;
        String nick=null;
        if(!updateUserRequestDto.getNickname().isEmpty()){
            usersRepository.findByNickname(updateUserRequestDto.getNickname()).ifPresent(user -> {
                throw new CustomException(ErrorCode.EXIST_NICKNAME_ERROR, String.format("[%s]??? ???????????? ??????????????????.", updateUserRequestDto.getNickname()));
            });
            nick = updateUserRequestDto.getNickname();
        }

        if (!updateUserRequestDto.getImg().isEmpty()) {
            String url = s3Uploader.uploadFile(updateUserRequestDto.getImg(),"thumbnail");
            profileImg = new ProfileImg(updateUserRequestDto.getImg().getOriginalFilename(),url);
        }
        System.out.println(nick);
        users.updateProfile(nick, profileImg);

        usersRepository.save(users);

    }

    public Result getUserList(Pageable pageable) {

        return usersRepository.retrieveAllUser(pageable);
    }
}
