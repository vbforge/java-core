package com.vbforge.locks_and_conditions;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.locks.StampedLock;

/**
 * {@link StampedLock} (Java 8) — a lock that hands out <i>stamps</i> and supports
 * <b>optimistic reads</b>.
 *
 * <p>Sections:
 * <ol>
 *   <li>Stamps and the optimistic-read pattern</li>
 *   <li>An optimistic stamp is invalidated by a writer</li>
 *   <li>Not reentrant</li>
 *   <li>Converting a read stamp into a write stamp</li>
 *   <li>A wrong stamp is rejected</li>
 * </ol>
 */
public class StampedLockDemo {

    // =====================================================================
    // 1. Stamps and the optimistic-read pattern
    // =====================================================================

    /**
     * What: {@code StampedLock} methods return a <i>stamp</i> (a {@code long}).
     * You pass that same stamp back to {@code unlockWrite(stamp)},
     * {@code unlockRead(stamp)} or {@code validate(stamp)}.
     * {@code tryOptimisticRead()} returns a stamp <b>without locking anything</b>;
     * you read the fields, then call {@code validate(stamp)} to learn whether a
     * writer slipped in meanwhile.
     *
     * <p>Why it matters: with a read/write lock every reader still updates shared
     * lock state. An optimistic read writes nothing, so it scales best for
     * read-heavy data that is rarely modified.
     *
     * <p>Complexity: O(1); an optimistic read is just two volatile reads.
     *
     * <p>Common gotcha: between {@code tryOptimisticRead()} and {@code validate()}
     * the fields may be inconsistent (a half-finished write). Copy them into
     * <b>local variables</b> first, validate, and only then use the locals.
     * If validation fails, fall back to a real read lock.
     */
    public static class StampedPoint {
        private final StampedLock lock = new StampedLock();
        private double x;
        private double y;

        public void move(double deltaX, double deltaY) {
            long stamp = lock.writeLock();
            try {
                x += deltaX;
                y += deltaY;
            } finally {
                lock.unlockWrite(stamp);
            }
        }

        public double distanceFromOrigin() {
            long stamp = lock.tryOptimisticRead();     // no locking at all
            double currentX = x;
            double currentY = y;
            if (!lock.validate(stamp)) {               // a writer interfered
                stamp = lock.readLock();               // pessimistic fallback
                try {
                    currentX = x;
                    currentY = y;
                } finally {
                    lock.unlockRead(stamp);
                }
            }
            return Math.sqrt(currentX * currentX + currentY * currentY);
        }

        public boolean isLocked() {
            return lock.isReadLocked() || lock.isWriteLocked();
        }
    }

    static void stampedPointDemo() {
        System.out.println("== 1. Stamps and the optimistic-read pattern ==");
        StampedPoint p = new StampedPoint();
        p.move(3, 4);
        System.out.println("distance from origin = " + p.distanceFromOrigin());
        System.out.println("lock held afterwards? " + p.isLocked());
    }

    // =====================================================================
    // 2. An optimistic stamp is invalidated by a writer
    // =====================================================================

    /**
     * What: {@code validate(stamp)} returns {@code true} only if <i>no write lock
     * was acquired</i> since the stamp was issued.
     *
     * <p>Why it matters: this is the safety net that makes optimistic reading
     * correct. Without validation you could act on torn data.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: validation says "no writer ran", not "the value is the
     * same". Even a write that stores the identical value invalidates stamps.
     *
     * @param writeInBetween whether a writer runs between stamp and validation
     * @return the result of {@code validate(stamp)}
     */
    static boolean optimisticStampStillValid(boolean writeInBetween) {
        StampedLock lock = new StampedLock();
        long stamp = lock.tryOptimisticRead();
        if (writeInBetween) {
            long writeStamp = lock.writeLock();
            lock.unlockWrite(writeStamp);
        }
        return lock.validate(stamp);
    }

    static void validateDemo() {
        System.out.println("== 2. Optimistic stamp validation ==");
        System.out.println("no writer  -> valid: " + optimisticStampStillValid(false));
        System.out.println("with writer -> valid: " + optimisticStampStillValid(true));
    }

    // =====================================================================
    // 3. Not reentrant
    // =====================================================================

