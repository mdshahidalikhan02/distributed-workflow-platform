import com.job_processing.distributed_platform.ratelimiter.RateLimiter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisRateLimiterTest {

    @Autowired
    private RateLimiter rateLimiter;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @BeforeEach
    void cleanRedis() {
        redisTemplate.delete("rate_limit:test-user");
    }

    @Test
    void shouldAllowRequestsUntilLimit() {

        assertTrue(rateLimiter.isAllowed("test-user"));
        assertTrue(rateLimiter.isAllowed("test-user"));
        assertTrue(rateLimiter.isAllowed("test-user"));
        assertTrue(rateLimiter.isAllowed("test-user"));
        assertTrue(rateLimiter.isAllowed("test-user"));

        assertFalse(rateLimiter.isAllowed("test-user"));
    }

    @Test
    void shouldMaintainSeparateLimitsForDifferentClients() {

        assertTrue(rateLimiter.isAllowed("user-a"));
        assertTrue(rateLimiter.isAllowed("user-b"));

        assertTrue(rateLimiter.isAllowed("user-a"));
        assertTrue(rateLimiter.isAllowed("user-b"));
    }

    @Test
    void shouldSetTtlForRateLimitKey() {

        rateLimiter.isAllowed("ttl-user");

        Long ttl =
                redisTemplate.getExpire(
                        "rate_limit:ttl-user"
                );

        assertNotNull(ttl);
        assertTrue(ttl > 0);
    }
}