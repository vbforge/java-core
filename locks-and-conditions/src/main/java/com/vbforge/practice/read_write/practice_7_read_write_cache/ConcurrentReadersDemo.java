package com.vbforge.practice.read_write.practice_7_read_write_cache;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Proof that read locks are shared: two threads wait for each other on a barrier while BOTH hold the read lock.
 * With an exclusive lock the second thread could never enter and the barrier would time out.
 */
public class ConcurrentReadersDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        CyclicBarrier barrier = new CyclicBarrier(2);
        AtomicInteger meetings = new AtomicInteger();

        Runnable reader = () -> {
            rw.readLock().lock();
            try {
                barrier.await(2, TimeUnit.SECONDS); // only passes if both readers are inside
                meetings.incrementAndGet();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (BrokenBarrierException | TimeoutException e) {
                System.out.println(Thread.currentThread().getName() + ": readers did NOT overlap");
            } finally {
                rw.readLock().unlock();
            }
        };

        Thread r1 = new Thread(reader, "reader-1");
        Thread r2 = new Thread(reader, "reader-2");
        r1.start();
        r2.start();
        r1.join();
        r2.join();

        System.out.println("readers that met inside the read lock: " + meetings.get());
    }
}
