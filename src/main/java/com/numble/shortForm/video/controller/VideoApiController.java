package com.numble.shortForm.video.controller;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.security.AuthenticationFacade;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.video.dto.request.EmbeddedVideoRequestDto;
import com.numble.shortForm.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/videos/")
@RequiredArgsConstructor
@Slf4j
public class VideoApiController {

    private final VideoService videoService;
    private final AuthenticationFacade authenticationFacade;
    private final UsersRepository usersRepository;

    @PostMapping("/upload/embedded")
    public ResponseEntity<?> uploadEmbeddedVideo(@ModelAttribute EmbeddedVideoRequestDto embeddedVideoRequestDto) throws IOException {
        String userEmail = authenticationFacade.getAuthentication().getName();
        log.info("find email Email {}",userEmail);
        Users users = usersRepository.findByEmail(userEmail).orElseThrow(() ->
                new CustomException(ErrorCode.NOT_FOUND_USER, "해당 유저 토큰에 해당하는 정보를 찾을수가 없습니다."));

        videoService.uploadEmbeddedVideo(embeddedVideoRequestDto,users.getId());

        return ResponseEntity.ok().body("ok");
    }
}
