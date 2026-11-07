package com.vbforge.practice.practice_8_synchronized_map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Thread-safe cache on a synchronizedMap that auto-loads a value when it is missing.
 * Two threads ask for the same key; the loader must run only once.
 *
 * Note: SynchronizedMap.computeIfAbsent is already synchronized on the wrapper,
 * so the explicit synchronized block is redundant here; it is kept to make the locking visible.
 */
public class SynchronizedAutoFillCacheDemo {

    private final Map<String, String> cache = Collections.synchronizedMap(new HashMap<>());
    private final AtomicInteger loads = new AtomicInteger();

    public String getValue(String key) {
        synchronized (cache) {
            return cache.computeIfAbsent(key, k -> {
                loads.incrementAndGet(); // "Loading value for: <key>"
                return "Value-" + k.toUpperCase();
            });
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedAutoFillCacheDemo cache = new SynchronizedAutoFillCacheDemo();
        List<String> results = Collections.synchronizedList(new ArrayList<>());

        Runnable r = () -> results.add(cache.getValue("user1"));

        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Results: " + results);
        System.out.println("Loader ran " + cache.loads.get() + " time(s)"); // 1
    }
}
