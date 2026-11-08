package com.vbforge.practice.practice_5_parameterized_tests;

import com.vbforge.practice.practice_5_parameterized_tests.ShippingCalculator.Zone;

public class ParameterizedDemo {
    public static void main(String[] args) {
        System.out.println(IsbnValidator.isValid("978-3-16-148410-0"));       // true
        System.out.println(ShippingCalculator.costInCents(Zone.EU, 1500));    // 1450
    }
}
