package com.vbforge.practice.practice_11_overload_resolution_and_widening;

/**
 * When both `int` and `Integer` overloads exist and the caller passes an `int`,
 * the primitive overload wins - autoboxing is a "later" phase in overload resolution.
 *
 * The same holds for `Integer`: passing an `Integer` matches the `Integer` overload exactly.
 */
public class OverloadResolutionPrimitiveVsWrapperDemo {

    public static void main(String[] args) {
        int     primitive = 7;
        Integer wrapper   = 7;

        accept(primitive); // -> int overload (no boxing)
        accept(wrapper);   // -> Integer overload (exact match)

        // Widening beats boxing:
        byte small = 7;
        acceptWidening(small); // -> int overload (widening, no boxing)
    }

    static void accept(int v)     { System.out.println("int overload     : " + v); }
    static void accept(Integer v) { System.out.println("Integer overload : " + v); }

    static void acceptWidening(int v)     { System.out.println("int (widening)   : " + v); }
    static void acceptWidening(Integer v) { System.out.println("Integer (boxing) : " + v); }
}
