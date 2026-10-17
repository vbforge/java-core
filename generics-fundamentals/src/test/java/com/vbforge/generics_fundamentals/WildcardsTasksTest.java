package com.vbforge.generics_fundamentals;

import com.vbforge.generics_fundamentals.WildcardsDemo.Apple;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("WildcardsTasks — your implementations")
class WildcardsTasksTest {

    private WildcardsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new WildcardsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumOfSquares()")
    @DisplayName("Task 1 — sumOfSquares(numbers)")
    class SumOfSquaresTest {

        @Test
        @DisplayName("example: List<Integer>")
        void integers() {
            assertThat(tasks.sumOfSquares(Arrays.asList(1, 2, 3))).isEqualTo(14.0);
        }

        @Test
        @DisplayName("example: List<Double>")
        void doubles() {
            assertThat(tasks.sumOfSquares(Arrays.asList(1.5, 2.0))).isEqualTo(6.25);
        }

        @Test
        @DisplayName("example: empty collection gives 0.0")
        void empty() {
            assertThat(tasks.sumOfSquares(Collections.<Integer>emptyList())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("accepts a List<Number> with mixed subtypes")
        void mixed() {
            List<Number> mixed = Arrays.<Number>asList(1, 2L, 0.5);

            assertThat(tasks.sumOfSquares(mixed)).isEqualTo(5.25);
        }

        @Test
        @DisplayName("negative numbers square to positive")
        void negatives() {
            assertThat(tasks.sumOfSquares(Arrays.asList(-3, 3))).isEqualTo(18.0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement addRange()")
    @DisplayName("Task 2 — addRange(target, from, toInclusive)")
    class AddRangeTest {

        @Test
        @DisplayName("example: List<Integer> gets 1, 2, 3")
        void integerList() {
            List<Integer> target = new ArrayList<>();

            tasks.addRange(target, 1, 3);

            assertThat(target).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("example: a List<Number> is accepted")
        void numberList() {
            List<Number> target = new ArrayList<>();

            tasks.addRange(target, 5, 5);

            assertThat(target).containsExactly(5);
        }

        @Test
        @DisplayName("example: a List<Object> is accepted")
        void objectList() {
            List<Object> target = new ArrayList<>();

            tasks.addRange(target, 1, 2);

            assertThat(target).containsExactly(1, 2);
        }

        @Test
        @DisplayName("example: from greater than toInclusive throws and leaves the list untouched")
        void invalidRange() {
            List<Integer> target = new ArrayList<>(Arrays.asList(9));

            assertThatThrownBy(() -> tasks.addRange(target, 3, 1)).isInstanceOf(IllegalArgumentException.class);
            assertThat(target).containsExactly(9);
        }

        @Test
        @DisplayName("values are appended after existing elements")
        void appendsToExisting() {
            List<Integer> target = new ArrayList<>(Arrays.asList(100));

            tasks.addRange(target, 1, 2);

            assertThat(target).containsExactly(100, 1, 2);
        }

        @Test
        @DisplayName("negative ranges work")
        void negativeRange() {
            List<Integer> target = new ArrayList<>();

            tasks.addRange(target, -2, 1);

            assertThat(target).containsExactly(-2, -1, 0, 1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement transferMatching()")
    @DisplayName("Task 3 — transferMatching(source, destination, filter)")
    class TransferMatchingTest {

        @Test
        @DisplayName("example: even Integers go into a List<Number>")
        void evenNumbers() {
            List<Number> numbers = new ArrayList<>();

            int added = tasks.transferMatching(Arrays.asList(1, 2, 3, 4), numbers, n -> n % 2 == 0);

            assertThat(added).isEqualTo(2);
            assertThat(numbers).containsExactly(2, 4);
        }

        @Test
        @DisplayName("example: nothing matches, nothing is added")
        void nothingMatches() {
            List<Object> objects = new ArrayList<>();

            int added = tasks.transferMatching(Arrays.asList("a", "bb"), objects, s -> s.length() > 5);

            assertThat(added).isEqualTo(0);
            assertThat(objects).isEmpty();
        }

        @Test
        @DisplayName("example: empty source adds nothing")
        void emptySource() {
            List<Object> destination = new ArrayList<>();

            int added = tasks.transferMatching(Collections.<Integer>emptyList(), destination, x -> true);

            assertThat(added).isEqualTo(0);
        }

        @Test
        @DisplayName("order is kept and existing destination elements stay in front")
        void orderAndAppend() {
            List<Object> destination = new ArrayList<>(Arrays.<Object>asList("first"));

            tasks.transferMatching(Arrays.asList("b", "a", "c"), destination, s -> !s.equals("a"));

            assertThat(destination).containsExactly("first", "b", "c");
        }

        @Test
        @DisplayName("the source list is not modified")
        void sourceUnchanged() {
            List<Integer> source = new ArrayList<>(Arrays.asList(1, 2, 3));

            tasks.transferMatching(source, new ArrayList<Number>(), n -> true);

            assertThat(source).containsExactly(1, 2, 3);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countNonNull()")
    @DisplayName("Task 4 — countNonNull(list)")
    class CountNonNullTest {

        @Test
        @DisplayName("example: List<String> with a null")
        void strings() {
            assertThat(tasks.countNonNull(Arrays.asList("a", null, "b"))).isEqualTo(2);
        }

        @Test
        @DisplayName("example: List<Integer> without nulls")
        void integers() {
            assertThat(tasks.countNonNull(Arrays.asList(1, 2, 3))).isEqualTo(3);
        }

        @Test
        @DisplayName("example: only nulls")
        void onlyNulls() {
            assertThat(tasks.countNonNull(Arrays.asList(null, null))).isEqualTo(0);
        }

        @Test
        @DisplayName("example: empty list")
        void emptyList() {
            assertThat(tasks.countNonNull(Collections.emptyList())).isEqualTo(0);
        }

        @Test
        @DisplayName("a null at the start or end is skipped")
        void nullsAtEnds() {
            assertThat(tasks.countNonNull(Arrays.asList(null, "x", null))).isEqualTo(1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement reverseInPlace()")
    @DisplayName("Task 5 — reverseInPlace(list)")
    class ReverseInPlaceTest {

        @Test
        @DisplayName("example: odd number of Strings")
        void oddLength() {
            List<String> list = new ArrayList<>(Arrays.asList("a", "b", "c"));

            tasks.reverseInPlace(list);

            assertThat(list).containsExactly("c", "b", "a");
        }

        @Test
        @DisplayName("example: two Integers")
        void twoElements() {
            List<Integer> list = new ArrayList<>(Arrays.asList(1, 2));

            tasks.reverseInPlace(list);

            assertThat(list).containsExactly(2, 1);
        }

        @Test
        @DisplayName("example: empty list stays empty")
        void emptyList() {
            List<String> list = new ArrayList<>();

            tasks.reverseInPlace(list);

            assertThat(list).isEmpty();
        }

        @Test
        @DisplayName("a single element stays in place")
        void singleElement() {
            List<String> list = new ArrayList<>(Arrays.asList("only"));

            tasks.reverseInPlace(list);

            assertThat(list).containsExactly("only");
        }

        @Test
        @DisplayName("even number of elements")
        void evenLength() {
            List<Integer> list = new ArrayList<>(Arrays.asList(1, 2, 3, 4));

            tasks.reverseInPlace(list);

            assertThat(list).containsExactly(4, 3, 2, 1);
        }

        @Test
        @DisplayName("reversing twice restores the original order")
        void twiceIsIdentity() {
            List<String> list = new ArrayList<>(Arrays.asList("x", "y", "z"));

            tasks.reverseInPlace(list);
            tasks.reverseInPlace(list);

            assertThat(list).containsExactly("x", "y", "z");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement maxOf()")
    @DisplayName("Task 6 — maxOf(items)")
    class MaxOfTest {

        @Test
        @DisplayName("example: largest Integer")
        void integers() {
            assertThat(tasks.maxOf(Arrays.asList(3, 9, 4))).isEqualTo(9);
        }

        @Test
        @DisplayName("example: Apples, which are only Comparable<Fruit>")
        void apples() {
            Apple heavier = new Apple(180);
            List<Apple> apples = Arrays.asList(new Apple(120), heavier);

            Apple result = tasks.maxOf(apples);

            assertThat(result).isSameAs(heavier);
        }

        @Test
        @DisplayName("example: largest String")
        void strings() {
            assertThat(tasks.maxOf(Arrays.asList("b", "a"))).isEqualTo("b");
        }

        @Test
        @DisplayName("example: empty collection throws IllegalArgumentException")
        void emptyCollection() {
            assertThatThrownBy(() -> tasks.maxOf(Collections.<Integer>emptyList()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "max of [{0}, {1}, {2}] is {3}")
        @CsvSource({"1, 2, 3, 3", "3, 2, 1, 3", "2, 3, 1, 3", "-1, -2, -3, -1"})
        @DisplayName("the position of the maximum does not matter")
        void parametrized(int a, int b, int c, int expected) {
            assertThat(tasks.maxOf(Arrays.asList(a, b, c))).isEqualTo(expected);
        }

        @Test
        @DisplayName("a single element is the maximum")
        void singleElement() {
            assertThat(tasks.maxOf(Collections.singleton("only"))).isEqualTo("only");
        }
    }
}
