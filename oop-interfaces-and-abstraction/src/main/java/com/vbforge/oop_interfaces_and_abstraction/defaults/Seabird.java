package com.vbforge.oop_interfaces_and_abstraction.defaults;

/** A more specific interface: it extends {@link Flyer} and overrides its default. */
public interface Seabird extends Flyer {

    @Override
    default String move() {
        return "glide";
    }
}
