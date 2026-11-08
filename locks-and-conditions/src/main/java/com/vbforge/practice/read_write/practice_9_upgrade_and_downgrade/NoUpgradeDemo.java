package com.vbforge.practice.read_write.practice_9_upgrade_and_downgrade;

import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Read -> write UPGRADE is impossible: writeLock().lock() while holding the read lock would wait for ourselves forever.
 * We use the non-blocking tryLock() so the demo can show the refusal without hanging.
 */
public class NoUpgradeDemo {

    public static void main(String[] args) {
        ReentrantReadWriteLock rw = new ReentrantReadWriteLock();

        rw.readLock().lock();
        try {
            System.out.println("read locks held: " + rw.getReadLockCount());
            boolean upgraded = rw.writeLock().tryLock();
            System.out.println("write tryLock while holding read: " + upgraded);
            // NEVER do rw.writeLock().lock() here: self-deadlock.
        } finally {
            rw.readLock().unlock();
        }

        boolean afterRelease = rw.writeLock().tryLock();
        System.out.println("write tryLock after releasing read: " + afterRelease);
        if (afterRelease) {
            rw.writeLock().unlock();
        }
    }
}
