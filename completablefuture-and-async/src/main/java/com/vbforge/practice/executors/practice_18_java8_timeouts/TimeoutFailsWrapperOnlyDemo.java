package com.vbforge.practice.executors.practice_18_java8_timeouts;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;

/**
 * Timeout in Java 8 (no orTimeout): race the source against a timer future that a ScheduledExecutorService
 * fails after N ms (applyToEither). The timeout fails the WRAPPER only: the slow source keeps running.
 */
public class TimeoutFailsWrapperOnlyDemo {

    static <T> CompletableFuture<T> timeoutAfter(ScheduledExecutorService scheduler, long millis) {
        CompletableFuture<T> timer = new CompletableFuture<>();
        scheduler.schedule(() -> timer.completeExceptionally(new TimeoutException("timed out after " + millis + " ms")),
                millis, TimeUnit.MILLISECONDS);
        return timer;
    }

    public static void main(String[] args) {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            CompletableFuture<String> slowSource = new CompletableFuture<>(); // not completed yet

            CompletableFuture<String> withTimeout =
                    slowSource.applyToEither(timeoutAfter(scheduler, 100), Function.identity());

            try {
                withTimeout.join();
            } catch (CompletionException e) {
                System.out.println("wrapper failed with: " + e.getCause().getClass().getSimpleName()
                        + " (" + e.getCause().getMessage() + ")");
            }

            System.out.println("slow source is still running (not done): " + !slowSource.isDone()); // true
            slowSource.complete("late result");
            System.out.println("source finished later with: " + slowSource.join());
        } finally {
            scheduler.shutdownNow();
        }
    }
}
