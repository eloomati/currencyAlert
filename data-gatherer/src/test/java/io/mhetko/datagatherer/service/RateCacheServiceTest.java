package io.mhetko.datagatherer.service;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class RateCacheServiceTest {

    private final RedisTemplate<String, Double> redisTemplate = mock(RedisTemplate.class);
    private final ListOperations<String, Double> listOps = mock(ListOperations.class);
    private final RateCacheService service = new RateCacheService(redisTemplate);

    @Test
    void saveRate_pushesAndTrimsList() {
        when(redisTemplate.opsForList()).thenReturn(listOps);

        service.saveRate("USD", "PLN", 4.20);

        verify(listOps).leftPush("rate:USD:PLN", 4.20);
        verify(listOps).trim("rate:USD:PLN", 0, 1);
    }

    @Test
    void getLastRates_returnsListFromRedis() {
        when(redisTemplate.opsForList()).thenReturn(listOps);
        when(listOps.range("rate:EUR:PLN", 0, 1)).thenReturn(List.of(4.50, 4.45));

        List<Double> result = service.getLastRates("EUR", "PLN");

        assertThat(result).containsExactly(4.50, 4.45);
        verify(listOps).range("rate:EUR:PLN", 0, 1);
    }
}