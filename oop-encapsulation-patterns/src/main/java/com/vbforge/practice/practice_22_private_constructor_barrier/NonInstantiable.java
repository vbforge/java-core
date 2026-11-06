package com.vbforge.practice.practice_22_private_constructor_barrier;

/**
 * A private constructor blocks 'new' from every other class (utility-class style).
 * Note: it only blocks OTHER classes. Code inside this class, and nested classes of it,
 * can still call it.
 */
final class NonInstantiable {

    private NonInstantiable() { }

    static String purpose() {
        return "static helpers only, no instances";
    }
}
