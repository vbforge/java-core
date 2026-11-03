package com.vbforge.locks_and_conditions;

import com.vbforge.locks_and_conditions.LivenessDemo.Account;

import java.util.Set;

/**
 * 🎯 Tasks for {@link LivenessDemo}.
 *
 * <p>Implement every method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code LivenessTasksTest}.
 */
public class LivenessTasks {

    /**
     * Move {@code amount} from {@code from} to {@code to} atomically, so that two
     * threads transferring in <b>opposite directions</b> can never deadlock.
     * Return {@code false} (and change nothing) if {@code from} has less than
     * {@code amount}. Both locks must be released when you return.
     *
     * <p>Examples:
     * <pre>
     * from=100, to=50, amount=30  → true   (from=70, to=80)
     * from=10,  to=50, amount=30  → false  (balances unchanged)
     * thread 1: transfer(a, b, 1) x 10000  and  thread 2: transfer(b, a, 1) x 10000
     *                              → both finish, a + b is unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code account.getLock()} and the {@code ...Unlocked} helpers on {@link Account}</li>
     *   <li>Decide the locking order by something both threads agree on — not by
     *       which argument is {@code from}</li>
     *   <li>Check the balance only after you hold both locks</li>
     * </ul>
     *
     * @param from   the source account
     * @param to     the target account (different from {@code from})
     * @param amount the amount to move (≥ 0)
     * @return {@code true} if the money was moved
     */
    public boolean transferOrdered(Account from, Account to, int amount) {
        throw new UnsupportedOperationException("TODO: implement transferOrdered");
    }

    /**
     * Same transfer, but without any global lock order: try to lock {@code from},
     * then {@code to}, waiting at most {@code timeoutMillis} for <b>each</b> lock.
     * If either lock cannot be obtained in time, release whatever you hold and
     * return {@code false} without changing anything. Also return {@code false} if
     * {@code from} has insufficient funds.
     *
     * <p>Examples:
     * <pre>
     * both free, enough money                 → true
     * 'to' locked by another thread, 30 ms    → false (and 'from' is NOT left locked)
     * 'from' locked by another thread, 30 ms  → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a timed {@code tryLock} on {@code ReentrantLock}</li>
     *   <li>Unlock only the locks you actually obtained</li>
     *   <li>Nested try/finally keeps the release logic simple</li>
     * </ul>
     *
     * @param from          the source account
     * @param to            the target account
     * @param amount        the amount to move (≥ 0)
     * @param timeoutMillis maximum wait per lock in milliseconds
     * @return {@code true} if the money was moved
     * @throws InterruptedException if interrupted while waiting for a lock
     */
    public boolean transferWithTimeout(Account from, Account to, int amount, long timeoutMillis)
            throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement transferWithTimeout");
    }

    /**
     * Ask the JVM which threads are currently deadlocked and return their ids.
     * Return an empty set when there is no deadlock.
     *
     * <p>Examples:
     * <pre>
     * no deadlock                          → {}
     * two threads locking A,B and B,A      → {id of thread 1, id of thread 2}
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Look in {@code java.lang.management}</li>
     *   <li>The method that also sees {@code ReentrantLock} cycles is not the one
     *       that only covers {@code synchronized}</li>
     *   <li>It may return {@code null} when nothing is deadlocked</li>
     * </ul>
     *
     * @return ids of deadlocked threads, never {@code null}
     */
    public Set<Long> findDeadlockedThreadIds() {
        throw new UnsupportedOperationException("TODO: implement findDeadlockedThreadIds");
    }

    /**
     * Exponential back-off (no jitter): the delay before retry number
     * {@code attempt} is {@code baseMillis * 2^attempt}, but never more than
     * {@code capMillis}. Attempts start at 0. It must not overflow for large attempts.
     *
     * <p>Examples:
     * <pre>
     * backoffDelayMillis(0, 10, 1000)   → 10
     * backoffDelayMillis(1, 10, 1000)   → 20
     * backoffDelayMillis(3, 10, 1000)   → 80
     * backoffDelayMillis(10, 10, 1000)  → 1000   (capped)
     * backoffDelayMillis(200, 10, 1000) → 1000   (no overflow)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code long} shift by 63 or more does not do what you want — stop early</li>
     *   <li>Compare against the cap before multiplying beyond it</li>
     * </ul>
     *
     * @param attempt    zero-based retry number (≥ 0)
     * @param baseMillis delay for attempt 0 (&gt; 0)
     * @param capMillis  upper bound for the delay (≥ baseMillis)
     * @return the delay in milliseconds, between {@code baseMillis} and {@code capMillis}
     */
    public long backoffDelayMillis(int attempt, long baseMillis, long capMillis) {
        throw new UnsupportedOperationException("TODO: implement backoffDelayMillis");
    }
}
