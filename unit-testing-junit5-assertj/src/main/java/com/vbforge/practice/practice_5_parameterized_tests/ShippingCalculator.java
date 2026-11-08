package com.vbforge.practice.practice_5_parameterized_tests;

/**
 * Shipping cost in cents: a base rate covers the first 1000 g,
 * every further STARTED 1000 g adds the per-kg rate of the zone.
 */
public final class ShippingCalculator {

    public static final int MAX_WEIGHT_GRAMS = 30_000;

    public enum Zone {
        DOMESTIC(500, 100),
        EU(1200, 250),
        WORLD(2500, 600);

        private final int base;
        private final int perExtraKg;

        Zone(int base, int perExtraKg) {
            this.base = base;
            this.perExtraKg = perExtraKg;
        }
    }

    private ShippingCalculator() {
    }

    public static int costInCents(Zone zone, int weightGrams) {
        if (zone == null) {
            throw new IllegalArgumentException("zone must not be null");
        }
        if (weightGrams <= 0 || weightGrams > MAX_WEIGHT_GRAMS) {
            throw new IllegalArgumentException("weight must be in 1.." + MAX_WEIGHT_GRAMS + " g but was " + weightGrams);
        }
        int startedKg = (weightGrams + 999) / 1000;
        return zone.base + (startedKg - 1) * zone.perExtraKg;
    }
}
