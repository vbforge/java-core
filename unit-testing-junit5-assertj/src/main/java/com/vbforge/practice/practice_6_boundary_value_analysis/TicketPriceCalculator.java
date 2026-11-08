package com.vbforge.practice.practice_6_boundary_value_analysis;

/**
 * Price by age. Every "if" on a number creates boundaries that deserve their own tests.
 * 0..2 infant (free) | 3..12 child (50%) | 13..64 adult (100%) | 65..130 senior (70%)
 */
public final class TicketPriceCalculator {

    public static final int BASE_PRICE_CENTS = 1000;
    public static final int MAX_AGE = 130;

    public enum Category { INFANT, CHILD, ADULT, SENIOR }

    private TicketPriceCalculator() {
    }

    public static Category categoryFor(int age) {
        if (age < 0 || age > MAX_AGE) {
            throw new IllegalArgumentException("age must be in 0.." + MAX_AGE + " but was " + age);
        }
        if (age <= 2) {
            return Category.INFANT;
        }
        if (age <= 12) {
            return Category.CHILD;
        }
        if (age <= 64) {
            return Category.ADULT;
        }
        return Category.SENIOR;
    }

    public static int priceInCents(int age) {
        switch (categoryFor(age)) {
            case INFANT:
                return 0;
            case CHILD:
                return BASE_PRICE_CENTS / 2;
            case SENIOR:
                return BASE_PRICE_CENTS * 70 / 100;
            default:
                return BASE_PRICE_CENTS;
        }
    }
}
