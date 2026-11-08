package com.vbforge.practice.practice_11_message_passing;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Message passing under ONE lock. Both fields are plain; the writer sets both inside a
 * synchronized block, the reader reads both inside a synchronized block on the same object.
 * The blocks are mutually exclusive and unlock happens-before the next lock, so the reader
 * sees either nothing (0, 0) or everything (1, 42), never a mix. Guaranteed, so asserted.
 */
public class MessagePassingUnderLockDemo {

    private static final int ROUNDS = 10_000;
    private static final Object LOCK = new Object();

    public static void main(String[] args) throws InterruptedException {
        int[] data = new int[ROUNDS];      // plain, guarded by LOCK
        int[] flag = new int[ROUNDS];      // plain, guarded by LOCK
        int[] seenFlag = new int[ROUNDS];
        int[] seenData = new int[ROUNDS];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread writer = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    synchronized (LOCK) {
                        data[i] = 42;
                        flag[i] = 1;
                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        Thread reader = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    synchronized (LOCK) {
                        seenFlag[i] = flag[i];
                        seenData[i] = data[i];
                    }
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        writer.start();
        reader.start();
        writer.join();
        reader.join();

        int mixed = 0;
        for (int i = 0; i < ROUNDS; i++) {
            boolean nothing = seenFlag[i] == 0 && seenData[i] == 0;
            boolean everything = seenFlag[i] == 1 && seenData[i] == 42;
            if (!nothing && !everything) {
                mixed++;
            }
        }
        System.out.println("rounds:                   " + ROUNDS);
        System.out.println("mixed observations:       " + mixed + "  (impossible under one lock)");
    }
}
