package com.vbforge.practice.practice_19_out_of_memory_errors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * "Java heap space": live objects do not fit in -Xmx. The demo starts a child JVM limited to
 * 16 MB (Serial GC, so the message is the plain one) that hoards byte arrays until it fails.
 */
public class HeapSpaceOutOfMemoryDemo {

    private static final String CHILD = "--child";

    public static void main(String[] args) throws Exception {
        if (args.length > 0 && CHILD.equals(args[0])) {
            fillHeapUntilOutOfMemory();
            return;
        }
        for (String line : ChildJvm.run(HeapSpaceOutOfMemoryDemo.class,
                Arrays.asList("-Xmx16m", "-XX:+UseSerialGC"), CHILD)) {
            System.out.println("child JVM: " + line);
        }
    }

    private static void fillHeapUntilOutOfMemory() {
        List<byte[]> hoard = new ArrayList<>();
        try {
            while (true) {
                hoard.add(new byte[256 * 1024]);
            }
        } catch (OutOfMemoryError e) {
            hoard.clear();
            System.out.println("OutOfMemoryError: " + e.getMessage());
        }
    }
}
