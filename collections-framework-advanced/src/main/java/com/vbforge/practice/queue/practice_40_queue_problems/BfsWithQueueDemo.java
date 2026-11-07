package com.vbforge.practice.queue.practice_40_queue_problems;

import java.util.*;

/**
 * BFS over a small adjacency list: the canonical Queue use case (level by level).
 */
public class BfsWithQueueDemo {

    // ─────────────────────────────────────────────────────────────
    // 1. Queue as FIFO (ArrayDeque)
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 2. Deque as Stack (LIFO)
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 3. Deque — both-ends operations
    // ─────────────────────────────────────────────────────────────


    // ─────────────────────────────────────────────────────────────
    // 4. PriorityQueue — min-heap, ordered retrieval
    // ─────────────────────────────────────────────────────────────



    // ─────────────────────────────────────────────────────────────
    // 5. Real-world patterns
    // ─────────────────────────────────────────────────────────────

    /**
     * BFS (Breadth-First Search) — the canonical Queue use case.
     * Process nodes level by level; guarantees shortest path in unweighted graphs.
     */
    public static void bfsWithQueue() {
        System.out.println("\n=== BFS Pattern with Queue ===");

        // Simple tree (as adjacency list): 1 → [2,3], 2 → [4,5], 3 → [6]
        Map<Integer, List<Integer>> graph = new HashMap<>();
        graph.put(1, Arrays.asList(2, 3));
        graph.put(2, Arrays.asList(4, 5));
        graph.put(3, Arrays.asList(6));
        graph.put(4, Collections.emptyList());
        graph.put(5, Collections.emptyList());
        graph.put(6, Collections.emptyList());

        Queue<Integer> bfsQueue = new ArrayDeque<>();
        Set<Integer>   visited  = new LinkedHashSet<>();

        bfsQueue.offer(1);
        visited.add(1);

        while (!bfsQueue.isEmpty()) {
            int node = bfsQueue.poll();
            System.out.print(node + " ");
            for (int neighbor : graph.getOrDefault(node, Collections.emptyList())) {
                if (!visited.contains(neighbor)) {
                    visited.add(neighbor);
                    bfsQueue.offer(neighbor);
                }
            }
        }
        System.out.println("\nBFS visited: " + visited); // [1, 2, 3, 4, 5, 6]
    }



    // ─────────────────────────────────────────────────────────────
    // main
    // ─────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        bfsWithQueue();
    }
}
