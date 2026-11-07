package com.vbforge.practice.compose.practice_3_then_compose_vs_then_apply;

import java.util.concurrent.CompletableFuture;

/**
 * thenCompose is flatMap for futures: use it when the next step is itself async.
 * thenApply with an async function gives a nested CompletableFuture<CompletableFuture<T>>.
 */
public class ThenComposeVsThenApplyDemo {

    static CompletableFuture<String> findUser(int id) {
        return CompletableFuture.supplyAsync(() -> "user-" + id);
    }

    static CompletableFuture<String> findEmail(String user) {
        return CompletableFuture.supplyAsync(() -> user + "@example.com");
    }

    public static void main(String[] args) {

        // thenApply: nested future, you have to join twice
        CompletableFuture<CompletableFuture<String>> nested = findUser(7).thenApply(user -> findEmail(user));
        System.out.println("thenApply (nested): " + nested.join().join());

        // thenCompose: flat CompletableFuture<String>
        CompletableFuture<String> flat = findUser(7).thenCompose(user -> findEmail(user));
        System.out.println("thenCompose (flat): " + flat.join());
    }
}
