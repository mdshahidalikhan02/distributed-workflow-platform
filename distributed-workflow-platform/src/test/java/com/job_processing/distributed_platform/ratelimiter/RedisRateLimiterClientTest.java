package com.job_processing.distributed_platform.ratelimiter;

import com.job_processing.distributed_platform.infrastructure.redis.RedisRateLimiterClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisRateLimiterClientTest {

    @Autowired
    private RedisRateLimiterClient rateLimiterClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void cleanRedis() {
        redisTemplate.delete(List.of(
                "rate_limit:test-user",
                "rate_limit:user-a",
                "rate_limit:user-b",
                "rate_limit:ttl-user"
        ));
    }

    @Test
    void shouldAllowRequestsUntilLimit() {

        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));

        assertFalse(rateLimiterClient.isAllowed("test-user"));
    }

    @Test
    void shouldMaintainSeparateLimitsForDifferentClients() {

        assertTrue(rateLimiterClient.isAllowed("user-a"));
        assertTrue(rateLimiterClient.isAllowed("user-b"));

        assertTrue(rateLimiterClient.isAllowed("user-a"));
        assertTrue(rateLimiterClient.isAllowed("user-b"));
    }

    @Test
    void shouldSetTtlForRateLimitKey() {

        rateLimiterClient.isAllowed("ttl-user");

        Long ttl =
                redisTemplate.getExpire(
                        "rate_limit:ttl-user"
                );

        assertNotNull(ttl);
        assertTrue(ttl > 0);
    }
}
