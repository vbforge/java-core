package com.vbforge.practice.compose.practice_2_then_apply_accept_run;

import java.util.concurrent.CompletableFuture;

/**
 * thenApply: transform the result of a future with a synchronous function (like Optional.map).
 * The type may change along the chain.
 */
public class ThenApplyChainDemo {

    public static void main(String[] args) {

        CompletableFuture<String> upper = CompletableFuture
                .supplyAsync(() -> "  java  ")
                .thenApply(String::trim)
                .thenApply(String::toUpperCase);
        System.out.println("trim + upper: " + upper.join()); // JAVA

        CompletableFuture<Integer> length = CompletableFuture
                .supplyAsync(() -> "completable")
                .thenApply(String::length);   // CompletableFuture<String> -> CompletableFuture<Integer>
        System.out.println("length: " + length.join()); // 11
    }
}
