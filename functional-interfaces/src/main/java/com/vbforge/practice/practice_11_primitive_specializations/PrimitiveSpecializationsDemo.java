package com.vbforge.practice.practice_11_primitive_specializations;

import java.util.function.IntBinaryOperator;
import java.util.function.IntPredicate;
import java.util.function.IntSupplier;
import java.util.function.IntUnaryOperator;

/**
 * Primitive specializations (int, long, double) avoid autoboxing:
 * <pre>
 *   IntFunction&lt;R&gt;     int -&gt; R          ToIntFunction&lt;T&gt;   T -&gt; int
 *   IntUnaryOperator   int -&gt; int        IntBinaryOperator  (int, int) -&gt; int
 *   IntSupplier        () -&gt; int         IntConsumer        int -&gt; void
 *   IntPredicate       int -&gt; boolean
 * </pre>
 * Use them in performance-sensitive code or with Stream.mapToInt and friends.
 */
public class PrimitiveSpecializationsDemo {

    public static void main(String[] args) {
        IntUnaryOperator  square   = n -> n * n;
        IntBinaryOperator add      = (a, b) -> a + b;
        IntPredicate      isEven   = n -> n % 2 == 0;
        IntSupplier       fortyTwo = () -> 42;

        System.out.println("square(9): "  + square.applyAsInt(9));
        System.out.println("add(3, 4): "  + add.applyAsInt(3, 4));
        System.out.println("isEven(6): "  + isEven.test(6));
        System.out.println("isEven(7): "  + isEven.test(7));
        System.out.println("fortyTwo(): " + fortyTwo.getAsInt());
    }
}
