package com.example.redisdemo.controller;

import com.example.redisdemo.model.User;
import com.example.redisdemo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/redis")
public class RedisController {

    @Autowired
    private RedisService redisService;
    
    /**
     * 测试Redis连接状态的端点
     */
    @GetMapping("/ping")
    public ResponseEntity<Map<String, String>> ping() {
        // 尝试执行一个简单的Redis命令来验证连接
        try {
            redisService.ping();
            Map<String, String> response = new HashMap<>();
            response.put("status", "pong");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("status", "error");
            response.put("message", "Redis连接失败: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    // ============================ 字符串(String)操作演示 ============================

    @PostMapping("/string")
    public ResponseEntity<String> setString(@RequestParam String key, @RequestParam String value,
                                          @RequestParam(required = false) Long expire) {
        if (expire != null && expire > 0) {
            redisService.set(key, value, expire, TimeUnit.SECONDS);
            return ResponseEntity.ok("字符串设置成功，过期时间：" + expire + "秒");
        } else {
            redisService.set(key, value);
            return ResponseEntity.ok("字符串设置成功");
        }
    }

    @GetMapping("/string/{key}")
    public ResponseEntity<Object> getString(@PathVariable String key) {
        Object value = redisService.get(key);
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/string/{key}")
    public ResponseEntity<String> deleteKey(@PathVariable String key) {
        boolean deleted = redisService.delete(key);
        return deleted ? ResponseEntity.ok("键删除成功") : ResponseEntity.notFound().build();
    }

    @GetMapping("/exists/{key}")
    public ResponseEntity<Map<String, Boolean>> exists(@PathVariable String key) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("exists", redisService.hasKey(key));
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取键的类型
     */
    @GetMapping("/type/{key}")
    public ResponseEntity<Map<String, String>> getType(@PathVariable String key) {
        if (redisService.hasKey(key)) {
            String type = redisService.getType(key);
            Map<String, String> result = new HashMap<>();
            result.put("type", type);
            return ResponseEntity.ok(result);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/expire/{key}")
    public ResponseEntity<String> expire(@PathVariable String key, @RequestParam Long seconds) {
        // 检查键是否存在
        boolean exists = redisService.hasKey(key);
        
        // 如果键不存在，先创建一个默认值的键
        if (!exists) {
            redisService.set(key, ""); // 设置空字符串作为默认值
        }
        
        // 设置过期时间
        boolean success = redisService.expire(key, seconds, TimeUnit.SECONDS);
        return success ? ResponseEntity.ok("过期时间设置成功") : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("设置过期时间失败");
    }

    @GetMapping("/ttl/{key}")
    public ResponseEntity<Map<String, Long>> getTtl(@PathVariable String key) {
        long ttl = redisService.getExpire(key, TimeUnit.SECONDS);
        Map<String, Long> result = new HashMap<>();
        result.put("ttl", ttl);
        return ResponseEntity.ok(result);
    }

    // ============================ 对象存储演示 ============================

    @PostMapping("/user")
    public ResponseEntity<String> saveUser(@RequestBody User user) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        
        String key = "user:" + user.getId();
        redisService.set(key, user);
        return ResponseEntity.ok("用户保存成功：" + key);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<Object> getUser(@PathVariable Long id) {
        String key = "user:" + id;
        Object user = redisService.get(key);
        if (user != null) {
            return ResponseEntity.ok(user);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/user-with-expire")
    public ResponseEntity<String> saveUserWithExpire(@RequestBody User user, @RequestParam Long expireSeconds) {
        if (user.getCreateTime() == null) {
            user.setCreateTime(LocalDateTime.now());
        }
        user.setUpdateTime(LocalDateTime.now());
        
        String key = "user:" + user.getId();
        redisService.set(key, user, expireSeconds, TimeUnit.SECONDS);
        return ResponseEntity.ok("用户保存成功，过期时间：" + expireSeconds + "秒");
    }

    // ============================ 哈希(Hash)操作演示 ============================

    @PostMapping("/hash")
    public ResponseEntity<String> setHash(@RequestParam String key, @RequestParam String field, 
                                        @RequestParam String value) {
        redisService.hSet(key, field, value);
        return ResponseEntity.ok("哈希字段设置成功");
    }

    @GetMapping("/hash/{key}/{field}")
    public ResponseEntity<Object> getHashField(@PathVariable String key, @PathVariable String field) {
        Object value = redisService.hGet(key, field);
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/hash/{key}")
    public ResponseEntity<Map<Object, Object>> getHashAll(@PathVariable String key) {
        Map<Object, Object> hash = redisService.hGetAll(key);
        if (!hash.isEmpty()) {
            return ResponseEntity.ok(hash);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================ 列表(List)操作演示 ============================

    @PostMapping("/list")
    public ResponseEntity<String> addToList(@RequestParam String key, @RequestBody List<String> values,
                                          @RequestParam(defaultValue = "right") String direction) {
        long size;
        if ("left".equals(direction)) {
            size = redisService.lPush(key, values.toArray());
        } else {
            size = redisService.rPush(key, values.toArray());
        }
        return ResponseEntity.ok("列表添加成功，当前列表大小：" + size);
    }

    @GetMapping("/list/{key}")
    public ResponseEntity<List<Object>> getList(@PathVariable String key, 
                                              @RequestParam(defaultValue = "0") long start, 
                                              @RequestParam(defaultValue = "-1") long end) {
        List<Object> list = redisService.lRange(key, start, end);
        return ResponseEntity.ok(list);
    }

    @DeleteMapping("/list/pop")
    public ResponseEntity<Object> popFromList(@RequestParam String key, 
                                            @RequestParam(defaultValue = "left") String direction) {
        Object value;
        if ("left".equals(direction)) {
            value = redisService.lPop(key);
        } else {
            value = redisService.rPop(key);
        }
        if (value != null) {
            return ResponseEntity.ok(value);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // ============================ 集合(Set)操作演示 ============================

    @PostMapping("/set")
    public ResponseEntity<String> addToSet(@RequestParam String key, @RequestBody List<String> values) {
        long count = redisService.sAdd(key, values.toArray());
        return ResponseEntity.ok("集合添加成功，添加数量：" + count);
    }

    @GetMapping("/set/{key}")
    public ResponseEntity<Set<Object>> getSet(@PathVariable String key) {
        Set<Object> set = redisService.sMembers(key);
        return ResponseEntity.ok(set);
    }

    @GetMapping("/set/member")
    public ResponseEntity<Map<String, Boolean>> isMember(@RequestParam String key, @RequestParam String value) {
        Map<String, Boolean> result = new HashMap<>();
        result.put("isMember", redisService.sIsMember(key, value));
        return ResponseEntity.ok(result);
    }

    // ============================ 有序集合(ZSet)操作演示 ============================

    @PostMapping("/zset")
    public ResponseEntity<String> addToZSet(@RequestParam String key, 
                                          @RequestParam String value, 
                                          @RequestParam Double score) {
        boolean success = redisService.zAdd(key, value, score);
        return success ? ResponseEntity.ok("有序集合添加成功") : ResponseEntity.badRequest().body("添加失败");
    }

    @GetMapping("/zset/{key}")
    public ResponseEntity<Set<Object>> getZSet(@PathVariable String key, 
                                             @RequestParam(defaultValue = "0") long start, 
                                             @RequestParam(defaultValue = "-1") long end) {
        Set<Object> zset = redisService.zRange(key, start, end);
        return ResponseEntity.ok(zset);
    }

    @GetMapping("/zset/score")
    public ResponseEntity<Map<String, Double>> getZSetScore(@RequestParam String key, 
                                                          @RequestParam String value) {
        Double score = redisService.zScore(key, value);
        Map<String, Double> result = new HashMap<>();
        result.put("score", score);
        return ResponseEntity.ok(result);
    }

    // ============================ 计数器操作演示 ============================

    @PostMapping("/counter/increment")
    public ResponseEntity<Map<String, Long>> increment(@RequestParam String key) {
        long value = redisService.increment(key);
        Map<String, Long> result = new HashMap<>();
        result.put("value", value);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/counter/increment-by")
    public ResponseEntity<Map<String, Long>> incrementBy(@RequestParam String key, 
                                                      @RequestParam Long delta) {
        long value = redisService.incrementBy(key, delta);
        Map<String, Long> result = new HashMap<>();
        result.put("value", value);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/counter/decrement")
    public ResponseEntity<Map<String, Long>> decrement(@RequestParam String key) {
        long value = redisService.decrement(key);
        Map<String, Long> result = new HashMap<>();
        result.put("value", value);
        return ResponseEntity.ok(result);
    }

    // ============================ 键过期监听演示 ============================

    @PostMapping("/demo/expiration")
    public ResponseEntity<String> demoKeyExpiration(@RequestParam String prefix, 
                                                  @RequestParam Long expireSeconds) {
        String key = prefix + ":" + UUID.randomUUID().toString().substring(0, 8);
        String value = "这是一个测试过期的键值，将在" + expireSeconds + "秒后过期";
        
        redisService.set(key, value, expireSeconds, TimeUnit.SECONDS);
        return ResponseEntity.ok("已创建测试键：" + key + "，过期时间：" + expireSeconds + "秒，请注意查看日志");
    }

    // ============================ 键管理演示 ============================

    @GetMapping("/keys")
    public ResponseEntity<Set<String>> getKeys(@RequestParam(defaultValue = "*") String pattern) {
        Set<String> keys = redisService.keys(pattern);
        return ResponseEntity.ok(keys);
    }

    @DeleteMapping("/keys")
    public ResponseEntity<String> deleteKeys(@RequestParam String pattern) {
        Set<String> keys = redisService.keys(pattern);
        if (!keys.isEmpty()) {
            long count = redisService.delete(keys);
            return ResponseEntity.ok("已删除 " + count + " 个键");
        } else {
            return ResponseEntity.ok("没有找到匹配的键");
        }
    }
}