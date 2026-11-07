package com.vbforge.practice.compose.practice_1_supply_and_run_async;

import java.util.concurrent.CompletableFuture;

/**
 * Futures you complete yourself: completedFuture(value) is already done;
 * new CompletableFuture<>() is open until someone calls complete(value). Only the first complete() wins.
 */
public class CompletedFutureManualCompleteDemo {

    public static void main(String[] args) {

        CompletableFuture<String> ready = CompletableFuture.completedFuture("ready");
        System.out.println("completedFuture isDone: " + ready.isDone() + ", value: " + ready.join());

        CompletableFuture<String> manual = new CompletableFuture<>();
        System.out.println("new future isDone: " + manual.isDone());
        System.out.println("getNow before complete: " + manual.getNow("default")); // default, does not wait

        System.out.println("first complete(): " + manual.complete("first"));   // true
        System.out.println("second complete(): " + manual.complete("second")); // false, already completed
        System.out.println("value: " + manual.join());                         // first
    }
}
