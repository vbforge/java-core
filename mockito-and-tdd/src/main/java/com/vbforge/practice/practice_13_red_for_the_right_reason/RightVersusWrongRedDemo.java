package com.vbforge.practice.practice_13_red_for_the_right_reason;

import com.vbforge.practice.practice_11_tdd_check_helper.MiniCheck;

/**
 * A red must fail for the RIGHT reason.
 *   ERROR (NullPointerException) -> the test itself is broken: fix the test first
 *   FAIL  (expected 110 but was 100) -> a meaningful red: now write the code
 */
public class RightVersusWrongRedDemo {

    interface RateSource {
        int percentFor(String region);
    }

    /** Skeleton: compiles, does nothing useful yet. */
    static class PriceCalculatorSkeleton {
        long grossCents(long netCents, String region) { return netCents; }
    }

    /** The simplest code that makes the test pass. */
    static class PriceCalculator {
        private final RateSource rates;
        PriceCalculator(RateSource rates) { this.rates = rates; }

        long grossCents(long netCents, String region) {
            return netCents + netCents * rates.percentFor(region) / 100;
        }
    }

    public static void main(String[] args) {
        System.out.println("1) the test forgot to create the calculator: a WRONG red");
        PriceCalculatorSkeleton forgotten = null;
        MiniCheck wrong = new MiniCheck();
        wrong.check("gross of 100 in DE is 110", 110L, () -> forgotten.grossCents(100, "DE"));

        System.out.println("2) test fixed, code is still the skeleton: the RIGHT red");
        PriceCalculatorSkeleton skeleton = new PriceCalculatorSkeleton();
        MiniCheck right = new MiniCheck();
        right.check("gross of 100 in DE is 110", 110L, () -> skeleton.grossCents(100, "DE"));

        System.out.println("3) simplest code written: green");
        PriceCalculator calculator = new PriceCalculator(region -> 10);
        MiniCheck green = new MiniCheck();
        green.check("gross of 100 in DE is 110", 110L, () -> calculator.grossCents(100, "DE"));
        System.out.println("    -> " + green.summary());
    }
}
