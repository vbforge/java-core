package com.vbforge.jvm_diagnostic_tools;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * jps and jcmd — find the JVM first, then ask it questions.
 *
 * <p>Every JDK diagnostic tool needs one thing before it can do anything:
 * the <b>process id</b> of the target JVM. {@code jps} finds it, {@code jcmd}
 * is the single front door that replaces most of the older one-purpose tools
 * ({@code jstack}, {@code jmap}, {@code jinfo}).
 *
 * <p>The tools themselves are command-line programs, so this demo shows the
 * same information through the JDK's in-process management API
 * ({@code java.lang.management}) and shows how to read the text the tools print.
 */
public class JpsAndJcmdDemo {

    /** Typical output of {@code jps -l} (note that jps lists itself). */
    public static final String SAMPLE_JPS_OUTPUT =
            "4711 com.example.OrderService\n"
          + "5120 org.gradle.launcher.daemon.bootstrap.GradleDaemon\n"
          + "6003 sun.tools.jps.Jps\n";

    /** What you want to do → the {@code jcmd <pid> <command>} that does it. */
    public static final Map<String, String> JCMD_CHEAT_SHEET;

    static {
        Map<String, String> m = new LinkedHashMap<>();
        m.put("help", "list every diagnostic command this particular JVM supports");
        m.put("VM.flags", "JVM flags in effect (-Xmx, chosen GC, ...)");
        m.put("VM.system_properties", "System.getProperties() of the target JVM");
        m.put("VM.version", "exact JVM build");
        m.put("VM.uptime", "how long the JVM has been running");
        m.put("Thread.print", "thread dump (what jstack prints)");
        m.put("GC.class_histogram", "instances and bytes per class (what jmap -histo prints)");
        m.put("GC.heap_dump", "write an .hprof heap dump (what jmap -dump does)");
        m.put("GC.heap_info", "heap regions and usage (replaces jmap -heap)");
        m.put("GC.run", "request a full GC, like System.gc()");
        m.put("JFR.start", "start a Flight Recorder recording");
        JCMD_CHEAT_SHEET = Collections.unmodifiableMap(m);
    }

    public static void main(String[] args) {
        pidDemo();
        System.out.println("---");
        jvmArgumentsDemo();
        System.out.println("---");
        jpsOutputDemo();
        System.out.println("---");
        jcmdDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Finding the pid (what jps lists)
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   jps prints "<pid> <main class>" for every JVM of the current user.
    //         Inside a JVM the same pid is available from RuntimeMXBean, whose
    //         name has the form "<pid>@<hostname>".
    // Why:    every other tool takes the pid as its argument. Wrong pid = you
    //         dump the wrong application.
    // Cost:   O(1) — a read of a cached string.
    // Gotcha: the "pid@host" format is a HotSpot convention, not a Java spec
    //         guarantee. Java 9+ offers ProcessHandle.current().pid(); this
    //         module stays Java 8-compatible, so parsePid() returns -1 rather
    //         than throwing when the format is unexpected.

    /** Extracts the pid from a {@code RuntimeMXBean.getName()} value such as "4711@my-host". */
    public static long parsePid(String runtimeName) {
        int at = runtimeName.indexOf('@');
        if (at <= 0) {
            return -1;
        }
        try {
            return Long.parseLong(runtimeName.substring(0, at));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /** The pid of the JVM running this code — the number jps would print for it. */
    public static long currentPid() {
        return parsePid(ManagementFactory.getRuntimeMXBean().getName());
    }

    static void pidDemo() {
        System.out.println("RuntimeMXBean name : " + ManagementFactory.getRuntimeMXBean().getName());
        System.out.println("pid                : " + currentPid());
        System.out.println("try in a terminal  : jps -l   (look for pid " + currentPid() + ")");
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. JVM arguments and uptime (jps -v, jcmd VM.flags, jcmd VM.uptime)
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   the flags a JVM was started with, and how long it has been up.
    // Why:    "why is the heap only 256 MB?" is answered by -Xmx (or its
    //         absence); "did it restart overnight?" is answered by uptime.
    //         Always check these BEFORE theorising about a problem.
    // Gotcha: getInputArguments() returns only what was typed on the command
    //         line. Flags the JVM chose itself (ergonomics: default heap size,
    //         default GC) appear only in jcmd <pid> VM.flags.

    /** Flags exactly as typed on the java command line (what jps -v shows). */
    public static List<String> jvmInputArguments() {
        return ManagementFactory.getRuntimeMXBean().getInputArguments();
    }

    /** True if any argument starts with the prefix, e.g. "-Xmx". */
    public static boolean hasInputArgument(List<String> arguments, String prefix) {
        for (String argument : arguments) {
            if (argument.startsWith(prefix)) {
                return true;
            }
        }
        return false;
    }

    /** Milliseconds since this JVM started (what jcmd VM.uptime reports, in seconds). */
    public static long uptimeMillis() {
        return ManagementFactory.getRuntimeMXBean().getUptime();
    }

    /** Wall-clock start time of this JVM, in epoch milliseconds. */
    public static long startTimeMillis() {
        return ManagementFactory.getRuntimeMXBean().getStartTime();
    }

    static void jvmArgumentsDemo() {
        System.out.println("input arguments : " + jvmInputArguments());
        System.out.println("has -Xmx        : " + hasInputArgument(jvmInputArguments(), "-Xmx"));
        System.out.println("uptime (ms)     : " + uptimeMillis());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Reading jps output
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   jps output is plain text, one JVM per line: "<pid> <name>".
    //         Scripts parse it to pick a target automatically.
    // Why:    in CI or on a server you cannot eyeball the list — you filter it.
    // Cost:   O(n) in the number of lines.
    // Gotcha: jps lists ITSELF ("Jps" or "sun.tools.jps.Jps") — filter it out
    //         or you will "diagnose" a process that is already gone. A line can
    //         also be just a pid when the name is unavailable.

    /** Splits one jps line into {pid, name}; the name is "" when jps printed only a pid. */
    public static String[] parseJpsLine(String line) {
        String trimmed = line.trim();
        int space = trimmed.indexOf(' ');
        if (space < 0) {
            return new String[]{trimmed, ""};
        }
        return new String[]{trimmed.substring(0, space), trimmed.substring(space + 1).trim()};
    }

    static void jpsOutputDemo() {
        for (String line : SAMPLE_JPS_OUTPUT.split("\n")) {
            String[] parts = parseJpsLine(line);
            System.out.println("pid=" + parts[0] + "  name=" + parts[1]);
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. jcmd — one tool, many commands
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   jcmd <pid> <command> sends a diagnostic command to a running JVM.
    //         "jcmd <pid> help" lists what that JVM supports.
    // Why:    jstack, jmap and jinfo each cover a slice; jcmd covers them all
    //         with one syntax and is the tool the JDK documentation recommends.
    // Gotcha: jcmd talks to the JVM through the attach mechanism, so it must
    //         run as the SAME OS user as the target JVM (or as root).
    //         Command names are case-sensitive: Thread.print, not thread.print.

    /** Same data as "jcmd &lt;pid&gt; VM.system_properties", read in-process. */
    public static String jvmSystemProperty(String key) {
        RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        return runtime.getSystemProperties().get(key);
    }

    static void jcmdDemo() {
        for (Map.Entry<String, String> entry : JCMD_CHEAT_SHEET.entrySet()) {
            System.out.println("jcmd <pid> " + entry.getKey() + "  →  " + entry.getValue());
        }
        System.out.println("java.version via MXBean: " + jvmSystemProperty("java.version"));
    }
}
