package com.vbforge.practice.practice_9_wrapper_identity;

/**
 * Wrapper identity (==) vs value equality (equals).
 *
 * Integer.valueOf caches -128..127. So autoboxed values inside that range
 * share the SAME object; outside, they don't.
 *
 * Rule: never compare wrappers with ==.
 */
public class IntegerCacheIdentityDemo {

    @SuppressWarnings("removal")
    public static void main(String[] args) {
        System.out.println("--- inside the cache (-128..127) ---");
        Integer a = 127;
        Integer b = 127;
        System.out.println("127 == 127      : " + (a == b));      // true
        System.out.println("127.equals(127) : " + a.equals(b));  // true

        System.out.println("\n--- outside the cache ---");
        Integer c = 1000;
        Integer d = 1000;
        System.out.println("1000 == 1000    : " + (c == d));      // false
        System.out.println("1000.equals    : " + c.equals(d));    // true

        System.out.println("\n--- boundary 128 ---");
        Integer e = 128;
        Integer f = 128;
        System.out.println("128 == 128      : " + (e == f));      // false

        System.out.println("\n--- Integer.valueOf vs new Integer ---");
        Integer v1 = Integer.valueOf(5);
        Integer v2 = Integer.valueOf(5);
        System.out.println("valueOf(5) == valueOf(5)        : " + (v1 == v2)); // true

        Integer n1 = new Integer(5);
        Integer n2 = new Integer(5);
        System.out.println("new Integer(5) == new Integer(5): " + (n1 == n2)); // false
        System.out.println("valueOf(5) == new Integer(5)    : " + (v1 == n1)); // false

        System.out.println("\n--- increment INSIDE the cache: identity survives ---");
        Integer y = 125;
        Integer x = y;
        System.out.println("x == y before increment : " + (x == y)); // true
        x++;
        y++;
        System.out.println("x == y after increment  : " + (x == y)); // true - both rebox to the cached 126

        System.out.println("\n--- same test OUTSIDE the cache ---");
        Integer y2 = 567;
        Integer x2 = y2;
        System.out.println("x2 == y2 before increment: " + (x2 == y2)); // true
        x2++;
        y2++;
        System.out.println("x2 == y2 after increment : " + (x2 == y2)); // false - 568 is not cached

        System.out.println("\n--- the classic 'different objects, same meaning' ---");
        Integer i1 = 1234;
        Integer i2 = 1234;
        if (i1 != i2)        System.out.println("different objects");
        if (i1.equals(i2))   System.out.println("meaningfully equal");

        System.out.println("\n--- primitives always compare by value ---");
        int p = 3000;
        int q = 3000;
        System.out.println("p == q          : " + (p == q));

        System.out.println("\n--- wrapper vs primitive unboxes the wrapper ---");
        Integer w = 200;
        int prim = 200;
        System.out.println("w == prim       : " + (w == prim)); // true
    }
}
