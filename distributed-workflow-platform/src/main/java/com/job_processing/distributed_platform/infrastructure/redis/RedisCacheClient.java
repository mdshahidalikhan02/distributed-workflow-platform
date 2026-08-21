package com.job_processing.distributed_platform.infrastructure.redis;

import com.job_processing.distributed_platform.cache.model.CachedJob;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class RedisCacheClient {

    private final RedisTemplate<String, CachedJob> redisTemplate;

    public RedisCacheClient(
            RedisTemplate<String, CachedJob> redisTemplate) {

        this.redisTemplate = redisTemplate;
    }

    @CircuitBreaker(name = "redisCache")
    public CachedJob get(String key) {

        return redisTemplate
                .opsForValue()
                .get(key);
    }

    @CircuitBreaker(name = "redisCache")
    public void put(
            String key,
            CachedJob cachedJob,
            Duration ttl) {

        redisTemplate
                .opsForValue()
                .set(key, cachedJob, ttl);
    }

    @CircuitBreaker(name = "redisCache")
    public void delete(String key) {

        redisTemplate.delete(key);
    }
}