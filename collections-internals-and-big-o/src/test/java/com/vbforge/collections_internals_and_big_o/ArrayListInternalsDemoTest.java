package com.vbforge.collections_internals_and_big_o;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ArrayListInternalsDemo — API contracts")
class ArrayListInternalsDemoTest {

    @Nested
    @DisplayName("1. The backing array and its growth")
    class GrowthTest {

        @Test
        @DisplayName("an empty list owns no capacity at all")
        void noAddsNoCapacity() {
            assertThat(ArrayListInternalsDemo.capacityHistoryDemo(0)).isEmpty();
        }

        @Test
        @DisplayName("the first add allocates capacity 10")
        void firstAddAllocatesTen() {
            assertThat(ArrayListInternalsDemo.capacityHistoryDemo(1)).containsExactly(10);
            assertThat(ArrayListInternalsDemo.capacityHistoryDemo(10)).containsExactly(10);
        }

        @Test
        @DisplayName("the 11th element triggers growth by 50 % to 15")
        void eleventhElementGrows() {
            assertThat(ArrayListInternalsDemo.capacityHistoryDemo(11)).containsExactly(10, 15);
        }

        @Test
        @DisplayName("capacities follow old + old/2: 10, 15, 22, 33, 49, 73")
        void growthSequence() {
            assertThat(ArrayListInternalsDemo.capacityHistoryDemo(60)).containsExactly(10, 15, 22, 33, 49, 73);
        }
    }

    @Nested
    @DisplayName("2. Why add(e) is O(1) amortised")
    class AmortisedTest {

        @Test
        @DisplayName("appending up to the initial capacity copies nothing")
        void noCopiesInitially() {
            assertThat(ArrayListInternalsDemo.elementsCopiedWhileGrowingDemo(10)).isZero();
        }

        @Test
        @DisplayName("the first growth copies the 10 existing elements")
        void firstGrowthCopiesTen() {
            assertThat(ArrayListInternalsDemo.elementsCopiedWhileGrowingDemo(11)).isEqualTo(10);
        }

        @Test
        @DisplayName("total copying stays below 3 element copies per append, however large the list")
        void copiesAreLinearInTotal() {
            assertThat(ArrayListInternalsDemo.elementsCopiedWhileGrowingDemo(1_000)).isLessThan(3 * 1_000);
            assertThat(ArrayListInternalsDemo.elementsCopiedWhileGrowingDemo(1_000_000)).isLessThan(3 * 1_000_000);
        }

        @Test
        @DisplayName("the number of reallocations grows like log(n): 13 for a thousand, 30 for a million")
        void reallocationsAreLogarithmic() {
            assertThat(ArrayListInternalsDemo.reallocationsDemo(10)).isEqualTo(1);
            assertThat(ArrayListInternalsDemo.reallocationsDemo(1_000)).isEqualTo(13);
            assertThat(ArrayListInternalsDemo.reallocationsDemo(1_000_000)).isEqualTo(30);
        }
    }

    @Nested
    @DisplayName("3. Inserting and removing in the middle shifts elements")
    class ShiftingTest {

        @ParameterizedTest(name = "insert at {1} into {0} elements shifts {2}")
        @CsvSource({
                "1000, 0,    1000",
                "1000, 500,  500",
                "1000, 999,  1",
                "1000, 1000, 0"
        })
        @DisplayName("an insert shifts every element behind the index")
        void insertShifts(int size, int index, int expected) {
            assertThat(ArrayListInternalsDemo.shiftsForInsertDemo(size, index)).isEqualTo(expected);
        }

        @Test
        @DisplayName("removing the first element shifts all the others")
        void removeFirstShiftsAll() {
            assertThat(ArrayListInternalsDemo.shiftsForRemoveFirstDemo(1000)).isEqualTo(999);
        }

        @Test
        @DisplayName("appending shifts nothing")
        void appendShiftsNothing() {
            assertThat(ArrayListInternalsDemo.shiftsForAppendDemo(1000)).isZero();
        }

