package com.vbforge.collections_internals_and_big_o;

/**
 * 🎯 TASKS — PriorityQueue internals (binary min-heap in an array)
 *
 * <p>Implement every method so that all tests in {@code HeapInternalsTasksTest} pass. The tests compare the exact array
 * layouts with those of the REAL {@code java.util.PriorityQueue}, so follow the standard sift algorithms.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>You may add private helper methods</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class HeapInternalsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the three relatives of a heap index as {@code {parent, leftChild, rightChild}}.
     * The root (index 0) has no parent: its parent is reported as {@code -1}.
     * The child indices are returned even if they lie outside the array.
     *
     * <p>Examples:
     * <pre>
     *   relatives(0)  → [-1, 1, 2]
     *   relatives(1)  → [0, 3, 4]
     *   relatives(2)  → [0, 5, 6]
     *   relatives(6)  → [2, 13, 14]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Children: {@code 2i + 1} and {@code 2i + 2}; parent: {@code (i - 1) / 2} (integer division)</li>
     * </ul>
     *
     * @param index the heap index, at least 0
     * @return {@code {parent, left, right}}
     */
    public int[] relatives(int index) {
        throw new UnsupportedOperationException("TODO: implement relatives");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Restore the min-heap property after a new element was placed at {@code index} (usually the last slot):
     * while the element is SMALLER than its parent, swap the two. Works in place.
     *
     * <p>Examples:
     * <pre>
     *   [1, 3, 8, 5, 0], siftUp(heap, 4)  → heap is now [0, 1, 8, 5, 3]
     *   [1, 3, 8, 5, 9], siftUp(heap, 4)  → unchanged     (9 is not smaller than its parent 3)
     *   [4],             siftUp(heap, 0)  → unchanged     (the root has no parent)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Stop at the root or as soon as the parent is not larger</li>
     *   <li>Equal values must NOT be swapped</li>
     * </ul>
     *
     * @param heap  the array that holds the heap
     * @param index the index of the element that may violate the heap property
     */
    public void siftUp(int[] heap, int index) {
        throw new UnsupportedOperationException("TODO: implement siftUp");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Restore the min-heap property after the element at {@code index} may be too large (usually the root after
     * a poll): while it is LARGER than its smaller child, swap it with that child. Only the first {@code size} slots
     * belong to the heap; anything behind them must not be read or changed. Works in place.
     *
     * <p>When both children are equal, take the LEFT one. Equal values must NOT be swapped.
     *
     * <p>Examples:
     * <pre>
     *   [9, 3, 8, 5, 4], size 5, siftDown(heap, 5, 0)  → heap is now [3, 4, 8, 5, 9]
     *   [9, 3, 8, 0, 0], size 3, siftDown(heap, 3, 0)  → heap is now [3, 9, 8, 0, 0]   (slots 3 and 4 are not part of the heap)
     *   [1, 3, 8],       size 3, siftDown(heap, 3, 0)  → unchanged
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A child exists only if its index is smaller than {@code size}</li>
     *   <li>Compare against the SMALLER child — otherwise the other child would end up above a smaller parent</li>
     * </ul>
     *
     * @param heap  the array that holds the heap
     * @param size  the number of elements that belong to the heap
     * @param index the index of the element to sink
     */
    public void siftDown(int[] heap, int size, int index) {
        throw new UnsupportedOperationException("TODO: implement siftDown");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Turn arbitrary values into a min-heap in O(n) ("heapify"): copy the values, then — starting at the LAST PARENT
     * (index {@code n/2 - 1}) and going back to index 0 — sift every element down. The input array is not modified.
     * The resulting layout must be identical to that of {@code new PriorityQueue<>(collection)}.
     *
     * <p>Examples:
     * <pre>
     *   heapify([5, 3, 8, 1])   → [1, 3, 8, 5]
     *   heapify([1, 2, 3])      → [1, 2, 3]
     *   heapify([3, 2, 1])      → [1, 2, 3]
     *   heapify([])             → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Reuse {@link #siftDown(int[], int, int)}</li>
     *   <li>Leaves are already valid heaps of size 1 — skip them</li>
     * </ul>
     *
     * @param values the values, never {@code null}
     * @return a new array in min-heap order
     */
    public int[] heapify(int[] values) {
        throw new UnsupportedOperationException("TODO: implement heapify");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Heap sort: return a NEW array with the values in ascending order, using a heap — heapify first, then repeatedly
     * move the minimum out. The input array is not modified. Duplicates are kept.
     *
     * <p>Examples:
     * <pre>
     *   heapSort([5, 3, 8, 1, 9, 7, 2])  → [1, 2, 3, 5, 7, 8, 9]
     *   heapSort([2, 2, 1])              → [1, 2, 2]
     *   heapSort([])                     → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>"Remove the root": take it, move the LAST element to the root, shrink the heap by one, sift down</li>
     *   <li>The sorted result is simply the sequence of removed roots</li>
     * </ul>
     *
     * @param values the values, never {@code null}
     * @return the values in ascending order
     */
    public int[] heapSort(int[] values) {
        throw new UnsupportedOperationException("TODO: implement heapSort");
    }
}
