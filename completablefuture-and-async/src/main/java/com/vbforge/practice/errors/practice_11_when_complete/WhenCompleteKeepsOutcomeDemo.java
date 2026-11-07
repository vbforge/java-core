package com.vbforge.practice.errors.practice_11_when_complete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * whenComplete observes the outcome (cleanup, logging) and KEEPS it: a value stays a value, a failure stays a failure.
 * handle, in contrast, turns the outcome into a new result.
 */
public class WhenCompleteKeepsOutcomeDemo {

    public static void main(String[] args) {

        List<String> log = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Integer> ok = CompletableFuture.supplyAsync(() -> 5)
                .whenComplete((value, error) -> log.add("observed value " + value));
        System.out.println("success keeps its value: " + ok.join()); // 5

        CompletableFuture<Integer> failing = CompletableFuture.<Integer>supplyAsync(() -> {
            throw new IllegalStateException("boom");
        }).whenComplete((value, error) -> log.add("observed error " + rootMessage(error)));
        try {
            failing.join();
        } catch (CompletionException e) {
            System.out.println("failure is kept: " + e.getCause().getMessage()); // boom
        }

        System.out.println(log);
    }

    private static String rootMessage(Throwable t) {
        while (t instanceof CompletionException && t.getCause() != null) {
            t = t.getCause();
        }
        return t.getMessage();
    }
}
