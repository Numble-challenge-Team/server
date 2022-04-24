package com.numble.shortForm.user.dto.request;

import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiParam;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;


public class UserRequestDto {

    @Getter
    @Setter
    public static class SignUp{

        @ApiModelProperty(value = "이메일",dataType="String", example="oz@gmail.com")
        private String email;

        @ApiModelProperty(value = "비밀번호",dataType="String",example="qwe1234!")
        private String password;


        @ApiModelProperty(value = "닉네임",dataType="String",example="양철나무꾼")
        private String nickname;
    }


    @Getter
    @Setter
    public static class Login{

        @ApiModelProperty(value = "이메일",dataType="String", example="oz@gmail.com")
        private String email;
        @ApiModelProperty(value = "비밀번호",dataType="String",example="qwe1234!")
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email,password);
        }
    }

    @Getter
    @Setter
    public static class Reissue {
        @ApiModelProperty(value = "Access 토큰")
        private String accessToken;
        @ApiModelProperty(value = "Refresh 토큰")
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Logout {
        @ApiModelProperty(value = "Access 토큰")
        private String accessToken;
        @ApiModelProperty(value = "Refresh 토큰")
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Change{
        private String email;

        private String password;

        private String nickname;
    }
}
