package com.job_processing.distributed_platform.cache;

import com.job_processing.distributed_platform.cache.model.CachedJob;

public interface JobDataSource {

    CachedJob getJob(String jobId);
}