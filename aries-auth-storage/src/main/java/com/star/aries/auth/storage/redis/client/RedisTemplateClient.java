package com.star.aries.auth.storage.redis.client;


import com.star.aries.auth.storage.redis.interf.RedisCache;
import com.star.aries.auth.storage.redis.invoker.RedisClientInvoker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Component("redisTemplateClient")
public class RedisTemplateClient implements RedisCache {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisClientInvoker invoker;

    // =============================common============================

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    @Override
    public boolean expire(String key, int time) {
        boolean res = invoker.invoke(redisTemplate,
                redisTpl -> redisTpl.expire(key, time, TimeUnit.SECONDS));
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.getExpire(key));
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.hasKey(key));
        return res;
    }

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    @SuppressWarnings("unchecked")
    @Override
    public void del(String... keys) {
        invoker.invoke(redisTemplate, redisTpl -> {
            if (keys != null && keys.length > 0) {
                if (keys.length == 1) {
                    redisTpl.delete(keys[0]);
                } else {
                    redisTpl.delete(CollectionUtils.arrayToList(keys));
                }
            }
            return null;
        });
    }

    // ============================String=============================

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    @Override
    public String get(String key) {
        String res = invoker.invoke(redisTemplate, redisTpl -> (String) redisTpl.opsForValue().get(key));
        return res;
    }

    /**
     * 根据key 获取值
     *
     * @param key
     * @param clazz 类class
     * @return 类对象
     */
    @Override
    public <T> T get(String key, Class<T> clazz) {
        T res = invoker.invoke(redisTemplate, redisTpl -> (T) redisTpl.opsForValue().get(key));
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
        List<T> res = invoker.invoke(redisTemplate, redisTpl -> (List<T>) redisTpl.opsForValue().get(key));
        return res;
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForValue().set(key, value);
            return true;
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            if (time > 0) {
                redisTpl.opsForValue().set(key, value, time, TimeUnit.SECONDS);
            } else {
                set(key, value);
            }
            return true;
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForValue().increment(key, delta));
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForValue().decrement(key, delta));
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
    public Object hGetObj(String key, String item) {
        return redisTemplate.opsForHash().get(key, item);
    }

    @Override
    public <T> T hget(String key, String item, Class<T> clazz) {
        T res = invoker.invoke(redisTemplate, redisTpl -> (T) hGetObj(key, item));
        return res;
    }

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    public Map<Object, Object> hmget(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    @Override
    public <T> Map<String, T> hgetAll(String key, Class<T> clazz) {
        Map<String, T> res = invoker.invoke(redisTemplate, redisTpl -> {
            Map<Object, Object> map = redisTpl.opsForHash().entries(key);
            Map<String, T> resultMap = new HashMap<>();
            if (map != null) {
                for (Map.Entry<Object, Object> item : map.entrySet()) {
                    String newKey = (String) item.getKey();
                    T newValue = (T) item.getValue();
                    resultMap.put(newKey, newValue);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForHash().putAll(key, map);
            return true;
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForHash().put(key, item, value);
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
        invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForHash().delete(key, items);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForHash().hasKey(key, item));
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
        double res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForHash().increment(key, item, delta));
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
        double res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForHash().increment(key, item, -delta));
        return res;
    }

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    public Set<Object> sGetObj(String key) {
        Set<Object> res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForSet().members(key));
        return res;
    }

    @Override
    public <T> Set<T> sGet(String key, Class<T> clazz) {
        Set<T> res = invoker.invoke(redisTemplate, redisTpl -> {
            Set<T> set = redisTpl.opsForSet().members(key);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForSet().isMember(key, value));
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForSet().add(key, values));
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
        long res = sSet(key, values);
        if (res > 0 && time > 0) {
            expire(key, time);
        }
        return res;
    }

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    @Override
    public long sGetSize(String key) {
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForSet().size(key));
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForSet().remove(key, values));
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
    public List<Object> lGet(String key, long start, long end) {
        List<Object> res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForList().range(key, start, end));
        return res;
    }

    @Override
    public <T> List<T> lGet(String key, long start, long end, Class<T> clazz) {
        List<T> res = invoker.invoke(redisTemplate, redisTpl -> {
            List<T> list = redisTpl.opsForList().range(key, start, end);
            return list;
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForList().size(key));
        return res;
    }

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    public Object lGetObjByIndex(String key, long index) {
        Object res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForList().index(key, index));
        return res;
    }

    @Override
    public <T> T lGetByIndex(String key, long index, Class<T> clazz) {
        T res = invoker.invoke(redisTemplate, redisTpl -> {
            Object obj = redisTpl.opsForList().index(key, index);
            return (T) obj;
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
        T res = invoker.invoke(redisTemplate, redisTpl -> {
            Object obj = redisTpl.opsForList().rightPop(key);
            return (T) obj;
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
        T res = invoker.invoke(redisTemplate, redisTpl -> {
            Object obj = redisTpl.opsForList().leftPop(key);
            return (T) obj;
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForList().rightPush(key, value);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForList().rightPushAll(key, values);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForList().leftPush(key, value);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForList().leftPushAll(key, values);
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
        boolean res = invoker.invoke(redisTemplate, redisTpl -> {
            redisTpl.opsForList().set(key, index, value);
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
        long res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForList().remove(key, count, value));
        return res;
    }


    @Override
    public boolean setBit(String key, int offset, boolean value) {
        boolean res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForValue().setBit(key, offset, value));
        return res;
    }

    @Override
    public boolean getBit(String key, int offset) {
        boolean res = invoker.invoke(redisTemplate, redisTpl -> redisTpl.opsForValue().getBit(key, offset));
        return res;
    }

    @Override
    public byte[] getBytes(final String key) {
        byte[] res = invoker.invoke(redisTemplate, redisTpl ->
                (byte[]) redisTpl.execute(new RedisCallback<byte[]>() {
                    @Override
                    public byte[] doInRedis(RedisConnection connection) throws DataAccessException {
                        byte[] bytes = connection.get(key.getBytes());
                        return bytes;
                    }
                }));
        return res;
    }
}
