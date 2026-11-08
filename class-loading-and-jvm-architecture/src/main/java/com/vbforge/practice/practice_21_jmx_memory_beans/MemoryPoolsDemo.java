package com.vbforge.practice.practice_21_jmx_memory_beans;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;

/**
 * The JVM splits memory into pools. The heap pools depend on the garbage collector, so their
 * names are not printed; the pool called Metaspace exists on HotSpot since Java 8 (it replaced PermGen).
 */
public class MemoryPoolsDemo {

    public static void main(String[] args) {
        int heapPools = 0;
        int nonHeapPools = 0;
        boolean metaspace = false;

        for (MemoryPoolMXBean pool : ManagementFactory.getMemoryPoolMXBeans()) {
            if (pool.getType() == MemoryType.HEAP) {
                heapPools++;
            } else {
                nonHeapPools++;
            }
            if ("Metaspace".equals(pool.getName())) {
                metaspace = true;
            }
        }

        System.out.println("at least one heap pool:        " + (heapPools > 0));
        System.out.println("at least one non-heap pool:    " + (nonHeapPools > 0));
        System.out.println("a pool named Metaspace exists: " + metaspace);
    }
}
