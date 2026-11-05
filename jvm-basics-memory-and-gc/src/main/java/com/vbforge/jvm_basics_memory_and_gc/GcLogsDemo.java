package com.vbforge.jvm_basics_memory_and_gc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Reading GC logs: enabling them, parsing the lines, and spotting trouble.
 */
public class GcLogsDemo {

    /**
     * A made-up but realistic Java 9+ unified-logging excerpt ({@code -Xlog:gc}). The live set
     * after each collection goes 4, 9, 21, 18, 22 MB — a healthy saw-tooth, not a leak.
     */
    public static final List<String> SAMPLE_LOG = Collections.unmodifiableList(Arrays.asList(
            "[0.512s][info][gc] Using G1",
            "[0.730s][info][gc] GC(0) Pause Young (Normal) (G1 Evacuation Pause) 24M->4M(256M) 3.456ms",
            "[1.940s][info][gc] GC(1) Pause Young (Normal) (G1 Evacuation Pause) 70M->9M(256M) 5.120ms",
            "[3.050s][info][gc] GC(2) Pause Young (Normal) (G1 Evacuation Pause) 95M->21M(256M) 6.002ms",
            "[4.400s][info][gc] GC(3) Pause Full (G1 Compaction Pause) 250M->18M(256M) 612.345ms",
            "[5.100s][info][gc] GC(4) Pause Young (Normal) (G1 Evacuation Pause) 80M->22M(256M) 7.250ms"));

