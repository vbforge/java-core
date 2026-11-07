package com.vbforge.practice.errors.practice_9_exceptionally_and_handle;

import java.util.concurrent.CompletableFuture;

/**
 * exceptionally: an async "catch". It runs only when the stage failed and supplies a replacement value;
 * on success it is skipped and the value passes through unchanged.
 */
public class ExceptionallyRecoverDemo {

    static CompletableFuture<Integer> parseAsync(String text) {
        return CompletableFuture.supplyAsync(() -> Integer.parseInt(text));
    }

    public static void main(String[] args) {

        System.out.println("bad input  -> " + parseAsync("abc").exceptionally(e -> -1).join()); // -1
        System.out.println("good input -> " + parseAsync("42").exceptionally(e -> -1).join());  // 42
    }
}
