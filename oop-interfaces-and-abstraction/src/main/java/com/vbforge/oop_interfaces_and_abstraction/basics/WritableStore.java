package com.vbforge.oop_interfaces_and_abstraction.basics;

/** A narrow capability: the holder can only store values. */
public interface WritableStore {

    void write(String key, String value);
}
