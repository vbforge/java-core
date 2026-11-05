# jvm-basics-memory-and-gc

> **Phase 7 — JVM Internals** | Java 8+ | Package: `com.vbforge.jvm_basics_memory_and_gc` | Tags: `JVM`, `PERFORMANCE`

Stack vs Heap, object lifecycle, strong/soft/weak/phantom references, GC basics + G1/ZGC trade-offs, GC logs, OOM causes.

## What's covered

| Topic | Key points |
|---|---|
| Stack vs Heap | frames hold primitives + references; objects live on the heap; Java is always pass-by-value; `StackOverflowError` vs `OutOfMemoryError`; `Runtime.maxMemory/totalMemory/freeMemory`; one stack per thread, one shared heap |
| Object lifecycle | GC roots, reachability (not reference counting), cycles are garbage, mark-and-sweep, retained set, Eden → Survivor → Old, tenuring threshold, G1 humongous objects (≥ half a region) |
| Reference types | strong, `SoftReference` (memory-sensitive cache), `WeakReference` (read-once idiom), `PhantomReference` + `ReferenceQueue`, `WeakHashMap`, why `finalize()` is obsolete |
| GC basics + collectors | stop-the-world, throughput vs latency, GC overhead %, pause percentiles, Serial / Parallel / G1 / ZGC trade-offs, `-XX:+UseG1GC`, `-XX:MaxGCPauseMillis`, `GarbageCollectorMXBean` |
| GC logs | `-Xlog:gc*` (9+) vs `-XX:+PrintGCDetails` (8), anatomy of a log line, K/M/G units, seconds vs milliseconds, full-GC count, rising live set = leak |
| OOM causes | the six `OutOfMemoryError` messages, static-collection leak, mutated hash key, `ThreadLocal` in pools, bounded LRU cache, `-XX:+HeapDumpOnOutOfMemoryError` |

## Files

