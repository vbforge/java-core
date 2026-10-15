package com.vbforge.equals_hashcode_and_object_contract;

import com.vbforge.equals_hashcode_and_object_contract.ToStringDemo.NoToString;
import com.vbforge.equals_hashcode_and_object_contract.ToStringDemo.Pair;
import com.vbforge.equals_hashcode_and_object_contract.ToStringDemo.Point;
import com.vbforge.equals_hashcode_and_object_contract.ToStringDemo.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ToStringDemoTest — verifies every concept shown in {@link ToStringDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how toString behaves.
 */
@DisplayName("ToStringDemo — API contracts")
class ToStringDemoTest {

    // ── 1. The default ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. The default")
    class DefaultFormatTest {

        @Test
        @DisplayName("without an override the text is ClassName@hexHash")
        void defaultFormat() {
            NoToString plain = new NoToString();
            assertThat(plain.toString()).isEqualTo(ToStringDemo.defaultFormat(plain));
            assertThat(plain.toString()).matches(".*ToStringDemo\\$NoToString@[0-9a-f]+");
        }

        @Test
        @DisplayName("Object itself prints java.lang.Object@…")
        void objectFormat() {
            assertThat(new Object().toString()).matches("java\\.lang\\.Object@[0-9a-f]+");
        }

        @Test
        @DisplayName("the hex part is the hash code")
        void hexIsHash() {
            Object o = new Object();
            assertThat(o.toString()).endsWith("@" + Integer.toHexString(o.hashCode()));
        }
    }

    // ── 2. Overriding ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Overriding")
    class OverrideTest {

        @Test
        @DisplayName("the override lists the class and the fields")
        void format() {
            assertThat(new Point(1, 2).toString()).isEqualTo("Point(x=1, y=2)");
        }

        @Test
        @DisplayName("concatenation, valueOf, %s and StringBuilder all call toString")
        void implicitCalls() {
            assertThat(ToStringDemo.implicitConversions(new Point(1, 2)))
                    .containsOnly("Point(x=1, y=2)")
                    .hasSize(4);
        }
    }

    // ── 3. Null-safety ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Null-safety")
    class NullSafetyTest {

        @Test
        @DisplayName("null-tolerant conversions give text, not an exception")
        void nullTolerant() {
            assertThat(ToStringDemo.nullHandling()).containsExactly("null", "null", "n/a", "null");
        }

        @Test
        @DisplayName("calling toString() on a null reference throws")
        void directCallThrows() {
            Object nothing = null;
            assertThatThrownBy(() -> nothing.toString()).isInstanceOf(NullPointerException.class);
        }
    }

    // ── 4. Cycles ─────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Cycles")
    class CyclesTest {

        @Test
        @DisplayName("mutual references in toString overflow the stack")
        void naiveOverflows() {
            assertThat(ToStringDemo.badCycleOverflows()).isTrue();
        }

        @Test
        @DisplayName("printing the other side's NAME breaks the cycle")
        void safeVersion() {
            assertThat(ToStringDemo.safeCycle()).isEqualTo("Dept(Sales, staff=[Emp(Ann, dept=Sales)])");
        }
    }

    // ── 5. Sensitive data ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Sensitive data")
    class SensitiveDataTest {

        @Test
        @DisplayName("the password never appears in toString")
        void passwordHidden() {
            User user = new User("ann", "s3cret");
            assertThat(user.toString()).isEqualTo("User(name=ann)").doesNotContain("s3cret");
        }

        @Test
        @DisplayName("mask shows only the last four characters")
        void maskLongSecret() {
            assertThat(ToStringDemo.mask("DE8937040044")).isEqualTo("****0044");
        }

        @Test
        @DisplayName("short or missing secrets are fully hidden")
        void maskShortSecret() {
            assertThat(ToStringDemo.mask("abc")).isEqualTo("****");
            assertThat(ToStringDemo.mask("abcd")).isEqualTo("****");
            assertThat(ToStringDemo.mask(null)).isEqualTo("****");
        }
    }

    // ── 6. Arrays, collections and records ────────────────────────────────────

    @Nested
    @DisplayName("6. Arrays, collections and records")
    class ArraysCollectionsRecordsTest {

        @Test
        @DisplayName("an int[] prints type code and hash")
        void rawArray() {
            assertThat(new int[] {1, 2}.toString()).matches("\\[I@[0-9a-f]+");
        }

        @Test
        @DisplayName("Arrays.toString and Arrays.deepToString print the elements")
        void arraysHelpers() {
            assertThat(Arrays.toString(new int[] {1, 2})).isEqualTo("[1, 2]");
            assertThat(Arrays.deepToString(new int[][] {{1, 2}, {3}})).isEqualTo("[[1, 2], [3]]");
            assertThat(Arrays.toString(new int[][] {{1}}).startsWith("[[I@")).isTrue();
        }

        @Test
        @DisplayName("collections and maps print their elements")
        void collections() {
            assertThat(Arrays.asList("x", "y").toString()).isEqualTo("[x, y]");
            assertThat(ToStringDemo.sampleMap().toString()).isEqualTo("{a=1, b=2}");
        }

        @Test
        @DisplayName("a record has a generated toString")
        void record() {
            assertThat(new Pair("x", 1).toString()).isEqualTo("Pair[left=x, right=1]");
        }

        @Test
        @DisplayName("Optional prints its content or Optional.empty")
        void optional() {
            assertThat(Optional.of("x").toString()).isEqualTo("Optional[x]");
            assertThat(Optional.empty().toString()).isEqualTo("Optional.empty");
        }
    }
}
