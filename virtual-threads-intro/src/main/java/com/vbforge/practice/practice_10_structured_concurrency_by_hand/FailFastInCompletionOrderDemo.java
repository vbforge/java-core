package com.vbforge.practice.practice_10_structured_concurrency_by_hand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Fail fast: take results in COMPLETION order so a failure is seen at once, then cancel the rest.
 * Reading future1.get() first would hang on the slow task and never notice the failure of the second.
 */
public class FailFastInCompletionOrderDemo {

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch slowReacted = new CountDownLatch(1);
        boolean[] slowWasInterrupted = new boolean[1];

        try (ExecutorService scope = Executors.newVirtualThreadPerTaskExecutor()) {
            ExecutorCompletionService<String> completion = new ExecutorCompletionService<>(scope);
            List<Future<String>> futures = new ArrayList<>();

            futures.add(completion.submit(() -> { // would take a minute
                try {
                    Thread.sleep(60_000);
                    return "slow result";
                } catch (InterruptedException e) {
                    slowWasInterrupted[0] = true;
                    throw e;
                } finally {
                    slowReacted.countDown();
                }
            }));
            futures.add(completion.submit(() -> {
                throw new IllegalStateException("second task failed");
            }));

            try {
                System.out.println("first completed: " + completion.take().get());
            } catch (ExecutionException e) {
                System.out.println("first completed with failure: " + e.getCause().getMessage());
                futures.forEach(f -> f.cancel(true));
            }

            slowReacted.await();
            System.out.println("slow task was interrupted: " + slowWasInterrupted[0]);
        }
    }
}
