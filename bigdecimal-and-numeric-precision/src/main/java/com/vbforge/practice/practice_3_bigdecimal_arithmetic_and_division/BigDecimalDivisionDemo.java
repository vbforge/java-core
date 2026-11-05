package com.vbforge.practice.practice_3_bigdecimal_arithmetic_and_division;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BigDecimal.divide: a non-terminating result needs an explicit scale + RoundingMode,
 * otherwise Java throws ArithmeticException.
 */
public class BigDecimalDivisionDemo {

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("10");
        BigDecimal b = new BigDecimal("3");

        System.out.println("10 / 3 (scale 4, HALF_UP) = "
                + a.divide(b, 4, RoundingMode.HALF_UP));                 // 3.3333

        // addition: show the exception the explicit scale protects against
        try {
            a.divide(b);
        } catch (ArithmeticException e) {
            System.out.println("10 / 3 without scale      -> ArithmeticException: " + e.getMessage());
        }
    }
}
