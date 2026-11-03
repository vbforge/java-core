package com.vbforge.locks_and_conditions;

import java.util.concurrent.locks.StampedLock;

/**
 * 🎯 Tasks for {@link StampedLockDemo}.
 *
 * <p>Implement every method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code StampedLockTasksTest}.
 */
public class StampedLockTasks {

    /**
     * Overwrite both coordinates of {@code point} ({@code point[0] = x},
     * {@code point[1] = y}) while holding the <b>write</b> lock of {@code lock}.
     *
     * <p>Examples:
     * <pre>
     * point = {0, 0}   writePoint(lock, point, 3, 4) → point is {3, 4}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code writeLock()} returns a stamp — keep it</li>
     *   <li>Give the same stamp back to the matching unlock method, in {@code finally}</li>
     * </ul>
     *
     * @param lock  the lock guarding {@code point}
     * @param point a two-element array {x, y}
     * @param x     new x
     * @param y     new y
     */
    public void writePoint(StampedLock lock, double[] point, double x, double y) {
        throw new UnsupportedOperationException("TODO: implement writePoint");
    }

    /**
     * Return a consistent copy {@code {x, y}} of {@code point} using an
     * <b>optimistic read</b>. If a writer interfered, fall back to a real read lock.
     * The lock must not be left read-locked.
     *
     * <p>Examples:
     * <pre>
     * point = {3, 4}   snapshotOptimistic(lock, point) → {3, 4}   (a new array)
     * a writer that always stores x == y concurrently → result always has result[0] == result[1]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Copy the fields into locals <b>before</b> validating</li>
     *   <li>There is a method that tells you whether a write happened since the stamp</li>
     *   <li>The fallback uses the pessimistic read lock and its own stamp</li>
     * </ul>
     *
     * @param lock  the lock guarding {@code point}
     * @param point a two-element array {x, y}
     * @return a new array holding a consistent {x, y}
     */
    public double[] snapshotOptimistic(StampedLock lock, double[] point) {
        throw new UnsupportedOperationException("TODO: implement snapshotOptimistic");
    }

    /**
     * Take an optimistic stamp from {@code lock}. If {@code writeInBetween} is
     * {@code true}, acquire and release the write lock once (changing nothing).
     * Then return whether the stamp is still valid.
     *
     * <p>Examples:
     * <pre>
     * isStampStillValid(lock, false) → true
     * isStampStillValid(lock, true)  → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>An optimistic stamp locks nothing, so there is nothing to unlock for it</li>
     *   <li>Even an empty write invalidates earlier optimistic stamps</li>
     * </ul>
     *
     * @param lock           a free lock
     * @param writeInBetween whether a write cycle happens between stamp and check
     * @return {@code true} if no write happened since the stamp was issued
     */
    public boolean isStampStillValid(StampedLock lock, boolean writeInBetween) {
        throw new UnsupportedOperationException("TODO: implement isStampStillValid");
    }

    /**
     * Acquire the <b>read</b> lock of {@code lock}, then try to convert it to the
     * write lock without releasing it. Return {@code true} if the conversion
     * worked. Whatever you end up holding must be released before returning.
     *
     * <p>Examples:
     * <pre>
     * free lock, caller is the only reader      → true
     * another thread also holds a read lock     → false (read lock is still released by you)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Look for a {@code tryConvertTo...} method on {@code StampedLock}</li>
     *   <li>A return value of {@code 0} means failure — then your read stamp is still valid</li>
     *   <li>Success returns a <b>new</b> write stamp — release that one</li>
     * </ul>
     *
     * @param lock the lock to experiment with
     * @return {@code true} if the read lock could be upgraded
     */
    public boolean upgradeSoleReader(StampedLock lock) {
        throw new UnsupportedOperationException("TODO: implement upgradeSoleReader");
    }

    /**
     * Hold the write lock of {@code lock} and, without blocking, try to acquire
     * the write lock <b>again</b> from the same thread. Return whether the second
     * acquisition worked. Release everything you acquired before returning.
     *
     * <p>Examples:
     * <pre>
     * writeLockIsReentrant(new StampedLock()) → false
     * (lock is completely free afterwards)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A non-blocking acquisition returns {@code 0} when it fails</li>
     *   <li>Release each stamp you really obtained, exactly once</li>
     * </ul>
     *
     * @param lock a free lock
     * @return {@code true} if the second write acquisition succeeded
     */
    public boolean writeLockIsReentrant(StampedLock lock) {
        throw new UnsupportedOperationException("TODO: implement writeLockIsReentrant");
    }
}
