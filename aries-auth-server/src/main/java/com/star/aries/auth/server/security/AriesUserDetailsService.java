package com.star.aries.auth.server.security;

import com.google.common.collect.Lists;
import com.star.aries.auth.dal.entity.Role;
import com.star.aries.auth.dal.entity.SysUser;
import com.star.aries.auth.server.service.UserService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AriesUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.loadUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户" + username + "不存在!");
        }
        //List<Authority> authorities = userService.getAuthoritiesByUserId(user.getUserId());
        List<Role> roles=userService.queryRolesByUserId(user.getId());
        return new User(
                username,
                user.getPassword(),
                mapToGrantedAuthorities(roles)
        );
    }

    private  List<GrantedAuthority> mapToGrantedAuthorities(List<Role> roles) {
        List<GrantedAuthority> grantedAuthorities = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(roles)){
            grantedAuthorities.addAll(roles.stream().map(p -> new SimpleGrantedAuthority(p.getCode()))
                    .collect(Collectors.toList()));
        }
        return grantedAuthorities;
    }
}
