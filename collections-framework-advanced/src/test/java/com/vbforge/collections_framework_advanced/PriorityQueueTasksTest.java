package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PriorityQueueTasks — your implementations")
class PriorityQueueTasksTest {

    private PriorityQueueTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PriorityQueueTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement serviceOrder()")
    @DisplayName("1. serviceOrder(tickets)")
    class ServiceOrderTest {

        @Test
        @DisplayName("example from Javadoc: smaller priority number first")
        void byPriority() {
            List<Ticket> tickets = Arrays.asList(new Ticket("T1", 3), new Ticket("T2", 1), new Ticket("T3", 2));

            assertThat(tasks.serviceOrder(tickets)).containsExactly("T2", "T3", "T1");
        }

        @Test
        @DisplayName("example from Javadoc: equal priorities are ordered by id, zero is allowed")
        void tieBreakById() {
            List<Ticket> tickets = Arrays.asList(new Ticket("B", 1), new Ticket("A", 1), new Ticket("C", 0));

            assertThat(tasks.serviceOrder(tickets)).containsExactly("C", "A", "B");
        }

        @Test
        @DisplayName("example from Javadoc: no tickets give an empty list")
        void noTickets() {
            assertThat(tasks.serviceOrder(Collections.<Ticket>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("many equal priorities are ordered by id regardless of arrival order")
        void manyTies() {
            List<Ticket> tickets = Arrays.asList(
                    new Ticket("D", 5), new Ticket("B", 5), new Ticket("E", 5),
                    new Ticket("A", 5), new Ticket("C", 5));

            assertThat(tasks.serviceOrder(tickets)).containsExactly("A", "B", "C", "D", "E");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lastStoneWeight()")
    @DisplayName("2. lastStoneWeight(stones)")
    class LastStoneWeightTest {

        @ParameterizedTest(name = "stones {0} -> {1}")
        @CsvSource({
                "'2,7,4,1,8,1', 1",
                "'7,6',         1",
                "'3,3',         0",
                "'5',           5",
                "'10,4,4',      2",
                "'1,1,1',       1"
        })
        @DisplayName("examples from Javadoc and more")
        void lastStone(String csv, int expected) {
            int[] stones = Arrays.stream(csv.split(",")).mapToInt(s -> Integer.parseInt(s.trim())).toArray();

            assertThat(tasks.lastStoneWeight(stones)).isEqualTo(expected);
        }

        @Test
        @DisplayName("example from Javadoc: no stones give 0")
        void noStones() {
            assertThat(tasks.lastStoneWeight(new int[0])).isZero();
        }

        @Test
        @DisplayName("the input array is not modified")
        void inputUntouched() {
            int[] stones = {2, 7, 4};

            tasks.lastStoneWeight(stones);

            assertThat(stones).containsExactly(2, 7, 4);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement mergeSorted()")
    @DisplayName("3. mergeSorted(lists)")
    class MergeSortedTest {

        @Test
        @DisplayName("example from Javadoc: three interleaved lists")
        void interleaved() {
            List<List<Integer>> lists = Arrays.asList(
                    Arrays.asList(1, 4, 7), Arrays.asList(2, 5), Arrays.asList(3, 6, 8));

            assertThat(tasks.mergeSorted(lists)).containsExactly(1, 2, 3, 4, 5, 6, 7, 8);
        }

        @Test
        @DisplayName("example from Javadoc: duplicates are kept")
        void duplicates() {
            List<List<Integer>> lists = Arrays.asList(Arrays.asList(1, 1), Arrays.asList(1));

            assertThat(tasks.mergeSorted(lists)).containsExactly(1, 1, 1);
        }

        @Test
        @DisplayName("example from Javadoc: empty lists are skipped")
        void emptyLists() {
            List<List<Integer>> lists = Arrays.asList(
                    Collections.<Integer>emptyList(), Arrays.asList(2), Collections.<Integer>emptyList());

            assertThat(tasks.mergeSorted(lists)).containsExactly(2);
        }

        @Test
        @DisplayName("example from Javadoc: no lists give an empty list")
        void noLists() {
            assertThat(tasks.mergeSorted(Collections.<List<Integer>>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("one list is returned in the same order")
        void singleList() {
            assertThat(tasks.mergeSorted(Collections.singletonList(Arrays.asList(1, 2, 3))))
                    .containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("lists of very different lengths and negative values")
        void differentLengths() {
            List<List<Integer>> lists = Arrays.asList(
                    Arrays.asList(-5, 0, 10, 20, 30), Arrays.asList(-1), Arrays.asList(5, 6));

            assertThat(tasks.mergeSorted(lists)).containsExactly(-5, -1, 0, 5, 6, 10, 20, 30);
        }

        @Test
        @DisplayName("the input lists are not modified")
        void inputUntouched() {
            List<Integer> first = Arrays.asList(1, 3);
            List<Integer> second = Arrays.asList(2);

            tasks.mergeSorted(Arrays.asList(first, second));

            assertThat(first).containsExactly(1, 3);
            assertThat(second).containsExactly(2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement kthLargest()")
    @DisplayName("4. kthLargest(numbers, k)")
    class KthLargestTest {

        @Test
        @DisplayName("example from Javadoc: second largest")
        void secondLargest() {
            assertThat(tasks.kthLargest(new int[]{3, 2, 1, 5, 6, 4}, 2)).isEqualTo(5);
        }

        @Test
        @DisplayName("example from Javadoc: duplicates count separately")
        void duplicatesCountSeparately() {
            assertThat(tasks.kthLargest(new int[]{3, 2, 3, 1, 2, 4, 5, 5, 6}, 4)).isEqualTo(4);
        }

        @Test
        @DisplayName("example from Javadoc: a single element")
        void singleElement() {
            assertThat(tasks.kthLargest(new int[]{7}, 1)).isEqualTo(7);
        }

        @Test
        @DisplayName("k = 1 is the maximum, k = length is the minimum")
        void extremes() {
            int[] numbers = {4, -2, 9, 0};

            assertThat(tasks.kthLargest(numbers, 1)).isEqualTo(9);
            assertThat(tasks.kthLargest(numbers, 4)).isEqualTo(-2);
        }

        @Test
        @DisplayName("the input array is not modified")
        void inputUntouched() {
            int[] numbers = {3, 1, 2};

            tasks.kthLargest(numbers, 2);

            assertThat(numbers).containsExactly(3, 1, 2);
        }

        @Test
        @DisplayName("k outside 1..length is rejected")
        void invalidK() {
            assertThatThrownBy(() -> tasks.kthLargest(new int[]{1, 2}, 0))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.kthLargest(new int[]{1, 2}, 3))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
