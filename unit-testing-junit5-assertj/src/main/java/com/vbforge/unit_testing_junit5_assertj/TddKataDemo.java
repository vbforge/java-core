package com.vbforge.unit_testing_junit5_assertj;

/**
 * TddKataDemo — the finished FizzBuzz kata, annotated with the TDD steps
 * that produced it.
 *
 * <p>This file is intentionally tiny. The teaching artifact is
 * {@link TddKataDemoTest} — read that next. It shows the tests <em>in the
 * order they were written</em>, not the order they normally appear in.
 *
 * <p>The TDD cycle — Red, Green, Refactor:
 * <ol>
 *   <li><b>RED</b> — write a test for a behaviour that does not exist yet.
 *       Run it. It fails.</li>
 *   <li><b>GREEN</b> — write the simplest code that makes the test pass.
 *       Nothing more.</li>
 *   <li><b>REFACTOR</b> — with all tests green, clean up. Tests stay green.</li>
 *   <li>Repeat for the next small behaviour.</li>
 * </ol>
 *
 * <p>Rule of the kata: <em>never write production code until a failing test
 * demands it.</em> Each line of the implementation below traces back to a
 * specific test — see the step numbers in the inline comments.
 */
public class TddKataDemo {

    /**
     * FizzBuzz for {@code n ≥ 1}:
     * <ul>
     *   <li>Multiple of 3 and 5 → {@code "FizzBuzz"}</li>
     *   <li>Multiple of 3       → {@code "Fizz"}</li>
     *   <li>Multiple of 5       → {@code "Buzz"}</li>
     *   <li>Anything else       → {@code String.valueOf(n)}</li>
     * </ul>
     *
     * <p>Behaviour for {@code n < 1} is intentionally unspecified — the kata
     * scope is {@code n ≥ 1}. Real TDD would add a test before extending the
     * contract.
     *
     * @param n an integer, {@code ≥ 1}
     * @return the FizzBuzz representation of {@code n}
     */
    public String fizzbuzz(int n) {
        // Step 5 — introduced when test 5 (15 → "FizzBuzz") went RED.
        // Must be checked FIRST, or the % 3 rule would fire and never reach here.
        if (n % 15 == 0) {
            return "FizzBuzz";
        }
        // Step 3 — introduced when test 3 (3 → "Fizz") went RED.
        if (n % 3 == 0) {
            return "Fizz";
        }
        // Step 4 — introduced when test 4 (5 → "Buzz") went RED.
        if (n % 5 == 0) {
            return "Buzz";
        }
        // Step 1 — the very first line written: simplest thing that made
        // test 1 (1 → "1") pass. Still the fall-through here.
        return String.valueOf(n);
    }

    public static void main(String[] args) {
        System.out.println("=== TDD FizzBuzz — the RED→GREEN log is in TddKataDemoTest ===");
        System.out.println();
        TddKataDemo d = new TddKataDemo();
        for (int i = 1; i <= 20; i++) {
            System.out.printf("%3d → %s%n", i, d.fizzbuzz(i));
        }
        System.out.println();
        System.out.println("Try the exercise yourself:");
        System.out.println("  1. Open TddKataTasksTest.java — it contains only the FIRST test, active.");
        System.out.println("  2. Run it. RED.");
        System.out.println("  3. Open TddKataTasks.java — make it pass with the simplest code.");
        System.out.println("  4. Add the next test yourself. RED. Then GREEN. Repeat.");
    }
}