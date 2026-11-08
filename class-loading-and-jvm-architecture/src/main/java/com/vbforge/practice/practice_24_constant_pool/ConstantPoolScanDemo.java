package com.vbforge.practice.practice_24_constant_pool;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.IOException;

/**
 * The constant pool is the class file's table of names, descriptors and literals; everything
 * else (methods, fields, instructions) refers to it by index (indices start at 1). The scan
 * checks which Utf8 entries are in it.
 */
public class ConstantPoolScanDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(SampleMath.class));

        System.out.println("constant_pool_count is larger than 10: " + (classFile.constantPoolCount > 10));

        String internalName = SampleMath.class.getName().replace('.', '/');
        String[] wanted = {internalName, "java/lang/Object", "add", "square", "twice", "mix", "banner", "(II)I", "Code"};
        for (String entry : wanted) {
            System.out.println("Utf8 \"" + entry + "\" present: " + classFile.utf8Constants.contains(entry));
        }
    }
}
