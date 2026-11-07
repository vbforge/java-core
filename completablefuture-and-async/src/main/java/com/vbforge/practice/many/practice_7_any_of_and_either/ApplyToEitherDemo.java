package com.vbforge.practice.many.practice_7_any_of_and_either;

import java.util.concurrent.CompletableFuture;

/**
 * applyToEither / acceptEither: typed "first of two". The function (or consumer) runs with
 * the result of whichever future completes first.
 */
public class ApplyToEitherDemo {

    public static void main(String[] args) {

        CompletableFuture<String> fast = CompletableFuture.completedFuture("fast");
        CompletableFuture<String> slow = new CompletableFuture<>(); // never completes in this demo

        String applied = fast.applyToEither(slow, String::toUpperCase).join();
        System.out.println("applyToEither: " + applied); // FAST

        StringBuilder consumed = new StringBuilder();
        fast.acceptEither(slow, consumed::append).join();
        System.out.println("acceptEither: " + consumed); // fast
    }
}
