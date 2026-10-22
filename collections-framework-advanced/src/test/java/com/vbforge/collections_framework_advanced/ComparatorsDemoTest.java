package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ComparatorsDemo — API contracts")
class ComparatorsDemoTest {

    private static List<Employee> team() {
        return Arrays.asList(
                new Employee("Zoe", "QA", 4000),
                new Employee("Ann", "DEV", 5000),
                new Employee("Bob", "DEV", 7000),
                new Employee("Cid", "DEV", 5000),
                new Employee("Eve", "OPS", 4500));
    }

    private static List<String> names(List<Employee> employees) {
        List<String> names = new ArrayList<>();
        for (Employee employee : employees) {
            names.add(employee.getName());
        }
        return names;
    }

    @Nested
    @DisplayName("1. Natural ordering")
    class NaturalOrderTest {

        @Test
        @DisplayName("String natural order puts ALL upper-case letters before lower-case ones")
        void upperCaseFirst() {
            assertThat(ComparatorsDemo.naturalOrderDemo(Arrays.asList("b", "A", "a", "B")))
                    .containsExactly("A", "B", "a", "b");
        }

        @Test
        @DisplayName("natural order sorts numbers numerically but digits in text lexicographically")
        void lexicographicDigits() {
            assertThat(ComparatorsDemo.naturalOrderDemo(Arrays.asList("10", "9", "2")))
                    .containsExactly("10", "2", "9");
        }

        @Test
        @DisplayName("CASE_INSENSITIVE_ORDER ignores case (equal letters keep their input order)")
        void caseInsensitive() {
            assertThat(ComparatorsDemo.caseInsensitiveDemo(Arrays.asList("b", "A", "a", "B")))
                    .containsExactly("A", "a", "b", "B");
        }

        @Test
        @DisplayName("the input list is not modified — a sorted copy is returned")
        void inputUntouched() {
            List<String> input = Arrays.asList("b", "a");

            List<String> sorted = ComparatorsDemo.naturalOrderDemo(input);

            assertThat(input).containsExactly("b", "a");
            assertThat(sorted).isNotSameAs(input);
        }

