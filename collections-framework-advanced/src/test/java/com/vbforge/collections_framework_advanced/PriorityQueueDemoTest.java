package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PriorityQueueDemo — API contracts")
class PriorityQueueDemoTest {

    @Nested
    @DisplayName("1. Natural order: smallest first")
    class NaturalOrderTest {

        @Test
        @DisplayName("polling until empty gives ascending order")
        void ascending() {
            assertThat(PriorityQueueDemo.pollOrderDemo(Arrays.asList(5, 3, 8, 1))).containsExactly(1, 3, 5, 8);
        }

        @Test
        @DisplayName("duplicates are kept")
        void duplicates() {
            assertThat(PriorityQueueDemo.pollOrderDemo(Arrays.asList(2, 1, 2))).containsExactly(1, 2, 2);
        }

        @Test
        @DisplayName("an empty input gives an empty result")
        void emptyInput() {
            assertThat(PriorityQueueDemo.pollOrderDemo(Collections.<Integer>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("negative numbers are ordered correctly")
        void negatives() {
            assertThat(PriorityQueueDemo.pollOrderDemo(Arrays.asList(0, -5, 3, -1))).containsExactly(-5, -1, 0, 3);
        }
    }

    @Nested
    @DisplayName("2. Gotcha: iteration order is NOT sorted")
    class IterationOrderTest {

        private final List<Integer> input = Arrays.asList(5, 3, 8, 1);

        @Test
        @DisplayName("iteration contains exactly the same elements")
        void sameElements() {
            assertThat(PriorityQueueDemo.iterationOrderDemo(input)).containsExactlyInAnyOrder(5, 3, 8, 1);
        }

        @Test
        @DisplayName("the first element visited is the minimum — that is the only guarantee")
        void headIsMinimum() {
            assertThat(PriorityQueueDemo.iterationOrderDemo(input).get(0)).isEqualTo(1);
        }

        @Test
        @DisplayName("but the remaining elements are not in sorted order")
        void restIsNotSorted() {
            List<Integer> sorted = new ArrayList<>(input);
            Collections.sort(sorted);

            assertThat(PriorityQueueDemo.iterationOrderDemo(input)).isNotEqualTo(sorted);
        }

        @Test
        @DisplayName("polling, in contrast, IS sorted")
        void pollingIsSorted() {
            assertThat(PriorityQueueDemo.pollOrderDemo(input)).containsExactly(1, 3, 5, 8);
        }
    }

    @Nested
    @DisplayName("3. A Comparator changes \"smallest\": max-heap")
    class MaxHeapTest {

        @Test
        @DisplayName("reversed natural order gives descending order")
        void descending() {
            assertThat(PriorityQueueDemo.maxHeapDemo(Arrays.asList(5, 3, 8, 1))).containsExactly(8, 5, 3, 1);
        }

        @Test
        @DisplayName("duplicates are kept")
        void duplicates() {
            assertThat(PriorityQueueDemo.maxHeapDemo(Arrays.asList(4, 9, 4))).containsExactly(9, 4, 4);
        }

        @Test
        @DisplayName("an empty input gives an empty result")
        void emptyInput() {
            assertThat(PriorityQueueDemo.maxHeapDemo(Collections.<Integer>emptyList())).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. Domain objects")
    class DomainObjectsTest {

        @Test
        @DisplayName("tickets are served by priority number, smallest first")
        void servedByPriority() {
            List<Ticket> tickets = Arrays.asList(new Ticket("T1", 3), new Ticket("T2", 1), new Ticket("T3", 2));

            assertThat(PriorityQueueDemo.ticketOrderDemo(tickets)).containsExactly("T2", "T3", "T1");
        }

        @Test
        @DisplayName("a single ticket is served as is")
        void singleTicket() {
            assertThat(PriorityQueueDemo.ticketOrderDemo(Collections.singletonList(new Ticket("X", 9))))
                    .containsExactly("X");
        }
    }

    @Nested
    @DisplayName("5. Gotcha: ties are not first-come-first-served")
    class TiesTest {

        @Test
        @DisplayName("equal priorities do NOT come out in arrival order")
        void tiesAreReordered() {
            assertThat(PriorityQueueDemo.unstableTiesDemo(Arrays.asList("a", "b", "c")))
                    .isNotEqualTo(Arrays.asList("a", "b", "c"));
        }

        @Test
        @DisplayName("no element is lost, only the order of equals changes")
        void sameElements() {
            assertThat(PriorityQueueDemo.unstableTiesDemo(Arrays.asList("a", "b", "c")))
                    .containsExactlyInAnyOrder("a", "b", "c");
        }

        @Test
        @DisplayName("an arrival-sequence tie-breaker restores first-come-first-served")
        void stableWithSequence() {
            assertThat(PriorityQueueDemo.stableTiesDemo(Arrays.asList("a", "b", "c")))
                    .containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("the stable version also keeps longer arrival sequences intact")
        void stableLongerSequence() {
            List<String> arrivals = Arrays.asList("p", "q", "r", "s", "t", "u", "v");

            assertThat(PriorityQueueDemo.stableTiesDemo(arrivals)).isEqualTo(arrivals);
        }
    }

    @Nested
    @DisplayName("6. Null and non-comparable elements")
    class InvalidElementsTest {

        @Test
        @DisplayName("null is rejected with NullPointerException")
        void nullRejected() {
            assertThatThrownBy(PriorityQueueDemo::offerNullDemo).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("elements that are not Comparable cause ClassCastException")
        void nonComparableRejected() {
            assertThatThrownBy(PriorityQueueDemo::addNonComparableDemo).isInstanceOf(ClassCastException.class);
        }
    }
}
