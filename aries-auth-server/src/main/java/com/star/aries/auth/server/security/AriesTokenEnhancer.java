package com.star.aries.auth.server.security;

import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.server.service.UserService;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;

import java.util.HashMap;
import java.util.Map;

public class AriesTokenEnhancer implements TokenEnhancer {

    private UserService userService;

    public void setUserService(UserService userService){
        this.userService = userService;
    }

    @Override
    public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
        final Map<String, Object> additionalInfo = new HashMap<>();
        if(userService != null){
            SysUser user = userService.queryCurUser(authentication);
            if(user != null) {
                //additionalInfo.put("merchantId", user.getMerchantId());
                additionalInfo.put("name", user.getName());
                additionalInfo.put("user_id", user.getId());
                additionalInfo.put("tenant_id", user.getMerchantId());
            }
        }
        ((DefaultOAuth2AccessToken) accessToken).setAdditionalInformation(additionalInfo);
        return accessToken;
    }
}