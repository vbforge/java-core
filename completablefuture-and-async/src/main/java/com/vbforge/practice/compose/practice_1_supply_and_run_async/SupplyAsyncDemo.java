package com.vbforge.practice.compose.practice_1_supply_and_run_async;

import java.util.concurrent.CompletableFuture;

/**
 * supplyAsync: run a Supplier on another thread (ForkJoinPool.commonPool() by default)
 * and get a CompletableFuture<T> for its result. join() waits for the value.
 */
public class SupplyAsyncDemo {

    public static void main(String[] args) {

        CompletableFuture<String> greeting = CompletableFuture.supplyAsync(() -> "hello");
        System.out.println("supplyAsync result: " + greeting.join());

        // the supplier runs on a pool thread, not on the calling thread
        CompletableFuture<String> threadName = CompletableFuture.supplyAsync(() -> Thread.currentThread().getName());
        System.out.println("ran on another thread than main: " + !"main".equals(threadName.join()));
    }
}
