package org.example.interview.atlassian.code_design.rate_limiter.strategy;

public interface RateLimiterStrategy {
    boolean allowRequest(int customerId);
}
