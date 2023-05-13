package com.star.aries.auth.server.security;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.stereotype.Component;

@Component
//@Aspect
public class AuthTokenAspect {
    @Around("execution(* org.springframework.security.oauth2.provider.endpoint.TokenEndpoint.postAccessToken(..))")
    public Object handleControllerMethod(ProceedingJoinPoint pjp) throws Throwable {
//        WebResp<Object> response = WebResp.ok();
        Object proceed = null;
        try {
            proceed = pjp.proceed();
        } catch (Throwable throwable) {
            throw throwable;
        }
        if (proceed != null) {
            ResponseEntity<OAuth2AccessToken> responseEntity = (ResponseEntity<OAuth2AccessToken>)proceed;
            OAuth2AccessToken body = responseEntity.getBody();
            //HttpServletResponse response =((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
            //CookieUtil.addCookie(response,"localhost","/","uid",body.getValue(),3600,false);

            System.out.println(body.getValue());
//            if (responseEntity.getStatusCode().is2xxSuccessful()) {
//                response.setCode(0);
//                response.setMessage(WebResp.SUCCESS_MSG);
//                response.setData(body);
//            } else {
//                log.error("error:{}", responseEntity.getStatusCode().toString());
//                response.setCode(OpenApiRespEnum.OAUTH_GET_TOKEN_FAIL_USER.getCode());
//                response.setMessage(OpenApiRespEnum.OAUTH_GET_TOKEN_FAIL_USER.getDesc());
//            }
        }
        return ResponseEntity.status(200).body(proceed);
    }
}
