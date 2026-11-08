package com.vbforge.practice.practice_15_class_file_errors;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_2_loader_sample_classes.Counter;

/**
 * The major version sits in bytes 6-7 of the class file (big-endian). Patch it to a version
 * newer than this JVM knows and defineClass throws UnsupportedClassVersionError,
 * the error you get when you run a class compiled by a newer JDK on an older JRE.
 */
public class UnsupportedClassVersionDemo {

    public static void main(String[] args) {
        String name = Counter.class.getName();
        byte[] original = ClassBytes.read(Counter.class);

        Class<?> ok = new RawDefiningClassLoader().define(name, original);
        System.out.println("original bytes -> defined " + ok.getSimpleName());

        byte[] future = original.clone();
        future[6] = 0;
        future[7] = 99;
        try {
            new RawDefiningClassLoader().define(name, future);
        } catch (UnsupportedClassVersionError e) {
            System.out.println("major version 99 -> " + e.getClass().getSimpleName());
            System.out.println("   is a ClassFormatError: " + (e instanceof ClassFormatError));
        }
    }
}
