package org.example.interview.atlassian.code_design.rate_limiter.strategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class SlidingWindowRateLimiter implements RateLimiterStrategy {
    private final int maxRequests;
    private final long windowSizeMillis;
    private final Map<Integer, Deque<Long>> userRequests = new ConcurrentHashMap<>();

    public SlidingWindowRateLimiter(int maxRequests, int windowSizeSeconds) {
        this.maxRequests = maxRequests;
        this.windowSizeMillis = windowSizeSeconds * 1000L;
    }

    @Override
    public boolean allowRequest(int customerId) {
        long now = System.currentTimeMillis();
        Deque<Long> deque = userRequests.computeIfAbsent(customerId, k -> new LinkedList<>());

        synchronized (deque) {
            while (!deque.isEmpty() && now - deque.peekFirst() >= windowSizeMillis) {
                deque.pollFirst();
            }

            if (deque.size() < maxRequests) {
                deque.addLast(now);
                return true;
            }

            return false;
        }
    }
}