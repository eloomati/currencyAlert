package io.mhetko.datagatherer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RateCacheService {

    private final RedisTemplate<String, Double> redisTemplate;

    private String getKey(String base, String symbol) {
        return "rate:" + base + ":" + symbol;
    }

    public void saveRate(String base, String symbol, double rate) {
        String key = getKey(base, symbol);
        redisTemplate.opsForList().leftPush(key, rate);
        redisTemplate.opsForList().trim(key, 0, 1);
    }

    public List<Double> getLastRates(String base, String symbol) {
        String key = getKey(base, symbol);
        return redisTemplate.opsForList().range(key, 0, 1);
    }
}
