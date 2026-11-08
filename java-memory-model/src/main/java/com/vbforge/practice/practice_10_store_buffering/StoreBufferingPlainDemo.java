package com.vbforge.practice.practice_10_store_buffering;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * Store-buffering litmus test with PLAIN fields.
 * <pre>
 *   thread 1: x = 1; r1 = y;        thread 2: y = 1; r2 = x;
 * </pre>
 * With plain fields the outcome r1 == 0 &amp;&amp; r2 == 0 is LEGAL: each store may still sit in a
 * store buffer when the other thread reads. Whether it shows up depends on the CPU, the JIT
 * and the scheduler (x86 shows it, many runs on other machines do not), so it is NOT counted
 * or asserted here. Only the guaranteed part is printed: every value is 0 or 1.
 */
public class StoreBufferingPlainDemo {

    private static final int ROUNDS = 10_000;

    public static void main(String[] args) throws InterruptedException {
        int[] x = new int[ROUNDS];
        int[] y = new int[ROUNDS];
        int[] r1 = new int[ROUNDS];
        int[] r2 = new int[ROUNDS];
        CyclicBarrier start = new CyclicBarrier(2);

        Thread t1 = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();            // both threads begin round i together
                    x[i] = 1;
                    r1[i] = y[i];
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        Thread t2 = new Thread(() -> {
            try {
                for (int i = 0; i < ROUNDS; i++) {
                    start.await();
                    y[i] = 1;
                    r2[i] = x[i];
                }
            } catch (InterruptedException | BrokenBarrierException e) {
                throw new IllegalStateException(e);
            }
        });
        t1.start();
        t2.start();
        t1.join();
        t2.join();

        boolean valuesValid = true;
        for (int i = 0; i < ROUNDS; i++) {
            valuesValid &= (r1[i] == 0 || r1[i] == 1) && (r2[i] == 0 || r2[i] == 1);
        }
        System.out.println("rounds:                " + ROUNDS);
        System.out.println("all values 0 or 1:     " + valuesValid);
        System.out.println("(0,0) outcome:         legal with plain fields, not counted here");
    }
}
