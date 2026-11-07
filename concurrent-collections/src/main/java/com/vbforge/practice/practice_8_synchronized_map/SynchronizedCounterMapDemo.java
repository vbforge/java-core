package com.vbforge.practice.practice_8_synchronized_map;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Thread-safe counter for multiple items on a synchronizedMap.
 * getOrDefault + put is a compound operation, so it needs an external lock on the map;
 * otherwise two threads can read the same value and one increment is lost.
 */
public class SynchronizedCounterMapDemo {

    private final Map<String, Integer> counterMap = Collections.synchronizedMap(new HashMap<>());

    public void increment(String key) {
        synchronized (counterMap) {
            counterMap.put(key, counterMap.getOrDefault(key, 0) + 1);
        }
    }

    public int get(String key) {
        synchronized (counterMap) {
            return counterMap.getOrDefault(key, 0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        SynchronizedCounterMapDemo counter = new SynchronizedCounterMapDemo();

        Runnable task = () -> {
            for (int i = 0; i < 1000; i++) {
                counter.increment("apple");
            }
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("Final Count: " + counter.get("apple")); // Expected: 2000
    }
}
