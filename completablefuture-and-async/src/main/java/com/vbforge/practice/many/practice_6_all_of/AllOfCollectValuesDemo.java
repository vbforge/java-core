package com.vbforge.practice.many.practice_6_all_of;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * allOf returns CompletableFuture<Void>: it tells you WHEN all are done, not WHAT they returned.
 * Read the values from the input futures afterwards; that also keeps the INPUT order.
 */
public class AllOfCollectValuesDemo {

    @SuppressWarnings("rawtypes") // generic array creation is not allowed: new CompletableFuture[0] is the standard allOf idiom
    public static void main(String[] args) {

        List<CompletableFuture<Integer>> futures = new ArrayList<>();
        for (int i = 1; i <= 5; i++) {
            int n = i;
            futures.add(CompletableFuture.supplyAsync(() -> n * 10));
        }

        CompletableFuture<Void> all = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        System.out.println("allOf(...).join() is: " + all.join()); // null

        List<Integer> values = all
                .thenApply(ignored -> futures.stream().map(CompletableFuture::join).collect(Collectors.toList()))
                .join();
        System.out.println("values in input order: " + values); // [10, 20, 30, 40, 50]
    }
}
