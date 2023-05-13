package com.star.aries.auth.storage.redis.interf;

import redis.clients.jedis.JedisCluster;

import java.io.IOException;

public interface JedisClusterCallBack<T> {
    T invoke(JedisCluster cluster) throws IOException;
}
