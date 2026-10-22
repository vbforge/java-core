package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("QueueTasks — your implementations")
class QueueTasksTest {

    private QueueTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new QueueTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement drainToList()")
    @DisplayName("1. drainToList(queue)")
    class DrainToListTest {

        @Test
        @DisplayName("example from Javadoc: returns all elements in order and empties the queue")
        void javadocExample() {
            Queue<String> queue = new ArrayDeque<>(Arrays.asList("a", "b", "c"));

            List<String> result = tasks.drainToList(queue);

            assertThat(result).containsExactly("a", "b", "c");
            assertThat(queue).isEmpty();
        }

        @Test
        @DisplayName("example from Javadoc: an empty queue gives an empty list")
        void emptyQueue() {
            assertThat(tasks.drainToList(new ArrayDeque<String>())).isEmpty();
        }

        @Test
        @DisplayName("a single element")
        void singleElement() {
            Queue<String> queue = new ArrayDeque<>(Arrays.asList("only"));

            assertThat(tasks.drainToList(queue)).containsExactly("only");
            assertThat(queue).isEmpty();
        }

        @Test
        @DisplayName("duplicates are kept")
        void duplicates() {
            Queue<String> queue = new ArrayDeque<>(Arrays.asList("x", "x", "y"));

            assertThat(tasks.drainToList(queue)).containsExactly("x", "x", "y");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement interleave()")
    @DisplayName("2. interleave(first, second)")
    class InterleaveTest {

        @Test
        @DisplayName("example from Javadoc: first queue is longer")
        void firstLonger() {
            Queue<String> first = new ArrayDeque<>(Arrays.asList("a", "b", "c"));
            Queue<String> second = new ArrayDeque<>(Arrays.asList("1", "2"));

            assertThat(tasks.interleave(first, second)).containsExactly("a", "1", "b", "2", "c");
        }

        @Test
        @DisplayName("example from Javadoc: second queue is longer")
        void secondLonger() {
            Queue<String> first = new ArrayDeque<>(Arrays.asList("a"));
            Queue<String> second = new ArrayDeque<>(Arrays.asList("1", "2", "3"));

            assertThat(tasks.interleave(first, second)).containsExactly("a", "1", "2", "3");
        }

        @Test
        @DisplayName("example from Javadoc: two empty queues give an empty list")
        void bothEmpty() {
            assertThat(tasks.interleave(new ArrayDeque<String>(), new ArrayDeque<String>())).isEmpty();
        }

        @Test
        @DisplayName("one empty queue gives the other one unchanged")
        void oneEmpty() {
            Queue<String> second = new ArrayDeque<>(Arrays.asList("1", "2"));

            assertThat(tasks.interleave(new ArrayDeque<String>(), second)).containsExactly("1", "2");
        }

        @Test
        @DisplayName("both queues are empty afterwards")
        void queuesAreConsumed() {
            Queue<String> first = new ArrayDeque<>(Arrays.asList("a", "b"));
            Queue<String> second = new ArrayDeque<>(Arrays.asList("1"));

            tasks.interleave(first, second);

            assertThat(first).isEmpty();
            assertThat(second).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lastSurvivor()")
    @DisplayName("3. lastSurvivor(n, k)")
    class LastSurvivorTest {

        @ParameterizedTest(name = "lastSurvivor({0}, {1}) = {2}")
        @CsvSource({
                "5, 2, 3",
                "7, 3, 4",
                "1, 5, 1",
                "6, 1, 6",
                "2, 2, 1",
                "10, 2, 5"
        })
        @DisplayName("examples from Javadoc and more")
        void survivors(int n, int k, int expected) {
            assertThat(tasks.lastSurvivor(n, k)).isEqualTo(expected);
        }

        @Test
        @DisplayName("n below 1 is rejected")
        void invalidN() {
            assertThatThrownBy(() -> tasks.lastSurvivor(0, 2)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("k below 1 is rejected")
        void invalidK() {
            assertThatThrownBy(() -> tasks.lastSurvivor(5, 0)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement recentCounts()")
    @DisplayName("4. recentCounts(timestamps, windowMs)")
    class RecentCountsTest {

        @Test
        @DisplayName("example from Javadoc: old requests expire from the window")
        void javadocExample() {
            assertThat(tasks.recentCounts(new int[]{1, 100, 3001, 3002}, 3000)).containsExactly(1, 2, 3, 3);
        }

        @Test
        @DisplayName("example from Javadoc: a zero-length window counts only the current request")
        void zeroWindow() {
            assertThat(tasks.recentCounts(new int[]{5, 6, 7}, 0)).containsExactly(1, 1, 1);
        }

        @Test
        @DisplayName("example from Javadoc: no requests give no counts")
        void noRequests() {
            assertThat(tasks.recentCounts(new int[0], 3000)).isEmpty();
        }

        @Test
        @DisplayName("the window boundary is inclusive")
        void inclusiveBoundary() {
            assertThat(tasks.recentCounts(new int[]{0, 10}, 10)).containsExactly(1, 2);
            assertThat(tasks.recentCounts(new int[]{0, 11}, 10)).containsExactly(1, 1);
        }

        @Test
        @DisplayName("a window larger than all timestamps counts everything so far")
        void hugeWindow() {
            assertThat(tasks.recentCounts(new int[]{1, 2, 3, 4}, 1_000_000)).containsExactly(1, 2, 3, 4);
        }

        @Test
        @DisplayName("a negative window is rejected")
        void negativeWindow() {
            assertThatThrownBy(() -> tasks.recentCounts(new int[]{1}, -1))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
