package com.vbforge.practice.many.practice_8_first_successful;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * "First success", which anyOf cannot do: complete your OWN future from callbacks.
 * The first successful input completes it; it fails only when ALL inputs have failed.
 */
public class FirstSuccessfulDemo {

    static <T> CompletableFuture<T> firstSuccessful(List<CompletableFuture<T>> futures) {
        CompletableFuture<T> result = new CompletableFuture<>();
        AtomicInteger failures = new AtomicInteger();
        for (CompletableFuture<T> f : futures) {
            f.whenComplete((value, error) -> {
                if (error == null) {
                    result.complete(value); // only the first call has an effect
                } else if (failures.incrementAndGet() == futures.size()) {
                    result.completeExceptionally(new IllegalStateException("all " + futures.size() + " failed"));
                }
            });
        }
        return result;
    }

    private static CompletableFuture<String> failed(String message) {
        CompletableFuture<String> f = new CompletableFuture<>();
        f.completeExceptionally(new RuntimeException(message));
        return f;
    }

    public static void main(String[] args) {

        // one fails right away, another succeeds later
        CompletableFuture<String> okLater = new CompletableFuture<>();
        CompletableFuture<String> first = firstSuccessful(Arrays.asList(failed("error-1"), okLater));
        System.out.println("after the early failure, done? " + first.isDone()); // false (anyOf would have failed)
        okLater.complete("ok");
        System.out.println("first successful: " + first.join());                // ok

        // everything fails
        CompletableFuture<String> none = firstSuccessful(Arrays.asList(failed("error-1"), failed("error-2")));
        System.out.println("all failed -> completed exceptionally: " + none.isCompletedExceptionally()); // true
    }
}
