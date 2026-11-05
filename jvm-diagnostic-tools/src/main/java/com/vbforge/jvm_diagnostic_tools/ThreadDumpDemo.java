package com.vbforge.jvm_diagnostic_tools;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

/**
 * Thread dumps (jstack / jcmd Thread.print) — a photograph of every thread.
 *
 * <p>When an application "hangs", a thread dump answers the first question:
 * <i>what is every thread doing right now?</i> Command line:
 * <pre>
 *   jstack &lt;pid&gt;                 (older, still everywhere)
 *   jcmd &lt;pid&gt; Thread.print      (preferred)
 *   kill -3 &lt;pid&gt;                (Unix: dump goes to the JVM's own stdout)
 * </pre>
 * The same data is available in-process through {@link ThreadMXBean}.
 */
public class ThreadDumpDemo {

    /**
     * A trimmed but realistic jstack dump. Note that the JVM's own threads
     * (GC, compiler) have a header but no {@code Thread.State} line and no frames.
     */
    public static final String SAMPLE_DUMP =
            "\"main\" #1 prio=5 os_prio=0 tid=0x00007f1c4800b800 nid=0x1a2b waiting on condition [0x00007f1c4f1ee000]\n"
          + "   java.lang.Thread.State: TIMED_WAITING (sleeping)\n"
          + "\tat java.lang.Thread.sleep(Native Method)\n"
          + "\tat com.example.App.main(App.java:12)\n"
          + "\n"
          + "\"worker-1\" #12 prio=5 os_prio=0 tid=0x00007f1c4800c000 nid=0x1a40 waiting for monitor entry [0x00007f1c3e5fb000]\n"
          + "   java.lang.Thread.State: BLOCKED (on object monitor)\n"
          + "\tat com.example.Account.withdraw(Account.java:30)\n"
          + "\t- waiting to lock <0x000000076ab62208> (a com.example.Account)\n"
          + "\tat com.example.Teller.run(Teller.java:18)\n"
          + "\n"
          + "\"worker-2\" #13 prio=5 os_prio=0 tid=0x00007f1c4800d000 nid=0x1a41 runnable [0x00007f1c3e4fa000]\n"
          + "   java.lang.Thread.State: RUNNABLE\n"
          + "\tat java.net.SocketInputStream.socketRead0(Native Method)\n"
          + "\tat com.example.Client.read(Client.java:55)\n"
          + "\t- locked <0x000000076ab62208> (a com.example.Account)\n"
          + "\n"
          + "\"worker-3\" #14 prio=5 os_prio=0 tid=0x00007f1c4800e000 nid=0x1a42 waiting for monitor entry [0x00007f1c3e3f9000]\n"
          + "   java.lang.Thread.State: BLOCKED (on object monitor)\n"
          + "\tat com.example.Account.deposit(Account.java:22)\n"
          + "\t- waiting to lock <0x000000076ab62208> (a com.example.Account)\n"
          + "\n"
          + "\"GC Thread#0\" os_prio=0 tid=0x00007f1c48042800 nid=0x1a2c runnable\n";

