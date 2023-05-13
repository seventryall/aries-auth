package com.star.aries.auth.storage.redis.invoker;

import com.star.aries.auth.storage.redis.interf.JedisCallBack;
import com.star.aries.auth.storage.redis.interf.JedisClusterCallBack;
import com.star.aries.auth.storage.redis.interf.RedisTemplateCallBack;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisCluster;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.exceptions.JedisException;

import java.io.IOException;

@Component
public class RedisClientInvoker {

    public <T> T invoke(RedisTemplate redisTpl, RedisTemplateCallBack<T> action) {
        T obj = null;
        try {
            obj = action.invoke(redisTpl);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public <T> T invoke(JedisCluster jedisCluster, JedisClusterCallBack<T> action) {
        T obj = null;
        try {
            obj = action.invoke(jedisCluster);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return obj;
    }

    public <T> T invoke(JedisPool pool, JedisCallBack<T> action) {
        T obj = null;
        Jedis jedis = null;
        try {
            jedis = pool.getResource();
            obj = action.invoke(jedis);
        } catch (JedisException | IOException ex) {
            ex.printStackTrace();
        } finally {
            if (jedis != null) {
                if (jedis.isConnected()) {
                    jedis.close();
                }
            }
        }
        return obj;
    }

}
