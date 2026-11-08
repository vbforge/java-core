package com.vbforge.practice.read_write.practice_8_get_or_compute_recheck;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Read first (shared lock), then - only on a miss - release it, take the write lock and RE-CHECK:
 * another thread may have filled the entry in between. Without the re-check the loader could run many times.
 * (There is no read -> write upgrade, so the read lock must be released first.)
 */
public class GetOrComputeDemo {

    static class LoadingCache {
        private final Map<String, String> map = new HashMap<>();
        private final ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        final AtomicInteger loads = new AtomicInteger();

        String getOrCompute(String key) {
            rw.readLock().lock();
            try {
                String value = map.get(key);
                if (value != null) {
                    return value;
                }
            } finally {
                rw.readLock().unlock();
            }

            rw.writeLock().lock();
            try {
                String value = map.get(key); // re-check under the write lock
                if (value == null) {
                    value = load(key);
                    map.put(key, value);
                }
                return value;
            } finally {
                rw.writeLock().unlock();
            }
        }

        private String load(String key) {
            loads.incrementAndGet();
            try {
                Thread.sleep(20); // pretend to be slow
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return "value-of-" + key;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        LoadingCache cache = new LoadingCache();
        final int threads = 8;
        CountDownLatch start = new CountDownLatch(1);
        Thread[] workers = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                try {
                    start.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    return;
                }
                cache.getOrCompute("answer");
            });
            workers[i].start();
        }
        start.countDown();
        for (Thread t : workers) t.join();

        System.out.println("threads asking for the same key: " + threads);
        System.out.println("loader calls: " + cache.loads.get());
        System.out.println("cached value: " + cache.getOrCompute("answer"));
    }
}
