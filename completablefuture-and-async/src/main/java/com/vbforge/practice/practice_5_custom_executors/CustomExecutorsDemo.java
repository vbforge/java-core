package com.vbforge.practice.practice_5_custom_executors;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomExecutorsDemo {

    public static void main(String[] args) {
        // Create a custom pool for I/O bound work
        ExecutorService ioPool = Executors.newFixedThreadPool(4, r -> {
            Thread t = new Thread(r);
            t.setName("io-pool-" + t.getId());
            t.setDaemon(true); // Prevent JVM from hanging if not shut down
            return t;
        });

        CompletableFuture<String> future = CompletableFuture.supplyAsync(() -> {
            System.out.println("Running on: " + Thread.currentThread().getName());
            return "Data from DB";
        }, ioPool); // Pass the custom executor

        System.out.println("Result: " + future.join());
        
        ioPool.shutdown();
    }
}