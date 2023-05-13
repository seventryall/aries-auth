package com.star.aries.auth.server.property;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@ConfigurationProperties(prefix = "security.oauth2")
@Data
public class SecurityOAuth2Properties {

    public static final String TOKEN_COOKIE_KEY="access_token";

    private Map<String,String> resource;

    private String jwtSignKey;

    private Integer accessTokenValiditySeconds;

    private Integer allowAutoRefreshTokenSeconds;

    private Boolean setCookie;

    private Map<String,Object> cookie;

}
