package com.vbforge.practice.practice_1_aaa_and_naming;

import java.math.BigDecimal;

public class PriceCalculatorDemo {
    public static void main(String[] args) {
        PriceCalculator calc = new PriceCalculator();
        System.out.println(calc.applyDiscount(new BigDecimal("19.99"), 15));            // 16.99
        System.out.println(calc.addTax(new BigDecimal("100.00"), new BigDecimal("20"))); // 120.00
    }
}
