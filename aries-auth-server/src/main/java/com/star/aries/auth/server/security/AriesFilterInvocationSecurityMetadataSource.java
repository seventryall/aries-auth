package com.star.aries.auth.server.security;


import com.star.aries.auth.dal.entity.Role;
import com.star.aries.auth.server.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


@Component
public class AriesFilterInvocationSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {

    @Autowired
    RoleService roleService;

    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        //获取当前访问url
        String url = ((FilterInvocation) object).getRequestUrl();
        List<ConfigAttribute> result = new ArrayList<>();
        List<Role> roles=roleService.queryRolesByUrl(url);
        if (!CollectionUtils.isEmpty(roles)) {
            for (Role role : roles) {
                ConfigAttribute conf = new SecurityConfig(role.getCode());
                result.add(conf);
            }
        }
        return result;
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
