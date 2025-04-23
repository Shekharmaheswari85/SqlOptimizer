package org.example.interview.atlassian.code_design.rate_limiter.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TokenBucketRateLimiter implements RateLimiterStrategy {
    private final int capacity;
    private final double refillRatePerSecond;
    private final Map<Integer, TokenBucket> userBuckets = new ConcurrentHashMap<>();

    public TokenBucketRateLimiter(int capacity, double refillRatePerSecond) {
        this.capacity = capacity;
        this.refillRatePerSecond = refillRatePerSecond;
    }

    private static class TokenBucket {
        double tokens;
        long lastRefillTimestamp;

        TokenBucket(long now, int capacity) {
            this.tokens = capacity;
            this.lastRefillTimestamp = now;
        }
    }

    @Override
    public boolean allowRequest(int customerId) {
        long now = System.currentTimeMillis();
        TokenBucket bucket = userBuckets.computeIfAbsent(customerId, k -> new TokenBucket(now, capacity));

        synchronized (bucket) {
            long elapsed = now - bucket.lastRefillTimestamp;
            double refill = (elapsed / 1000.0) * refillRatePerSecond;
            bucket.tokens = Math.min(capacity, bucket.tokens + refill);
            bucket.lastRefillTimestamp = now;

            if (bucket.tokens >= 1) {
                bucket.tokens--;
                return true;
            }

            return false;
        }
    }
}