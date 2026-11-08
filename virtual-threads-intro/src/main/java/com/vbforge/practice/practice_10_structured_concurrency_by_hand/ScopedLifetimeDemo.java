package com.vbforge.practice.practice_10_structured_concurrency_by_hand;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The idea of structured concurrency with final APIs only: the try block is a scope, subtasks are
 * forked inside it and none of them outlives it. (StructuredTaskScope itself is a preview API in Java 21.)
 */
public class ScopedLifetimeDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        AtomicInteger finished = new AtomicInteger();
        List<Future<String>> subtasks = new ArrayList<>();

        try (ExecutorService scope = Executors.newVirtualThreadPerTaskExecutor()) {
            for (String name : List.of("user", "orders", "recommendations")) {
                subtasks.add(scope.submit(() -> {
                    Thread.sleep(50);
                    finished.incrementAndGet();
                    return name + " loaded";
                }));
            }
        } // leaving the scope: all subtasks are done, no thread is left behind

        System.out.println("subtasks finished at scope exit: " + finished.get());
        for (Future<String> subtask : subtasks) {
            System.out.println(subtask.get());
        }
    }
}
