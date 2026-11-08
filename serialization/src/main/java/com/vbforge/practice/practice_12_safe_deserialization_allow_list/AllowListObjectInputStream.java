package com.vbforge.practice.practice_12_safe_deserialization_allow_list;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;
import java.util.Set;

/**
 * Shared helper for this package: an ObjectInputStream that only resolves classes from an allow-list.
 * Works on Java 8 (ObjectInputFilter is Java 9+).
 */
public class AllowListObjectInputStream extends ObjectInputStream {

    private final Set<String> allowed;

    public AllowListObjectInputStream(InputStream in, Set<String> allowed) throws IOException {
        super(in);
        this.allowed = allowed;
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc) throws IOException, ClassNotFoundException {
        if (!allowed.contains(desc.getName())) {
            throw new InvalidClassException(desc.getName(), "class is not on the allow-list");
        }
        return super.resolveClass(desc);
    }
}
