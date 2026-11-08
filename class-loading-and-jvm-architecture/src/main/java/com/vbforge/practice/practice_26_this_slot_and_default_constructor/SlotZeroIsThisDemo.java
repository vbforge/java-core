package com.vbforge.practice.practice_26_this_slot_and_default_constructor;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.IOException;

/**
 * In an instance method local slot 0 holds "this", so the first parameter is in slot 1
 * (iload_1). A static method has no "this": its first parameter is in slot 0 (iload_0).
 */
public class SlotZeroIsThisDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(SampleMath.class));

        ClassFileReader.MethodInfo add = classFile.method("add");
        ClassFileReader.MethodInfo square = classFile.method("square");

        System.out.println("instance add(int,int):  " + add.mnemonics() + "  first load = " + add.mnemonics().get(0));
        System.out.println("static   square(int):   " + square.mnemonics() + "  first load = " + square.mnemonics().get(0));
        System.out.println("add loads slots 1 and 2 because slot 0 is this; square starts at slot 0");
    }
}
