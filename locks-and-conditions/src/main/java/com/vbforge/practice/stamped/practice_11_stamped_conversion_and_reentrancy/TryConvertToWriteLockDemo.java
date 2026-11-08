package com.vbforge.practice.stamped.practice_11_stamped_conversion_and_reentrancy;

import java.util.concurrent.locks.StampedLock;

/**
 * tryConvertToWriteLock(readStamp) upgrades a read lock to a write lock - but only if the caller is the SOLE reader.
 * It returns 0 when the upgrade is impossible (then release the read lock and take the write lock the normal way).
 */
public class TryConvertToWriteLockDemo {

    public static void main(String[] args) {
        StampedLock lock = new StampedLock();

        // case 1: sole reader
        long read = lock.readLock();
        long write = lock.tryConvertToWriteLock(read);
        System.out.println("sole reader, converted: " + (write != 0) + ", write locked: " + lock.isWriteLocked());
        if (write != 0) {
            lock.unlockWrite(write);
        } else {
            lock.unlockRead(read);
        }

        // case 2: two readers hold the lock
        long first = lock.readLock();
        long second = lock.readLock();
        long failed = lock.tryConvertToWriteLock(first);
        System.out.println("two readers, converted: " + (failed != 0) + ", read locks: " + lock.getReadLockCount());
        lock.unlockRead(first);
        lock.unlockRead(second);
    }
}
