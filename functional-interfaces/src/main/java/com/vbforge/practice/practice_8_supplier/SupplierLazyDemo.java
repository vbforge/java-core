package com.vbforge.practice.practice_8_supplier;

import java.util.Random;
import java.util.function.Supplier;

/**
 * {@code Supplier<T>}: () -> T. The computation is deferred until {@code get()} is called.
 * Common uses: lazy defaults (Optional.orElseGet), object factories,
 * deferred logging ({@code log.debug(() -> expensiveMsg())}).
 */
public class SupplierLazyDemo {

    /** Simulates an expensive object. */
    static class HeavyObject {
        HeavyObject() {
            System.out.println("  [HeavyObject created]");
        }

        @Override
        public String toString() {
            return "HeavyObject@instance";
        }
    }

    public static void main(String[] args) {
        Supplier<String> greeting = () -> "Hello, world!";
        Random rng = new Random(42);                 // seeded: the output is reproducible
        Supplier<Double> random = rng::nextDouble;

        System.out.println("greeting: " + greeting.get());
        System.out.println("random 1: " + random.get());
        System.out.println("random 2: " + random.get());   // each get() computes a new value

        // Lazy initialization
        Supplier<HeavyObject> lazy = HeavyObject::new;      // not created yet
        System.out.println("Before lazy.get()");
        HeavyObject obj = lazy.get();                        // created here
        System.out.println("Got: " + obj);
    }
}
