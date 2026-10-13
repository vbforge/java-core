package com.vbforge.oop_interfaces_and_abstraction.basics;

import java.util.HashMap;
import java.util.Map;

/** In-memory implementation. One object, three types: {@link ReadWriteStore}, {@link ReadableStore}, {@link WritableStore}. */
public class MemoryStore implements ReadWriteStore {

    private final Map<String, String> data = new HashMap<>();

    @Override
    public String read(String key) {
        return data.get(key);
    }

    @Override
    public void write(String key, String value) {
        data.put(key, value);
    }
}
