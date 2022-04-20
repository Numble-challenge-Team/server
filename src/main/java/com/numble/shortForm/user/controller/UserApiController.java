package com.numble.shortForm.user.controller;

import com.numble.shortForm.response.Response;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.service.UserService;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
public class UserApiController {

    private final UserService userService;
    private final Response response;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserRequestDto.SignUp signUpDto) {

        return userService.signUp(signUpDto);
    }

    @PostMapping("/login")
    @ApiOperation(value = "로그인",notes = "로그인에 성공하면, accessToken, RefreshToken 반환")
    @ApiModelProperty
    public ResponseEntity<?> login(@RequestBody UserRequestDto.Login loginDto) {
       UserResponseDto.TokenInfo tokenInfo = userService.login(loginDto);

        return response.success(tokenInfo,"로그인 완료",HttpStatus.OK);
    }

    @PostMapping("/reissue")
    @ApiOperation(value = "Access Token 재발급 ")
    public ResponseEntity<?> reissue(@RequestBody UserRequestDto.Reissue reissueDto) {
        return userService.reissue(reissueDto);
    }

    @PostMapping("/logout")
    @ApiOperation(value = "로그아웃")
    public ResponseEntity<?> logout(@RequestBody UserRequestDto.Logout logoutDto) {
        return userService.logout(logoutDto);
    }


    @PutMapping("/change")
    @ApiOperation(value = "유저 정보 변경")
    public ResponseEntity<?> change(@RequestBody UserRequestDto.Change changeDto) {

        return userService.change(changeDto);
    }

    @GetMapping("/test")
    public ResponseEntity jwtCheck(@RequestParam String message) {

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
