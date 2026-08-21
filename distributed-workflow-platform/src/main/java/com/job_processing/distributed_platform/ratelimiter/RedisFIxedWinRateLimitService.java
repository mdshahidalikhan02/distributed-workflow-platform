package com.job_processing.distributed_platform.ratelimiter;

import com.job_processing.distributed_platform.api.exception.RateLimiterUnavailableException;
import com.job_processing.distributed_platform.infrastructure.redis.RedisRateLimiterClient;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

@Service
public class RedisFIxedWinRateLimitService implements RateLimiter {

    private final RedisRateLimiterClient redisRateLimiterClient;

    public RedisFIxedWinRateLimitService(
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

   public boolean rateLimitFallback(
            String clientId,
            Throwable throwable) {

        throw new RateLimiterUnavailableException(
                "Rate limiter is currently unavailable",
                throwable
        );
    }

    @PostConstruct
    public void checkProxy() {
        System.out.println(
                "RedisFIxedWinRateLimitService class = " + this.getClass()
        );
    }
}