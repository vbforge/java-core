package com.vbforge.practice.many.practice_6_all_of;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * allOf without failing: turn every future into "did it succeed?" with handle(),
 * so allOf never sees an exception, then count the successes.
 */
public class AllOfCountSuccessfulDemo {

    @SuppressWarnings("rawtypes") // generic array creation is not allowed: new CompletableFuture[0] is the standard allOf idiom
    public static void main(String[] args) {

        List<CompletableFuture<String>> futures = Arrays.asList(
                CompletableFuture.supplyAsync(() -> "ok-1"),
                CompletableFuture.supplyAsync(() -> {
                    throw new IllegalStateException("fails");
                }),
                CompletableFuture.supplyAsync(() -> "ok-2"));

        List<CompletableFuture<Boolean>> outcomes = new java.util.ArrayList<>();
        for (CompletableFuture<String> f : futures) {
            outcomes.add(f.handle((value, error) -> error == null));
        }

        CompletableFuture.allOf(outcomes.toArray(new CompletableFuture[0])).join();

        long successful = outcomes.stream().filter(CompletableFuture::join).count();
        System.out.println("successful: " + successful + " of " + futures.size()); // 2 of 3
    }
}
