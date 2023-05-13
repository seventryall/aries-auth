package com.star.aries.auth.storage.redis;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.*;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import redis.clients.jedis.HostAndPort;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Configuration
public class RedisConfig {
    @Autowired
    private RedisProperties properties;

    @Bean
    public JedisPool getJedisPool() {
        JedisPool pool = new JedisPool(jedisPoolConfig(), properties.getHost(), properties.getPort(),
                properties.getTimeout() == null ? 2000 : (int) properties.getTimeout().toMillis());
        return pool;
    }

    private JedisPoolConfig jedisPoolConfig() {
        JedisPoolConfig config = new JedisPoolConfig();
        RedisProperties.Pool pool = properties.getJedis().getPool();
        if (pool != null) {
            config.setMaxIdle(pool.getMaxIdle());
            config.setMaxTotal(pool.getMaxActive());
            config.setMaxWaitMillis(pool.getMaxWait().toMillis());
        }
        return config;
    }


    @Bean
    public JedisCluster getJedisCluster() {
        Set<HostAndPort> nodes = new LinkedHashSet<>();
        if (properties.getCluster() != null) {
            List<String> ipList = properties.getCluster().getNodes();
            ipList.forEach(ip -> {
                String[] arr = ip.split(":");
                nodes.add(new HostAndPort(arr[0], Integer.parseInt(arr[1])));
            });
        }
        JedisCluster cluster = new JedisCluster(nodes, jedisPoolConfig());
        return cluster;
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
        template.setConnectionFactory(factory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        om.setVisibility(PropertyAccessor.ALL, JsonAutoDetect.Visibility.ANY);
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean
    public RedisClusterConfiguration redisClusterConfiguration() {
        RedisClusterConfiguration redisClusterConfiguration = new RedisClusterConfiguration();
        if (properties.getCluster() == null || properties.getCluster().getNodes() == null) {
            return redisClusterConfiguration;
        }
        redisClusterConfiguration.setMaxRedirects(properties.getCluster().getMaxRedirects());
        List<RedisNode> nodeList = new ArrayList<>();
        List<String> hpList = properties.getCluster().getNodes();
        hpList.forEach(hp -> {
            String[] arr = hp.split(":");
            nodeList.add(new RedisNode(arr[0], Integer.parseInt(arr[1])));
        });
        redisClusterConfiguration.setClusterNodes(nodeList);
        return redisClusterConfiguration;
    }

    @Bean
    public RedisStandaloneConfiguration redisStandaloneConfiguration() {
        RedisStandaloneConfiguration configuration = new RedisStandaloneConfiguration();
        configuration.setDatabase(properties.getDatabase());
        configuration.setHostName(properties.getHost());
        configuration.setPort(properties.getPort());
        if (!StringUtils.isBlank(properties.getPassword())) {
            configuration.setPassword(RedisPassword.of(properties.getPassword()));
        }
        return configuration;
    }


    @Bean
    public JedisConnectionFactory jedisConnectionFactory(RedisStandaloneConfiguration redisStandaloneConfiguration,
                                                         RedisClusterConfiguration redisClusterConfiguration) {
        if (properties.getCluster() != null) {
            return new JedisConnectionFactory(redisClusterConfiguration, jedisPoolConfig());
        }
        return new JedisConnectionFactory(redisStandaloneConfiguration);
    }
}
