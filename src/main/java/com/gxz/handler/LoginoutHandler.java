package com.gxz.handler;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
//退出登录
public class LoginoutHandler implements LogoutSuccessHandler {
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException {
        //销毁session
        request.getSession().invalidate();
        // 清空cookie
        Cookie cookie = new Cookie("cookie",null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);// 设置 maxAge 为 0 来删除 Cookie
        response.addCookie(cookie);
        //设置为未登录
        authentication.setAuthenticated(false);
        //设置重定向地址
        response.sendRedirect("/index");
    }
}
