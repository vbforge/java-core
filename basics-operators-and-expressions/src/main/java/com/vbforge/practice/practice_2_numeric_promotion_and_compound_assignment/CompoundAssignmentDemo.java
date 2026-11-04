package com.vbforge.practice.practice_2_numeric_promotion_and_compound_assignment;

/**
 * Compound assignment (+= -= *= /= %=) hides an implicit narrowing cast:
 * `E1 op= E2` is `E1 = (T)(E1 op E2)` where T is the type of E1.
 *
 * So `short s = 1; s += 2;` compiles, but `s = s + 2;` does not.
 */
public class CompoundAssignmentDemo {

    public static void main(String[] args) {
        System.out.println("--- plain compound assignment ---");
        int a = 10, b = 20;
        a += b;                               // a = a + b
        System.out.println("a += b → " + a);  // 30
        a -= b;                               // a = a - b
        System.out.println("a -= b → " + a);  // 10

        System.out.println("\n--- short with +2: += vs = ---");
        short x = 1;
        x += 2;                               // OK: hidden cast to short
        // x = x + 2;                         // compile error: int → short
        System.out.println("x += 2 → " + x);

        System.out.println("\n--- int += double: the fraction is cut off ---");
        int k = 3;
        k += 2.5;                             // k = (int)(k + 2.5) = 5
        System.out.println("3 += 2.5 → " + k);

        System.out.println("\n--- int += long: the hidden cast narrows back to int and overflows ---");
        long la = 2_147_483_647_000L;
        int  ib = 1;
        ib += la;                             // ib = (int)(ib + la)
        System.out.println("int b = 1; b += 2_147_483_647_000L → " + ib);
    }
}
