package com.vbforge.multithreading_basics;

/**
 * Tasks for synchronization. Implement every method so that
 * {@code SynchronizationTasksTest} goes green (remove {@code @Disabled} per task).
 *
 * All methods may be called concurrently from many threads.
 */
public class SynchronizationTasks {

    /**
     * Adds 1 to {@code counter[0]}, {@code times} times. Must be correct when several
     * threads call it at the same time on the SAME array.
     *
     * <pre>
     * Examples:
     *   4 threads each call incrementSafely(c, 10_000) on the same c = {0}  → c[0] == 40_000
     *   incrementSafely(c, 0)                                               → c[0] unchanged
     * </pre>
     *
     * Hints:
     * - Every caller must lock the same object — what do all callers already share?
     * - The lock must cover the whole read-modify-write
     *
     * @param counter a one-element array shared by all callers
     * @param times   how many increments to perform
     */
    public void incrementSafely(int[] counter, int times) {
        throw new UnsupportedOperationException("TODO: implement incrementSafely");
    }

    /**
     * Moves {@code amount} from {@code accounts[from]} to {@code accounts[to]} atomically
     * with respect to other callers working on the same array.
     *
     * <pre>
     * Examples:
     *   accounts = {1000, 1000}; transfer(accounts, 0, 1, 100) → {900, 1100}
     *   two threads transferring in opposite directions 20_000 times each → total stays 2000
     * </pre>
     *
     * Hints:
     * - Both balances change inside one critical section
     * - One lock for the whole array is enough here
     *
     * @param accounts shared balances
     * @param from     source index
     * @param to       target index
     * @param amount   amount to move (no overdraft check is needed)
     */
    public void transfer(long[] accounts, int from, int to, long amount) {
        throw new UnsupportedOperationException("TODO: implement transfer");
    }

    /**
     * Stores {@code value} in {@code box[0]} and wakes up threads that wait for it.
     *
     * <pre>
     * Examples:
     *   box = {null}; putValue(box, "A") → box[0] == "A", sleeping takers wake up
     * </pre>
     *
     * Hints:
     * - Waiters and the notifier must use the same monitor, and the notifier must own it
     * - Notifying before the value is stored is pointless
     *
     * @param box   a one-element array used as a shared mailbox
     * @param value the value to publish
     */
    public void putValue(Object[] box, Object value) {
        throw new UnsupportedOperationException("TODO: implement putValue");
    }

    /**
     * Blocks until {@code box[0]} is not null, then returns that value and resets
     * {@code box[0]} to null. Returns immediately if a value is already there.
     *
     * <pre>
     * Examples:
     *   box = {"A"}; takeValue(box)  → "A", box[0] == null
     *   box = {null}; takeValue(box) → blocks until another thread calls putValue(box, "B"), then "B"
     * </pre>
     *
     * Hints:
     * - Re-check the condition after every wake-up
     * - Waiting gives the monitor up; that is what lets the producer in
     *
     * @param box a one-element array used as a shared mailbox
     * @return the taken value
     * @throws InterruptedException if interrupted while waiting
     */
    public Object takeValue(Object[] box) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement takeValue");
    }

    /**
     * Like {@link #transfer} but with one lock PER account. Two threads transferring in
     * opposite directions (0→1 and 1→0) must never deadlock.
     *
     * <pre>
     * Examples:
     *   accounts = {1000, 1000}, locks = {new Object(), new Object()}
     *   transferOrdered(accounts, locks, 0, 1, 100) → {900, 1100}
     *   thread A: 0→1 and thread B: 1→0, 20_000 times each → both finish, total stays 2000
     * </pre>
     *
     * Hints:
     * - Deadlock needs two threads taking the same two locks in opposite orders
     * - Give every thread the same, index-based rule for which lock comes first
     *
     * @param accounts shared balances
     * @param locks    one lock object per account, same length as accounts
     * @param from     source index (different from to)
     * @param to       target index
     * @param amount   amount to move
     */
    public void transferOrdered(long[] accounts, Object[] locks, int from, int to, long amount) {
        throw new UnsupportedOperationException("TODO: implement transferOrdered");
    }
}
