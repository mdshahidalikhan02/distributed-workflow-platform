package com.job_processing.distributed_platform.api.exception;

import java.time.Instant;

public record ErrorResponse(
        Instant timestamp,
        int status,
        String code,
        String message
) {
}
