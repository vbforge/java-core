package com.vbforge.basics.operators;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("Operators and Expressions")
class OperatorsDemoTest {

    // ── Arithmetic ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("Arithmetic operators")
    class Arithmetic {

        @Test
        @DisplayName("Basic arithmetic: + - * / %")
        void basicArithmetic() {
            assertThat(10 + 3).isEqualTo(13);
            assertThat(10 - 3).isEqualTo(7);
            assertThat(10 * 3).isEqualTo(30);
            assertThat(10 / 3).isEqualTo(3);    // integer division
            assertThat(10 % 3).isEqualTo(1);    // remainder
        }

        @Test
        @DisplayName("Integer division truncates — cast to double for exact result")
        void integerDivisionTruncates() {
            assertThat(5 / 2).isEqualTo(2);                      // NOT 2.5
            assertThat((double) 5 / 2).isEqualTo(2.5);           // cast first operand
            assertThat(5 / 2.0).isEqualTo(2.5);                  // or use double literal
        }

        @Test
        @DisplayName("Remainder sign follows the dividend, not divisor")
        void remainderSign() {
            assertThat(-7 % 3).isEqualTo(-1);   // negative dividend → negative remainder
            assertThat(7 % -3).isEqualTo(1);    // positive dividend → positive remainder
            assertThat(-7 % -3).isEqualTo(-1);
        }

        @Test
        @DisplayName("Double division by zero produces Infinity, not an exception")
        void doubleDivisionByZero() {
            double result = 1.0 / 0.0;
            assertThat(result).isEqualTo(Double.POSITIVE_INFINITY);
            assertThat(Double.isInfinite(result)).isTrue();
        }

        @Test
        @DisplayName("Integer division by zero throws ArithmeticException")
        void intDivisionByZeroThrows() {
            assertThatThrownBy(() -> {
                int x = 1 / 0;
            }).isInstanceOf(ArithmeticException.class)
              .hasMessageContaining("/ by zero");
        }

        @ParameterizedTest(name = "{0} + {1} = {2}")
        @CsvSource({"1, 2, 3", "0, 0, 0", "-5, 5, 0", "-3, -4, -7", "100, 200, 300"})
        @DisplayName("Addition works for various int pairs")
        void additionParameterized(int a, int b, int expected) {
            assertThat(a + b).isEqualTo(expected);
        }

        @ParameterizedTest(name = "{0} % {1} = {2}")
        @CsvSource({"10, 3, 1", "9, 3, 0", "7, 2, 1", "100, 7, 2"})
        @DisplayName("Modulo remainder for various inputs")
        void remainderParameterized(int a, int b, int expected) {
            assertThat(a % b).isEqualTo(expected);
        }
    }


    // ── Increment & Decrement ─────────────────────────────────────────────────

    @Nested
    @DisplayName("Increment and decrement operators")
    class IncrementDecrement {

        @Test
        @DisplayName("Post-increment returns current value, then increments")
        void postIncrement() {
            int x = 5;
            int result = x++;
            assertThat(result).isEqualTo(5);    // old value returned
            assertThat(x).isEqualTo(6);         // then incremented
        }

        @Test
        @DisplayName("Pre-increment increments first, then returns new value")
        void preIncrement() {
            int x = 5;
            int result = ++x;
            assertThat(result).isEqualTo(6);    // incremented first
            assertThat(x).isEqualTo(6);
        }

        @Test
        @DisplayName("Post-decrement returns current value, then decrements")
        void postDecrement() {
            int x = 5;
            int result = x--;
            assertThat(result).isEqualTo(5);
            assertThat(x).isEqualTo(4);
        }

        @Test
        @DisplayName("Pre-decrement decrements first, then returns new value")
        void preDecrement() {
            int x = 5;
            int result = --x;
            assertThat(result).isEqualTo(4);
            assertThat(x).isEqualTo(4);
        }
    }


    // ── Comparison Operators ──────────────────────────────────────────────────

    @Nested
    @DisplayName("Comparison operators")
    class Comparison {

        @Test
        @DisplayName("Primitive comparisons work by value")
        void primitiveComparisons() {
            assertThat(5 == 5).isTrue();
            assertThat(5 != 6).isTrue();
            assertThat(3 < 5).isTrue();
            assertThat(5 <= 5).isTrue();
            assertThat(6 > 5).isTrue();
            assertThat(5 >= 5).isTrue();
        }

