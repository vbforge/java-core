package com.vbforge.mockito_and_tdd;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TddCycleDemo — annotated reference for test-driven development's
 * <b>red → green → refactor</b> loop, using the classic "String Calculator" kata.
 *
 * <p>You cannot ship a failing test, so each stage of the cycle is frozen as its own
 * class: {@link CalculatorV1} is the <em>red</em> state (it fails the new requirement),
 * {@link CalculatorV2} the <em>green</em> state (simplest code that passes), and
 * {@link CalculatorV3} the <em>refactored</em> state (same behaviour, cleaner code).
 * {@code TddCycleDemoTest} proves each claim.
 *
 * <p>The requirements arrive one at a time, each as a new failing test:
 * <ol>
 *   <li>{@code ""} → 0, a single number → itself</li>
 *   <li>{@code "1,2"} → 3 — several numbers, comma separated</li>
 *   <li>{@code "1\n2,3"} → 6 — newlines are separators too</li>
 *   <li>negative numbers are rejected: {@code IllegalArgumentException("negatives not allowed: -1,-2")}</li>
 *   <li>numbers above 1000 are ignored; 1000 itself counts</li>
 * </ol>
 */
public class TddCycleDemo {

    public static void main(String[] args) {
        redDemo();
        System.out.println("---");
        fakeItAndTriangulateDemo();
        System.out.println("---");
        greenDemo();
        System.out.println("---");
        refactorDemo();
    }

    // ── The specification (one row per requirement) ───────────────────────────

    /** {input, expected sum} — the executable spec every finished calculator must satisfy. */
    static final Object[][] SPEC = {
            {"",          0},
            {"7",         7},
            {"1,2",       3},
            {"1,2,3",     6},
            {"1\n2,3",    6},
            {"2,1001",    2},
            {"1000,1",    1001},
    };

    public interface Calculator {
        int add(String numbers);
    }

    /**
     * RED. Written when only requirement 1 existed: it handles {@code ""} and one number
     * — and silently ignores everything after the first comma.
     */
    public static class CalculatorV1 implements Calculator {
        @Override
        public int add(String numbers) {
            if (numbers.isEmpty()) {
                return 0;
            }
            return Integer.parseInt(numbers.split(",")[0]);
        }
    }

    /**
     * A "fake it till you make it" step: the cheapest code that turns one specific test
     * green is a hard-coded constant.
     */
    public static class CalculatorFakeIt implements Calculator {
        @Override
        public int add(String numbers) {
            return numbers.isEmpty() ? 0 : 3;
        }
    }

    /** GREEN. The simplest straightforward code that satisfies every requirement. */
    public static class CalculatorV2 implements Calculator {
        @Override
        public int add(String numbers) {
            if (numbers.isEmpty()) {
                return 0;
            }
            String[] parts = numbers.split("[,\n]");
            int sum = 0;
            StringBuilder negatives = new StringBuilder();
            for (String part : parts) {
                int value = Integer.parseInt(part);
                if (value < 0) {
                    if (negatives.length() > 0) {
                        negatives.append(",");
                    }
                    negatives.append(value);
                } else if (value <= 1000) {
                    sum += value;
                }
            }
            if (negatives.length() > 0) {
                throw new IllegalArgumentException("negatives not allowed: " + negatives);
            }
            return sum;
        }
    }

    /** REFACTORED. Identical behaviour to V2, expressed in small, named steps. */
    public static class CalculatorV3 implements Calculator {
        private static final String SEPARATORS = "[,\n]";
        private static final int MAX_COUNTED = 1000;

        @Override
        public int add(String numbers) {
            if (numbers.isEmpty()) {
                return 0;
            }
            List<Integer> values = parse(numbers);
            rejectNegatives(values);
            return values.stream()
                    .filter(value -> value <= MAX_COUNTED)
                    .mapToInt(Integer::intValue)
                    .sum();
        }

        private List<Integer> parse(String numbers) {
            List<Integer> values = new ArrayList<>();
            for (String part : numbers.split(SEPARATORS)) {
                values.add(Integer.parseInt(part));
            }
            return values;
        }

        private void rejectNegatives(List<Integer> values) {
            String negatives = values.stream()
                    .filter(value -> value < 0)
                    .map(String::valueOf)
                    .collect(Collectors.joining(","));
            if (!negatives.isEmpty()) {
                throw new IllegalArgumentException("negatives not allowed: " + negatives);
            }
        }
    }

