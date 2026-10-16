package com.vbforge.sealed_classes_pattern_matching;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("PatternMatchingTasks — your implementations")
class PatternMatchingTasksTest {

    private PatternMatchingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new PatternMatchingTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parseToInt()")
    @DisplayName("parseToInt")
    class ParseToIntTest {

        @Test
        @DisplayName("Integer, Long and String are converted")
        void happyPath() {
            assertThat(tasks.parseToInt(42)).isEqualTo(42);
            assertThat(tasks.parseToInt(7L)).isEqualTo(7);
            assertThat(tasks.parseToInt(" 15 ")).isEqualTo(15);
        }

        @Test
        @DisplayName("negative and boundary values")
        void boundaries() {
            assertThat(tasks.parseToInt(-3)).isEqualTo(-3);
            assertThat(tasks.parseToInt((long) Integer.MAX_VALUE)).isEqualTo(Integer.MAX_VALUE);
            assertThat(tasks.parseToInt("-8")).isEqualTo(-8);
        }

        @Test
        @DisplayName("Long that does not fit in an int throws ArithmeticException")
        void longTooBig() {
            assertThatThrownBy(() -> tasks.parseToInt(3_000_000_000L)).isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("invalid text throws NumberFormatException")
        void invalidText() {
            assertThatThrownBy(() -> tasks.parseToInt("abc")).isInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("unsupported types and null throw IllegalArgumentException")
        void unsupported() {
            assertThatThrownBy(() -> tasks.parseToInt(2.5)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.parseToInt(null)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sizeLabel()")
    @DisplayName("sizeLabel")
    class SizeLabelTest {

        @Test
        @DisplayName("null is labelled none")
        void nullValue() {
            assertThat(tasks.sizeLabel(null)).isEqualTo("none");
        }

        @Test
        @DisplayName("strings: empty and non-empty")
        void strings() {
            assertThat(tasks.sizeLabel("")).isEqualTo("empty text");
            assertThat(tasks.sizeLabel("hey")).isEqualTo("text of 3");
        }

        @Test
        @DisplayName("collections: empty and non-empty")
        void collections() {
            assertThat(tasks.sizeLabel(List.of())).isEqualTo("empty collection");
            assertThat(tasks.sizeLabel(List.of(1, 2))).isEqualTo("collection of 2");
        }

        @Test
        @DisplayName("int arrays, including length 0")
        void intArrays() {
            assertThat(tasks.sizeLabel(new int[]{1, 2, 3})).isEqualTo("int array of 3");
            assertThat(tasks.sizeLabel(new int[0])).isEqualTo("int array of 0");
        }

        @Test
        @DisplayName("everything else is a scalar")
        void scalar() {
            assertThat(tasks.sizeLabel(42)).isEqualTo("scalar");
            assertThat(tasks.sizeLabel(2.5)).isEqualTo("scalar");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement looselyEquals()")
    @DisplayName("looselyEquals")
    class LooselyEqualsTest {

        @Test
        @DisplayName("strings equal ignoring case")
        void strings() {
            assertThat(tasks.looselyEquals("Java", "JAVA")).isTrue();
            assertThat(tasks.looselyEquals("Java", "Kotlin")).isFalse();
        }

        @ParameterizedTest(name = "numbers {0} and {1} → {2}")
        @CsvSource({"1, 1.0, true", "2, 2.5, false", "0, 0.0, true"})
        @DisplayName("numbers compare by doubleValue")
        void numbers(double a, double b, boolean expected) {
            assertThat(tasks.looselyEquals((int) a, b)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Integer and Double with the same value are equal")
        void mixedNumbers() {
            assertThat(tasks.looselyEquals(1, 1.0)).isTrue();
            assertThat(tasks.looselyEquals(5L, 5)).isTrue();
        }

        @Test
        @DisplayName("mixed types and nulls are never equal")
        void mixedAndNull() {
            assertThat(tasks.looselyEquals(1, "1")).isFalse();
            assertThat(tasks.looselyEquals(null, null)).isFalse();
            assertThat(tasks.looselyEquals("a", null)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement collectStrings()")
    @DisplayName("collectStrings")
    class CollectStringsTest {

        @Test
        @DisplayName("keeps trimmed non-blank strings in order")
        void happyPath() {
            assertThat(tasks.collectStrings(List.of("a", 1, "  ", " b "))).containsExactly("a", "b");
        }

        @Test
        @DisplayName("no strings → empty list")
        void noStrings() {
            assertThat(tasks.collectStrings(List.of(1, 2, 3))).isEmpty();
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() {
            assertThat(tasks.collectStrings(List.of())).isEmpty();
        }

        @Test
        @DisplayName("null elements are ignored")
        void nullElements() {
            assertThat(tasks.collectStrings(Arrays.asList("x", null, "y"))).containsExactly("x", "y");
        }

        @Test
        @DisplayName("returns a new list, not the input")
        void newList() {
            List<Object> input = List.of("a");

            assertThat(tasks.collectStrings(input)).isNotSameAs(input);
        }
    }
}
