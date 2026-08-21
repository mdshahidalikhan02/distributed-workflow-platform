package com.job_processing.distributed_platform.job.service;

import com.job_processing.distributed_platform.enums.JobStatus;
import com.job_processing.distributed_platform.job.dto.CreateJobRequest;
import com.job_processing.distributed_platform.job.dto.JobResponse;
import com.job_processing.distributed_platform.job.model.Job;
import com.job_processing.distributed_platform.job.repository.JobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class JobService {

    private final JobRepository jobRepository;

    public JobService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Transactional
    public JobResponse createJob(CreateJobRequest request) {

        Instant now = Instant.now();

        Job job = new Job(
                UUID.randomUUID().toString(),
                request.jobType(),
                request.payload(),
                JobStatus.PENDING,
                now,
                now
        );

        Job savedJob = jobRepository.save(job);

        return toResponse(savedJob);
    }

    public JobResponse getJob(String jobId) {

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() ->
                        new IllegalArgumentException(
                                "Job not found: " + jobId
                        )
                );

        return toResponse(job);
    }

    private JobResponse toResponse(Job job) {

        return new JobResponse(
                job.getJobId(),
                job.getJobType(),
                job.getStatus(),
                job.getResult(),
                job.getCreatedAt(),
                job.getUpdatedAt()
        );
    }
}