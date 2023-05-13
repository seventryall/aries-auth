package com.star.aries.auth.implicit.facade.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.Map;

@Data
@Configuration
@PropertySource("classpath:/auth-client-config.properties")
@ConfigurationProperties(prefix = "security.oauth2.client")
public class AuthClientConfigProperties {
    public static final String TOKEN_COOKIE_KEY="access_token";

    private Map<String,String> resource;

    private String loginPermitUrlPatterns;

    private String jwtSignKey;

    private Integer accessTokenValiditySeconds;

    private Integer allowAutoRefreshTokenSeconds;

    private Map<String,Object> cookie;
}
