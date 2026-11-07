package com.vbforge.practice.errors.practice_9_exceptionally_and_handle;

import java.util.concurrent.CompletableFuture;

/**
 * handle((value, error) -> result): always runs, sees either the value or the error (the other one is null)
 * and transforms the outcome into a new result.
 */
public class HandleValueOrErrorDemo {

    static String describe(CompletableFuture<Integer> future) {
        return future.handle((value, error) -> error == null ? "value " + value : "failed").join();
    }

    public static void main(String[] args) {

        System.out.println(describe(CompletableFuture.supplyAsync(() -> 10)));
        System.out.println(describe(CompletableFuture.<Integer>supplyAsync(() -> {
            throw new IllegalStateException("boom");
        })));
    }
}
