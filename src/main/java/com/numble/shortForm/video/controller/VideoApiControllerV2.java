package com.numble.shortForm.video.controller;

import com.numble.shortForm.config.security.AuthenticationFacade;
import com.numble.shortForm.config.security.UserLibrary;
import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.user.jwt.JwtTokenProvider;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.service.VideoService;
import com.numble.shortForm.video.vimeo.VimeoLogic;
import io.swagger.annotations.Api;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v2/videos/")
@RequiredArgsConstructor
@Slf4j
@Api(tags = "비디오 API")
public class VideoApiControllerV2 {

    private final VideoService videoService;
    private final AuthenticationFacade authenticationFacade;
    private final UsersRepository usersRepository;
    private final VimeoLogic vimeoLogic;
    private final UserLibrary userLibrary;
    private static final String IMAGE_TYPE ="thumbnail";
    private final JwtTokenProvider jwtTokenProvider;
    private static final String AUTHORIZATION_HEADER ="Authorization";
    private static final String BEARER_TYPE  ="Bearer";


    @GetMapping("/main")
    public Result mainVideoList(Long videoId,Pageable pageable, HttpServletRequest request) {
        // header에서 guest 유무 확인

        checkToken(request);

        Long userId=userLibrary.retrieveUserId();

        if (userId == 0L) {
            log.info("로그인 안됌");
            return videoService.retrieveMainVideoListNotLoginNoOffset(videoId,pageable);
        }

        return videoService.retrieveMainVideoListNoOffset(videoId,pageable,userId);

    }

    private void checkToken(HttpServletRequest request) {
        String guest = request.getHeader("guest");

        if(guest==null)
            throw new CustomException(ErrorCode.NOT_ENOUGH_HEADER);

        String token = resolveToken(request);
        log.info("guest :{}",guest);
        if(guest.equals("false")){
            jwtTokenProvider.validationTokenIn(token);
        }
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_TYPE)) {
            return  bearerToken.substring(7);
        }
        return null;
    }
}
