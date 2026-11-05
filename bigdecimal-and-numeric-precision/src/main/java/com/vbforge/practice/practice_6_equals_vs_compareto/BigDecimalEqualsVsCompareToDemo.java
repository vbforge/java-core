package com.vbforge.practice.practice_6_equals_vs_compareto;

import java.math.BigDecimal;

/**
 * BigDecimal.equals compares value AND scale (2.0 != 2.00);
 * compareTo compares the numeric value only.
 */
public class BigDecimalEqualsVsCompareToDemo {

    public static void main(String[] args) {
        BigDecimal a = new BigDecimal("2.0");
        BigDecimal b = new BigDecimal("2.00");

        System.out.println("2.0 equals    2.00 : " + a.equals(b));      // false (scale 1 vs 2)
        System.out.println("2.0 compareTo 2.00 : " + a.compareTo(b));   // 0 (same value)
    }
}
