package com.vbforge.practice.practice_4_executor_per_task;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 1000 tasks on a per-task virtual executor use 1000 threads; a fixed pool of 4 reuses its 4 threads. */
public class ThreadsAreNeverReusedDemo {

    private static final int TASKS = 1_000;

    public static void main(String[] args) {
        Set<Thread> virtualThreads = ConcurrentHashMap.newKeySet();
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < TASKS; i++) {
                executor.submit(() -> virtualThreads.add(Thread.currentThread()));
            }
        }
        System.out.println("virtual per-task executor: distinct threads == tasks : " + (virtualThreads.size() == TASKS));

        Set<Thread> pooledThreads = ConcurrentHashMap.newKeySet();
        try (ExecutorService pool = Executors.newFixedThreadPool(4)) {
            for (int i = 0; i < TASKS; i++) {
                pool.submit(() -> pooledThreads.add(Thread.currentThread()));
            }
        }
        System.out.println("fixed pool of 4          : distinct threads <= 4     : " + (pooledThreads.size() <= 4));
    }
}
