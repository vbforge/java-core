package com.vbforge.practice.practice_6_concurrent_hashmap_atomic_updates;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Counting words from many threads with ConcurrentHashMap.
 *
 * ConcurrentHashMap is safe for individual operations, but a check-then-act sequence
 * is still a race:
 *     if (!map.containsKey(k)) map.put(k, v);     // two threads can both see "absent"
 * Use one atomic step instead: putIfAbsent / computeIfAbsent / merge.
 *
 * Here computeIfAbsent creates the counter once per word and AtomicInteger makes the increment atomic.
 */
public class ConcurrentWordCountDemo {

    public static void main(String[] args) throws InterruptedException {

        ConcurrentHashMap<String, AtomicInteger> wordCount = new ConcurrentHashMap<>();

        String[] words = {"java", "is", "fun", "java", "and", "fast", "java", "is"};

        // One thread per word, all updating the same map concurrently
        List<Thread> threads = new ArrayList<>();
        for (String word : words) {
            threads.add(new Thread(() ->
                    wordCount.computeIfAbsent(word, k -> new AtomicInteger(0)).incrementAndGet()));
        }

        threads.forEach(Thread::start);
        for (Thread t : threads) {
            t.join();
        }

        System.out.println("Word counts (thread-safe): " + new TreeMap<>(wordCount));
        // {and=1, fast=1, fun=1, is=2, java=3}
    }
}
