package com.vbforge.practice.executors.practice_17_fan_out_fan_in;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Fan-out / fan-in: start one future per element on an executor (fan-out),
 * wait with allOf and combine the results in input order (fan-in).
 */
public class SumOfSquaresFanOutDemo {

    @SuppressWarnings("rawtypes") // generic array creation is not allowed: new CompletableFuture[0] is the standard allOf idiom
    public static void main(String[] args) {

        ExecutorService pool = Executors.newFixedThreadPool(4);
        try {
            List<CompletableFuture<Integer>> futures = IntStream.rangeClosed(1, 10)
                    .boxed()
                    .map(n -> CompletableFuture.supplyAsync(() -> n * n, pool))
                    .collect(Collectors.toList());

            int sum = CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                    .thenApply(ignored -> futures.stream().mapToInt(CompletableFuture::join).sum())
                    .join();

            System.out.println("squares: " + futures.stream().map(CompletableFuture::join).collect(Collectors.toList()));
            System.out.println("sum of squares 1..10: " + sum); // 385
        } finally {
            pool.shutdown();
        }
    }
}
