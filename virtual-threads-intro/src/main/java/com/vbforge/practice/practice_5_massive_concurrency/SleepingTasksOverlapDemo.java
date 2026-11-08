package com.vbforge.practice.practice_5_massive_concurrency;

import java.time.Duration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 1000 tasks each sleep 100 ms. Run one after another that would take 100 s; with one virtual
 * thread per task the sleeps overlap. Only a boolean is printed, so the output does not depend on timing.
 */
public class SleepingTasksOverlapDemo {

    public static void main(String[] args) {
        int tasks = 1_000;
        Duration eachSleep = Duration.ofMillis(100);
        Duration sequentialTotal = eachSleep.multipliedBy(tasks);

        long start = System.nanoTime();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < tasks; i++) {
                executor.submit(() -> {
                    Thread.sleep(eachSleep);
                    return null;
                });
            }
        }
        Duration elapsed = Duration.ofNanos(System.nanoTime() - start);

        System.out.println("sequential would need " + sequentialTotal.toSeconds() + " s");
        System.out.println("overlapped run was at least 10x faster: " + (elapsed.compareTo(sequentialTotal.dividedBy(10)) < 0));
    }
}
