package com.vbforge.practice.practice_6_limiting_concurrency_with_semaphore;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 200 virtual threads, but at most 5 may use the "resource" at once. The limit belongs to the
 * resource (the Semaphore), not to a thread pool.
 */
public class SemaphoreLimitsConcurrencyDemo {

    private static final int LIMIT = 5;
    private static final int TASKS = 200;

    public static void main(String[] args) {
        Semaphore permits = new Semaphore(LIMIT);
        AtomicInteger inside = new AtomicInteger();
        AtomicInteger maxInside = new AtomicInteger();
        AtomicInteger completed = new AtomicInteger();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < TASKS; i++) {
                executor.submit(() -> {
                    permits.acquire();
                    try {
                        int now = inside.incrementAndGet();
                        maxInside.accumulateAndGet(now, Math::max);
                        Thread.sleep(10); // the "database call"
                        inside.decrementAndGet();
                        completed.incrementAndGet();
                    } finally {
                        permits.release();
                    }
                    return null;
                });
            }
        }

        System.out.println("tasks completed          : " + completed.get());
        System.out.println("never more than " + LIMIT + " inside : " + (maxInside.get() <= LIMIT));
        System.out.println("permits back at the limit: " + (permits.availablePermits() == LIMIT));
    }
}
