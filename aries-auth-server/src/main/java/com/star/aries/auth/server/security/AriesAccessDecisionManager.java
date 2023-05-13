package com.star.aries.auth.server.security;


import org.apache.commons.lang3.StringUtils;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Iterator;

/**
 * 检查用户是否够权限访问资源
 * 参数authentication是从spring的全局缓存SecurityContextHolder中拿到的，里面是用户的权限信息
 * 参数object是url
 * 参数configAttributes所需的权限
 */
@Component
public class AriesAccessDecisionManager implements AccessDecisionManager {

    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes)
            throws AccessDeniedException, InsufficientAuthenticationException {
        if (null == configAttributes || configAttributes.size() <= 0) {
            return;
        }
        ConfigAttribute c;
        String needAuth;
        for (Iterator<ConfigAttribute> iterator = configAttributes.iterator(); iterator.hasNext(); ) {
            c = iterator.next();
            needAuth = c.getAttribute();
            //1.url没有配置任何权限，则为Default，登录即可访问
            if(StringUtils.equals(needAuth, "Default") ){
                if(authentication.getPrincipal() != null && authentication.getPrincipal() instanceof String && ((String) ((String) authentication.getPrincipal()).toLowerCase())
                        .startsWith("anonymous")){
                    System.out.println(1);
                }else{
                    return;
                }
            }
            if(needAuth.startsWith("{") && needAuth.endsWith("}")){
                System.out.println(needAuth);
            }
            for (GrantedAuthority ga : authentication.getAuthorities()) {
                //1.精确匹配
                if (needAuth.trim().equals(ga.getAuthority())) {
                    return;
                }
            }
        }
        throw new AccessDeniedException("no right");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
