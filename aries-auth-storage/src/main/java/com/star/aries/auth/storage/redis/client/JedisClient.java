package com.star.aries.auth.storage.redis.client;


import com.star.aries.auth.common.util.ClassConvertUtil;
import com.star.aries.auth.common.util.JsonUtil;
import com.star.aries.auth.storage.redis.interf.RedisCache;
import com.star.aries.auth.storage.redis.invoker.RedisClientInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Component;
import redis.clients.jedis.JedisPool;

import java.util.*;

@Component("jedisClient")
public class JedisClient implements RedisCache {
    private static final Long LONG_ZERO = 0L;
    private static final String OK = "OK";

    @Autowired
    private JedisPool jedisPool;

    @Autowired
    private RedisClientInvoker invoker;

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    @Override
    public boolean expire(String key, int time) {
        boolean res = invoker.invoke(jedisPool, (jedis) -> {
            Long lResult = jedis.expire(key, time);
            return !LONG_ZERO.equals(lResult);
        });
        return res;
    }

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    @Override
    public long getExpire(String key) {
        long res = invoker.invoke(jedisPool, jedis -> jedis.ttl(key));
        return res;
    }

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    @Override
    public boolean hasKey(String key) {
        boolean res = invoker.invoke(jedisPool, jedis -> jedis.exists(key));
        return res;
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    @Override
    public void del(String... keys) {
        invoker.invoke(jedisPool, jedis -> {
            if (keys != null && keys.length > 0) {
                jedis.del(keys);
            }
            return null;
        });
    }

    // ============================String=============================

    /**
     * 根据key 获取值
     *
     * @param key
     * @param clazz 类class
     * @return 类对象
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        T res = invoker.invoke(jedisPool, jedis -> {
            String val = jedis.get(key);
            return val == null ? null : JsonUtil.toBean(val, clazz);
        });
        return res;
    }

    /**
     * 根据key 获取值
     * 返回 key 的值，如果 key 不存在时，返回 nil。
     * 如果 key 不是字符串类型，那么返回一个错误。
     *
     * @param key
     * @return String
     */
    @Override
    public String get(String key) {
        String res = invoker.invoke(jedisPool, jedisCulster -> {
            String val = jedisCulster.get(key);
            return val;
        });
        return res;
    }

    /**
     * 根据key 获取值
     *
     * @param key
     * @param clazz 集合泛型对象
     * @return 集合对象
     */
    @Override
    public <T> List<T> getList(String key, Class<T> clazz) {
        String str = this.get(key, String.class);
        return JsonUtil.toList(str, clazz);
    }

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    @Override
    public boolean set(String key, Object value) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            String str = jedis.set(key, JsonUtil.toString(value));
            return OK.equals(str);
        });
        return res;
    }

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    @Override
    public boolean set(String key, Object value, int time) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            String str = jedis.set(key, JsonUtil.toString(value));
            if (OK.equals(str) && time > 0) {
                Long lResult = jedis.expire(key, time);//jedis.pexpire(key, TimeUnit.MILLISECONDS.toSeconds(time));
                return !LONG_ZERO.equals(lResult);
            }
            return false;
        });
        return res;
    }

    /**
     * 递增
     *
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    @Override
    public long incr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        long res = invoker.invoke(jedisPool, jedis -> jedis.incrBy(key, delta));
        return res;
    }

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(大于0)
     * @return
     */
    @Override
    public long decr(String key, long delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        long res = invoker.invoke(jedisPool, jedis -> jedis.decrBy(key, delta));
        return res;
    }

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    @Override
    public <T> T hget(String key, String item, Class<T> clazz) {
        T res = invoker.invoke(jedisPool, jedis -> {
            String val = jedis.hget(key, item);
            if (val != null && val.length() != 0) {
                return JsonUtil.toBean(val, clazz);
            }
            return null;
        });
        return res;
    }

    @Override
    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) {
        Map<String, T> res = invoker.invoke(jedisPool, jedis -> {
            Map<String, String> map = jedis.hgetAll(key);
            Map<String, T> resultMap = new HashMap<>();
            if (map != null) {
                for (Map.Entry<String, String> item : map.entrySet()) {
                    T newValue = JsonUtil.toBean(item.getValue(), clazz);
                    resultMap.put(item.getKey(), newValue);
                }
                return resultMap;
            }
            return null;
        });
        return res;
    }

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    @Override
    public boolean hmset(String key, Map<String, Object> map) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            Map<String, String> resultMap = new HashMap<>();
            if (map != null) {
                for (Map.Entry<String, Object> item : map.entrySet()) {
                    String newValue = JsonUtil.toString(item.getValue());
                    resultMap.put(item.getKey(), newValue);
                }
                String strResult = jedis.hmset(key, resultMap);
                return OK.equals(strResult);
            }
            return false;
        });
        return res;
    }

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    @Override
    public boolean hmset(String key, Map<String, Object> map, int time) {
        boolean res = hmset(key, map);
        if (res && time > 0) {
            res = expire(key, time);
        }
        return res;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    @Override
    public boolean hset(String key, String item, Object value) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            jedis.hset(key, item, JsonUtil.toString(value));
            return true;
        });
        return res;
    }

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    @Override
    public boolean hset(String key, String item, Object value, int time) {
        boolean res = hset(key, item, value);
        if (res && time > 0) {
            res = expire(key, time);
        }
        return res;
    }

    /**
     * 删除hash表中的值
     *
     * @param key   键 不能为null
     * @param items 项 可以使多个 不能为null
     */
    @Override
    public void hdel(String key, Object... items) {
        invoker.invoke(jedisPool, jedis -> {
            List<String> strList = new ArrayList<>();
            for (Object item : items) {
                strList.add(JsonUtil.toString(item));
            }
            jedis.hdel(key, (new String[strList.size()]));
            return null;
        });
    }

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    @Override
    public boolean hHasKey(String key, String item) {
        boolean res = invoker.invoke(jedisPool, jedis -> jedis.hexists(key, item));
        return res;
    }

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param delta 要增加几(大于0)
     * @return
     */
    @Override
    public double hincr(String key, String item, double delta) {
        if (delta < 0) {
            throw new RuntimeException("递增因子必须大于0");
        }
        double res = invoker.invoke(jedisPool, jedis -> jedis.hincrByFloat(key, item, delta));
        return res;
    }

    /**
     * hash递减
     *
     * @param key   键
     * @param item  项
     * @param delta 要减少几
     * @return
     */
    @Override
    public double hdecr(String key, String item, double delta) {
        if (delta < 0) {
            throw new RuntimeException("递减因子必须大于0");
        }
        double res = invoker.invoke(jedisPool, jedis -> jedis.hincrByFloat(key, item, -delta));
        return res;
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    @Override
    public <T> Set<T> sGet(String key, Class<T> clazz) {
        Set<T> res = invoker.invoke(jedisPool, jedis -> {
            Set<String> strSet = jedis.smembers(key);
            Set<T> set = new HashSet<>();
            strSet.forEach(str -> set.add(JsonUtil.toBean(str, clazz)));
            return set;
        });
        return res;
    }

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    @Override
    public boolean sHasKey(String key, Object value) {
        boolean res = invoker.invoke(jedisPool, jedis -> jedis.sismember(key, JsonUtil.toString(value)));
        return res;
    }

    /**
     * 将数据放入set缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    @Override
    public long sSet(String key, Object... values) {
        long res = invoker.invoke(jedisPool, jedis -> {
            List<String> strList = new ArrayList<>();
            for (Object v : values) {
                strList.add(JsonUtil.toString(v));
            }
            return jedis.sadd(key, strList.toArray(new String[strList.size()]));
        });
        return res;
    }

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    @Override
    public long sSetAndExpire(String key, int time, Object... values) {
        long count = sSet(key, values);
        if (count > 0 && time > 0) {
            expire(key, time);
        }
        return count;

    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    @Override
    public long sGetSize(String key) {
        long res = invoker.invoke(jedisPool, jedis -> jedis.scard(key));
        return res;
    }

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    @Override
    public long setRemove(String key, Object... values) {
        long res = invoker.invoke(jedisPool, jedis -> {
            List<String> strList = ClassConvertUtil.convertToList(Arrays.asList(values), String.class);
            Long count = jedis.srem(key, strList.toArray(new String[strList.size()]));
            return count;
        });
        return res;
    }

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    @Override
    public <T> List<T> lGet(String key, long start, long end, Class<T> clazz) {
        List<T> res = invoker.invoke(jedisPool, jedis -> {
            List<String> strList = jedis.lrange(key, start, end);
            List<T> resultList = ClassConvertUtil.convertToList(strList, clazz);
            return resultList;
        });
        return res;
    }

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    @Override
    public long lGetSize(String key) {
        long res = invoker.invoke(jedisPool, jedis -> jedis.llen(key));
        return res;
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    @Override
    public <T> T lGetByIndex(String key, long index, Class<T> clazz) {
        T res = invoker.invoke(jedisPool, jedis -> {
            String val = jedis.lindex(key, index);
            return JsonUtil.toBean(val, clazz);
        });
        return res;
    }

    /**
     * list尾部删除元素，并返回删除元素
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> T lRightGet(String key, Class<T> clazz) {
        T res = invoker.invoke(jedisPool, jedis -> {
            String val = jedis.rpop(key);
            return JsonUtil.toBean(val, clazz);
        });
        return res;
    }

    /**
     * list头部删除元素，并返回删除元素
     *
     * @param key
     * @param clazz
     * @return
     */
    @Override
    public <T> T lLeftGet(String key, Class<T> clazz) {
        T res = invoker.invoke(jedisPool, jedis -> {
            String val = jedis.lpop(key);
            return JsonUtil.toBean(val, clazz);
        });
        return res;
    }

    /**
     * list尾部添加元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @Override
    public boolean lRightSet(String key, Object value) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            jedis.rpush(key, JsonUtil.toString(value));
            return true;
        });
        return res;
    }

    /**
     * list尾部添加元素,设置缓存时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    @Override
    public boolean lRightSet(String key, Object value, int time) {
        boolean res = lRightSet(key, value);
        if (res && time > 0) {
            res = expire(key, time);
        }
        return res;

    }

    /**
     * list尾部批量添加元素
     *
     * @param key    键
     * @param values 值
     * @return
     */
    @Override
    public boolean lRightSet(String key, Object... values) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            List<String> strList = new ArrayList<>();
            for (Object o : values) {
                strList.add(JsonUtil.toString(o));
            }
            jedis.rpush(key, strList.toArray(new String[strList.size()]));
            return true;
        });
        return res;
    }

    /**
     * list尾部批量添加元素，设置缓存时间
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return
     */
    @Override
    public boolean lRightSet(String key, int time, Object... values) {
        boolean res = lRightSet(key, values);
        if (res && time > 0) {
            res = expire(key, time);
        }
        return res;
    }


    /**
     * list头部添加元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    @Override
    public boolean lLeftSet(String key, Object value) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            jedis.lpush(key, JsonUtil.toString(value));
            return true;
        });
        return res;
    }

    /**
     * list头部添加元素，设置缓存时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    @Override
    public boolean lLeftSet(String key, Object value, int time) {
        boolean res = lLeftSet(key, value);
        if (res && time > 0) {
            res = expire(key, time);
        }
        return res;

    }

    /**
     * list头部批量添加元素
     *
     * @param key    键
     * @param values 值
     * @return
     */
    @Override
    public boolean lLeftSet(String key, Object... values) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            List<String> strList = new ArrayList<>();
            for (Object o : values) {
                strList.add(JsonUtil.toString(o));
            }
            jedis.lpush(key, strList.toArray(new String[strList.size()]));
            return true;
        });
        return res;
    }

    /**
     * list头部批量添加元素，设置缓存时间
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return
     */
    @Override
    public boolean lLeftSet(String key, int time, Object... values) {
        boolean res = lLeftSet(key, values);
        if (res && time > 0) {
            res = expire(key, time);
        }
        return res;
    }

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    @Override
    public boolean lSetByIndex(String key, long index, Object value) {
        boolean res = invoker.invoke(jedisPool, jedis -> {
            jedis.lset(key, index, JsonUtil.toString(value));
            return true;
        });
        return res;
    }

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    @Override
    public long lRemove(String key, long count, Object value) {
        long res = invoker.invoke(jedisPool, jedis -> jedis.lrem(key, count, JsonUtil.toString(value)));
        return res;
    }

    @Override
    public boolean setBit(String key, int offset, boolean value) {
        return invoker.invoke(jedisPool, jedis -> jedis.setbit(key, offset, value));
    }

    @Override
    public boolean getBit(String key, int offset) {
        return invoker.invoke(jedisPool, jedis -> jedis.getbit(key, offset));
    }

    @Override
    public byte[] getBytes(String key) {
        return invoker.invoke(jedisPool, jedis -> jedis.get(new StringRedisSerializer().serialize(key)));
    }
}
