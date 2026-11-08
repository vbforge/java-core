package com.vbforge.practice.practice_8_carrier_pinning;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Carrier pinning, made visible with a scheduler that has exactly ONE carrier thread.
 *
 * A "slow" virtual thread sleeps inside a critical section, then a "fast" unrelated virtual thread starts.
 * - sleep inside synchronized: on Java 21 the slow thread pins the only carrier, so "fast" waits for it.
 * - sleep inside ReentrantLock: the slow thread unmounts while sleeping, so "fast" finishes first.
 *
 * Java 21 behaviour (JEP 491 removed this pinning in Java 24). To see the stack trace of a pinned
 * thread yourself, run with -Djdk.tracePinnedThreads=full.
 */
public class PinningOrderDemo {

    private static final Object MONITOR = new Object();
    private static final ReentrantLock LOCK = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        // Must be set before the first virtual thread is created.
        System.setProperty("jdk.virtualThreadScheduler.parallelism", "1");
        System.setProperty("jdk.virtualThreadScheduler.maxPoolSize", "1");

        System.out.println("sleep inside synchronized   -> " + run(() -> {
            synchronized (MONITOR) {
                sleepInside();
            }
        }));

        System.out.println("sleep inside ReentrantLock  -> " + run(() -> {
            LOCK.lock();
            try {
                sleepInside();
            } finally {
                LOCK.unlock();
            }
        }));
    }

    private static final ThreadLocal<CountDownLatch> INSIDE = new ThreadLocal<>();

    private static void sleepInside() {
        INSIDE.get().countDown();
        try {
            Thread.sleep(Duration.ofMillis(300));
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static List<String> run(Runnable criticalSection) throws InterruptedException {
        List<String> completionOrder = Collections.synchronizedList(new ArrayList<>());
        CountDownLatch slowIsInside = new CountDownLatch(1);

        Thread slow = Thread.ofVirtual().name("slow").start(() -> {
            INSIDE.set(slowIsInside);
            criticalSection.run();
            completionOrder.add("slow");
        });

        slowIsInside.await(); // only now start the unrelated thread
        Thread fast = Thread.ofVirtual().name("fast").start(() -> completionOrder.add("fast"));

        slow.join();
        fast.join();
        return new ArrayList<>(completionOrder);
    }
}
