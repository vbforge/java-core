package com.vbforge.collections_internals_and_big_o;

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

@DisplayName("HeapInternalsDemo — API contracts")
class HeapInternalsDemoTest {

    @Nested
    @DisplayName("1. A tree stored in an array: index arithmetic")
    class IndexArithmeticTest {

        @ParameterizedTest(name = "index {0}: parent {1}, children {2} and {3}")
        @CsvSource({
                "0, -1, 1,  2",
                "1, 0,  3,  4",
                "2, 0,  5,  6",
                "3, 1,  7,  8",
                "4, 1,  9,  10",
                "6, 2,  13, 14"
        })
        @DisplayName("parent = (i-1)/2, children = 2i+1 and 2i+2; the root has no parent")
        void relatives(int index, int parent, int left, int right) {
            assertThat(HeapInternalsDemo.parentIndexDemo(index)).isEqualTo(parent);
            assertThat(HeapInternalsDemo.leftChildIndexDemo(index)).isEqualTo(left);
            assertThat(HeapInternalsDemo.rightChildIndexDemo(index)).isEqualTo(right);
        }

        @Test
        @DisplayName("a parent and its children point at each other")
        void roundTrip() {
            for (int i = 0; i < 100; i++) {
                assertThat(HeapInternalsDemo.parentIndexDemo(HeapInternalsDemo.leftChildIndexDemo(i))).isEqualTo(i);
                assertThat(HeapInternalsDemo.parentIndexDemo(HeapInternalsDemo.rightChildIndexDemo(i))).isEqualTo(i);
            }
        }

        @Test
        @DisplayName("isMinHeap accepts a valid heap — and a heap is NOT necessarily sorted")
        void validHeap() {
            assertThat(HeapInternalsDemo.isMinHeapDemo(new int[]{1, 3, 8, 5}, 4)).isTrue();
            assertThat(HeapInternalsDemo.isMinHeapDemo(new int[]{1, 5, 2, 6, 7, 3}, 6)).isTrue();
        }

        @Test
        @DisplayName("isMinHeap rejects an array where a child is smaller than its parent")
        void invalidHeap() {
            assertThat(HeapInternalsDemo.isMinHeapDemo(new int[]{5, 3, 8}, 3)).isFalse();
            assertThat(HeapInternalsDemo.isMinHeapDemo(new int[]{1, 3, 8, 2}, 4)).isFalse();
        }

        @Test
        @DisplayName("only the first size elements are inspected")
        void sizeLimitsTheCheck() {
            assertThat(HeapInternalsDemo.isMinHeapDemo(new int[]{1, 3, 8, 0}, 3)).isTrue();
        }
    }

    @Nested
    @DisplayName("2. offer = append at the end, then sift UP")
    class OfferTest {

        @Test
        @DisplayName("offering 5, 3, 8, 1 gives [1, 3, 8, 5] — the 1 climbs to the root")
        void climbingToTheRoot() {
            assertThat(HeapInternalsDemo.layoutAfterOffersDemo(5, 3, 8, 1)).containsExactly(1, 3, 8, 5);
        }

        @Test
        @DisplayName("ascending input stays exactly in input order")
        void ascendingStaysPut() {
            assertThat(HeapInternalsDemo.layoutAfterOffersDemo(1, 2, 3, 4, 5)).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("the layout is NOT sorted, but it is always a valid heap")
        void layoutIsAHeapNotSorted() {
            int[] layout = HeapInternalsDemo.layoutAfterOffersDemo(5, 3, 8, 1);

            assertThat(HeapInternalsDemo.isMinHeapDemo(layout, layout.length)).isTrue();
            assertThat(layout).isNotEqualTo(new int[]{1, 3, 5, 8});
        }

        @Test
        @DisplayName("MiniMinHeap produces exactly the array layout of a real PriorityQueue")
        void matchesRealPriorityQueue() {
            int[][] inputs = {
                    {5, 3, 8, 1},
                    {9, 8, 7, 6, 5, 4, 3, 2, 1},
                    {4, 10, 3, 5, 1, 12, 7, 2},
                    {1, 1, 1},
                    {7, 3, 9, 3, 1, 8, 2, 8, 5, 6, 4}
            };

            for (int[] input : inputs) {
                assertThat(HeapInternalsDemo.layoutAfterOffersDemo(input))
                        .isEqualTo(HeapInternalsDemo.realQueueLayoutDemo(input));
            }
        }
    }

    @Nested
    @DisplayName("3. poll = take the root, move the last element up, sift DOWN")
    class PollTest {

        @Test
        @DisplayName("after one poll the old root is gone and the heap is valid")
        void validAfterPoll() {
            int[] layout = HeapInternalsDemo.layoutAfterPollDemo(5, 3, 8, 1, 9, 7);

            assertThat(layout).hasSize(5);
            assertThat(HeapInternalsDemo.isMinHeapDemo(layout, layout.length)).isTrue();
            assertThat(layout).doesNotContain(1);
        }

        @Test
        @DisplayName("the new root is the second-smallest element")
        void newRootIsSecondSmallest() {
            assertThat(HeapInternalsDemo.layoutAfterPollDemo(5, 3, 8, 1, 9, 7)[0]).isEqualTo(3);
        }

        @Test
        @DisplayName("polling the only element leaves an empty heap")
        void pollLastElement() {
            assertThat(HeapInternalsDemo.layoutAfterPollDemo(42)).isEmpty();
        }

