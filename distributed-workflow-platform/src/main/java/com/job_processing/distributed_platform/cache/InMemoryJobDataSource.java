package com.job_processing.distributed_platform.cache;

import com.job_processing.distributed_platform.cache.model.CachedJob;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryJobDataSource implements JobDataSource {

    private final Map<String, CachedJob> jobs = new ConcurrentHashMap<>();

    public InMemoryJobDataSource() {

        jobs.put(
                "job-1",
                new CachedJob(
                        "job-1",
                        "REPORT_GENERATION",
                        "COMPLETED",
                        "Report generated successfully",
                        Instant.now()
                )
        );

        jobs.put(
                "job-2",
                new CachedJob(
                        "job-2",
                        "REPORT_GENERATION",
                        "PROCESSING",
                        null,
                        Instant.now()
                )
        );
    }

    @Override
    public CachedJob getJob(String jobId) {

        return jobs.get(jobId);
    }
}