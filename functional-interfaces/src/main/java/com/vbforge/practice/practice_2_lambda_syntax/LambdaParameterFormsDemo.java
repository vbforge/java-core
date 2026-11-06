package com.vbforge.practice.practice_2_lambda_syntax;

import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Lambda syntax by number of parameters:
 * <pre>
 *   ()        -> expression                  // no params
 *   param     -> expression                  // one param, parentheses optional
 *   (a, b)    -> { statements; return val; } // several params, block body, explicit return
 * </pre>
 * Expression body vs block body and anonymous class vs lambda are practiced in
 * enums_and_nested_classes.practice_10_anonymous_to_lambda.
 */
public class LambdaParameterFormsDemo {

    public static void main(String[] args) {
        // No params
        Runnable noParams = () -> System.out.println("no-param lambda");

        // Single param: parentheses optional, type inferred
        Function<String, String> shout = s -> s.toUpperCase() + "!";

        // Multi-param, block body with explicit return
        BiFunction<Integer, Integer, Integer> clamp = (value, max) -> {
            if (value > max) return max;
            return value;
        };

        noParams.run();
        System.out.println("shout(\"hello\"): " + shout.apply("hello"));
        System.out.println("clamp(150, 100): " + clamp.apply(150, 100));
    }
}
