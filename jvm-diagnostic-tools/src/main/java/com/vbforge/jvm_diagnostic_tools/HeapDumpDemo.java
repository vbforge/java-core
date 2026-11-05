package com.vbforge.jvm_diagnostic_tools;

import com.sun.management.HotSpotDiagnosticMXBean;

import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Heap inspection (jmap / jcmd GC.*) — what is in memory and who holds it.
 *
 * <p>Three levels of detail, from cheap to expensive:
 * <pre>
 *   1. numbers      jcmd &lt;pid&gt; GC.heap_info            (used / committed / max)
 *   2. histogram    jcmd &lt;pid&gt; GC.class_histogram      (instances + bytes per class)
 *   3. heap dump    jcmd &lt;pid&gt; GC.heap_dump file.hprof (every object and reference)
 * </pre>
 * Always start with the cheapest level that can answer your question.
 */
public class HeapDumpDemo {

    /** Output shape of {@code jmap -histo} / {@code jcmd GC.class_histogram}. */
    public static final String SAMPLE_HISTOGRAM =
            " num     #instances         #bytes  class name\n"
          + "----------------------------------------------\n"
          + "   1:          1000        5000000  [B\n"
          + "   2:         20000        2400000  java.lang.String\n"
          + "   3:         10000         800000  [Ljava.lang.Object;\n"
          + "   4:          5000         160000  java.lang.Integer\n"
          + "   5:           100          80000  [[I\n"
          + "Total         36100        8440000\n";

    /** JVM type codes used in histogram names: "[B" is byte[], "[[I" is int[][]. */
    public static final Map<Character, String> JVM_TYPE_CODES;

    static {
        Map<Character, String> m = new LinkedHashMap<>();
        m.put('B', "byte");
        m.put('C', "char");
        m.put('D', "double");
        m.put('F', "float");
        m.put('I', "int");
        m.put('J', "long");
        m.put('S', "short");
        m.put('Z', "boolean");
        JVM_TYPE_CODES = Collections.unmodifiableMap(m);
    }

    /** JVM flags that make the JVM write a dump by itself at the moment of an OutOfMemoryError. */
    public static final List<String> OOM_DUMP_FLAGS = Collections.unmodifiableList(Arrays.asList(
            "-XX:+HeapDumpOnOutOfMemoryError",
            "-XX:HeapDumpPath=/var/dumps"));

