package com.star.aries.auth.storage.redis;

import com.star.aries.auth.storage.redis.interf.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sj
 * redis缓存服务
 */
@Primary
@Component
public class RedisCacheService implements CacheService {

    @Autowired
    @Qualifier("redisTemplateClient")
    private RedisCache redisCache;

    @Override
    public boolean hasKey(String key) {
        return redisCache.hasKey(key);
    }

    @Override
    public boolean set(String key, Object value) {
        return redisCache.set(key, value);
    }

    @Override
    public boolean set(String key, Object value, int time) {
        return redisCache.set(key, value, time);
    }

    @Override
    public long incr(String key, long delta) {
        return redisCache.incr(key, delta);
    }


    @Override
    public boolean hset(String key, String item, Object value) {
        return redisCache.hset(key, item, value);
    }

    @Override
    public boolean hset(String key, String item, Object value, int time) {
        return redisCache.hset(key, item, value, time);
    }

    @Override
    public void hdel(String key, String... items) {
        redisCache.hdel(key, items);
    }

    @Override
    public boolean mapSet(String key, Map<String, Object> map) {
        return redisCache.hmset(key, map);
    }

    @Override
    public boolean mapSet(String key, Map<String, Object> map, int time) {
        return redisCache.hmset(key, map, time);
    }

    @Override
    public long sSet(String key, Object... values) {
        return redisCache.sSet(key, values);
    }

    @Override
    public long setRemove(String key, Object... values) {
        return redisCache.setRemove(key, values);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return redisCache.get(key, clazz);
    }

    @Override
    public String get(String key) {
        return redisCache.get(key);
    }

    @Override
    public <T> T hget(String key, String item, Class<T> clazz) {
        return redisCache.hget(key, item, clazz);
    }

    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        return redisCache.getList(key, clazz);
    }

    @Override
    public <T> Map<String, T> getMap(String key, Class<T> clazz) {
        return redisCache.hgetAll(key, clazz);
    }

    @Override
    public boolean lLeftSet(String key, Object value) {
        return redisCache.lLeftSet(key, value);
    }

    @Override
    public boolean lLeftSet(String key, Object value, int time) {
        return redisCache.lLeftSet(key, value, time);
    }

    @Override
    public boolean lLeftSet(String key, int time, Object... values) {
        return redisCache.lLeftSet(key, values, time);
    }

    @Override
    public <T> T lRightGet(String key, Class<T> clazz) {
        return redisCache.lRightGet(key, clazz);
    }

    @Override
    public <T> List<T> lRangeGet(String key, long start, long end, Class<T> clazz) {
        return redisCache.lGet(key, start, end, clazz);
    }

    @Override
    public <T> Set<T> getSet(String key, Class<T> clazz) {
        return redisCache.sGet(key, clazz);
    }

    @Override
    public boolean setBit(String key, int offset, boolean value) {
        return redisCache.setBit(key, offset, value);
    }

    @Override
    public boolean getBit(String key, int offset) {
        return redisCache.getBit(key, offset);
    }

    @Override
    public byte[] getBytes(String key) {
        return redisCache.getBytes(key);
    }

    @Override
    public void delKeys(String... keys) {
        redisCache.del(keys);
    }
}
