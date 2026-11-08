package com.vbforge.practice.practice_11_message_passing;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Message passing with a VOLATILE flag (AtomicIntegerArray.set/get = volatile write/read).
 * The writer's plain write to data happens-before its volatile write of the flag; a reader
 * that sees flag == 1 sees that volatile write, so it also sees data == 42.
 * "flag seen, data stale" is impossible: guaranteed, so asserted (count is 0).
 */
public class MessagePassingVolatileFlagDemo {

    private static final int ROUNDS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        int[] data = new int[ROUNDS];                       // plain
        AtomicIntegerArray flag = new AtomicIntegerArray(ROUNDS); // volatile semantics
        int[] seenFlag = new int[ROUNDS];
        int[] seenData = new int[ROUNDS];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread writer = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    data[i] = 42;
                    flag.set(i, 1);                         // publishes data[i]
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        Thread reader = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    seenFlag[i] = flag.get(i);              // read the flag FIRST
                    seenData[i] = data[i];
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        writer.start();
        reader.start();
        writer.join();
        reader.join();

        int stale = 0;
        for (int i = 0; i < ROUNDS; i++) {
            if (seenFlag[i] == 1 && seenData[i] != 42) {
                stale++;
            }
        }
        System.out.println("rounds:              " + ROUNDS);
        System.out.println("stale reads (1, 0):  " + stale + "  (impossible with a volatile flag)");
    }
}
