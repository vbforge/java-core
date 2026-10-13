package com.vbforge.oop_interfaces_and_abstraction.defaults;

/** A capability with a default implementation of {@code move()}. */
public interface Flyer {

    default String move() {
        return "fly";
    }
}
