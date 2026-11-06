package com.vbforge.practice.practice_9_consumer;

import java.util.function.Consumer;

/**
 * {@code Consumer<T>}: T -> void. Used to DO something with a value (side effect) without
 * returning anything: forEach, logging, event handlers, save operations.
 * {@code andThen} chains two consumers sequentially.
 */
public class ConsumerAndThenDemo {

    public static void main(String[] args) {
        Consumer<String> print      = System.out::println;
        Consumer<String> printUpper = s -> System.out.println(s.toUpperCase());

        // andThen: both consumers run, in order, on the same value
        Consumer<String> both = print.andThen(printUpper);
        both.accept("consumer chain");
    }
}
