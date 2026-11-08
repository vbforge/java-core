package com.vbforge.practice.reentrant_lock.practice_4_lock_interruptibly;

import java.util.concurrent.locks.ReentrantLock;

/**
 * lockInterruptibly(): a thread waiting for the lock can be cancelled with interrupt().
 */
public class LockInterruptiblyDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock(); // main holds the lock so the waiter must queue

        Thread waiter = new Thread(() -> {
            try {
                lock.lockInterruptibly();
                try {
                    System.out.println("waiter: got the lock (should not happen)");
                } finally {
                    lock.unlock();
                }
            } catch (InterruptedException e) {
                System.out.println("waiter: interrupted while waiting, gave up");
            }
        }, "waiter");
        waiter.start();

        while (!lock.hasQueuedThread(waiter)) {
            Thread.sleep(1);
        }
        waiter.interrupt();
        waiter.join();

        System.out.println("waiter still queued: " + lock.hasQueuedThread(waiter));
        lock.unlock();
    }
}
