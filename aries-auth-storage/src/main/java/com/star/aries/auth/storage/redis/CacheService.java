package com.star.aries.auth.storage.redis;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author sj
 * 缓存服务接口
 */
public interface CacheService {

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    boolean hasKey(String key);

    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    boolean set(String key, Object value);

    /**
     * 普通缓存放入并设置时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒) time要大于0 如果time小于等于0 将设置无限期
     * @return true成功 false 失败
     */
    boolean set(String key, Object value, int time);

    /**
     * 递增
     *
     * @param key
     * @return
     */
    long incr(String key, long delta);

    /**
     * HashSet,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    boolean hset(String key, String item, Object value);

    /**
     * HashSet,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    boolean hset(String key, String item, Object value, int time);


    /**
     * 参数HashSet中
     *
     * @param key
     * @param items
     */
    void hdel(String key, String... items);

    /**
     * MapSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    boolean mapSet(String key, Map<String, Object> map);

    /**
     * MapSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    boolean mapSet(String key, Map<String, Object> map, int time);

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    long sSet(String key, Object... values);

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    long setRemove(String key, Object... values);

    /**
     * Get String
     *
     * @param key
     * @return String
     */
    String get(String key);

    /**
     * 获取对象
     *
     * @param key
     * @param clazz 类class
     * @return 类对象
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 获取对象集合
     *
     * @param key
     * @param clazz
     * @return
     */
    <T> List<T> getList(String key, Class<T> clazz);

    /**
     * HashGet
     *
     * @param key   键 不能为null
     * @param item  项 不能为null
     * @param clazz 类对象
     * @return 值
     */
    <T> T hget(String key, String item, Class<T> clazz);

    /**
     * Get Map
     *
     * @param key
     * @param clazz
     * @return
     */
    <T> Map<String, T> getMap(String key, Class<T> clazz);

    /**
     * list头部添加元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    boolean lLeftSet(String key, Object value);

    boolean lLeftSet(String key, Object value, int time);

    boolean lLeftSet(String key, int time, Object... values);

    /**
     * list尾部删除元素，并返回删除元素
     *
     * @param key
     * @param clazz
     * @return
     */
    <T> T lRightGet(String key, Class<T> clazz);

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    <T> List<T> lRangeGet(String key, long start, long end, Class<T> clazz);

    /**
     * @param key
     * @param clazz
     * @return
     */
    <T> Set<T> getSet(String key, Class<T> clazz);

    /**
     * 对 key 所储存的字符串值，设置指定偏移量上的位
     *
     * @param key
     * @param offset
     * @param value
     * @return
     */
    boolean setBit(String key, int offset, boolean value);

    /**
     * 对 key 所储存的字符串值，获取指定偏移量上的位
     *
     * @param key
     * @param offset
     * @return
     */
    boolean getBit(String key, int offset);

    /**
     * 获取key的字节数组
     *
     * @param key
     * @return
     */
    byte[] getBytes(String key);

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    void delKeys(String... keys);


}
