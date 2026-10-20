package com.vbforge.optional_and_null_handling;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OptionalCreationTasks — your implementations")
class OptionalCreationTasksTest {

    private OptionalCreationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new OptionalCreationTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parseInteger()")
    @DisplayName("1. parseInteger(text)")
    class ParseIntegerTest {

        @Test
        @DisplayName("example from Javadoc: valid numbers are wrapped")
        void validNumbers() {
            assertThat(tasks.parseInteger("42")).contains(42);
            assertThat(tasks.parseInteger("-7")).contains(-7);
            assertThat(tasks.parseInteger("0")).contains(0);
        }

        @ParameterizedTest(name = "\"{0}\" is not a valid int")
        @ValueSource(strings = {"abc", "", "4.5", " 42", "2147483648"})
        @DisplayName("invalid text gives an empty Optional")
        void invalidText(String text) {
            assertThat(tasks.parseInteger(text)).isEmpty();
        }

        @Test
        @DisplayName("null gives an empty Optional")
        void nullText() {
            assertThat(tasks.parseInteger(null)).isEmpty();
        }

        @Test
        @DisplayName("Integer.MAX_VALUE is still valid")
        void maxValue() {
            assertThat(tasks.parseInteger("2147483647")).contains(Integer.MAX_VALUE);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement nonBlank()")
    @DisplayName("2. nonBlank(text)")
    class NonBlankTest {

        @Test
        @DisplayName("example from Javadoc: surrounding whitespace is trimmed")
        void trimsText() {
            assertThat(tasks.nonBlank("  hi  ")).contains("hi");
        }

        @Test
        @DisplayName("text without whitespace is returned unchanged")
        void plainText() {
            assertThat(tasks.nonBlank("hello")).contains("hello");
        }

        @ParameterizedTest(name = "\"{0}\" is blank")
        @ValueSource(strings = {"", " ", "   ", "\t\n"})
        @DisplayName("empty or whitespace-only text gives an empty Optional")
        void blankText(String text) {
            assertThat(tasks.nonBlank(text)).isEmpty();
        }

        @Test
        @DisplayName("null gives an empty Optional")
        void nullText() {
            assertThat(tasks.nonBlank(null)).isEmpty();
        }

        @Test
        @DisplayName("inner whitespace is preserved")
        void innerWhitespace() {
            assertThat(tasks.nonBlank(" a b ")).contains("a b");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement lookup()")
    @DisplayName("3. lookup(map, key)")
    class LookupTest {

        @Test
        @DisplayName("example from Javadoc: existing key is wrapped")
        void existingKey() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", "42");

            assertThat(tasks.lookup(map, "id")).contains("42");
        }

        @Test
        @DisplayName("missing key gives an empty Optional")
        void missingKey() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", "42");

            assertThat(tasks.lookup(map, "missing")).isEmpty();
        }

        @Test
        @DisplayName("key mapped to null gives an empty Optional")
        void nullValue() {
            Map<String, String> map = new HashMap<String, String>();
            map.put("id", null);

            assertThat(tasks.lookup(map, "id")).isEmpty();
        }

        @Test
        @DisplayName("null key gives an empty Optional, even for a map that rejects null keys")
        void nullKey() {
            Map<String, String> map = new TreeMap<String, String>();
            map.put("id", "42");

            assertThat(tasks.lookup(map, null)).isEmpty();
        }

        @Test
        @DisplayName("empty map gives an empty Optional")
        void emptyMap() {
            assertThat(tasks.lookup(new HashMap<String, String>(), "id")).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement firstNegative()")
    @DisplayName("4. firstNegative(numbers)")
    class FirstNegativeTest {

        @Test
        @DisplayName("example from Javadoc: returns the FIRST negative number")
        void firstOfSeveral() {
            assertThat(tasks.firstNegative(new int[]{3, -1, -5})).hasValue(-1);
        }

        @Test
        @DisplayName("no negative numbers gives an empty OptionalInt")
        void noNegatives() {
            assertThat(tasks.firstNegative(new int[]{3, 0, 5})).isEmpty();
        }

        @Test
        @DisplayName("empty array gives an empty OptionalInt")
        void emptyArray() {
            assertThat(tasks.firstNegative(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("zero is not negative")
        void zeroIsNotNegative() {
            assertThat(tasks.firstNegative(new int[]{0})).isEmpty();
        }

        @Test
        @DisplayName("Integer.MIN_VALUE is negative")
        void minValue() {
            assertThat(tasks.firstNegative(new int[]{1, Integer.MIN_VALUE})).hasValue(Integer.MIN_VALUE);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement average()")
    @DisplayName("5. average(numbers)")
    class AverageTest {

        @ParameterizedTest(name = "average of {0} = {1}")
        @CsvSource({
                "'1,2,3,4', 2.5",
                "'5',       5.0",
                "'-1,1',    0.0",
                "'1,2',     1.5"
        })
        @DisplayName("returns the arithmetic mean")
        void mean(String csv, double expected) {
            String[] parts = csv.split(",");
            int[] numbers = new int[parts.length];
            for (int i = 0; i < parts.length; i++) {
                numbers[i] = Integer.parseInt(parts[i].trim());
            }

            assertThat(tasks.average(numbers)).hasValue(expected);
        }

        @Test
        @DisplayName("empty array has no average")
        void emptyArray() {
            assertThat(tasks.average(new int[0])).isEmpty();
        }

        @Test
        @DisplayName("large values do not overflow")
        void noOverflow() {
            assertThat(tasks.average(new int[]{Integer.MAX_VALUE, Integer.MAX_VALUE}))
                    .hasValue((double) Integer.MAX_VALUE);
        }
    }
}
