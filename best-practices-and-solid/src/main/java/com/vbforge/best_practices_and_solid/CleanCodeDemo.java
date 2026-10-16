package com.vbforge.best_practices_and_solid;

import java.util.Locale;

/**
 * <b>Clean code</b> — small habits that make code readable for the next person
 * (usually you, in six months).
 *
 * <p>Every section shows a "before" and an "after" that return <b>identical results</b>;
 * {@code CleanCodeDemoTest} proves it, which is the point of safe refactoring.
 *
 * <p>Sections:
 * <ol>
 *   <li>Intention-revealing names</li>
 *   <li>Guard clauses instead of arrow-shaped nesting</li>
 *   <li>Named constants instead of magic numbers</li>
 *   <li>No boolean flag arguments</li>
 * </ol>
 */
public class CleanCodeDemo {

    // ── 1. Names ──────────────────────────────────────────────────────────────
    /**
     * What it is: {@code calc(d, r, t)} versus {@code calculateSimpleInterest(principal,
     * annualRate, years)}.
     *
     * <p>Why it matters: code is read far more often than it is written. A good name
     * removes the need for a comment — and unlike a comment, it cannot go stale
     * without the compiler or a reader noticing.
     *
     * <p>Gotcha: swapped {@code double} parameters compile fine. Names are your only
     * defence, so make them unmistakable.
     */
    public static double calc(double d, double r, int t) {
        return d * r * t;
    }

    public static double calculateSimpleInterest(double principal, double annualRate, int years) {
        return principal * annualRate * years;
    }


    // ── 2. Guard clauses ──────────────────────────────────────────────────────
    /**
     * What it is: handle the invalid and special cases first and return early, so the
     * "normal" path is not buried under indentation.
     *
     * <p>Why it matters: the nested version needs you to keep three open conditions in
     * your head to understand the last line. The flat version reads top to bottom.
     *
     * <p>Complexity: identical — O(1) either way. This is purely about readability.
     */
    public static String ticketCategoryNested(int age, boolean student) {
        if (age >= 0 && age <= 120) {
            if (age < 6) {
                return "free";
            } else {
                if (age < 18 || student) {
                    return "reduced";
                } else {
                    return "full";
                }
            }
        } else {
            throw new IllegalArgumentException("age out of range: " + age);
        }
    }

    public static String ticketCategory(int age, boolean student) {
        if (age < 0 || age > 120) {
            throw new IllegalArgumentException("age out of range: " + age);
        }
        if (age < 6) {
            return "free";
        }
        if (age < 18 || student) {
            return "reduced";
        }
        return "full";
    }


    // ── 3. Magic numbers ──────────────────────────────────────────────────────
    /**
     * What it is: {@code 50}, {@code 4.99} and {@code 0.5} versus constants whose
     * names say what they mean.
     *
     * <p>Why it matters: when the free-shipping limit changes, you change one constant
     * instead of hunting every {@code 50} in the codebase (some of which are not the limit).
     */
    public static final double FREE_SHIPPING_THRESHOLD = 50.0;
    public static final double BASE_SHIPPING_FEE = 4.99;
    public static final double FEE_PER_KG = 0.5;

    public static double shippingFeeMagic(double orderTotal, double weightKg) {
        if (orderTotal >= 50.0) {
            return 0.0;
        }
        return 4.99 + 0.5 * weightKg;
    }

    public static double shippingFee(double orderTotal, double weightKg) {
        if (orderTotal >= FREE_SHIPPING_THRESHOLD) {
            return 0.0;
        }
        return BASE_SHIPPING_FEE + FEE_PER_KG * weightKg;
    }


    // ── 4. Flag arguments ─────────────────────────────────────────────────────
    /**
     * What it is: {@code formatPrice(12.5, true)} versus {@code formatWithCurrency(12.5)}.
     *
     * <p>Why it matters: at the call site {@code true} means nothing, and a boolean
     * parameter is proof that the method does two things (see SRP). Two well-named
     * methods read like prose.
     *
     * <p>Gotcha: {@code String.format} needs {@code Locale.ROOT} to print "12.50"
     * on every machine.
     */
    public static String formatPrice(double amount, boolean withCurrency) {
        String number = String.format(Locale.ROOT, "%.2f", amount);
        return withCurrency ? "USD " + number : number;
    }

    public static String formatAmount(double amount) {
        return String.format(Locale.ROOT, "%.2f", amount);
    }

    public static String formatWithCurrency(double amount) {
        return "USD " + formatAmount(amount);
    }


    // ── Demo runners ──────────────────────────────────────────────────────────
    static void namesDemo() {
        System.out.println("calc(1000, 0.05, 3)                      = " + calc(1000, 0.05, 3));
        System.out.println("calculateSimpleInterest(1000, 0.05, 3)   = " + calculateSimpleInterest(1000, 0.05, 3));
    }

    static void guardClausesDemo() {
        System.out.println("nested : " + ticketCategoryNested(30, true));
        System.out.println("guarded: " + ticketCategory(30, true));
    }

    static void magicNumbersDemo() {
        System.out.println("magic : " + shippingFeeMagic(20.0, 2.0));
        System.out.println("named : " + shippingFee(20.0, 2.0));
    }

    static void flagArgumentsDemo() {
        System.out.println(formatPrice(12.5, true) + "  vs  " + formatWithCurrency(12.5));
    }

    public static void main(String[] args) {
        namesDemo();
        System.out.println("---");
        guardClausesDemo();
        System.out.println("---");
        magicNumbersDemo();
        System.out.println("---");
        flagArgumentsDemo();
    }
}
