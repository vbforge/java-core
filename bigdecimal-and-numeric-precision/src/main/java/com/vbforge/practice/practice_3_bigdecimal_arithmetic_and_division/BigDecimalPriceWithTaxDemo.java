package com.vbforge.practice.practice_3_bigdecimal_arithmetic_and_division;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Money arithmetic with BigDecimal: multiply, round to cents (scale 2), add.
 */
public class BigDecimalPriceWithTaxDemo {

    public static void main(String[] args) {
        BigDecimal price = new BigDecimal("199.99");
        BigDecimal taxRate = new BigDecimal("0.08");

        // 199.99 * 0.08 = 15.9992 (scale 4) -> rounded to cents
        BigDecimal tax = price.multiply(taxRate).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = price.add(tax);

        System.out.println("price = " + price);   // 199.99
        System.out.println("tax   = " + tax);     // 16.00
        System.out.println("total = " + total);   // 215.99
    }
}
