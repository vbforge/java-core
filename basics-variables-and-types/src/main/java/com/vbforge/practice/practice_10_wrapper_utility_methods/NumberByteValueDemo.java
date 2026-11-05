package com.vbforge.practice.practice_10_wrapper_utility_methods;

/**
 * Number.byteValue() truncates exactly like a (byte) cast.
 */
public class NumberByteValueDemo {

    public static void main(String[] args) {
        Number n1 = Integer.valueOf(50);            // fits in byte
        Number n2 = Integer.valueOf(500);           // does NOT fit in byte
        Number n3 = Long.valueOf(500L);
        System.out.println("(byte) 50       = " + n1.byteValue());   // 50
        System.out.println("(byte) 500      = " + n2.byteValue());   // -12 (truncated)
        System.out.println("(byte) 500L     = " + n3.byteValue());   // -12 (same)
        System.out.println("(byte) null     = " + asByte(null));     // 0 (guarded)
    }

    private static byte asByte(Number num) {
        return num != null ? num.byteValue() : 0;
    }
}
