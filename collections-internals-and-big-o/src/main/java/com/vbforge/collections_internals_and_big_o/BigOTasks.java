package com.vbforge.collections_internals_and_big_o;

/**
 * 🎯 TASKS — Big-O in practice
 *
 * <p>Implement every method so that all tests in {@code BigOTasksTest} pass. Some tests use LARGE inputs with a time limit:
 * a solution with the wrong complexity is correct but too slow, and fails (the test measures the elapsed time itself and fails when it is above two seconds).
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>You may add private helper methods</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class BigOTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * The WORST-CASE number of comparisons binary search needs in a sorted array of {@code n} elements. Every comparison
     * examines the middle element and then discards it together with one half of the array; in the worst case the
     * search continues in the LARGER half, which holds {@code n / 2} elements (integer division). The search ends when
     * no element is left.
     *
     * <p>Examples:
     * <pre>
     *   binarySearchWorstCase(0)          → 0
     *   binarySearchWorstCase(1)          → 1
     *   binarySearchWorstCase(2)          → 2
     *   binarySearchWorstCase(3)          → 2
     *   binarySearchWorstCase(4)          → 3
     *   binarySearchWorstCase(7)          → 3
     *   binarySearchWorstCase(8)          → 4
     *   binarySearchWorstCase(1024)       → 11
     *   binarySearchWorstCase(1_000_000)  → 20
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Count how many times {@code n} can be halved (integer division) before it reaches 0; one comparison per halving</li>
     *   <li>The result equals {@code floor(log2(n)) + 1} — derive it with a loop, not with {@code Math.log}</li>
     * </ul>
     *
     * @param n the number of sorted elements, at least 0
     * @return the maximum number of comparisons
     * @throws IllegalArgumentException if {@code n} is negative
     */
    public int binarySearchWorstCase(int n) {
        throw new UnsupportedOperationException("TODO: implement binarySearchWorstCase");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Does the array contain the same value twice? It must run in O(n) time: the tests use HALF A MILLION elements,
     * which a pairwise comparison (O(n²), over a hundred billion steps) cannot handle within the time limit.
     *
     * <p>Examples:
     * <pre>
     *   hasDuplicates([1, 2, 3, 4])      → false
     *   hasDuplicates([1, 2, 3, 1])      → true
     *   hasDuplicates([7])               → false
     *   hasDuplicates([])                → false
     *   hasDuplicates([5, 5])            → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Which collection answers "have I seen this value before?" in O(1) on average?</li>
     *   <li>Stop at the first repeat</li>
     * </ul>
     *
     * @param data the values, never {@code null}
     * @return {@code true} if some value occurs at least twice
     */
    public boolean hasDuplicates(int[] data) {
        throw new UnsupportedOperationException("TODO: implement hasDuplicates");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Find two DIFFERENT positions {@code i < j} whose values add up to {@code target}. Return {@code {i, j}}, or an
     * empty array if there is no such pair. When several pairs exist, return the one with the smallest {@code j}
     * (the pair that is completed first while scanning from left to right); for that {@code j} use the smallest {@code i}.
     * It must run in O(n) time — the tests use half a million elements. The sum of any two values fits in an {@code int}.
     *
     * <p>Examples:
     * <pre>
     *   twoSum([2, 7, 11, 15], 9)    → [0, 1]
     *   twoSum([3, 2, 4], 6)         → [1, 2]
     *   twoSum([3, 3], 6)            → [0, 1]
     *   twoSum([1, 2, 3], 7)         → []
     *   twoSum([-1, -2, -3, -4, -5], -8)  → [2, 4]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>For each value you only need to know: "was the COMPLEMENT {@code target - value} seen earlier, and at which index?"</li>
     *   <li>A map from value to index answers that in O(1) on average; remember the FIRST index of each value</li>
     * </ul>
     *
     * @param numbers the values, never {@code null}
     * @param target  the wanted sum
     * @return the two indices {@code {i, j}} or an empty array
     */
    public int[] twoSum(int[] numbers, int target) {
        throw new UnsupportedOperationException("TODO: implement twoSum");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * The time complexity of an operation on a JDK collection, as a string: {@code "O(1)"}, {@code "O(log n)"} or
     * {@code "O(n)"}. "Amortised" and "average case" count as {@code "O(1)"}. The table below is the one from the README.
     *
     * <pre>
     *   ArrayList      get, set, add(e)            O(1)       add(index, e), remove(index), contains, indexOf   O(n)
     *   LinkedList     addFirst, addLast, removeFirst, removeLast   O(1)      get(index), add(index, e), contains  O(n)
     *   ArrayDeque     addFirst, addLast, pollFirst, pollLast, peekFirst   O(1)      contains   O(n)
     *   HashMap        get, put, remove, containsKey   O(1)      containsValue   O(n)
     *   LinkedHashMap  get, put, remove, containsKey   O(1)
     *   HashSet        add, remove, contains       O(1)
     *   TreeMap        get, put, remove, containsKey, firstKey, lastKey, floorKey, ceilingKey   O(log n)      containsValue   O(n)
     *   TreeSet        add, remove, contains, first, last, floor, ceiling   O(log n)
     *   PriorityQueue  peek   O(1)      offer, poll   O(log n)      contains, remove(Object)   O(n)
     * </pre>
     *
     * <p>Examples:
     * <pre>
     *   complexityOf("ArrayList", "get")         → "O(1)"
     *   complexityOf("LinkedList", "get")        → "O(n)"
     *   complexityOf("HashMap", "put")           → "O(1)"
     *   complexityOf("TreeMap", "floorKey")      → "O(log n)"
     *   complexityOf("PriorityQueue", "peek")    → "O(1)"
     *   complexityOf("PriorityQueue", "poll")    → "O(log n)"
     *   complexityOf("ArrayList", "fly")         → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A nested lookup (structure, then operation) keeps the code readable</li>
     *   <li>Ask yourself WHY each cost is what it is: array index, shifting, hashing, tree height, heap height</li>
     * </ul>
     *
     * @param structure the collection name, e.g. {@code "ArrayList"}
     * @param operation the operation name, e.g. {@code "get"}
     * @return {@code "O(1)"}, {@code "O(log n)"} or {@code "O(n)"}
     * @throws IllegalArgumentException if the structure or the operation is not in the table
     */
    public String complexityOf(String structure, String operation) {
        throw new UnsupportedOperationException("TODO: implement complexityOf");
    }
}
