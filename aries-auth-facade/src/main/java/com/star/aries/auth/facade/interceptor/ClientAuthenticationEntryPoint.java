package com.star.aries.auth.facade.interceptor;

import com.alibaba.fastjson.JSON;
import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.pojo.MsgResult;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.common.exceptions.InvalidTokenException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 处理匿名用户访问无权限资源时的异常,也就是跟token相关的资源异常
 */
@Component
public class ClientAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws ServletException {
        MsgResult result = null;
        Throwable cause = authException.getCause();
        if (cause instanceof InvalidTokenException) {
            //token输入错误时处理
            result = MsgResult.builder().statusText(StatusType.INVALID_TOKEN.getStatusText())
                    .status(StatusType.INVALID_TOKEN.getCode()).build();
        } else if (authException instanceof InsufficientAuthenticationException) {
            //没有输入token情况
            result = MsgResult.builder().statusText(StatusType.NO_LOGIN.getStatusText())
                    .status(StatusType.NO_LOGIN.getCode()).build();
        } else {
            result = MsgResult.builder().statusText(StatusType.NO_LOGIN.getStatusText())
                    .status(StatusType.NO_LOGIN.getCode()).build();
        }

        String origin = request.getHeader("Origin");// 获取源站

        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Depth, SysUser-Agent, X-File-Size, X-Requested-With," +
                "X-Requested-By, If-Modified-Since, X-File-Name, X-File-Type, Cache-Control, Origin");
        response.setHeader("Cache-Control", "no-cache");
        try {
            PrintWriter out = response.getWriter();
            out.print(JSON.toJSONString(result));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
