package com.vbforge.practice.deque.practice_10_deque_internals;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

/**
 * ArrayDeque vs LinkedList — as a queue / stack.
 *
 * ArrayDeque is the preferred Deque / Stack implementation:
 *   - No node object per element (unlike LinkedList) -> less GC pressure
 *   - Cache-friendly circular array
 *   - O(1) amortized for addFirst/addLast/pollFirst/pollLast
 *
 * LinkedList: O(1) at both ends but each node is a heap object (prev + next refs).
 *
 * Rule: Prefer ArrayDeque unless you specifically need List + Deque together.
 */
public class DequeInternalsDemo {

    public static void main(String[] args) {

        // ================== Scene 1: ArrayDeque as FIFO queue ==================
        System.out.println("=== ArrayDeque as queue ===");
        Deque<Integer> adQueue = new ArrayDeque<>(List.of(1, 2, 3, 4, 5));
        System.out.println("Processed (ArrayDeque): " + processQueue(adQueue) + " items");

        // ================== Scene 2: LinkedList as FIFO queue — same API, heavier nodes ==================
        System.out.println("\n=== LinkedList as queue ===");
        Deque<Integer> llQueue = new LinkedList<>(List.of(1, 2, 3, 4, 5));
        System.out.println("Processed (LinkedList): " + processQueue(llQueue) + " items");
    }

    /**
     * Both implementations offer O(1) pollFirst/pollLast; ArrayDeque is faster
     * in practice because it stores elements in a circular array with no
     * per-element node overhead.
     */
    public static int processQueue(Deque<Integer> deque) {
        int processed = 0;
        while (!deque.isEmpty()) {
            deque.poll();  // O(1) for both; ArrayDeque is faster in practice
            processed++;
        }
        return processed;
    }
}
