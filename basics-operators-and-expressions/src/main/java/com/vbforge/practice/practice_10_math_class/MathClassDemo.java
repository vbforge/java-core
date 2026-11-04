package com.vbforge.practice.practice_10_math_class;

import static java.lang.Math.toDegrees;
import static java.lang.Math.toRadians;

/**
 * java.lang.Math — all methods are static, never instantiate the class.
 * Covers constants, arithmetic, trigonometry (in radians), logs, rounding, random.
 * Random values are checked against their range instead of printed, so the output is stable.
 */
public class MathClassDemo {

    public static void main(String[] args) {
        System.out.println("--- constants ---");
        System.out.println("PI = " + Math.PI);
        System.out.println("E  = " + Math.E);

        System.out.println("\n--- basic arithmetic ---");
        System.out.println("abs(-4)     = " + Math.abs(-4));
        System.out.println("max(1, 2)   = " + Math.max(1, 2));
        System.out.println("min(1, 2)   = " + Math.min(1, 2));
        System.out.println("pow(2, 4)   = " + Math.pow(2, 4));
        System.out.println("sqrt(16)    = " + Math.sqrt(16));
        System.out.println("cbrt(-8)    = " + Math.cbrt(-8));
        System.out.println("hypot(3, 4) = " + Math.hypot(3, 4));

        System.out.println("\n--- trigonometry (radians) ---");
        System.out.println("sin(0)   = " + Math.sin(0));
        System.out.println("cos(0)   = " + Math.cos(0));
        System.out.println("tan(0)   = " + Math.tan(0));
        System.out.println("asin(0)  = " + Math.asin(0));
        System.out.println("acos(1)  = " + Math.acos(1));
        System.out.println("atan(0)  = " + Math.atan(0));
        System.out.println("toRadians(1) = " + toRadians(1));
        System.out.println("toDegrees(1) = " + toDegrees(1));

        System.out.println("\n--- logs ---");
        System.out.println("exp(1)      = " + Math.exp(1));
        System.out.println("log(E)      = " + Math.log(Math.E));
        System.out.println("log10(100)  = " + Math.log10(100));

        System.out.println("\n--- rounding ---");
        System.out.println("ceil(3.3)  = " + Math.ceil(3.3));
        System.out.println("floor(5.7) = " + Math.floor(5.7));
        System.out.println("round(5.8) = " + Math.round(5.8));
        System.out.println("round(-0.7)= " + Math.round(-0.7));

        System.out.println("\n--- random in [0, 1) ---");
        double r = Math.random();
        System.out.println("0.0 <= Math.random() < 1.0 : " + (r >= 0.0 && r < 1.0));

        System.out.println("\n--- random in a range [5.0, 10.0) ---");
        double min = 5.0, max = 10.0;
        double ranged = min + Math.random() * (max - min);
        System.out.println("5.0 <= ranged < 10.0       : " + (ranged >= min && ranged < max));
    }
}