    public static void main(String[] args) {
        enablingLogsDemo();
        System.out.println("---");
        parsingDemo();
        System.out.println("---");
        legacyFormatDemo();
        System.out.println("---");
        summaryDemo();
        System.out.println("---");
        warningSignsDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Turning GC logging on
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: GC logging is cheap enough to leave on in production.
     *   Java 9+ (unified logging):  -Xlog:gc*:file=gc.log:time,uptime,level,tags
     *   Java 8:                     -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log
     *
     * Why it matters: a GC log is the only record of what the collector did when an
     *       incident happened. Without it you are guessing.
     *
     * Common gotcha: Java 8 flags are rejected (or ignored) on newer JVMs, and
     *       -Xlog is unknown on Java 8 — the flags depend on the runtime version.
     */
    public static void enablingLogsDemo() {
        System.out.println("Java 8  : " + loggingFlags(8));
        System.out.println("Java 17 : " + loggingFlags(17));
    }

    /** Returns the logging flags appropriate for a runtime feature version (8, 11, 17, 21 …). */
    public static String loggingFlags(int javaFeatureVersion) {
        return javaFeatureVersion >= 9
                ? "-Xlog:gc*:file=gc.log:time,uptime,level,tags"
                : "-XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log";
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Anatomy of a unified GC line
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: [uptime][level][tags] GC(id) <what happened> <before>-><after>(<committed>) <duration>
     *   e.g. GC(3) Pause Full (G1 Compaction Pause) 250M->18M(256M) 612.345ms
     *       - "Pause Young" = young collection; "Pause Full" = stop-the-world full GC
     *       - parenthesised text = the reason / phase
     *       - 250M->18M = heap used before -> after; (256M) = committed heap
     *       - last number = pause duration
     *
     * Why it matters: before->after is the live set the collector could not free;
     *       the duration is what your users felt.
     *
     * Complexity: parsing one line is O(line length).
     *
     * Common gotcha: units vary line to line (K, M, G) — normalise before comparing.
     */
    private static final Pattern UNIFIED = Pattern.compile(
            "^\\[(\\d+\\.\\d+)s]\\[info]\\[gc\\s*] GC\\((\\d+)\\) (.+?) "
                    + "(\\d+)([KMG])->(\\d+)([KMG])\\((\\d+)([KMG])\\) (\\d+(?:\\.\\d+)?)ms$");

    /** One parsed collection. Sizes are in kilobytes. */
    public static final class GcEvent {
        private final int id;
        private final String description;
        private final long beforeKb;
        private final long afterKb;
        private final long committedKb;
        private final double pauseMs;

        GcEvent(int id, String description, long beforeKb, long afterKb, long committedKb, double pauseMs) {
            this.id = id;
            this.description = description;
            this.beforeKb = beforeKb;
            this.afterKb = afterKb;
            this.committedKb = committedKb;
            this.pauseMs = pauseMs;
        }

        public int id() {
            return id;
        }

        public String description() {
            return description;
        }

        public long beforeKb() {
            return beforeKb;
        }

        public long afterKb() {
            return afterKb;
        }

        public long committedKb() {
            return committedKb;
        }

        public double pauseMs() {
            return pauseMs;
        }

        public long reclaimedKb() {
            return beforeKb - afterKb;
        }

        public boolean isFull() {
            return description.startsWith("Pause Full");
        }
    }

    public static void parsingDemo() {
        for (String line : SAMPLE_LOG) {
            Optional<GcEvent> event = parseUnified(line);
            if (event.isPresent()) {
                GcEvent e = event.get();
                System.out.println("GC(" + e.id() + ") " + e.description()
                        + " reclaimed " + e.reclaimedKb() / 1024 + " MB in " + e.pauseMs() + " ms");
            } else {
                System.out.println("(not a collection line) " + line);
            }
        }
    }

    /** Parses one {@code -Xlog:gc} line; empty when the line is not a collection summary. */
    public static Optional<GcEvent> parseUnified(String line) {
        Matcher m = UNIFIED.matcher(line);
        if (!m.matches()) {
            return Optional.empty();
        }
        return Optional.of(new GcEvent(
                Integer.parseInt(m.group(2)),
                m.group(3),
                toKb(Long.parseLong(m.group(4)), m.group(5).charAt(0)),
                toKb(Long.parseLong(m.group(6)), m.group(7).charAt(0)),
                toKb(Long.parseLong(m.group(8)), m.group(9).charAt(0)),
                Double.parseDouble(m.group(10))));
    }

    /** Converts a size with unit {@code K}, {@code M} or {@code G} to kilobytes. */
    public static long toKb(long value, char unit) {
        switch (unit) {
            case 'K':
                return value;
            case 'M':
                return value * 1024;
            case 'G':
                return value * 1024 * 1024;
            default:
                throw new IllegalArgumentException("unknown unit: " + unit);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. The legacy (Java 8, -verbose:gc) format
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: [GC (Allocation Failure)  33280K->4096K(125952K), 0.0050000 secs]
     *       The same facts, different shape: the cause is in parentheses, the duration
     *       is in SECONDS and sizes are in KB.
     *
     * Why it matters: you will meet both formats in real systems.
     *
     * Common gotcha: seconds vs milliseconds — a 100x misread is easy.
     */
    private static final Pattern LEGACY = Pattern.compile(
            "^\\[(Full GC|GC) \\(([^)]*)\\)\\s+(\\d+)K->(\\d+)K\\((\\d+)K\\), (\\d+\\.\\d+) secs]$");

    public static void legacyFormatDemo() {
        String line = "[Full GC (Ergonomics)  98304K->40960K(125952K), 0.2500000 secs]";
        Optional<GcEvent> event = parseLegacy(line);
        System.out.println(line);
        System.out.println("-> full=" + event.get().isFull() + ", pause=" + event.get().pauseMs() + " ms");
    }

    /** Parses a Java 8 {@code -verbose:gc} line. */
    public static Optional<GcEvent> parseLegacy(String line) {
        Matcher m = LEGACY.matcher(line);
        if (!m.matches()) {
            return Optional.empty();
        }
        String kind = "Full GC".equals(m.group(1)) ? "Pause Full" : "Pause Young";
        return Optional.of(new GcEvent(
                -1,
                kind + " (" + m.group(2) + ")",
                Long.parseLong(m.group(3)),
                Long.parseLong(m.group(4)),
                Long.parseLong(m.group(5)),
                Double.parseDouble(m.group(6)) * 1000.0));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Summarising a log
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: reduce a log to the numbers you act on — number of collections, number of
     *       full GCs, total and maximum pause.
     *
     * Why it matters: 1 000 lines are unreadable; four numbers tell you whether to dig.
     *
     * Complexity: O(lines).
     */
    public static void summaryDemo() {
        List<GcEvent> events = parseAll(SAMPLE_LOG);
        System.out.println("collections : " + events.size());
        System.out.println("full GCs    : " + countFull(events));
        System.out.println("total pause : " + totalPauseMs(events) + " ms");
        System.out.println("max pause   : " + maxPauseMs(events) + " ms");
    }

    public static List<GcEvent> parseAll(List<String> lines) {
        List<GcEvent> events = new ArrayList<>();
        for (String line : lines) {
            parseUnified(line).ifPresent(events::add);
        }
        return events;
    }

    public static long countFull(List<GcEvent> events) {
        long count = 0;
        for (GcEvent event : events) {
            if (event.isFull()) {
                count++;
            }
        }
        return count;
    }

    public static double totalPauseMs(List<GcEvent> events) {
        double total = 0;
        for (GcEvent event : events) {
            total += event.pauseMs();
        }
        return total;
    }

    public static double maxPauseMs(List<GcEvent> events) {
        double max = 0;
        for (GcEvent event : events) {
            max = Math.max(max, event.pauseMs());
        }
        return max;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 5. Warning signs
    // ═══════════════════════════════════════════════════════════════════════

    /*
     * What: patterns that mean trouble.
     *   - the "after" value climbs after every collection → live set grows → leak
     *     (a healthy app shows a saw-tooth that returns to the same floor)
     *   - repeated Pause Full where after ≈ committed → heap too small or leak
     *   - pauses far above the pause goal → tune the goal or the heap
     *
     * Why it matters: these are the first questions to ask of any GC log.
     *
     * Common gotcha: one rising value is not a leak — look for a trend across
     *       several collections of the same kind.
     */
    public static void warningSignsDemo() {
        List<GcEvent> events = parseAll(SAMPLE_LOG);
        System.out.println("live set after each GC (KB): " + afterValues(events));
        System.out.println("after-GC floor keeps rising  : " + floorKeepsRising(events));
    }

    public static List<Long> afterValues(List<GcEvent> events) {
        List<Long> values = new ArrayList<>();
        for (GcEvent event : events) {
            values.add(event.afterKb());
        }
        return values;
    }

    /** True when there are at least 3 events and every "after" value is larger than the previous one. */
    public static boolean floorKeepsRising(List<GcEvent> events) {
        if (events.size() < 3) {
            return false;
        }
        for (int i = 1; i < events.size(); i++) {
            if (events.get(i).afterKb() <= events.get(i - 1).afterKb()) {
                return false;
            }
        }
        return true;
    }
}
