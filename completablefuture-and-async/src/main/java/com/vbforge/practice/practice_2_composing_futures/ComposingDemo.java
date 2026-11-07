package com.vbforge.practice.practice_2_composing_futures;

import java.util.concurrent.CompletableFuture;

public class ComposingDemo {
    
    // Simulates fetching a user ID
    public static CompletableFuture<Integer> fetchUserId(String username) {
        return CompletableFuture.supplyAsync(() -> username.length() * 10);
    }

    // Simulates fetching an email based on user ID
    public static CompletableFuture<String> fetchEmail(int userId) {
        return CompletableFuture.supplyAsync(() -> "user" + userId + "@example.com");
    }

    public static void main(String[] args) {
        // ❌ BAD: Nested future
        CompletableFuture<CompletableFuture<String>> nested = fetchUserId("alice")
                .thenApply(id -> fetchEmail(id));
        
        // ✅ GOOD: Flattened via thenCompose
        CompletableFuture<String> flat = fetchUserId("alice")
                .thenCompose(id -> fetchEmail(id));

        System.out.println("Email: " + flat.join());
    }
}