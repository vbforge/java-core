package com.vbforge.collections_framework_advanced;

import java.util.List;
import java.util.Queue;

/**
 * 🎯 TASKS — Queue
 *
 * <p>Implement every method so that all tests in {@code QueueTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class QueueTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Remove ALL elements from the queue and return them as a list, in queue order
     * (head first). Afterwards the queue must be empty.
     *
     * <p>Examples:
     * <pre>
     *   queue [a, b, c]  → drainToList(queue) returns [a, b, c], queue is now []
     *   queue []         → drainToList(queue) returns []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the removal method that signals "empty" with a special value instead of an exception</li>
     *   <li>The queue never contains {@code null}</li>
     * </ul>
     *
     * @param queue the queue to empty, never {@code null}
     * @return the removed elements in head-to-tail order
     */
    public List<String> drainToList(Queue<String> queue) {
        throw new UnsupportedOperationException("TODO: implement drainToList");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Merge two queues by taking elements alternately: one from {@code first}, one from
     * {@code second}, starting with {@code first}. When one queue runs out, append everything
     * that is left in the other one. Both queues are EMPTY afterwards.
     *
     * <p>Examples:
     * <pre>
     *   first [a, b, c], second [1, 2]    → [a, 1, b, 2, c]
     *   first [a],       second [1, 2, 3] → [a, 1, 2, 3]
     *   first [],        second []        → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Keep going while at least one of the two queues still has elements</li>
     * </ul>
     *
     * @param first  the queue to start with, never {@code null}
     * @param second the other queue, never {@code null}
     * @return the alternating merge
     */
    public List<String> interleave(Queue<String> first, Queue<String> second) {
        throw new UnsupportedOperationException("TODO: implement interleave");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Josephus problem. {@code n} people numbered {@code 1..n} stand in a circle.
     * Starting with person 1, count {@code k} people; the person at the count is eliminated
     * and counting restarts with the next person. Return the number of the last person standing.
     *
     * <p>Examples:
     * <pre>
     *   lastSurvivor(5, 2)  → 3     (eliminated in order: 2, 4, 1, 5)
     *   lastSurvivor(7, 3)  → 4     (eliminated in order: 3, 6, 2, 7, 5, 1)
     *   lastSurvivor(1, 5)  → 1
     *   lastSurvivor(6, 1)  → 6
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>"Skip a person" means: take them from the head and put them back at the tail</li>
     *   <li>The person at the count leaves for good</li>
     * </ul>
     *
     * @param n the number of people, at least 1
     * @param k the counting step, at least 1
     * @return the number of the survivor
     * @throws IllegalArgumentException if {@code n} or {@code k} is less than 1
     */
    public int lastSurvivor(int n, int k) {
        throw new UnsupportedOperationException("TODO: implement lastSurvivor");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Request counter. For every timestamp (in milliseconds, strictly increasing) return how many
     * requests happened in the last {@code windowMs} milliseconds, INCLUDING the current one.
     * A request at time {@code t'} counts for the request at time {@code t} when
     * {@code t - windowMs <= t' <= t}.
     *
     * <p>Examples:
     * <pre>
     *   recentCounts([1, 100, 3001, 3002], 3000)  → [1, 2, 3, 3]
     *   recentCounts([5, 6, 7], 0)                → [1, 1, 1]
     *   recentCounts([], 3000)                    → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Old requests can only expire from the FRONT, in the order they arrived</li>
     *   <li>The queue size right after adding the current request is the answer</li>
     * </ul>
     *
     * @param timestamps the request times, strictly increasing, never {@code null}
     * @param windowMs   the window length in milliseconds, at least 0
     * @return one count per timestamp
     * @throws IllegalArgumentException if {@code windowMs} is negative
     */
    public List<Integer> recentCounts(int[] timestamps, int windowMs) {
        throw new UnsupportedOperationException("TODO: implement recentCounts");
    }
}
