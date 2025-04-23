package org.example.interview.atlassian.code_design.rate_limiter.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class LeakyBucketRateLimiter implements RateLimiterStrategy {
    private final int capacity;
    private final double leakRatePerSecond;
    private final Map<Integer, LeakyBucket> userBuckets = new ConcurrentHashMap<>();

    public LeakyBucketRateLimiter(int capacity, double leakRatePerSecond) {
        this.capacity = capacity;
        this.leakRatePerSecond = leakRatePerSecond;
    }

    private static class LeakyBucket {
        double waterLevel;
        long lastLeakTimestamp;

        LeakyBucket(long now) {
            this.waterLevel = 0;
            this.lastLeakTimestamp = now;
        }
    }

    @Override
    public boolean allowRequest(int customerId) {
        long now = System.currentTimeMillis();
        LeakyBucket bucket = userBuckets.computeIfAbsent(customerId, k -> new LeakyBucket(now));

        synchronized (bucket) {
            long elapsed = now - bucket.lastLeakTimestamp;
            double leaked = (elapsed / 1000.0) * leakRatePerSecond;
            bucket.waterLevel = Math.max(0, bucket.waterLevel - leaked);
            bucket.lastLeakTimestamp = now;

            if (bucket.waterLevel < capacity) {
                bucket.waterLevel++;
                return true;
            }

            return false;
        }
    }
}

