package com.vbforge.bigdecimal_and_numeric_precision;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimalDemo — annotated reference for exact numeric handling in Java.
 *
 * <p>Read top-to-bottom. Each section covers one aspect of why {@code double}
 * is a poor choice for money and measurements, and how {@link BigDecimal},
 * {@link RoundingMode}, and {@code long} cents solve the problem.
 *
 * <p>Topics:
 * <ol>
 *   <li>Floating-point error — why {@code 0.1 + 0.2 != 0.3}</li>
 *   <li>BigDecimal creation — String vs double vs {@code valueOf}</li>
 *   <li>Scale and rounding — {@code setScale}, {@link RoundingMode}</li>
 *   <li>{@code compareTo} vs {@code equals} — same value, different scale</li>
 *   <li>Integer overflow — wrap-around and {@code Math.addExact}</li>
 *   <li>Money in cents — {@code long} for exact integer arithmetic</li>
 * </ol>
 */
public class BigDecimalDemo {

    public static void main(String[] args) {
        floatingPointErrorDemo();
        System.out.println("---");
        bigDecimalCreationDemo();
        System.out.println("---");
        scaleAndRoundingDemo();
        System.out.println("---");
        compareToVsEqualsDemo();
        System.out.println("---");
        integerOverflowDemo();
        System.out.println("---");
        moneyInCentsDemo();
    }

    // ── 1. Floating-point error ───────────────────────────────────────────────

    /**
     * {@code double} and {@code float} follow IEEE 754 binary floating point.
     * Decimal fractions like {@code 0.1}, {@code 0.2}, {@code 0.3} have NO
     * exact binary representation — only approximations.
     *
     * <p>WHY this matters: any money-related code that uses {@code double}
     * will eventually produce rounding errors that accumulate. The classic
     * "one cent off" bug in financial systems traces back to this.
     *
     * <p>Gotcha: the error is not always visible. {@code 0.1 + 0.2} prints as
     * {@code 0.30000000000000004} — but some sums happen to round to the
     * "correct-looking" string and hide the underlying error.
     */
    static void floatingPointErrorDemo() {
        System.out.println("=== Floating-point error ===");

        double sum = 0.1 + 0.2;
        System.out.println("0.1 + 0.2    : " + sum);              // 0.30000000000000004
        System.out.println("sum == 0.3   : " + (sum == 0.3));    // false — the classic bug

        // The exact decimal answer via BigDecimal + String constructor
        BigDecimal exact = new BigDecimal("0.1").add(new BigDecimal("0.2"));
        System.out.println("BigDecimal   : " + exact);             // 0.3
        System.out.println("equals 0.3   : " + exact.equals(new BigDecimal("0.3")));  // true

        // Even a single multiplication can drift
        double total = 0.1 * 3;
        System.out.println("0.1 * 3      : " + total);              // 0.30000000000000004
    }

    // ── 2. BigDecimal creation ────────────────────────────────────────────────

    /**
     * {@link BigDecimal} can be created from many sources, and the choice
     * matters. {@code new BigDecimal(double)} captures the exact binary value
     * of the {@code double} — which is rarely what you want.
     *
     * <p>WHY this matters: "0.1" entered by a user or read from config should
     * be {@code new BigDecimal("0.1")}, not {@code new BigDecimal(0.1)}.
     *
     * <p>Three safe ways to create a BigDecimal from a number:
     * <ul>
     *   <li>{@code new BigDecimal("0.1")} — preferred, String source</li>
     *   <li>{@code BigDecimal.valueOf(0.1)} — uses {@code Double.toString} internally</li>
     *   <li>{@code new BigDecimal(0.1)} — WRONG for decimal values; captures binary noise</li>
     * </ul>
     */
    static void bigDecimalCreationDemo() {
        System.out.println("=== BigDecimal creation ===");

        BigDecimal fromString = new BigDecimal("0.1");
        BigDecimal fromValueOf = BigDecimal.valueOf(0.1);
        BigDecimal fromDouble = new BigDecimal(0.1);

        System.out.println("new BigDecimal(\"0.1\")   : " + fromString);
        System.out.println("BigDecimal.valueOf(0.1) : " + fromValueOf);
        System.out.println("new BigDecimal(0.1)     : " + fromDouble);

        // Only the String / valueOf forms are equal to the decimal we meant
        System.out.println("String == valueOf : " + fromString.equals(fromValueOf));   // true
        System.out.println("String == double  : " + fromString.equals(fromDouble));    // false

        // Exact integer value — safe with the int constructor
        BigDecimal exactInt = new BigDecimal(42);
        System.out.println("new BigDecimal(42) : " + exactInt);

        // ZERO, ONE, TEN are pre-defined constants
        System.out.println("BigDecimal.ZERO    : " + BigDecimal.ZERO);
        System.out.println("BigDecimal.TEN     : " + BigDecimal.TEN);
    }