    /**
     * Runs {@link #SPEC} against a calculator and returns how many rows failed.
     * An exception counts as a failure. This is the "test suite" for the demo.
     */
    static int failures(Calculator calculator) {
        int failed = 0;
        for (Object[] row : SPEC) {
            String input = (String) row[0];
            int expected = (Integer) row[1];
            try {
                if (calculator.add(input) != expected) {
                    failed++;
                }
            } catch (RuntimeException e) {
                failed++;
            }
        }
        return failed;
    }

    // ── 1. Red ────────────────────────────────────────────────────────────────

    /**
     * RED: write a test for the next requirement and watch it fail — for the right reason.
     *
     * <p>WHY this matters: a test you have never seen fail proves nothing; it might pass
     * even with no implementation. Seeing "expected 3 but was 1" confirms the test can
     * detect the missing behaviour.
     *
     * <p>Common gotcha: <em>wrong-reason reds</em>. If the new test fails with a typo or
     * {@code NullPointerException}, fix that first — a red you did not expect teaches you nothing.
     * Here {@code "1,2"} fails with a wrong <em>value</em> (1, not 3), which is the right reason.
     */
    static void redDemo() {
        System.out.println("=== Red ===");

        Calculator v1 = new CalculatorV1();
        System.out.println("add(\"\")       : " + v1.add(""));      // 0  — requirement 1 is green
        System.out.println("add(\"7\")      : " + v1.add("7"));     // 7  — still green
        System.out.println("add(\"1,2\")    : " + v1.add("1,2"));   // 1  — expected 3: RED, for the right reason
        System.out.println("spec failures  : " + failures(v1));
    }

    // ── 2. Fake it, then triangulate ──────────────────────────────────────────

    /**
     * The smallest step to green is sometimes a constant. A second example then
     * <em>forces</em> the real algorithm — this is called triangulation.
     *
     * <p>WHY this matters: it keeps steps tiny (minutes, not hours) and means every line
     * of production code exists because a test demanded it.
     *
     * <p>Common gotcha: do not skip straight to the clever general solution — you will
     * write code no test justifies and miss the cases the tests would have revealed.
     */
    static void fakeItAndTriangulateDemo() {
        System.out.println("=== Fake it, then triangulate ===");

        Calculator fake = new CalculatorFakeIt();
        System.out.println("add(\"1,2\")    : " + fake.add("1,2"));   // 3 — green with a hard-coded 3
        System.out.println("add(\"4,5\")    : " + fake.add("4,5"));   // 3 — expected 9: red again, now generalise
    }

    // ── 3. Green ──────────────────────────────────────────────────────────────

    /**
     * GREEN: write the simplest code that makes <em>all</em> tests pass. Ugly is fine.
     *
     * <p>WHY this matters: separating "make it work" from "make it clean" keeps you from
     * doing both badly at once. The loops and {@code StringBuilder} in V2 are not pretty,
     * but they are obviously correct, and now they are protected by tests.
     *
     * <p>Time complexity of V2: O(n) in the length of the input.
     */
    static void greenDemo() {
        System.out.println("=== Green ===");

        Calculator v2 = new CalculatorV2();
        System.out.println("add(\"1\\n2,3\") : " + v2.add("1\n2,3"));      // 6
        System.out.println("add(\"2,1001\") : " + v2.add("2,1001"));      // 2 — 1001 ignored
        System.out.println("spec failures  : " + failures(v2));            // 0
        try {
            v2.add("1,-2,-3");
        } catch (IllegalArgumentException e) {
            System.out.println("negatives     : " + e.getMessage());
        }
    }

    // ── 4. Refactor ───────────────────────────────────────────────────────────

    /**
     * REFACTOR: improve the structure without changing behaviour, with the tests green
     * the whole time. If a test goes red, you changed behaviour — undo it.
     *
     * <p>WHY this matters: the test suite is the safety net that makes cleaning up cheap.
     * Without it, refactoring is a gamble, so code is left to rot.
     *
     * <p>Common gotcha: refactor only on green, and never mix it with new behaviour in the
     * same step. Each loop of the cycle does exactly one of the two.
     */
    static void refactorDemo() {
        System.out.println("=== Refactor ===");

        Calculator v2 = new CalculatorV2();
        Calculator v3 = new CalculatorV3();
        for (Object[] row : SPEC) {
            String input = (String) row[0];
            System.out.println("same result   : " + (v2.add(input) == v3.add(input))
                    + "  for \"" + input.replace("\n", "\\n") + "\"");
        }
        System.out.println("spec failures  : " + failures(v3));   // 0
    }
}
