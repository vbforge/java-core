package com.vbforge.practice.queue.practice_14_delay_queue_problems;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.DelayQueue;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * Expiring cache entries: a time-sensitive cache that removes old entries by itself.
 *   - values live in a ConcurrentHashMap (thread-safe: read by main, cleaned by the cleaner thread)
 *   - a DelayQueue of CacheEntry tells a daemon cleaner thread WHEN to remove each key
 *   - put() encapsulates adding to both structures
 *   - the cleaner uses remove(key, value), so a newer value stored under the same key is not removed by mistake
 */
public class ExpiringCacheDemo {

    private static final DelayQueue<CacheEntry> cacheQueue = new DelayQueue<>();
    private static final ConcurrentMap<String, String> cacheMap = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {

        // Start the cleaner thread
        Thread cleaner = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    CacheEntry entry = cacheQueue.take();
                    // Remove the key in a thread-safe manner
                    if (cacheMap.remove(entry.key(), entry.value())) {
                        System.out.println("Removed expired cache: " + entry.key());
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        });
        cleaner.setDaemon(true);
        cleaner.start();

        // Add cache entries
        put("token-1", "user-1", 3000L);
        put("token-2", "user-1", 10000L);
        Thread.sleep(5000); // wait for 5 seconds

        System.out.println("Cache contains token-1: " + cacheMap.containsKey("token-1")); // false, removed by the cleaner
        System.out.println("Cache contains token-2: " + cacheMap.containsKey("token-2")); // true, not expired yet
    }

    public static void put(String key, String value, long ttlMillis) {
        cacheMap.put(key, value);
        cacheQueue.put(CacheEntry.of(key, value, ttlMillis));
    }

    /** expiresAtMillis is an absolute time; use CacheEntry.of(key, value, ttl) to build it from a time-to-live. */
    record CacheEntry(String key, String value, long expiresAtMillis) implements Delayed {

        static CacheEntry of(String key, String value, long ttlMillis) {
            return new CacheEntry(key, value, System.currentTimeMillis() + ttlMillis);
        }

        @Override
        public int compareTo(Delayed o) {
            return Long.compare(this.expiresAtMillis, ((CacheEntry) o).expiresAtMillis);
        }

        @Override
        public long getDelay(TimeUnit unit) {
            long diff = expiresAtMillis - System.currentTimeMillis();
            return unit.convert(diff, TimeUnit.MILLISECONDS);
        }
    }
}
