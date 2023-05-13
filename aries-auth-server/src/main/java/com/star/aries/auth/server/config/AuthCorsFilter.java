package com.star.aries.auth.server.config;

import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URL;

@Component
@WebFilter(urlPatterns = {"/**"}, filterName = "AuthCorsFilter")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class AuthCorsFilter implements Filter {
    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse rep = (HttpServletResponse) response;

        //设置允许跨域的配置
        // 这里填写你允许进行跨域的主机ip（正式上线时可以动态配置具体允许的域名和IP）
        rep.setHeader("Access-Control-Max-Age", "3600");
        String origin = req.getHeader("Origin");// 获取源站
        // 允许的访问方法
        rep.setHeader("Access-Control-Allow-Methods", "POST, GET, PUT, DELETE, OPTIONS, PATCH");
        // Access-Control-Max-Age 用于 CORS 相关配置的缓存

        rep.setHeader("Access-Control-Allow-Origin", origin);
//        String referer = req.getHeader("Referer");
//        if (StringUtils.isNotBlank(referer)) {
//            URL url = new URL(referer);
//            origin = url.getProtocol() + "://" + url.getHost();
//            if(url.getPort()!=-1){
//                origin+=":"+url.getPort();
//            }
//            rep.addHeader("Access-Control-Allow-Origin", origin);
//        } else {
//            rep.addHeader("Access-Control-Allow-Origin", "*");
//        }

        rep.setHeader("Access-Control-Allow-Credentials", "true");
        rep.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Depth, SysUser-Agent, X-File-Size, X-Requested-With," +
                "X-Requested-By, If-Modified-Since, X-File-Name, X-File-Type, Cache-Control, Origin");
        rep.setCharacterEncoding("UTF-8");
        rep.setContentType("application/json; charset=utf-8");
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig arg0) throws ServletException {

    }
}

