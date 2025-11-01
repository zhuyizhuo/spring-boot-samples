package com.example.redisdemo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class RedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 测试Redis连接状态
     */
    public void ping() {
        redisTemplate.getConnectionFactory().getConnection().ping();
    }

    // ============================ 字符串(String)操作 ============================

    /**
     * 设置字符串值
     * @param key 键
     * @param value 值
     */
    public void set(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置字符串值并指定过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public void set(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    /**
     * 获取字符串值
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * 删除键
     * @param key 键
     * @return 是否删除成功
     */
    public boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    /**
     * 批量删除键
     * @param keys 键集合
     * @return 删除成功的数量
     */
    public long delete(Collection<String> keys) {
        return redisTemplate.delete(keys);
    }

    /**
     * 设置键的过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    public boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    /**
     * 获取键的剩余过期时间
     * @param key 键
     * @param unit 时间单位
     * @return 剩余过期时间
     */
    public long getExpire(String key, TimeUnit unit) {
        return redisTemplate.getExpire(key, unit);
    }

    /**
     * 键是否存在
     * @param key 键
     * @return 是否存在
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    // ============================ 哈希(Hash)操作 ============================

    /**
     * 设置哈希字段值
     * @param key 键
     * @param field 字段
     * @param value 值
     */
    public void hSet(String key, String field, Object value) {
        redisTemplate.opsForHash().put(key, field, value);
    }

    /**
     * 批量设置哈希字段值
     * @param key 键
     * @param map 字段和值的映射
     */
    public void hPutAll(String key, Map<String, Object> map) {
        redisTemplate.opsForHash().putAll(key, map);
    }

    /**
     * 获取哈希字段值
     * @param key 键
     * @param field 字段
     * @return 值
     */
    public Object hGet(String key, String field) {
        return redisTemplate.opsForHash().get(key, field);
    }

    /**
     * 获取所有哈希字段和值
     * @param key 键
     * @return 字段和值的映射
     */
    public Map<Object, Object> hGetAll(String key) {
        return redisTemplate.opsForHash().entries(key);
    }

    /**
     * 删除哈希字段
     * @param key 键
     * @param fields 字段数组
     * @return 删除成功的数量
     */
    public long hDelete(String key, Object... fields) {
        return redisTemplate.opsForHash().delete(key, fields);
    }

    /**
     * 哈希字段是否存在
     * @param key 键
     * @param field 字段
     * @return 是否存在
     */
    public boolean hHasKey(String key, String field) {
        return redisTemplate.opsForHash().hasKey(key, field);
    }

    // ============================ 列表(List)操作 ============================

    /**
     * 在列表头部添加元素
     * @param key 键
     * @param values 值数组
     * @return 列表长度
     */
    public long lPush(String key, Object... values) {
        return redisTemplate.opsForList().leftPushAll(key, values);
    }

    /**
     * 在列表尾部添加元素
     * @param key 键
     * @param values 值数组
     * @return 列表长度
     */
    public long rPush(String key, Object... values) {
        return redisTemplate.opsForList().rightPushAll(key, values);
    }

    /**
     * 获取列表指定范围内的元素
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 元素列表
     */
    public List<Object> lRange(String key, long start, long end) {
        return redisTemplate.opsForList().range(key, start, end);
    }

    /**
     * 弹出列表头部元素
     * @param key 键
     * @return 头部元素
     */
    public Object lPop(String key) {
        return redisTemplate.opsForList().leftPop(key);
    }

    /**
     * 弹出列表尾部元素
     * @param key 键
     * @return 尾部元素
     */
    public Object rPop(String key) {
        return redisTemplate.opsForList().rightPop(key);
    }

    /**
     * 获取列表长度
     * @param key 键
     * @return 列表长度
     */
    public long lLen(String key) {
        return redisTemplate.opsForList().size(key);
    }

    // ============================ 集合(Set)操作 ============================

    /**
     * 添加元素到集合
     * @param key 键
     * @param values 值数组
     * @return 添加成功的数量
     */
    public long sAdd(String key, Object... values) {
        return redisTemplate.opsForSet().add(key, values);
    }

    /**
     * 获取集合所有元素
     * @param key 键
     * @return 元素集合
     */
    public Set<Object> sMembers(String key) {
        return redisTemplate.opsForSet().members(key);
    }

    /**
     * 判断元素是否在集合中
     * @param key 键
     * @param value 值
     * @return 是否存在
     */
    public boolean sIsMember(String key, Object value) {
        return redisTemplate.opsForSet().isMember(key, value);
    }

    /**
     * 从集合中移除元素
     * @param key 键
     * @param values 值数组
     * @return 移除成功的数量
     */
    public long sRemove(String key, Object... values) {
        return redisTemplate.opsForSet().remove(key, values);
    }

    /**
     * 获取集合大小
     * @param key 键
     * @return 集合大小
     */
    public long sCard(String key) {
        return redisTemplate.opsForSet().size(key);
    }

    // ============================ 有序集合(ZSet)操作 ============================

    /**
     * 添加元素到有序集合
     * @param key 键
     * @param value 值
     * @param score 分数
     * @return 是否添加成功
     */
    public boolean zAdd(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 获取有序集合指定范围内的元素（按分数排序）
     * @param key 键
     * @param start 开始索引
     * @param end 结束索引
     * @return 元素集合
     */
    public Set<Object> zRange(String key, long start, long end) {
        return redisTemplate.opsForZSet().range(key, start, end);
    }

    /**
     * 获取有序集合中元素的分数
     * @param key 键
     * @param value 值
     * @return 分数
     */
    public Double zScore(String key, Object value) {
        return redisTemplate.opsForZSet().score(key, value);
    }

    /**
     * 更新有序集合中元素的分数
     * @param key 键
     * @param value 值
     * @param score 新分数
     * @return 是否更新成功
     */
    public boolean zUpdateScore(String key, Object value, double score) {
        return redisTemplate.opsForZSet().add(key, value, score);
    }

    /**
     * 从有序集合中移除元素
     * @param key 键
     * @param values 值数组
     * @return 移除成功的数量
     */
    public long zRemove(String key, Object... values) {
        return redisTemplate.opsForZSet().remove(key, values);
    }

    /**
     * 获取有序集合大小
     * @param key 键
     * @return 集合大小
     */
    public long zCard(String key) {
        return redisTemplate.opsForZSet().size(key);
    }

    // ============================ 其他常用操作 ============================

    /**
     * 原子递增
     * @param key 键
     * @return 递增后的值
     */
    public long increment(String key) {
        return redisTemplate.opsForValue().increment(key);
    }

    /**
     * 原子递增指定值
     * @param key 键
     * @param delta 递增的值
     * @return 递增后的值
     */
    public long incrementBy(String key, long delta) {
        return redisTemplate.opsForValue().increment(key, delta);
    }

    /**
     * 原子递减
     * @param key 键
     * @return 递减后的值
     */
    public long decrement(String key) {
        return redisTemplate.opsForValue().decrement(key);
    }

    /**
     * 原子递减指定值
     * @param key 键
     * @param delta 递减的值
     * @return 递减后的值
     */
    public long decrementBy(String key, long delta) {
        return redisTemplate.opsForValue().decrement(key, delta);
    }

    /**
     * 查找匹配的键
     * @param pattern 匹配模式
     * @return 匹配的键集合
     */
    public Set<String> keys(String pattern) {
        return redisTemplate.keys(pattern);
    }
    
    /**
     * 获取键的类型
     * @param key 键
     * @return 键的类型字符串
     */
    public String getType(String key) {
        return redisTemplate.type(key).code();
    }

    /**
     * 清空当前数据库
     */
    public void flushDb() {
        redisTemplate.getConnectionFactory().getConnection().flushDb();
    }
}