package com.job_processing.distributed_platform.cache;

import com.job_processing.distributed_platform.cache.model.CachedJob;
import com.job_processing.distributed_platform.api.exception.CacheUnavailableException;
import org.springframework.stereotype.Service;

@Service
public class CacheService {

    private final CacheL1 cacheL1;
    private final CacheL2 cacheL2;
    private final JobDataSource jobDataSource;

    public CacheService(
            CacheL1 cacheL1,
            CacheL2 cacheL2,
            JobDataSource jobDataSource) {

        this.cacheL1 = cacheL1;
        this.cacheL2 = cacheL2;
        this.jobDataSource = jobDataSource;
    }

    public CachedJob getJob(String jobId) {

        // Step 1: Check L1 cache
        CachedJob cachedJob = cacheL1.get(jobId);

        if (cachedJob != null) {
            return cachedJob;
        }

        // Step 2: Check L2 cache
        try {

            cachedJob = cacheL2.get(jobId);

            if (cachedJob != null) {

                // Promote L2 result to L1
                cacheL1.put(jobId, cachedJob);

                return cachedJob;
            }

        } catch (CacheUnavailableException exception) {

            // Redis is unavailable.
            // Continue to the source instead of failing the request.
        }

        // Step 3: Read from source
        CachedJob jobFromSource =
                jobDataSource.getJob(jobId);

        if (jobFromSource == null) {
            return null;
        }

        // Step 4: Populate L2
        try {

            cacheL2.put(jobId, jobFromSource);

        } catch (CacheUnavailableException exception) {

            // Cache population is best effort.
            // The source result is still valid.
        }

        // Step 5: Populate L1
        cacheL1.put(jobId, jobFromSource);

        return jobFromSource;
    }

    public void evictJob(String jobId) {

        cacheL1.evict(jobId);

        try {

            cacheL2.evict(jobId);

        } catch (CacheUnavailableException exception) {

            // L2 eviction failed.
            // The L1 cache has already been invalidated.
        }
    }
}