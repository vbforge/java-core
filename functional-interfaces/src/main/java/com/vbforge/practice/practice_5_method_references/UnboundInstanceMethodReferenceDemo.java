package com.vbforge.practice.practice_5_method_references;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Unbound instance method reference: {@code ClassName::instanceMethod} is
 * {@code (obj, args) -> obj.instanceMethod(args)}.
 * The receiver is NOT fixed: it is the FIRST parameter of the functional interface.
 * Very common in Stream.map(), Stream.filter(), ...
 */
public class UnboundInstanceMethodReferenceDemo {

    public static void main(String[] args) {
        // (String s) -> s.toUpperCase()
        Function<String, String> upper = String::toUpperCase;
        System.out.println(upper.apply("hello"));

        // (String s) -> s.length()
        Function<String, Integer> len = String::length;
        System.out.println("length of 'method': " + len.apply("method"));

        // In a stream pipeline: each element is the receiver
        List<String> words = Arrays.asList("apple", "banana", "cherry");
        List<String> upperWords = words.stream()
            .map(String::toUpperCase)
            .collect(Collectors.toList());
        System.out.println("uppercased: " + upperWords);

        // Two parameters: (String a, String b) -> a.compareTo(b)
        Comparator<String> alpha = String::compareTo;
        List<String> sorted = new ArrayList<>(words);
        sorted.sort(alpha);
        System.out.println("sorted: " + sorted);
    }
}
