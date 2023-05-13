package com.star.aries.auth.server.endpoint;


import com.star.aries.auth.common.enums.StatusType;
import com.star.aries.auth.common.pojo.MsgResult;
import com.star.aries.auth.common.util.MsgResultUtil;
import com.star.aries.auth.server.property.SecurityOAuth2Properties;
import com.star.aries.auth.server.util.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.endpoint.TokenEndpoint;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.security.Principal;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
public class LoginEndpoint {

    @Autowired
    private TokenEndpoint tokenEndpoint;

    @Autowired
    SecurityOAuth2Properties securityOAuth2Properties;



    /**
     * 登录
     *
     * @param username
     * @param password
     * @return
     */
    @RequestMapping(
            value = {"/oauth2/loginByPassword"},
            method = {RequestMethod.POST}
    )
    public MsgResult<OAuth2AccessToken> loginByPassword(String clientId, String clientSecret, String username, String password,
                                                        String scope,HttpServletResponse response) throws HttpRequestMethodNotSupportedException {
        if(StringUtils.isBlank(clientId)||StringUtils.isBlank(clientSecret)){
            clientId=securityOAuth2Properties.getResource().get("id");
            clientSecret=securityOAuth2Properties.getResource().get("secret");
        }
        Authentication principal = new UsernamePasswordAuthenticationToken(clientId, clientSecret, null);
        Map<String, String> param = new LinkedHashMap<>();
        param.put("grant_type", "password");
        param.put("username", username);
        param.put("password", password);
        param.put("validate_type", "password");
        if (StringUtils.isNotBlank(scope)) {
            param.put("scope", scope);
        }
        ResponseEntity<OAuth2AccessToken> responseEntity = tokenEndpoint.postAccessToken(principal, param);
        OAuth2AccessToken token = responseEntity.getBody();
        if(securityOAuth2Properties.getSetCookie()) {
            CookieUtil.addCookie(response, securityOAuth2Properties.getCookie().get("domain").toString(), "/",
                    SecurityOAuth2Properties.TOKEN_COOKIE_KEY, token.getValue(),
                    Integer.parseInt(securityOAuth2Properties.getCookie().get("maxAge").toString()), false);
        }
        MsgResult<OAuth2AccessToken> res = MsgResult.<OAuth2AccessToken>builder().status(StatusType.OK.getCode()).data(token).build();
        return res;
    }

    @RequestMapping(value = "/oauth2/token", method= RequestMethod.POST)
    public ResponseEntity<OAuth2AccessToken> postAccessToken(Principal principal,@RequestParam
            Map<String, String> parameters, HttpServletResponse response) throws HttpRequestMethodNotSupportedException {
        Authentication authentication= new UsernamePasswordAuthenticationToken(parameters.get("client_id"),
                parameters.get("client_secret"),null);
        ResponseEntity<OAuth2AccessToken> responseEntity=tokenEndpoint.postAccessToken(authentication,parameters);
        //System.out.println("access_token:"+responseEntity.getBody().getValue());
        if(securityOAuth2Properties.getSetCookie()) {
            CookieUtil.addCookie(response, securityOAuth2Properties.getCookie().get("domain").toString(), "/", SecurityOAuth2Properties.TOKEN_COOKIE_KEY,
                    responseEntity.getBody().getValue(),
                    Integer.parseInt(securityOAuth2Properties.getCookie().get("maxAge").toString()), false);
        }
        return responseEntity;
    }

    @RequestMapping(value = "/oauth2/logout", method= RequestMethod.GET)
    public MsgResult logout(HttpServletResponse response) {
        CookieUtil.addCookie(response,securityOAuth2Properties.getCookie().get("domain").toString(),"/",SecurityOAuth2Properties.TOKEN_COOKIE_KEY,null,0,false);
        return MsgResultUtil.buildSuccess(null);
    }

}

