package com.vbforge.collections_internals_and_big_o;

import com.vbforge.collections_internals_and_big_o.mini.MiniArrayList;

import java.util.HashSet;
import java.util.Set;

/**
 * Demonstrates BIG-O notation by COUNTING STEPS instead of measuring time. Time measurements depend on the machine,
 * the JIT warm-up and the garbage collector; a step count is exact and repeatable — so every claim here is verified by a test.
 *
 * <p>Big-O describes how the number of steps GROWS with the input size {@code n}, ignoring constant factors:
 * <pre>
 *   O(1)        constant      same cost for 10 or 10 million elements
 *   O(log n)    logarithmic   doubling n adds ONE step
 *   O(n)        linear        doubling n doubles the cost
 *   O(n log n)  linearithmic  efficient sorting
 *   O(n²)       quadratic     doubling n quadruples the cost
 *   O(2ⁿ)       exponential   adding ONE element doubles the cost
 * </pre>
 *
 * <p>Demo methods are {@code static} and RETURN their result so that {@code BigODemoTest}
 * can verify every claim.
 */
public class BigODemo {

    // ── 1. Linear vs binary search ────────────────────────────────────────────
    /**
     * Walk through the array until the target is found: O(n). Returns the number of elements examined
     * (the whole array if the target is absent).
     */
    public static int linearSearchStepsDemo(int[] data, int target) {
        int steps = 0;
        for (int value : data) {
            steps++;
            if (value == target) {
                break;
            }
        }
        return steps;
    }

    /**
     * Binary search needs a SORTED array and halves the remaining range with every comparison: O(log n).
     * Returns the number of comparisons. Searching for a value larger than every element is a worst case:
     * 11 comparisons for 1 024 elements, 12 for 2 048 — doubling the input adds ONE step.
     */
    public static int binarySearchStepsDemo(int[] sorted, int target) {
        int steps = 0;
        int low = 0;
        int high = sorted.length - 1;
        while (low <= high) {
            steps++;
            int middle = (low + high) >>> 1;
            if (sorted[middle] == target) {
                break;
            } else if (sorted[middle] < target) {
                low = middle + 1;
            } else {
                high = middle - 1;
            }
        }
        return steps;
    }


    // ── 2. Growth rates ───────────────────────────────────────────────────────
    /**
     * The number of steps for each growth class, for input size {@code n} (log means log base 2, rounded up).
     * Compare the results for n = 1000 and n = 2000 to FEEL the difference: O(1) stays, O(log n) gains one,
     * O(n) doubles, O(n²) quadruples. {@code "O(2^n)"} is only defined for n up to 62 (it overflows a long beyond).
     *
     * @throws IllegalArgumentException for an unknown class, {@code n < 1} or an {@code n} that would overflow
     */
    public static long stepsDemo(String complexity, int n) {
        if (n < 1) {
            throw new IllegalArgumentException("n must be at least 1");
        }
        switch (complexity) {
            case "O(1)":
                return 1;
            case "O(log n)":
                return log2Ceil(n);
            case "O(n)":
                return n;
            case "O(n log n)":
                return (long) n * log2Ceil(n);
            case "O(n^2)":
                return (long) n * n;
            case "O(2^n)":
                if (n > 62) {
                    throw new IllegalArgumentException("2^n overflows a long for n > 62");
                }
                return 1L << n;
            default:
                throw new IllegalArgumentException("Unknown complexity: " + complexity);
        }
    }

    private static int log2Ceil(int n) {
        return n <= 1 ? 0 : 32 - Integer.numberOfLeadingZeros(n - 1);
    }


    // ── 3. Quadratic vs linear: finding a duplicate ───────────────────────────
    /**
     * Compare every pair of elements: O(n²). Returns the number of comparisons performed until a duplicate is found
     * or all pairs are exhausted. For n distinct values that is n (n - 1) / 2.
     */
    public static long quadraticDuplicateCheckStepsDemo(int[] data) {
        long comparisons = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = i + 1; j < data.length; j++) {
                comparisons++;
                if (data[i] == data[j]) {
                    return comparisons;
                }
            }
        }
        return comparisons;
    }

    /**
     * Remember the values already seen in a hash set: O(n), because each {@code add} is O(1) on average.
     * Returns the number of {@code add} calls until a duplicate is found or the array is exhausted.
     * The price: O(n) extra memory — a time/space trade-off.
     */
    public static int hashSetDuplicateCheckStepsDemo(int[] data) {
        Set<Integer> seen = new HashSet<>();
        int steps = 0;
        for (int value : data) {
            steps++;
            if (!seen.add(value)) {
                break;
            }
        }
        return steps;
    }


    // ── 4. Amortised O(1) ─────────────────────────────────────────────────────
    /**
     * Appending to an array-backed list is "O(1) amortised": an occasional append is O(n) (the array is copied), but averaged
     * over many appends the cost per append stays constant. Returns the average number of element writes per append —
     * one write for the element itself plus the copying done while growing.
     */
    public static double averageCostPerAppendDemo(int n) {
        MiniArrayList<Integer> list = new MiniArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(i);
        }
        return (n + list.copyCount()) / (double) n;
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        int[] small = sortedArray(1_024);
        int[] large = sortedArray(1_048_576);

        System.out.println("=== 1. searching for a value larger than everything (worst case) ===");
        System.out.println("n = 1 024     linear: " + linearSearchStepsDemo(small, Integer.MAX_VALUE)
                + "   binary: " + binarySearchStepsDemo(small, Integer.MAX_VALUE));
        System.out.println("n = 1 048 576 linear: " + linearSearchStepsDemo(large, Integer.MAX_VALUE)
                + "   binary: " + binarySearchStepsDemo(large, Integer.MAX_VALUE));

        System.out.println("---");
        System.out.println("=== 2. growth rates for n = 1000 and n = 2000 ===");
        for (String complexity : new String[]{"O(1)", "O(log n)", "O(n)", "O(n log n)", "O(n^2)"}) {
            System.out.println(complexity + ": " + stepsDemo(complexity, 1_000) + " -> " + stepsDemo(complexity, 2_000));
        }
        System.out.println("O(2^n) for n = 20 -> 21: " + stepsDemo("O(2^n)", 20) + " -> " + stepsDemo("O(2^n)", 21));

        System.out.println("---");
        System.out.println("=== 3. finding a duplicate in 10 000 distinct values ===");
        int[] distinct = sortedArray(10_000);
        System.out.println("pairwise comparisons: " + quadraticDuplicateCheckStepsDemo(distinct));
        System.out.println("hash set adds:        " + hashSetDuplicateCheckStepsDemo(distinct));

        System.out.println("---");
        System.out.println("=== 4. amortised cost of an append ===");
        System.out.println("1 000 000 appends: " + averageCostPerAppendDemo(1_000_000) + " writes per append");
    }

    private static int[] sortedArray(int n) {
        int[] data = new int[n];
        for (int i = 0; i < n; i++) {
            data[i] = i;
        }
        return data;
    }
}
