package com.vbforge.practice.practice_10_structured_concurrency_by_hand;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** invokeAny: the first successful result wins, failures are ignored, the losers are cancelled (interrupted). */
public class FirstSuccessWinsDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CountDownLatch slowReacted = new CountDownLatch(1);
        boolean[] slowWasInterrupted = new boolean[1];

        Callable<String> failing = () -> {
            throw new IllegalStateException("replica A is down");
        };
        Callable<String> fast = () -> "answer from replica B";
        Callable<String> slow = () -> {
            try {
                Thread.sleep(60_000);
                return "answer from replica C";
            } catch (InterruptedException e) {
                slowWasInterrupted[0] = true;
                throw e;
            } finally {
                slowReacted.countDown();
            }
        };

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            String winner = executor.invokeAny(List.of(failing, fast, slow));
            System.out.println("winner: " + winner);

            slowReacted.await();
            System.out.println("slow replica was interrupted: " + slowWasInterrupted[0]);
        }
    }
}
