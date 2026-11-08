package com.vbforge.practice.practice_10_structured_concurrency_by_hand;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Future.get(timeout) only stops WAITING; the task keeps running until we cancel(true) it. */
public class TimeoutThenCancelDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CountDownLatch reacted = new CountDownLatch(1);
        boolean[] wasInterrupted = new boolean[1];

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = executor.submit(() -> {
                try {
                    Thread.sleep(60_000);
                    return "too late";
                } catch (InterruptedException e) {
                    wasInterrupted[0] = true;
                    throw e;
                } finally {
                    reacted.countDown();
                }
            });

            try {
                future.get(100, TimeUnit.MILLISECONDS);
            } catch (TimeoutException e) {
                System.out.println("get(100 ms) -> TimeoutException, isDone=" + future.isDone());
                future.cancel(true);
            }

            reacted.await();
            System.out.println("task interrupted by cancel(true): " + wasInterrupted[0]);
        }
    }
}
