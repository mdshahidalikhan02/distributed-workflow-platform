package com.job_processing.distributed_platform.job.dto;

public record CreateJobRequest(
        String jobType,
        String payload
) {
}