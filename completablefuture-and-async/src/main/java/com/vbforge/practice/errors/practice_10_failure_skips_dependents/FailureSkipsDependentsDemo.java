package com.vbforge.practice.errors.practice_10_failure_skips_dependents;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * When a stage fails, its dependent stages (thenApply, thenAccept, ...) are SKIPPED and
 * complete exceptionally with the same failure. Only exceptionally / handle / whenComplete see it.
 */
public class FailureSkipsDependentsDemo {

    public static void main(String[] args) {

        AtomicInteger dependentRuns = new AtomicInteger();

        CompletableFuture<String> failing = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("source failed");
        });

        CompletableFuture<Integer> chain = failing
                .thenApply(s -> {
                    dependentRuns.incrementAndGet();
                    return s.length();
                })
                .thenApply(n -> {
                    dependentRuns.incrementAndGet();
                    return n * 2;
                });

        CompletableFuture<Integer> recovered = chain.exceptionally(e -> -1);

        System.out.println("recovered value: " + recovered.join());                 // -1
        System.out.println("dependent stages that ran: " + dependentRuns.get());    // 0
        System.out.println("chain failed too: " + chain.isCompletedExceptionally()); // true
    }
}
