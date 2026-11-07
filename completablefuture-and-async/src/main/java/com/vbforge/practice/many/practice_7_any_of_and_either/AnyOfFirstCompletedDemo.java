package com.vbforge.practice.many.practice_7_any_of_and_either;

import java.util.concurrent.CompletableFuture;

/**
 * anyOf returns CompletableFuture<Object> (the type is lost) with the first COMPLETED result,
 * not the first SUCCESSFUL one: an early error beats a later success.
 */
public class AnyOfFirstCompletedDemo {

    public static void main(String[] args) {

        // first completed wins, typed as Object
        CompletableFuture<String> done = CompletableFuture.completedFuture("done");
        CompletableFuture<String> open = new CompletableFuture<>();
        Object first = CompletableFuture.anyOf(done, open).join();
        System.out.println("anyOf result: " + first);

        // an early failure wins over a later success
        CompletableFuture<String> failsFast = new CompletableFuture<>();
        failsFast.completeExceptionally(new IllegalStateException("fast failure"));
        CompletableFuture<String> succeedsLater = new CompletableFuture<>();

        CompletableFuture<Object> any = CompletableFuture.anyOf(failsFast, succeedsLater);
        succeedsLater.complete("late success");
        System.out.println("anyOf failed although a later future succeeded: " + any.isCompletedExceptionally()); // true
    }
}
