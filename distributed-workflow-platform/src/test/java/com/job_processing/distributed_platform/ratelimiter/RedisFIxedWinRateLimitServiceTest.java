package com.job_processing.distributed_platform.ratelimiter;

import com.job_processing.distributed_platform.api.exception.RateLimiterUnavailableException;
import com.job_processing.distributed_platform.infrastructure.redis.RedisRateLimiterClient;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RedisFIxedWinRateLimitServiceTest {

    private final RedisRateLimiterClient redisRateLimiterClient =
            mock(RedisRateLimiterClient.class);

    private final RedisFIxedWinRateLimitService redisFIxedWinRateLimitService =
            new RedisFIxedWinRateLimitService(redisRateLimiterClient);

    @Test
    void shouldDelegateToRedisClient() {
        when(redisRateLimiterClient.isAllowed("client-a")).thenReturn(true);

        assertTrue(redisFIxedWinRateLimitService.isAllowed("client-a"));
    }

    @Test
    void shouldExposeFallbackException() {
        assertThrows(
                RateLimiterUnavailableException.class,
                () -> redisFIxedWinRateLimitService.rateLimitFallback(
                        "client-a",
                        new RuntimeException("redis unavailable")
                )
        );
    }
}
