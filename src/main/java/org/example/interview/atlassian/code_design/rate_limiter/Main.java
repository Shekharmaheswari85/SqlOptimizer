package org.example.interview.atlassian.code_design.rate_limiter;

import org.example.interview.atlassian.code_design.rate_limiter.service.RateLimiterService;
import org.example.interview.atlassian.code_design.rate_limiter.strategy.*;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        RateLimiterService fixed = new RateLimiterService(new FixedWindowRateLimiter(3, 10, 5));
        RateLimiterService sliding = new RateLimiterService(new SlidingWindowRateLimiter(3, 10));
        RateLimiterService token = new RateLimiterService(new TokenBucketRateLimiter(5, 1)); // 5 tokens max, 1 token/sec
        RateLimiterService leaky = new RateLimiterService(new LeakyBucketRateLimiter(3, 1)); // leak 1/sec
        RateLimiterService fixedWithCredits = new RateLimiterService(new FixedWindowWithCreditRateLimiter(3, 10, 5));

        int customerId = 101;

        System.out.println("=== Fixed Window ===");
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + i + ": " + fixed.rateLimit(customerId));
        }

        System.out.println("\n=== Sliding Window ===");
        for (int i = 0; i < 5; i++) {
            System.out.println("Request " + i + ": " + sliding.rateLimit(customerId));
            Thread.sleep(1000);
        }

        System.out.println("\n=== Token Bucket ===");
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + i + ": " + token.rateLimit(customerId));
            Thread.sleep(500);
        }

        System.out.println("\n=== Leaky Bucket ===");
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + i + ": " + leaky.rateLimit(customerId));
            Thread.sleep(500);
        }

        System.out.println("\n=== Fixed Window WITH Credits ===");
        for (int i = 0; i < 7; i++) {
            System.out.println("Request " + i + ": " + fixedWithCredits.rateLimit(customerId));
        }

        // Optional: simulate some cool down and retry
        System.out.println("\nSleeping for 10 seconds to reset window and regenerate credits...\n");
        Thread.sleep(10_000);

        for (int i = 0; i < 10; i++) {
            System.out.println("Request " + i + ": " + fixedWithCredits.rateLimit(customerId));
        }
    }
}
