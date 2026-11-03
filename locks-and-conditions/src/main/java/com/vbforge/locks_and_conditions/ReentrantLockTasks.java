package com.vbforge.locks_and_conditions;

import java.util.concurrent.locks.ReentrantLock;

/**
 * 🎯 Tasks for {@link ReentrantLockDemo}.
 *
 * <p>Implement every method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code ReentrantLockTasksTest}.
 */
public class ReentrantLockTasks {

    /**
     * Start {@code threads} threads; each one increments a shared counter
     * {@code incrementsPerThread} times. Protect the counter with a
     * {@link ReentrantLock} and return the final value after every thread finished.
     *
     * <p>Examples:
     * <pre>
     * incrementConcurrently(4, 1000)  → 4000
     * incrementConcurrently(1, 5)     → 5
     * incrementConcurrently(3, 0)     → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A plain {@code int} field is not enough — a lock must guard it</li>
     *   <li>Use the lock / try / finally / unlock pattern</li>
     *   <li>Wait for the threads with {@code Thread.join()}</li>
     * </ul>
     *
     * @param threads             number of threads to start (≥ 0)
     * @param incrementsPerThread increments done by each thread (≥ 0)
     * @return the final counter value
     * @throws InterruptedException if interrupted while waiting for the threads
     */
    public int incrementConcurrently(int threads, int incrementsPerThread) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement incrementConcurrently");
    }

    /**
     * Run {@code action} only if {@code lock} is free right now. Never wait.
     * Return {@code true} if the action ran and {@code false} if the lock was busy.
     * If the lock was acquired it must be released again, even if the action throws.
     *
     * <p>Examples:
     * <pre>
     * free lock,  action sets a flag  → true  (flag is set, lock is free afterwards)
     * lock held by another thread     → false (action is NOT run)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Look at the non-blocking acquisition method on {@code ReentrantLock}</li>
     *   <li>Only unlock if you actually obtained the lock</li>
     * </ul>
     *
     * @param lock   the lock to try
     * @param action the work to run while holding the lock
     * @return {@code true} if the action ran
     */
    public boolean runIfFree(ReentrantLock lock, Runnable action) {
        throw new UnsupportedOperationException("TODO: implement runIfFree");
    }

    /**
     * Acquire {@code lock} reentrantly {@code depth} times, read its hold count
     * while all acquisitions are active, then release every acquisition.
     * Return the hold count you read. The lock must be completely free afterwards.
     *
     * <p>Examples:
     * <pre>
     * nestedHoldCount(lock, 3)  → 3   (lock.isLocked() == false afterwards)
     * nestedHoldCount(lock, 1)  → 1
     * nestedHoldCount(lock, 0)  → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A loop that locks and a loop that unlocks is enough</li>
     *   <li>Ask the lock itself how many times the current thread holds it</li>
     *   <li>Make sure the release loop also runs if something goes wrong</li>
     * </ul>
     *
     * @param lock  the lock to acquire (not held by the caller on entry)
     * @param depth how many times to acquire it (≥ 0)
     * @return the hold count observed after the last acquisition
     */
    public int nestedHoldCount(ReentrantLock lock, int depth) {
        throw new UnsupportedOperationException("TODO: implement nestedHoldCount");
    }

    /**
     * Try to obtain {@code lock} within {@code timeoutMillis}. If you get it,
     * release it immediately and return {@code true}. If the time runs out,
     * return {@code false}. If the thread is interrupted while waiting, restore
     * the interrupt flag and return {@code false}.
     *
     * <p>Examples:
     * <pre>
     * free lock, 100 ms                    → true
     * lock held by another thread, 30 ms   → false (after about 30 ms)
     * thread interrupted while waiting     → false, Thread.currentThread().isInterrupted() == true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a {@code tryLock} overload that takes a time and a {@code TimeUnit}</li>
     *   <li>It declares {@code InterruptedException} — do not just swallow it</li>
     * </ul>
     *
     * @param lock          the lock to acquire
     * @param timeoutMillis maximum wait in milliseconds
     * @return {@code true} if the lock could be acquired in time
     */
    public boolean acquireWithTimeout(ReentrantLock lock, long timeoutMillis) {
        throw new UnsupportedOperationException("TODO: implement acquireWithTimeout");
    }
}
