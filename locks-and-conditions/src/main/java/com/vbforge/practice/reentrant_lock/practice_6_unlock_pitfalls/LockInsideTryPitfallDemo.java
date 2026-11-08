package com.vbforge.practice.reentrant_lock.practice_6_unlock_pitfalls;

import java.util.concurrent.locks.ReentrantLock;

/**
 * lock() belongs BEFORE the try block. If acquiring fails (here: interrupted flag + lockInterruptibly),
 * the finally block unlocks a lock we never held, and its IllegalMonitorStateException hides the real problem.
 */
public class LockInsideTryPitfallDemo {

    public static void main(String[] args) {
        ReentrantLock lock = new ReentrantLock();
        Thread.currentThread().interrupt(); // makes lockInterruptibly() fail immediately
        try {
            try {
                lock.lockInterruptibly(); // WRONG place: inside try
            } finally {
                lock.unlock();            // runs although we never acquired the lock
            }
        } catch (IllegalMonitorStateException e) {
            System.out.println("caught IllegalMonitorStateException - the InterruptedException was lost");
        } catch (InterruptedException e) {
            System.out.println("caught InterruptedException");
        }
        System.out.println("interrupt flag cleared: " + !Thread.currentThread().isInterrupted());
    }
}
