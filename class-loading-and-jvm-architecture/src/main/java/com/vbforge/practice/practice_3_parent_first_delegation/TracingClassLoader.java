package com.vbforge.practice.practice_3_parent_first_delegation;

import java.util.ArrayList;
import java.util.List;

/**
 * A loader that changes nothing about delegation (it still calls super.loadClass,
 * which is parent-first) and only records what it was asked and when findClass ran.
 */
public class TracingClassLoader extends ClassLoader {

    private final List<String> trace = new ArrayList<>();

    public TracingClassLoader(ClassLoader parent) {
        super(parent);
    }

    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        trace.add("loadClass " + name);
        return super.loadClass(name, resolve);
    }

    /** Only reached when findLoadedClass and the whole parent chain have failed. */
    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        trace.add("findClass " + name + "  (the parent could not supply it)");
        throw new ClassNotFoundException(name);
    }

    public List<String> drainTrace() {
        List<String> copy = new ArrayList<>(trace);
        trace.clear();
        return copy;
    }
}
