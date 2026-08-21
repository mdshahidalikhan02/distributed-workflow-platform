package com.job_processing.distributed_platform.job.model;

import com.job_processing.distributed_platform.enums.JobStatus;
import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    private String jobId;

    @Column(nullable = false)
    private String jobType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String payload;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private JobStatus status;

    @Column(columnDefinition = "TEXT")
    private String result;

    @Column(nullable = false)
    private Instant createdAt;

    private Instant updatedAt;

    protected Job() {
    }

    public Job(
            String jobId,
            String jobType,
            String payload,
            JobStatus status,
            Instant createdAt,
            Instant updatedAt) {

        this.jobId = jobId;
        this.jobType = jobType;
        this.payload = payload;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public String getJobId() {
        return jobId;
    }

    public String getJobType() {
        return jobType;
    }

    public String getPayload() {
        return payload;
    }

    public JobStatus getStatus() {
        return status;
    }

    public String getResult() {
        return result;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }
}