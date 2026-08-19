package com.job_processing.distributed_platform.api.controller;

import com.job_processing.distributed_platform.ratelimiter.RateLimiter;
import org.springframework.http.HttpStatus;
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

        try {

            if (!rateLimiter.isAllowed(clientId)) {
                return ResponseEntity
                        .status(HttpStatus.TOO_MANY_REQUESTS)
                        .body("Rate limit exceeded");
            }

            return ResponseEntity.ok("Request successful");

        } catch (Exception e) {

            return ResponseEntity
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body("Rate limiting service unavailable");
        }
    }
}