package com.vbforge.practice.practice_2_system_an_runtime;

/**
 * Demonstrates java.lang.System and java.lang.Runtime.
 *
 * System  — static utility class for system-level operations:
 *   System.out / err / in  — standard streams
 *   System.currentTimeMillis() / nanoTime() — timing
 *   System.getenv()         — environment variables
 *   System.getProperty()    — JVM / system properties
 *   System.arraycopy()      — fast native array copy
 *   System.gc()             — suggest GC (not guaranteed)
 *   System.exit()           — terminate JVM
 *   System.lineSeparator()  — platform-aware line separator
 *
 * Runtime — single instance per JVM, runtime environment info:
 *   Runtime.getRuntime()    — singleton accessor
 *   availableProcessors()   — CPU cores available
 *   totalMemory()           — current heap size
 *   freeMemory()            — free heap
 *   maxMemory()             — max heap (-Xmx)
 */
public class SystemAndRuntimeDemo {

    public static void main(String[] args) {
        System.out.println("=== System and Runtime Demo ===\n");

        // --- Timing ---
        System.out.println("-- Timing --");
        long epochMs   = System.currentTimeMillis();
        long nanoStart = System.nanoTime();

        // Do some work to measure
        long sum = 0;
        for (int i = 0; i < 1_000_000; i++) sum += i;

        long elapsedNs = System.nanoTime() - nanoStart;
        System.out.println("currentTimeMillis(): " + epochMs + " (epoch ms)");
        System.out.printf("nanoTime elapsed:    %,d ns (%.3f ms) for 1M iterations%n",
            elapsedNs, elapsedNs / 1_000_000.0);
        System.out.println("sum = " + sum + " (prevent dead code elimination)");

        // currentTimeMillis: wall-clock time (affected by system clock changes)
        // nanoTime:          monotonic, high-precision — use for elapsed time measurements

        // --- System properties ---
        System.out.println("\n-- Key System properties --");
        String[] props = {
            "java.version", "java.vendor", "os.name", "os.arch",
            "user.name",    "user.home",   "file.separator", "line.separator"
        };
        for (String prop : props) {
            String value = System.getProperty(prop);
            if ("line.separator".equals(prop)) {
                value = value.replace("\r", "\\r").replace("\n", "\\n");
            }
            System.out.printf("  %-18s = %s%n", prop, value);
        }

        // --- Environment variables ---
        System.out.println("\n-- Environment variables (common ones) --");
        String[] envVars = { "PATH", "HOME", "JAVA_HOME", "USERNAME" };
        for (String var : envVars) {
            String value = System.getenv(var);
            if (value != null && value.length() > 60) value = value.substring(0, 57) + "...";
            System.out.printf("  %-12s = %s%n", var, value);
        }

        // --- arraycopy ---
        System.out.println("\n-- System.arraycopy (native, fastest array copy) --");
        int[] source = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        int[] dest   = new int[10];
        // arraycopy(src, srcPos, dest, destPos, length)
        System.arraycopy(source, 2, dest, 0, 5);  // copy 5 elements from index 2
        System.out.print("arraycopy result: ");
        for (int v : dest) System.out.print(v + " ");
        System.out.println();

        // --- identityHashCode ---
        System.out.println("\n-- System.identityHashCode --");
        String s1 = new String("hello");
        String s2 = new String("hello");
        System.out.println("s1.hashCode():                " + s1.hashCode());
        System.out.println("s2.hashCode():                " + s2.hashCode() + "  ← same (overridden)");
        System.out.println("identityHashCode(s1):         " + System.identityHashCode(s1));
        System.out.println("identityHashCode(s2):         " + System.identityHashCode(s2)
            + "  ← different objects");

        // --- lineSeparator ---
        System.out.println("\n-- lineSeparator --");
        System.out.println("lineSeparator: "
            + System.lineSeparator().replace("\r", "\\r").replace("\n", "\\n"));

        // --- Runtime ---
        System.out.println("\n-- Runtime --");
        Runtime rt = Runtime.getRuntime();
        System.out.println("availableProcessors: " + rt.availableProcessors());
        System.out.printf("maxMemory:           %,d bytes (%.0f MB)%n",
            rt.maxMemory(), rt.maxMemory() / 1_048_576.0);
        System.out.printf("totalMemory:         %,d bytes (%.0f MB)%n",
            rt.totalMemory(), rt.totalMemory() / 1_048_576.0);
        System.out.printf("freeMemory:          %,d bytes (%.0f MB)%n",
            rt.freeMemory(), rt.freeMemory() / 1_048_576.0);

        // Suggest GC (JVM may or may not honour this)
        rt.gc();
        System.out.printf("freeMemory after gc: %,d bytes%n", rt.freeMemory());

        // --- System.exit() note ---
        System.out.println("\n-- System.exit() --");
        System.out.println("System.exit(0) terminates JVM with status code 0");
        System.out.println("Avoid in library code — only appropriate in main/CLI entrypoints");
        // System.exit(0);  // commented out — would terminate the demo
    }
}
