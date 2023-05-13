package com.star.aries.auth.facade.interceptor;

import com.star.aries.auth.facade.config.AuthClientConfigProperties;
import com.star.aries.auth.facade.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Component
public class ClientAuthRequestFilter extends OncePerRequestFilter {

    @Autowired
    TokenStore tokenStore;
    @Autowired
    JwtAccessTokenConverter jwtAccessTokenConverter;
    @Autowired
    AuthClientConfigProperties authClientConfigProperties;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String tokenCookieKey=authClientConfigProperties.TOKEN_COOKIE_KEY;
        Map<String, String> cookieMap = CookieUtil.readCookie(request, tokenCookieKey);
        if (cookieMap != null && cookieMap.containsKey(tokenCookieKey)) {
            if (StringUtils.isBlank(cookieMap.get(tokenCookieKey)) || tokenStore == null) {
                filterChain.doFilter(request, response);
                return;
            }
            String access_token = cookieMap.get(tokenCookieKey);
            try {
                DefaultOAuth2AccessToken oAuth2AccessToken = (DefaultOAuth2AccessToken) tokenStore.readAccessToken(access_token);
                if (oAuth2AccessToken != null) {
                    int allowAutoRefreshTokenSeconds = authClientConfigProperties.getAllowAutoRefreshTokenSeconds() == null ? 0 :
                            authClientConfigProperties.getAllowAutoRefreshTokenSeconds();
                    boolean refreshToken = oAuth2AccessToken.isExpired() && allowAutoRefreshTokenSeconds > 0;
                    Date refreshExpDate = new Date(System.currentTimeMillis() - allowAutoRefreshTokenSeconds * 1000L);
                    refreshToken = refreshToken && oAuth2AccessToken.getExpiration().after(refreshExpDate);
                    if (refreshToken) {
                        oAuth2AccessToken.setExpiration(new Date(System.currentTimeMillis() + authClientConfigProperties.getAccessTokenValiditySeconds() * 1000L));
                        oAuth2AccessToken.setTokenType(UUID.randomUUID().toString());
                        OAuth2Authentication authentication = tokenStore.readAuthentication(oAuth2AccessToken);
                        OAuth2AccessToken newAccessToken = jwtAccessTokenConverter.enhance(oAuth2AccessToken, authentication);
                        access_token = newAccessToken.getValue();
                        CookieUtil.addCookie(response, authClientConfigProperties.getCookie().get("domain").toString(), "/", tokenCookieKey,
                                access_token, Integer.parseInt(authClientConfigProperties.getCookie().get("maxAge").toString()), false);
                        //tokenStore.storeAccessToken(oAuth2AccessToken, authentication);
                    }
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }

            HeaderMapRequestWrapper requestWrapper = new HeaderMapRequestWrapper(request);
            requestWrapper.addHeader("Authorization", "bearer " + access_token);
            filterChain.doFilter(requestWrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
