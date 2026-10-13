package com.vbforge.oop_interfaces_and_abstraction.defaults;

import java.util.ArrayList;
import java.util.List;

/** Implements ONLY the abstract method; {@code warn} and {@code error} come from the interface. */
public class PlainLogger implements Logger {

    private final List<String> lines = new ArrayList<>();

    @Override
    public void log(String message) {
        lines.add(message);
    }

    public List<String> lines() {
        return lines;
    }
}