        @Test
        @DisplayName("MiniMinHeap produces exactly the array layout of a real PriorityQueue after a poll")
        void matchesRealPriorityQueue() {
            int[][] inputs = {
                    {5, 3, 8, 1, 9, 7},
                    {9, 8, 7, 6, 5, 4, 3, 2, 1},
                    {4, 10, 3, 5, 1, 12, 7, 2},
                    {7, 3, 9, 3, 1, 8, 2, 8, 5, 6, 4}
            };

            for (int[] input : inputs) {
                assertThat(HeapInternalsDemo.layoutAfterPollDemo(input))
                        .isEqualTo(HeapInternalsDemo.realQueueLayoutAfterPollDemo(input));
            }
        }
    }

    @Nested
    @DisplayName("4. Why offer is O(log n): count the moves")
    class MovesTest {

        @Test
        @DisplayName("ascending input needs no moves at all")
        void ascendingIsFree() {
            assertThat(HeapInternalsDemo.movesForAscendingOffersDemo(7)).isZero();
            assertThat(HeapInternalsDemo.movesForAscendingOffersDemo(1_000)).isZero();
        }

        @Test
        @DisplayName("descending input climbs the full depth every time: 10 moves for 7 elements")
        void descendingSmall() {
            assertThat(HeapInternalsDemo.movesForDescendingOffersDemo(7)).isEqualTo(10);
        }

        @Test
        @DisplayName("1000 descending offers cost 7987 moves: about n log n, never more than 9 levels per offer")
        void descendingLarge() {
            int moves = HeapInternalsDemo.movesForDescendingOffersDemo(1_000);

            assertThat(moves).isEqualTo(7_987);
            assertThat(moves).isLessThan(1_000 * 10);
        }
    }

    @Nested
    @DisplayName("5. Building a heap: n offers vs heapify")
    class HeapifyTest {

        @Test
        @DisplayName("for 7 descending values: 10 moves with offers, only 4 with heapify")
        void smallComparison() {
            int[] descending = {7, 6, 5, 4, 3, 2, 1};

            assertThat(HeapInternalsDemo.movesForRepeatedOffersDemo(descending)).isEqualTo(10);
            assertThat(HeapInternalsDemo.movesForHeapifyDemo(descending)).isEqualTo(4);
        }

        @Test
        @DisplayName("for 1000 descending values heapify needs fewer moves than elements — O(n)")
        void heapifyIsLinear() {
            int[] descending = new int[1_000];
            for (int i = 0; i < descending.length; i++) {
                descending[i] = descending.length - i;
            }

            assertThat(HeapInternalsDemo.movesForHeapifyDemo(descending)).isLessThan(1_000);
            assertThat(HeapInternalsDemo.movesForRepeatedOffersDemo(descending)).isGreaterThan(7_000);
        }

        @Test
        @DisplayName("heapify gives a valid heap")
        void heapifyIsValid() {
            int[] layout = HeapInternalsDemo.heapifyLayoutDemo(9, 4, 7, 1, 8, 2, 6, 3, 5);

            assertThat(HeapInternalsDemo.isMinHeapDemo(layout, layout.length)).isTrue();
            assertThat(layout[0]).isEqualTo(1);
        }

        @Test
        @DisplayName("MiniMinHeap.heapify produces exactly the layout of new PriorityQueue<>(collection)")
        void matchesRealPriorityQueue() {
            int[][] inputs = {
                    {5, 3, 8, 1},
                    {9, 8, 7, 6, 5, 4, 3, 2, 1},
                    {4, 10, 3, 5, 1, 12, 7, 2},
                    {7, 3, 9, 3, 1, 8, 2, 8, 5, 6, 4}
            };

            for (int[] input : inputs) {
                assertThat(HeapInternalsDemo.heapifyLayoutDemo(input))
                        .isEqualTo(HeapInternalsDemo.realQueueHeapifyLayoutDemo(input));
            }
        }

        @Test
        @DisplayName("heapify and repeated offers usually give DIFFERENT layouts of the same elements")
        void layoutsDiffer() {
            int[] values = {9, 8, 7, 6, 5, 4, 3, 2, 1};

            assertThat(HeapInternalsDemo.heapifyLayoutDemo(values))
                    .isNotEqualTo(HeapInternalsDemo.layoutAfterOffersDemo(values));
        }
    }

    @Nested
    @DisplayName("6. Heap sort")
    class HeapSortTest {

        @Test
        @DisplayName("offer everything, poll everything: ascending order")
        void sorts() {
            assertThat(HeapInternalsDemo.heapSortDemo(5, 3, 8, 1, 9, 7, 2)).containsExactly(1, 2, 3, 5, 7, 8, 9);
        }

        @Test
        @DisplayName("duplicates are kept")
        void duplicates() {
            assertThat(HeapInternalsDemo.heapSortDemo(2, 2, 1)).containsExactly(1, 2, 2);
        }

        @Test
        @DisplayName("an empty input gives an empty result")
        void empty() {
            assertThat(HeapInternalsDemo.heapSortDemo()).isEmpty();
        }

        @Test
        @DisplayName("agrees with polling a real PriorityQueue")
        void agreesWithRealQueue() {
            int[] values = {12, -4, 7, 0, 7, 33, -4, 5};
            PriorityQueue<Integer> queue = new PriorityQueue<>();
            for (int value : values) {
                queue.add(value);
            }
            List<Integer> polled = new ArrayList<>();
            while (!queue.isEmpty()) {
                polled.add(queue.poll());
            }

            int[] sorted = HeapInternalsDemo.heapSortDemo(values);

            assertThat(Arrays.asList(Arrays.stream(sorted).boxed().toArray(Integer[]::new))).isEqualTo(polled);
        }
    }
}
