package com.vbforge.practice.practice_3_combining_many_futures;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class CombiningDemo {

    public static void main(String[] args) {
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Result A");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "Result B");
        CompletableFuture<String> f3 = CompletableFuture.supplyAsync(() -> "Result C");

        List<CompletableFuture<String>> futures = List.of(f1, f2, f3);

        // allOf waits for ALL to complete
        CompletableFuture<Void> allDone = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        
        // Gathering results in order after allOf completes
        CompletableFuture<List<String>> allResults = allDone.thenApply(v -> 
            futures.stream().map(CompletableFuture::join).collect(Collectors.toList())
        );

        System.out.println("All results: " + allResults.join());

        // anyOf returns the FIRST completed (success OR failure!)
        CompletableFuture<Object> anyDone = CompletableFuture.anyOf(
            CompletableFuture.supplyAsync(() -> { sleep(100); return "Slow"; }),
            CompletableFuture.supplyAsync(() -> "Fast")
        );
        System.out.println("Any result: " + anyDone.join());
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}