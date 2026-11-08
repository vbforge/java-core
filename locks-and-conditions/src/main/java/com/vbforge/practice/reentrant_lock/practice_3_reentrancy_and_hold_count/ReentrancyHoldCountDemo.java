package com.vbforge.practice.reentrant_lock.practice_3_reentrancy_and_hold_count;

import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread that already owns a ReentrantLock may lock it again; each lock() must be matched by an unlock().
 * getHoldCount() shows the nesting depth, isLocked() stays true until the count returns to 0.
 */
public class ReentrancyHoldCountDemo {

    private final ReentrantLock lock = new ReentrantLock();

    void outer() {
        lock.lock();
        try {
            System.out.println("outer: holdCount=" + lock.getHoldCount());
            inner();
            System.out.println("back in outer: holdCount=" + lock.getHoldCount());
        } finally {
            lock.unlock();
        }
    }

    void inner() {
        lock.lock(); // does not block: we already own it
        try {
            System.out.println("inner: holdCount=" + lock.getHoldCount()
                    + ", heldByMe=" + lock.isHeldByCurrentThread());
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ReentrancyHoldCountDemo demo = new ReentrancyHoldCountDemo();
        System.out.println("before: holdCount=" + demo.lock.getHoldCount() + ", locked=" + demo.lock.isLocked());
        demo.outer();
        System.out.println("after:  holdCount=" + demo.lock.getHoldCount() + ", locked=" + demo.lock.isLocked());
    }
}
