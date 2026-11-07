package com.vbforge.practice.compose.practice_5_join_vs_get;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * join() vs get() on a failed future:
 *   join() throws the UNCHECKED CompletionException (handy in lambdas)
 *   get()  throws the CHECKED ExecutionException (and InterruptedException)
 * In both cases the original exception is the cause.
 */
public class JoinVsGetDemo {

    public static void main(String[] args) throws InterruptedException {

        CompletableFuture<String> failing = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("boom");
        });

        try {
            failing.join();
        } catch (CompletionException e) {
            System.out.println("join: " + e.getClass().getSimpleName() + ", cause: " + e.getCause());
        }

        try {
            failing.get();
        } catch (ExecutionException e) {
            System.out.println("get:  " + e.getClass().getSimpleName() + ", cause: " + e.getCause());
        }
    }
}
