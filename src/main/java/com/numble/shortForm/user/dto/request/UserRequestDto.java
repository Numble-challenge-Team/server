package com.numble.shortForm.user.dto.request;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import javax.validation.constraints.NotEmpty;


public class UserRequestDto {

    @Getter
    @Setter
    public static class SignUp{
        private String email;

        private String password;

        private String nickname;
    }


    @Getter
    @Setter
    public static class Login{
        private String email;
        private String password;

        public UsernamePasswordAuthenticationToken toAuthentication() {
            return new UsernamePasswordAuthenticationToken(email,password);
        }
    }

    @Getter
    @Setter
    public static class Reissue {
        private String accessToken;
        private String refreshToken;
    }

    @Getter
    @Setter
    public static class Logout {
        private String accessToken;
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