```
src/
├── main/java/com/vbforge/jvm_basics_memory_and_gc/
│   ├── StackHeapDemo.java            ← annotated reference: stack frames, heap, pass-by-value
│   ├── StackHeapTasks.java           ← 🎯 your tasks
│   ├── ObjectLifecycleDemo.java      ← annotated reference: reachability, mark-and-sweep, generations
│   ├── ObjectLifecycleTasks.java     ← 🎯 your tasks
│   ├── ReferencesDemo.java           ← annotated reference: strong/soft/weak/phantom, WeakHashMap
│   ├── ReferencesTasks.java          ← 🎯 your tasks
│   ├── GcCollectorsDemo.java         ← annotated reference: Serial/Parallel/G1/ZGC, pause metrics
│   ├── GcCollectorsTasks.java        ← 🎯 your tasks
│   ├── GcLogsDemo.java               ← annotated reference: enabling, parsing and reading GC logs
│   ├── GcLogsTasks.java              ← 🎯 your tasks
│   ├── OomCausesDemo.java            ← annotated reference: OOM families and leak patterns
│   └── OomCausesTasks.java           ← 🎯 your tasks
└── test/java/com/vbforge/jvm_basics_memory_and_gc/
    ├── StackHeapDemoTest.java        ← 🧪 tests (green)
    ├── StackHeapTasksTest.java       ← 🧪 tests for your tasks (@Disabled until you start)
    ├── ObjectLifecycleDemoTest.java  ← 🧪 tests (green)
    ├── ObjectLifecycleTasksTest.java ← 🧪 tests for your tasks (@Disabled until you start)
    ├── ReferencesDemoTest.java       ← 🧪 tests (green)
    ├── ReferencesTasksTest.java      ← 🧪 tests for your tasks (@Disabled until you start)
    ├── GcCollectorsDemoTest.java     ← 🧪 tests (green)
    ├── GcCollectorsTasksTest.java    ← 🧪 tests for your tasks (@Disabled until you start)
    ├── GcLogsDemoTest.java           ← 🧪 tests (green)
    ├── GcLogsTasksTest.java          ← 🧪 tests for your tasks (@Disabled until you start)
    ├── OomCausesDemoTest.java        ← 🧪 tests (green)
    └── OomCausesTasksTest.java       ← 🧪 tests for your tasks (@Disabled until you start)
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `StackHeapTasks.swapFirstTwoInPlace` | writing through a copied reference mutates the shared array |
| 2 | `StackHeapTasks.appendCopy` | arrays cannot grow — allocate a new one, leave the source alone |
| 3 | `StackHeapTasks.sumIteratively` | loop instead of recursion; `long` accumulator |
| 4 | `StackHeapTasks.overflowsStack` | catching `StackOverflowError` (an `Error`) and nothing else |
| 5 | `StackHeapTasks.heapHeadroomBytes` | `max - (total - free)` — free ≠ available |
| 6 | `ObjectLifecycleTasks.findGarbage` | everything not reachable from the roots |
| 7 | `ObjectLifecycleTasks.retainedSet` | what would be freed if one object disappeared |
| 8 | `ObjectLifecycleTasks.lifecycle` | Eden → Survivor → Old with a tenuring threshold |
| 9 | `ObjectLifecycleTasks.humongousRegionsNeeded` | G1 humongous threshold and region rounding |
| 10 | `ReferencesTasks.getOrDefault` | read a weak referent exactly once |
| 11 | `ReferencesTasks.countAlive` | cleared references return `null` |
| 12 | `ReferencesTasks.pruneCleared` | remove cleared references in place |
| 13 | `ReferencesTasks.drainQueue` | non-blocking `ReferenceQueue.poll()` loop |
| 14 | `ReferencesTasks.getOrLoad` | memory-sensitive cache with `SoftReference` |
| 15 | `GcCollectorsTasks.gcOverheadPercent` | share of wall-clock time spent paused |
| 16 | `GcCollectorsTasks.pausePercentile` | nearest-rank p50/p99 — the tail the mean hides |
| 17 | `GcCollectorsTasks.recommendCollector` | Serial / Parallel / G1 / ZGC selection rules |
| 18 | `GcCollectorsTasks.jvmFlags` | `-Xms -Xmx` + collector flag |
| 19 | `GcLogsTasks.reclaimedKb` | parse before→after with K/M/G units |
| 20 | `GcLogsTasks.pauseMs` | parse the pause duration |
| 21 | `GcLogsTasks.totalPauseMs` | sum pauses, skip non-collection lines |
| 22 | `GcLogsTasks.countFullGcs` | count `Pause Full` collections |
| 23 | `GcLogsTasks.heapAfterGcGrowing` | strictly rising live set = leak suspicion |
| 24 | `OomCausesTasks.diagnoseOom` | map an `OutOfMemoryError` message to its family |
| 25 | `OomCausesTasks.newLruCache` | bounded access-ordered cache |
| 26 | `OomCausesTasks.runWithThreadLocal` | `try`/`finally` + `ThreadLocal.remove()` |
| 27 | `OomCausesTasks.evictStale` | TTL eviction from a map, in place |

Every nested class in a `*TasksTest` ships with `@Disabled("Remove @Disabled when you implement <method>()")`.
Remove it from the nested class you want to work on, then:

```bash
mvn test -Dtest=StackHeapTasksTest
mvn test -Dtest=ObjectLifecycleTasksTest
mvn test -Dtest=ReferencesTasksTest
mvn test -Dtest=GcCollectorsTasksTest
mvn test -Dtest=GcLogsTasksTest
mvn test -Dtest=OomCausesTasksTest
```

## Key things to remember

```java
// ❌ "I reassigned the parameter but the caller's array didn't change"
static void reset(int[] a) { a = new int[]{0}; }   // re-points only the local COPY of the reference
// ✅ Java is always pass-by-value: you can mutate what the reference points to, not the caller's variable
static void reset(int[] a) { a[0] = 0; }
```

```java
// ❌ Deep recursion — no tail-call optimisation in Java, one stack frame per call
long sum(int n) { return n == 0 ? 0 : n + sum(n - 1); }   // StackOverflowError for large n
// ✅ A loop uses constant stack; StackOverflowError is thrown long before the heap is touched
long sum(int n) { long s = 0; for (int i = 1; i <= n; i++) s += i; return s; }
```

```java
// ❌ Reference counting thinking: "D and E point at each other, so they stay alive"
// ✅ The JVM traces from GC roots. A cycle nobody can reach is garbage,
//    and a reference FROM garbage does not keep its target alive.
```

```java
// ❌ Two reads of a weak reference — the GC can clear it between them
if (ref.get() != null) { ref.get().use(); }          // possible NullPointerException
// ✅ Read once into a local variable, then test the local
Foo foo = ref.get();
if (foo != null) { foo.use(); }
```

```java
// ❌ WeakHashMap with a literal / interned / cached key — never evicted
map.put("literal", value);                 // the JVM holds "literal" strongly forever
// ❌ A value that references its own key keeps the entry alive
// ✅ Use keys you own and drop the last strong reference elsewhere
```

```java
// ❌ finalize() — can resurrect objects, delays reclamation, deprecated for removal since Java 18
// ✅ PhantomReference + ReferenceQueue, or java.lang.ref.Cleaner for cleanup after collection
```

```java
// ❌ Judging GC health by the average pause: [5, 6, 5, 7, 480] looks "fine" at 100 ms
// ✅ Look at percentiles (p99, p99.9), GC overhead %, and the live set AFTER each collection
```

```java
// ❌ Mutating a field that feeds hashCode() while the object is a HashMap key
key.id = 2;                  // entry now sits in the wrong bucket
map.remove(key);             // no effect — a permanent leak
// ✅ Immutable keys (String, Integer, records of immutables)
```

```java
// ❌ ThreadLocal in a pool thread without cleanup — value outlives the task, leaks memory,
//    and the NEXT task on that thread can see it
ctx.set(user);  handle();
// ✅ Always clear in finally
try { ctx.set(user); handle(); } finally { ctx.remove(); }
```

```java
// ❌ Static collections that only grow are GC roots: "reachable but no longer needed" IS the Java memory leak
static final List<byte[]> CACHE = new ArrayList<>();
// ✅ Bound it (LRU via LinkedHashMap.removeEldestEntry) or expire entries
```

```text
# ❌ Raising -Xmx for every OutOfMemoryError — "Metaspace", "unable to create native thread" and
#    "Direct buffer memory" are NOT heap problems, and a real leak only crashes later.
# ✅ Read the message after "java.lang.OutOfMemoryError:" first, then capture evidence:
-XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=/var/dumps/app.hprof -XX:+ExitOnOutOfMemoryError
```

```text
# GC log flags depend on the Java version
# ❌ Java 8 flags on a modern JVM (-XX:+PrintGCDetails) — rejected / ignored
# ✅ Java 9+: -Xlog:gc*:file=gc.log:time,uptime,level,tags
#    Java 8 : -XX:+PrintGCDetails -XX:+PrintGCDateStamps -Xloggc:gc.log
# Also: legacy durations are in SECONDS, unified-log durations are in MILLISECONDS.
```

```text
# Collector choice is choosing which cost you can afford:
#   Parallel — max throughput, long stop-the-world pauses
#   G1       — balanced default; pause GOAL (-XX:MaxGCPauseMillis), not a guarantee
#   ZGC      — pauses stay tiny at any heap size; pays CPU and needs allocation headroom
#   (JDK 21: -XX:+UseZGC -XX:+ZGenerational; generational is the default from JDK 23)
```

## Run

```bash
# demo tests + task tests (task tests are skipped until you remove @Disabled)
mvn test

# only the demo tests (always green)
mvn test -Dtest='*DemoTest'

# only the task tests
mvn test -Dtest='*TasksTest'

# run an annotated demo (swap in any of the six Demo classes)
mvn exec:java -Dexec.mainClass="com.vbforge.jvm_basics_memory_and_gc.StackHeapDemo"
```

Demo classes: `StackHeapDemo`, `ObjectLifecycleDemo`, `ReferencesDemo`, `GcCollectorsDemo`, `GcLogsDemo`, `OomCausesDemo`.
