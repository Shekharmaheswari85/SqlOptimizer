package org.example.interview.atlassian.code_design.rate_limiter.strategy;

public interface TimeProvider {
    long getCurrentTimeMillis();
    void advanceTime(long millis);
} 