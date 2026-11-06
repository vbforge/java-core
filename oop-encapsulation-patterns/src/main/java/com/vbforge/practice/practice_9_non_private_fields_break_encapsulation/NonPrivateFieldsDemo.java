package com.vbforge.practice.practice_9_non_private_fields_break_encapsulation;

/**
 * Negative example: fields without 'private' can be changed by anyone, at any time,
 * with no validation. This is exactly what encapsulation prevents.
 */
public class NonPrivateFieldsDemo {

    static class IntegerPair {
        int x;      // package-private: reachable from the whole package
        int y;

        IntegerPair(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public String toString() {
            return "IntegerPair{x=" + x + ", y=" + y + "}";
        }
    }

    public static void main(String[] args) {
        IntegerPair ip = new IntegerPair(10, 20);
        System.out.println(ip);

        ip.x = 50;                 // mutate freely: nothing guards the field
        System.out.println(ip);
    }
}
