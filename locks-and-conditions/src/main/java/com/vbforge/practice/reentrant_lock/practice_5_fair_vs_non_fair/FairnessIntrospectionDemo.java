package com.vbforge.practice.reentrant_lock.practice_5_fair_vs_non_fair;

import java.util.concurrent.locks.ReentrantLock;

/**
 * The default ReentrantLock is NON-fair (higher throughput, a waiting thread may starve).
 * new ReentrantLock(true) is fair (FIFO, lower throughput). Note: the untimed tryLock() barges even on a fair lock.
 */
public class FairnessIntrospectionDemo {

    public static void main(String[] args) {
        ReentrantLock normal = new ReentrantLock();
        ReentrantLock fair = new ReentrantLock(true);

        System.out.println("default lock isFair: " + normal.isFair());
        System.out.println("fair lock    isFair: " + fair.isFair());
        System.out.println("hasQueuedThreads (nobody waits): " + fair.hasQueuedThreads());
        System.out.println("queue length: " + fair.getQueueLength());
    }
}
