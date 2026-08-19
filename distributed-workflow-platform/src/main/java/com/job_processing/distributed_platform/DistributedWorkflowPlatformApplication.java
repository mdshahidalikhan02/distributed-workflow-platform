package com.job_processing.distributed_platform;

import com.job_processing.distributed_platform.ratelimiter.RateLimiterProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(RateLimiterProperties.class)
public class DistributedWorkflowPlatformApplication {

	public static void main(String[] args) {
		SpringApplication.run(DistributedWorkflowPlatformApplication.class, args);
	}

}
