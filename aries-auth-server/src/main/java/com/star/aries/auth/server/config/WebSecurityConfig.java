package com.star.aries.auth.server.config;

import com.star.aries.auth.server.security.AriesAuthenticationProvider;
import com.star.aries.auth.server.security.MyFailureHandler;
import com.star.aries.auth.server.security.MyLogoutHandler;
import com.star.aries.auth.server.security.MySuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

import java.util.Arrays;

@Configuration
@Order(2)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    AriesAuthenticationProvider ariesAuthenticationProvider;

    /**
     * 默认实现了Inmemory，LDAP，JDBC方式的认证
     * 可以自定义AuthenticationProvider，实现多种方式验证用户信息
     *
     * @param auth
     * @throws Exception
     */
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(ariesAuthenticationProvider);
    }

    @Autowired
    MySuccessHandler mySuccessHandler;

    @Autowired
    MyFailureHandler myFailureHandler;

    @Autowired
    MyLogoutHandler myLogoutHandler;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
          http.cors().and().csrf().disable();
        http.requestMatchers()
                .antMatchers("/oauth/**","/token","/login","/oauth2/**","/logout","/login-error")
                .and()
                .authorizeRequests()
                .antMatchers("/oauth/**").authenticated() //需要登录认证
                .and()
                .formLogin()
                .and().logout()
                //.logoutSuccessUrl("https://www.baidu.com");
                .addLogoutHandler(myLogoutHandler);


                //.loginPage("/login");
                //.and().cors().and().csrf().disable();
                //.and().antMatcher("/user/**").cors().and().csrf().disable();
                //.successHandler(mySuccessHandler).failureHandler(myFailureHandler);

//        http.requestMatchers()
//                //.antMatchers("/**")
//                .antMatchers("/oauth/**","/token","/login")
//                .and()
//                .authorizeRequests().anyRequest().authenticated()
//                .and()
//                .formLogin();
                //.and().cors().and().csrf().disable();

    }

    @Override
    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        ProviderManager authenticationManager = new ProviderManager(Arrays.asList(ariesAuthenticationProvider));
        //不擦除认证密码，擦除会导致TokenBasedRememberMeServices因为找不到Credentials再调用UserDetailsService而抛出UsernameNotFoundException
        authenticationManager.setEraseCredentialsAfterAuthentication(false);
        return authenticationManager;
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        //解决静态资源被拦截的问题
        web.ignoring().antMatchers("/css/**","/js/**","/lib/**");
    }


}
