package com.vbforge.practice.practice_17_compile_puzzles;

/**
 * Will this compile? A wildcard only varies the TYPE ARGUMENT - the generic classes
 * themselves must still be related. Base and Derived below are two UNRELATED classes
 * (Derived does not extend Base), despite the names - that is the trap.
 */
public class WildcardWithUnrelatedClassesDemo {

    public static void main(String[] args) {
        // Compilation results of the wider matrix:
        //   Base<? extends Number> b1 = new Base<Number>();        // OK
        //   Base<? extends Number> b2 = new Derived<Number>();     // ERROR: Derived is not a subtype of Base
        //   Base<? extends Number> b3 = new Derived<Integer>();    // ERROR: same reason
        //   Derived<? extends Number> b4 = new Derived<Integer>(); // OK
        //   Base<?> b5 = new Derived<Integer>();                   // ERROR: same reason
        //   Derived<?> b6 = new Derived<Integer>();                // OK
        Base<? extends Number> b1 = new Base<Number>();
        Derived<? extends Number> b4 = new Derived<Integer>();
        Derived<?> b6 = new Derived<Integer>();

        System.out.println("All compile puzzles ran cleanly (see comments for the error cases).");
        System.out.println(b1.getClass().getSimpleName() + ", " + b4.getClass().getSimpleName() + ", " + b6.getClass().getSimpleName());
    }

    static class Base<T> {
    }

    static class Derived<T> {
    }
}
