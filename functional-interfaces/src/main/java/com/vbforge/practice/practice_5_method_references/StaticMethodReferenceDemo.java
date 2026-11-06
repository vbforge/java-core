package com.vbforge.practice.practice_5_method_references;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.IntUnaryOperator;

/**
 * Static method reference: {@code ClassName::staticMethod} is
 * {@code (args) -> ClassName.staticMethod(args)}.
 */
public class StaticMethodReferenceDemo {

    public static void main(String[] args) {
        // Lambda version (verbose)
        Function<String, Integer> parseLambda = s -> Integer.parseInt(s);

        // Method reference version (concise)
        Function<String, Integer> parseRef = Integer::parseInt;

        System.out.println("lambda: " + parseLambda.apply("42"));
        System.out.println("ref:    " + parseRef.apply("42"));

        // Math::abs as an IntUnaryOperator
        IntUnaryOperator abs = Math::abs;
        System.out.println("abs(-7): " + abs.applyAsInt(-7));

        // Integer::compare as a Comparator
        List<Integer> nums = Arrays.asList(5, 2, 8, 1, 9);
        nums.sort(Integer::compare);
        System.out.println("sorted: " + nums);
    }
}
