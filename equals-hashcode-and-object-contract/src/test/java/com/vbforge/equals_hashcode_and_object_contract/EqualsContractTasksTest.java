package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.CaseInsensitiveText;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.GoodEquals;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.Measurement;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.Name;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.NoEquals;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.OverloadedEquals;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.Point;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractTasks.UnorderedPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EqualsContractTasksTest — tests for every method in {@link EqualsContractTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code EqualsContractTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=EqualsContractTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("EqualsContractTasks — your implementations")
class EqualsContractTasksTest {

    private EqualsContractTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new EqualsContractTasks();
    }

    // ── Task 1: Point.equals ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Point.equals()")
    @DisplayName("Task 1 — Point.equals()")
    class PointEqualsTest {

        @ParameterizedTest(name = "Point(1, 2) vs Point({0}, {1}) → {2}")
        @CsvSource({
                "1, 2, true",
                "2, 1, false",
                "1, 3, false",
                "0, 2, false",
                "-1, -2, false",
        })
        @DisplayName("parametrized: comparison with Point(1, 2)")
        void comparison(int x, int y, boolean expected) {
            assertThat(new Point(1, 2).equals(new Point(x, y))).isEqualTo(expected);
        }

        @Test
        @DisplayName("null → false, never an exception")
        void nullArgument() {
            assertThat(new Point(1, 2).equals(null)).isFalse();
        }

        @Test
        @DisplayName("another type → false")
        void otherType() {
            assertThat(new Point(1, 2).equals("1,2")).isFalse();
        }

        @Test
        @DisplayName("reflexive and symmetric")
        void reflexiveAndSymmetric() {
            Point a = new Point(5, 6);
            Point b = new Point(5, 6);
            assertThat(a.equals(a)).isTrue();
            assertThat(a.equals(b)).isEqualTo(b.equals(a)).isTrue();
        }
    }

    // ── Task 2: Name.equals ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Name.equals()")
    @DisplayName("Task 2 — Name.equals()")
    class NameEqualsTest {

        @Test
        @DisplayName("ignores letter case in both parts")
        void ignoresCase() {
            assertThat(new Name("Ann", "Lee").equals(new Name("ANN", "lee"))).isTrue();
        }

        @Test
        @DisplayName("a different last name → false")
        void differentLast() {
            assertThat(new Name("Ann", "Lee").equals(new Name("Ann", "Li"))).isFalse();
        }

        @Test
        @DisplayName("first and last swapped → false")
        void swapped() {
            assertThat(new Name("Ann", "Lee").equals(new Name("Lee", "Ann"))).isFalse();
        }

        @Test
        @DisplayName("null → false")
        void nullArgument() {
            assertThat(new Name("Ann", "Lee").equals(null)).isFalse();
        }

        @Test
        @DisplayName("never equal to a String (keeps symmetry)")
        void notEqualToString() {
            assertThat(new Name("Ann", "Lee").equals("Ann Lee")).isFalse();
            assertThat("Ann Lee".equals(new Name("Ann", "Lee"))).isFalse();
        }
    }

    // ── Task 3: Measurement.equals ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Measurement.equals()")
    @DisplayName("Task 3 — Measurement.equals()")
    class MeasurementEqualsTest {

        @Test
        @DisplayName("same value and unit → equal")
        void same() {
            assertThat(new Measurement(2.5, "m").equals(new Measurement(2.5, "m"))).isTrue();
        }

        @Test
        @DisplayName("different unit → not equal")
        void differentUnit() {
            assertThat(new Measurement(2.5, "m").equals(new Measurement(2.5, "km"))).isFalse();
        }

        @Test
        @DisplayName("different value → not equal")
        void differentValue() {
            assertThat(new Measurement(2.5, "m").equals(new Measurement(2.6, "m"))).isFalse();
        }

        @Test
        @DisplayName("NaN is equal to itself (reflexivity)")
        void nan() {
            Measurement m = new Measurement(Double.NaN, "m");
            assertThat(m.equals(m)).isTrue();
            assertThat(m.equals(new Measurement(Double.NaN, "m"))).isTrue();
        }

        @Test
        @DisplayName("0.0 and -0.0 are different, as in Double.equals")
        void negativeZero() {
            assertThat(new Measurement(0.0, "m").equals(new Measurement(-0.0, "m"))).isFalse();
        }

        @Test
        @DisplayName("a null unit is allowed")
        void nullUnit() {
            assertThat(new Measurement(1.0, null).equals(new Measurement(1.0, null))).isTrue();
            assertThat(new Measurement(1.0, null).equals(new Measurement(1.0, "m"))).isFalse();
            assertThat(new Measurement(1.0, "m").equals(new Measurement(1.0, null))).isFalse();
        }

        @Test
        @DisplayName("null and other types → false")
        void nullAndOtherTypes() {
            assertThat(new Measurement(1.0, "m").equals(null)).isFalse();
            assertThat(new Measurement(1.0, "m").equals("1.0 m")).isFalse();
        }
    }

    // ── Task 4: UnorderedPair.equals ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement UnorderedPair.equals()")
    @DisplayName("Task 4 — UnorderedPair.equals()")
    class UnorderedPairEqualsTest {

        @Test
        @DisplayName("same order → equal")
        void sameOrder() {
            assertThat(new UnorderedPair("x", "y").equals(new UnorderedPair("x", "y"))).isTrue();
        }

        @Test
        @DisplayName("swapped order → equal")
        void swapped() {
            assertThat(new UnorderedPair("x", "y").equals(new UnorderedPair("y", "x"))).isTrue();
        }

        @Test
        @DisplayName("one element differs → not equal")
        void differentElement() {
            assertThat(new UnorderedPair("x", "y").equals(new UnorderedPair("x", "z"))).isFalse();
            assertThat(new UnorderedPair("x", "y").equals(new UnorderedPair("z", "y"))).isFalse();
        }

        @Test
        @DisplayName("(x, x) is not equal to (x, y), in either direction")
        void repeatedElement() {
            assertThat(new UnorderedPair("x", "x").equals(new UnorderedPair("x", "y"))).isFalse();
            assertThat(new UnorderedPair("x", "y").equals(new UnorderedPair("x", "x"))).isFalse();
        }

        @Test
        @DisplayName("symmetric, and null → false")
        void symmetricAndNull() {
            UnorderedPair p = new UnorderedPair("a", "b");
            UnorderedPair q = new UnorderedPair("b", "a");
            assertThat(p.equals(q)).isEqualTo(q.equals(p));
            assertThat(p.equals(null)).isFalse();
        }
    }

    // ── Task 5: violatesSymmetry ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement violatesSymmetry()")
    @DisplayName("Task 5 — violatesSymmetry()")
    class ViolatesSymmetryTest {

        @Test
        @DisplayName("CaseInsensitiveText vs String → true (in both argument orders)")
        void brokenClass() {
            assertThat(tasks.violatesSymmetry(new CaseInsensitiveText("Java"), "java")).isTrue();
            assertThat(tasks.violatesSymmetry("java", new CaseInsensitiveText("Java"))).isTrue();
        }

        @Test
        @DisplayName("two CaseInsensitiveText objects → false")
        void sameType() {
            assertThat(tasks.violatesSymmetry(new CaseInsensitiveText("Java"), new CaseInsensitiveText("JAVA")))
                    .isFalse();
        }

        @Test
        @DisplayName("two equal Strings → false")
        void equalStrings() {
            assertThat(tasks.violatesSymmetry("a", "a")).isFalse();
        }

        @Test
        @DisplayName("two different Strings → false (false vs false is symmetric)")
        void differentStrings() {
            assertThat(tasks.violatesSymmetry("a", "b")).isFalse();
        }

        @Test
        @DisplayName("Integer 1 vs Long 1 → false (both directions are false)")
        void boxedTypes() {
            assertThat(tasks.violatesSymmetry(1, 1L)).isFalse();
        }
    }

    // ── Task 6: overridesEquals ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement overridesEquals()")
    @DisplayName("Task 6 — overridesEquals()")
    class OverridesEqualsTest {

        @Test
        @DisplayName("a real override → true")
        void realOverride() {
            assertThat(tasks.overridesEquals(GoodEquals.class)).isTrue();
        }

        @Test
        @DisplayName("an overload is not an override → false")
        void overload() {
            assertThat(tasks.overridesEquals(OverloadedEquals.class)).isFalse();
        }

        @Test
        @DisplayName("an inherited equals does not count → false")
        void inherited() {
            assertThat(tasks.overridesEquals(NoEquals.class)).isFalse();
        }

        @Test
        @DisplayName("JDK: String → true, Object → true, ArrayDeque → false (inherits Object.equals)")
        void jdkTypes() {
            assertThat(tasks.overridesEquals(String.class)).isTrue();
            assertThat(tasks.overridesEquals(Object.class)).isTrue();
            assertThat(tasks.overridesEquals(java.util.ArrayDeque.class)).isFalse();
        }
    }
}
