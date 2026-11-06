package com.vbforge.practice.practice_12_two_argument_interfaces;

import java.util.function.BiConsumer;

/** {@code BiConsumer<T, U>}: (T, U) -> void. The abstract method is {@code accept(T, U)}. */
public class BiConsumerDemo {

    public static void main(String[] args) {
        BiConsumer<String, Integer> printInfo = (name, age) ->
            System.out.println(name + " is " + age + " years old.");

        printInfo.accept("Alice", 30);   // Alice is 30 years old.
    }
}
