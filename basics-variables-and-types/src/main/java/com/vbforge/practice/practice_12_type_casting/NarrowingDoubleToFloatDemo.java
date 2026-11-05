package com.vbforge.practice.practice_12_type_casting;

/**
 * double -> float narrowing: precision loss, subnormal values, underflow to 0 and overflow to Infinity.
 */
public class NarrowingDoubleToFloatDemo {

    public static void main(String[] args) {
        double highPrecision = 123_456_789.123456789;
        float asFloat = (float) highPrecision;
        System.out.println("double -> float: " + highPrecision + " -> " + asFloat);

        double verySmall = 1e-40;
        float smallAsFloat = (float) verySmall;
        System.out.println("1e-40  -> float: " + smallAsFloat); // still non-zero: a subnormal float, precision is lost

        double veryLarge = 1e40;
        float largeAsFloat = (float) veryLarge;
        System.out.println("1e40   -> float: " + largeAsFloat); // Infinity

        double underflow = 1.0e-46;
        float underflowed = (float) underflow;
        System.out.println("1.0e-46 -> float: " + underflowed); // 0.0 (below the smallest subnormal float)
    }
}
