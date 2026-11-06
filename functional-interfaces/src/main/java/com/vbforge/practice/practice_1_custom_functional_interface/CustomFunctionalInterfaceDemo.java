package com.vbforge.practice.practice_1_custom_functional_interface;

/**
 * Chains custom {@link Transformer}s built from method references and a lambda,
 * using the default method {@code andThen}.
 */
public class CustomFunctionalInterfaceDemo {

    public static void main(String[] args) {
        Transformer<String> trim  = String::trim;
        Transformer<String> upper = String::toUpperCase;
        Transformer<String> bang  = s -> s + "!";

        Transformer<String> pipeline = trim.andThen(upper).andThen(bang);
        System.out.println(pipeline.transform("  hello world  "));
    }
}
