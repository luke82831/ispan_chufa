package com.ispan.chufa.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisTestService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void testRedisConnection() {
        try {
            redisTemplate.opsForValue().set("testKey", "Hello Redis");
            String value = redisTemplate.opsForValue().get("testKey");
            System.out.println("🔵 Redis 測試成功！取出的值: " + value);
        } catch (Exception e) {
            System.err.println("🚨 Redis 連線失敗: " + e.getMessage());
        }
    }
}