    public static void main(String[] args) throws IOException {
        heapNumbersDemo();
        System.out.println("---");
        histogramDemo();
        System.out.println("---");
        heapDumpFileDemo();
        System.out.println("---");
        commandsDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Heap numbers
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   used = bytes occupied by objects (live AND not-yet-collected garbage);
    //         committed = bytes the OS has actually given the JVM;
    //         max = the -Xmx ceiling (-1 when undefined).
    // Why:    the first question is "is the heap actually full, or just busy?"
    // Cost:   O(1) — counters the JVM already keeps.
    // Gotcha: "used" right now says little: garbage counts too. A leak shows in
    //         the "used AFTER a full GC" trend — see LeakHuntingDemo.

    public static long heapUsedBytes() {
        Runtime runtime = Runtime.getRuntime();
        return runtime.totalMemory() - runtime.freeMemory();
    }

    public static long heapCommittedBytes() {
        return Runtime.getRuntime().totalMemory();
    }

    public static long heapMaxBytes() {
        return Runtime.getRuntime().maxMemory();
    }

    /** 1536 → "1.5 KB", 1048576 → "1.0 MB"; values below 1024 keep the "B" unit. */
    public static String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + " B";
        }
        String[] units = {"KB", "MB", "GB", "TB"};
        double value = bytes;
        int unit = -1;
        while (value >= 1024 && unit < units.length - 1) {
            value /= 1024;
            unit++;
        }
        return String.format(Locale.ROOT, "%.1f %s", value, units[unit]);
    }

    static void heapNumbersDemo() {
        System.out.println("used      : " + formatBytes(heapUsedBytes()));
        System.out.println("committed : " + formatBytes(heapCommittedBytes()));
        System.out.println("max       : " + formatBytes(heapMaxBytes()));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. The class histogram
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   one row per class: rank, instance count, SHALLOW bytes, class name.
    // Why:    answers "WHAT is filling the heap?" without a multi-GB dump file.
    //         Take two histograms minutes apart and compare — the class whose
    //         bytes only go up is your suspect.
    // Cost:   walks the whole heap and — unless you ask for all objects (jcmd -all / jmap -histo:all) — triggers a
    //         full GC first (live objects only), so expect a pause.
    // Gotcha: shallow size = the object's own fields only; a HashMap with a
    //         million entries is a few dozen bytes. The memory sits in
    //         "[B", "[C", "java.lang.String" and "HashMap$Node" — and a heap
    //         dump's "retained size" is what tells you who OWNS them.

    /** One parsed row of a class histogram. */
    public static final class HistogramRow {
        public final int rank;
        public final long instances;
        public final long bytes;
        public final String className;

        HistogramRow(int rank, long instances, long bytes, String className) {
            this.rank = rank;
            this.instances = instances;
            this.bytes = bytes;
            this.className = className;
        }
    }

    /** Parses one histogram line; returns null for the header, separator and Total lines. */
    public static HistogramRow parseHistogramRow(String line) {
        String[] tokens = line.trim().split("\\s+");
        if (tokens.length != 4 || !tokens[0].endsWith(":")) {
            return null;
        }
        try {
            return new HistogramRow(
                    Integer.parseInt(tokens[0].substring(0, tokens[0].length() - 1)),
                    Long.parseLong(tokens[1]),
                    Long.parseLong(tokens[2]),
                    tokens[3]);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static List<HistogramRow> parseHistogram(String histogram) {
        List<HistogramRow> rows = new ArrayList<>();
        for (String line : histogram.split("\n")) {
            HistogramRow row = parseHistogramRow(line);
            if (row != null) {
                rows.add(row);
            }
        }
        return rows;
    }

    static void histogramDemo() {
        for (HistogramRow row : parseHistogram(SAMPLE_HISTOGRAM)) {
            System.out.println(row.rank + ". " + row.className + "  " + row.instances + " objects, " + formatBytes(row.bytes));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Writing a heap dump from code
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   HotSpotDiagnosticMXBean.dumpHeap(file, live) writes an HPROF file
    //         — the same format as jcmd GC.heap_dump and -XX:+HeapDumpOnOutOfMemoryError.
    //         Open it in Eclipse MAT, VisualVM or IntelliJ.
    // Why:    lets an admin endpoint or a watchdog capture evidence at the
    //         exact moment something looks wrong.
    // Cost:   time and file size are proportional to the heap (roughly the
    //         size of the used heap on disk); the application is paused throughout.
    // Gotcha: (1) the target file must NOT exist — the call throws otherwise;
    //         (2) live=true forces a full GC first, so garbage is excluded
    //         but the pause is longer; (3) the dump contains EVERYTHING in
    //         memory — passwords, tokens, personal data. Treat it as a secret.
    //         (4) com.sun.management is HotSpot-specific, not part of the Java SE API.

    /** True when this JVM exposes {@link HotSpotDiagnosticMXBean}. */
    public static boolean isHeapDumpSupported() {
        try {
            return ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class) != null;
        } catch (Throwable notAvailable) {
            return false;
        }
    }

    /** Writes an HPROF heap dump to {@code file}, which must not exist yet. */
    public static void dumpHeap(Path file, boolean liveOnly) throws IOException {
        HotSpotDiagnosticMXBean diagnostics = ManagementFactory.getPlatformMXBean(HotSpotDiagnosticMXBean.class);
        diagnostics.dumpHeap(file.toString(), liveOnly);
    }

    /** First bytes of an HPROF file: the ASCII text "JAVA PROFILE". */
    public static String hprofMagic(Path file) throws IOException {
        byte[] head = new byte[12];
        try (java.io.InputStream in = Files.newInputStream(file)) {
            int read = in.read(head);
            return new String(head, 0, Math.max(read, 0), java.nio.charset.StandardCharsets.US_ASCII);
        }
    }

    static void heapDumpFileDemo() throws IOException {
        if (!isHeapDumpSupported()) {
            System.out.println("heap dump API not available on this JVM");
            return;
        }
        Path dir = Files.createTempDirectory("heapdump-demo");
        Path file = dir.resolve("demo.hprof");
        try {
            dumpHeap(file, true);
            System.out.println("dump size : " + formatBytes(Files.size(file)));
            System.out.println("file magic: " + hprofMagic(file));
        } finally {
            Files.deleteIfExists(file);
            Files.deleteIfExists(dir);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Command-line equivalents
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   the two ways to dump from outside, plus the "automatic" way.
    // Why:    in production you rarely get to run code inside the process;
    //         and the OOM that kills the JVM is the one you most want a dump of.
    // Gotcha: jmap -dump:live,format=b,file=... and jcmd GC.heap_dump are
    //         equivalent, but jcmd is the one the JDK documentation recommends.
    //         The path is resolved by the TARGET JVM, not by your shell —
    //         relative paths end up in the target's working directory.

    public static List<String> heapDumpCommands(long pid, String file) {
        return Arrays.asList(
                "jcmd " + pid + " GC.heap_dump " + file,
                "jmap -dump:live,format=b,file=" + file + " " + pid);
    }

    static void commandsDemo() {
        for (String command : heapDumpCommands(4711, "/tmp/app.hprof")) {
            System.out.println(command);
        }
        System.out.println("on OOM: " + String.join(" ", OOM_DUMP_FLAGS));
    }
}
