package com.vbforge.practice.practice_24_constant_pool;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.IOException;

/**
 * A string literal is stored as Utf8 text in the constant pool of the class file; the ldc
 * instruction turns it into the interned String at run time, so the same literal in two
 * classes is one object.
 */
public class StringLiteralInConstantPoolDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(SampleMath.class));

        System.out.println("\"hello bytecode\" is a Utf8 entry in SampleMath.class: "
                + classFile.utf8Constants.contains("hello bytecode"));
        System.out.println("banner() returns the same object as the literal here: "
                + (new SampleMath().banner() == "hello bytecode"));
    }
}
