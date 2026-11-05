# jvm-diagnostic-tools

> **Phase 7 — JVM Internals** | Java 8+ | Package: `com.vbforge.jvm_diagnostic_tools` | Tags: `JVM`, `TOOLS`

## What's covered

| Topic | Key points |
|---|---|
| `jps` / `jcmd` | find the pid first; `jps -l`, `jcmd <pid> help`, `VM.flags`, `VM.uptime`; same OS user as the target; `RuntimeMXBean` / `ManagementFactory` give the same data in-process |
| `jstack` / thread dumps | `jcmd <pid> Thread.print`, `kill -3`; six `Thread.State` values; `BLOCKED` vs `WAITING`; hex `nid` vs decimal `top -H`; `findDeadlockedThreads` |
| `jmap` / heap dumps | `GC.heap_info` → `GC.class_histogram` → `GC.heap_dump`; shallow vs retained size; `-XX:+HeapDumpOnOutOfMemoryError`; `HotSpotDiagnosticMXBean.dumpHeap` |
| Finding a leak | used-**after-GC** trend (staircase vs sawtooth); two histograms → growing class; static caches, keys without `equals`/`hashCode`, lapsed listeners |
| JFR | `-XX:StartFlightRecording`, `jcmd JFR.start/dump/stop`, `jdk.ExecutionSample`, `jdk.OldObjectSample`, `jfr summary`; free since JDK 11 / OpenJDK 8u262+ |
| VisualVM / JMX | Monitor tab = `MemoryMXBean`, `ThreadMXBean`, `GarbageCollectorMXBean`; `service:jmx:rmi:///jndi/rmi://host:port/jmxrmi`; `jmxremote` flags and why they are dangerous |

## Files

