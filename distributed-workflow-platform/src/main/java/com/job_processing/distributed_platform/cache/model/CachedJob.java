package com.job_processing.distributed_platform.cache.model;

import java.time.Instant;

public record CachedJob(
        String jobId,
        String jobType,
        String status,
        String result,
        Instant updatedAt
) {
}