package com.vbforge.practice.practice_2_bigdecimal_construction;

import java.math.BigDecimal;

/**
 * Three ways to create a BigDecimal and why the double constructor is a trap.
 *
 *   new BigDecimal(0.1)        -> exact value of the binary double (noise)
 *   new BigDecimal("0.1")      -> exactly 0.1
 *   BigDecimal.valueOf(0.1)    -> uses Double.toString(0.1), so exactly 0.1
 *
 * Rule: construct from String (or valueOf), never from a double literal.
 */
public class BigDecimalConstructionDemo {

    public static void main(String[] args) {
        System.out.println("--- what each constructor stores for 0.1 ---");
        System.out.println("new BigDecimal(0.1)      = " + new BigDecimal(0.1));
        // 0.1000000000000000055511151231257827021181583404541015625
        System.out.println("new BigDecimal(\"0.1\")    = " + new BigDecimal("0.1"));      // 0.1
        System.out.println("BigDecimal.valueOf(0.1)  = " + BigDecimal.valueOf(0.1));     // 0.1

        System.out.println("\n--- 0.1 + 0.2 stays exact ---");
        System.out.println("BigDecimal(\"0.1\") + (\"0.2\") = "
                + new BigDecimal("0.1").add(new BigDecimal("0.2")));                    // 0.3

        System.out.println("\n--- TRAP: double constructor inherits the binary error ---");
        BigDecimal wrong = new BigDecimal(0.3).subtract(new BigDecimal(0.1));
        System.out.println("new BigDecimal(0.3) - new BigDecimal(0.1) = " + wrong);
        // 0.1999999999999999833466546306226518936455249786376953125

        System.out.println("\n--- FIX: valueOf / String constructor ---");
        BigDecimal viaValueOf = BigDecimal.valueOf(0.3).subtract(BigDecimal.valueOf(0.1));
        System.out.println("valueOf(0.3) - valueOf(0.1)       = " + viaValueOf);       // 0.2

        BigDecimal viaString = new BigDecimal("0.4").subtract(new BigDecimal("0.3"));
        System.out.println("new BigDecimal(\"0.4\") - (\"0.3\")   = " + viaString);      // 0.1
    }
}
