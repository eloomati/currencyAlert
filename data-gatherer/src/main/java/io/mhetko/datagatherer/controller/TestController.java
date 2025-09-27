package io.mhetko.datagatherer.controller;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StringRedisTemplate redisTemplate;
    public TestController(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }
    @GetMapping("/redis-test")
    public String testRedis() {
        try {
            redisTemplate.opsForValue().set("testKey", "testValue");
            return redisTemplate.opsForValue().get("testKey");
        } catch (Exception e) {
            return "Redis error: " + e.getMessage();
        }
    }
}

