package com.numble.shortForm.user.controller;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import com.numble.shortForm.exception.ErrorResponse;
import com.numble.shortForm.response.Response;
import com.numble.shortForm.response.ResponseDto;
import com.numble.shortForm.security.AuthenticationFacade;
import com.numble.shortForm.upload.S3Uploader;
import com.numble.shortForm.user.dto.request.TestDto;
import com.numble.shortForm.user.dto.request.UpdateUserRequestDto;
import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.dto.response.UserProfileDto;
import com.numble.shortForm.user.dto.response.UserResponseDto;
import com.numble.shortForm.user.entity.Users;
import com.numble.shortForm.user.repository.UsersRepository;
import com.numble.shortForm.user.service.UserService;
import io.swagger.annotations.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/users")
@Slf4j
@Api(tags = "유저 API")
public class UserApiController {

    private final UserService userService;
    private final UsersRepository usersRepository;
    private final AuthenticationFacade authenticationFacade;
    private final S3Uploader s3Uploader;
    private final static String IMAGE_TYPE ="profile";


    @ApiOperation(value = "회원가입", notes = "<big>로그인에 성공하면, accessToken, RefreshToken 반환</big>")
    @ApiResponses({
            @ApiResponse(code = 200, message = "가입 완료", response = Response.class),
            @ApiResponse(code = 403, message = "권한 없음", response = ErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserRequestDto.SignUp signUpDto) {

        return userService.signUp(signUpDto);
    }


    @ApiOperation(value = "로그인", notes = "<big>로그인에 성공하면, accessToken, RefreshToken 반환</big>")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그인 성공 토큰생성", response = ResponseDto.class),
            @ApiResponse(code = 403, message = "권한 없음", response = ErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/login")
    public ResponseDto login(@RequestBody UserRequestDto.Login loginDto) {
        UserResponseDto.TokenInfo tokenInfo = userService.login(loginDto);

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
    public ResponseEntity<?> reissue(@RequestBody UserRequestDto.Reissue reissueDto) {
        return userService.reissue(reissueDto);
    }


    @ApiOperation(value = "로그아웃", notes = "Access 토큰과 Refresh 토큰을 json 형식으로 전달")
    @ApiResponses({
            @ApiResponse(code = 200, message = "로그아웃", response = ResponseDto.class),
            @ApiResponse(code = 400, message = "Token 정보가 유효하지 않습니다.", response = ErrorResponse.class, responseContainer = "List")
    })
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody UserRequestDto.Logout logoutDto) {
        return userService.logout(logoutDto);
    }


    @ApiOperation(value = "이메일 중복체크")
    @ApiResponses({
            @ApiResponse(code = 200, message = "존재하지 않은 이메일입니다.", response = ResponseDto.class),
            @ApiResponse(code = 400, message = "존재하는 이메일입니다.", response = ErrorResponse.class, responseContainer = "List")
    })
    @ApiImplicitParam(name = "email", value = "이메일(json 형식으로 email=\"oz@gamil.com\")")
    @PostMapping("/validation/email")
    public ResponseEntity<?> isValidationEmail(@RequestBody Map<String, String> map) {

        String email = map.get("email");
        if (email == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM, "json의 email의 형식을 확인해주세요");
        }

        usersRepository.findByEmail(email).ifPresent(user -> {
            throw new CustomException(ErrorCode.EXIST_EMAIL_ERROR, String.format("[%s]는 존재하는 이메일입니다.", email));
        });
        return Response.success("", "존재하지 않는 이메일입니다.", HttpStatus.OK);
    }


    @ApiOperation(value = "닉네임 중복체크")
    @ApiResponses({
            @ApiResponse(code = 200, message = "생성가능한 닉네임", response = ResponseDto.class),
            @ApiResponse(code = 400, message = "이미 존재하는 닉네임", response = ErrorResponse.class, responseContainer = "List")
    })
    @ApiImplicitParam(name = "nickname", value = "닉네임(json 형식으로 nickname=\"오즈의마법사\")")
    @PostMapping("/validation/nickname")
    public ResponseEntity<?> isValidationNickname(@RequestBody Map<String, String> map) {
        String nickname = map.get("nickname");
        if (nickname == null) {
            throw new CustomException(ErrorCode.BAD_REQUEST_PARAM, "json의 nickname의 형식을 확인해주세요");
        }

        usersRepository.findByNickname(nickname).ifPresent(user -> {
            throw new CustomException(ErrorCode.EXIST_NICKNAME_ERROR, String.format("[%s]는 존재하는 닉네임입니다.", nickname));
        });

        return Response.success("", "존재하지 않는 닉네임입니다.", HttpStatus.OK);
    }


    @ApiOperation(value = "회원 탈퇴, 토큰필요")
    @DeleteMapping("/sign-out")
    public ResponseEntity signOut() {

        Long userId = retrieveUserId();

        if (userId == 0L) {
            throw new CustomException(ErrorCode.NONE_AUTHENTICATION_TOKEN,"유저 정보가 일치하지 않습니다.");
        }
        userService.signOut(userId);

        return Response.success("true", "탈퇴되없습니다.", HttpStatus.OK);
    }

    @ApiOperation(value = "이미지 가져오기", notes = "profile 이미지 가져오기,원본 파일 네임 넘겨주시면 됩니다.")
    @GetMapping("/retrieveImg/{filename}")
    public ResponseEntity<Resource> updateForm(@PathVariable("filename")String filename, HttpServletRequest request, HttpServletResponse response) throws IOException {


        Resource resource = s3Uploader.getObject(filename,IMAGE_TYPE);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename);
        headers.add("Content-type","image/png");
        return new ResponseEntity<>(resource, headers, HttpStatus.OK);

    }

    @ApiOperation(value = "프로필 조회",notes ="토큰 필요" )
    @GetMapping("/profile")
    public ResponseEntity getProfile() {

        Long userId = retrieveUserId();

        if (userId == 0L) {
            throw new CustomException(ErrorCode.NONE_AUTHENTICATION_TOKEN,"유저 정보가 일치하지 않습니다.");
        }
        return ResponseEntity.ok().body(userService.getProfile(userId));
    }

    @PutMapping("/update")
    public ResponseEntity updateProfile(UpdateUserRequestDto updateUserRequestDto) throws IOException {
        Long userId = retrieveUserId();

        if (userId == 0L) {
            throw new CustomException(ErrorCode.NONE_AUTHENTICATION_TOKEN,"유저 정보가 일치하지 않습니다.");
        }
        userService.updateProfile(updateUserRequestDto,userId);

        return ResponseEntity.ok().body("변경완료");
    }




    // userid 조회
    private Long retrieveUserId() {
        String userEmail = authenticationFacade.getAuthentication().getName();
        if(userEmail.equals("anonymousUser")){
            return 0L;
        }
        Users users = usersRepository.findByEmail(userEmail).orElseThrow(()->{
            throw new CustomException(ErrorCode.NOT_FOUND_USER,"유저가 조회되지 않습니다.");
        });
        return users.getId();
    }
}
