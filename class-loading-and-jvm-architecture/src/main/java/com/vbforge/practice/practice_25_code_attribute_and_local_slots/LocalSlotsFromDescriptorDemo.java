package com.vbforge.practice.practice_25_code_attribute_and_local_slots;

import com.vbforge.practice.practice_2_loader_sample_classes.ClassBytes;
import com.vbforge.practice.practice_22_class_file_sample_classes.ClassFileReader;
import com.vbforge.practice.practice_22_class_file_sample_classes.SampleMath;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * The local slots needed by the parameters follow from the descriptor alone: one slot for
 * "this" in an instance method, one per parameter, two for long (J) and double (D).
 * For the sample methods (no extra locals) that equals max_locals in the class file.
 */
public class LocalSlotsFromDescriptorDemo {

    public static void main(String[] args) throws IOException {
        ClassFileReader.ClassFile classFile = ClassFileReader.parse(ClassBytes.read(SampleMath.class));

        List<ClassFileReader.MethodInfo> methods = new ArrayList<>(classFile.methods);
        methods.sort((a, b) -> a.name.compareTo(b.name));

        for (ClassFileReader.MethodInfo method : methods) {
            int computed = slotsFor(method.descriptor, method.isStatic());
            System.out.printf("%-7s %-12s %-8s computed=%d max_locals=%d match=%b%n",
                    method.name, method.descriptor, method.isStatic() ? "static" : "instance",
                    computed, method.maxLocals, computed == method.maxLocals);
        }

        System.out.println("(JJ)V static                         -> " + slotsFor("(JJ)V", true));
        System.out.println("(Ljava/lang/String;[IJ)V instance    -> " + slotsFor("(Ljava/lang/String;[IJ)V", false));
        System.out.println("([[Ljava/lang/String;D)V static      -> " + slotsFor("([[Ljava/lang/String;D)V", true));
    }

    static int slotsFor(String descriptor, boolean isStatic) {
        int slots = isStatic ? 0 : 1;
        int i = 1;                                       // skip '('
        while (descriptor.charAt(i) != ')') {
            char c = descriptor.charAt(i);
            if (c == 'J' || c == 'D') {
                slots += 2;
                i++;
            } else if (c == 'L') {
                slots += 1;
                i = descriptor.indexOf(';', i) + 1;
            } else if (c == '[') {
                slots += 1;
                while (descriptor.charAt(i) == '[') {
                    i++;
                }
                i = descriptor.charAt(i) == 'L' ? descriptor.indexOf(';', i) + 1 : i + 1;
            } else {
                slots += 1;
                i++;
            }
        }
        return slots;
    }
}
