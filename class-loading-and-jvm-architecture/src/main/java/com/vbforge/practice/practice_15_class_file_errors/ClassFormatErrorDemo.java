package com.vbforge.practice.practice_15_class_file_errors;

import com.vbforge.practice.practice_15_class_file_errors.RawDefiningClassLoader;
import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_2_loader_sample_classes.Counter;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

/**
 * Bytes that are not a valid class file make defineClass throw ClassFormatError:
 * wrong magic number, or a file cut short.
 */
public class ClassFormatErrorDemo {

    public static void main(String[] args) {
        RawDefiningClassLoader loader = new RawDefiningClassLoader();

        byte[] garbage = "this is plain text, not a class file".getBytes(StandardCharsets.UTF_8);
        try {
            loader.define(null, garbage);
        } catch (ClassFormatError e) {
            System.out.println("garbage bytes   -> " + e.getClass().getSimpleName()
                    + ", is a LinkageError: " + (e instanceof LinkageError));
        }

        byte[] truncated = Arrays.copyOf(ClassBytes.read(Counter.class), 30);
        try {
            loader.define(Counter.class.getName(), truncated);
        } catch (ClassFormatError e) {
            System.out.println("truncated file  -> " + e.getClass().getSimpleName());
        }
    }
}
