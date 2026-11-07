package com.vbforge.practice.executors.practice_16_then_apply_vs_async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

/**
 * Which thread runs a callback?
 *   thenApply          runs on the thread that completes the future, or on the CALLER if it is already complete
 *   thenApplyAsync(fn, executor)  always runs on the executor you pass (move heavy callbacks to your own pool)
 */
public class ThenApplyVsThenApplyAsyncDemo {

    public static void main(String[] args) throws InterruptedException {

        ThreadFactory factory = r -> new Thread(r, "callback-pool-1");
        ExecutorService pool = Executors.newSingleThreadExecutor(factory);
        try {
            // already completed: thenApply runs on the calling (main) thread
            String sync = CompletableFuture.completedFuture(1)
                    .thenApply(x -> Thread.currentThread().getName()).join();
            System.out.println("thenApply on a completed future:  " + sync);

            // thenApplyAsync with an executor: always on the pool thread
            String async = CompletableFuture.completedFuture(1)
                    .thenApplyAsync(x -> Thread.currentThread().getName(), pool).join();
            System.out.println("thenApplyAsync(fn, executor):     " + async);

            // callback registered BEFORE completion: runs on the thread that completes the future
            CompletableFuture<Integer> open = new CompletableFuture<>();
            CompletableFuture<String> callback = open.thenApply(x -> Thread.currentThread().getName());
            Thread completer = new Thread(() -> open.complete(1), "completer-thread");
            completer.start();
            completer.join();
            System.out.println("thenApply registered before complete: " + callback.join());
        } finally {
            pool.shutdown();
        }
    }
}