```
src/
├── main/java/com/vbforge/jvm_diagnostic_tools/
│   ├── JpsAndJcmdDemo.java          ← pid, JVM flags, uptime, jps output, jcmd cheat sheet
│   ├── JpsAndJcmdTasks.java         ← 🎯 your tasks
│   ├── ThreadDumpDemo.java          ← snapshots, thread states, reading jstack text, deadlocks
│   ├── ThreadDumpTasks.java         ← 🎯 your tasks
│   ├── HeapDumpDemo.java            ← heap numbers, class histogram, dumpHeap, commands
│   ├── HeapDumpTasks.java           ← 🎯 your tasks
│   ├── LeakHuntingDemo.java         ← three classic leaks + measuring retention
│   ├── LeakHuntingTasks.java        ← 🎯 your tasks
│   ├── FlightRecorderDemo.java      ← JFR commands/events, JMX sampler, remote JMX flags
│   └── FlightRecorderTasks.java     ← 🎯 your tasks
└── test/java/com/vbforge/jvm_diagnostic_tools/
    ├── JpsAndJcmdDemoTest.java      ← 🧪 tests (green)
    ├── JpsAndJcmdTasksTest.java     ← 🧪 tests for your tasks
    ├── ThreadDumpDemoTest.java      ← 🧪 tests (green)
    ├── ThreadDumpTasksTest.java     ← 🧪 tests for your tasks
    ├── HeapDumpDemoTest.java        ← 🧪 tests (green)
    ├── HeapDumpTasksTest.java       ← 🧪 tests for your tasks
    ├── LeakHuntingDemoTest.java     ← 🧪 tests (green)
    ├── LeakHuntingTasksTest.java    ← 🧪 tests for your tasks
    ├── FlightRecorderDemoTest.java  ← 🧪 tests (green)
    └── FlightRecorderTasksTest.java ← 🧪 tests for your tasks
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `JpsAndJcmdTasks.parsePids` | read `jps -l` output, skip jps itself |
| 2 | `JpsAndJcmdTasks.findPidByMainClass` | pick a target JVM by name |
| 3 | `JpsAndJcmdTasks.jcmdCommand` | build a `jcmd <pid> <command> [args]` line |
| 4 | `JpsAndJcmdTasks.parseUptimeSeconds` | read `VM.uptime` output |
| 5 | `JpsAndJcmdTasks.maxHeapBytes` | `-Xmx` with `k`/`m`/`g`, last one wins |
| 6 | `ThreadDumpTasks.extractThreadName` | thread header → name |
| 7 | `ThreadDumpTasks.nidToDecimal` | hex `nid` → decimal for `top -H` |
| 8 | `ThreadDumpTasks.countByState` | threads per `Thread.State` |
| 9 | `ThreadDumpTasks.findBlockedThreads` | who is stuck on a monitor |
| 10 | `ThreadDumpTasks.topFrame` | what a thread is executing right now |
| 11 | `HeapDumpTasks.bytesByClass` | parse `jmap -histo` |
| 12 | `HeapDumpTasks.topClassesByBytes` | biggest classes first |
| 13 | `HeapDumpTasks.averageObjectSize` | bytes ÷ instances |
| 14 | `HeapDumpTasks.decodeClassName` | `[B` → `byte[]`, `[[I` → `int[][]` |
| 15 | `LeakHuntingTasks.isLeakSuspect` | staircase vs sawtooth after GC |
| 16 | `LeakHuntingTasks.growthByClass` | diff two histograms |
| 17 | `LeakHuntingTasks.biggestGrower` | name the leak suspect |
| 18 | `LeakHuntingTasks.bytesPerHour` | leak rate |
| 19 | `LeakHuntingTasks.hoursUntilFull` | time to `OutOfMemoryError` |
| 20 | `FlightRecorderTasks.jfrStartCommand` | `JFR.start` command line |
| 21 | `FlightRecorderTasks.jmxServiceUrl` | URL for VisualVM / JConsole |
| 22 | `FlightRecorderTasks.gcOverheadPercent` | share of time spent in GC |
| 23 | `FlightRecorderTasks.eventCounts` | parse `jfr summary` |

```bash
mvn test -Dtest=JpsAndJcmdTasksTest
mvn test -Dtest=ThreadDumpTasksTest
mvn test -Dtest=HeapDumpTasksTest
mvn test -Dtest=LeakHuntingTasksTest
mvn test -Dtest=FlightRecorderTasksTest
```

## Key things to remember

**1. Measure the heap after a GC, not before.**
```java
// ❌ "used" includes garbage that simply has not been collected yet
long used = rt.totalMemory() - rt.freeMemory();

// ✅ a leak is what is STILL used after a full GC — watch this number over time
System.gc();
long retained = rt.totalMemory() - rt.freeMemory();
// healthy = sawtooth that returns to the same floor; leak = staircase that never comes down
```

**2. A `RUNNABLE` thread is not necessarily using CPU.**
```
"worker-2" ... RUNNABLE
   at java.net.SocketInputStream.socketRead0(Native Method)   ← waiting for the network!
// ✅ read the TOP FRAME before blaming the CPU; and take 3 dumps ~5 s apart.
// One dump is one instant; the same frame in all three is a real stall.
```

**3. `nid` in a dump is hex; `top -H` prints decimal.**
```
// ❌ searching the dump for the decimal id from top -H finds nothing
// ✅ printf '%x\n' 6720   →   1a40   →   search the dump for  nid=0x1a40
```

**4. A histogram shows shallow sizes.**
```
// ❌ "HashMap is only 48 bytes, it cannot be the leak"
// ✅ the memory sits in [B, [C, String and HashMap$Node — a heap dump's
//    RETAINED size / path to GC roots tells you who owns them
```

**5. Keys without `equals`/`hashCode` leak silently.**
```java
// ❌ identity equality: "user-1" is a NEW key every time
if (!map.containsKey(new BadKey("user-1"))) map.put(new BadKey("user-1"), v);
// ✅ implement equals + hashCode (or use a record / String key) — map stays at 1 entry
```

**6. Heap dumps are heavy and sensitive.**
```
// ❌ jmap -dump on a 20 GB heap at peak traffic: long pause, huge file, passwords inside
// ✅ one dump, off-peak or from a replica; protect the file;
//    -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/dumps for the crash case
```

**7. `jcmd` must run as the same OS user, and paths belong to the target JVM.**
```
// ❌ sudo -u root jcmd 4711 GC.heap_dump dump.hprof   → "dump.hprof" lands in the TARGET's working dir
// ✅ run as the JVM's user and pass an absolute path: jcmd 4711 GC.heap_dump /var/dumps/app.hprof
```

**8. Remote JMX with `authenticate=false` is remote code execution.**
```
// ❌ -Dcom.sun.management.jmxremote.authenticate=false on a port reachable from the network
// ✅ only behind an SSH tunnel on a dev box; otherwise enable auth + TLS, or copy .jfr files out instead
```

**9. JFR licensing depends on the JDK build.**
```
// ✅ JDK 11+ and OpenJDK 8u262+: free, jcmd <pid> JFR.start just works
// ❌ older Oracle JDK 8: needs -XX:+UnlockCommercialFeatures and a licence
```

## Run

```bash
# demo + tasks
mvn test

# demo tests only
mvn test -Dtest='*DemoTest'

# task tests only (all @Nested classes are @Disabled until you remove it)
mvn test -Dtest='*TasksTest'

# run a demo
mvn exec:java -Dexec.mainClass="com.vbforge.jvm_diagnostic_tools.JpsAndJcmdDemo"
```

Replace the class name to run the other demos: `ThreadDumpDemo`, `HeapDumpDemo`, `LeakHuntingDemo`, `FlightRecorderDemo`.
Try the real tools against a running demo: start `mvn exec:java …ThreadDumpDemo` in one terminal, then `jps -l` and `jcmd <pid> Thread.print` in another.