    /**
     * What: unlike {@code ReentrantLock} and {@code ReentrantReadWriteLock},
     * a {@code StampedLock} does <b>not</b> remember which thread owns it. Asking
     * for the write lock again from the owner is just another contender.
     *
     * <p>Why it matters: code that locks, then calls another method that locks the
     * same {@code StampedLock}, <b>deadlocks itself</b>. Re-entrant call chains
     * must be restructured.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: a blocking {@code writeLock()} here would hang forever.
     * {@code tryWriteLock()} returns {@code 0} when the lock is not available.
     *
     * @return {@code true} if the owner obtained the write lock a second time
     *         (it cannot — the answer is {@code false})
     */
    static boolean secondWriteLockObtained() {
        StampedLock lock = new StampedLock();
        long first = lock.writeLock();
        try {
            long second = lock.tryWriteLock();
            if (second != 0L) {
                lock.unlockWrite(second);
                return true;
            }
            return false;
        } finally {
            lock.unlockWrite(first);
        }
    }

    static void notReentrantDemo() {
        System.out.println("== 3. Not reentrant ==");
        System.out.println("owner got a second write stamp? " + secondWriteLockObtained());
    }

    // =====================================================================
    // 4. Conversion
    // =====================================================================

    /**
     * What: {@code tryConvertToWriteLock(readStamp)} upgrades a read lock to a
     * write lock <i>without</i> releasing it — but only if the caller is the
     * <b>only</b> reader. On failure it returns {@code 0} and the read lock is
     * still held.
     *
     * <p>Why it matters: {@code ReentrantReadWriteLock} cannot upgrade at all.
     * "Read, then maybe write" logic (check a field, update only if needed)
     * becomes possible without releasing and re-checking.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: after a failed conversion you still own the <b>read</b>
     * stamp and must release it with {@code unlockRead}.
     *
     * @param otherReaderPresent whether another thread also holds a read lock
     * @return {@code true} if the conversion succeeded
     */
    static boolean convertReadToWrite(boolean otherReaderPresent) throws InterruptedException {
        StampedLock lock = new StampedLock();
        CountDownLatch held = new CountDownLatch(1);
        CountDownLatch finish = new CountDownLatch(1);
        Thread other = null;
        if (otherReaderPresent) {
            other = new Thread(() -> {
                long stamp = lock.readLock();
                try {
                    held.countDown();
                    finish.await();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    lock.unlockRead(stamp);
                }
            });
            other.start();
            held.await();
        }

        long readStamp = lock.readLock();
        long writeStamp = lock.tryConvertToWriteLock(readStamp);
        boolean converted = writeStamp != 0L;
        if (converted) {
            lock.unlockWrite(writeStamp);
        } else {
            lock.unlockRead(readStamp);
        }

        if (other != null) {
            finish.countDown();
            other.join();
        }
        return converted;
    }

    static void conversionDemo() throws InterruptedException {
        System.out.println("== 4. Conversion read -> write ==");
        System.out.println("sole reader converts: " + convertReadToWrite(false));
        System.out.println("shared reader converts: " + convertReadToWrite(true));
    }

    // =====================================================================
    // 5. Wrong stamp
    // =====================================================================

    /**
     * What: stamps identify one acquisition. Releasing with a stamp that does not
     * match the current lock state throws {@link IllegalMonitorStateException}.
     *
     * <p>Why it matters: because the lock does not track owners, the stamp is the
     * only proof you hold it.
     *
     * <p>Complexity: O(1).
     *
     * <p>Common gotcha: reusing one {@code long stamp} variable for several locks
     * and unlocking with the wrong one.
     *
     * @return {@code true} if releasing with a bogus stamp was rejected
     */
    static boolean wrongStampRejected() {
        StampedLock lock = new StampedLock();
        long stamp = lock.writeLock();
        try {
            lock.unlockWrite(stamp + 1);
            return false;
        } catch (IllegalMonitorStateException expected) {
            return true;
        } finally {
            lock.unlockWrite(stamp);
        }
    }

    static void wrongStampDemo() {
        System.out.println("== 5. Wrong stamp ==");
        System.out.println("bogus stamp rejected? " + wrongStampRejected());
    }

    public static void main(String[] args) throws InterruptedException {
        stampedPointDemo();
        System.out.println("---");
        validateDemo();
        System.out.println("---");
        notReentrantDemo();
        System.out.println("---");
        conversionDemo();
        System.out.println("---");
        wrongStampDemo();
    }
}
