package com.vbforge.practice.read_write.practice_7_read_write_cache;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/** While one thread holds the write lock, other threads can neither read nor write. */
public class WriterExcludesReadersDemo {

    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();
        rw.writeLock().lock();
        try {
            Thread other = new Thread(() -> {
                System.out.println("other thread tryLock on read lock:  " + rw.readLock().tryLock());
                System.out.println("other thread tryLock on write lock: " + rw.writeLock().tryLock());
            }, "other");
            other.start();
            other.join();
        } finally {
            rw.writeLock().unlock();
        }

        Thread after = new Thread(() -> {
            boolean got = rw.readLock().tryLock();
            System.out.println("after write unlock, read tryLock: " + got);
            if (got) {
                rw.readLock().unlock();
            }
        }, "after");
        after.start();
        after.join();
    }
}
