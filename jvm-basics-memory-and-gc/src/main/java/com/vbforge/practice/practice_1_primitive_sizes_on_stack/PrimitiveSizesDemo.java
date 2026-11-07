package com.vbforge.practice.practice_1_primitive_sizes_on_stack;

/**
 * Approximate in-memory footprint of primitives in a local scope.
 * Stack layout / padding is JVM-dependent — treat these numbers as illustrative.
 */
public class PrimitiveSizesDemo {

    public static void main(String[] args) {
        char    entrance         = '1';
        boolean inTheCenter      = true;
        double  square           = 104.79;
        int     floors           = 9;
        byte    apartments       = 3;

        int total = sizeOf(entrance)
                + sizeOf(inTheCenter)
                + sizeOf(square)
                + sizeOf(floors)
                + sizeOf(apartments);

        System.out.println("Total primitive footprint: " + total + " bytes");
    }

    private static int sizeOf(char v)     { return 2; }
    private static int sizeOf(boolean v)  { return 1; }
    private static int sizeOf(double v)   { return 8; }
    private static int sizeOf(int v)      { return 4; }
    private static int sizeOf(byte v)     { return 1; }
}