package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TddKataDemoTest — the finished FizzBuzz test suite, shown through a TDD lens.
 *
 * <p>Each {@code @Nested} class represents a step in the TDD session. Read them
 * top-to-bottom — that is the order the tests were actually written. The
 * {@code @DisplayName} of each class carries the RED→GREEN log.
 *
 * <p>This is not how tests normally get organised (you would group by concern,
 * not by history). It is organised by TDD step because the history is the
 * lesson — this file is a journal, not a reference.
 */
@DisplayName("TddKataDemo — the FizzBuzz kata as a TDD journal")
class TddKataDemoTest {

    private final TddKataDemo demo = new TddKataDemo();

    // ── Step 1 — the first RED test ───────────────────────────────────────────

    @Nested
    @DisplayName("Step 1 — RED: fizzbuzz(1) == \"1\"  →  GREEN: return String.valueOf(n)")
    class Step1 {

        @Test
        @DisplayName("1 → \"1\"  (simplest possible case)")
        void oneReturnsOne() {
            assertThat(demo.fizzbuzz(1)).isEqualTo("1");
        }
    }

    // ── Step 2 — still GREEN, no production code change needed ────────────────

    @Nested
    @DisplayName("Step 2 — GREEN on the same code: fizzbuzz(2) == \"2\"")
    class Step2 {

        @Test
        @DisplayName("2 → \"2\"  (confirms the general case works)")
        void twoReturnsTwo() {
            assertThat(demo.fizzbuzz(2)).isEqualTo("2");
        }

        @Test
        @DisplayName("4 → \"4\"  (another non-multiple)")
        void fourReturnsFour() {
            assertThat(demo.fizzbuzz(4)).isEqualTo("4");
        }
    }

    // ── Step 3 — RED, then GREEN: adds the % 3 rule ───────────────────────────

    @Nested
    @DisplayName("Step 3 — RED: fizzbuzz(3) == \"Fizz\"  →  GREEN: add `if (n % 3 == 0)`")
    class Step3 {

        @ParameterizedTest(name = "{0} → \"Fizz\"")
        @ValueSource(ints = {3, 6, 9, 12, 18})
        @DisplayName("multiples of 3 → \"Fizz\"")
        void multiplesOfThree(int n) {
            assertThat(demo.fizzbuzz(n)).isEqualTo("Fizz");
        }

        @Test
        @DisplayName("still not-a-multiple: 7 → \"7\"  (regression guard)")
        void stillReturnsNumber() {
            assertThat(demo.fizzbuzz(7)).isEqualTo("7");
        }
    }

    // ── Step 4 — RED, then GREEN: adds the % 5 rule ───────────────────────────

    @Nested
    @DisplayName("Step 4 — RED: fizzbuzz(5) == \"Buzz\"  →  GREEN: add `if (n % 5 == 0)`")
    class Step4 {

        @ParameterizedTest(name = "{0} → \"Buzz\"")
        @ValueSource(ints = {5, 10, 20, 25, 35})
        @DisplayName("multiples of 5 (not 3) → \"Buzz\"")
        void multiplesOfFive(int n) {
            assertThat(demo.fizzbuzz(n)).isEqualTo("Buzz");
        }
    }

    // ── Step 5 — RED, then GREEN: adds the % 15 rule (ORDER MATTERS) ──────────

    @Nested
    @DisplayName("Step 5 — RED: fizzbuzz(15) == \"FizzBuzz\"  →  GREEN: add `% 15` check FIRST")
    class Step5 {

        @ParameterizedTest(name = "{0} → \"FizzBuzz\"")
        @ValueSource(ints = {15, 30, 45, 60, 90})
        @DisplayName("multiples of 15 → \"FizzBuzz\"")
        void multiplesOfFifteen(int n) {
            assertThat(demo.fizzbuzz(n)).isEqualTo("FizzBuzz");
        }
    }

    // ── Step 6 — REFACTOR: no new behaviour, all tests stay GREEN ─────────────

    @Nested
    @DisplayName("Step 6 — REFACTOR: order the ifs so % 15 comes first")
    class Step6 {

        @Test
        @DisplayName("30 is BOTH a multiple of 3 and 5 — must be \"FizzBuzz\"")
        void thirtyIsFizzBuzz() {
            // The classic bug: if you check % 3 before % 15, this returns "Fizz".
            assertThat(demo.fizzbuzz(30)).isEqualTo("FizzBuzz");
        }

        @Test
        @DisplayName("45 is BOTH — must be \"FizzBuzz\"")
        void fortyFiveIsFizzBuzz() {
            assertThat(demo.fizzbuzz(45)).isEqualTo("FizzBuzz");
        }
    }
}