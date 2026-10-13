package com.vbforge.oop_interfaces_and_abstraction.defaults;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/** Overrides ONE default method and reuses the interface version with {@code Logger.super.warn(...)}. */
public class ShoutingLogger implements Logger {

    private final List<String> lines = new ArrayList<>();

    @Override
    public void log(String message) {
        lines.add(message);
    }

    @Override
    public void warn(String message) {
        Logger.super.warn(message.toUpperCase(Locale.ROOT));      // call the interface's own default
    }

    public List<String> lines() {
        return lines;
    }
}
