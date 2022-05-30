package com.numble.shortForm.user.controller;

import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.response.ResponseDto;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.service.AuthService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users")
@Slf4j
@Api(tags = "유저 API")
public class AuthController {

    private final AuthService authService;

    @ApiOperation(value = "로그인", notes = "<big>로그인에 성공하면, accessToken, RefreshToken 반환</big>")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공 토큰생성", response = ResponseDto.class),
            @ApiResponse(code = 403, message = "권한 없음", response = ErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/login")
    public ResponseDto login(@RequestBody UserRequestDto.Login loginDto, HttpServletResponse response) {
        UserResponseDto.TokenInfo tokenInfo = authService.login(loginDto,response);

        return ResponseDto.builder()
                .state(200)
                .result("success")
                .data(tokenInfo)
                .message("로그인 성공")
                .build();
    }


    @ApiOperation(value = "Access Token 재발급 ", notes = "Access 토큰과 Refresh 토큰을 json 형식으로 전달")
    @ApiResponses({
            @ApiResponse(code = 200, message = "토큰 재발급", response = ResponseDto.class),
            @ApiResponse(code = 400, message = "Refresh Token 정보가 유효하지 않습니다.", response = ErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(HttpServletRequest request,HttpServletResponse response) {
        return authService.reissue(request,response);
    }


    @ApiOperation(value = "로그아웃", notes = "Access 토큰과 Refresh 토큰을 json 형식으로 전달")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그아웃", response = ResponseDto.class),
            @ApiResponse(code = 400, message = "Token 정보가 유효하지 않습니다.", response = ErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,HttpServletResponse response) {
        return authService.logout(request,response);
    }
}
