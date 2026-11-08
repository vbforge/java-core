package com.vbforge.practice.read_write.practice_9_upgrade_and_downgrade;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Write -> read DOWNGRADE is allowed: take the read lock while still holding the write lock,
 * then release the write lock. The data cannot change between the two steps.
 */
public class DowngradeDemo {

    private static boolean otherThreadCanWrite(ReentrantReadWriteLock rw) throws InterruptedException {
        boolean[] result = new boolean[1];
        Thread t = new Thread(() -> {
            result[0] = rw.writeLock().tryLock();
            if (result[0]) {
                rw.writeLock().unlock();
            }
        });
        t.start();
        t.join();
        return result[0];
    }

    public static void main(String[] args) throws InterruptedException {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

        rw.writeLock().lock();
        try {
            System.out.println("1. updated data under the write lock");
            rw.readLock().lock();                 // acquire read BEFORE releasing write
        } finally {
            rw.writeLock().unlock();              // downgrade complete
        }

        try {
            System.out.println("2. write lock held: " + rw.isWriteLocked() + ", read locks: " + rw.getReadLockCount());
            System.out.println("3. another thread can write now: " + otherThreadCanWrite(rw));
        } finally {
            rw.readLock().unlock();
        }
        System.out.println("4. after releasing read, another thread can write: " + otherThreadCanWrite(rw));
    }
}
