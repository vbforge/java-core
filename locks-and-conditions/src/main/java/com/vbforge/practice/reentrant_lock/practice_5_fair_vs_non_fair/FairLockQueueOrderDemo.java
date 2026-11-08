package com.vbforge.practice.reentrant_lock.practice_5_fair_vs_non_fair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A fair lock hands the lock to waiting threads in FIFO order.
 * The threads are queued one by one (we wait until each is really queued), so the order is deterministic.
 */
public class FairLockQueueOrderDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lock = new ReentrantLock(true);
        List<String> acquisitionOrder = Collections.synchronizedList(new ArrayList<>());

        lock.lock(); // main holds the lock while the workers queue up
        List<Thread> workers = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            final String name = "T" + i;
            Thread t = new Thread(() -> {
                lock.lock();
                try {
                    acquisitionOrder.add(name);
                } finally {
                    lock.unlock();
                }
            }, name);
            workers.add(t);
            t.start();
            while (lock.getQueueLength() < i) {
                Thread.sleep(1);
            }
        }
        System.out.println("isFair=" + lock.isFair() + ", queue length=" + lock.getQueueLength());

        lock.unlock();
        for (Thread t : workers) t.join();
        System.out.println("acquisition order: " + acquisitionOrder);
    }
}
