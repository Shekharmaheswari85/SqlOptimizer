package org.example.interview.atlassian.code_design.rate_limiter.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowRateLimiter implements RateLimiterStrategy {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final int maxCredits;
    private final Map<Integer, Bucket> userBuckets = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, int windowSizeSeconds, int maxCredits) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeSeconds * 1000L;
        this.maxCredits = maxCredits;
    }

    private static class Bucket {
        long windowStart;
        int requestCount;
        int credits;

        Bucket(long time) {
            this.windowStart = time;
            this.requestCount = 0;
            this.credits = 0;
        }
    }

    @Override
    public boolean allowRequest(int customerId) {
        long now = System.currentTimeMillis();
        Bucket bucket = userBuckets.computeIfAbsent(customerId, k -> new Bucket(now));

        synchronized (bucket) {
            if (now - bucket.windowStart >= windowSizeMillis) {
                int unused = Math.max(0, maxRequests - bucket.requestCount);
                bucket.credits = Math.min(maxCredits, bucket.credits + unused);
                bucket.windowStart = now;
                bucket.requestCount = 0;
            }

            if (bucket.requestCount < maxRequests) {
                bucket.requestCount++;
                return true;
            } else if (bucket.credits > 0) {
                bucket.credits--;
                return true;
            }

            return false;
        }
    }
}
