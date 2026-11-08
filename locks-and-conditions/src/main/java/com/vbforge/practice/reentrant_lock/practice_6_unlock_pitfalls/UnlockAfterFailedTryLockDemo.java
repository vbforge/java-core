package com.vbforge.practice.reentrant_lock.practice_6_unlock_pitfalls;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.ReentrantLock;

/** Wrong: tryLock() then unlock() regardless of the result. Right: unlock only inside if (tryLock()). */
public class UnlockAfterFailedTryLockDemo {

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
        });
        holder.start();
        holding.await();

        boolean got = lock.tryLock();
        System.out.println("tryLock: " + got);
        try {
            lock.unlock(); // WRONG: we did not get the lock
        } catch (IllegalMonitorStateException e) {
            System.out.println("unlock after failed tryLock: IllegalMonitorStateException");
        }

        release.countDown();
        holder.join();
    }
}
