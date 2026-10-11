package com.vbforge.unit_testing_junit5_assertj;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * TddKataTasksTest — your TDD exercise.
 *
 * <p><b>Read this carefully — this test file is different from every other
 * {@code *TasksTest} in the repo.</b>
 *
 * <p>All other task tests ship with the full suite pre-written and {@code @Disabled}
 * at the {@code @Nested} level. <em>This</em> file ships with only the first
 * test active. Your job is to add the remaining tests yourself, in RED → GREEN
 * order, before writing the corresponding production code.
 *
 * <p><b>How to proceed:</b>
 * <ol>
 *   <li>Run {@code mvn test -Dtest=TddKataTasksTest} — <b>Step 1 is RED</b>
 *       (it currently calls a stub that throws).</li>
 *   <li>Open {@code TddKataTasks.java} and write the simplest possible body
 *       that makes step 1 pass. Nothing else.</li>
 *   <li>Run again — <b>GREEN</b>.</li>
 *   <li>Now uncomment (or hand-write) Step 2's test. Run — still GREEN.</li>
 *   <li>Step 3's test. Run — RED. Extend the production code. GREEN.</li>
 *   <li>Continue until all 5 steps are GREEN, then optionally refactor.</li>
 * </ol>
 *
 * <p><b>The rule:</b> you may not write production code that a failing test did
 * not ask for. Do not look at {@code TddKataDemo.java} until you have finished —
 * that defeats the exercise.
 */
@DisplayName("TddKataTasks — your TDD journal (add the tests yourself)")
class TddKataTasksTest {

    private TddKataTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TddKataTasks();
    }

    // ── Step 1 — the only active test you get for free ────────────────────────

    @Test
    @DisplayName("Step 1 — fizzbuzz(1) == \"1\"")
    void step1_returnsTheNumber() {
        assertThat(tasks.fizzbuzz(1)).isEqualTo("1");
    }

    // ── Step 2 — uncomment to activate after step 1 is green ──────────────────

    // @Test
    // @DisplayName("Step 2 — fizzbuzz(2) == \"2\"")
    // void step2_returnsAnotherNumber() {
    //     assertThat(tasks.fizzbuzz(2)).isEqualTo("2");
    // }

    // ── Step 3 — uncomment AFTER writing the failing test yourself ────────────

    // @Test
    // @DisplayName("Step 3 — fizzbuzz(3) == \"Fizz\"")
    // void step3_threeIsFizz() {
    //     assertThat(tasks.fizzbuzz(3)).isEqualTo("Fizz");
    // }

    // ── Step 4 — uncomment AFTER writing the failing test yourself ────────────

    // @Test
    // @DisplayName("Step 4 — fizzbuzz(5) == \"Buzz\"")
    // void step4_fiveIsBuzz() {
    //     assertThat(tasks.fizzbuzz(5)).isEqualTo("Buzz");
    // }

    // ── Step 5 — uncomment AFTER writing the failing test yourself ────────────

    // @Test
    // @DisplayName("Step 5 — fizzbuzz(15) == \"FizzBuzz\"")
    // void step5_fifteenIsFizzBuzz() {
    //     assertThat(tasks.fizzbuzz(15)).isEqualTo("FizzBuzz");
    // }

    // ── Optional — a nested-class grouping if you prefer structure ────────────

    /**
     * If you prefer the repo's {@code @Nested} convention, replace the flat
     * tests above with the following skeleton and fill it in yourself:
     *
     * <pre>
     *   &#64;Nested
     *   &#64;DisplayName("Step 1 — return the number")
     *   class Step1 {
     *       &#64;Test
     *       &#64;DisplayName("1 → \"1\"")
     *       void one() { assertThat(tasks.fizzbuzz(1)).isEqualTo("1"); }
     *   }
     *   // ...and so on for Steps 2–5
     * </pre>
     *
     * <p>Whatever you choose, the discipline is the same: one failing test,
     * then the simplest code that makes it pass.
     */
    @Nested
    @DisplayName("(place your own @Nested steps here)")
    class YourTddJournal {

        @Test
        @DisplayName("placeholder — delete me when you start writing your own steps")
        void placeholder() {
            // Intentionally empty. Delete this class once you have added your own steps.
        }
    }
}