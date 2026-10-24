package com.vbforge.collections_internals_and_big_o.mini;

import java.util.Arrays;
import java.util.NoSuchElementException;

/**
 * A stripped-down re-implementation of the binary min-heap that backs {@link java.util.PriorityQueue}
 * (for {@code int} values), with a MOVE counter.
 *
 * <p>The heap lives in a plain array: the children of index {@code i} are {@code 2i+1} and
 * {@code 2i+2}, its parent is {@code (i-1)/2}. The sift algorithms are the same ones the JDK uses,
 * so the array layout is identical to the one a real {@code PriorityQueue} would have.
 */
public class MiniMinHeap {

    private int[] heap = new int[11];
    private int size;
    private int moves;   // how many times an element was moved one level up or down

    public void offer(int value) {
        if (size == heap.length) {
            heap = Arrays.copyOf(heap, heap.length * 2);
        }
        siftUp(size, value);
        size++;
    }

    public int peek() {
        if (size == 0) {
            throw new NoSuchElementException("heap is empty");
        }
        return heap[0];
    }

    public int poll() {
        if (size == 0) {
            throw new NoSuchElementException("heap is empty");
        }
        int result = heap[0];
        int n = --size;
        int last = heap[n];
        if (n > 0) {
            siftDown(0, last, n);
        }
        return result;
    }

    /** Builds a heap from arbitrary values in O(n) by sifting down every parent, last parent first. */
    public static MiniMinHeap heapify(int[] values) {
        MiniMinHeap result = new MiniMinHeap();
        result.heap = Arrays.copyOf(values, Math.max(values.length, 1));
        result.size = values.length;
        for (int i = (values.length >>> 1) - 1; i >= 0; i--) {
            result.siftDown(i, result.heap[i], values.length);
        }
        return result;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    /** A copy of the used part of the backing array, in heap (NOT sorted) order. */
    public int[] toArray() {
        return Arrays.copyOf(heap, size);
    }

    public int moves() {
        return moves;
    }

    private void siftUp(int index, int value) {
        int k = index;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            int parentValue = heap[parent];
            if (value >= parentValue) {
                break;
            }
            heap[k] = parentValue;
            moves++;
            k = parent;
        }
        heap[k] = value;
    }

    private void siftDown(int index, int value, int n) {
        int k = index;
        int half = n >>> 1;
        while (k < half) {
            int child = 2 * k + 1;
            int smallest = heap[child];
            int right = child + 1;
            if (right < n && smallest > heap[right]) {
                child = right;
                smallest = heap[child];
            }
            if (value <= smallest) {
                break;
            }
            heap[k] = smallest;
            moves++;
            k = child;
        }
        heap[k] = value;
    }
}
