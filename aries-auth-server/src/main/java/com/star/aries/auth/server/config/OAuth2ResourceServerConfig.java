package com.star.aries.auth.server.config;

import com.star.aries.auth.server.interceptor.AriesAuthRequestFilter;
import com.star.aries.auth.server.interceptor.AriesAuthenticationEntryPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.web.cors.CorsUtils;

/**
 * 资源服务器配置
 */
@EnableResourceServer
@Configuration
@Order(1)
public class OAuth2ResourceServerConfig extends ResourceServerConfigurerAdapter {

    @Value("${security.oauth2.resource.id}")
    private String RESOURCE_ID;

    @Autowired
    private AriesAuthenticationEntryPoint ariesAuthenticationEntryPoint;

    @Autowired
    private AriesAuthRequestFilter ariesAuthRequestFilter;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) {
        resources.resourceId(RESOURCE_ID).stateless(true);
        //resources.accessDeniedHandler(cloudAccessDeniedHandler)
        resources.authenticationEntryPoint(ariesAuthenticationEntryPoint);

    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers("/test","/setCookie","/oauth2/**").permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();
        http.addFilterAfter(ariesAuthRequestFilter, HeaderWriterFilter.class);


//        http.authorizeRequests()
//                .antMatchers("/auth/*","/authLogin","/login","/test").permitAll()
//                .anyRequest().authenticated();
        //http.addFilterBefore(cloudFilterSecurityInterceptor, FilterSecurityInterceptor.class);
    }

}
