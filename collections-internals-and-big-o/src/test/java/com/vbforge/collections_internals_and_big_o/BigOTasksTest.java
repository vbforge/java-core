package com.vbforge.collections_internals_and_big_o;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BigOTasks — your implementations")
class BigOTasksTest {

    /** An O(n) solution needs a few hundred milliseconds for half a million elements; an O(n²) one needs many seconds. */
    private static final long TIME_LIMIT_MILLIS = 2_000;

    private static long millisSince(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }

    private BigOTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BigOTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement binarySearchWorstCase()")
    @DisplayName("1. binarySearchWorstCase(n)")
    class BinarySearchWorstCaseTest {

        @ParameterizedTest(name = "n = {0} -> {1} comparisons")
        @CsvSource({
                "0,       0",
                "1,       1",
                "2,       2",
                "3,       2",
                "4,       3",
                "7,       3",
                "8,       4",
                "1023,    10",
                "1024,    11",
                "1000000, 20"
        })
        @DisplayName("examples from Javadoc and more")
        void worstCase(int n, int expected) {
            assertThat(tasks.binarySearchWorstCase(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("doubling n adds exactly one comparison")
        void doublingAddsOne() {
            for (int n = 1; n < 100_000; n *= 3) {
                assertThat(tasks.binarySearchWorstCase(2 * n)).isEqualTo(tasks.binarySearchWorstCase(n) + 1);
            }
        }

        @Test
        @DisplayName("matches the real number of steps of a binary search on a worst-case target")
        void matchesRealSearch() {
            for (int n : new int[]{1, 2, 5, 16, 100, 1024, 5000}) {
                int[] data = new int[n];
                for (int i = 0; i < n; i++) {
                    data[i] = i;
                }
                int worst = 0;
                for (int target = -1; target <= n; target++) {
                    worst = Math.max(worst, BigODemo.binarySearchStepsDemo(data, target));
                }

                assertThat(tasks.binarySearchWorstCase(n)).as("n=%d", n).isEqualTo(worst);
            }
        }

        @Test
        @DisplayName("a negative size is rejected")
        void negativeSize() {
            assertThatThrownBy(() -> tasks.binarySearchWorstCase(-1)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement hasDuplicates()")
    @DisplayName("2. hasDuplicates(data)")
    class HasDuplicatesTest {

        @Test
        @DisplayName("example from Javadoc: all values different")
        void noDuplicates() {
            assertThat(tasks.hasDuplicates(new int[]{1, 2, 3, 4})).isFalse();
        }

        @Test
        @DisplayName("example from Javadoc: a repeated value")
        void withDuplicate() {
            assertThat(tasks.hasDuplicates(new int[]{1, 2, 3, 1})).isTrue();
        }

        @Test
        @DisplayName("example from Javadoc: a single element and an empty array")
        void tinyArrays() {
            assertThat(tasks.hasDuplicates(new int[]{7})).isFalse();
            assertThat(tasks.hasDuplicates(new int[0])).isFalse();
        }

        @Test
        @DisplayName("example from Javadoc: two equal neighbours")
        void equalNeighbours() {
            assertThat(tasks.hasDuplicates(new int[]{5, 5})).isTrue();
        }

        @Test
        @DisplayName("negative numbers and zero are ordinary values")
        void negativeNumbers() {
            assertThat(tasks.hasDuplicates(new int[]{-1, 0, -1})).isTrue();
            assertThat(tasks.hasDuplicates(new int[]{-1, 0, 1})).isFalse();
        }

        @Test
        @DisplayName("the input array is not modified (sorting a copy would be O(n log n), not O(n))")
        void inputUntouched() {
            int[] data = {3, 1, 2, 1};

            tasks.hasDuplicates(data);

            assertThat(data).containsExactly(3, 1, 2, 1);
        }

        @Test
        @DisplayName("500 000 distinct values must be checked in well under a second, not minutes: O(n), not O(n²)")
        void largeInputWithoutDuplicates() {
            int[] data = new int[500_000];
            for (int i = 0; i < data.length; i++) {
                data[i] = i * 3;
            }

            long start = System.nanoTime();
            boolean result = tasks.hasDuplicates(data);
            long millis = millisSince(start);

            assertThat(result).isFalse();
            assertThat(millis).as("took %d ms — an O(n²) solution is too slow", millis).isLessThan(TIME_LIMIT_MILLIS);
        }

        @Test
        @DisplayName("500 000 values whose only duplicate sits at the very end — no early exit for a pairwise comparison")
        void largeInputWithDuplicateAtTheEnd() {
            int[] data = new int[500_000];
            for (int i = 0; i < data.length; i++) {
                data[i] = i;
            }
            data[data.length - 1] = data[data.length - 2];

            long start = System.nanoTime();
            boolean result = tasks.hasDuplicates(data);
            long millis = millisSince(start);

            assertThat(result).isTrue();
            assertThat(millis).as("took %d ms — an O(n²) solution is too slow", millis).isLessThan(TIME_LIMIT_MILLIS);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement twoSum()")
    @DisplayName("3. twoSum(numbers, target)")
    class TwoSumTest {

        @Test
        @DisplayName("example from Javadoc: the first two values")
        void firstTwo() {
            assertThat(tasks.twoSum(new int[]{2, 7, 11, 15}, 9)).containsExactly(0, 1);
        }

        @Test
        @DisplayName("example from Javadoc: the pair is in the middle and at the end")
        void middleAndEnd() {
            assertThat(tasks.twoSum(new int[]{3, 2, 4}, 6)).containsExactly(1, 2);
        }

        @Test
        @DisplayName("example from Javadoc: the same value twice uses two different positions")
        void sameValueTwice() {
            assertThat(tasks.twoSum(new int[]{3, 3}, 6)).containsExactly(0, 1);
        }

        @Test
        @DisplayName("example from Javadoc: no pair gives an empty array")
        void noPair() {
            assertThat(tasks.twoSum(new int[]{1, 2, 3}, 7)).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: negative numbers")
        void negativeNumbers() {
            assertThat(tasks.twoSum(new int[]{-1, -2, -3, -4, -5}, -8)).containsExactly(2, 4);
        }

        @Test
        @DisplayName("a single value can never be paired with itself")
        void singleValue() {
            assertThat(tasks.twoSum(new int[]{5}, 10)).isEmpty();
            assertThat(tasks.twoSum(new int[]{4, 1}, 8)).isEmpty();
        }

        @Test
        @DisplayName("with several valid pairs the one that is completed FIRST (smallest second index) is returned")
        void firstCompletedPairWins() {
            assertThat(tasks.twoSum(new int[]{1, 4, 5, 6, 9, 3}, 10)).containsExactly(1, 3);
        }

        @Test
        @DisplayName("an empty array gives an empty result")
        void emptyArray() {
            assertThat(tasks.twoSum(new int[0], 3)).isEmpty();
        }

        @Test
        @DisplayName("500 000 values must be searched in well under a second: O(n), not O(n²)")
        void largeInput() {
            int[] numbers = new int[500_000];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = i * 2;
            }
            int target = numbers[numbers.length - 2] + numbers[numbers.length - 1];

            long start = System.nanoTime();
            int[] result = tasks.twoSum(numbers, target);
            long millis = millisSince(start);

            assertThat(result).containsExactly(numbers.length - 2, numbers.length - 1);
            assertThat(millis).as("took %d ms — an O(n²) solution is too slow", millis).isLessThan(TIME_LIMIT_MILLIS);
        }

        @Test
        @DisplayName("500 000 values without any valid pair must also be fast")
        void largeInputWithoutPair() {
            int[] numbers = new int[500_000];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = i * 2;
            }

            long start = System.nanoTime();
            int[] result = tasks.twoSum(numbers, 1);
            long millis = millisSince(start);

            assertThat(result).isEmpty();
            assertThat(millis).as("took %d ms — an O(n²) solution is too slow", millis).isLessThan(TIME_LIMIT_MILLIS);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement complexityOf()")
    @DisplayName("4. complexityOf(structure, operation)")
    class ComplexityOfTest {

        @ParameterizedTest(name = "{0}.{1} is {2}")
        @CsvSource({
                "ArrayList,     get,            O(1)",
                "ArrayList,     set,            O(1)",
                "ArrayList,     add,            O(1)",
                "ArrayList,     add(index),     O(n)",
                "ArrayList,     remove(index),  O(n)",
                "ArrayList,     contains,       O(n)",
                "ArrayList,     indexOf,        O(n)",
                "LinkedList,    addFirst,       O(1)",
                "LinkedList,    addLast,        O(1)",
                "LinkedList,    removeFirst,    O(1)",
                "LinkedList,    removeLast,     O(1)",
                "LinkedList,    get,            O(n)",
                "LinkedList,    add(index),     O(n)",
                "LinkedList,    contains,       O(n)",
                "ArrayDeque,    addFirst,       O(1)",
                "ArrayDeque,    addLast,        O(1)",
                "ArrayDeque,    pollFirst,      O(1)",
                "ArrayDeque,    pollLast,       O(1)",
                "ArrayDeque,    peekFirst,      O(1)",
                "ArrayDeque,    contains,       O(n)",
                "HashMap,       get,            O(1)",
                "HashMap,       put,            O(1)",
                "HashMap,       remove,         O(1)",
                "HashMap,       containsKey,    O(1)",
                "HashMap,       containsValue,  O(n)",
                "LinkedHashMap, get,            O(1)",
                "LinkedHashMap, put,            O(1)",
                "LinkedHashMap, remove,         O(1)",
                "LinkedHashMap, containsKey,    O(1)",
                "HashSet,       add,            O(1)",
                "HashSet,       remove,         O(1)",
                "HashSet,       contains,       O(1)",
                "TreeMap,       get,            O(log n)",
                "TreeMap,       put,            O(log n)",
                "TreeMap,       remove,         O(log n)",
                "TreeMap,       containsKey,    O(log n)",
                "TreeMap,       firstKey,       O(log n)",
                "TreeMap,       lastKey,        O(log n)",
                "TreeMap,       floorKey,       O(log n)",
                "TreeMap,       ceilingKey,     O(log n)",
                "TreeMap,       containsValue,  O(n)",
                "TreeSet,       add,            O(log n)",
                "TreeSet,       remove,         O(log n)",
                "TreeSet,       contains,       O(log n)",
                "TreeSet,       first,          O(log n)",
                "TreeSet,       last,           O(log n)",
                "TreeSet,       floor,          O(log n)",
                "TreeSet,       ceiling,        O(log n)",
                "PriorityQueue, peek,           O(1)",
                "PriorityQueue, offer,          O(log n)",
                "PriorityQueue, poll,           O(log n)",
                "PriorityQueue, contains,       O(n)",
                "PriorityQueue, remove(Object), O(n)"
        })
        @DisplayName("the table of the README")
        void table(String structure, String operation, String expected) {
            assertThat(tasks.complexityOf(structure, operation)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: an unknown operation is rejected")
        void unknownOperation() {
            assertThatThrownBy(() -> tasks.complexityOf("ArrayList", "fly"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an unknown structure is rejected")
        void unknownStructure() {
            assertThatThrownBy(() -> tasks.complexityOf("Vector3D", "get"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an operation that exists, but on a different structure, is rejected")
        void wrongStructureForOperation() {
            assertThatThrownBy(() -> tasks.complexityOf("HashSet", "firstKey"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
