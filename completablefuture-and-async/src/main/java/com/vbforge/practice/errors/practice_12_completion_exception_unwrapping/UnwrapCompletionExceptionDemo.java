package com.vbforge.practice.errors.practice_12_completion_exception_unwrapping;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * The exception you receive is often WRAPPED in a CompletionException, so
 *   e instanceof IllegalStateException
 * can be false. Unwrap CompletionException / ExecutionException first, then inspect the cause.
 */
public class UnwrapCompletionExceptionDemo {

    static Throwable unwrap(Throwable t) {
        while ((t instanceof CompletionException || t instanceof ExecutionException) && t.getCause() != null) {
            t = t.getCause();
        }
        return t;
    }

    public static void main(String[] args) {

        // 1. a supplier throws: the handler sees a CompletionException
        CompletableFuture<Integer> thrown = CompletableFuture.supplyAsync(() -> {
            throw new IllegalStateException("from supplier");
        });
        System.out.println("received type: " + thrown.handle((v, e) -> e.getClass().getSimpleName()).join());
        System.out.println("instanceof without unwrap: " + thrown.exceptionally(e -> e instanceof IllegalStateException ? 0 : -1).join()); // -1
        System.out.println("instanceof with unwrap:    " + thrown.exceptionally(e -> unwrap(e) instanceof IllegalStateException ? 0 : -1).join()); // 0

        // 2. completeExceptionally on the future itself: the handler sees the RAW exception ...
        CompletableFuture<Integer> manual = new CompletableFuture<>();
        manual.completeExceptionally(new IllegalStateException("manual"));
        System.out.println("manual, own handler:       " + manual.handle((v, e) -> e.getClass().getSimpleName()).join());

        // ... but a DEPENDENT stage receives it wrapped
        System.out.println("manual, dependent handler: " + manual.thenApply(v -> v + 1).handle((v, e) -> e.getClass().getSimpleName()).join());
    }
}
