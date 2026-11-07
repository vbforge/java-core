package com.vbforge.practice.compose.practice_1_supply_and_run_async;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * runAsync: run a Runnable on another thread. There is no result, so the future is CompletableFuture<Void>
 * and join() returns null; it only tells you WHEN the work is done.
 */
public class RunAsyncDemo {

    public static void main(String[] args) {

        AtomicBoolean ran = new AtomicBoolean(false);

        CompletableFuture<Void> done = CompletableFuture.runAsync(() -> ran.set(true));

        Void result = done.join();
        System.out.println("join() result: " + result);   // null
        System.out.println("task ran: " + ran.get());       // true
    }
}
