package com.vbforge.unit_testing_junit5_assertj;

/**
 * ParameterizedTasks — implement every method below to make {@code ParameterizedTasksTest} go GREEN.
 *
 * <p>These methods were chosen because they are naturally tested with
 * <em>many</em> inputs — the perfect fit for {@code @ParameterizedTest}.
 */
public class ParameterizedTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code n} is a prime number.
     *
     * <p>Rules:
     * <ul>
     *   <li>Prime numbers are ≥ 2 and have exactly two divisors: 1 and themselves.</li>
     *   <li>{@code n < 2} → {@code false} (covers 0, 1, and negatives)</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   isPrime(2)   → true
     *   isPrime(3)   → true
     *   isPrime(4)   → false
     *   isPrime(17)  → true
     *   isPrime(25)  → false
     *   isPrime(97)  → true
     *   isPrime(1)   → false
     *   isPrime(-7)  → false
     *   isPrime(0)   → false
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard {@code n < 2} first.</li>
     *   <li>Loop {@code i} from 2 up to (and including) {@code sqrt(n)}; if
     *       {@code n % i == 0}, not prime.</li>
     *   <li>{@code (int) Math.sqrt(n)} is enough for the loop bound.</li>
     * </ul>
     *
     * @param n candidate integer
     * @return {@code true} if {@code n} is prime
     */
    public boolean isPrime(int n) {
        throw new UnsupportedOperationException("TODO: implement isPrime");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the greatest common divisor of {@code a} and {@code b}
     * (always non-negative). Uses the Euclidean algorithm.
     *
     * <p>Examples:
     * <pre>
     *   gcd(12, 8)   → 4
     *   gcd(17, 5)   → 1
     *   gcd(48, 18)  → 6
     *   gcd(0, 5)    → 5
     *   gcd(5, 0)    → 5
     *   gcd(0, 0)    → 0
     *   gcd(-12, 8)  → 4
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Take absolute values first: {@code Math.abs(a)}, {@code Math.abs(b)}.</li>
     *   <li>Euclidean loop: while {@code b != 0}, {@code (a, b) = (b, a % b)}.</li>
     *   <li>Edge case {@code (0, 0)} returns 0 naturally.</li>
     * </ul>
     *
     * @param a first integer
     * @param b second integer
     * @return the greatest common divisor
     */
    public int gcd(int a, int b) {
        throw new UnsupportedOperationException("TODO: implement gcd");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code n!} (n factorial) as a {@code long}.
     *
     * <p>Rules:
     * <ul>
     *   <li>{@code 0! = 1} and {@code 1! = 1}</li>
     *   <li>{@code n < 0} → throws {@link IllegalArgumentException}</li>
     *   <li>{@code n > 20} → throws {@link ArithmeticException} (would overflow
     *       {@code long}; {@code 20! = 2432902008176640000} fits,
     *       {@code 21!} does not)</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   factorial(0)  → 1
     *   factorial(1)  → 1
     *   factorial(5)  → 120
     *   factorial(10) → 3628800
     *   factorial(20) → 2432902008176640000L
     *   factorial(-1) → IllegalArgumentException
     *   factorial(21) → ArithmeticException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard negative input first.</li>
     *   <li>Guard {@code n > 20} second — this is the overflow boundary.</li>
     *   <li>Loop-multiply into a {@code long} starting from 1.</li>
     * </ul>
     *
     * @param n the number (≥ 0, ≤ 20)
     * @return {@code n!} as a {@code long}
     */
    public long factorial(int n) {
        throw new UnsupportedOperationException("TODO: implement factorial");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the maximum value in a non-empty array. Throws
     * {@link IllegalArgumentException} for null or empty arrays.
     *
     * <p>Examples:
     * <pre>
     *   maxOf([1, 2, 3])       → 3
     *   maxOf([3, 2, 1])       → 3
     *   maxOf([-5, -1, -10])   → -1
     *   maxOf([42])            → 42
     *   maxOf([])              → IllegalArgumentException
     *   maxOf(null)            → IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard null/empty first.</li>
     *   <li>Assume {@code arr[0]} is the max, then loop from index 1.</li>
     * </ul>
     *
     * @param arr array of integers (non-null, non-empty)
     * @return the maximum value
     */
    public int maxOf(int[] arr) {
        throw new UnsupportedOperationException("TODO: implement maxOf");
    }
}