    // ── 3. Scale and rounding ─────────────────────────────────────────────────

    /**
     * <b>Scale</b> is the number of digits after the decimal point.
     * {@code 19.99} has scale 2. {@code 19.990} has scale 3.
     *
     * <p>{@code setScale(int, RoundingMode)} produces a new BigDecimal with
     * the requested scale, rounding per the given {@link RoundingMode}.
     *
     * <p>WHY this matters: any division or tax calculation produces an
     * infinite decimal. You must choose a scale AND a rounding rule — never
     * let Java pick silently. {@code HALF_UP} is the "banker's opposite" —
     * the rounding taught in school.
     *
     * <p>Common modes:
     * <ul>
     *   <li>{@code HALF_UP} — 0.5 rounds away from zero (school rounding)</li>
     *   <li>{@code HALF_EVEN} — 0.5 rounds to nearest even (banker's, less bias)</li>
     *   <li>{@code DOWN} — always truncate toward zero</li>
     *   <li>{@code CEILING} — always round toward +∞</li>
     *   <li>{@code FLOOR} — always round toward −∞</li>
     * </ul>
     */
    static void scaleAndRoundingDemo() {
        System.out.println("=== Scale and rounding ===");

        BigDecimal value = new BigDecimal("2.345");
        System.out.println("original    : " + value + "  (scale " + value.scale() + ")");

        System.out.println("HALF_UP     : " + value.setScale(2, RoundingMode.HALF_UP));    // 2.35
        System.out.println("HALF_DOWN   : " + value.setScale(2, RoundingMode.HALF_DOWN));  // 2.34
        System.out.println("HALF_EVEN   : " + value.setScale(2, RoundingMode.HALF_EVEN));  // 2.34
        System.out.println("DOWN        : " + value.setScale(2, RoundingMode.DOWN));       // 2.34
        System.out.println("CEILING     : " + value.setScale(2, RoundingMode.CEILING));    // 2.35
        System.out.println("FLOOR       : " + value.setScale(2, RoundingMode.FLOOR));      // 2.34

        // Negative numbers behave around zero, not around a "sign"
        BigDecimal neg = new BigDecimal("-2.345");
        System.out.println("-2.345 HALF_UP  : " + neg.setScale(2, RoundingMode.HALF_UP));   // -2.35
        System.out.println("-2.345 CEILING  : " + neg.setScale(2, RoundingMode.CEILING));   // -2.34

        // Growing the scale is exact — no rounding needed, just padding with zeros
        BigDecimal small = new BigDecimal("2.5");
        System.out.println("2.5 to scale 4  : " + small.setScale(4, RoundingMode.HALF_UP)); // 2.5000

        // setScale with a smaller scale without a RoundingMode throws
        try {
            new BigDecimal("2.345").setScale(2);
        } catch (ArithmeticException e) {
            System.out.println("no-mode throws  : ArithmeticException");
        }
    }

    // ── 4. compareTo vs equals ────────────────────────────────────────────────

