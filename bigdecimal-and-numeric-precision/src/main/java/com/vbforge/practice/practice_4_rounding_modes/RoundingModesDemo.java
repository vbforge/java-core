package com.vbforge.practice.practice_4_rounding_modes;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * setScale(newScale, RoundingMode): all modes applied to the same tie value 2.55 (scale 1),
 * and rounding used to clean up a noisy double-constructor result.
 */
public class RoundingModesDemo {

    public static void main(String[] args) {
        System.out.println("--- 2.55 -> scale 1 ---");
        BigDecimal value = new BigDecimal("2.55");
        for (RoundingMode mode : new RoundingMode[]{
                RoundingMode.UP, RoundingMode.DOWN,
                RoundingMode.CEILING, RoundingMode.FLOOR,
                RoundingMode.HALF_UP, RoundingMode.HALF_DOWN, RoundingMode.HALF_EVEN
        }) {
            System.out.printf("  %-10s -> %s%n", mode, value.setScale(1, mode));
        }

        System.out.println("\n--- rounding away the binary noise ---");
        BigDecimal noisy = new BigDecimal(0.3).subtract(new BigDecimal(0.1));
        System.out.println("noisy                = " + noisy);
        System.out.println("setScale(3, CEILING) = " + noisy.setScale(3, RoundingMode.CEILING)); // 0.200
    }
}
