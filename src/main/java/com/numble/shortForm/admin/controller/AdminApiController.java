package com.numble.shortForm.admin.controller;

import com.numble.shortForm.admin.response.UserAdminResponse;
import com.numble.shortForm.config.security.AuthenticationFacade;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.user.service.UserService;
import com.numble.shortForm.video.dto.response.Result;
import com.numble.shortForm.video.service.VideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/admins")
@Slf4j
@Api(tags = "어드민 API")
public class AdminApiController {
    private final UserService userService;
    private final VideoService videoService;
    private final UsersRepository usersRepository;
    private final AuthenticationFacade authenticationFacade;



    @ApiOperation(value = "Admin 유저 리스트 조회", notes = "page넘버 size 넘버 parameter로 넘겨야함 ")
    @GetMapping("/userList")
    public ResponseEntity<?> getUserList(Pageable pageable) {

       Result result = userService.getUserList(pageable);

         return ResponseEntity.ok().body(result);
    }

//    @ApiOperation(value = "유저 개인 페이지,비디오 리스트와 유저정보 같이 반환")



    @ApiOperation(value = "Admin 비디오 삭제 ")
    @DeleteMapping("/delete/video/{videoId}")
    public ResponseEntity<?> deleteVideo(@PathVariable("videoId") Long videoId) {


        boolean bol = videoService.deleteVideoAdmin(videoId);

        Map<String,String> obj = new HashMap<>();

        if(bol== true)
            obj.put("isDeleted","true");
        else{
            obj.put("isDeleted","false");
        }
        return ResponseEntity.ok().body(obj);
    }


}
