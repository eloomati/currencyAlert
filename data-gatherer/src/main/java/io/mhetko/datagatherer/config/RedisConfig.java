package io.mhetko.datagatherer.config;

import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericToStringSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(RedisProperties props) {
        RedisStandaloneConfiguration conf = new RedisStandaloneConfiguration(props.getHost(), props.getPort());
        if (props.getPassword() != null && !props.getPassword().isBlank()) {
            conf.setPassword(props.getPassword());
        }
        conf.setDatabase(props.getDatabase());
        return new LettuceConnectionFactory(conf);
    }

    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory cf) {
        return new StringRedisTemplate(cf);
    }

    @Bean
    public RedisTemplate<String, Double> doubleRedisTemplate(RedisConnectionFactory cf) {
        RedisTemplate<String, Double> template = new RedisTemplate<>();
        template.setConnectionFactory(cf);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new GenericToStringSerializer<>(Double.class));
        template.afterPropertiesSet();
        return template;
    }
}