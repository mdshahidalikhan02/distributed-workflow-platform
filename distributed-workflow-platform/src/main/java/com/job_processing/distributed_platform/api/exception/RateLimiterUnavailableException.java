package com.job_processing.distributed_platform.api.exception;

public class RateLimiterUnavailableException extends RuntimeException {

    public RateLimiterUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
