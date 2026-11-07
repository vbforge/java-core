package com.vbforge.practice.many.practice_6_all_of;

import java.util.concurrent.CompletableFuture;

/**
 * allOf is NOT fail-fast: even if one input has failed, allOf waits for the slowest input
 * and only then completes exceptionally. Manually completed futures make this visible without any timing.
 */
public class AllOfNotFailFastDemo {

    public static void main(String[] args) {

        CompletableFuture<String> failed = new CompletableFuture<>();
        failed.completeExceptionally(new IllegalStateException("one input failed"));
        CompletableFuture<String> slow = new CompletableFuture<>(); // still running

        CompletableFuture<Void> all = CompletableFuture.allOf(failed, slow);
        System.out.println("failed input is done, allOf done? " + all.isDone()); // false: waits for 'slow'

        slow.complete("finally");
        System.out.println("slow input done, allOf done? " + all.isDone());
        System.out.println("allOf failed: " + all.isCompletedExceptionally()); // true
    }
}
