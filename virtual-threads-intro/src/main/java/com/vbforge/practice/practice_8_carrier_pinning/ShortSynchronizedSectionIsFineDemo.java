package com.vbforge.practice.practice_8_carrier_pinning;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Pinning only hurts when the thread BLOCKS inside synchronized. A short, non-blocking critical
 * section is perfectly fine with virtual threads: 10 000 of them still produce the exact total.
 */
public class ShortSynchronizedSectionIsFineDemo {

    private static final Object MONITOR = new Object();
    private static int counter;

    public static void main(String[] args) {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 10_000; i++) {
                executor.submit(() -> {
                    synchronized (MONITOR) {
                        counter++; // no I/O, no sleep, no waiting in here
                    }
                });
            }
        }
        System.out.println("counter = " + counter);
    }
}
