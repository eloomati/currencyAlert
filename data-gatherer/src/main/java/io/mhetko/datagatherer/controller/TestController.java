// data-gatherer/src/main/java/io/mhetko/datagatherer/controller/TestController.java
package io.mhetko.datagatherer.controller;

import io.mhetko.datagatherer.producer.RateChangedProducer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {
    private final StringRedisTemplate redisTemplate;
    private final RateChangedProducer rateChangedProducer;

    public TestController(StringRedisTemplate redisTemplate, RateChangedProducer rateChangedProducer) {
        this.redisTemplate = redisTemplate;
        this.rateChangedProducer = rateChangedProducer;
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

    @PostMapping("/simulate")
    public void simulateRateChange(@RequestBody Object payload) {
        rateChangedProducer.sendJson(payload);
    }
}