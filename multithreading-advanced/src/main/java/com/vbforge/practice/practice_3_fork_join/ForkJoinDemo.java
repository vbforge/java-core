// IMPLEMENTATION
package com.vbforge.practice.practice_3_fork_join;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

/**
 * Demonstrates the Fork/Join framework — designed for divide-and-conquer parallelism.
 *
 * Core idea:
 *   Split a large problem into smaller sub-problems (fork), solve each recursively,
 *   then combine the results (join). Identical in structure to recursive algorithms.
 *
 * Key classes:
 *   ForkJoinPool     — the pool; uses work-stealing scheduling
 *   ForkJoinTask<V>  — base class for tasks submitted to a ForkJoinPool
 *     RecursiveTask<V>   — fork/join task that RETURNS a result
 *     RecursiveAction    — fork/join task that returns VOID (side-effecting)
 *
 * Work-stealing:
 *   Each worker thread has its own double-ended task deque (deck).
 *   It pushes/pops new sub-tasks to/from its own deque (LIFO — cache warm).
 *   When idle, it STEALS tasks from the TAIL of another thread's deque (FIFO).
 *   This keeps all threads busy without a shared task queue bottleneck.
 *   Result: near-linear speedup for CPU-bound, balanced recursive workloads.
 *
 * ForkJoinPool.commonPool():
 *   A JVM-wide shared pool; parallelism = Runtime.availableProcessors() - 1.
 *   Used automatically by parallel streams and CompletableFuture.
 *   Suitable for short CPU-bound tasks that don't block.
 *   NEVER submit blocking I/O tasks to the common pool — it starves other users.
 *
 * Canonical RecursiveTask pattern:
 *
 *   protected V compute() {
 *       if (problem is small enough) {
 *           return solveDirectly();            // base case
 *       }
 *       SubTask left  = new SubTask(leftHalf);
 *       SubTask right = new SubTask(rightHalf);
 *       left.fork();                           // async: push left to deque
 *       V rightResult = right.compute();       // sync:  solve right on this thread
 *       V leftResult  = left.join();           // block: wait for left
 *       return combine(leftResult, rightResult);
 *   }
 *
 * Why compute right synchronously?
 *   Calling right.compute() on the current thread avoids creating an extra task
 *   object and the associated scheduling overhead. Only one sub-task needs to be
 *   forked; the other runs in place.
 *
 * Threshold tuning:
 *   Too small → too many tasks, scheduling overhead dominates.
 *   Too large → insufficient parallelism.
 *   Rule of thumb: threshold ≈ array_size / (4 * availableProcessors).
 */
public class ForkJoinDemo {

    // ── 1. RecursiveTask — parallel array sum ─────────────────────────────────

    /**
     * Computes the sum of a long[] array segment in parallel.
     * Demonstrates the canonical RecursiveTask pattern.
     */
    static class ParallelSum extends RecursiveTask<Long> {

        private static final int THRESHOLD = 1_000;

        private final long[] array;
        private final int    start;   // inclusive
        private final int    end;     // exclusive

        ParallelSum(long[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end   = end;
        }

        @Override
        protected Long compute() {
            int length = end - start;

            // Base case — sequential sum of small segment
            if (length <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end; i++) sum += array[i];
                return sum;
            }

            // Recursive case — split in half
            int   mid   = start + length / 2;
            ParallelSum left  = new ParallelSum(array, start, mid);
            ParallelSum right = new ParallelSum(array, mid,   end);

            left.fork();                        // schedule left asynchronously
            long rightResult = right.compute(); // solve right on this thread
            long leftResult  = left.join();     // wait for left to finish

            return leftResult + rightResult;
        }
    }

    // ── 2. RecursiveTask — parallel merge sort ────────────────────────────────

    /**
     * Parallel merge sort over an int[] array.
     * Splitting is parallelised; merging is sequential (merge is O(n), not splittable).
     */
    static class ParallelMergeSort extends RecursiveAction {

        private static final int THRESHOLD = 512;

        private final int[] array;
        private final int   start;
        private final int   end;

        ParallelMergeSort(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end   = end;
        }

