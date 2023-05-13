package com.star.aries.auth.storage.redis.interf;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface RedisCache {

    /**
     * 指定缓存失效时间
     *
     * @param key  键
     * @param time 时间(秒)
     * @return
     */
    boolean expire(String key, int time);

    /**
     * 根据key 获取过期时间
     *
     * @param key 键 不能为null
     * @return 时间(秒) 返回0代表为永久有效
     */
    long getExpire(String key);

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    boolean hasKey(String key);

    /**
     * 删除缓存
     *
     * @param keys 可以传一个值 或多个
     */
    void del(String... keys);

    // ============================String=============================

    /**
     * 根据key 获取值
     *
     * @param key
     * @param clazz 类class
     * @return 类对象
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * 根据key 获取值
     *
     * @param key
     * @return String
     */
    String get(String key);

    /**
     * 根据key 获取值
     *
     * @param key
     * @param clazz 集合泛型对象
     * @return 集合对象
     */
    <T> List<T> getList(String key, Class<T> clazz);

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
     * @param key   键
     * @param delta 要增加几(大于0)
     * @return
     */
    long incr(String key, long delta);

    /**
     * 递减
     *
     * @param key   键
     * @param delta 要减少几(大于0)
     * @return
     */
    long decr(String key, long delta);

    // ================================Map=================================

    /**
     * HashGet
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return 值
     */
    <T> T hget(String key, String item, Class<T> clazz);

    /**
     * 获取hashKey对应的所有键值
     *
     * @param key 键
     * @return 对应的多个键值
     */
    <T> Map<String, T> hgetAll(String key, Class<T> clazz);

    /**
     * HashSet
     *
     * @param key 键
     * @param map 对应多个键值
     * @return true 成功 false 失败
     */
    boolean hmset(String key, Map<String, Object> map);

    /**
     * HashSet 并设置时间
     *
     * @param key  键
     * @param map  对应多个键值
     * @param time 时间(秒)
     * @return true成功 false失败
     */
    boolean hmset(String key, Map<String, Object> map, int time);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @return true 成功 false失败
     */
    boolean hset(String key, String item, Object value);

    /**
     * 向一张hash表中放入数据,如果不存在将创建
     *
     * @param key   键
     * @param item  项
     * @param value 值
     * @param time  时间(秒) 注意:如果已存在的hash表有时间,这里将会替换原有的时间
     * @return true 成功 false失败
     */
    boolean hset(String key, String item, Object value, int time);

    /**
     * 删除hash表中的值
     *
     * @param key   键 不能为null
     * @param items 项 可以使多个 不能为null
     */
    void hdel(String key, Object... items);

    /**
     * 判断hash表中是否有该项的值
     *
     * @param key  键 不能为null
     * @param item 项 不能为null
     * @return true 存在 false不存在
     */
    boolean hHasKey(String key, String item);

    /**
     * hash递增 如果不存在,就会创建一个 并把新增后的值返回
     *
     * @param key   键
     * @param item  项
     * @param delta 要增加几(大于0)
     * @return
     */
    double hincr(String key, String item, double delta);

    /**
     * hash递减
     *
     * @param key   键
     * @param item  项
     * @param delta 要减少几
     * @return
     */
    double hdecr(String key, String item, double delta);

    // ============================set=============================

    /**
     * 根据key获取Set中的所有值
     *
     * @param key 键
     * @return
     */
    <T> Set<T> sGet(String key, Class<T> clazz);

    /**
     * 根据value从一个set中查询,是否存在
     *
     * @param key   键
     * @param value 值
     * @return true 存在 false不存在
     */
    boolean sHasKey(String key, Object value);

    /**
     * 将set数据放入缓存
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 成功个数
     */
    long sSet(String key, Object... values);

    /**
     * 将set数据放入缓存并设置过期时间
     *
     * @param key    键
     * @param time   时间(秒)
     * @param values 值 可以是多个
     * @return 成功个数
     */
    long sSetAndExpire(String key, int time, Object... values);

    /**
     * 获取set缓存的长度
     *
     * @param key 键
     * @return
     */
    long sGetSize(String key);

    /**
     * 移除值为value的
     *
     * @param key    键
     * @param values 值 可以是多个
     * @return 移除的个数
     */
    long setRemove(String key, Object... values);

    // ===============================list=================================

    /**
     * 获取list缓存的内容
     *
     * @param key   键
     * @param start 开始
     * @param end   结束 0 到 -1代表所有值
     * @return
     */
    <T> List<T> lGet(String key, long start, long end, Class<T> clazz);

    /**
     * 获取list缓存的长度
     *
     * @param key 键
     * @return
     */
    long lGetSize(String key);

    /**
     * 通过索引 获取list中的值
     *
     * @param key   键
     * @param index 索引 index>=0时， 0 表头，1 第二个元素，依次类推；index<0时，-1，表尾，-2倒数第二个元素，依次类推
     * @return
     */
    <T> T lGetByIndex(String key, long index, Class<T> clazz);

    /**
     * list尾部删除元素，并返回删除元素
     *
     * @param key
     * @param clazz
     * @return
     */
    <T> T lRightGet(String key, Class<T> clazz);

    /**
     * list头部删除元素，并返回删除元素
     *
     * @param key
     * @param clazz
     * @return
     */
    <T> T lLeftGet(String key, Class<T> clazz);

    /**
     * list尾部添加元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    boolean lRightSet(String key, Object value);

    /**
     * list尾部添加元素，设置缓存时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    boolean lRightSet(String key, Object value, int time);

    /**
     * list尾部批量添加元素
     *
     * @param key    键
     * @param values 值
     * @return
     */
    boolean lRightSet(String key, Object... values);

    /**
     * list尾部批量添加元素，设置缓存时间
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return
     */
    boolean lRightSet(String key, int time, Object... values);

    /**
     * list头部添加元素
     *
     * @param key   键
     * @param value 值
     * @return
     */
    boolean lLeftSet(String key, Object value);

    /**
     * list头部添加元素，设置缓存时间
     *
     * @param key   键
     * @param value 值
     * @param time  时间(秒)
     * @return
     */
    boolean lLeftSet(String key, Object value, int time);

    /**
     * list头部批量添加元素
     *
     * @param key    键
     * @param values 值
     * @return
     */
    boolean lLeftSet(String key, Object... values);

    /**
     * list头部批量添加元素，设置缓存时间
     *
     * @param key    键
     * @param values 值
     * @param time   时间(秒)
     * @return
     */
    boolean lLeftSet(String key, int time, Object... values);

    /**
     * 根据索引修改list中的某条数据
     *
     * @param key   键
     * @param index 索引
     * @param value 值
     * @return
     */
    boolean lSetByIndex(String key, long index, Object value);

    /**
     * 移除N个值为value
     *
     * @param key   键
     * @param count 移除多少个
     * @param value 值
     * @return 移除的个数
     */
    long lRemove(String key, long count, Object value);

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

}
