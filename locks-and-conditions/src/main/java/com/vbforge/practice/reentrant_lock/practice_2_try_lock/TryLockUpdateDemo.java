package com.vbforge.practice.reentrant_lock.practice_2_try_lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tryLock() returns immediately: true = lock acquired, false = someone else holds it.
 * Unlock ONLY when tryLock returned true.
 */
public class TryLockUpdateDemo {

    static class TryLockResource {
        private final ReentrantLock lock = new ReentrantLock();
        private int resourceValue = 0;

        /** Updates the value if the lock is free right now; returns false without blocking otherwise. */
        public boolean tryUpdate(int newValue) {
            if (lock.tryLock()) {
                try {
                    resourceValue = newValue;
                    return true;
                } finally {
                    lock.unlock();
                }
            }
            return false; // lock was busy - caller decides what to do
        }

        public int getValue() {
            lock.lock();
            try {
                return resourceValue;
            } finally {
                lock.unlock();
            }
        }

        /** Lets the demo simulate a busy resource. */
        ReentrantLock lock() {
            return lock;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        TryLockResource resource = new TryLockResource();

        System.out.println("free lock, tryUpdate(42): " + resource.tryUpdate(42));
        System.out.println("value: " + resource.getValue());

        // another thread holds the lock until we say so
        CountDownLatch holding = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);
        Thread holder = new Thread(() -> {
            resource.lock().lock();
            try {
                holding.countDown();
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                resource.lock().unlock();
            }
        }, "holder");
        holder.start();
        holding.await();

        System.out.println("busy lock, tryUpdate(99): " + resource.tryUpdate(99));
        release.countDown();
        holder.join();

        System.out.println("value is still: " + resource.getValue());
    }
}
