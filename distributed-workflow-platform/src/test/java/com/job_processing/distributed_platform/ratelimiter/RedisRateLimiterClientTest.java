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

    private static final String RATE_LIMIT_PREFIX = "rate_limit:";

    @Autowired
    private RedisRateLimiterClient rateLimiterClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void cleanRedis() {

        redisTemplate.delete(List.of(
                RATE_LIMIT_PREFIX + "test-user",
                RATE_LIMIT_PREFIX + "user-a",
                RATE_LIMIT_PREFIX + "user-b",
                RATE_LIMIT_PREFIX + "ttl-user"
        ));
    }

    @Test
    void shouldAllowRequestsUntilLimit() {

        // First 5 requests should be allowed
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));
        assertTrue(rateLimiterClient.isAllowed("test-user"));

        // 6th request should be rejected
        assertFalse(rateLimiterClient.isAllowed("test-user"));
    }

    @Test
    void shouldRejectAllRequestsAfterLimitIsExceeded() {

        // Consume the complete limit
        for (int i = 0; i < 5; i++) {
            assertTrue(rateLimiterClient.isAllowed("test-user"));
        }

        // Requests after the limit should be rejected
        assertFalse(rateLimiterClient.isAllowed("test-user"));
        assertFalse(rateLimiterClient.isAllowed("test-user"));
        assertFalse(rateLimiterClient.isAllowed("test-user"));
    }

    @Test
    void shouldMaintainSeparateLimitsForDifferentClients() {

        // user-a gets its own counter
        assertTrue(rateLimiterClient.isAllowed("user-a"));
        assertTrue(rateLimiterClient.isAllowed("user-a"));

        // user-b has a separate counter
        assertTrue(rateLimiterClient.isAllowed("user-b"));
        assertTrue(rateLimiterClient.isAllowed("user-b"));

        // Consume remaining requests for user-a
        assertTrue(rateLimiterClient.isAllowed("user-a"));
        assertTrue(rateLimiterClient.isAllowed("user-a"));
        assertTrue(rateLimiterClient.isAllowed("user-a"));

        // user-a is now limited
        assertFalse(rateLimiterClient.isAllowed("user-a"));

        // user-b should still have capacity
        assertTrue(rateLimiterClient.isAllowed("user-b"));
    }

    @Test
    void shouldSetTtlForRateLimitKey() {

        boolean allowed = rateLimiterClient.isAllowed("ttl-user");

        assertTrue(
                allowed,
                "First request should be allowed"
        );

        Long ttl =
                redisTemplate.getExpire(
                        "rate_limit:ttl-user"
                );

        assertNotNull(ttl);

        assertTrue(
                ttl > 0,
                "Expected positive TTL but got: " + ttl
        );
    }

    @Test
    void shouldCreateRateLimitKeyAfterFirstRequest() {

        String key = RATE_LIMIT_PREFIX + "test-user";

        // Key should not exist initially
        assertNull(
                redisTemplate.opsForValue().get(key)
        );

        // First request
        rateLimiterClient.isAllowed("test-user");

        // Key should now exist
        String currentCount =
                redisTemplate.opsForValue().get(key);

        assertNotNull(currentCount);
        assertEquals("1", currentCount);
    }

    @Test
    void shouldIncrementCounterForEachRequest() {

        String key = RATE_LIMIT_PREFIX + "test-user";

        rateLimiterClient.isAllowed("test-user");

        assertEquals(
                "1",
                redisTemplate.opsForValue().get(key)
        );

        rateLimiterClient.isAllowed("test-user");

        assertEquals(
                "2",
                redisTemplate.opsForValue().get(key)
        );

        rateLimiterClient.isAllowed("test-user");

        assertEquals(
                "3",
                redisTemplate.opsForValue().get(key)
        );
    }

    @Test
    void shouldCountRejectedRequest() {

        String key = RATE_LIMIT_PREFIX + "test-user";

        // Consume allowed requests
        for (int i = 0; i < 5; i++) {
            assertTrue(
                    rateLimiterClient.isAllowed("test-user")
            );
        }

        // 6th request is rejected
        assertFalse(
                rateLimiterClient.isAllowed("test-user")
        );

        /*
         * The Lua script performs:
         *
         * INCR
         *   ↓
         * check limit
         *   ↓
         * allow / reject
         *
         * Therefore the rejected request is still counted.
         */
        assertEquals(
                "6",
                redisTemplate.opsForValue().get(key)
        );
    }
}