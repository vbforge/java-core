package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Ticket;

import java.util.List;

/**
 * 🎯 TASKS — PriorityQueue
 *
 * <p>Implement every method so that all tests in {@code PriorityQueueTasksTest} pass.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>You may add private helper classes/methods if you need them</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class PriorityQueueTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the ids of the tickets in SERVICE order: smaller priority number first; tickets with the
     * same priority are served in alphabetical order of their id.
     *
     * <p>Examples:
     * <pre>
     *   [T1 p3, T2 p1, T3 p2]          → [T2, T3, T1]
     *   [B p1, A p1, C p0]             → [C, A, B]
     *   []                             → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A {@code Comparator} with a primary key and a tie-breaker defines the queue's order</li>
     *   <li>Remember that iterating a priority queue does not give sorted order</li>
     * </ul>
     *
     * @param tickets the tickets, never {@code null}
     * @return the ticket ids in service order
     */
    public List<String> serviceOrder(List<Ticket> tickets) {
        throw new UnsupportedOperationException("TODO: implement serviceOrder");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Last stone weight. Repeat: take the two HEAVIEST stones; if they weigh the same both are
     * destroyed, otherwise the lighter is destroyed and the heavier one is replaced by a stone
     * weighing the DIFFERENCE. Return the weight of the last remaining stone, or 0 if none is left.
     *
     * <p>Examples:
     * <pre>
     *   lastStoneWeight([2, 7, 4, 1, 8, 1])  → 1
     *   lastStoneWeight([7, 6])              → 1
     *   lastStoneWeight([3, 3])              → 0
     *   lastStoneWeight([5])                 → 5
     *   lastStoneWeight([])                  → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You need "give me the largest" over and over — which direction should the queue order?</li>
     * </ul>
     *
     * @param stones the stone weights, all positive, never {@code null}
     * @return the weight of the last stone, or 0
     */
    public int lastStoneWeight(int[] stones) {
        throw new UnsupportedOperationException("TODO: implement lastStoneWeight");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Merge several ASCENDING-sorted lists into one ascending list. Duplicates are kept.
     * Lists may be empty.
     *
     * <p>Examples:
     * <pre>
     *   [[1, 4, 7], [2, 5], [3, 6, 8]]  → [1, 2, 3, 4, 5, 6, 7, 8]
     *   [[1, 1], [1]]                   → [1, 1, 1]
     *   [[], [2], []]                   → [2]
     *   []                              → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The smallest remaining element overall is always the head of one of the lists</li>
     *   <li>Queue elements must remember WHERE they came from — an {@code int[]} triple
     *       {@code {value, listIndex, positionInList}} is enough</li>
     * </ul>
     *
     * @param lists the sorted lists, never {@code null}
     * @return one sorted list with all elements
     */
    public List<Integer> mergeSorted(List<List<Integer>> lists) {
        throw new UnsupportedOperationException("TODO: implement mergeSorted");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return the k-th LARGEST number, counting duplicates separately (it is the k-th in descending
     * order, not the k-th distinct value). Keep memory at O(k): never hold all numbers in the queue.
     *
     * <p>Examples:
     * <pre>
     *   kthLargest([3, 2, 1, 5, 6, 4], 2)         → 5
     *   kthLargest([3, 2, 3, 1, 2, 4, 5, 5, 6], 4) → 4
     *   kthLargest([7], 1)                         → 7
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A queue that holds the k largest numbers seen so far — and always knows its SMALLEST member —
     *       has the answer at its head at the end</li>
     * </ul>
     *
     * @param numbers the numbers, never {@code null}
     * @param k       the rank, between 1 and {@code numbers.length}
     * @return the k-th largest number
     * @throws IllegalArgumentException if {@code k} is outside {@code 1..numbers.length}
     */
    public int kthLargest(int[] numbers, int k) {
        throw new UnsupportedOperationException("TODO: implement kthLargest");
    }
}
