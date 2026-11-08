package com.vbforge.practice.reentrant_lock.practice_1_reentrant_lock_counter;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * ReentrantLock as a drop-in replacement for a synchronized counter.
 * Pattern: lock() BEFORE try, unlock() in finally - every single time.
 */
public class ReentrantLockCounterDemo {

    static class ReentrantCounter {
        private final ReentrantLock lock = new ReentrantLock();
        private int count = 0;

        public void increment() {
            lock.lock();
            try {
                count++;
            } finally {
                lock.unlock();
            }
        }

        public int get() {
            lock.lock();
            try {
                return count;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        final int threads = 5;
        final int increments = 1_000;
        ReentrantCounter counter = new ReentrantCounter();

        List<Thread> list = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            list.add(new Thread(() -> {
                for (int j = 0; j < increments; j++) {
                    counter.increment();
                }
            }));
        }
        for (Thread t : list) t.start();
        for (Thread t : list) t.join();

        System.out.printf("Expected %d, got %d%n", threads * increments, counter.get());
    }
}
