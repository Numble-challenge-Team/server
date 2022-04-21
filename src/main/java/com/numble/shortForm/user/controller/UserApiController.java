package com.numble.shortForm.user.controller;

import com.numble.shortForm.user.dto.request.UserRequestDto;
import com.numble.shortForm.user.service.UserService;
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

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody UserRequestDto.SignUp signUpDto) {

        return userService.signUp(signUpDto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserRequestDto.Login loginDto) {
        log.info("호출 완료");
        return userService.login(loginDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<?> reissue(@RequestBody UserRequestDto.Reissue reissueDto) {
        return userService.reissue(reissueDto);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody UserRequestDto.Logout logoutDto) {
        return userService.logout(logoutDto);
    }


    @PutMapping("/change")
    public ResponseEntity<?> change(@RequestBody UserRequestDto.SignUp signUpDto) {

        return userService.signUp(signUpDto);
    }

    @GetMapping("/test")
    public ResponseEntity jwtCheck(@RequestParam String message) {

        return ResponseEntity.status(HttpStatus.OK).body(message);
    }
}
