package com.vbforge.practice.stamped.practice_11_stamped_conversion_and_reentrancy;

import java.util.concurrent.locks.StampedLock;

/**
 * StampedLock is NOT reentrant: the owner of the write lock cannot take it (or a read lock) again.
 * writeLock() would block forever; the non-blocking tryWriteLock()/tryReadLock() return 0 instead.
 */
public class NotReentrantDemo {

    public static void main(String[] args) {
        StampedLock lock = new StampedLock();

        long stamp = lock.writeLock();
        try {
            long again = lock.tryWriteLock();
            System.out.println("second write lock by the owner: " + (again != 0 ? "acquired" : "refused (0)"));
            long read = lock.tryReadLock();
            System.out.println("read lock by the write-lock owner: " + (read != 0 ? "acquired" : "refused (0)"));
            // lock.writeLock() here would be a self-deadlock
        } finally {
            lock.unlockWrite(stamp);
        }
        System.out.println("write locked after unlock: " + lock.isWriteLocked());
    }
}
