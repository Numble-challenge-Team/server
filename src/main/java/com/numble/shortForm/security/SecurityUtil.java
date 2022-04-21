package com.numble.shortForm.security;

import com.numble.shortForm.exception.CustomException;
import com.numble.shortForm.exception.ErrorCode;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtil {

    public static String getCurrentUserEmail() {
        final Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() ==null) {
            throw new CustomException(ErrorCode.NONE_AUTHENTICATION_TOKEN);
        }
        return authentication.getName();
    }
}
