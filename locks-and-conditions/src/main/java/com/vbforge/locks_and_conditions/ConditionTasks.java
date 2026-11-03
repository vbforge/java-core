package com.vbforge.locks_and_conditions;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 🎯 Tasks for {@link ConditionDemo}.
 *
 * <p>Implement every method, then remove {@code @Disabled} from the matching
 * {@code @Nested} class in {@code ConditionTasksTest}.
 */
public class ConditionTasks {

    /**
     * A blocking, one-slot mailbox. {@code put} blocks while the slot is full,
     * {@code take} blocks while it is empty. Use <b>one lock and two conditions</b>.
     *
     * <p>Examples:
     * <pre>
     * box.put("a"); box.take()           → "a"
     * box.take() (empty)                 → blocks until another thread calls put
     * box.put("a"); box.put("b") (full)  → second put blocks until someone takes
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Create the conditions with {@code lock.newCondition()}</li>
     *   <li>Re-check the predicate in a loop around {@code await()}</li>
     *   <li>After changing the slot, signal the condition the <i>other</i> side waits on</li>
     * </ul>
     *
     * @param <T> element type
     */
    public static class Mailbox<T> {

        /**
         * Store {@code item}; wait while the slot is occupied.
         *
         * @param item the value to store
         * @throws InterruptedException if interrupted while waiting
         */
        public void put(T item) throws InterruptedException {
            throw new UnsupportedOperationException("TODO: implement Mailbox.put");
        }

        /**
         * Remove and return the stored value; wait while the slot is empty.
         *
         * @return the stored value
         * @throws InterruptedException if interrupted while waiting
         */
        public T take() throws InterruptedException {
            throw new UnsupportedOperationException("TODO: implement Mailbox.take");
        }
    }

    /**
     * Wait on {@code condition} until {@code ready} becomes {@code true}, but give
     * up after {@code timeoutMillis}. The caller already holds {@code lock}. Return
     * {@code true} if the flag became {@code true} in time and {@code false} if the
     * time ran out first. The lock must still be held by the caller when you return.
     *
     * <p>Examples:
     * <pre>
     * flag already true                         → true  (returns at once)
     * flag set by another thread after 20 ms,
     *   timeout 5000 ms                         → true
     * flag never set, timeout 30 ms             → false (after about 30 ms)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>There is a timed {@code await} that takes a time and a {@code TimeUnit}</li>
     *   <li>Use a loop, and track the remaining time — a timed {@code await} can
     *       return early</li>
     *   <li>The {@code awaitNanos} variant returns the remaining nanoseconds</li>
     * </ul>
     *
     * @param lock          the lock the caller holds
     * @param condition     a condition created from {@code lock}
     * @param ready         the flag set (under {@code lock}) by another thread
     * @param timeoutMillis maximum wait in milliseconds
     * @return {@code true} if {@code ready} is {@code true} when you return
     * @throws InterruptedException if interrupted while waiting
     */
    public boolean awaitFlag(ReentrantLock lock, Condition condition, AtomicBoolean ready,
                             long timeoutMillis) throws InterruptedException {
        throw new UnsupportedOperationException("TODO: implement awaitFlag");
    }

    /**
     * A reusable "turn" gate for exactly two threads taking turns, A then B then A...
     * {@code awaitTurn(true)} blocks until it is A's turn, {@code awaitTurn(false)}
     * until it is B's turn. {@code passTurn()} hands the turn to the other side.
     * A starts. Use <b>one lock and one condition</b>.
     *
     * <p>Examples:
     * <pre>
     * new TurnGate(): awaitTurn(true) returns at once (A starts)
     * awaitTurn(false) blocks until A calls passTurn()
     * two threads alternating 5 times each produce: A B A B A B A B A B
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Keep a boolean field "isATurn" guarded by the lock</li>
     *   <li>Both sides wait on the same condition — which signal method is needed then?</li>
     * </ul>
     */
    public static class TurnGate {

        /**
         * Block until it is the requested side's turn.
         *
         * @param sideA {@code true} for A, {@code false} for B
         * @throws InterruptedException if interrupted while waiting
         */
        public void awaitTurn(boolean sideA) throws InterruptedException {
            throw new UnsupportedOperationException("TODO: implement TurnGate.awaitTurn");
        }

        /** Hand the turn to the other side and wake waiting threads. */
        public void passTurn() {
            throw new UnsupportedOperationException("TODO: implement TurnGate.passTurn");
        }
    }
}
