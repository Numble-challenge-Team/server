package com.numble.shortForm.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    NOT_ENOGH_HEADER(HttpStatus.BAD_REQUEST,"헤더를 확인해주세요"),
    REFRESH_TOKEN_EXPIRE(HttpStatus.FORBIDDEN,"리프레쉬 토큰 만료됐습니다. 다시 로그인해주세요"),
    EXIST_REPORT(HttpStatus.FORBIDDEN,"신고는 한번만 가능합니다"),
    NOT_OWNER(HttpStatus.FORBIDDEN,"해당 글의 권한이 없습니다"),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND,"댓글이 존재하지 않습니다."),
    NON_LOGIN(HttpStatus.BAD_REQUEST,"로그인 된 상태가 아닙니다."),
    EXPIRE_TOKEN(HttpStatus.BAD_REQUEST,"토큰이 만료되었습니다."),
    NOT_FOUND_USER(HttpStatus.NOT_FOUND,"해당하는 유저가 존재하지 않습니다"),
    NOT_FOUND_VIDEO(HttpStatus.NOT_FOUND,"해당하는 비디오가 존재하지 않습니다"),
    EXIST_EMAIL_ERROR(HttpStatus.BAD_REQUEST,"이미 존재하는 이메일입니다."),
    EXIST_NICKNAME_ERROR(HttpStatus.BAD_REQUEST,"이미 존재하는 닉네임입니다."),

    BAD_REQUEST_PARAM(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),
    BAD_REQUEST_POST(HttpStatus.BAD_REQUEST, "글 입력값을 다시 확인하세요."),



    NONE_AUTHENTICATION_TOKEN(HttpStatus.BAD_REQUEST,"권한 정보가 없는 토큰입니다."),
    WRONG_TYPE_TOKEN(HttpStatus.BAD_REQUEST,"토큰의 타입이 틀렸습니다."),
    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED,"만료된 토큰입니다."),
    UNSUPPORTED_TOKEN(HttpStatus.BAD_REQUEST,"지원하지 않는 토큰입니다."),
    WRONG_TOKEN(HttpStatus.BAD_REQUEST,"토큰이 이상합니다"),
    UNKNOWN_ERROR(HttpStatus.BAD_REQUEST,"토큰이 유효하지 않습니다."),
    ACCESS_DENIED(HttpStatus.UNAUTHORIZED,"접근이 거부되었습니다."),
    FILE_CONVERT_ERROR(HttpStatus.BAD_REQUEST,"파일 변환에 실패했습니다.");
    private final HttpStatus httpStatus;
    private final String detail;
}
