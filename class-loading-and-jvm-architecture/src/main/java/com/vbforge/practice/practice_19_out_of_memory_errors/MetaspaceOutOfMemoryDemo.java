package com.vbforge.practice.practice_19_out_of_memory_errors;

import com.vbforge.practice.practice_2_loader_sample_classes.Greeter;
import com.vbforge.practice.practice_4_find_class_and_define_class.IsolatingClassLoader;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class metadata lives in Metaspace (native memory), not on the heap. A child JVM with
 * -XX:MaxMetaspaceSize=16m defines Greeter again and again, each time in a new loader and
 * kept reachable, until Metaspace is full. (String.concat is used in the catch block on
 * purpose: it needs no new class, so reporting the error cannot fail by itself.)
 */
public class MetaspaceOutOfMemoryDemo {

    private static final String CHILD = "--child";

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && CHILD.equals(args[0])) {
            defineClassesUntilOutOfMemory();
            return;
        }
        for (String line : ChildJvm.run(MetaspaceOutOfMemoryDemo.class,
                Arrays.asList("-XX:MaxMetaspaceSize=16m", "-XX:+UseSerialGC"), CHILD)) {
            System.out.println("child JVM: " + line);
        }
    }

    private static void defineClassesUntilOutOfMemory() throws Exception {
        List<Class<?>> kept = new ArrayList<>();
        String name = Greeter.class.getName();
        try {
            for (int i = 0; i < 1_000_000; i++) {
                kept.add(new IsolatingClassLoader().loadClass(name));
            }
            System.out.println("limit was not reached");
        } catch (OutOfMemoryError e) {
            System.out.println("OutOfMemoryError: ".concat(String.valueOf(e.getMessage())));
        }
    }
}
