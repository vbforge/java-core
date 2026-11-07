package com.vbforge.practice.executors.practice_18_java8_timeouts;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Java 8 version of completeOnTimeout: the timer future is completed with a FALLBACK VALUE
 * instead of an exception, and applyToEither takes whichever finishes first.
 */
public class FallbackOnTimeoutDemo {

    static <T> CompletableFuture<T> withFallbackOnTimeout(CompletableFuture<T> source, ScheduledExecutorService scheduler,
                                                          long millis, T fallback) {
        CompletableFuture<T> timer = new CompletableFuture<>();
        scheduler.schedule(() -> timer.complete(fallback), millis, TimeUnit.MILLISECONDS);
        return source.applyToEither(timer, Function.identity());
    }

    public static void main(String[] args) {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
        try {
            CompletableFuture<String> slow = new CompletableFuture<>(); // never completes in this demo
            System.out.println("slow source:  " + withFallbackOnTimeout(slow, scheduler, 100, "fallback").join()); // fallback

            CompletableFuture<String> fast = CompletableFuture.completedFuture("real value");
            System.out.println("fast source:  " + withFallbackOnTimeout(fast, scheduler, 1000, "fallback").join()); // real value
        } finally {
            scheduler.shutdownNow();
        }
    }
}
