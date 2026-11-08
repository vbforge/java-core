package com.vbforge.practice.practice_5_massive_concurrency;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/** 10 000 virtual threads blocked at the same moment: blocking is cheap, they all park and wait for one latch. */
public class TenThousandBlockedThreadsDemo {

    private static final int THREADS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch release = new CountDownLatch(1);
        AtomicInteger blocked = new AtomicInteger();
        AtomicInteger finished = new AtomicInteger();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < THREADS; i++) {
                executor.submit(() -> {
                    blocked.incrementAndGet();
                    release.await();
                    return finished.incrementAndGet();
                });
            }

            while (blocked.get() < THREADS) {
                Thread.sleep(5);
            }
            System.out.println("threads started and blocked together: " + blocked.get());
            System.out.println("finished while still blocked        : " + finished.get());

            release.countDown();
        }

        System.out.println("finished after release              : " + finished.get());
    }
}
