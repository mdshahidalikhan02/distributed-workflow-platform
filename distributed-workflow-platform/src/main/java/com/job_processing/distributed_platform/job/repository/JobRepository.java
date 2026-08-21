package com.job_processing.distributed_platform.job.repository;

import com.job_processing.distributed_platform.job.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobRepository extends JpaRepository<Job, String> {
}