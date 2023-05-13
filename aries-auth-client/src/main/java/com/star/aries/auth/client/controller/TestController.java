package com.star.aries.auth.client.controller;

import org.springframework.security.oauth2.provider.token.RemoteTokenServices;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
public class TestController {

    @RequestMapping("/test")
    public String test(){
        return "auth client test success!";
    }

    @RequestMapping("/user")
    public String user(Principal principal){
        return principal.getName();
    }
}
