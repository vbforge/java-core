package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.mini.MiniMinHeap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Demonstrates how {@link PriorityQueue} works inside: a BINARY MIN-HEAP stored in a plain array.
 *
 * <p>The heap property: every element is {@code <=} its children, so the smallest element is always at index 0.
 * Nothing else is ordered — which is why iterating a {@code PriorityQueue} does not give sorted output.
 *
 * <p>The sift algorithms are shown with {@link MiniMinHeap}, a small re-implementation that uses the same steps as the JDK.
 * The tests compare its array layout with the layout of a REAL {@code PriorityQueue}.
 *
 * <p>Complexity: {@code peek} O(1); {@code offer} and {@code poll} O(log n) (the height of the tree);
 * building a heap from n elements O(n); {@code contains}/{@code remove(Object)} O(n).
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code HeapInternalsDemoTest}
 * can verify every claim.
 */
public class HeapInternalsDemo {

    // ── 1. A tree stored in an array: index arithmetic ────────────────────────
    /**
     * Index {@code i} has its children at {@code 2i + 1} and {@code 2i + 2} and its parent at {@code (i - 1) / 2}.
     * No pointers are needed. The root (index 0) has no parent: the result is -1.
     */
    public static int parentIndexDemo(int index) {
        return index == 0 ? -1 : (index - 1) / 2;
    }

    public static int leftChildIndexDemo(int index) {
        return 2 * index + 1;
    }

    public static int rightChildIndexDemo(int index) {
        return 2 * index + 2;
    }

    /** Checks the heap property for the first {@code size} elements: no element is smaller than its parent. */
    public static boolean isMinHeapDemo(int[] array, int size) {
        for (int i = 1; i < size; i++) {
            if (array[parentIndexDemo(i)] > array[i]) {
                return false;
            }
        }
        return true;
    }


    // ── 2. offer = append at the end, then sift UP ────────────────────────────
    /**
     * A new element is placed in the first free slot and then swapped upwards while it is smaller than its parent.
     * Offering 5, 3, 8, 1 gives the array {@code [1, 3, 8, 5]}: the 1 climbed from index 3 to the root.
     */
    public static int[] layoutAfterOffersDemo(int... values) {
        MiniMinHeap heap = new MiniMinHeap();
        for (int value : values) {
            heap.offer(value);
        }
        return heap.toArray();
    }

