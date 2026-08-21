package com.job_processing.distributed_platform.infrastructure.redis;

import com.job_processing.distributed_platform.ratelimiter.RateLimiterProperties;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RedisRateLimiterClient {

    private final RedisTemplate<String, String> redisTemplate;
    private final RateLimiterProperties properties;
    private final RedisScript<Long> rateLimitScript;

    public RedisRateLimiterClient(
            @Qualifier("redisTemplate")
            RedisTemplate<String, String> redisTemplateRate,
            RateLimiterProperties properties) {
        this.redisTemplate = redisTemplateRate;
        this.properties = properties;
        DefaultRedisScript<Long> script = new DefaultRedisScript<>();
        script.setLocation(new ClassPathResource("redis/rate-limiter.lua"));
        script.setResultType(Long.class);
        this.rateLimitScript = script;
    }

    @CircuitBreaker(name = "redisRateLimiter", fallbackMethod = "redisFallback")

    public boolean isAllowed(String clientId) {
        String key = "rate_limit:" + clientId;

        Long result = redisTemplate.execute(
                rateLimitScript,
                List.of(key),
                String.valueOf(properties.getWindowSeconds()),
                String.valueOf(properties.getLimit())
        );

        return result != null && result == 1;
    }
}