    /**
     * {@code equals()} compares <em>value AND scale</em>.
     * {@code compareTo()} compares <em>value only</em>.
     *
     * <p>WHY this matters: {@code new BigDecimal("2.0").equals(new BigDecimal("2.00"))}
     * is {@code false}, but {@code compareTo(...)} returns {@code 0}. If you
     * put BigDecimals in a {@link java.util.HashSet} or {@link java.util.HashMap}
     * the wrong comparison will silently produce "duplicate" entries or miss
     * lookups.
     *
     * <p>Rule: use {@code compareTo(...) == 0} for "is this the same number",
     * and {@code equals(...)} for "is this exactly the same representation".
     */
    static void compareToVsEqualsDemo() {
        System.out.println("=== compareTo vs equals ===");

        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");

        System.out.println("a = " + a + " (scale " + a.scale() + ")");
        System.out.println("b = " + b + " (scale " + b.scale() + ")");
        System.out.println("a.equals(b)     : " + a.equals(b));                 // false
        System.out.println("a.compareTo(b)  : " + a.compareTo(b));              // 0
        System.out.println("compareTo == 0  : " + (a.compareTo(b) == 0));       // true

        // Directional comparisons — useful for sorting
        System.out.println("2.0 vs 2.001    : " + a.compareTo(new BigDecimal("2.001")));  // -1
        System.out.println("2.0 vs 1.999    : " + a.compareTo(new BigDecimal("1.999")));  // 1

        // Ordering methods on BigDecimal
        System.out.println("min             : " + a.min(new BigDecimal("1.5")));   // 1.5
        System.out.println("max             : " + a.max(new BigDecimal("1.5")));   // 2.0
    }

    // ── 5. Integer overflow ───────────────────────────────────────────────────

    /**
     * {@code int} is a 32-bit signed type with range
     * {@code [-2147483648, 2147483647]}. Adding past that boundary <em>wraps
     * around silently</em> — no exception, no warning.
     *
     * <p>WHY this matters: counter increments, index arithmetic, and money
     * amounts in cents can silently go negative when large. Java 8 added
     * {@link Math#addExact} to fail loudly instead.
     *
     * <p>Three ways to handle it:
     * <ul>
     *   <li>{@code Math.addExact(a, b)} — throws {@link ArithmeticException} on overflow</li>
     *   <li>Widen to {@code long} first: {@code (long) a + b}</li>
     *   <li>Pre-check: {@code b > 0 && a > Integer.MAX_VALUE - b}</li>
     * </ul>
     */
    static void integerOverflowDemo() {
        System.out.println("=== Integer overflow ===");

        int max = Integer.MAX_VALUE;
        int min = Integer.MIN_VALUE;
        System.out.println("MAX_VALUE       : " + max);
        System.out.println("MIN_VALUE       : " + min);

        // Silent wraparound — the classic bug
        int wrapped = max + 1;
        System.out.println("MAX_VALUE + 1   : " + wrapped);              // -2147483648
        System.out.println("wrapped == MIN  : " + (wrapped == min));      // true

        // Math.addExact refuses to do the wrong thing
        try {
            Math.addExact(max, 1);
        } catch (ArithmeticException e) {
            System.out.println("addExact throws : ArithmeticException");
        }

        // Safe within-range addition
        System.out.println("addExact(1, 2)  : " + Math.addExact(1, 2));   // 3

        // Widening to long is always safe for one addition
        long wideSum = (long) max + 1;
        System.out.println("(long)MAX + 1   : " + wideSum);                // 2147483648
    }

    // ── 6. Money in cents ─────────────────────────────────────────────────────

    /**
     * The safest representation for money when you control both sides is
     * {@code long} <em>cents</em>. All amounts are integers, all arithmetic
     * is integer arithmetic, and there is no rounding to argue about until
     * you actually print.
     *
     * <p>WHY this matters: tax calculations, currency splits, and totals are
     * all exact when done in cents. Convert to {@link BigDecimal} with scale 2
     * only at the presentation layer.
     *
     * <p>Trade-off: {@code long} caps at about 92 quadrillion cents
     * (~$92 trillion). For most business amounts that is fine. When you need
     * arbitrary scale or currency exchange, use {@link BigDecimal} directly.
     */
    static void moneyInCentsDemo() {
        System.out.println("=== Money in cents ===");

        long priceCents = 1999L;      // $19.99
        long taxCents   = 160L;       // $1.60
        long totalCents = priceCents + taxCents;
        System.out.println("total cents     : " + totalCents);              // 2159

        BigDecimal total = BigDecimal.valueOf(totalCents, 2);
        System.out.println("as dollars      : " + total);                    // 21.59
        System.out.println("scale           : " + total.scale());            // 2

        // Back to cents — exact, no float round-trip
        long back = total.movePointRight(2).longValueExact();
        System.out.println("back to cents   : " + back);                     // 2159
    }
}