    /** The array layout of a REAL {@code PriorityQueue} after the same offers (its iteration order IS the array order). */
    public static int[] realQueueLayoutDemo(int... values) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (int value : values) {
            queue.offer(value);
        }
        return toIntArray(new ArrayList<>(queue));
    }


    // ── 3. poll = take the root, move the last element up, sift DOWN ──────────
    /**
     * The root is removed, the LAST element is moved to the root, and then it sinks, each time swapping with the smaller
     * child, until the heap property holds again. Returns the array layout after offering the values and polling once.
     */
    public static int[] layoutAfterPollDemo(int... values) {
        MiniMinHeap heap = new MiniMinHeap();
        for (int value : values) {
            heap.offer(value);
        }
        heap.poll();
        return heap.toArray();
    }

    /** The layout of a REAL {@code PriorityQueue} after the same offers and one poll. */
    public static int[] realQueueLayoutAfterPollDemo(int... values) {
        PriorityQueue<Integer> queue = new PriorityQueue<>();
        for (int value : values) {
            queue.offer(value);
        }
        queue.poll();
        return toIntArray(new ArrayList<>(queue));
    }


    // ── 4. Why offer is O(log n): count the moves ─────────────────────────────
    /** Ascending input never has to climb: every new element is already {@code >=} its parent — zero moves. */
    public static int movesForAscendingOffersDemo(int n) {
        MiniMinHeap heap = new MiniMinHeap();
        for (int i = 1; i <= n; i++) {
            heap.offer(i);
        }
        return heap.moves();
    }

    /**
     * Descending input is the worst case: every new element becomes the new minimum and climbs all the way to the root,
     * i.e. as many levels as the tree is deep — about log2(n) each, so O(n log n) moves in total.
     */
    public static int movesForDescendingOffersDemo(int n) {
        MiniMinHeap heap = new MiniMinHeap();
        for (int i = n; i >= 1; i--) {
            heap.offer(i);
        }
        return heap.moves();
    }


    // ── 5. Building a heap: n offers vs heapify ───────────────────────────────
    /** Moves needed when the values are offered ONE BY ONE. */
    public static int movesForRepeatedOffersDemo(int... values) {
        MiniMinHeap heap = new MiniMinHeap();
        for (int value : values) {
            heap.offer(value);
        }
        return heap.moves();
    }

    /**
     * Moves needed by "heapify": put all values in the array and sift down every parent, from the last parent to the root.
     * Most nodes are near the bottom and barely move, so the total is O(n) instead of O(n log n).
     */
    public static int movesForHeapifyDemo(int... values) {
        return MiniMinHeap.heapify(values).moves();
    }

    /** The layout produced by the bottom-up heapify. */
    public static int[] heapifyLayoutDemo(int... values) {
        return MiniMinHeap.heapify(values).toArray();
    }

    /** The layout of {@code new PriorityQueue<>(collection)}, which uses the same bottom-up heapify. */
    public static int[] realQueueHeapifyLayoutDemo(int... values) {
        List<Integer> list = new ArrayList<>();
        for (int value : values) {
            list.add(value);
        }
        return toIntArray(new ArrayList<>(new PriorityQueue<>(list)));
    }


    // ── 6. Heap sort ──────────────────────────────────────────────────────────
    /**
     * Offer everything, then poll everything: the elements come out in ascending order.
     * n offers and n polls, each O(log n) — O(n log n) overall, with no extra comparison sort involved.
     */
    public static int[] heapSortDemo(int... values) {
        MiniMinHeap heap = new MiniMinHeap();
        for (int value : values) {
            heap.offer(value);
        }
        int[] sorted = new int[values.length];
        for (int i = 0; i < sorted.length; i++) {
            sorted[i] = heap.poll();
        }
        return sorted;
    }

    private static int[] toIntArray(List<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = list.get(i);
        }
        return result;
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        System.out.println("=== 1. index arithmetic ===");
        System.out.println("children of 1: " + leftChildIndexDemo(1) + ", " + rightChildIndexDemo(1)
                + " | parent of 4: " + parentIndexDemo(4) + " | parent of 0: " + parentIndexDemo(0));

        System.out.println("---");
        System.out.println("=== 2. offer = sift up ===");
        System.out.println("mini: " + Arrays.toString(layoutAfterOffersDemo(5, 3, 8, 1)));
        System.out.println("real: " + Arrays.toString(realQueueLayoutDemo(5, 3, 8, 1)));

        System.out.println("---");
        System.out.println("=== 3. poll = sift down ===");
        System.out.println("mini: " + Arrays.toString(layoutAfterPollDemo(5, 3, 8, 1, 9, 7)));
        System.out.println("real: " + Arrays.toString(realQueueLayoutAfterPollDemo(5, 3, 8, 1, 9, 7)));

        System.out.println("---");
        System.out.println("=== 4. moves for 1000 offers ===");
        System.out.println("ascending : " + movesForAscendingOffersDemo(1000));
        System.out.println("descending: " + movesForDescendingOffersDemo(1000));

        System.out.println("---");
        System.out.println("=== 5. repeated offers vs heapify (descending 1..1000) ===");
        int[] descending = new int[1000];
        for (int i = 0; i < descending.length; i++) {
            descending[i] = 1000 - i;
        }
        System.out.println("offers  : " + movesForRepeatedOffersDemo(descending));
        System.out.println("heapify : " + movesForHeapifyDemo(descending));

        System.out.println("---");
        System.out.println("=== 6. heap sort ===");
        System.out.println(Arrays.toString(heapSortDemo(5, 3, 8, 1, 9, 7, 2)));
    }
}
