package com.vbforge.practice.practice_5_math_context;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;

/**
 * MathContext limits SIGNIFICANT DIGITS (precision), unlike setScale which limits
 * digits after the decimal point.
 */
public class MathContextPrecisionDemo {

    public static void main(String[] args) {
        // DECIMAL32 = 7 significant digits, HALF_EVEN
        BigDecimal withContext = new BigDecimal(0.3, MathContext.DECIMAL32)
                .subtract(new BigDecimal(0.1, MathContext.DECIMAL32));
        System.out.println("DECIMAL32: 0.3 - 0.1 = " + withContext);              // 0.2000000

        // 1/7 never terminates: a MathContext makes divide possible
        BigDecimal oneSeventh = BigDecimal.ONE.divide(new BigDecimal("7"),
                new MathContext(10, RoundingMode.HALF_UP));
        System.out.println("1/7 with 10 significant digits = " + oneSeventh);     // 0.1428571429
    }
}
