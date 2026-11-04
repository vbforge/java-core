package com.vbforge.practice.practice_3_increment_decrement;

/**
 * Postfix (x++) vs prefix (++x):
 *   - postfix: returns the CURRENT value, then increments
 *   - prefix:  increments first, then returns the new value
 *
 * The difference only matters when the result is USED (assigned, printed, passed).
 * As a standalone statement `x++;` and `++x;` are equivalent.
 */
public class IncrementDecrementDemo {

    public static void main(String[] args) {
        int a = 10;
        System.out.println("a++   = " + (a++)); // 10 → then a = 11
        System.out.println("a after a++ = " + a);

        int b = 10;
        System.out.println("++b   = " + (++b)); // b = 11 → then prints 11
        System.out.println("b after ++b = " + b);

        System.out.println("\n--- decrement ---");
        int d = 10;
        System.out.println("d--   = " + (d--)); // 10 → then d = 9
        System.out.println("--d   = " + (--d)); // d = 8 → then prints 8

        System.out.println("\n--- classic pitfall: array[i++] vs array[++i] ---");
        int[] arr = {100, 200, 300};
        int i = 0;
        System.out.println("arr[i++] = " + arr[i++] + ", i = " + i); // prints 100, i becomes 1
        i = 0;
        System.out.println("arr[++i] = " + arr[++i] + ", i = " + i); // i becomes 1, prints 200
    }
}
