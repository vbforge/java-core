package com.vbforge.mockito_and_tdd;

/**
 * TddCycleTasks — implement every method below to make {@code TddCycleTasksTest} go GREEN.
 *
 * <p><b>Workflow (this one is a TDD exercise — go slowly):</b>
 * <ol>
 *   <li>Read {@code TddCycleDemo.java} to see the red → green → refactor loop.</li>
 *   <li>Open {@code TddCycleTasksTest.java} and remove {@code @Disabled} from <em>one</em> test
 *       class. Run it and watch it fail: that is your <b>red</b>.</li>
 *   <li>Write the simplest code that passes — even a hard-coded value is allowed at first
 *       (<b>green</b>). Then enable more of the tests and generalise.</li>
 *   <li>With everything green, tidy the code (<b>refactor</b>) and re-run.</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class TddCycleTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Classic FizzBuzz for a single number: returns {@code "Fizz"} for multiples of 3,
     * {@code "Buzz"} for multiples of 5, {@code "FizzBuzz"} for multiples of both, and
     * the number itself as text otherwise.
     *
     * <p>Examples:
     * <pre>
     *   fizzBuzz(1)   → "1"
     *   fizzBuzz(3)   → "Fizz"
     *   fizzBuzz(5)   → "Buzz"
     *   fizzBuzz(15)  → "FizzBuzz"
     *   fizzBuzz(98)  → "98"
     *   fizzBuzz(0)   → throws IllegalArgumentException
     *   fizzBuzz(-3)  → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Do the tests one at a time: first make {@code 1 → "1"} pass, then 3, then 5, then 15.</li>
     *   <li>The order in which you check the divisors matters.</li>
     * </ul>
     *
     * @param n a positive integer
     * @return the FizzBuzz text for {@code n}
     * @throws IllegalArgumentException if {@code n < 1}
     */
    public String fizzBuzz(int n) {
        throw new UnsupportedOperationException("TODO: implement fizzBuzz");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Checks a password against three rules, all of which must hold:
     * at least 8 characters, at least one digit, and both an uppercase and a
     * lowercase letter. {@code null} is simply invalid.
     *
     * <p>Examples:
     * <pre>
     *   isValidPassword("Abcdef12")  → true
     *   isValidPassword("abcdefg1")  → false   (no uppercase)
     *   isValidPassword("ABCDEFG1")  → false   (no lowercase)
     *   isValidPassword("Abcdefgh")  → false   (no digit)
     *   isValidPassword("Ab1")       → false   (too short)
     *   isValidPassword("")          → false
     *   isValidPassword(null)        → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Add one rule per red-green step — the tests are ordered that way.</li>
     *   <li>{@link Character} has {@code isDigit}, {@code isUpperCase} and {@code isLowerCase}.</li>
     * </ul>
     *
     * @param password the candidate password (may be {@code null})
     * @return {@code true} only if all rules are satisfied
     */
    public boolean isValidPassword(String password) {
        throw new UnsupportedOperationException("TODO: implement isValidPassword");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Converts an integer from 1 to 3999 to a Roman numeral.
     *
     * <p>Examples:
     * <pre>
     *   toRoman(1)     → "I"
     *   toRoman(4)     → "IV"
     *   toRoman(9)     → "IX"
     *   toRoman(14)    → "XIV"
     *   toRoman(58)    → "LVIII"
     *   toRoman(400)   → "CD"
     *   toRoman(1994)  → "MCMXCIV"
     *   toRoman(3999)  → "MMMCMXCIX"
     *   toRoman(0)     → throws IllegalArgumentException
     *   toRoman(4000)  → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Start with just 1, 2, 3 and 5 — let each new failing test force the next idea.</li>
     *   <li>Think of it as making change: the "coins" include the subtractive pairs
     *       such as 900, 400, 90, 40, 9 and 4.</li>
     * </ul>
     *
     * @param number the number to convert, 1 to 3999
     * @return the Roman numeral
     * @throws IllegalArgumentException if {@code number} is outside 1..3999
     */
    public String toRoman(int number) {
        throw new UnsupportedOperationException("TODO: implement toRoman");
    }
}
