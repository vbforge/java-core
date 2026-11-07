package com.vbforge.practice.errors.practice_14_cancel;

import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * cancel(...) completes the FUTURE exceptionally with CancellationException, but it does NOT stop the work:
 * the mayInterruptIfRunning flag is ignored by CompletableFuture, so a running supplier keeps running to the end.
 * Dependents complete with a CompletionException that wraps the CancellationException.
 */
public class CancelDoesNotInterruptDemo {

    public static void main(String[] args) throws InterruptedException {

        CountDownLatch started = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        CountDownLatch finished = new CountDownLatch(1);
        AtomicBoolean wasInterrupted = new AtomicBoolean(true);

        CompletableFuture<String> work = CompletableFuture.supplyAsync(() -> {
            started.countDown();
            try {
                release.await(5, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            wasInterrupted.set(Thread.currentThread().isInterrupted());
            finished.countDown();
            return "finished anyway";
        });
        CompletableFuture<Integer> dependent = work.thenApply(String::length);

        started.await(5, TimeUnit.SECONDS);
        System.out.println("cancel(true) returned: " + work.cancel(true)); // true
        System.out.println("isCancelled: " + work.isCancelled() + ", isDone: " + work.isDone());

        try {
            work.join();
        } catch (CancellationException e) {
            System.out.println("join on the cancelled future: CancellationException");
        }
        try {
            dependent.join();
        } catch (CompletionException e) {
            System.out.println("dependent: CompletionException caused by " + e.getCause().getClass().getSimpleName());
        }

        release.countDown();
        finished.await(5, TimeUnit.SECONDS);
        System.out.println("supplier was interrupted: " + wasInterrupted.get()); // false, it ran to the end
    }
}