        @Test
        @DisplayName("n inserts at the front cost 0+1+...+(n-1) shifts in total — quadratic")
        void frontInsertsAreQuadratic() {
            assertThat(ArrayListInternalsDemo.totalShiftsForFrontInsertsDemo(100)).isEqualTo(4_950);
            assertThat(ArrayListInternalsDemo.totalShiftsForFrontInsertsDemo(1_000)).isEqualTo(499_500);
        }
    }

    @Nested
    @DisplayName("4. Fail-fast iterators (modCount)")
    class FailFastTest {

        @Test
        @DisplayName("removing inside a for-each loop throws ConcurrentModificationException")
        void removeInLoopThrows() {
            assertThatThrownBy(() -> ArrayListInternalsDemo.removeWhileIteratingDemo(Arrays.asList(1, 2, 3, 4)))
                    .isInstanceOf(ConcurrentModificationException.class);
        }

        @Test
        @DisplayName("removing the SECOND-TO-LAST element ends the loop silently — no exception, element 4 is never visited")
        void secondToLastQuirk() {
            assertThat(ArrayListInternalsDemo.removeSecondToLastWhileIteratingDemo(Arrays.asList(1, 2, 3, 4)))
                    .containsExactly(1, 2, 4);
        }

        @Test
        @DisplayName("Iterator.remove() is the safe way")
        void iteratorRemoveIsSafe() {
            assertThat(ArrayListInternalsDemo.removeEvensWithIteratorDemo(Arrays.asList(1, 2, 3, 4, 6)))
                    .containsExactly(1, 3);
        }

        @Test
        @DisplayName("the input list is never modified by the demos")
        void inputUntouched() {
            List<Integer> input = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

            ArrayListInternalsDemo.removeEvensWithIteratorDemo(input);

            assertThat(input).containsExactly(1, 2, 3, 4);
        }
    }

    @Nested
    @DisplayName("5. remove(int index) vs remove(Object)")
    class RemoveOverloadTest {

        @Test
        @DisplayName("remove(int) removes the element at that INDEX")
        void removeByIndex() {
            assertThat(ArrayListInternalsDemo.removeByIndexDemo(Arrays.asList(5, 6, 7), 1)).containsExactly(5, 7);
        }

        @Test
        @DisplayName("remove(Integer.valueOf(v)) removes the first element EQUAL to the value")
        void removeByValue() {
            assertThat(ArrayListInternalsDemo.removeByValueDemo(Arrays.asList(5, 6, 7), 7)).containsExactly(5, 6);
        }

        @Test
        @DisplayName("the same number means different things: index 2 of [5,6,7] is 7, but the value 2 is absent")
        void sameNumberDifferentMeaning() {
            assertThat(ArrayListInternalsDemo.removeByIndexDemo(Arrays.asList(5, 6, 7), 2)).containsExactly(5, 6);
            assertThat(ArrayListInternalsDemo.removeByValueDemo(Arrays.asList(5, 6, 7), 2)).containsExactly(5, 6, 7);
        }

        @Test
        @DisplayName("an index outside the list throws IndexOutOfBoundsException")
        void indexOutOfBounds() {
            assertThatThrownBy(() -> ArrayListInternalsDemo.removeByIndexDemo(Arrays.asList(5, 6, 7), 3))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @DisplayName("6. subList is a VIEW")
    class SubListTest {

        @Test
        @DisplayName("writing through the view changes the parent list")
        void writeThrough() {
            assertThat(ArrayListInternalsDemo.writeThroughSubListDemo(Arrays.asList(1, 2, 3)))
                    .containsExactly(99, 2, 3);
        }

        @Test
        @DisplayName("a structural change of the parent invalidates the view")
        void viewInvalidatedByParentChange() {
            assertThatThrownBy(() -> ArrayListInternalsDemo.useSubListAfterParentChangeDemo(Arrays.asList(1, 2, 3)))
                    .isInstanceOf(ConcurrentModificationException.class);
        }
    }
}
