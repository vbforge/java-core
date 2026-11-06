package com.vbforge.practice.practice_5_method_references;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Bound instance method reference: {@code instance::method} is
 * {@code (args) -> instance.method(args)}.
 * The instance is fixed when the reference is created ("bound"); only the remaining
 * arguments come from the functional interface's parameters.
 */
public class BoundInstanceMethodReferenceDemo {

    public static void main(String[] args) {
        String prefix = "Hello, ";
        // 'prefix' is the bound object; concat receives one argument
        Function<String, String> greet = prefix::concat;
        System.out.println(greet.apply("Alice"));
        System.out.println(greet.apply("Bob"));

        // System.out is the bound object; println is the method
        Consumer<String> print = System.out::println;
        print.accept("bound to System.out");

        // A bound Predicate: 'str' is fixed
        String str = "java programming";
        Predicate<String> contains = str::contains;
        System.out.println("contains 'java': "   + contains.test("java"));
        System.out.println("contains 'python': " + contains.test("python"));
    }
}
