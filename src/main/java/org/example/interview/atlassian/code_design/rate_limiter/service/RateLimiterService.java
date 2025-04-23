package org.example.interview.atlassian.code_design.rate_limiter.service;

import org.example.interview.atlassian.code_design.rate_limiter.strategy.RateLimiterStrategy;

public class RateLimiterService {
    private final RateLimiterStrategy strategy;

    public RateLimiterService(RateLimiterStrategy strategy) {
        this.strategy = strategy;
    }

    public boolean rateLimit(int customerId) {
        return strategy.allowRequest(customerId);
    }
}
