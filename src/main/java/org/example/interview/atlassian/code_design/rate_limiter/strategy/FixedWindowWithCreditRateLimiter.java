package org.example.interview.atlassian.code_design.rate_limiter.strategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FixedWindowWithCreditRateLimiter implements RateLimiterStrategy {

    private final int limit;
    private final long windowInMillis;
    private final int maxCredits;
    private final TimeProvider timeProvider;

    private static class UserBucket {
        long windowStart;
        int requestCount;
        int credits;

        public UserBucket(long now) {
            this.windowStart = now;
            this.requestCount = 0;
            this.credits = 0;
        }
    }

    private final Map<Integer, UserBucket> userBuckets = new ConcurrentHashMap<>();

    public FixedWindowWithCreditRateLimiter(int limit, int windowSeconds, int maxCredits) {
        this(limit, windowSeconds, maxCredits, new DefaultTimeProvider());
    }

    public FixedWindowWithCreditRateLimiter(int limit, int windowSeconds, int maxCredits, TimeProvider timeProvider) {
        this.limit = limit;
        this.windowInMillis = windowSeconds * 1000L;
        this.maxCredits = maxCredits;
        this.timeProvider = timeProvider;
    }

    @Override
    public boolean allowRequest(int customerId) {
        long now = timeProvider.getCurrentTimeMillis();

        UserBucket bucket = userBuckets.computeIfAbsent(customerId, id -> new UserBucket(now));

        synchronized (bucket) {
            // Check for window reset
            if (now - bucket.windowStart >= windowInMillis) {
                int unused = Math.max(0, limit - bucket.requestCount);
                bucket.credits = Math.min(maxCredits, bucket.credits + unused);
                bucket.windowStart = now;
                bucket.requestCount = 0;
            }

            if (bucket.requestCount < limit) {
                bucket.requestCount++;
                return true;
            }

            if (bucket.credits > 0) {
                bucket.credits--;
                return true;
            }

            return false;
        }
    }
    
    // Default implementation of TimeProvider that uses System.currentTimeMillis()
    private static class DefaultTimeProvider implements TimeProvider {
        @Override
        public long getCurrentTimeMillis() {
            return System.currentTimeMillis();
        }
        
        @Override
        public void advanceTime(long millis) {
            // No-op for the default implementation
        }
    }
}