        @Test
        @DisplayName("String == compares references, not content")
        void stringReferenceEquality() {
            String s1 = new String("hello");
            String s2 = new String("hello");
            // noinspection StringEquality — intentional reference test
            assertThat(s1 == s2).isFalse();         // different objects
            assertThat(s1.equals(s2)).isTrue();     // same content
        }

        @Test
        @DisplayName("String literals from pool share reference")
        void stringPoolEquality() {
            String s1 = "hello";
            String s2 = "hello";
            // noinspection StringEquality — intentional pool test
            assertThat(s1 == s2).isTrue();          // same pooled object
        }
    }


    // ── Logical Operators ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Logical operators and short-circuit evaluation")
    class Logical {

        @Test
        @DisplayName("AND (&&): both must be true")
        void logicalAnd() {
            assertThat(true  && true).isTrue();
            assertThat(true  && false).isFalse();
            assertThat(false && true).isFalse();
            assertThat(false && false).isFalse();
        }

        @Test
        @DisplayName("OR (||): at least one must be true")
        void logicalOr() {
            assertThat(true  || true).isTrue();
            assertThat(true  || false).isTrue();
            assertThat(false || true).isTrue();
            assertThat(false || false).isFalse();
        }

        @Test
        @DisplayName("NOT (!): inverts boolean")
        void logicalNot() {
            assertThat(!true).isFalse();
            assertThat(!false).isTrue();
        }

        @Test
        @DisplayName("XOR (^): true when operands differ")
        void logicalXor() {
            assertThat(true  ^ false).isTrue();
            assertThat(false ^ true).isTrue();
            assertThat(true  ^ true).isFalse();
            assertThat(false ^ false).isFalse();
        }

        @Test
        @DisplayName("Short-circuit &&: right side NOT evaluated when left is false")
        void shortCircuitAnd() {
            int[] counter = {0};
            boolean result = false && (++counter[0] > 0); // right side skipped
            assertThat(result).isFalse();
            assertThat(counter[0]).isEqualTo(0);          // never incremented
        }

        @Test
        @DisplayName("Short-circuit ||: right side NOT evaluated when left is true")
        void shortCircuitOr() {
            int[] counter = {0};
            boolean result = true || (++counter[0] > 0);  // right side skipped
            assertThat(result).isTrue();
            assertThat(counter[0]).isEqualTo(0);          // never incremented
        }

        @Test
        @DisplayName("Non-short-circuit &: BOTH sides always evaluated")
        void nonShortCircuitAnd() {
            int[] counter = {0};
            boolean result = false & (++counter[0] > 0);  // right side always runs
            assertThat(result).isFalse();
            assertThat(counter[0]).isEqualTo(1);          // was incremented
        }
    }


    // ── Bitwise Operators ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("Bitwise and shift operators")
    class Bitwise {

        @Test
        @DisplayName("Bitwise AND: bit is 1 only when both bits are 1")
        void bitwiseAnd() {
            // 5 = 0101, 3 = 0011, 5&3 = 0001 = 1
            assertThat(5 & 3).isEqualTo(1);
            assertThat(0b1010 & 0b1100).isEqualTo(0b1000);   // 8
        }

        @Test
        @DisplayName("Bitwise OR: bit is 1 when either bit is 1")
        void bitwiseOr() {
            // 5 = 0101, 3 = 0011, 5|3 = 0111 = 7
            assertThat(5 | 3).isEqualTo(7);
        }

        @Test
        @DisplayName("Bitwise XOR: bit is 1 when bits differ")
        void bitwiseXor() {
            // 5 = 0101, 3 = 0011, 5^3 = 0110 = 6
            assertThat(5 ^ 3).isEqualTo(6);
        }

        @Test
        @DisplayName("Bitwise NOT (~): flips all bits — result is -(n+1)")
        void bitwiseNot() {
            assertThat(~5).isEqualTo(-6);
            assertThat(~0).isEqualTo(-1);
        }

