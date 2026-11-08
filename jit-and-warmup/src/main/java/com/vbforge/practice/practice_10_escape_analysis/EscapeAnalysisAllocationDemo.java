package com.vbforge.practice.practice_10_escape_analysis;

import java.lang.management.ManagementFactory;

/**
 * Escape analysis made visible: measure the bytes allocated per iteration.
 * NoEscape: the Point objects never leave the method, C2 can remove the allocation (scalar replacement).
 * GlobalEscape: the Point is stored in a static field, so it is always a real heap object.
 * Only C2 does this. Compare with: -XX:-DoEscapeAnalysis (allocation comes back), -Xint (always allocates).
 * Byte counts are approximate and depend on the JVM, so read them as "about zero" vs "not zero".
 */
public class EscapeAnalysisAllocationDemo {

    record Point(long x, long y) {
        Point minus(Point o) {
            return new Point(x - o.x, y - o.y);
        }

        long lengthSquared() {
            return x * x + y * y;
        }
    }

    static volatile Point escaped;
    static volatile long sink;

    static long noEscape(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            s += new Point(i, i + 1).minus(new Point(1, 2)).lengthSquared();
        }
        return s;
    }

    static long globalEscape(int n) {
        long s = 0;
        for (int i = 0; i < n; i++) {
            Point p = new Point(i, i + 1);
            escaped = p;
            s += p.minus(new Point(1, 2)).lengthSquared();
        }
        return s;
    }

    interface Work {
        long run(int n);
    }

    static void measure(String label, Work work) {
        com.sun.management.ThreadMXBean mx =
                (com.sun.management.ThreadMXBean) ManagementFactory.getThreadMXBean();
        if (!mx.isThreadAllocatedMemorySupported()) {
            System.out.println(label + ": allocation measurement not supported");
            return;
        }
        long threadId = Thread.currentThread().getId();
        int n = 200_000;
        long result = 0;
        long bytesPerIteration = 0;
        for (int round = 0; round < 40; round++) {
            long before = mx.getThreadAllocatedBytes(threadId);
            result = work.run(n);
            bytesPerIteration = (mx.getThreadAllocatedBytes(threadId) - before) / n;
        }
        sink = result;
        System.out.println(String.format("%-13s checksum=%d  ~%d bytes allocated per iteration (last round)",
                label, result, bytesPerIteration));
    }

    public static void main(String[] args) {
        measure("noEscape", EscapeAnalysisAllocationDemo::noEscape);
        measure("globalEscape", EscapeAnalysisAllocationDemo::globalEscape);
    }
}
