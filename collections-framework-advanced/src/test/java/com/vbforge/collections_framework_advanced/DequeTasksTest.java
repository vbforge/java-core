package com.vbforge.collections_framework_advanced;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("DequeTasks — your implementations")
class DequeTasksTest {

    private DequeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new DequeTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement areBracketsBalanced()")
    @DisplayName("1. areBracketsBalanced(text)")
    class BracketsTest {

        @ParameterizedTest(name = "\"{0}\" is balanced")
        @ValueSource(strings = {"([]{})", "a(b[c]d)e", "", "no brackets at all", "{[()]}()", "()[]{}"})
        @DisplayName("examples from Javadoc: balanced texts")
        void balanced(String text) {
            assertThat(tasks.areBracketsBalanced(text)).isTrue();
        }

        @ParameterizedTest(name = "\"{0}\" is NOT balanced")
        @ValueSource(strings = {"(]", "((", ")(", "(()", "())", "([)]", "}", "{[}"})
        @DisplayName("examples from Javadoc: unbalanced texts")
        void unbalanced(String text) {
            assertThat(tasks.areBracketsBalanced(text)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement evaluateRpn()")
    @DisplayName("2. evaluateRpn(tokens)")
    class EvaluateRpnTest {

        @Test
        @DisplayName("example from Javadoc: (2 + 1) * 3")
        void addThenMultiply() {
            assertThat(tasks.evaluateRpn(new String[]{"2", "1", "+", "3", "*"})).isEqualTo(9);
        }

        @Test
        @DisplayName("example from Javadoc: 4 + (13 / 5)")
        void divisionTruncates() {
            assertThat(tasks.evaluateRpn(new String[]{"4", "13", "5", "/", "+"})).isEqualTo(6);
        }

        @Test
        @DisplayName("example from Javadoc: division truncates toward zero for negatives")
        void negativeDivision() {
            assertThat(tasks.evaluateRpn(new String[]{"7", "-2", "/"})).isEqualTo(-3);
        }

        @Test
        @DisplayName("example from Javadoc: a single number is its own value")
        void singleNumber() {
            assertThat(tasks.evaluateRpn(new String[]{"5"})).isEqualTo(5);
        }

        @Test
        @DisplayName("the first popped operand is the RIGHT-hand side: 10 - 3 = 7")
        void operandOrder() {
            assertThat(tasks.evaluateRpn(new String[]{"10", "3", "-"})).isEqualTo(7);
        }

        @Test
        @DisplayName("a longer expression with negative numbers")
        void longExpression() {
            String[] tokens = {"10", "6", "9", "3", "+", "-11", "*", "/", "*", "17", "+", "5", "+"};

            assertThat(tasks.evaluateRpn(tokens)).isEqualTo(22);
        }

        @Test
        @DisplayName("an unknown token is rejected")
        void unknownToken() {
            assertThatThrownBy(() -> tasks.evaluateRpn(new String[]{"2", "3", "^"}))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement simplifyPath()")
    @DisplayName("3. simplifyPath(path)")
    class SimplifyPathTest {

        @ParameterizedTest(name = "\"{0}\" -> \"{1}\"")
        @CsvSource({
                "/home//foo/,       /home/foo",
                "/a/./b/../../c/,   /c",
                "/../,              /",
                "/,                 /",
                "/a/b/c,            /a/b/c",
                "/a/../../b,        /b",
                "///,               /",
                "/a//b////c/d//.,   /a/b/c/d"
        })
        @DisplayName("examples from Javadoc and more")
        void simplifies(String path, String expected) {
            assertThat(tasks.simplifyPath(path)).isEqualTo(expected);
        }

        @Test
        @DisplayName("names that merely contain dots are real directories")
        void dotsInNames() {
            assertThat(tasks.simplifyPath("/a/.../b/..hidden")).isEqualTo("/a/.../b/..hidden");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement slidingWindowMax()")
    @DisplayName("4. slidingWindowMax(numbers, k)")
    class SlidingWindowMaxTest {

        @Test
        @DisplayName("example from Javadoc: mixed values, k = 3")
        void javadocExample() {
            assertThat(tasks.slidingWindowMax(new int[]{1, 3, -1, -3, 5, 3, 6, 7}, 3))
                    .containsExactly(3, 3, 5, 5, 6, 7);
        }

        @Test
        @DisplayName("example from Javadoc: decreasing values")
        void decreasing() {
            assertThat(tasks.slidingWindowMax(new int[]{5, 4, 3, 2, 1}, 2)).containsExactly(5, 4, 3, 2);
        }

        @Test
        @DisplayName("example from Javadoc: k = 1 returns every element")
        void windowOfOne() {
            assertThat(tasks.slidingWindowMax(new int[]{9, 8}, 1)).containsExactly(9, 8);
        }

        @Test
        @DisplayName("example from Javadoc: k larger than the array gives no windows")
        void windowTooLarge() {
            assertThat(tasks.slidingWindowMax(new int[]{1, 2}, 5)).isEmpty();
        }

        @Test
        @DisplayName("increasing values: the maximum is always the newest element")
        void increasing() {
            assertThat(tasks.slidingWindowMax(new int[]{1, 2, 3, 4, 5}, 3)).containsExactly(3, 4, 5);
        }

        @Test
        @DisplayName("window as large as the array gives one maximum")
        void fullWindow() {
            assertThat(tasks.slidingWindowMax(new int[]{4, 9, 2}, 3)).containsExactly(9);
        }

        @Test
        @DisplayName("equal values do not break the window bookkeeping")
        void equalValues() {
            assertThat(tasks.slidingWindowMax(new int[]{2, 2, 2, 2}, 2)).containsExactly(2, 2, 2);
        }

        @Test
        @DisplayName("an empty array gives an empty result")
        void emptyArray() {
            assertThat(tasks.slidingWindowMax(new int[0], 3)).isEmpty();
        }

        @Test
        @DisplayName("k below 1 is rejected")
        void invalidK() {
            assertThatThrownBy(() -> tasks.slidingWindowMax(new int[]{1, 2}, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
