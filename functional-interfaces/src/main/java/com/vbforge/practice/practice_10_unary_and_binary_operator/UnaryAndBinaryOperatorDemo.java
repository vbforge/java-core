package com.vbforge.practice.practice_10_unary_and_binary_operator;

import java.util.Comparator;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * Same-type specializations of Function:
 * <ul>
 *   <li>{@code UnaryOperator<T> extends Function<T, T>}: T -> T</li>
 *   <li>{@code BinaryOperator<T> extends BiFunction<T, T, T>}: (T, T) -> T</li>
 * </ul>
 * Useful when input and output types are the same (String transforms, math, reduce).
 */
public class UnaryAndBinaryOperatorDemo {

    public static void main(String[] args) {
        UnaryOperator<String> trim  = String::trim;
        UnaryOperator<String> upper = String::toUpperCase;

        UnaryOperator<String> normalize = s -> upper.apply(trim.apply(s));
        System.out.println("normalize(\"  hello  \"): '" + normalize.apply("  hello  ") + "'");

        // UnaryOperator extends Function, so andThen is available (it returns a Function)
        Function<String, String> normalizeChained = trim.andThen(upper);
        System.out.println("andThen version:         '" + normalizeChained.apply("  hello  ") + "'");

        BinaryOperator<Integer> max = (a, b) -> a >= b ? a : b;
        System.out.println("max(3, 7): " + max.apply(3, 7));

        // BinaryOperator.maxBy / minBy: factory methods
        BinaryOperator<String> longer = BinaryOperator.maxBy(Comparator.comparingInt(String::length));
        System.out.println("longer(\"hi\", \"world\"): " + longer.apply("hi", "world"));
    }
}
