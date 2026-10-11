package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * LifecycleTasksTest — tests for every method in {@link LifecycleTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code LifecycleTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=LifecycleTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 4 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("LifecycleTasks — your implementations")
class LifecycleTasksTest {

    private LifecycleTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new LifecycleTasks();
    }

    // ── Task 1: isPalindrome ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isPalindrome()")
    @DisplayName("Task 1 — isPalindrome()")
    class IsPalindromeTest {

        @ParameterizedTest(name = "\"{0}\" is a palindrome")
        @ValueSource(strings = {
                "racecar",
                "A man, a plan, a canal: Panama",
                "Was it a car or a cat I saw?",
                "a",
                ""
        })
        @DisplayName("true for palindromes")
        void palindromes(String s) {
            assertThat(tasks.isPalindrome(s)).isTrue();
        }

        @ParameterizedTest(name = "\"{0}\" is NOT a palindrome")
        @ValueSource(strings = {
                "hello",
                "abca",
                "This is not"
        })
        @DisplayName("false for non-palindromes")
        void nonPalindromes(String s) {
            assertThat(tasks.isPalindrome(s)).isFalse();
        }

        @Test
        @DisplayName("null is treated as a palindrome")
        void nullIsPalindrome() {
            assertThat(tasks.isPalindrome(null)).isTrue();
        }
    }

    // ── Task 2: reverse ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement reverse()")
    @DisplayName("Task 2 — reverse()")
    class ReverseTest {

        @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
        @CsvSource({
                "hello,    olleh",
                "abc cba,  abc cba",
                "a,        a",
                "'',       ''"
        })
        @DisplayName("reverses a string")
        void reverse(String input, String expected) {
            assertThat(tasks.reverse(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("null → null")
        void nullReverse() {
            assertThat(tasks.reverse(null)).isNull();
        }
    }

    // ── Task 3: countVowels ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countVowels()")
    @DisplayName("Task 3 — countVowels()")
    class CountVowelsTest {

        @ParameterizedTest(name = "countVowels(\"{0}\") = {1}")
        @CsvSource({
                "hello,        2",
                "AEIOU,        5",
                "xyz,          0",
                "'',           0",
                "Hello World,  3",
                "rhythm,       0"
        })
        @DisplayName("counts vowels case-insensitively")
        void countVowels(String input, int expected) {
            assertThat(tasks.countVowels(input)).isEqualTo(expected);
        }

        @Test
        @DisplayName("null → 0")
        void nullReturnsZero() {
            assertThat(tasks.countVowels(null)).isZero();
        }
    }

    // ── Task 4: capitalizeWords ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement capitalizeWords()")
    @DisplayName("Task 4 — capitalizeWords()")
    class CapitalizeWordsTest {

        @Test
        @DisplayName("hello world → Hello World")
        void basic() {
            assertThat(tasks.capitalizeWords("hello world")).isEqualTo("Hello World");
        }

        @Test
        @DisplayName("java is fun → Java Is Fun")
        void threeWords() {
            assertThat(tasks.capitalizeWords("java is fun")).isEqualTo("Java Is Fun");
        }

        @Test
        @DisplayName("empty string unchanged")
        void emptyString() {
            assertThat(tasks.capitalizeWords("")).isEmpty();
        }

        @Test
        @DisplayName("null unchanged")
        void nullUnchanged() {
            assertThat(tasks.capitalizeWords(null)).isNull();
        }
    }
}