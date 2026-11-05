package com.vbforge.jvm_diagnostic_tools;

import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * JFR and VisualVM — continuous recording versus live monitoring.
 *
 * <pre>
 *   Java Flight Recorder (JFR)  records events inside the JVM to a .jfr file.
 *                               Built for production: low overhead, always-on friendly.
 *                               Answer "what happened between 02:00 and 02:10?" AFTER the fact.
 *   VisualVM                    live GUI that attaches to a running JVM (local attach or JMX).
 *                               Answer "what is happening right now?" — but it is
 *                               not shipped with the JDK since Java 9; download it separately.
 * </pre>
 * Both are fed by the same sources: the JVM's own counters ({@code java.lang.management}).
 */
public class FlightRecorderDemo {

    /** The JFR event types you will use most, with the question each one answers. */
    public static final Map<String, String> JFR_EVENTS;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("jdk.ExecutionSample", "where is CPU time going? (sampled stack traces)");
        m.put("jdk.GarbageCollection", "when did each GC run and how long did it take?");
        m.put("jdk.CPULoad", "JVM and machine CPU usage over time");
        m.put("jdk.JavaMonitorEnter", "which synchronized blocks made threads wait?");
        m.put("jdk.OldObjectSample", "which long-lived objects are leak candidates, and what holds them?");
        JFR_EVENTS = Collections.unmodifiableMap(m);
    }

    /** One reading of the counters VisualVM's "Monitor" tab charts. */
    public static final class MonitorSample {
        public final long heapUsedBytes;
        public final int liveThreads;
        public final int loadedClasses;
        public final long gcCount;
        public final long gcTimeMillis;

        MonitorSample(long heapUsedBytes, int liveThreads, int loadedClasses, long gcCount, long gcTimeMillis) {
            this.heapUsedBytes = heapUsedBytes;
            this.liveThreads = liveThreads;
            this.loadedClasses = loadedClasses;
            this.gcCount = gcCount;
            this.gcTimeMillis = gcTimeMillis;
        }
    }

    public static void main(String[] args) {
        jfrCommandsDemo();
        System.out.println("---");
        jfrEventsDemo();
        System.out.println("---");
        monitorSampleDemo();
        System.out.println("---");
        remoteJmxDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Controlling JFR
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   a recording can be started at JVM launch or on a running JVM,
    //         then stopped / dumped to a file you open in JDK Mission Control
    //         or read with the "jfr" command-line tool.
    // Why:    unlike a heap dump, a recording shows behaviour OVER TIME — allocation
    //         rates, lock contention, GC pauses — not just one instant.
    // Cost:   designed to be cheap enough for production. The "default" settings
    //         profile records less than "profile" (more detail, more overhead).
    // Gotcha: JFR is open source and free to use since JDK 11 (and OpenJDK 8u262+).
    //         On older Oracle JDK 8 builds it needs -XX:+UnlockCommercialFeatures
    //         and a licence. Check "java -version" before promising it to a customer.

    /** Flag that starts a recording at JVM launch. */
    public static String startupFlag(int durationSeconds, String fileName) {
        return "-XX:StartFlightRecording=duration=" + durationSeconds + "s,filename=" + fileName;
    }

    /** Stops a recording and writes it to a file. */
    public static String jfrStopCommand(long pid, String recordingName, String fileName) {
        return "jcmd " + pid + " JFR.stop name=" + recordingName + " filename=" + fileName;
    }

    /** Writes what has been recorded so far WITHOUT stopping the recording. */
    public static String jfrDumpCommand(long pid, String recordingName, String fileName) {
        return "jcmd " + pid + " JFR.dump name=" + recordingName + " filename=" + fileName;
    }

    static void jfrCommandsDemo() {
        System.out.println(startupFlag(60, "app.jfr"));
        System.out.println(jfrDumpCommand(4711, "leak", "leak.jfr"));
        System.out.println(jfrStopCommand(4711, "leak", "leak.jfr"));
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. Which events to look at
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   JFR records hundreds of event types; a handful answer most questions.
    // Why:    opening a recording without a question is a way to lose an afternoon.
    //         Pick the symptom first: slow → ExecutionSample, pauses →
    //         GarbageCollection, contention → JavaMonitorEnter, growth → OldObjectSample.
    // Gotcha: jdk.OldObjectSample records the allocation site of long-lived
    //         objects — it is JFR's leak finder — but the path to GC roots is
    //         only included if the recording was started with path-to-gc-roots=true
    //         (which is costlier).

    public static String describeEvent(String eventName) {
        String description = JFR_EVENTS.get(eventName);
        return description != null ? description : "not in the short list — run: jfr summary <file>";
    }

    static void jfrEventsDemo() {
        for (String event : JFR_EVENTS.keySet()) {
            System.out.println(event + "  →  " + describeEvent(event));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. What VisualVM's Monitor tab reads (JMX beans)
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   heap, threads, loaded classes and GC counters from the platform MXBeans.
    // Why:    these are the cheapest signals available: sample them every few
    //         seconds in any environment and you already see leaks (heap
    //         staircase), thread leaks (threads only climb) and classloader
    //         leaks (loaded classes only climb).
    // Cost:   O(1) per sample.
    // Gotcha: GarbageCollectorMXBean.getCollectionCount() returns -1 when a
    //         collector does not report it; summing without a check
    //         silently corrupts the total.

    public static MonitorSample sample() {
        Runtime runtime = Runtime.getRuntime();
        long gcCount = 0;
        long gcTime = 0;
        for (GarbageCollectorMXBean gc : ManagementFactory.getGarbageCollectorMXBeans()) {
            if (gc.getCollectionCount() >= 0) {
                gcCount += gc.getCollectionCount();
            }
            if (gc.getCollectionTime() >= 0) {
                gcTime += gc.getCollectionTime();
            }
        }
        return new MonitorSample(
                runtime.totalMemory() - runtime.freeMemory(),
                ManagementFactory.getThreadMXBean().getThreadCount(),
                ManagementFactory.getClassLoadingMXBean().getLoadedClassCount(),
                gcCount,
                gcTime);
    }

    static void monitorSampleDemo() {
        MonitorSample s = sample();
        System.out.println("heap used   : " + s.heapUsedBytes);
        System.out.println("threads     : " + s.liveThreads);
        System.out.println("classes     : " + s.loadedClasses);
        System.out.println("gc runs/ms  : " + s.gcCount + " / " + s.gcTimeMillis);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Attaching VisualVM to a remote JVM
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   a local JVM of the same OS user is attached automatically; for a
    //         remote one the JVM must expose a JMX port.
    // Why:    containers and servers are remote by definition.
    // Gotcha: these flags turn authentication and TLS OFF. That is acceptable
    //         for a port reachable only through an SSH tunnel on a dev box and
    //         dangerous anywhere else — JMX access is remote code execution.
    //         In production enable authentication + TLS, or prefer JFR files
    //         copied out of the machine.

    public static List<String> jmxRemoteFlags(int port) {
        return Arrays.asList(
                "-Dcom.sun.management.jmxremote",
                "-Dcom.sun.management.jmxremote.port=" + port,
                "-Dcom.sun.management.jmxremote.authenticate=false",
                "-Dcom.sun.management.jmxremote.ssl=false");
    }

    static void remoteJmxDemo() {
        for (String flag : jmxRemoteFlags(9010)) {
            System.out.println(flag);
        }
    }
}
