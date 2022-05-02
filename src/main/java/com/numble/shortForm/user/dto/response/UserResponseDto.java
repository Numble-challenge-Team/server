package com.numble.shortForm.user.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class UserResponseDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class TokenInfo{
        @ApiModelProperty(value = "토큰 타입")
        private String grantType;
        @ApiModelProperty(value = "Access 토큰")
        private String accessToken;
        @ApiModelProperty(value = "Refresh 토큰")
        private String refreshToken;
        @ApiModelProperty(value = "Refresh 토큰 만료시간")
        private Long refreshTokenExpirationTime;

    }
}
