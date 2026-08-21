package com.job_processing.distributed_platform.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.job_processing.distributed_platform.cache.model.CachedJob;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class CacheL1 {

    private final Cache<String, CachedJob> cache;

    public CacheL1(CacheProperties cacheProperties) {

        long ttlSeconds = cacheProperties
                .getCacheL1()
                .getTtlSeconds();

        int maximumSize = cacheProperties
                .getCacheL1()
                .getMaximumSize();

        this.cache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofSeconds(ttlSeconds))
                .maximumSize(maximumSize)
                .build();
    }

    public CachedJob get(String jobId) {
        return cache.getIfPresent(jobId);
    }

    public void put(String jobId, CachedJob cachedJob) {
        cache.put(jobId, cachedJob);
    }

    public void evict(String jobId) {
        cache.invalidate(jobId);
    }

    public void clear() {
        cache.invalidateAll();
    }
}