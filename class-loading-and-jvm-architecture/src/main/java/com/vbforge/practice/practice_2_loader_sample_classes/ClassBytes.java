package com.vbforge.practice.practice_2_loader_sample_classes;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Shared fixture (not a concept): reads the raw .class bytes of a class from the
 * classpath. Every custom loader in this practice gets its bytes here.
 */
public final class ClassBytes {

    private ClassBytes() {
    }

    public static byte[] read(String className) throws ClassNotFoundException {
        String resource = className.replace('.', '/') + ".class";
        try (InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(resource)) {
            if (in == null) {
                throw new ClassNotFoundException(className);
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
            }
            return out.toByteArray();
        } catch (IOException e) {
            throw new ClassNotFoundException(className, e);
        }
    }

    public static byte[] read(Class<?> type) {
        try {
            return read(type.getName());
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("class file not on the classpath: " + type.getName(), e);
        }
    }
}
