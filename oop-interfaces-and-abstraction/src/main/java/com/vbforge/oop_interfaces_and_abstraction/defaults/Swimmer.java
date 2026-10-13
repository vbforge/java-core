package com.vbforge.oop_interfaces_and_abstraction.defaults;

/** Another capability that ALSO offers a default {@code move()} — the two clash in a class that has both. */
public interface Swimmer {

    default String move() {
        return "swim";
    }
}
