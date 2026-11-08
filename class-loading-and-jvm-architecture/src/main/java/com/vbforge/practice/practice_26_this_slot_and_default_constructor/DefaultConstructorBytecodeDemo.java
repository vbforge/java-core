package com.vbforge.practice.practice_26_this_slot_and_default_constructor;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.EmptyClass;

import java.io.IOException;
import java.lang.reflect.Modifier;

/**
 * EmptyClass declares nothing, yet its class file has one method: the constructor &lt;init&gt;()V
 * that javac wrote. It loads this (aload_0), calls Object's constructor (invokespecial) and returns.
 */
public class DefaultConstructorBytecodeDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(EmptyClass.class));

        System.out.println("methods in the class file: " + classFile.methods.size());
        ClassFileReader.MethodInfo init = classFile.method("<init>");
        System.out.println("<init> descriptor: " + init.descriptor);
        System.out.println("<init> bytecode:   " + init.mnemonics());
        System.out.println("max_stack=" + init.maxStack + " max_locals=" + init.maxLocals + " (slot 0 = this)");

        System.out.println("reflection sees " + EmptyClass.class.getDeclaredConstructors().length
                + " constructor, public: " + Modifier.isPublic(EmptyClass.class.getDeclaredConstructors()[0].getModifiers()));
    }
}
