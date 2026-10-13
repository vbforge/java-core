package com.vbforge.oop_interfaces_and_abstraction.basics;

/** A narrow capability: the holder can only look values up. */
public interface ReadableStore {

    /** Returns the value stored under {@code key}, or {@code null} if there is none. */
    String read(String key);
}
