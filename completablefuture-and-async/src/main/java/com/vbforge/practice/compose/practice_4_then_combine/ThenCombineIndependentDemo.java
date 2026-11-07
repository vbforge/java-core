package com.vbforge.practice.compose.practice_4_then_combine;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * thenCombine: merge the results of two INDEPENDENT futures; both are started first, so latency = max(a, b).
 * (thenCompose here would serialize them: latency = a + b.)
 * Proof of parallelism without timing: each task waits on a latch that only opens when BOTH tasks are running.
 */
public class ThenCombineIndependentDemo {

    public static void main(String[] args) {

        ExecutorService executor = Executors.newFixedThreadPool(2);
        try {
            CountDownLatch bothRunning = new CountDownLatch(2);
            AtomicBoolean ranTogether = new AtomicBoolean(true);

            CompletableFuture<String> a = CompletableFuture.supplyAsync(() -> meetAtLatch(bothRunning, ranTogether, "Hello"), executor);
            CompletableFuture<String> b = CompletableFuture.supplyAsync(() -> meetAtLatch(bothRunning, ranTogether, "World"), executor);

            String combined = a.thenCombine(b, (x, y) -> x + " " + y).join();
            System.out.println("combined: " + combined);                  // Hello World
            System.out.println("tasks ran at the same time: " + ranTogether.get()); // true
        } finally {
            executor.shutdown();
        }
    }

    private static String meetAtLatch(CountDownLatch latch, AtomicBoolean ranTogether, String value) {
        latch.countDown();
        try {
            if (!latch.await(5, TimeUnit.SECONDS)) {
                ranTogether.set(false);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            ranTogether.set(false);
        }
        return value;
    }
}
