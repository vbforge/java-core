package com.vbforge.practice.practice_2_jit_introspection;

import java.lang.management.CompilationMXBean;
import java.lang.management.ManagementFactory;

/**
 * CompilationMXBean: ask the running JVM about its JIT compiler.
 * Total compilation time only ever grows; a hot loop makes the JIT work.
 */
public class CompilationMxBeanDemo {

    static volatile long sink;

    static long hot(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += i % 13;
        }
        return s;
    }

    public static void main(String[] args) {
        CompilationMXBean jit = ManagementFactory.getCompilationMXBean();
        System.out.println("JIT compiler name: " + jit.getName());
        boolean supported = jit.isCompilationTimeMonitoringSupported();
        System.out.println("compilation time monitoring supported: " + supported);

        if (!supported) {
            System.out.println("nothing more to show on this JVM");
            return;
        }

        long before = jit.getTotalCompilationTime();
        long acc = 0;
        for (int i = 0; i < 20_000; i++) {
            acc += hot(1_000);
        }
        sink = acc;
        long after = jit.getTotalCompilationTime();

        System.out.println("total compilation time never decreases: " + (after >= before));
        System.out.println("checksum = " + acc);
    }
}