        @Test
        @DisplayName("Left shift << multiplies by 2^n")
        void leftShift() {
            assertThat(1 << 0).isEqualTo(1);    // 1 * 2^0
            assertThat(1 << 1).isEqualTo(2);    // 1 * 2^1
            assertThat(1 << 4).isEqualTo(16);   // 1 * 2^4
            assertThat(5 << 2).isEqualTo(20);   // 5 * 4
        }

        @Test
        @DisplayName("Signed right shift >> divides by 2^n (preserves sign)")
        void signedRightShift() {
            assertThat(20 >> 2).isEqualTo(5);   // 20 / 4
            assertThat(-8 >> 1).isEqualTo(-4);  // sign preserved: -8 / 2
        }

        @Test
        @DisplayName("Odd/even check via bitwise AND is a common optimization")
        void oddEvenCheck() {
            assertThat(5 & 1).isEqualTo(1);     // odd
            assertThat(4 & 1).isEqualTo(0);     // even
            assertThat(7 & 1).isEqualTo(1);     // odd
            assertThat(100 & 1).isEqualTo(0);   // even
        }

        @ParameterizedTest(name = "{0} << 1 = {1}")
        @CsvSource({"1, 2", "2, 4", "3, 6", "8, 16", "16, 32"})
        @DisplayName("Left shift by 1 always doubles the value")
        void leftShiftByOneDoubles(int input, int expected) {
            assertThat(input << 1).isEqualTo(expected);
        }
    }


    // ── Ternary Operator ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("Ternary operator")
    class Ternary {

        @Test
        @DisplayName("Ternary returns true-branch value when condition is true")
        void ternaryTrue() {
            int x = 10;
            String result = x > 5 ? "big" : "small";
            assertThat(result).isEqualTo("big");
        }

        @Test
        @DisplayName("Ternary returns false-branch value when condition is false")
        void ternaryFalse() {
            int x = 2;
            String result = x > 5 ? "big" : "small";
            assertThat(result).isEqualTo("small");
        }

        @Test
        @DisplayName("Ternary can compute absolute value")
        void ternaryAbsoluteValue() {
            assertThat(-7 < 0 ? 7 : -7).isEqualTo(7);
            assertThat(5 < 0 ? -5 : 5).isEqualTo(5);
        }

        @Test
        @DisplayName("Nested ternary can replicate grade logic (use sparingly)")
        void nestedTernaryGrade() {
            int score = 75;
            String grade = score >= 90 ? "A"
                         : score >= 80 ? "B"
                         : score >= 70 ? "C"
                         : "F";
            assertThat(grade).isEqualTo("C");
        }

        @ParameterizedTest(name = "score={0} → grade={1}")
        @CsvSource({"95, A", "85, B", "75, C", "65, F", "90, A", "80, B", "70, C"})
        @DisplayName("Grade assignment via ternary chain covers all boundaries")
        void gradeParameterized(int score, String expectedGrade) {
            String grade = score >= 90 ? "A"
                         : score >= 80 ? "B"
                         : score >= 70 ? "C"
                         : "F";
            assertThat(grade).isEqualTo(expectedGrade);
        }
    }


    // ── Operator Precedence ───────────────────────────────────────────────────

    @Nested
    @DisplayName("Operator precedence")
    class Precedence {

        @Test
        @DisplayName("* has higher precedence than + — evaluated first")
        void multiplicationBeforeAddition() {
            assertThat(2 + 3 * 4).isEqualTo(14);    // 2 + (3*4) = 14
            assertThat((2 + 3) * 4).isEqualTo(20);  // explicit grouping
        }

        @Test
        @DisplayName("&& has higher precedence than || — affects boolean logic")
        void andBeforeOr() {
            // true || (false && false) → true || false → true
            assertThat(true || false && false).isTrue();
            // (true || false) && false → true && false → false
            assertThat((true || false) && false).isFalse();
        }

        @Test
        @DisplayName("Unary minus applies before multiplication")
        void unaryMinusPrecedence() {
            int x = 2;
            assertThat(-x * 3).isEqualTo(-6);   // (-2) * 3
        }

        @Test
        @DisplayName("Parentheses always override default precedence")
        void parenthesesOverride() {
            assertThat(10 - 2 * 3).isEqualTo(4);   // 10 - (2*3) = 4
            assertThat((10 - 2) * 3).isEqualTo(24); // (8) * 3   = 24
        }
    }
}
