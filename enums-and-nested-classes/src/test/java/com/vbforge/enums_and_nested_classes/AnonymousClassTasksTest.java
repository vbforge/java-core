package com.vbforge.enums_and_nested_classes;

import com.vbforge.enums_and_nested_classes.AnonymousClassTasks.Notifier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnonymousClassTasks — your implementations")
class AnonymousClassTasksTest {

    private AnonymousClassTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AnonymousClassTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sortByLastChar()")
    @DisplayName("Task 1 — sortByLastChar(words)")
    class SortByLastCharTest {

        @Test
        @DisplayName("sorts by last character — example from the Javadoc")
        void example() {
            assertThat(tasks.sortByLastChar(Arrays.asList("apple", "kiwi", "banana", "fig")))
                    .containsExactly("banana", "apple", "fig", "kiwi");
        }

        @Test
        @DisplayName("ties on the last character use natural order")
        void tiesUseNaturalOrder() {
            assertThat(tasks.sortByLastChar(Arrays.asList("ab", "cb", "aa")))
                    .containsExactly("aa", "ab", "cb");
            assertThat(tasks.sortByLastChar(Arrays.asList("cb", "ab", "aa")))
                    .containsExactly("aa", "ab", "cb");
        }

        @Test
        @DisplayName("an empty list gives an empty list")
        void emptyList() {
            assertThat(tasks.sortByLastChar(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("a single word is returned as is")
        void singleWord() {
            assertThat(tasks.sortByLastChar(Arrays.asList("solo"))).containsExactly("solo");
        }

        @Test
        @DisplayName("returns a new list and does not modify the input")
        void returnsNewList() {
            List<String> input = new ArrayList<>(Arrays.asList("kiwi", "banana"));
            List<String> sorted = tasks.sortByLastChar(input);
            assertThat(sorted).isNotSameAs(input);
            assertThat(input).containsExactly("kiwi", "banana");
            assertThat(sorted).containsExactly("banana", "kiwi");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement fibonacciSupplier()")
    @DisplayName("Task 2 — fibonacciSupplier()")
    class FibonacciSupplierTest {

        @Test
        @DisplayName("produces 0, 1, 1, 2, 3, 5 — example from the Javadoc")
        void example() {
            Supplier<Integer> fib = tasks.fibonacciSupplier();
            List<Integer> firstSix = new ArrayList<>();
            for (int i = 0; i < 6; i++) {
                firstSix.add(fib.get());
            }
            assertThat(firstSix).containsExactly(0, 1, 1, 2, 3, 5);
        }

        @Test
        @DisplayName("continues correctly for the first ten numbers")
        void firstTen() {
            Supplier<Integer> fib = tasks.fibonacciSupplier();
            List<Integer> values = new ArrayList<>();
            for (int i = 0; i < 10; i++) {
                values.add(fib.get());
            }
            assertThat(values).containsExactly(0, 1, 1, 2, 3, 5, 8, 13, 21, 34);
        }

        @Test
        @DisplayName("every supplier starts again at 0")
        void independentSuppliers() {
            Supplier<Integer> first = tasks.fibonacciSupplier();
            Supplier<Integer> second = tasks.fibonacciSupplier();
            first.get();
            first.get();
            first.get();
            assertThat(second.get()).isEqualTo(0);
            assertThat(first.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("the 40th number (index 39) is 63245986")
        void fortiethNumber() {
            Supplier<Integer> fib = tasks.fibonacciSupplier();
            int last = 0;
            for (int i = 0; i < 40; i++) {
                last = fib.get();
            }
            assertThat(last).isEqualTo(63245986);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement multiplesOf()")
    @DisplayName("Task 3 — multiplesOf(factor, count)")
    class MultiplesOfTest {

        @ParameterizedTest(name = "multiplesOf({0}, {1}) → last supplier returns {2}")
        @CsvSource({
                "3, 4, 12",
                "5, 1, 5",
                "7, 3, 21",
                "-2, 5, -10"
        })
        @DisplayName("the last supplier returns factor * count")
        void parametrized(int factor, int count, int expectedLast) {
            List<Supplier<Integer>> suppliers = tasks.multiplesOf(factor, count);
            assertThat(suppliers).hasSize(count);
            assertThat(suppliers.get(count - 1).get()).isEqualTo(expectedLast);
        }

        @Test
        @DisplayName("suppliers return 3, 6, 9, 12 — example from the Javadoc")
        void example() {
            List<Integer> values = new ArrayList<>();
            for (Supplier<Integer> supplier : tasks.multiplesOf(3, 4)) {
                values.add(supplier.get());
            }
            assertThat(values).containsExactly(3, 6, 9, 12);
        }

        @Test
        @DisplayName("count 0 gives an empty list")
        void zeroCount() {
            assertThat(tasks.multiplesOf(2, 0)).isEmpty();
        }

        @Test
        @DisplayName("suppliers can be called in any order, repeatedly")
        void callOrderDoesNotMatter() {
            List<Supplier<Integer>> suppliers = tasks.multiplesOf(10, 3);
            assertThat(suppliers.get(2).get()).isEqualTo(30);
            assertThat(suppliers.get(0).get()).isEqualTo(10);
            assertThat(suppliers.get(2).get()).isEqualTo(30);
            assertThat(suppliers.get(1).get()).isEqualTo(20);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement emailNotifier()")
    @DisplayName("Task 4 — emailNotifier(address)")
    class EmailNotifierTest {

        @Test
        @DisplayName("channel and format — example from the Javadoc")
        void example() {
            Notifier notifier = tasks.emailNotifier("ann@example.com");
            assertThat(notifier.channel()).isEqualTo("email");
            assertThat(notifier.format("hello")).isEqualTo("[email:ann@example.com] hello");
        }

        @Test
        @DisplayName("the notifier is an instance of an anonymous class")
        void isAnonymous() {
            assertThat(tasks.emailNotifier("ann@example.com").getClass().isAnonymousClass()).isTrue();
        }

        @Test
        @DisplayName("each notifier remembers its own address")
        void notifiersAreIndependent() {
            Notifier ann = tasks.emailNotifier("ann@example.com");
            Notifier bob = tasks.emailNotifier("bob@example.com");
            assertThat(ann.format("hi")).isEqualTo("[email:ann@example.com] hi");
            assertThat(bob.format("hi")).isEqualTo("[email:bob@example.com] hi");
        }

        @Test
        @DisplayName("an empty message is still wrapped")
        void emptyMessage() {
            assertThat(tasks.emailNotifier("a@b.c").format("")).isEqualTo("[email:a@b.c] ");
        }
    }
}
