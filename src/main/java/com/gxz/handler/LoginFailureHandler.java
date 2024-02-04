package com.gxz.handler;

import com.gxz.service.SecurityService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
//登录失败
public class LoginFailureHandler implements AuthenticationFailureHandler {
    private final Integer lockCounts = 5;
    private final Long lockDuration= (long) (10*60*1000);//10分钟

    @Autowired
    private SecurityService securityService;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String userName = request.getParameter("userName");
        String enabled = securityService.LoginFailure(userName, lockCounts, lockDuration);
        response.sendRedirect("/mylogin?state=failure&enabled="+enabled);
    }
}
