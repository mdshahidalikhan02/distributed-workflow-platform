package com.job_processing.distributed_platform.ratelimiter;

import com.job_processing.distributed_platform.infrastructure.redis.RedisRateLimiterClient;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class RateLimiterConcurrencyTest {

    @Test
    void shouldHandleConcurrentCallsThroughService() throws InterruptedException {
        RedisRateLimiterClient redisRateLimiterClient =
                mock(RedisRateLimiterClient.class);
        RateLimitService rateLimitService =
                new RateLimitService(redisRateLimiterClient);
        AtomicInteger allowedCalls = new AtomicInteger();

        when(redisRateLimiterClient.isAllowed("client-a"))
                .thenAnswer(invocation -> allowedCalls.incrementAndGet() <= 5);

        int requestCount = 10;
        CountDownLatch ready = new CountDownLatch(requestCount);
        CountDownLatch start = new CountDownLatch(1);
        CountDownLatch done = new CountDownLatch(requestCount);
        AtomicInteger allowed = new AtomicInteger();
        ExecutorService executorService = Executors.newFixedThreadPool(requestCount);

        for (int i = 0; i < requestCount; i++) {
            executorService.submit(() -> {
                ready.countDown();
                start.await();
                if (rateLimitService.isAllowed("client-a")) {
                    allowed.incrementAndGet();
                }
                done.countDown();
                return null;
            });
        }

        ready.await(1, TimeUnit.SECONDS);
        start.countDown();
        done.await(1, TimeUnit.SECONDS);
        executorService.shutdownNow();

        assertEquals(5, allowed.get());
    }
}
