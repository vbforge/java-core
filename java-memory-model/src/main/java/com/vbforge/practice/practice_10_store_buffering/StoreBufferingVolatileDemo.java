package com.vbforge.practice.practice_10_store_buffering;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * The same litmus test with volatile semantics ({@code AtomicIntegerArray.get/set} behave like
 * volatile reads and writes). All volatile accesses form ONE total order that every thread
 * agrees on, so at least one thread must see the other's write:
 * r1 == 0 &amp;&amp; r2 == 0 is impossible. This is guaranteed, so it is asserted: the count is 0.
 */
public class StoreBufferingVolatileDemo {

    private static final int ROUNDS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        AtomicIntegerArray x = new AtomicIntegerArray(ROUNDS);
        AtomicIntegerArray y = new AtomicIntegerArray(ROUNDS);
        int[] r1 = new int[ROUNDS];
        int[] r2 = new int[ROUNDS];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    x.set(i, 1);              // volatile write
                    r1[i] = y.get(i);         // volatile read
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    y.set(i, 1);
                    r2[i] = x.get(i);
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        int forbidden = 0;
        for (int i = 0; i < ROUNDS; i++) {
            if (r1[i] == 0 && r2[i] == 0) {
                forbidden++;
            }
        }
        System.out.println("rounds:                " + ROUNDS);
        System.out.println("(0,0) outcomes:        " + forbidden + "  (impossible with volatile)");
    }
}