        @Test
        @DisplayName("natural order cannot handle null — NullPointerException")
        void nullFails() {
            assertThatThrownBy(() -> ComparatorsDemo.naturalOrderDemo(Arrays.asList("a", null)))
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("2. A custom Comparator, and stability")
    class CustomComparatorTest {

        @Test
        @DisplayName("shorter strings come first")
        void byLength() {
            assertThat(ComparatorsDemo.byLengthStableDemo(Arrays.asList("ccc", "bb", "d")))
                    .containsExactly("d", "bb", "ccc");
        }

        @Test
        @DisplayName("sort is STABLE: equal-length strings keep their original order")
        void stable() {
            assertThat(ComparatorsDemo.byLengthStableDemo(Arrays.asList("ccc", "bb", "aa", "d")))
                    .containsExactly("d", "bb", "aa", "ccc");
        }

        @Test
        @DisplayName("an empty list stays empty")
        void emptyList() {
            assertThat(ComparatorsDemo.byLengthStableDemo(Collections.<String>emptyList())).isEmpty();
        }
    }

    @Nested
    @DisplayName("3. comparing / thenComparing")
    class ThenComparingTest {

        @Test
        @DisplayName("department A-Z, then salary high-low, then name A-Z")
        void multiKeySort() {
            assertThat(names(ComparatorsDemo.sortEmployeesDemo(team())))
                    .containsExactly("Bob", "Ann", "Cid", "Eve", "Zoe");
        }

        @Test
        @DisplayName("the third key decides between equal department and salary")
        void nameBreaksTies() {
            List<Employee> input = Arrays.asList(
                    new Employee("Cid", "DEV", 5000),
                    new Employee("Ann", "DEV", 5000));

            assertThat(names(ComparatorsDemo.sortEmployeesDemo(input))).containsExactly("Ann", "Cid");
        }

        @Test
        @DisplayName("the input list is not modified")
        void inputUntouched() {
            List<Employee> input = team();

            ComparatorsDemo.sortEmployeesDemo(input);

            assertThat(names(input)).containsExactly("Zoe", "Ann", "Bob", "Cid", "Eve");
        }
    }

    @Nested
    @DisplayName("4. reversed() — what exactly is reversed?")
    class ReversedTest {

        @Test
        @DisplayName("reversed() at the end flips the department order as well (QA, OPS, DEV)")
        void wholeChainReversed() {
            assertThat(names(ComparatorsDemo.reversedWholeChainDemo(team())))
                    .containsExactly("Zoe", "Eve", "Bob", "Ann", "Cid");
        }

        @Test
        @DisplayName("reversing only the salary comparator keeps departments A-Z")
        void onlySecondKeyReversed() {
            assertThat(names(ComparatorsDemo.reversedSecondKeyOnlyDemo(team())))
                    .containsExactly("Bob", "Ann", "Cid", "Eve", "Zoe");
        }

        @Test
        @DisplayName("the two variants produce different results for the same input")
        void variantsDiffer() {
            assertThat(names(ComparatorsDemo.reversedWholeChainDemo(team())))
                    .isNotEqualTo(names(ComparatorsDemo.reversedSecondKeyOnlyDemo(team())));
        }
    }

    @Nested
    @DisplayName("5. Null-safe comparators")
    class NullSafeTest {

        @Test
        @DisplayName("nullsLast moves nulls to the end")
        void nullsLast() {
            assertThat(ComparatorsDemo.nullsLastDemo(Arrays.asList("b", null, "a")))
                    .containsExactly("a", "b", null);
        }

        @Test
        @DisplayName("nullsFirst moves nulls to the beginning")
        void nullsFirst() {
            assertThat(ComparatorsDemo.nullsFirstDemo(Arrays.asList("b", null, "a")))
                    .containsExactly(null, "a", "b");
        }

        @Test
        @DisplayName("several nulls stay together")
        void severalNulls() {
            assertThat(ComparatorsDemo.nullsLastDemo(Arrays.asList(null, "z", null, "y")))
                    .containsExactly("y", "z", null, null);
        }
    }

    @Nested
    @DisplayName("6. The subtraction trap")
    class SubtractionTrapTest {

        @Test
        @DisplayName("a - b gives the right sign for nearby values")
        void worksForSmallValues() {
            assertThat(ComparatorsDemo.brokenCompareDemo(3, 5)).isNegative();
            assertThat(ComparatorsDemo.brokenCompareDemo(5, 3)).isPositive();
        }

        @Test
        @DisplayName("a - b OVERFLOWS: Integer.MIN_VALUE is wrongly reported as greater than 1")
        void overflows() {
            assertThat(ComparatorsDemo.brokenCompareDemo(Integer.MIN_VALUE, 1)).isPositive();
        }

        @Test
        @DisplayName("Integer.compare is correct for extreme values")
        void safeCompare() {
            assertThat(ComparatorsDemo.safeCompareDemo(Integer.MIN_VALUE, 1)).isNegative();
            assertThat(ComparatorsDemo.safeCompareDemo(Integer.MAX_VALUE, -1)).isPositive();
            assertThat(ComparatorsDemo.safeCompareDemo(7, 7)).isZero();
        }
    }

    @Nested
    @DisplayName("7. max / min with a Comparator")
    class MaxMinTest {

        @Test
        @DisplayName("max finds the highest salary")
        void topEarner() {
            assertThat(ComparatorsDemo.topEarnerDemo(team()).getName()).isEqualTo("Bob");
        }

        @Test
        @DisplayName("min finds the lowest salary")
        void lowestEarner() {
            assertThat(ComparatorsDemo.lowestEarnerDemo(team()).getName()).isEqualTo("Zoe");
        }

        @Test
        @DisplayName("a single employee is both the highest and the lowest")
        void singleEmployee() {
            List<Employee> only = Collections.singletonList(new Employee("Solo", "DEV", 1));

            assertThat(ComparatorsDemo.topEarnerDemo(only)).isSameAs(only.get(0));
            assertThat(ComparatorsDemo.lowestEarnerDemo(only)).isSameAs(only.get(0));
        }

        @Test
        @DisplayName("an empty list throws NoSuchElementException")
        void emptyList() {
            assertThatThrownBy(() -> ComparatorsDemo.topEarnerDemo(Collections.<Employee>emptyList()))
                    .isInstanceOf(NoSuchElementException.class);
        }
    }
}
