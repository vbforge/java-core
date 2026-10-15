package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.CaseInsensitiveText;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.ColorPt;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.OverloadedPoint;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.Plain;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.Point;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.Pt;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.Reading;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.StrictPt;
import com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo.Tag;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * EqualsContractDemoTest — verifies every concept shown in {@link EqualsContractDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how the equals contract behaves.
 */
@DisplayName("EqualsContractDemo — API contracts")
class EqualsContractDemoTest {

    // ── 1. The five rules ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. The five rules")
    class FiveRulesTest {

        private final Point a = new Point(1, 2);
        private final Point b = new Point(1, 2);
        private final Point c = new Point(1, 2);

        @Test
        @DisplayName("reflexive: x.equals(x)")
        void reflexive() {
            assertThat(a.equals(a)).isTrue();
        }

        @Test
        @DisplayName("symmetric: x.equals(y) iff y.equals(x)")
        void symmetric() {
            assertThat(a.equals(b)).isTrue();
            assertThat(b.equals(a)).isTrue();
            assertThat(a.equals(new Point(9, 9))).isEqualTo(new Point(9, 9).equals(a));
        }

        @Test
        @DisplayName("transitive: x=y and y=z imply x=z")
        void transitive() {
            assertThat(a.equals(b) && b.equals(c)).isTrue();
            assertThat(a.equals(c)).isTrue();
        }

        @Test
        @DisplayName("consistent: repeated calls agree")
        void consistent() {
            assertThat(a.equals(b)).isEqualTo(a.equals(b));
        }

        @Test
        @DisplayName("null-safe: x.equals(null) is false")
        void nullSafe() {
            assertThat(a.equals(null)).isFalse();
        }

        @Test
        @DisplayName("a different type is never equal")
        void otherType() {
            assertThat(a.equals("Point(1, 2)")).isFalse();
        }

        @Test
        @DisplayName("different coordinates are not equal")
        void differentValues() {
            assertThat(a.equals(new Point(1, 3))).isFalse();
            assertThat(a.equals(new Point(2, 1))).isFalse();
        }
    }

    // ── 2. The default is identity ────────────────────────────────────────────

    @Nested
    @DisplayName("2. The default is identity")
    class IdentityTest {

        @Test
        @DisplayName("without an override, equal-looking objects are not equal")
        void defaultIsIdentity() {
            assertThat(new Plain(1).equals(new Plain(1))).isFalse();
        }

        @Test
        @DisplayName("an object is always equal to itself")
        void sameReference() {
            Plain p = new Plain(1);
            assertThat(p.equals(p)).isTrue();
        }

        @Test
        @DisplayName("== compares references, equals compares text for Strings")
        void stringsEqualsVsIdentity() {
            boolean[] result = EqualsContractDemo.stringComparison();
            assertThat(result[0]).isFalse();
            assertThat(result[1]).isTrue();
        }
    }

    // ── 3. The overload trap ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. The overload trap")
    class OverloadTrapTest {

        @Test
        @DisplayName("a call with the static type picks the overload and looks fine")
        void directCall() {
            assertThat(new OverloadedPoint(1, 2).equals(new OverloadedPoint(1, 2))).isTrue();
        }

        @Test
        @DisplayName("a call through an Object reference reaches Object.equals — identity")
        void viaObjectReference() {
            assertThat(EqualsContractDemo.overloadTrap()[1]).isFalse();
        }

        @Test
        @DisplayName("List.contains uses equals(Object) and does not find the element")
        void listContains() {
            assertThat(EqualsContractDemo.overloadTrap()[2]).isFalse();
        }

        @Test
        @DisplayName("reflection tells a real override from an overload")
        void detectsOverride() {
            assertThat(EqualsContractDemo.overridesEquals(Point.class)).isTrue();
            assertThat(EqualsContractDemo.overridesEquals(OverloadedPoint.class)).isFalse();
            assertThat(EqualsContractDemo.overridesEquals(Plain.class)).isFalse();
        }
    }

    // ── 4. Symmetry ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Symmetry")
    class SymmetryTest {

        @Test
        @DisplayName("the custom class claims to equal a String…")
        void oneDirection() {
            assertThat(new CaseInsensitiveText("Java").equals("java")).isTrue();
        }

        @Test
        @DisplayName("…but String.equals does not agree: symmetry is broken")
        void otherDirection() {
            assertThat("java".equals(new CaseInsensitiveText("Java"))).isFalse();
        }

        @Test
        @DisplayName("between two CaseInsensitiveText objects the rule holds")
        void sameTypeIsFine() {
            CaseInsensitiveText a = new CaseInsensitiveText("Java");
            CaseInsensitiveText b = new CaseInsensitiveText("JAVA");
            assertThat(a.equals(b)).isEqualTo(b.equals(a)).isTrue();
        }
    }

    // ── 5. Inheritance and transitivity ───────────────────────────────────────

    @Nested
    @DisplayName("5. Inheritance and transitivity")
    class TransitivityTest {

        private final ColorPt red = new ColorPt(1, 2, "red");
        private final Pt plain = new Pt(1, 2);
        private final ColorPt blue = new ColorPt(1, 2, "blue");

        @Test
        @DisplayName("the instanceof version is symmetric…")
        void symmetricButNot() {
            assertThat(red.equals(plain)).isTrue();
            assertThat(plain.equals(red)).isTrue();
        }

        @Test
        @DisplayName("…but not transitive: red = plain, plain = blue, red ≠ blue")
        void notTransitive() {
            assertThat(red.equals(plain)).isTrue();
            assertThat(plain.equals(blue)).isTrue();
            assertThat(red.equals(blue)).isFalse();
        }

        @Test
        @DisplayName("the getClass version stays transitive…")
        void getClassVersion() {
            assertThat(new StrictPt(1, 2).equals(new StrictPt(1, 2))).isTrue();
        }

        @Test
        @DisplayName("…but a state-less subclass is no longer equal to its parent")
        void breaksSubstitution() {
            StrictPt anonymousSubclass = new StrictPt(1, 2) { };
            assertThat(new StrictPt(1, 2).equals(anonymousSubclass)).isFalse();
        }
    }

    // ── 6. Doubles and records ────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Doubles and records")
    class DoublesAndRecordsTest {

        @Test
        @DisplayName("== and Double.equals disagree for -0.0 and NaN")
        void primitiveVsWrapper() {
            boolean[] r = EqualsContractDemo.doubleEquality();
            assertThat(r).containsExactly(true, false, false, true);
        }

        @Test
        @DisplayName("a record with a NaN component is equal to itself")
        void recordNaN() {
            assertThat(new Reading(Double.NaN)).isEqualTo(new Reading(Double.NaN));
        }

        @Test
        @DisplayName("a record distinguishes 0.0 from -0.0")
        void recordNegativeZero() {
            assertThat(new Reading(0.0)).isNotEqualTo(new Reading(-0.0));
        }

        @Test
        @DisplayName("a record accepts a null component")
        void recordNullComponent() {
            assertThat(new Tag(null)).isEqualTo(new Tag(null));
            assertThat(new Tag(null)).isNotEqualTo(new Tag("x"));
        }
    }

    // ── 7. Null fields and arrays ─────────────────────────────────────────────

    @Nested
    @DisplayName("7. Null fields and arrays")
    class NullsAndArraysTest {

        @Test
        @DisplayName("Objects.equals tolerates null on either side")
        void objectsEquals() {
            assertThat(Objects.equals(null, null)).isTrue();
            assertThat(Objects.equals("a", null)).isFalse();
            assertThat(Objects.equals(null, "a")).isFalse();
            assertThat(Objects.equals("a", "a")).isTrue();
        }

        @Test
        @DisplayName("array.equals is identity, Arrays.equals compares content")
        void arrays() {
            boolean[] r = EqualsContractDemo.arrayEquality(new int[] {1, 2}, new int[] {1, 2});
            assertThat(r[0]).isFalse();
            assertThat(r[1]).isTrue();
        }

        @Test
        @DisplayName("Arrays.deepEquals handles nested arrays")
        void deepEquals() {
            assertThat(Arrays.deepEquals(new int[][] {{1}, {2}}, new int[][] {{1}, {2}})).isTrue();
            assertThat(Arrays.equals(new int[][] {{1}, {2}}, new int[][] {{1}, {2}})).isFalse();
        }
    }
}
