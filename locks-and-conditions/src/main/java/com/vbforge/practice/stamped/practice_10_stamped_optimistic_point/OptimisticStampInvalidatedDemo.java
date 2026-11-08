package com.vbforge.practice.stamped.practice_10_stamped_optimistic_point;

import java.util.concurrent.locks.StampedLock;

/** An optimistic stamp is only a version number: any write lock acquisition after it makes validate() fail. */
public class OptimisticStampInvalidatedDemo {

    public static void main(String[] args) {
        StampedLock lock = new StampedLock();

        long stamp = lock.tryOptimisticRead();
        System.out.println("fresh stamp valid: " + lock.validate(stamp));

        long w = lock.writeLock();
        lock.unlockWrite(w);
        System.out.println("after a write, the old stamp valid: " + lock.validate(stamp));

        long newStamp = lock.tryOptimisticRead();
        System.out.println("new stamp valid: " + lock.validate(newStamp));

        long r = lock.readLock();
        try {
            long whileReading = lock.tryOptimisticRead();
            System.out.println("optimistic stamp while a reader is inside, valid: " + lock.validate(whileReading));
        } finally {
            lock.unlockRead(r);
        }

        long w2 = lock.writeLock();
        try {
            long whileWriting = lock.tryOptimisticRead();
            System.out.println("tryOptimisticRead while write-locked returns 0: " + (whileWriting == 0));
        } finally {
            lock.unlockWrite(w2);
        }
    }
}
