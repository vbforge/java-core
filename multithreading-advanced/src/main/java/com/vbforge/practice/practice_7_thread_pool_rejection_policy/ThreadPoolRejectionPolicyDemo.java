package com.vbforge.practice.practice_7_thread_pool_rejection_policy;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread pool work queue and rejection.
 * Scenario: a ThreadPoolExecutor with a BOUNDED LinkedBlockingQueue as its work queue.
 * 2 core threads, queue of 10, max 4 threads:
 *   tasks 1-2   -> core threads
 *   tasks 3-12  -> the queue (capacity 10)
 *   tasks 13-14 -> two extra threads (queue is full, max is 4)
 *   task 15     -> rejected: the RejectedExecutionHandler runs
 *
 * Alternatives for the handler:
 *   Option 1: increase the queue size to prevent rejections: new LinkedBlockingQueue<>(20)
 *   Option 2: new ThreadPoolExecutor.CallerRunsPolicy()  (the submitting thread runs the task itself)
 *   Others:   AbortPolicy (the default, throws RejectedExecutionException), DiscardPolicy, DiscardOldestPolicy
 */
public class ThreadPoolRejectionPolicyDemo {

    public static void main(String[] args) throws InterruptedException {

        AtomicInteger rejected = new AtomicInteger();
        AtomicInteger completed = new AtomicInteger();
        Set<String> threadNames = ConcurrentHashMap.newKeySet();

        // own handler: counts the rejection and drops the task
        RejectedExecutionHandler handler = (r, executor) -> rejected.incrementAndGet();

        ExecutorService executor = new ThreadPoolExecutor(
                2, 4,
                60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10),
                handler);

        for (int i = 1; i <= 15; i++) {
            executor.submit(() -> {
                threadNames.add(Thread.currentThread().getName());
                try {
                    TimeUnit.MILLISECONDS.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                completed.incrementAndGet();
            });
        }

        executor.shutdown();

        // wait for all tasks to finish
        if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
            System.out.println("Some tasks did not finish in time");
        }

        System.out.println("Completed tasks: " + completed.get());   // 14
        System.out.println("Rejected tasks: " + rejected.get());     // 1
        System.out.println("Threads used: " + threadNames.size());   // 4
    }
}
