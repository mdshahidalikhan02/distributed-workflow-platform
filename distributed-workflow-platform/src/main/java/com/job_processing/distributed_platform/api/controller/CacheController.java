package com.job_processing.distributed_platform.api.controller;

import com.job_processing.distributed_platform.cache.CacheService;
import com.job_processing.distributed_platform.cache.model.CachedJob;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cache")
public class CacheController {

    private final CacheService cacheService;

    public CacheController(CacheService cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<CachedJob> getJob(
            @PathVariable String jobId) {

        CachedJob job = cacheService.getJob(jobId);

        if (job == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(job);
    }

    @DeleteMapping("/jobs/{jobId}")
    public ResponseEntity<Void> evictJob(
            @PathVariable String jobId) {

        cacheService.evictJob(jobId);

        return ResponseEntity.noContent().build();
    }
}