package com.vbforge.practice.reentrant_lock.practice_6_unlock_pitfalls;

import java.util.concurrent.locks.ReentrantLock;

/** unlock() by a thread that does not own the lock throws IllegalMonitorStateException. */
public class UnlockWithoutLockDemo {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        try {
            lock.unlock();
        } catch (IllegalMonitorStateException e) {
            System.out.println("unlock without lock: IllegalMonitorStateException");
        }
    }
}
