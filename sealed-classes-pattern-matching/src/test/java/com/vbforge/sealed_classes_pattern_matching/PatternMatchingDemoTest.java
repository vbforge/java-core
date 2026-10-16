package com.vbforge.sealed_classes_pattern_matching;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.vbforge.sealed_classes_pattern_matching.PatternMatchingDemo.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PatternMatchingDemo — API contracts")
class PatternMatchingDemoTest {

    @Nested
    @DisplayName("1. instanceof patterns")
    class InstanceofPatterns {

        @Test
        @DisplayName("binding gives a typed variable without a cast")
        void binding() {
            assertThat(describeWithInstanceof("hello")).isEqualTo("String of length 5");
            assertThat(describeWithInstanceof(5)).isEqualTo("Integer 5");
        }

        @Test
        @DisplayName("binding is usable to the right of &&")
        void bindingInCondition() {
            assertThat(describeWithInstanceof(500)).isEqualTo("big Integer 500");
            assertThat(describeWithInstanceof(100)).isEqualTo("Integer 100");
        }

        @Test
        @DisplayName("anything else, including null, falls through")
        void fallThrough() {
            assertThat(describeWithInstanceof(2.5)).isEqualTo("other");
            assertThat(describeWithInstanceof(null)).isEqualTo("other");
        }

        @Test
        @DisplayName("negated check with early return keeps the binding in scope afterwards")
        void negatedFlowScoping() {
            assertThat(lengthOrMinusOne("abc")).isEqualTo(3);
            assertThat(lengthOrMinusOne(42)).isEqualTo(-1);
            assertThat(lengthOrMinusOne(null)).isEqualTo(-1);
        }

        @Test
        @DisplayName("equals written with instanceof is null-safe and type-safe")
        void equalsIdiom() {
            Version v = new Version(1, 2);

            assertThat(v.equals(new Version(1, 2))).isTrue();
            assertThat(v.equals(new Version(1, 3))).isFalse();
            assertThat(v.equals(null)).isFalse();
            assertThat(v.equals("1.2")).isFalse();
            assertThat(v).hasSameHashCodeAs(new Version(1, 2));
        }
    }

    @Nested
    @DisplayName("2. switch with type patterns")
    class SwitchTypePatterns {

        @Test
        @DisplayName("each type pattern picks its branch")
        void branches() {
            assertThat(typeSwitch(7)).isEqualTo("Integer 7");
            assertThat(typeSwitch(7L)).isEqualTo("Long 7");
            assertThat(typeSwitch("x")).isEqualTo("String 'x'");
            assertThat(typeSwitch(new int[]{1, 2})).isEqualTo("int[] of 2");
        }

        @Test
        @DisplayName("default handles every other type")
        void defaultBranch() {
            assertThat(typeSwitch(2.5)).isEqualTo("other Double");
        }
    }

    @Nested
    @DisplayName("3. Guards with when")
    class Guards {

        @ParameterizedTest(name = "classify({0}) → {1}")
        @CsvSource({"-5, negative", "-1, negative", "0, zero", "1, positive", "99, positive"})
        @DisplayName("Integer is split into three cases by guards")
        void integers(int value, String expected) {
            assertThat(classify(value)).isEqualTo(expected);
        }

        @Test
        @DisplayName("String is split into blank and non-blank")
        void strings() {
            assertThat(classify("   ")).isEqualTo("blank text");
            assertThat(classify("")).isEqualTo("blank text");
            assertThat(classify("hi")).isEqualTo("text");
        }

        @Test
        @DisplayName("other types reach default")
        void other() {
            assertThat(classify(1.5)).isEqualTo("unsupported");
        }
    }

    @Nested
    @DisplayName("4. null handling")
    class NullHandling {

        @Test
        @DisplayName("switch without case null throws NullPointerException")
        void noNullCase() {
            assertThatThrownBy(() -> withoutNullCase(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("case null handles null explicitly")
        void caseNull() {
            assertThat(withNullCase(null)).isEqualTo("null!");
            assertThat(withNullCase("s")).isEqualTo("string");
            assertThat(withNullCase(1)).isEqualTo("not a string");
        }

        @Test
        @DisplayName("case null, default covers null and everything else")
        void nullAndDefault() {
            assertThat(nullAndDefaultTogether(null)).isEqualTo("null or other");
            assertThat(nullAndDefaultTogether(1)).isEqualTo("null or other");
            assertThat(nullAndDefaultTogether("s")).isEqualTo("string");
        }
    }
}
