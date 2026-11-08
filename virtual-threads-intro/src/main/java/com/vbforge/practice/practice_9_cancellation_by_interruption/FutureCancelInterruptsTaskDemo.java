package com.vbforge.practice.practice_9_cancellation_by_interruption;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Future.cancel(true) only REQUESTS an interrupt. We wait on a latch for the task to really react;
 * close() alone may return before a cancelled task has reacted (JDK 21).
 */
public class FutureCancelInterruptsTaskDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch reacted = new CountDownLatch(1);
        boolean[] sawInterrupt = new boolean[1];

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            Future<String> future = executor.submit(() -> {
                started.countDown();
                try {
                    Thread.sleep(60_000);
                    return "finished";
                } catch (InterruptedException e) {
                    sawInterrupt[0] = true;
                    throw e;
                } finally {
                    reacted.countDown();
                }
            });

            started.await();
            System.out.println("cancel(true) returned : " + future.cancel(true));
            System.out.println("isCancelled / isDone  : " + future.isCancelled() + " / " + future.isDone());

            try {
                future.get();
            } catch (CancellationException e) {
                System.out.println("get() threw           : " + e.getClass().getSimpleName());
            }

            reacted.await(); // wait for the task itself, not only for the future's state
            System.out.println("task saw the interrupt: " + sawInterrupt[0]);
        }
    }
}
