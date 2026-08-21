package com.job_processing.distributed_platform.cache;

import com.job_processing.distributed_platform.cache.model.CachedJob;
import com.job_processing.distributed_platform.infrastructure.redis.RedisCacheClient;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CacheL2 {

    private static final String CACHE_KEY_PREFIX = "job:";

    private final RedisCacheClient redisCacheClient;
    private final CacheProperties cacheProperties;

    public CacheL2(
            RedisCacheClient redisCacheClient,
            CacheProperties cacheProperties) {

        this.redisCacheClient = redisCacheClient;
        this.cacheProperties = cacheProperties;
    }

    public CachedJob get(String jobId) {

        String cacheKey = buildCacheKey(jobId);

        return redisCacheClient.get(cacheKey);
    }

    public void put(String jobId, CachedJob cachedJob) {

        String cacheKey = buildCacheKey(jobId);

        long ttlSeconds = cacheProperties
                .getCacheL2()
                .getTtlSeconds();

        Duration ttl = Duration.ofSeconds(ttlSeconds);

        redisCacheClient.put(
                cacheKey,
                cachedJob,
                ttl
        );
    }

    public void evict(String jobId) {

        String cacheKey = buildCacheKey(jobId);

        redisCacheClient.delete(cacheKey);
    }

    private String buildCacheKey(String jobId) {

        return CACHE_KEY_PREFIX + jobId;
    }
}