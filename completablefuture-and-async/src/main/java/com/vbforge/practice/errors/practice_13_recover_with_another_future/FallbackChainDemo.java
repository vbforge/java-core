package com.vbforge.practice.errors.practice_13_recover_with_another_future;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Java 8 has no exceptionallyCompose, so "recover with another FUTURE" is built from
 * handle (value or fallback future) + thenCompose(identity) to flatten.
 * The fallback is only started when the primary failed.
 */
public class FallbackChainDemo {

    static <T> CompletableFuture<T> recoverWith(CompletableFuture<T> primary, Supplier<CompletableFuture<T>> fallback) {
        CompletableFuture<CompletableFuture<T>> nested = primary.handle((value, error) ->
                error == null ? CompletableFuture.completedFuture(value) : fallback.get());
        return nested.thenCompose(Function.identity());
    }

    private static CompletableFuture<String> failing(String message) {
        return CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException(message);
        });
    }

    public static void main(String[] args) {

        AtomicInteger fallbackStarts = new AtomicInteger();

        // primary succeeds: the fallback is never started
        CompletableFuture<String> fine = recoverWith(
                CompletableFuture.supplyAsync(() -> "primary"),
                () -> {
                    fallbackStarts.incrementAndGet();
                    return CompletableFuture.completedFuture("fallback");
                });
        System.out.println(fine.join() + ", fallback started: " + fallbackStarts.get()); // primary, 0

        // primary and secondary fail, tertiary answers
        CompletableFuture<String> chain = recoverWith(
                recoverWith(failing("primary down"), () -> failing("secondary down")),
                () -> CompletableFuture.supplyAsync(() -> "tertiary"));
        System.out.println("chain result: " + chain.join()); // tertiary
    }
}
