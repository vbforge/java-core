package com.vbforge.practice.practice_2_catch_order;

/**
 * Several catch blocks: the FIRST matching block wins, so a more specific type
 * must come BEFORE its supertype, or the compiler rejects the code as unreachable.
 */
public class OrderedCatchBlocksDemo {

    public static void main(String[] args) {
        int[] arr = {1, 2, 3};
        try {
            System.out.println(arr[10] / 0);     // arr[10] is evaluated first -> AIOOBE, the division never happens
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  AIOOBE");
        } catch (ArithmeticException e) {
            System.out.println("  ArithmeticException");
        } catch (Exception e) {
            System.out.println("  general Exception");
        }

        // Wrong order does not compile ("exception ... has already been caught"):
        // try { ... }
        // catch (Exception e) { }
        // catch (ArrayIndexOutOfBoundsException e) { }    // unreachable
    }
}
