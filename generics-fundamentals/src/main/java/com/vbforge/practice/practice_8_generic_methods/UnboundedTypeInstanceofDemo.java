package com.vbforge.practice.practice_8_generic_methods;

/**
 * An unbounded T only gives you the Object API, so reaching Number's methods
 * needs an instanceof check and a cast. (With a bound, {@code <T extends Number>},
 * the check disappears - see practice_9_bounded_types.)
 */
public class UnboundedTypeInstanceofDemo {

    public static void main(String[] args) {
        System.out.println(asByte(Integer.valueOf(7)));       // 7
        System.out.println(asByte(Float.valueOf(7.0F)));      // 7
        System.out.println(asByte(Character.valueOf('7')));   // 0 - Character is not a Number
    }

    public static <T> byte asByte(T num) {
        if (num instanceof Number) {
            return ((Number) num).byteValue();
        }
        return 0;
    }
}
