package com.numble.shortForm.config.cookie;

import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseCookie;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RequiredArgsConstructor
@Slf4j
public class CookieUtil {



    public static void addCookie(HttpServletResponse response,String name,String value,int age) {



        ResponseCookie cookie = ResponseCookie.from(name, value)
                .maxAge(age)
                .path("/")
                .sameSite("None")
                .secure(true)
                .build();
        response.setHeader("set-cookie",cookie.toString());

    }

    public static String getCookie(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();
        log.info("request {}",request.getAttribute("domain"));
        log.info("url {}",request.getRequestURL());
        log.info("cookies null check {}",cookies ==null);
        if(cookies !=null ){
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(name)) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }


    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name,null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
    }
}
