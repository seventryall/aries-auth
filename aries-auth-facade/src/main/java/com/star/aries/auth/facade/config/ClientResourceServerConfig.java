package com.star.aries.auth.facade.config;


import com.star.aries.auth.facade.interceptor.ClientAuthRequestFilter;
import com.star.aries.auth.facade.interceptor.ClientAuthenticationEntryPoint;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.ResourceServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.header.HeaderWriterFilter;

@Configuration
@EnableResourceServer
@ComponentScan(basePackages = {"com.star.aries.auth.facade"})
public class ClientResourceServerConfig extends ResourceServerConfigurerAdapter {
    @Autowired
    private TokenStore tokenStore;
    @Autowired
    private ClientAuthenticationEntryPoint clientAuthenticationEntryPoint;
    @Autowired
    private ClientAuthRequestFilter clientAuthRequestFilter;
    @Autowired
    private AuthClientConfigProperties authClientConfigProperties;

    @Override
    public void configure(ResourceServerSecurityConfigurer resources) throws Exception {
        resources
                .resourceId(authClientConfigProperties.getResource().get("id"))
                .tokenStore(tokenStore)//原来是 .tokenServices(tokenServices())去调用远程验证，现在只需要自己验证自己即可
                //把token信息记录在session中?
                .stateless(true);
        resources.authenticationEntryPoint(clientAuthenticationEntryPoint);
    }

    @Override
    public void configure(HttpSecurity http) throws Exception {
        String[] permitUrlPatterns;
        String loginPermitUrlPatterns=authClientConfigProperties.getLoginPermitUrlPatterns();
        if(StringUtils.isBlank(loginPermitUrlPatterns)){
            permitUrlPatterns=new String[]{"/test"};
        }else{
            permitUrlPatterns=loginPermitUrlPatterns.split(",");
        }
        http.requestMatchers().antMatchers("/**")
                .and()
                .authorizeRequests()
                .antMatchers(permitUrlPatterns).permitAll()
                .and()
                .authorizeRequests().anyRequest().authenticated();

        http.addFilterAfter(clientAuthRequestFilter, HeaderWriterFilter.class);
    }
}
