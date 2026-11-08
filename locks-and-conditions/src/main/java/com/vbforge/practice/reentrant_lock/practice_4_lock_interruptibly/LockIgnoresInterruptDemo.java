package com.vbforge.practice.reentrant_lock.practice_4_lock_interruptibly;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Plain lock() does NOT react to interrupt(): the thread keeps waiting, the interrupt flag stays set.
 */
public class LockIgnoresInterruptDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        lock.lock();

        Thread waiter = new Thread(() -> {
            lock.lock(); // not cancellable
            try {
                System.out.println("waiter: got the lock, interrupt flag still set = "
                        + Thread.currentThread().isInterrupted());
            } finally {
                lock.unlock();
            }
        }, "waiter");
        waiter.start();

        while (!lock.hasQueuedThread(waiter)) {
            Thread.sleep(1);
        }
        waiter.interrupt();
        Thread.sleep(100);
        System.out.println("after interrupt: alive=" + waiter.isAlive()
                + ", still queued=" + lock.hasQueuedThread(waiter));

        lock.unlock();
        waiter.join();
    }
}
