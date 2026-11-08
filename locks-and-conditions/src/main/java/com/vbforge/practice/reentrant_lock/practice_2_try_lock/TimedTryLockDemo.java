package com.vbforge.practice.reentrant_lock.practice_2_try_lock;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 * tryLock(timeout, unit) waits up to the timeout, then gives up.
 * It is also interruptible: InterruptedException must be handled (restore the flag).
 */
public class TimedTryLockDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        CountDownLatch holding = new CountDownLatch(1);
        CountDownLatch release = new CountDownLatch(1);

        Thread holder = new Thread(() -> {
            lock.lock();
            try {
                holding.countDown();
                release.await();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                lock.unlock();
            }
        }, "holder");
        holder.start();
        holding.await();

        boolean first = lock.tryLock(50, TimeUnit.MILLISECONDS);
        System.out.println("while held, tryLock(50ms): " + first);

        release.countDown();
        holder.join();

        boolean second = lock.tryLock(1, TimeUnit.SECONDS);
        System.out.println("after release, tryLock(1s): " + second);
        if (second) {
            lock.unlock();
        }
    }
}
