package com.vbforge.collections_internals_and_big_o;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.PriorityQueue;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HeapInternalsTasks — your implementations")
class HeapInternalsTasksTest {

    private HeapInternalsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new HeapInternalsTasks();
    }

    private static boolean isMinHeap(int[] array, int size) {
        for (int i = 1; i < size; i++) {
            if (array[(i - 1) / 2] > array[i]) {
                return false;
            }
        }
        return true;
    }

    private static int[] realHeapifyLayout(int[] values) {
        List<Integer> list = new ArrayList<>();
        for (int value : values) {
            list.add(value);
        }
        List<Integer> layout = new ArrayList<>(new PriorityQueue<>(list));
        int[] result = new int[layout.size()];
        for (int i = 0; i < result.length; i++) {
            result[i] = layout.get(i);
        }
        return result;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement relatives()")
    @DisplayName("1. relatives(index)")
    class RelativesTest {

        @Test
        @DisplayName("example from Javadoc: the root has no parent")
        void root() {
            assertThat(tasks.relatives(0)).containsExactly(-1, 1, 2);
        }

        @ParameterizedTest(name = "relatives({0}) = [{1}, {2}, {3}]")
        @CsvSource({
                "1,  0,  3,  4",
                "2,  0,  5,  6",
                "3,  1,  7,  8",
                "4,  1,  9,  10",
                "6,  2,  13, 14",
                "10, 4,  21, 22"
        })
        @DisplayName("examples from Javadoc and more")
        void others(int index, int parent, int left, int right) {
            assertThat(tasks.relatives(index)).containsExactly(parent, left, right);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement siftUp()")
    @DisplayName("2. siftUp(heap, index)")
    class SiftUpTest {

        @Test
        @DisplayName("example from Javadoc: a small element climbs to the root")
        void climbsToRoot() {
            int[] heap = {1, 3, 8, 5, 0};

            tasks.siftUp(heap, 4);

            assertThat(heap).containsExactly(0, 1, 8, 5, 3);
        }

        @Test
        @DisplayName("example from Javadoc: a large element stays where it is")
        void staysPut() {
            int[] heap = {1, 3, 8, 5, 9};

            tasks.siftUp(heap, 4);

            assertThat(heap).containsExactly(1, 3, 8, 5, 9);
        }

        @Test
        @DisplayName("example from Javadoc: the root has no parent")
        void rootUnchanged() {
            int[] heap = {4};

            tasks.siftUp(heap, 0);

            assertThat(heap).containsExactly(4);
        }

        @Test
        @DisplayName("an element that climbs only one level")
        void oneLevel() {
            int[] heap = {2, 6, 9, 7};

            tasks.siftUp(heap, 3);
            // 7 is larger than its parent 6: nothing moves
            assertThat(heap).containsExactly(2, 6, 9, 7);

            int[] other = {2, 6, 9, 4};
            tasks.siftUp(other, 3);
            assertThat(other).containsExactly(2, 4, 9, 6);
        }

        @Test
        @DisplayName("equal values are NOT swapped")
        void equalValuesStay() {
            int[] heap = {1, 5, 5};

            tasks.siftUp(heap, 2);

            assertThat(heap).containsExactly(1, 5, 5);
        }

        @Test
        @DisplayName("building a heap with repeated siftUp matches a real PriorityQueue's layout")
        void matchesRealQueue() {
            int[] values = {7, 3, 9, 3, 1, 8, 2, 8, 5, 6, 4};
            int[] heap = new int[values.length];
            PriorityQueue<Integer> real = new PriorityQueue<>();
            for (int i = 0; i < values.length; i++) {
                heap[i] = values[i];
                tasks.siftUp(heap, i);
                real.offer(values[i]);
            }

            assertThat(Arrays.asList(Arrays.stream(heap).boxed().toArray(Integer[]::new)))
                    .isEqualTo(new ArrayList<>(real));
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement siftDown()")
    @DisplayName("3. siftDown(heap, size, index)")
    class SiftDownTest {

        @Test
        @DisplayName("example from Javadoc: a large root sinks two levels")
        void sinksTwoLevels() {
            int[] heap = {9, 3, 8, 5, 4};

            tasks.siftDown(heap, 5, 0);

            assertThat(heap).containsExactly(3, 4, 8, 5, 9);
        }

        @Test
        @DisplayName("example from Javadoc: slots behind size are not touched")
        void respectsSize() {
            int[] heap = {9, 3, 8, 0, 0};

            tasks.siftDown(heap, 3, 0);

            assertThat(heap).containsExactly(3, 9, 8, 0, 0);
        }

        @Test
        @DisplayName("example from Javadoc: a valid heap is unchanged")
        void validHeapUnchanged() {
            int[] heap = {1, 3, 8};

            tasks.siftDown(heap, 3, 0);

            assertThat(heap).containsExactly(1, 3, 8);
        }

        @Test
        @DisplayName("the element swaps with the SMALLER child (otherwise the heap would break)")
        void smallerChildWins() {
            int[] heap = {10, 2, 1};

            tasks.siftDown(heap, 3, 0);

            assertThat(heap).containsExactly(1, 2, 10);
        }

        @Test
        @DisplayName("with equal children the LEFT one is chosen")
        void leftChildOnTie() {
            int[] heap = {10, 2, 2};

            tasks.siftDown(heap, 3, 0);

            assertThat(heap).containsExactly(2, 10, 2);
        }

        @Test
        @DisplayName("a node with only a left child is handled")
        void onlyLeftChild() {
            int[] heap = {5, 1};

            tasks.siftDown(heap, 2, 0);

            assertThat(heap).containsExactly(1, 5);
        }

        @Test
        @DisplayName("sifting down from a middle index leaves the rest alone")
        void middleIndex() {
            int[] heap = {1, 9, 2, 3, 4, 5, 6};

            tasks.siftDown(heap, 7, 1);

            assertThat(heap).containsExactly(1, 3, 2, 9, 4, 5, 6);
        }

        @Test
        @DisplayName("replacing the root of a heap with a large value and sifting down restores a valid heap")
        void restoresHeap() {
            int[] heap = {1, 3, 2, 7, 4, 5, 6};
            heap[0] = 100;

            tasks.siftDown(heap, heap.length, 0);

            assertThat(isMinHeap(heap, heap.length)).isTrue();
            assertThat(heap[0]).isEqualTo(2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement heapify()")
    @DisplayName("4. heapify(values)")
    class HeapifyTest {

        @Test
        @DisplayName("example from Javadoc: [5, 3, 8, 1]")
        void example() {
            assertThat(tasks.heapify(new int[]{5, 3, 8, 1})).containsExactly(1, 3, 8, 5);
        }

        @Test
        @DisplayName("example from Javadoc: already a heap")
        void alreadyAHeap() {
            assertThat(tasks.heapify(new int[]{1, 2, 3})).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("example from Javadoc: descending three")
        void descendingThree() {
            assertThat(tasks.heapify(new int[]{3, 2, 1})).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("example from Javadoc: empty input")
        void empty() {
            assertThat(tasks.heapify(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("the layout is identical to new PriorityQueue<>(collection) for several inputs")
        void matchesRealQueue() {
            int[][] inputs = {
                    {9, 8, 7, 6, 5, 4, 3, 2, 1},
                    {4, 10, 3, 5, 1, 12, 7, 2},
                    {7, 3, 9, 3, 1, 8, 2, 8, 5, 6, 4},
                    {20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1},
                    {42}
            };

            for (int[] input : inputs) {
                assertThat(tasks.heapify(input)).isEqualTo(realHeapifyLayout(input));
            }
        }

        @Test
        @DisplayName("the input array is not modified")
        void inputUntouched() {
            int[] input = {5, 3, 8, 1};

            int[] result = tasks.heapify(input);

            assertThat(input).containsExactly(5, 3, 8, 1);
            assertThat(result).isNotSameAs(input);
        }

        @Test
        @DisplayName("a large random-looking input gives a valid heap")
        void largeInput() {
            int[] input = new int[1_000];
            for (int i = 0; i < input.length; i++) {
                input[i] = (i * 7919) % 1_009;
            }

            int[] result = tasks.heapify(input);

            assertThat(isMinHeap(result, result.length)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement heapSort()")
    @DisplayName("5. heapSort(values)")
    class HeapSortTest {

        @Test
        @DisplayName("example from Javadoc: sorts ascending")
        void sorts() {
            assertThat(tasks.heapSort(new int[]{5, 3, 8, 1, 9, 7, 2})).containsExactly(1, 2, 3, 5, 7, 8, 9);
        }

        @Test
        @DisplayName("example from Javadoc: duplicates are kept")
        void duplicates() {
            assertThat(tasks.heapSort(new int[]{2, 2, 1})).containsExactly(1, 2, 2);
        }

        @Test
        @DisplayName("example from Javadoc: empty input")
        void empty() {
            assertThat(tasks.heapSort(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("negative numbers and a single element")
        void negativesAndSingle() {
            assertThat(tasks.heapSort(new int[]{3, -1, -7, 0})).containsExactly(-7, -1, 0, 3);
            assertThat(tasks.heapSort(new int[]{5})).containsExactly(5);
        }

        @Test
        @DisplayName("the input array is not modified")
        void inputUntouched() {
            int[] input = {3, 1, 2};

            int[] result = tasks.heapSort(input);

            assertThat(input).containsExactly(3, 1, 2);
            assertThat(result).isNotSameAs(input);
        }

        @Test
        @DisplayName("agrees with Arrays.sort on a large input")
        void matchesArraysSort() {
            int[] input = new int[2_000];
            for (int i = 0; i < input.length; i++) {
                input[i] = (i * 7919) % 1_013 - 500;
            }
            int[] expected = input.clone();
            Arrays.sort(expected);

            assertThat(tasks.heapSort(input)).isEqualTo(expected);
        }
    }
}
