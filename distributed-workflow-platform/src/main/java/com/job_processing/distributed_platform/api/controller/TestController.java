package com.job_processing.distributed_platform.api.controller;

import com.job_processing.distributed_platform.api.exception.RateLimitExceededException;
import com.job_processing.distributed_platform.ratelimiter.RateLimiter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class TestController {

    private final RateLimiter rateLimiter;

    public TestController(RateLimiter rateLimiter) {
        this.rateLimiter = rateLimiter;
    }

    @GetMapping("/api/test")
    public ResponseEntity<String> test(
            @RequestHeader("X-Client-Id") String clientId) {

        if (!rateLimiter.isAllowed(clientId)) {
            throw new RateLimitExceededException("Rate limit exceeded");
        }

        return ResponseEntity.ok("Request successful");
    }
}