    public static void main(String[] args) throws InterruptedException {
        snapshotDemo();
        System.out.println("---");
        threadStatesDemo();
        System.out.println("---");
        readingJstackDemo();
        System.out.println("---");
        deadlockDemo();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 1. Taking a snapshot in-process
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   ThreadMXBean.dumpAllThreads() returns one ThreadInfo per live
    //         thread (state, stack, lock info); Thread.getAllStackTraces()
    //         is the simpler, less detailed variant.
    // Why:    health endpoints and watchdogs can capture a dump from INSIDE
    //         the process when attaching from outside is not allowed.
    // Cost:   O(threads x stack depth); it brings every thread to a safepoint,
    //         so on a JVM with thousands of threads a dump is a visible pause.
    // Gotcha: a dump is ONE instant. A single dump shows who is stuck only if
    //         the same thread has the same frame in a second dump a few
    //         seconds later — always take 3 dumps ~5 s apart.

    /** How many live threads are in each {@link Thread.State} right now. */
    public static Map<Thread.State, Integer> threadStateCounts() {
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        Map<Thread.State, Integer> counts = new EnumMap<>(Thread.State.class);
        for (ThreadInfo info : threads.dumpAllThreads(false, false)) {
            Integer previous = counts.get(info.getThreadState());
            counts.put(info.getThreadState(), previous == null ? 1 : previous + 1);
        }
        return counts;
    }

    /** True when the calling thread appears in {@link Thread#getAllStackTraces()}. */
    public static boolean currentThreadInSnapshot() {
        return Thread.getAllStackTraces().containsKey(Thread.currentThread());
    }

    /**
     * Name of the first element of {@code Thread.currentThread().getStackTrace()}.
     * On HotSpot that is {@code getStackTrace} itself, not your method — a
     * common off-by-one when logging "who called me".
     */
    public static String firstFrameMethodName() {
        return Thread.currentThread().getStackTrace()[0].getMethodName();
    }

    static void snapshotDemo() {
        System.out.println("thread states now : " + threadStateCounts());
        System.out.println("current in dump   : " + currentThreadInSnapshot());
        System.out.println("first frame is    : " + firstFrameMethodName());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 2. The six thread states, observed for real
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   NEW, RUNNABLE, BLOCKED, WAITING, TIMED_WAITING, TERMINATED.
    // Why:    the state tells you WHERE to look:
    //           BLOCKED       → fighting for a synchronized monitor (lock contention)
    //           WAITING       → wait()/join()/park() with no timeout (waiting for a signal)
    //           TIMED_WAITING → sleep()/wait(ms)/poll(timeout)
    //           RUNNABLE      → on CPU OR in a native call such as a socket read
    // Gotcha: a thread reading a socket shows RUNNABLE although it uses no CPU.
    //         "Many RUNNABLE threads" does not prove a CPU problem — check
    //         the top frame (see section 3).

    /**
     * Creates one thread in each of five states and reports what
     * {@link Thread#getState()} says. Keys: new, sleeping, waiting, blocked, finished.
     */
    public static Map<String, Thread.State> observeThreadStates() throws InterruptedException {
        Map<String, Thread.State> observed = new LinkedHashMap<>();
        final Object monitor = new Object();
        final Object contended = new Object();

        Thread idle = new Thread(() -> { }, "states-idle");
        observed.put("new", idle.getState());

        Thread sleeper = daemon("states-sleeper", () -> {
            try {
                Thread.sleep(60_000);
            } catch (InterruptedException ignored) {
                // interrupted at the end of the demo
            }
        });
        Thread waiter = daemon("states-waiter", () -> {
            synchronized (monitor) {
                try {
                    monitor.wait();
                } catch (InterruptedException ignored) {
                    // interrupted at the end of the demo
                }
            }
        });
        Thread blocker = daemon("states-blocker", () -> {
            synchronized (contended) {
                // nothing to do — entering the monitor is the point
            }
        });

        sleeper.start();
        waiter.start();
        awaitState(sleeper, Thread.State.TIMED_WAITING);
        awaitState(waiter, Thread.State.WAITING);
        observed.put("sleeping", sleeper.getState());
        observed.put("waiting", waiter.getState());

        synchronized (contended) {
            blocker.start();
            awaitState(blocker, Thread.State.BLOCKED);
            observed.put("blocked", blocker.getState());
        }

        blocker.join();
        observed.put("finished", blocker.getState());

        sleeper.interrupt();
        waiter.interrupt();
        sleeper.join();
        waiter.join();
        return observed;
    }

    private static Thread daemon(String name, Runnable body) {
        Thread thread = new Thread(body, name);
        thread.setDaemon(true);
        return thread;
    }

    private static void awaitState(Thread thread, Thread.State expected) throws InterruptedException {
        long deadline = System.currentTimeMillis() + 5_000;
        while (thread.getState() != expected) {
            if (System.currentTimeMillis() > deadline) {
                throw new IllegalStateException(thread.getName() + " never reached " + expected);
            }
            Thread.sleep(5);
        }
    }

    static void threadStatesDemo() throws InterruptedException {
        System.out.println("states: " + observeThreadStates());
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 3. Reading a jstack dump
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   each thread is one block separated by a blank line:
    //           "name" #id prio=.. tid=.. nid=0x.. <short status> [..]
    //              java.lang.Thread.State: <STATE> (detail)
    //              at <top frame>            ← where the thread is RIGHT NOW
    //              - waiting to lock <0x..>  ← the monitor it wants
    //              - locked <0x..>           ← a monitor it holds
    // Why:    the same <0x..> address in a "locked" line of one thread and a
    //         "waiting to lock" line of another IS the culprit. Name the
    //         lock owner, then read ITS top frame.
    // Gotcha: nid is HEX. Linux tools such as "top -H -p <pid>" print thread
    //         ids in DECIMAL — convert before matching a hot thread to its stack.

    /** Splits a dump into one string per thread (blocks that start with a quote). */
    public static List<String> splitBlocks(String dump) {
        List<String> blocks = new ArrayList<>();
        for (String block : dump.split("\n\n")) {
            String trimmed = block.trim();
            if (trimmed.startsWith("\"")) {
                blocks.add(trimmed);
            }
        }
        return blocks;
    }

    /** The state word of a block ("BLOCKED", "RUNNABLE", ...) or null for JVM-internal threads. */
    public static String stateOf(String block) {
        String marker = "java.lang.Thread.State:";
        for (String line : block.split("\n")) {
            String trimmed = line.trim();
            if (trimmed.startsWith(marker)) {
                String rest = trimmed.substring(marker.length()).trim();
                int space = rest.indexOf(' ');
                return space < 0 ? rest : rest.substring(0, space);
            }
        }
        return null;
    }

    static void readingJstackDemo() {
        for (String block : splitBlocks(SAMPLE_DUMP)) {
            String header = block.split("\n")[0];
            System.out.println(header.substring(0, header.indexOf('"', 1) + 1) + " → " + stateOf(block));
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // 4. Deadlock detection
    // ═══════════════════════════════════════════════════════════════════════
    //
    // What:   two threads each hold a lock the other needs; neither can ever proceed.
    // Why:    jstack and jcmd Thread.print append a "Found one Java-level
    //         deadlock" section on their own; ThreadMXBean.findDeadlockedThreads()
    //         gives you the same answer in code.
    // Cost:   the detection walks the wait-for graph — O(threads).
    // Gotcha: only monitor / java.util.concurrent lock cycles are detected.
    //         A thread waiting forever on a CountDownLatch that nobody will
    //         count down is just as stuck but is NOT reported as a deadlock.
    //         And a deadlocked thread cannot be killed — only a restart fixes it.

    /**
     * Starts two daemon threads that deadlock on purpose, then asks the JVM
     * which threads it considers deadlocked. Only threads whose name starts with
     * {@code prefix + "-"} are reported, sorted by name.
     */
    public static List<String> provokeAndDetectDeadlock(String prefix) throws InterruptedException {
        final Object lockA = new Object();
        final Object lockB = new Object();
        final CountDownLatch bothHoldFirstLock = new CountDownLatch(2);

        deadlockThread(prefix + "-1", lockA, lockB, bothHoldFirstLock).start();
        deadlockThread(prefix + "-2", lockB, lockA, bothHoldFirstLock).start();

        ThreadMXBean threads = ManagementFactory.getThreadMXBean();
        long deadline = System.currentTimeMillis() + 5_000;
        while (System.currentTimeMillis() < deadline) {
            long[] ids = threads.findDeadlockedThreads();
            if (ids != null) {
                List<String> names = new ArrayList<>();
                for (ThreadInfo info : threads.getThreadInfo(ids)) {
                    if (info != null && info.getThreadName().startsWith(prefix + "-")) {
                        names.add(info.getThreadName());
                    }
                }
                if (names.size() == 2) {
                    Collections.sort(names);
                    return names;
                }
            }
            Thread.sleep(20);
        }
        return Collections.emptyList();
    }

    private static Thread deadlockThread(String name, Object first, Object second, CountDownLatch latch) {
        return daemon(name, () -> {
            synchronized (first) {
                latch.countDown();
                try {
                    latch.await();
                } catch (InterruptedException e) {
                    return;
                }
                synchronized (second) {
                    // never reached
                }
            }
        });
    }

    static void deadlockDemo() throws InterruptedException {
        System.out.println("deadlocked threads: " + provokeAndDetectDeadlock("demo-deadlock"));
    }
}
