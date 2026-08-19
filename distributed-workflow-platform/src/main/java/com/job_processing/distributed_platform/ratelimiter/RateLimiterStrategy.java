package com.job_processing.distributed_platform.ratelimiter;

public interface RateLimiter {

    boolean isAllowed(String clientId);
}
