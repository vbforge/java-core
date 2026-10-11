package com.vbforge.unit_testing_junit5_assertj;

/**
 * TddKataTasks — your TDD exercise.
 *
 * <p><b>This is a different kind of task.</b> All other tasks in this module ship
 * with a full test suite and you implement the production code. Here you do the
 * opposite: <em>you write the tests yourself</em>, one at a time, following the
 * RED → GREEN → REFACTOR cycle.
 *
 * <p><b>The exercise:</b> implement {@link #fizzbuzz(int)} following FizzBuzz
 * rules. The twist — you must add each rule by first adding a failing test.
 *
 * <p><b>Suggested TDD sequence</b> (each step starts with a new test):
 * <pre>
 *   Step 1 — assert fizzbuzz(1) == "1"        → simplest possible code
 *   Step 2 — assert fizzbuzz(2) == "2"        → verifies the general case
 *   Step 3 — assert fizzbuzz(3) == "Fizz"     → adds the % 3 rule
 *   Step 4 — assert fizzbuzz(5) == "Buzz"     → adds the % 5 rule
 *   Step 5 — assert fizzbuzz(15) == "FizzBuzz" → adds the % 15 rule (order!)
 * </pre>
 *
 * <p><b>How to run the cycle:</b>
 * <ol>
 *   <li>Open {@code TddKataTasksTest.java} — only step 1 is active, all other
 *       nested classes are empty on purpose.</li>
 *   <li>Run it — RED. Write the simplest body in this class that makes it pass.</li>
 *   <li>Add the next test yourself (uncomment / paste the template from the
 *       Javadoc examples below).</li>
 *   <li>Run — RED. Make it pass. Repeat.</li>
 * </ol>
 *
 * <p><b>Rule of TDD:</b> never write a line of production code that a failing
 * test did not ask for. If you find yourself writing {@code if (n % 3 == 0)}
 * before you have a failing test for {@code fizzbuzz(3)}, you are not doing TDD.
 */
public class TddKataTasks {

    /**
     * Returns the FizzBuzz representation of {@code n ≥ 1}.
     *
     * <p>Rules:
     * <ul>
     *   <li>{@code n % 3 == 0 && n % 5 == 0} → {@code "FizzBuzz"}</li>
     *   <li>{@code n % 3 == 0}               → {@code "Fizz"}</li>
     *   <li>{@code n % 5 == 0}               → {@code "Buzz"}</li>
     *   <li>otherwise                        → {@code String.valueOf(n)}</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   fizzbuzz(1)  → "1"
     *   fizzbuzz(2)  → "2"
     *   fizzbuzz(3)  → "Fizz"
     *   fizzbuzz(5)  → "Buzz"
     *   fizzbuzz(15) → "FizzBuzz"
     *   fizzbuzz(30) → "FizzBuzz"
     *   fizzbuzz(7)  → "7"
     * </pre>
     *
     * @param n integer {@code ≥ 1}
     * @return "Fizz", "Buzz", "FizzBuzz", or {@code String.valueOf(n)}
     */
    public String fizzbuzz(int n) {
        throw new UnsupportedOperationException("TODO: implement fizzbuzz");
    }
}