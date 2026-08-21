package com.job_processing.distributed_platform.job.dto;

import com.job_processing.distributed_platform.enums.JobStatus;

import java.time.Instant;

public record JobResponse(
        String jobId,
        String jobType,
        JobStatus status,
        String result,
        Instant createdAt,
        Instant updatedAt
) {
}