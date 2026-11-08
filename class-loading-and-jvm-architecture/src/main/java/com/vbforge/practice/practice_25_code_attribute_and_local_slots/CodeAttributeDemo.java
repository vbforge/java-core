package com.vbforge.practice.practice_25_code_attribute_and_local_slots;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Each method carries a Code attribute: max_stack (operand stack depth), max_locals (local
 * variable slots) and the bytecode. long and double take two stack and two local slots.
 */
public class CodeAttributeDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(SampleMath.class));

        List<ClassFileReader.MethodInfo> methods = new ArrayList<>(classFile.methods);
        methods.sort((a, b) -> a.name.compareTo(b.name));

        for (ClassFileReader.MethodInfo method : methods) {
            System.out.printf("%-7s %-12s max_stack=%d max_locals=%d %s%n",
                    method.name, method.descriptor, method.maxStack, method.maxLocals, method.mnemonics());
        }
    }
}
