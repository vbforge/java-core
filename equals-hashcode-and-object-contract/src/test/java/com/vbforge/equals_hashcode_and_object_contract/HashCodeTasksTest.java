package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.HashCodeTasks.BrokenHash;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeTasks.Measurement;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeTasks.MutableKey;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeTasks.Name;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeTasks.Point;
import com.vbforge.equals_hashcode_and_object_contract.HashCodeTasks.UnorderedPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

/**
 * HashCodeTasksTest — tests for every method in {@link HashCodeTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code HashCodeTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=HashCodeTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("HashCodeTasks — your implementations")
class HashCodeTasksTest {

    private HashCodeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new HashCodeTasks();
    }

    // ── Task 1: Point.hashCode ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Point.hashCode()")
    @DisplayName("Task 1 — Point.hashCode()")
    class PointHashCodeTest {

        @Test
        @DisplayName("equal points → equal hash codes")
        void consistentWithEquals() {
            assertThat(new Point(1, 2).hashCode()).isEqualTo(new Point(1, 2).hashCode());
        }

        @Test
        @DisplayName("(1, 2) and (2, 1) have different hash codes")
        void orderMatters() {
            assertThat(new Point(1, 2).hashCode()).isNotEqualTo(new Point(2, 1).hashCode());
        }

        @Test
        @DisplayName("each coordinate matters")
        void eachCoordinate() {
            assertThat(new Point(1, 2).hashCode()).isNotEqualTo(new Point(1, 3).hashCode());
            assertThat(new Point(1, 2).hashCode()).isNotEqualTo(new Point(0, 2).hashCode());
        }

        @Test
        @DisplayName("a 10×10 grid gives at least 90 distinct hash codes")
        void goodSpread() {
            Set<Integer> hashes = new HashSet<>();
            for (int x = 0; x < 10; x++) {
                for (int y = 0; y < 10; y++) {
                    hashes.add(new Point(x, y).hashCode());
                }
            }
            assertThat(hashes.size()).isGreaterThanOrEqualTo(90);
        }

        @Test
        @DisplayName("a HashSet recognises an equal point")
        void worksInHashSet() {
            Set<Point> set = new HashSet<>();
            set.add(new Point(3, 4));
            assertThat(set.contains(new Point(3, 4))).isTrue();
            assertThat(set.add(new Point(3, 4))).isFalse();
        }
    }

    // ── Task 2: Name.hashCode ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Name.hashCode()")
    @DisplayName("Task 2 — Name.hashCode()")
    class NameHashCodeTest {

        @Test
        @DisplayName("names differing only in case have the same hash code")
        void ignoresCase() {
            assertThat(new Name("Ann", "Lee").hashCode()).isEqualTo(new Name("ANN", "lee").hashCode());
        }

        @Test
        @DisplayName("swapped first and last name differ")
        void swapped() {
            assertThat(new Name("Ann", "Lee").hashCode()).isNotEqualTo(new Name("Lee", "Ann").hashCode());
        }

        @Test
        @DisplayName("a different first name differs")
        void differentFirst() {
            assertThat(new Name("Ann", "Lee").hashCode()).isNotEqualTo(new Name("Bob", "Lee").hashCode());
        }

        @Test
        @DisplayName("a HashSet treats names that differ only in case as one element")
        void worksInHashSet() {
            Set<Name> set = new HashSet<>();
            set.add(new Name("Ann", "Lee"));
            set.add(new Name("ANN", "LEE"));
            assertThat(set).hasSize(1);
        }
    }

    // ── Task 3: Measurement.hashCode ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Measurement.hashCode()")
    @DisplayName("Task 3 — Measurement.hashCode()")
    class MeasurementHashCodeTest {

        @Test
        @DisplayName("equal measurements → equal hash codes")
        void consistent() {
            assertThat(new Measurement(2.5, "m").hashCode()).isEqualTo(new Measurement(2.5, "m").hashCode());
        }

        @Test
        @DisplayName("NaN measurements hash the same")
        void nan() {
            assertThat(new Measurement(Double.NaN, "m").hashCode())
                    .isEqualTo(new Measurement(Double.NaN, "m").hashCode());
        }

        @Test
        @DisplayName("a null unit does not throw")
        void nullUnit() {
            assertThatCode(() -> new Measurement(1.0, null).hashCode()).doesNotThrowAnyException();
            assertThat(new Measurement(1.0, null).hashCode()).isEqualTo(new Measurement(1.0, null).hashCode());
        }

        @Test
        @DisplayName("different value or unit → different hash code")
        void differs() {
            assertThat(new Measurement(1.0, "m").hashCode()).isNotEqualTo(new Measurement(2.0, "m").hashCode());
            assertThat(new Measurement(1.0, "m").hashCode()).isNotEqualTo(new Measurement(1.0, "km").hashCode());
        }

        @Test
        @DisplayName("a HashSet finds a NaN measurement")
        void nanInHashSet() {
            Set<Measurement> set = new HashSet<>();
            set.add(new Measurement(Double.NaN, "m"));
            assertThat(set.contains(new Measurement(Double.NaN, "m"))).isTrue();
        }
    }

    // ── Task 4: UnorderedPair.hashCode ────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement UnorderedPair.hashCode()")
    @DisplayName("Task 4 — UnorderedPair.hashCode()")
    class UnorderedPairHashCodeTest {

        @Test
        @DisplayName("the order of the elements does not matter")
        void orderIndependent() {
            assertThat(new UnorderedPair("x", "y").hashCode()).isEqualTo(new UnorderedPair("y", "x").hashCode());
        }

        @Test
        @DisplayName("same order → same hash code")
        void sameOrder() {
            assertThat(new UnorderedPair("x", "y").hashCode()).isEqualTo(new UnorderedPair("x", "y").hashCode());
        }

        @Test
        @DisplayName("a different element → different hash code")
        void differentElement() {
            assertThat(new UnorderedPair("x", "y").hashCode()).isNotEqualTo(new UnorderedPair("x", "z").hashCode());
        }

        @Test
        @DisplayName("(x, x) differs from (x, y)")
        void repeated() {
            assertThat(new UnorderedPair("x", "x").hashCode()).isNotEqualTo(new UnorderedPair("x", "y").hashCode());
        }

        @Test
        @DisplayName("a HashSet treats (x, y) and (y, x) as one element")
        void worksInHashSet() {
            Set<UnorderedPair> set = new HashSet<>();
            set.add(new UnorderedPair("x", "y"));
            set.add(new UnorderedPair("y", "x"));
            assertThat(set).hasSize(1);
        }
    }

    // ── Task 5: isConsistentWithEquals ────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isConsistentWithEquals()")
    @DisplayName("Task 5 — isConsistentWithEquals()")
    class IsConsistentTest {

        @Test
        @DisplayName("equal Strings → true")
        void equalStrings() {
            assertThat(tasks.isConsistentWithEquals("a", "a")).isTrue();
        }

        @Test
        @DisplayName("unequal objects → true (nothing to check)")
        void unequal() {
            assertThat(tasks.isConsistentWithEquals("a", "b")).isTrue();
            assertThat(tasks.isConsistentWithEquals(new BrokenHash(1), new BrokenHash(2))).isTrue();
        }

        @Test
        @DisplayName("equal objects with different hash codes → false")
        void broken() {
            assertThat(tasks.isConsistentWithEquals(new BrokenHash(1), new BrokenHash(1))).isFalse();
        }

        @Test
        @DisplayName("a correct class: Integer 5 and Integer 5 → true")
        void correctClass() {
            assertThat(tasks.isConsistentWithEquals(5, 5)).isTrue();
        }
    }

    // ── Task 6: isLostAfterMutation ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isLostAfterMutation()")
    @DisplayName("Task 6 — isLostAfterMutation()")
    class IsLostAfterMutationTest {

        @Test
        @DisplayName("the mutated key is no longer found → true")
        void lost() {
            assertThat(tasks.isLostAfterMutation(new MutableKey(1))).isTrue();
        }

        @Test
        @DisplayName("the key keeps its changed id (original + 1)")
        void keyChanged() {
            MutableKey key = new MutableKey(1);
            tasks.isLostAfterMutation(key);
            assertThat(key.id()).isEqualTo(2);
        }

        @Test
        @DisplayName("works for a negative id")
        void negativeId() {
            MutableKey key = new MutableKey(-5);
            assertThat(tasks.isLostAfterMutation(key)).isTrue();
            assertThat(key.id()).isEqualTo(-4);
        }
    }
}
