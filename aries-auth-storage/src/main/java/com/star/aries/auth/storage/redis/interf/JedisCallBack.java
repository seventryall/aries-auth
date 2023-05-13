package com.star.aries.auth.storage.redis.interf;

import redis.clients.jedis.Jedis;

import java.io.IOException;

public interface JedisCallBack<T> {
    T invoke(Jedis redis) throws IOException;
}
