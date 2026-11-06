package com.vbforge.practice.practice_4_lambda_as_value;

import java.util.function.Function;

/**
 * Lambdas are first-class values: store them in a variable, pass them as a method
 * argument, return them from a method.
 */
public class LambdaAsValueDemo {

    /** Accepts a function as an argument: a higher-order function. */
    static int applyTwice(int value, Function<Integer, Integer> fn) {
        return fn.apply(fn.apply(value));
    }

    /** Returns a lambda: a factory of behavior. */
    static Function<Integer, Integer> multiplier(int factor) {
        // 'factor' is effectively final: captured by the returned lambda
        return n -> n * factor;
    }

    public static void main(String[] args) {
        // Store
        Function<Integer, Integer> doubleIt = n -> n * 2;
        Function<Integer, Integer> addTen   = n -> n + 10;

        // Pass
        System.out.println("applyTwice(5, doubleIt): " + applyTwice(5, doubleIt));
        System.out.println("applyTwice(5, addTen):   " + applyTwice(5, addTen));

        // Return
        Function<Integer, Integer> tripler = multiplier(3);
        System.out.println("tripler(7): " + tripler.apply(7));
    }
}
