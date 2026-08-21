package com.job_processing.distributed_platform.job.controller;

import com.job_processing.distributed_platform.job.dto.CreateJobRequest;
import com.job_processing.distributed_platform.job.dto.JobResponse;
import com.job_processing.distributed_platform.job.service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/jobs")
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @PostMapping
    public ResponseEntity<JobResponse> createJob(
            @RequestBody CreateJobRequest request) {

        JobResponse response = jobService.createJob(request);

        return ResponseEntity
                .created(URI.create("/api/jobs/" + response.jobId()))
                .body(response);
    }

    @GetMapping("/{jobId}")
    public ResponseEntity<JobResponse> getJob(
            @PathVariable String jobId) {

        return ResponseEntity.ok(
                jobService.getJob(jobId)
        );
    }
}