package com.star.aries.auth.implicit.facade.interceptor;

import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.implicit.facade.config.AuthClientConfigProperties;
import com.star.aries.auth.implicit.facade.util.CookieUtil;
import com.star.aries.auth.implicit.facade.util.JwtUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.jwt.Jwt;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

@WebFilter(filterName = "clientAuthenticationFilter", urlPatterns = {"/**"})
@Component
public class ClientAuthenticationFilter implements Filter {
    private AntPathMatcher matcher = new AntPathMatcher();

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    AuthClientConfigProperties authClientConfigProperties;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        final HttpServletRequest req = (HttpServletRequest) request;
        final HttpServletResponse resp = (HttpServletResponse) response;
        if (isPermit(req)) {
            chain.doFilter(req, resp);
            return;
        }
        String tokenValue = getToken(req);
        if (StringUtils.isBlank(tokenValue)) {
            MsgResult result = MsgResult.builder().statusText(StatusType.NO_LOGIN.getStatusText())
                    .status(StatusType.NO_LOGIN.getCode()).build();
            doResponse(req, resp, result);
            return;
        }
        try {
            Jwt jwt = jwtUtil.decode(tokenValue);
            boolean isExpired = jwtUtil.isExpired(jwt);
            if (!isExpired) {
                chain.doFilter(req, resp);
                return;
            }
            int allowAutoRefreshTokenSeconds = authClientConfigProperties.getAllowAutoRefreshTokenSeconds() == null ? 0 :
                    authClientConfigProperties.getAllowAutoRefreshTokenSeconds();
            boolean autoRefresh = allowAutoRefreshTokenSeconds > 0;
            Date refreshExpDate = new Date(System.currentTimeMillis() - allowAutoRefreshTokenSeconds * 1000L);
            autoRefresh = autoRefresh && new Date(jwtUtil.getExpiration(jwt)).after(refreshExpDate);
            if (autoRefresh) {
                int newExpiration=(int)((System.currentTimeMillis() + authClientConfigProperties.getAccessTokenValiditySeconds() * 1000L)/1000);
                String newToken= jwtUtil.encode(jwt.getClaims(), jwtUtil.decodeHeader(tokenValue),newExpiration);
                CookieUtil.addCookie(resp, authClientConfigProperties.getCookie().get("domain").toString(), "/", "access_token",
                        newToken, Integer.parseInt(authClientConfigProperties.getCookie().get("maxAge").toString()), false);
                chain.doFilter(req,resp);
            }else{
                MsgResult result = MsgResult.builder().statusText(StatusType.INVALID_TOKEN.getStatusText())
                        .status(StatusType.INVALID_TOKEN.getCode()).build();
                doResponse(req, resp, result);
            }
        } catch (Exception ex) {
            MsgResult result = MsgResult.builder().statusText(StatusType.INVALID_TOKEN.getStatusText())
                    .status(StatusType.INVALID_TOKEN.getCode()).build();
            doResponse(req, resp, result);
        }

    }

    private boolean isPermit(HttpServletRequest request) {
        String path = getRequestPath(request);
        String permitUrlPatterns= authClientConfigProperties.getLoginPermitUrlPatterns();
        if(StringUtils.isBlank(permitUrlPatterns)){
            return false;
        }
        List<String> excludePatterns=Lists.newArrayList(permitUrlPatterns.split(","));
        return match(excludePatterns, path);
    }


    private String getRequestPath(HttpServletRequest request) {
        String url = request.getServletPath();

        if (request.getPathInfo() != null) {
            url += request.getPathInfo();
        }

        return url;
    }

    private boolean match(List<String> patterns, String path) {
        if (patterns == null) {
            return false;
        }
        for (String pattern : patterns) {
            if (matcher.match(pattern, path)) {
                return true;
            }
        }
        return false;
    }

    private String getToken(HttpServletRequest request) {
        String tokenKey = "access_token";
        Map<String, String> cookieMap = CookieUtil.readCookie(request, tokenKey);
        String tokenValue = null;
        if (cookieMap != null && cookieMap.containsKey(tokenKey) &&
                StringUtils.isNotBlank(cookieMap.get(tokenKey))) {
            tokenValue = cookieMap.get(tokenKey);
        } else {
            Enumeration<String> headers = request.getHeaders("Authorization");
            while (headers.hasMoreElements()) {
                String value = headers.nextElement();
                if ((value.toLowerCase().startsWith("bearer"))) {
                    tokenValue = value.substring("bearer".length()).trim();
                    int commaIndex = tokenValue.indexOf(',');
                    if (commaIndex > 0) {
                        tokenValue = tokenValue.substring(0, commaIndex);
                    }
                }
            }
        }
        return tokenValue;
    }

    private void doResponse(HttpServletRequest request, HttpServletResponse response, MsgResult msgResult) {
        String origin = request.getHeader("Origin");// 获取源站
        response.setContentType("application/json;charset=utf-8");
        response.setHeader("Access-Control-Allow-Origin", origin);
        response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type, Depth, User-Agent, " +
                "X-File-Size, X-Requested-With," +
                "X-Requested-By, If-Modified-Since, X-File-Name, X-File-Type, Cache-Control, Origin");
        response.setHeader("Cache-Control", "no-cache");
        try {
            PrintWriter out = response.getWriter();
            out.print(JSON.toJSONString(msgResult));
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
