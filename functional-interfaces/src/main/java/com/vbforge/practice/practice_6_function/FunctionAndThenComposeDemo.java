package com.vbforge.practice.practice_6_function;

import java.util.function.Function;

/**
 * {@code Function<T, R>}: transform T into R.
 * <ul>
 *   <li>{@code f.andThen(g)} is {@code x -> g.apply(f.apply(x))}: left to right.</li>
 *   <li>{@code f.compose(g)} is {@code x -> f.apply(g.apply(x))}: g runs first.</li>
 * </ul>
 */
public class FunctionAndThenComposeDemo {

    public static void main(String[] args) {
        Function<String, Integer> length   = String::length;
        Function<Integer, String> intToHex = Integer::toHexString;

        // andThen: length first, then intToHex: "hello" -> 5 -> "5"
        Function<String, String> lengthAsHex = length.andThen(intToHex);
        System.out.println("\"hello\" length as hex: " + lengthAsHex.apply("hello"));

        // compose: intToHex.compose(length) is the same pipeline written from the other end
        Function<String, String> composed = intToHex.compose(length);
        System.out.println("composed result:       " + composed.apply("hello"));

        // The order matters when both functions are Integer -> Integer
        Function<Integer, Integer> plusOne  = x -> x + 1;
        Function<Integer, Integer> timesTwo = x -> x * 2;
        System.out.println("plusOne.andThen(timesTwo).apply(5): " + plusOne.andThen(timesTwo).apply(5));   // 12
        System.out.println("plusOne.compose(timesTwo).apply(5): " + plusOne.compose(timesTwo).apply(5));   // 11
    }
}
