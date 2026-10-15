package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.ComparableTasks.Person;
import com.vbforge.equals_hashcode_and_object_contract.ComparableTasks.Version;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ComparableTasksTest — tests for every method in {@link ComparableTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ComparableTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ComparableTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ComparableTasks — your implementations")
class ComparableTasksTest {

    private ComparableTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ComparableTasks();
    }

    // ── Task 1: Version.compareTo ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Version.compareTo()")
    @DisplayName("Task 1 — Version.compareTo()")
    class VersionCompareToTest {

        @ParameterizedTest(name = "{0}.{1}.{2} vs {3}.{4}.{5} → sign {6}")
        @CsvSource({
                "1, 2, 3,  1, 2, 4,  -1",
                "2, 0, 0,  1, 9, 9,   1",
                "1, 2, 3,  1, 2, 3,   0",
                "1, 10, 0, 1, 9, 0,   1",
                "1, 2, 9,  1, 3, 0,  -1",
                "0, 0, 1,  0, 0, 0,   1",
        })
        @DisplayName("parametrized: the sign of the comparison")
        void signs(int a1, int a2, int a3, int b1, int b2, int b3, int expectedSign) {
            int result = new Version(a1, a2, a3).compareTo(new Version(b1, b2, b3));
            assertThat(Integer.signum(result)).isEqualTo(expectedSign);
        }

        @Test
        @DisplayName("no overflow for extreme values")
        void noOverflow() {
            assertThat(new Version(Integer.MIN_VALUE, 0, 0).compareTo(new Version(1, 0, 0))).isNegative();
            assertThat(new Version(Integer.MAX_VALUE, 0, 0).compareTo(new Version(-1, 0, 0))).isPositive();
            assertThat(new Version(1, Integer.MIN_VALUE, 0).compareTo(new Version(1, 1, 0))).isNegative();
            assertThat(new Version(1, 1, Integer.MIN_VALUE).compareTo(new Version(1, 1, 1))).isNegative();
        }

        @Test
        @DisplayName("antisymmetric")
        void antisymmetric() {
            Version a = new Version(1, 2, 3);
            Version b = new Version(1, 3, 0);
            assertThat(Integer.signum(a.compareTo(b))).isEqualTo(-Integer.signum(b.compareTo(a)));
        }

        @Test
        @DisplayName("sorting a list gives numeric order")
        void sorting() {
            List<Version> versions = new ArrayList<>(Arrays.asList(
                    new Version(1, 10, 0), new Version(1, 2, 3), new Version(1, 9, 0), new Version(0, 99, 99)));
            Collections.sort(versions);
            assertThat(versions.toString()).isEqualTo("[0.99.99, 1.2.3, 1.9.0, 1.10.0]");
        }

        @Test
        @DisplayName("compareTo(null) → NullPointerException")
        void nullThrows() {
            assertThatThrownBy(() -> new Version(1, 0, 0).compareTo(null)).isInstanceOf(NullPointerException.class);
        }
    }

    // ── Task 2: byAgeThenName ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement byAgeThenName()")
    @DisplayName("Task 2 — byAgeThenName()")
    class ByAgeThenNameTest {

        @Test
        @DisplayName("age first, name as the tie-breaker")
        void sorts() {
            List<Person> people = new ArrayList<>(Arrays.asList(
                    new Person("Cid", 30), new Person("Ann", 30), new Person("Bob", 25)));
            people.sort(tasks.byAgeThenName());
            assertThat(people.toString()).isEqualTo("[Bob(25), Ann(30), Cid(30)]");
        }

        @Test
        @DisplayName("same name, different age → younger first")
        void sameName() {
            List<Person> people = new ArrayList<>(Arrays.asList(new Person("Bob", 25), new Person("Bob", 20)));
            people.sort(tasks.byAgeThenName());
            assertThat(people.toString()).isEqualTo("[Bob(20), Bob(25)]");
        }

        @Test
        @DisplayName("identical people compare as equal")
        void equalPeople() {
            assertThat(tasks.byAgeThenName().compare(new Person("Ann", 30), new Person("Ann", 30))).isZero();
        }

        @Test
        @DisplayName("age is compared numerically, not as text (9 < 10)")
        void numericAge() {
            assertThat(tasks.byAgeThenName().compare(new Person("A", 9), new Person("A", 10))).isNegative();
        }

        @Test
        @DisplayName("reversed() flips the order")
        void reversed() {
            assertThat(tasks.byAgeThenName().reversed().compare(new Person("A", 1), new Person("B", 2))).isPositive();
        }
    }

    // ── Task 3: byNameNullsLast ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement byNameNullsLast()")
    @DisplayName("Task 3 — byNameNullsLast()")
    class ByNameNullsLastTest {

        @Test
        @DisplayName("names ascending, null names at the end")
        void sorts() {
            List<Person> people = new ArrayList<>(Arrays.asList(
                    new Person("Cid", 1), new Person(null, 2), new Person("Ann", 3)));
            people.sort(tasks.byNameNullsLast());
            assertThat(people.toString()).isEqualTo("[Ann(3), Cid(1), null(2)]");
        }

        @Test
        @DisplayName("two null names compare as equal")
        void bothNull() {
            assertThat(tasks.byNameNullsLast().compare(new Person(null, 1), new Person(null, 2))).isZero();
        }

        @Test
        @DisplayName("a name comes before null")
        void nameBeforeNull() {
            assertThat(tasks.byNameNullsLast().compare(new Person("Ann", 1), new Person(null, 1))).isNegative();
            assertThat(tasks.byNameNullsLast().compare(new Person(null, 1), new Person("Ann", 1))).isPositive();
        }

        @Test
        @DisplayName("equal names compare as equal (age is ignored)")
        void equalNames() {
            assertThat(tasks.byNameNullsLast().compare(new Person("Ann", 1), new Person("Ann", 99))).isZero();
        }
    }

    // ── Task 4: safeCompare ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement safeCompare()")
    @DisplayName("Task 4 — safeCompare()")
    class SafeCompareTest {

        @ParameterizedTest(name = "safeCompare({0}, {1}) → {2}")
        @CsvSource({
                "1,           2,           -1",
                "2,           2,            0",
                "3,           2,            1",
                "2147483647, -1,            1",
                "-2147483648, 1,           -1",
                "-2147483648, 2147483647,  -1",
                "2147483647, -2147483648,   1",
        })
        @DisplayName("parametrized: exactly -1, 0 or 1")
        void results(int a, int b, int expected) {
            assertThat(tasks.safeCompare(a, b)).isEqualTo(expected);
        }
    }

    // ── Task 5: isConsistentWithEquals ────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isConsistentWithEquals()")
    @DisplayName("Task 5 — isConsistentWithEquals()")
    class IsConsistentTest {

        @Test
        @DisplayName("equal Integers → true")
        void equalIntegers() {
            assertThat(tasks.isConsistentWithEquals(1, 1)).isTrue();
        }

        @Test
        @DisplayName("different Integers → true (not 0 and not equal)")
        void differentIntegers() {
            assertThat(tasks.isConsistentWithEquals(1, 2)).isTrue();
        }

        @Test
        @DisplayName("Strings → true")
        void strings() {
            assertThat(tasks.isConsistentWithEquals("a", "a")).isTrue();
            assertThat(tasks.isConsistentWithEquals("a", "b")).isTrue();
        }

        @Test
        @DisplayName("BigDecimal 1.0 vs 1.00 → false")
        void bigDecimal() {
            assertThat(tasks.isConsistentWithEquals(new BigDecimal("1.0"), new BigDecimal("1.00"))).isFalse();
        }

        @Test
        @DisplayName("BigDecimal with the same scale → true")
        void bigDecimalSameScale() {
            assertThat(tasks.isConsistentWithEquals(new BigDecimal("1.0"), new BigDecimal("1.0"))).isTrue();
        }

        @Test
        @DisplayName("Version is consistent (also needs Task 1)")
        void version() {
            assertThat(tasks.isConsistentWithEquals(new Version(1, 2, 3), new Version(1, 2, 3))).isTrue();
            assertThat(tasks.isConsistentWithEquals(new Version(1, 2, 3), new Version(1, 2, 4))).isTrue();
        }
    }

    // ── Task 6: distinctSorted ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement distinctSorted()")
    @DisplayName("Task 6 — distinctSorted()")
    class DistinctSortedTest {

        @Test
        @DisplayName("Strings: sorted, without duplicates")
        void strings() {
            assertThat(tasks.distinctSorted(Arrays.asList("b", "a", "b"))).containsExactly("a", "b");
        }

        @Test
        @DisplayName("Integers: sorted, without duplicates")
        void integers() {
            assertThat(tasks.distinctSorted(Arrays.asList(3, 1, 2, 1))).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("an empty input gives an empty list")
        void empty() {
            assertThat(tasks.distinctSorted(Collections.<Integer>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("duplicates are decided by compareTo: 1.0 and 1.00 are one element")
        void bigDecimals() {
            List<BigDecimal> result = tasks.distinctSorted(Arrays.asList(
                    new BigDecimal("1.0"), new BigDecimal("1.00"), new BigDecimal("2")));
            assertThat(result).hasSize(2);
            assertThat(result.get(0).compareTo(new BigDecimal("1"))).isZero();
            assertThat(result.get(1).compareTo(new BigDecimal("2"))).isZero();
        }

        @Test
        @DisplayName("the input collection is not modified")
        void inputUnchanged() {
            List<Integer> input = new ArrayList<>(Arrays.asList(3, 1, 3));
            tasks.distinctSorted(input);
            assertThat(input).containsExactly(3, 1, 3);
        }

        @Test
        @DisplayName("the result is a list in ascending order (also works for Versions, needs Task 1)")
        void versions() {
            List<Version> result = tasks.distinctSorted(Arrays.asList(
                    new Version(1, 10, 0), new Version(1, 9, 0), new Version(1, 9, 0)));
            assertThat(result.toString()).isEqualTo("[1.9.0, 1.10.0]");
        }
    }
}
