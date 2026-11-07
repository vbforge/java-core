package com.vbforge.practice.executors.practice_15_custom_executor;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * supplyAsync(supplier, executor): run the work on a pool you control (give blocking work its own pool,
 * not the common pool) with a ThreadFactory that names the threads and sets the daemon flag.
 * Executors.newFixedThreadPool threads are NON-daemon by default: forgetting shutdown() keeps the JVM alive,
 * so shut the pool down in finally.
 */
public class CustomExecutorThreadNamesDemo {

    static ThreadFactory namedFactory(String prefix, boolean daemon) {
        AtomicInteger counter = new AtomicInteger(1);
        return runnable -> {
            Thread t = new Thread(runnable, prefix + counter.getAndIncrement());
            t.setDaemon(daemon);
            return t;
        };
    }

    public static void main(String[] args) throws InterruptedException {

        ExecutorService ioPool = Executors.newFixedThreadPool(2, namedFactory("io-pool-", true));
        try {
            String info = CompletableFuture
                    .supplyAsync(() -> Thread.currentThread().getName() + "|" + Thread.currentThread().isDaemon(), ioPool)
                    .join();
            String[] parts = info.split("\\|");
            System.out.println("ran on a thread named io-pool-N: " + parts[0].startsWith("io-pool-"));
            System.out.println("thread is daemon: " + parts[1]);
        } finally {
            ioPool.shutdown();
        }
        System.out.println("terminated after shutdown: " + ioPool.awaitTermination(5, TimeUnit.SECONDS));
    }
}
