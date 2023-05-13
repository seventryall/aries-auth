package com.star.aries.auth.server.controller;

import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.server.util.CookieUtil;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(){
        return "auth server test success!";
    }


    @PostMapping("/setCookie")
    public SysUser cookie(HttpServletResponse response){
        CookieUtil.addCookie(response,"localhost","/","test","test",3600,false);
        return SysUser.builder().name("123").build();
    }
}
