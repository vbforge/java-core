package com.vbforge.collections_framework_advanced;

import com.vbforge.collections_framework_advanced.model.Employee;
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
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ComparatorsTasks — your implementations")
class ComparatorsTasksTest {

    private ComparatorsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ComparatorsTasks();
    }

    private static List<String> names(List<Employee> employees) {
        List<String> names = new ArrayList<>();
        for (Employee employee : employees) {
            names.add(employee.getName());
        }
        return names;
    }

    private static <T> List<T> sorted(List<T> input, Comparator<T> comparator) {
        List<T> copy = new ArrayList<>(input);
        copy.sort(comparator);
        return copy;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement byLengthThenAlphabetical()")
    @DisplayName("1. byLengthThenAlphabetical()")
    class ByLengthThenAlphabeticalTest {

        @Test
        @DisplayName("example from Javadoc: sorting a list")
        void javadocSorting() {
            List<String> result = sorted(Arrays.asList("pear", "fig", "apple", "kiwi", "date"),
                    tasks.byLengthThenAlphabetical());

            assertThat(result).containsExactly("fig", "date", "kiwi", "pear", "apple");
        }

        @ParameterizedTest(name = "compare({0}, {1}) has sign {2}")
        @CsvSource({
                "a,  bb, -1",
                "bb, a,   1",
                "ab, ab,  0",
                "ab, ac, -1",
                "ac, ab,  1",
                "B,  a,  -1"
        })
        @DisplayName("examples from Javadoc: sign of compare")
        void signOfCompare(String left, String right, int expectedSign) {
            assertThat(Integer.signum(tasks.byLengthThenAlphabetical().compare(left, right)))
                    .isEqualTo(expectedSign);
        }

        @Test
        @DisplayName("length wins over alphabet: a long word starting with 'a' is after a short word starting with 'z'")
        void lengthBeforeAlphabet() {
            assertThat(tasks.byLengthThenAlphabetical().compare("aaaa", "z")).isPositive();
        }

        @Test
        @DisplayName("an empty list can be sorted")
        void emptyList() {
            assertThat(sorted(Collections.<String>emptyList(), tasks.byLengthThenAlphabetical())).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sortBySalaryDescThenName()")
    @DisplayName("2. sortBySalaryDescThenName(employees)")
    class SortBySalaryDescThenNameTest {

        @Test
        @DisplayName("example from Javadoc: highest salary first, equal salaries by name")
        void javadocExample() {
            List<Employee> input = Arrays.asList(
                    new Employee("Ann", "DEV", 5000),
                    new Employee("Bob", "DEV", 7000),
                    new Employee("Cid", "DEV", 5000));

            assertThat(names(tasks.sortBySalaryDescThenName(input))).containsExactly("Bob", "Ann", "Cid");
        }

        @Test
        @DisplayName("example from Javadoc: an empty list gives an empty list")
        void emptyList() {
            assertThat(tasks.sortBySalaryDescThenName(Collections.<Employee>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("the name tie-breaker does not depend on the input order")
        void tieBreakerIndependentOfInputOrder() {
            List<Employee> input = Arrays.asList(
                    new Employee("Cid", "DEV", 5000),
                    new Employee("Ann", "OPS", 5000));

            assertThat(names(tasks.sortBySalaryDescThenName(input))).containsExactly("Ann", "Cid");
        }

        @Test
        @DisplayName("returns a NEW list and leaves the input untouched")
        void newListReturned() {
            List<Employee> input = new ArrayList<>(Arrays.asList(
                    new Employee("Ann", "DEV", 1000),
                    new Employee("Bob", "DEV", 2000)));

            List<Employee> result = tasks.sortBySalaryDescThenName(input);

            assertThat(result).isNotSameAs(input);
            assertThat(names(input)).containsExactly("Ann", "Bob");
            assertThat(names(result)).containsExactly("Bob", "Ann");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement caseInsensitiveNullsLast()")
    @DisplayName("3. caseInsensitiveNullsLast()")
    class CaseInsensitiveNullsLastTest {

        @Test
        @DisplayName("example from Javadoc: sorting a list with a null")
        void javadocSorting() {
            List<String> result = sorted(Arrays.asList("b", null, "A", "c"), tasks.caseInsensitiveNullsLast());

            assertThat(result).containsExactly("A", "b", "c", null);
        }

        @Test
        @DisplayName("example from Javadoc: strings that differ only in case are equal")
        void caseIgnored() {
            assertThat(tasks.caseInsensitiveNullsLast().compare("apple", "APPLE")).isZero();
        }

        @Test
        @DisplayName("example from Javadoc: null is greater than any string")
        void nullIsLast() {
            assertThat(tasks.caseInsensitiveNullsLast().compare(null, "a")).isPositive();
            assertThat(tasks.caseInsensitiveNullsLast().compare("a", null)).isNegative();
        }

        @Test
        @DisplayName("example from Javadoc: two nulls are equal")
        void twoNulls() {
            assertThat(tasks.caseInsensitiveNullsLast().compare(null, null)).isZero();
        }

        @Test
        @DisplayName("upper-case does NOT sort before lower-case")
        void notAsciiOrder() {
            assertThat(tasks.caseInsensitiveNullsLast().compare("B", "a")).isPositive();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement versionComparator()")
    @DisplayName("4. versionComparator()")
    class VersionComparatorTest {

        @ParameterizedTest(name = "compare({0}, {1}) has sign {2}")
        @CsvSource({
                "1.2,   1.10,  -1",
                "2,     1.9.9,  1",
                "1.0,   1,      0",
                "1.0.1, 1,      1",
                "1.10,  1.9,    1",
                "10,    9,      1",
                "1.0.0, 1,      0",
                "1.2.3, 1.2.3,  0"
        })
        @DisplayName("examples from Javadoc and more: sign of compare")
        void signOfCompare(String left, String right, int expectedSign) {
            assertThat(Integer.signum(tasks.versionComparator().compare(left, right))).isEqualTo(expectedSign);
        }

        @Test
        @DisplayName("example from Javadoc: sorting a list of versions")
        void javadocSorting() {
            List<String> result = sorted(Arrays.asList("1.10", "1.2", "1.2.1", "1"), tasks.versionComparator());

            assertThat(result).containsExactly("1", "1.2", "1.2.1", "1.10");
        }

        @Test
        @DisplayName("the comparator is antisymmetric")
        void antisymmetric() {
            Comparator<String> comparator = tasks.versionComparator();

            assertThat(Integer.signum(comparator.compare("3.1", "3.0.5")))
                    .isEqualTo(-Integer.signum(comparator.compare("3.0.5", "3.1")));
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement highestPaid()")
    @DisplayName("5. highestPaid(employees, department)")
    class HighestPaidTest {

        @Test
        @DisplayName("example from Javadoc: best-paid of one department")
        void javadocExample() {
            List<Employee> employees = Arrays.asList(
                    new Employee("Ann", "DEV", 5000),
                    new Employee("Bob", "DEV", 7000),
                    new Employee("Zoe", "QA", 9000));

            Optional<Employee> result = tasks.highestPaid(employees, "DEV");

            assertThat(result).isPresent();
            assertThat(result.get().getName()).isEqualTo("Bob");
        }

        @Test
        @DisplayName("example from Javadoc: on a tie the FIRST employee in the list wins")
        void tieKeepsFirst() {
            List<Employee> employees = Arrays.asList(
                    new Employee("Ann", "DEV", 7000),
                    new Employee("Bob", "DEV", 7000));

            assertThat(tasks.highestPaid(employees, "DEV").get().getName()).isEqualTo("Ann");
        }

        @Test
        @DisplayName("example from Javadoc: a department without employees gives empty")
        void unknownDepartment() {
            List<Employee> employees = Collections.singletonList(new Employee("Ann", "DEV", 5000));

            assertThat(tasks.highestPaid(employees, "OPS")).isEmpty();
        }

        @Test
        @DisplayName("an empty list gives empty")
        void emptyList() {
            assertThat(tasks.highestPaid(Collections.<Employee>emptyList(), "DEV")).isEmpty();
        }

        @Test
        @DisplayName("employees of other departments are ignored even when they earn more")
        void otherDepartmentsIgnored() {
            List<Employee> employees = Arrays.asList(
                    new Employee("Zoe", "QA", 9000),
                    new Employee("Ann", "DEV", 100));

            assertThat(tasks.highestPaid(employees, "DEV").get().getName()).isEqualTo("Ann");
        }
    }
}
