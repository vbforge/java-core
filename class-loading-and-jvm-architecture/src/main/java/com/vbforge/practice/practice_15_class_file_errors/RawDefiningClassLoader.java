package com.vbforge.practice.practice_15_class_file_errors;

/** Fixture: exposes defineClass so a demo can feed it arbitrary bytes. Its parent cannot see application classes. */
public class RawDefiningClassLoader extends ClassLoader {

    public RawDefiningClassLoader() {
        super(ClassLoader.getSystemClassLoader().getParent());
    }

    public Class<?> define(String name, byte[] bytes) {
        return defineClass(name, bytes, 0, bytes.length);
    }
}
