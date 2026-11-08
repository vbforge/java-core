package com.vbforge.practice.condition.practice_15_condition_requires_lock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A Condition belongs to its lock: await(), signal() and signalAll() may only be called while holding that lock,
 * otherwise IllegalMonitorStateException (the same rule as wait/notify inside synchronized).
 */
public class AwaitWithoutLockDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();

        try {
            condition.signal();
        } catch (IllegalMonitorStateException e) {
            System.out.println("signal() without the lock: IllegalMonitorStateException");
        }
        try {
            condition.await();
        } catch (IllegalMonitorStateException e) {
            System.out.println("await() without the lock:  IllegalMonitorStateException");
        }

        lock.lock();
        try {
            condition.signal();
            System.out.println("signal() with the lock: fine (nobody waiting, nothing happens)");
        } finally {
            lock.unlock();
        }
    }
}
