package com.job_processing.distributed_platform.job.repository;

import com.job_processing.distributed_platform.cache.JobDataSource;
import com.job_processing.distributed_platform.cache.model.CachedJob;
import com.job_processing.distributed_platform.job.model.Job;
import org.springframework.stereotype.Component;

@Component
public class PostgresJobDataSource implements JobDataSource {

    private final JobRepository jobRepository;

    public PostgresJobDataSource(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Override
    public CachedJob getJob(String jobId) {

        return jobRepository.findById(jobId)
                .map(this::toCachedJob)
                .orElse(null);
    }

    private CachedJob toCachedJob(Job job) {

        return new CachedJob(
                job.getJobId(),
                job.getJobType(),
                job.getStatus().name(),
                job.getResult(),
                job.getUpdatedAt()
        );
    }
}