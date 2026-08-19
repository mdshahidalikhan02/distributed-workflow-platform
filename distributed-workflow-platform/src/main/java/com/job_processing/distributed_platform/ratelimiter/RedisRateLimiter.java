package com.job_processing.distributed_platform.ratelimiter;

import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisRateLimiter implements RateLimiter {

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimiterProperties properties;
    private final RedisScript<Long> rateLimitScript;

    public RedisRateLimiter(
            RedisTemplate<String, String> redisTemplate,
            RateLimiterProperties properties) {

        this.redisTemplate = redisTemplate;
        this.properties = properties;

        this.rateLimitScript =
                new DefaultRedisScript<>(
                        new ClassPathResource(
                                "redis/rate-limiter.lua"),
                        Long.class
                );
    }

    @Override
    public boolean isAllowed(String clientId) {

        String key = "rate_limit:" + clientId;

        Long result = redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(
                        properties.getWindowSeconds()
                ),
                String.valueOf(
                        properties.getLimit()
                )
        );

        return result != null && result == 1;
    }
}