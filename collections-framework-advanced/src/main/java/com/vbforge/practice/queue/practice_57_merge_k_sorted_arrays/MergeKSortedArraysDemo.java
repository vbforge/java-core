package com.vbforge.practice.queue.practice_57_merge_k_sorted_arrays;

import java.util.PriorityQueue;

/**
 * Merge K sorted arrays with a min-heap.
 * The queue always holds the smallest not-yet-merged element of each array;
 * polling it gives the next value of the merged output, then the next element of the same array is added.
 */
public class MergeKSortedArraysDemo {

    public static void main(String[] args) {
        int[][] arrays = {
                {1, 4, 7},
                {2, 5, 8},
                {3, 6, 9}
        };

        PriorityQueue<StreamEntry> pq = new PriorityQueue<>();

        for (int i = 0; i < arrays.length; i++) {
            pq.add(new StreamEntry(arrays[i][0], i, 0));
        }

        while (!pq.isEmpty()) {
            StreamEntry current = pq.poll();
            System.out.print(current.value + " ");

            int nextIdx = current.elementIndex + 1;
            if (nextIdx < arrays[current.arrayIndex].length) {
                pq.add(new StreamEntry(
                        arrays[current.arrayIndex][nextIdx], current.arrayIndex, nextIdx));
            }
        }
        System.out.println();
    }

    static class StreamEntry implements Comparable<StreamEntry> {
        final int value;
        final int arrayIndex;
        final int elementIndex;

        StreamEntry(int value, int arrayIndex, int elementIndex) {
            this.value = value;
            this.arrayIndex = arrayIndex;
            this.elementIndex = elementIndex;
        }

        @Override
        public int compareTo(StreamEntry o) {
            return Integer.compare(this.value, o.value);
        }
    }
}
