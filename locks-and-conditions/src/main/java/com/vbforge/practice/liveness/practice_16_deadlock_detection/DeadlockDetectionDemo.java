package com.vbforge.practice.liveness.practice_16_deadlock_detection;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A real deadlock (opposite lock order) that is detected with ThreadMXBean.findDeadlockedThreads()
 * and then broken on purpose: the second lock is taken with lockInterruptibly(), so interrupt() can cancel the wait.
 * The barrier makes sure each thread already owns its first lock, so the deadlock always happens.
 * (A deadlock is "frozen, no CPU, visible to the MXBean" - a livelock would be invisible to it.)
 */
public class DeadlockDetectionDemo {

    private static Thread worker(String name, ReentrantLock first, ReentrantLock second, CyclicBarrier barrier,
                                 ConcurrentLinkedQueue<String> gaveUp) {
        return new Thread(() -> {
            first.lock();
            try {
                barrier.await();
                second.lockInterruptibly(); // blocks forever: the other thread owns it
                second.unlock();
            } catch (InterruptedException e) {
                gaveUp.add(Thread.currentThread().getName()); // printed after join, so the output order is stable
            } catch (BrokenBarrierException e) {
                System.out.println(Thread.currentThread().getName() + ": barrier broken");
            } finally {
                first.unlock();
            }
        }, name);
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantLock lockA = new ReentrantLock();
        ReentrantLock lockB = new ReentrantLock();
        CyclicBarrier barrier = new CyclicBarrier(2);
        ConcurrentLinkedQueue<String> gaveUp = new ConcurrentLinkedQueue<>();

        Thread w1 = worker("worker-1", lockA, lockB, barrier, gaveUp); // A then B
        Thread w2 = worker("worker-2", lockB, lockA, barrier, gaveUp); // B then A
        w1.start();
        w2.start();

        ThreadMXBean mx = ManagementFactory.getThreadMXBean();
        long[] ids = null;
        long deadline = System.currentTimeMillis() + 5_000;
        while (ids == null && System.currentTimeMillis() < deadline) {
            ids = mx.findDeadlockedThreads();
            if (ids == null) {
                Thread.sleep(10);
            }
        }

        if (ids == null) {
            System.out.println("no deadlock detected (unexpected)");
        } else {
            List<String> names = new ArrayList<>();
            for (ThreadInfo info : mx.getThreadInfo(ids)) {
                names.add(info.getThreadName());
            }
            Collections.sort(names);
            System.out.println("deadlocked threads: " + names);
        }

        // break the deadlock so the program can end
        w1.interrupt();
        w2.interrupt();
        w1.join();
        w2.join();
        List<String> aborted = new ArrayList<>(gaveUp);
        Collections.sort(aborted);
        System.out.println("threads that gave up after interrupt: " + aborted);
        System.out.println("deadlock after interrupt: " + (mx.findDeadlockedThreads() != null));
    }
}