        @Override
        protected void compute() {
            if (end - start <= THRESHOLD) {
                // Base case — insertion sort for small segments (avoids recursion overhead)
                insertionSort(array, start, end);
                return;
            }

            int mid = start + (end - start) / 2;
            ParallelMergeSort left  = new ParallelMergeSort(array, start, mid);
            ParallelMergeSort right = new ParallelMergeSort(array, mid,   end);

            left.fork();
            right.compute();   // right runs on this thread
            left.join();

            merge(array, start, mid, end);
        }

        private static void insertionSort(int[] a, int lo, int hi) {
            for (int i = lo + 1; i < hi; i++) {
                int key = a[i];
                int j   = i - 1;
                while (j >= lo && a[j] > key) { a[j + 1] = a[j]; j--; }
                a[j + 1] = key;
            }
        }

        private static void merge(int[] a, int lo, int mid, int hi) {
            int[] tmp = new int[hi - lo];
            int i = lo, j = mid, k = 0;
            while (i < mid && j < hi) {
                tmp[k++] = (a[i] <= a[j]) ? a[i++] : a[j++];
            }
            while (i < mid) tmp[k++] = a[i++];
            while (j < hi)  tmp[k++] = a[j++];
            System.arraycopy(tmp, 0, a, lo, tmp.length);
        }
    }

    // ── 3. RecursiveTask — parallel max search ────────────────────────────────

    /**
     * Finds the maximum value in an int[] using parallel divide-and-conquer.
     * Demonstrates combining results with a simple comparison rather than addition.
     */
    static class ParallelMax extends RecursiveTask<Integer> {

        private static final int THRESHOLD = 500;

        private final int[] array;
        private final int   start;
        private final int   end;

        ParallelMax(int[] array, int start, int end) {
            this.array = array;
            this.start = start;
            this.end   = end;
        }

        @Override
        protected Integer compute() {
            if (end - start <= THRESHOLD) {
                int max = array[start];
                for (int i = start + 1; i < end; i++) {
                    if (array[i] > max) max = array[i];
                }
                return max;
            }
            int mid = start + (end - start) / 2;
            ParallelMax left  = new ParallelMax(array, start, mid);
            ParallelMax right = new ParallelMax(array, mid,   end);

            left.fork();
            int rightMax = right.compute();
            int leftMax  = left.join();

            return Math.max(leftMax, rightMax);
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) {
        System.out.println("=== Fork/Join Demo ===\n");

        ForkJoinPool pool = new ForkJoinPool();   // custom pool; not the common pool
        System.out.println("Parallelism: " + pool.getParallelism());

        // --- 1. Parallel sum ---
        System.out.println("\n-- ParallelSum --");
        int n      = 10_000_000;
        long[] arr = new long[n];
        for (int i = 0; i < n; i++) arr[i] = i + 1L;   // 1..n

        long expected = (long) n * (n + 1) / 2;         // Gauss formula
        long result   = pool.invoke(new ParallelSum(arr, 0, n));
        System.out.println("Expected : " + expected);
        System.out.println("Got      : " + result);
        System.out.println("Match    : " + (expected == result));

        // --- 2. Parallel merge sort ---
        System.out.println("\n-- ParallelMergeSort --");
        int[] toSort = new java.util.Random(42).ints(100_000, 0, 1_000_000).toArray();
        pool.invoke(new ParallelMergeSort(toSort, 0, toSort.length));
        boolean sorted = true;
        for (int i = 1; i < toSort.length; i++) {
            if (toSort[i] < toSort[i - 1]) { sorted = false; break; }
        }
        System.out.println("Array sorted correctly: " + sorted);

        // --- 3. Parallel max ---
        System.out.println("\n-- ParallelMax --");
        int[] nums    = { 3, 9, 1, 7, 5, 12, 4, 8, 2, 11, 6 };
        int   max     = pool.invoke(new ParallelMax(nums, 0, nums.length));
        System.out.println("Max: " + max + " (expected 12)");

        // --- 4. Common pool info ---
        System.out.println("\n-- Common pool --");
        ForkJoinPool common = ForkJoinPool.commonPool();
        System.out.println("Common pool parallelism: " + common.getParallelism());
        long sumViaCommon = ForkJoinPool.commonPool()
                .invoke(new ParallelSum(arr, 0, n));
        System.out.println("Sum via common pool: " + (sumViaCommon == expected ? "correct" : "wrong"));

        pool.shutdown();
        System.out.println("\nMain done.");
    }
}