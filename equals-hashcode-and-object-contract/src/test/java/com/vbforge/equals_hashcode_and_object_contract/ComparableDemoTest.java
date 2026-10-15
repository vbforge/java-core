package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.ComparableDemo.Person;
import com.vbforge.equals_hashcode_and_object_contract.ComparableDemo.Release;
import com.vbforge.equals_hashcode_and_object_contract.ComparableDemo.Version;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ComparableDemoTest — verifies every concept shown in {@link ComparableDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how Comparable and Comparator behave.
 */
@DisplayName("ComparableDemo — API contracts")
class ComparableDemoTest {

    // ── 1. The compareTo contract ─────────────────────────────────────────────

    @Nested
    @DisplayName("1. The compareTo contract")
    class ContractTest {

        private final Version low = new Version(1, 2, 3);
        private final Version mid = new Version(1, 9, 0);
        private final Version high = new Version(1, 10, 0);

        @Test
        @DisplayName("negative, zero and positive for less, equal and greater")
        void signs() {
            assertThat(low.compareTo(mid)).isNegative();
            assertThat(high.compareTo(mid)).isPositive();
            assertThat(low.compareTo(new Version(1, 2, 3))).isZero();
        }

        @Test
        @DisplayName("antisymmetric: sgn(x.compareTo(y)) == -sgn(y.compareTo(x))")
        void antisymmetric() {
            assertThat(Integer.signum(low.compareTo(high))).isEqualTo(-Integer.signum(high.compareTo(low)));
        }

        @Test
        @DisplayName("transitive: low < mid < high implies low < high")
        void transitive() {
            assertThat(low.compareTo(mid)).isNegative();
            assertThat(mid.compareTo(high)).isNegative();
            assertThat(low.compareTo(high)).isNegative();
        }

        @Test
        @DisplayName("numeric, not textual: 1.10.0 sorts after 1.9.0")
        void numericNotTextual() {
            List<Version> versions = new ArrayList<>(Arrays.asList(high, low, mid));
            Collections.sort(versions);
            assertThat(versions).containsExactly(low, mid, high);
            assertThat("1.10.0".compareTo("1.9.0")).isNegative();      // as Strings it is the opposite
        }

        @Test
        @DisplayName("compareTo(null) throws NullPointerException")
        void nullThrows() {
            assertThatThrownBy(() -> low.compareTo(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Collections.max uses the natural ordering")
        void max() {
            assertThat(Collections.max(Arrays.asList(low, high, mid))).isEqualTo(high);
        }
    }

    // ── 2. The subtraction trap ───────────────────────────────────────────────

    @Nested
    @DisplayName("2. The subtraction trap")
    class SubtractionTrapTest {

        @Test
        @DisplayName("a - b overflows and returns the wrong sign")
        void broken() {
            assertThat(ComparableDemo.brokenCompare(Integer.MAX_VALUE, -1)).isNegative();
            assertThat(ComparableDemo.brokenCompare(Integer.MIN_VALUE, 1)).isPositive();
        }

        @Test
        @DisplayName("Integer.compare returns the right sign")
        void safe() {
            assertThat(ComparableDemo.safeCompare(Integer.MAX_VALUE, -1)).isPositive();
            assertThat(ComparableDemo.safeCompare(Integer.MIN_VALUE, 1)).isNegative();
            assertThat(ComparableDemo.safeCompare(5, 5)).isZero();
        }

        @Test
        @DisplayName("for small numbers both agree, which hides the bug")
        void smallNumbersHideTheBug() {
            assertThat(Integer.signum(ComparableDemo.brokenCompare(3, 7)))
                    .isEqualTo(Integer.signum(ComparableDemo.safeCompare(3, 7)));
        }
    }

    // ── 3. Consistent with equals ─────────────────────────────────────────────

    @Nested
    @DisplayName("3. Consistent with equals")
    class ConsistentWithEqualsTest {

        private final BigDecimal one = new BigDecimal("1.0");
        private final BigDecimal oneAgain = new BigDecimal("1.00");

        @Test
        @DisplayName("BigDecimal 1.0 and 1.00: not equal, but compareTo is 0")
        void bigDecimal() {
            assertThat(one.equals(oneAgain)).isFalse();
            assertThat(one.compareTo(oneAgain)).isZero();
        }

        @Test
        @DisplayName("a HashSet keeps both, a TreeSet keeps one")
        void setSizes() {
            assertThat(ComparableDemo.bigDecimalSetSizes()).containsExactly(2, 1);
        }

        @Test
        @DisplayName("our Version IS consistent with equals")
        void versionIsConsistent() {
            Version a = new Version(1, 2, 3);
            Version b = new Version(1, 2, 3);
            assertThat(a.compareTo(b) == 0).isEqualTo(a.equals(b));
        }
    }

    // ── 4. Comparators ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Comparators")
    class ComparatorsTest {

        private final List<Person> people = Arrays.asList(
                new Person("Cid", 30), new Person("Ann", 30), new Person("Bob", 25));

        @Test
        @DisplayName("by age, then by name")
        void byAgeThenName() {
            assertThat(ComparableDemo.sortedBy(people, ComparableDemo.BY_AGE_THEN_NAME).toString())
                    .isEqualTo("[Bob(25), Ann(30), Cid(30)]");
        }

        @Test
        @DisplayName("reversed() reverses the whole comparator")
        void reversed() {
            assertThat(ComparableDemo.sortedBy(people, ComparableDemo.BY_AGE_THEN_NAME.reversed()).toString())
                    .isEqualTo("[Cid(30), Ann(30), Bob(25)]");
        }

        @Test
        @DisplayName("the sort works on a copy: the input list is unchanged")
        void inputUnchanged() {
            ComparableDemo.sortedBy(people, ComparableDemo.BY_AGE_THEN_NAME);
            assertThat(people.toString()).isEqualTo("[Cid(30), Ann(30), Bob(25)]");
        }

        @Test
        @DisplayName("nullsFirst puts a null name before the others")
        void nullsFirst() {
            List<Person> withNull = Arrays.asList(new Person("Ann", 1), new Person(null, 2));
            assertThat(ComparableDemo.sortedBy(withNull, ComparableDemo.BY_NAME_NULLS_FIRST).get(0).name()).isNull();
        }

        @Test
        @DisplayName("the sort is stable: equal elements keep their input order")
        void stable() {
            List<Person> sameAge = Arrays.asList(new Person("Zed", 30), new Person("Amy", 30), new Person("Max", 30));
            List<Person> sorted = ComparableDemo.sortedBy(sameAge, java.util.Comparator.comparingInt(Person::age));
            assertThat(sorted.toString()).isEqualTo("[Zed(30), Amy(30), Max(30)]");
        }
    }

    // ── 5. TreeSet / TreeMap ──────────────────────────────────────────────────

    @Nested
    @DisplayName("5. TreeSet / TreeMap")
    class TreeCollectionsTest {

        @Test
        @DisplayName("a HashSet keeps \"a\" and \"A\", a case-insensitive TreeSet keeps one")
        void setSizes() {
            assertThat(ComparableDemo.caseInsensitiveSetSizes()).containsExactly(2, 1);
        }

        @Test
        @DisplayName("a TreeMap finds a key through the comparator")
        void treeMapLookup() {
            assertThat(ComparableDemo.treeMapLookupIgnoringCase()).isEqualTo(1);
        }

        @Test
        @DisplayName("a TreeSet of Versions is sorted and rejects compareTo-duplicates")
        void treeSetOfVersions() {
            TreeSet<Version> set = new TreeSet<>();
            set.add(new Version(2, 0, 0));
            set.add(new Version(1, 0, 0));
            set.add(new Version(1, 0, 0));
            assertThat(set).hasSize(2);
            assertThat(set.first()).isEqualTo(new Version(1, 0, 0));
        }
    }

    // ── 6. Records ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Records")
    class RecordsTest {

        @Test
        @DisplayName("a record can implement Comparable by delegating to a Comparator")
        void sorts() {
            List<Release> releases = new ArrayList<>(Arrays.asList(
                    new Release(2, 0), new Release(1, 5), new Release(1, 10)));
            Collections.sort(releases);
            assertThat(releases).containsExactly(new Release(1, 5), new Release(1, 10), new Release(2, 0));
        }

        @Test
        @DisplayName("the ordering is consistent with the generated equals")
        void consistent() {
            Release a = new Release(1, 5);
            Release b = new Release(1, 5);
            assertThat(a.equals(b)).isTrue();
            assertThat(a.compareTo(b)).isZero();
            assertThat(a.equals(new Release(1, 6))).isFalse();
            assertThat(a.compareTo(new Release(1, 6))).isNotZero();
        }

        @Test
        @DisplayName("the generated toString lists the components")
        void generatedToString() {
            assertThat(new Release(1, 5).toString()).isEqualTo("Release[major=1, minor=5]");
        }
    }
}
