package com.numble.shortForm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"해당하는 유저가 존재하지 않습니다"),
    EXIST_EMAIL_ERROR(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일입니다."),
    BAD_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    BAD_REQUEST_POST(HttpStatus.BAD_REQUEST, "글 입력값을 다시 확인하세요."),

    NONE_AUTHENTICATION_TOKEN(HttpStatus.BAD_REQUEST,"권한 정보가 없는 토큰입니다.");



    private final HttpStatus httpStatus;
    private final String detail;
}
