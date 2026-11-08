package com.vbforge.practice.practice_4_executor_per_task;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/** ExecutorService is AutoCloseable: leaving the try block calls close(), which waits for all submitted tasks. */
public class ExecutorCloseWaitsDemo {

    public static void main(String[] args) {
        AtomicInteger done = new AtomicInteger();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 5; i++) {
                executor.submit(() -> {
                    Thread.sleep(100);
                    return done.incrementAndGet();
                });
            }
        } // close() blocks here until all five tasks have finished

        System.out.println("tasks done after the try block: " + done.get());
    }
}
