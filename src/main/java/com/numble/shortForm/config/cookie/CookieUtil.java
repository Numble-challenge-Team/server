package com.numble.shortForm.config.cookie;

import com.numble.shortForm.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
@RequiredArgsConstructor
public class CookieUtil {



    public static void addCookie(HttpServletResponse response,String name,String value,int age) {

        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(age);
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

    }

    public static String getCookie(HttpServletRequest request, String name) {

        Cookie[] cookies = request.getCookies();

        if(cookies !=null || cookies.length>0 ){
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
