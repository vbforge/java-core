package com.vbforge.practice.practice_4_pass_by_value_primitive;

/**
 * Java is pass-by-value. A primitive argument is COPIED into the parameter, so
 * `i *= 10` inside `result` changes only the local copy; the caller's variable is unchanged.
 *
 * A static field is different: it is shared, so `j += 2` is visible to everyone.
 */
public class PassByValuePrimitiveDemo {

    private static int j = 2;

    private static void result(int i) {
        i *= 10;                  // local copy → 100, thrown away
        j += 2;                   // static field → 4
    }

    public static void main(String[] args) {
        int i = 10;
        result(i);
        System.out.println("i = " + i);   // 10 — unchanged
        System.out.println("j = " + j);   // 4  — static field was mutated
    }
}
