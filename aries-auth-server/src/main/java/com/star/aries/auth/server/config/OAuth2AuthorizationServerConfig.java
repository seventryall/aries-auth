package com.star.aries.auth.server.config;

import com.star.aries.auth.server.property.SecurityOAuth2Properties;
import com.star.aries.auth.server.security.AriesClientDetailsService;
import com.star.aries.auth.server.security.AriesTokenEnhancer;
import com.star.aries.auth.server.security.AriesUserDetailsService;
import com.star.aries.auth.server.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtTokenStore;

import java.util.ArrayList;
import java.util.List;

/**
 * 授权服务器配置
 */
@Configuration
@EnableAuthorizationServer
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {
    /**
     * 认证管理器
     */
    @Autowired
    private AuthenticationManager authenticationManager;

    /**
     * 获取用户信息
     */
    @Autowired
    private AriesUserDetailsService ariesUserDetailsService;

    /**
     * 获取客户端信息
     */
    @Autowired
    private AriesClientDetailsService ariesClientDetailsService;

    @Autowired
    RedisConnectionFactory redisConnectionFactory;
    @Autowired
    UserService userService;
    @Autowired
    SecurityOAuth2Properties securityOAuth2Properties;


    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    /**
     * 配置令牌端点(Token Endpoint)的安全约束
     * Spring Security OAuth2会公开了两个端点，用于检查令牌（/oauth/check_token和/oauth/token_key），
     * 这些端点默认受保护denyAll()。tokenKeyAccess（）和checkTokenAccess（）方法会打开这些端点以供使用。
     *
     * @param security
     * @throws Exception
     */
    @Override
    public void configure(final AuthorizationServerSecurityConfigurer security) throws Exception {
        security.allowFormAuthenticationForClients().passwordEncoder(passwordEncoder())
                .tokenKeyAccess("permitAll()")
                .checkTokenAccess("isAuthenticated()");
    }

    /**
     * 配置客户端详情服务（ClientDetailsService）
     *
     * @param clients
     * @throws Exception
     */
    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.withClientDetails(ariesClientDetailsService);
    }

    /**
     * 配置授权（authorization）以及令牌（token）的访问端点和令牌服务(token services)
     *
     * @param endpoints
     * @throws Exception
     */
    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        List<TokenEnhancer> enhancers = new ArrayList<>();
        enhancers.add(tokenEnhancer());
        enhancers.add(accessTokenConverter());
        tokenEnhancerChain.setTokenEnhancers(enhancers);
        endpoints.authenticationManager(authenticationManager)
                .userDetailsService(ariesUserDetailsService)
                .tokenStore(tokenStore())
                .accessTokenConverter(accessTokenConverter())
                .tokenEnhancer(tokenEnhancerChain);
    }
    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey(securityOAuth2Properties.getJwtSignKey());
        return converter;
    }
    @Bean
    public TokenEnhancer tokenEnhancer() {
        AriesTokenEnhancer tokenEnhancer = new AriesTokenEnhancer();
        tokenEnhancer.setUserService(userService);
        return tokenEnhancer;
    }
    @Bean
    public TokenStore tokenStore() {
        //return new RedisTokenStore(redisConnectionFactory);
        //为什么不使用默认的RedisTokenStore,默认的使用pipeline，redis集群不适用
        //return new AriesRedisTokenStore(redisConnectionFactory);
        return new JwtTokenStore(accessTokenConverter());
    }
}
