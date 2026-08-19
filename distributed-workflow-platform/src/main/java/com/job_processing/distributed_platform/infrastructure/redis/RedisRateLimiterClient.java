package com.job_processing.distributed_platform.infrastucture.redis;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;

@Service
public class RateLimitService implements RateLimiter {

    private final RedisRateLimiterClient redisRateLimiterClient;

    public RateLimitService(
            RedisRateLimiterClient redisRateLimiterClient) {
        this.redisRateLimiterClient = redisRateLimiterClient;
    }

    @Override
    @CircuitBreaker(
            name = "redisRateLimiter",
            fallbackMethod = "rateLimitFallback"
    )
    public boolean isAllowed(String clientId) {

        return redisRateLimiterClient.isAllowed(clientId);
    }

    private boolean rateLimitFallback(
            String clientId,
            Throwable throwable) {

        throw new RateLimiterUnavailableException(
                "Rate limiter is currently unavailable",
                throwable
        );
    }
}
