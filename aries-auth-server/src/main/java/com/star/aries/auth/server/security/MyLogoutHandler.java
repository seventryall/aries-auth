package com.star.aries.auth.server.security;

import com.star.aries.auth.server.property.SecurityOAuth2Properties;
import com.star.aries.auth.server.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class MyLogoutHandler implements LogoutHandler {

    @Autowired
    SecurityOAuth2Properties securityOAuth2Properties;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            //String aa = request.getParameter("aa");//aa即为前端传来自定义跳转url地址
            String clientId=request.getParameter("client_id");
            String scope=request.getParameter("scope");
            String redirectUri=request.getParameter("redirect_uri");
            String redirect="/auth/oauth/authorize?response_type=code&client_id="+clientId+"&redirect_uri="+redirectUri+"&scope="+scope;
            CookieUtil.addCookie(response,securityOAuth2Properties.getCookie().get("domain").toString(),"/", SecurityOAuth2Properties.TOKEN_COOKIE_KEY,
                    null,0,false);
            response.sendRedirect(redirect);//实现自定义重定向
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

