package com.vbforge.practice.practice_11_message_passing;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Message-passing litmus test with a PLAIN flag.
 * <pre>
 *   writer: data = 42; flag = 1;        reader: f = flag; d = data;
 * </pre>
 * Seeing f == 1 together with d == 0 (a "stale read") is LEGAL here: nothing orders the two
 * writes or the two reads. Weakly ordered CPUs (ARM) can show it, x86 usually does not, so
 * it is not asserted. Only a guaranteed fact is printed: d is 0 or 42, never anything else.
 */
public class MessagePassingPlainDemo {

    private static final int ROUNDS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        int[] data = new int[ROUNDS];
        int[] flag = new int[ROUNDS];
        int[] seenFlag = new int[ROUNDS];
        int[] seenData = new int[ROUNDS];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread writer = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    data[i] = 42;
                    flag[i] = 1;
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        Thread reader = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    seenFlag[i] = flag[i];
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

        boolean valuesValid = true;
        for (int i = 0; i < ROUNDS; i++) {
            valuesValid &= seenData[i] == 0 || seenData[i] == 42;
        }
        System.out.println("rounds:               " + ROUNDS);
        System.out.println("data is 0 or 42:      " + valuesValid);
        System.out.println("stale read (1, 0):    legal with a plain flag, not counted here");
    }
}
