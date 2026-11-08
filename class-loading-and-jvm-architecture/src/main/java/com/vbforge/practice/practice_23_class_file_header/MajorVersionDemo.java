package com.vbforge.practice.practice_23_class_file_header;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.IOException;

/**
 * After the magic come minor and major version. Major version = Java release + 44
 * (52 = Java 8, 55 = 11, 61 = 17, 65 = 21). A JVM refuses a major version it does not know.
 */
public class MajorVersionDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(SampleMath.class));

        System.out.println("minor version = " + classFile.minor);
        System.out.println("major version = " + classFile.major + "  -> compiled for Java " + (classFile.major - 44));

        System.out.println("major -> release table:");
        for (int major : new int[]{52, 55, 61, 65}) {
            System.out.println("   " + major + " -> Java " + (major - 44));
        }
    }
}
