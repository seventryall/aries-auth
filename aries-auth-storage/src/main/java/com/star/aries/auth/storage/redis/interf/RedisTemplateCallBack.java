package com.star.aries.auth.storage.redis.interf;

import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;

public interface RedisTemplateCallBack<T> {
    T invoke(RedisTemplate redis) throws IOException;
}
