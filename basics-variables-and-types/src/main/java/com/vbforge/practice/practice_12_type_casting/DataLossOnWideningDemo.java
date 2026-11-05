package com.vbforge.practice.practice_12_type_casting;

/**
 * Widening conversions compile without error but can still lose precision
 * because int/long have more significant bits than the mantissa of float/double.
 */
public class DataLossOnWideningDemo {

    public static void main(String[] args) {
        int intValue = 123_456_789;
        float intToFloat = intValue;
        System.out.println("int  -> float : " + intValue + " -> " + intToFloat);

        long longValue = 9_876_543_210L;
        float longToFloat = longValue;
        System.out.println("long -> float : " + longValue + " -> " + longToFloat);

        long bigLong = Long.MAX_VALUE;
        double longToDouble = bigLong;
        System.out.println("long -> double: " + bigLong + " -> " + longToDouble);
    }
}
