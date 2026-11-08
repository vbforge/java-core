package com.vbforge.practice.practice_4_executor_per_task;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/** One virtual thread per task. Results come back in submission order because we read the futures in that order. */
public class SquaresPerTaskDemo {

    public static void main(String[] args) throws InterruptedException, ExecutionException {
        List<Future<Integer>> futures = new ArrayList<>();

        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 1; i <= 10; i++) {
                final int n = i;
                futures.add(executor.submit(() -> n * n));
            }
        }

        List<Integer> squares = new ArrayList<>();
        for (Future<Integer> future : futures) {
            squares.add(future.get());
        }
        System.out.println("squares: " + squares);
    }
}
