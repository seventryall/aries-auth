package com.star.aries.auth.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@Controller
public class LoginController {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    /**
     * 统一登录页面
     *
     * @return
     */
    @GetMapping("/oauth2/login")
    public String loginPage(HttpServletResponse response) {
        return "login";
    }

    @GetMapping("/oauth2/welcome")
    public String welcome(HttpServletResponse response) {
        return "welcome";
    }

    @GetMapping("/oauth2/welcome2")
    public String welcome2(HttpServletResponse response) {
        return "welcome2";
    }

    @GetMapping("/oauth2/callback")
    public String callback() {
        return "callback";
    }

}
