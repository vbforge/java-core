package com.vbforge.practice.practice_6_concurrent_hashmap_atomic_updates;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * ConcurrentHashMap.merge: an atomic read-modify-write on one key.
 * Four threads increment the same key 1000 times each, and no update is lost
 * (get + put would lose updates).
 */
public class SharedCounterWithMergeDemo {

    public static void main(String[] args) throws InterruptedException {

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(4);

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                map.merge("count", 1, Integer::sum);
            }
        };

        for (int i = 0; i < 4; i++) {
            executor.submit(task);
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        System.out.println("Final count: " + map.get("count")); // Expected: 4000
    }
}
