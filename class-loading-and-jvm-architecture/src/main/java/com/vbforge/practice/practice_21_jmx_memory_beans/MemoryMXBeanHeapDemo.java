package com.vbforge.practice.practice_21_jmx_memory_beans;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

/**
 * MemoryMXBean reports heap and non-heap usage: used <= committed <= max. Holding a 16 MB array
 * raises the used heap by roughly that much (System.gc() first, so older garbage cannot hide it).
 */
public class MemoryMXBeanHeapDemo {

    public static void main(String[] args) {
        MemoryMXBean bean = ManagementFactory.getMemoryMXBean();

        MemoryUsage heap = bean.getHeapMemoryUsage();
        System.out.println("heap: used <= committed:             " + (heap.getUsed() <= heap.getCommitted()));
        System.out.println("heap: committed <= max (when known): " + (heap.getMax() == -1 || heap.getCommitted() <= heap.getMax()));

        System.gc();
        long before = bean.getHeapMemoryUsage().getUsed();
        byte[] block = new byte[16 * 1024 * 1024];
        long after = bean.getHeapMemoryUsage().getUsed();

        System.out.println("a live 16 MB array raised used heap by at least 8 MB: " + (after - before >= 8L * 1024 * 1024));
        System.out.println("block kept alive until here, length = " + block.length);

        System.out.println("non-heap (Metaspace, code cache, ...) used > 0: "
                + (bean.getNonHeapMemoryUsage().getUsed() > 0));
    }
}
