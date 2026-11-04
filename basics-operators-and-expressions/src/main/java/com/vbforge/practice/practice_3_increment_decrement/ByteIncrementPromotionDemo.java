package com.vbforge.practice.practice_3_increment_decrement;

/**
 * `b++` on a byte "just works" because the compiler inserts the narrowing cast.
 * `b = b + 1` does not — because `b + 1` promotes to int.
 * (`+=` follows the same rule: see CompoundAssignmentDemo.)
 */
public class ByteIncrementPromotionDemo {

    public static void main(String[] args) {
        byte b = 1;
        b++;                                    // OK: compiler inserts (byte) cast
        System.out.println("b++ → " + b);

        // b = b + 1;                           // compile error: int → byte
        b = (byte) (b + 1);                     // OK: explicit cast
        System.out.println("(byte)(b + 1) → " + b);

        System.out.println("\n--- same for short and char ---");
        short s = 10;
        s++;            // OK
        // s = s + 1;   // compile error
        System.out.println("short s++ → " + s);

        char c = 'a';
        c++;                                    // OK
        System.out.println("char c++ → " + c);
    }
}
