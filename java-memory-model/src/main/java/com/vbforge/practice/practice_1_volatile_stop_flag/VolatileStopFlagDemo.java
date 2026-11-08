package com.vbforge.practice.practice_1_volatile_stop_flag;

import java.util.concurrent.CountDownLatch;

/**
 * A stop flag the worker is guaranteed to see.
 *
 * <p>With a plain {@code boolean} this loop is a data race: the JIT may hoist the read of
 * {@code stop} out of the loop and the worker may never terminate. That version is NOT run
 * here (it can hang). The {@code volatile} flag adds a happens-before edge from the write in
 * {@code main} to every later read in the worker.
 */
public class VolatileStopFlagDemo {

    private static volatile boolean stop;

    public static void main(String[] args) throws InterruptedException {
        CountDownLatch started = new CountDownLatch(1);

        Thread worker = new Thread(() -> {
            started.countDown();
            long spins = 0;
            while (!stop) {
                spins++;           // "work"
            }
        }, "worker");
        worker.setDaemon(true);    // never keeps the JVM alive, even if something goes wrong
        worker.start();

        started.await();           // the worker is really running
        stop = true;               // volatile write: the worker is guaranteed to see it
        worker.join(5_000);

        System.out.println("worker stopped: " + !worker.isAlive());
    }
}
