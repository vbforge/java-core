package com.vbforge.collections_internals_and_big_o;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ArrayListInternalsTasks — your implementations")
class ArrayListInternalsTasksTest {

    private ArrayListInternalsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ArrayListInternalsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement grow()")
    @DisplayName("1. grow(oldCapacity, minCapacity)")
    class GrowTest {

        @ParameterizedTest(name = "grow({0}, {1}) = {2}")
        @CsvSource({
                "0,   1,   10",
                "10,  11,  15",
                "15,  16,  22",
                "22,  23,  33",
                "33,  34,  49",
                "1,   2,   2",
                "10,  100, 100",
                "0,   50,  50",
                "100, 101, 150"
        })
        @DisplayName("examples from Javadoc and more")
        void growthRule(int oldCapacity, int minCapacity, int expected) {
            assertThat(tasks.grow(oldCapacity, minCapacity)).isEqualTo(expected);
        }

        @Test
        @DisplayName("an odd capacity rounds the 50 % down: 3 -> 4")
        void oddCapacityRoundsDown() {
            assertThat(tasks.grow(3, 4)).isEqualTo(4);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement reallocations()")
    @DisplayName("2. reallocations(adds)")
    class ReallocationsTest {

        @ParameterizedTest(name = "{0} appends -> {1} allocations")
        @CsvSource({
                "0,       0",
                "1,       1",
                "10,      1",
                "11,      2",
                "15,      2",
                "16,      3",
                "22,      3",
                "34,      5",
                "1000,    13",
                "1000000, 30"
        })
        @DisplayName("examples from Javadoc and more")
        void allocations(int adds, int expected) {
            assertThat(tasks.reallocations(adds)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement insertAt()")
    @DisplayName("3. insertAt(elements, size, index, value)")
    class InsertAtTest {

        @Test
        @DisplayName("example from Javadoc: free room — same array, elements shifted")
        void reusesArrayWhenThereIsRoom() {
            int[] elements = {1, 2, 4, 0, 0};

            int[] result = tasks.insertAt(elements, 3, 2, 3);

            assertThat(result).isSameAs(elements);
            assertThat(result).containsExactly(1, 2, 3, 4, 0);
        }

        @Test
        @DisplayName("example from Javadoc: full array — a NEW, bigger array is returned")
        void growsWhenFull() {
            int[] elements = {1, 2};

            int[] result = tasks.insertAt(elements, 2, 0, 9);

            assertThat(result).isNotSameAs(elements);
            assertThat(result).hasSize(3);
            assertThat(result).containsExactly(9, 1, 2);
        }

        @Test
        @DisplayName("example from Javadoc: an empty array grows to capacity 10")
        void emptyArrayGrowsToTen() {
            int[] result = tasks.insertAt(new int[0], 0, 0, 7);

            assertThat(result).hasSize(10);
            assertThat(result[0]).isEqualTo(7);
        }

        @Test
        @DisplayName("a full array of 10 grows to 15")
        void fullTenGrowsToFifteen() {
            int[] elements = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};

            int[] result = tasks.insertAt(elements, 10, 10, 11);

            assertThat(result).hasSize(15);
            assertThat(Arrays.copyOf(result, 11)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11);
        }

        @Test
        @DisplayName("inserting at the end (index == size) shifts nothing")
        void appendAtEnd() {
            int[] elements = {5, 6, 0, 0};

            int[] result = tasks.insertAt(elements, 2, 2, 7);

            assertThat(result).containsExactly(5, 6, 7, 0);
        }

        @Test
        @DisplayName("an index beyond size is rejected")
        void indexTooLarge() {
            assertThatThrownBy(() -> tasks.insertAt(new int[]{1, 0, 0}, 1, 2, 5))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("a negative index is rejected")
        void negativeIndex() {
            assertThatThrownBy(() -> tasks.insertAt(new int[]{1, 0, 0}, 1, -1, 5))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement removeAt()")
    @DisplayName("4. removeAt(elements, size, index)")
    class RemoveAtTest {

        @Test
        @DisplayName("example from Javadoc: removing from the middle")
        void middle() {
            int[] elements = {10, 20, 30, 40};

            assertThat(tasks.removeAt(elements, 4, 1)).isEqualTo(20);
            assertThat(elements).containsExactly(10, 30, 40, 0);
        }

        @Test
        @DisplayName("example from Javadoc: removing the last element")
        void last() {
            int[] elements = {10, 20, 30, 40};

            assertThat(tasks.removeAt(elements, 4, 3)).isEqualTo(40);
            assertThat(elements).containsExactly(10, 20, 30, 0);
        }

        @Test
        @DisplayName("example from Javadoc: removing the only element leaves all slots at 0")
        void onlyElement() {
            int[] elements = {5, 0, 0};

            assertThat(tasks.removeAt(elements, 1, 0)).isEqualTo(5);
            assertThat(elements).containsExactly(0, 0, 0);
        }

        @Test
        @DisplayName("slots behind the used part are not touched")
        void unusedSlotsUntouched() {
            int[] elements = {1, 2, 3, 99};

            tasks.removeAt(elements, 3, 0);

            assertThat(elements).containsExactly(2, 3, 0, 99);
        }

        @Test
        @DisplayName("an index equal to size is rejected")
        void indexEqualToSize() {
            assertThatThrownBy(() -> tasks.removeAt(new int[]{1, 2, 3}, 2, 2))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }

        @Test
        @DisplayName("a negative index is rejected")
        void negativeIndex() {
            assertThatThrownBy(() -> tasks.removeAt(new int[]{1, 2, 3}, 3, -1))
                    .isInstanceOf(IndexOutOfBoundsException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement totalShiftsForFrontInserts()")
    @DisplayName("5. totalShiftsForFrontInserts(count)")
    class TotalShiftsTest {

        @ParameterizedTest(name = "{0} front inserts -> {1} shifts")
        @CsvSource({
                "0,      0",
                "1,      0",
                "2,      1",
                "5,      10",
                "100,    4950",
                "1000,   499500",
                "100000, 4999950000"
        })
        @DisplayName("examples from Javadoc and more (the last one needs a long)")
        void totals(int count, long expected) {
            assertThat(tasks.totalShiftsForFrontInserts(count)).isEqualTo(expected);
        }
    }
}
