package com.job_processing.distributed_platform.cache;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "cache")
public class CacheProperties {

    private CacheL1 cacheL1 = new CacheL1();
    private CacheL2 cacheL2 = new CacheL2();

    public CacheL1 getCacheL1() {
        return cacheL1;
    }

    public void setCacheL1(CacheL1 cacheL1) {
        this.cacheL1 = cacheL1;
    }

    public CacheL2 getCacheL2() {
        return cacheL2;
    }

    public void setCacheL2(CacheL2 cacheL2) {
        this.cacheL2 = cacheL2;
    }

    public static class CacheL1 {

        private long ttlSeconds;
        private int maximumSize;

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }

        public int getMaximumSize() {
            return maximumSize;
        }

        public void setMaximumSize(int maximumSize) {
            this.maximumSize = maximumSize;
        }
    }

    public static class CacheL2 {

        private long ttlSeconds;

        public long getTtlSeconds() {
            return ttlSeconds;
        }

        public void setTtlSeconds(long ttlSeconds) {
            this.ttlSeconds = ttlSeconds;
        }
    }
}