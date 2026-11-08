package com.vbforge.practice.condition.practice_13_condition_timed_await;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Timed wait: awaitNanos(n) returns the REMAINING time, so a predicate loop can keep waiting
 * after a spurious wakeup without extending the total timeout.
 */
public class AwaitFlagDemo {

    static class Flag {
        private final ReentrantLock lock = new ReentrantLock();
        private final Condition raised = lock.newCondition();
        private boolean up = false;

        void raise() {
            lock.lock();
            try {
                up = true;
                raised.signalAll();
            } finally {
                lock.unlock();
            }
        }

        /** @return true if the flag went up within the timeout, false if the time ran out */
        boolean awaitUp(long timeout, TimeUnit unit) throws InterruptedException {
            long nanos = unit.toNanos(timeout);
            lock.lock();
            try {
                while (!up) {
                    if (nanos <= 0L) {
                        return false;
                    }
                    nanos = raised.awaitNanos(nanos);
                }
                return true;
            } finally {
                lock.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Flag flag = new Flag();
        System.out.println("nobody raises it, awaitUp(50ms): " + flag.awaitUp(50, TimeUnit.MILLISECONDS));

        Thread raiser = new Thread(() -> {
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            flag.raise();
        }, "raiser");
        raiser.start();
        System.out.println("raised after ~50ms, awaitUp(5s): " + flag.awaitUp(5, TimeUnit.SECONDS));
        raiser.join();
    }
}
