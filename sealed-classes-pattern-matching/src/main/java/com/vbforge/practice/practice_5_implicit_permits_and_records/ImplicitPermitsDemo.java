package com.vbforge.practice.practice_5_implicit_permits_and_records;

import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * When all subtypes live in the SAME source file, permits can be omitted: the compiler infers
 * the permitted subtypes from the file. Records are implicitly final.
 */
public class ImplicitPermitsDemo {

    static String describe(TrafficSignal signal) {
        return switch (signal) {
            case Stop s    -> "stop";
            case Go g      -> "go";
            case Caution c -> "caution for " + c.seconds() + "s";
        };
    }

    public static void main(String[] args) {
        System.out.println("sealed: " + TrafficSignal.class.isSealed());
        String[] names = Arrays.stream(TrafficSignal.class.getPermittedSubclasses())
                .map(Class::getSimpleName)
                .sorted()
                .toArray(String[]::new);
        System.out.println("permitted (inferred): " + Arrays.toString(names));
        System.out.println("Stop is final: " + Modifier.isFinal(Stop.class.getModifiers()) + " (record)");

        System.out.println(describe(new Stop()));
        System.out.println(describe(new Go()));
        System.out.println(describe(new Caution(3)));
    }
}

// no 'permits' clause: all subtypes are in this file
sealed interface TrafficSignal {}
record Stop() implements TrafficSignal {}
record Go() implements TrafficSignal {}
record Caution(int seconds) implements TrafficSignal {}
