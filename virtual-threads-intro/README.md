# virtual-threads-intro

> **Phase 6 — Concurrency** | Java 21+ | Package: `com.vbforge.virtual_threads_intro` | Tags: `CONCURRENCY`, `MODERN-JAVA`

Project Loom / virtual threads basics, structured concurrency intro.

## What's covered

| Topic | Key points |
|-------|-----------|
| Creating virtual threads | `Thread.ofVirtual().name(..).start`, `Thread.startVirtualThread`, `unstarted`, `factory`, `name(prefix, start)` |
| Properties | `isVirtual`, always daemon (`setDaemon(false)` throws), fixed `NORM_PRIORITY`, empty default name, `"VirtualThreads"` group |
| One thread per task | `Executors.newVirtualThreadPerTaskExecutor`, `ExecutorService` is `AutoCloseable` (`close()` waits), never pool them |
| Massive concurrency | 10 000 threads blocked at once, blocking is cheap, CPU work is not faster |
| Limiting concurrency | `Semaphore` (`acquire` / `release` in `finally`), the limit belongs to the resource, not to a pool |
| Thread-locals | `ThreadLocal.withInitial`, per-thread copies, not inherited, `ScopedValue` is only a preview in 21 |
| Carrier pinning | `synchronized` + blocking pins the carrier on 21, `ReentrantLock` does not, `-Djdk.tracePinnedThreads=full` |
| Structured concurrency | scoped lifetime, `fork` / `joinAll`, fail fast, `Future.cancel(true)`, `Future.resultNow()`, `invokeAny`, `Future.get(timeout)` |

## Files

```
src/
├── main/java/com/vbforge/virtual_threads_intro/
│   ├── VirtualThreadBasicsDemo.java          ← creating, properties, executor per task, scale
│   ├── VirtualThreadBasicsTasks.java         ← 🎯 your tasks
│   ├── VirtualThreadPitfallsDemo.java        ← Semaphore limits, ThreadLocal, synchronized vs ReentrantLock
│   ├── VirtualThreadPitfallsTasks.java       ← 🎯 your tasks
│   ├── StructuredConcurrencyDemo.java        ← scoped lifetime, TaskScope, fail fast
│   └── StructuredConcurrencyTasks.java       ← 🎯 your tasks
└── test/java/com/vbforge/virtual_threads_intro/
    ├── VirtualThreadBasicsDemoTest.java      ← 🧪 tests (already green)
    ├── VirtualThreadBasicsTasksTest.java     ← 🧪 tests for your code (start @Disabled)
    ├── VirtualThreadPitfallsDemoTest.java    ← 🧪 tests (already green)
    ├── VirtualThreadPitfallsTasksTest.java   ← 🧪 tests for your code (start @Disabled)
    ├── StructuredConcurrencyDemoTest.java    ← 🧪 tests (already green)
    └── StructuredConcurrencyTasksTest.java   ← 🧪 tests for your code (start @Disabled)
```

## 🎯 Tasks

| # | Method | Topic |
|---|--------|-------|
| 1 | `VirtualThreadBasicsTasks.runOnVirtualThread` | Start a named virtual thread and report from inside it |
| 2 | `VirtualThreadBasicsTasks.squaresOnVirtualThreads` | One virtual thread per task, ordered results |
| 3 | `VirtualThreadBasicsTasks.countDistinctThreads` | Virtual threads are never reused |
| 4 | `VirtualThreadBasicsTasks.createUnstartedVirtual` | `unstarted` builder, thread state `NEW` |
| 5 | `VirtualThreadPitfallsTasks.maxObservedConcurrency` | Limit concurrency with a `Semaphore` |
| 6 | `VirtualThreadPitfallsTasks.perThreadCounts` | `ThreadLocal` gives every thread its own copy |
| 7 | `VirtualThreadPitfallsTasks.withdrawConcurrently` | Atomic check-then-act with a lock that is safe to block in |
| 8 | `StructuredConcurrencyTasks.combine` | Parallel fork/join with fail-fast cancellation |
| 9 | `StructuredConcurrencyTasks.firstSuccessful` | First success wins, losers are cancelled |
| 10 | `StructuredConcurrencyTasks.callWithTimeout` | Timeout that really cancels the task |

Every task test class starts with `@Disabled` on the nested class. Remove it when you implement that method:

```bash
mvn test -Dtest=VirtualThreadBasicsTasksTest
mvn test -Dtest=VirtualThreadPitfallsTasksTest
mvn test -Dtest=StructuredConcurrencyTasksTest
```

## Key things to remember

```java
// ✅ Always join — virtual threads are daemon threads
Thread t = Thread.ofVirtual().start(work);
t.join();                      // without this, the JVM may exit before `work` runs

// ❌ Fire and forget: main() can end while the virtual thread is still running
Thread.startVirtualThread(work);
```

```java
// ✅ One virtual thread per task; the try block owns the tasks (close() waits)
try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
    executor.submit(task);
}

// ❌ Pooling virtual threads — pooling exists for scarce resources, they are not scarce
ExecutorService pool = Executors.newFixedThreadPool(200, Thread.ofVirtual().factory());
```

```java
// ✅ Limit the RESOURCE, not the threads
semaphore.acquire();
try { callDatabase(); } finally { semaphore.release(); }  // finally: a leaked permit is a permanent leak

// ❌ "Fewer threads = fewer DB calls" no longer holds: 100_000 tasks means 100_000 threads
```

```java
// ✅ ReentrantLock around code that blocks (sleep, I/O, remote call)
lock.lock();
try { callSlowService(); } finally { lock.unlock(); }

// ⚠️ On Java 21, blocking inside synchronized pins the carrier thread. Correct, but it
//    limits scalability. Diagnose with: -Djdk.tracePinnedThreads=full
synchronized (monitor) { callSlowService(); }
```

```java
// ✅ Keep ThreadLocal values small — there may now be hundreds of thousands of threads
private static final ThreadLocal<String> USER = ThreadLocal.withInitial(() -> "anonymous");

// ❌ A big per-thread cache: one copy PER virtual thread, and virtual threads are never reused
private static final ThreadLocal<byte[]> BUFFER = ThreadLocal.withInitial(() -> new byte[1_000_000]);
```

```java
// ✅ Cancellation = interruption. Let blocking calls throw InterruptedException and stop
try { Thread.sleep(60_000); }
catch (InterruptedException e) { cleanUp(); throw e; }

// ❌ Swallowing the interrupt: the task can never be cancelled
try { Thread.sleep(60_000); } catch (InterruptedException ignored) { }
```

```java
// ✅ Notice a failure immediately: take results in COMPLETION order
//    (TaskScope.joinAll, ExecutorService.invokeAny)

// ❌ future1.get(); future2.get();  → if future1 blocks forever, a failure in future2 is never seen
```

```java
// ⚠️ cancel(true) only REQUESTS an interrupt. On JDK 21, ExecutorService.close() can return
//    before a task cancelled through a submit() future has reacted to it. TaskScope uses
//    FutureTask + execute() so that close() reliably waits for cancelled subtasks.
```

**About `StructuredTaskScope`:** Java 21 ships it as a *preview* API (JEP 453). It needs `--enable-preview`, and its shape changed in later releases. This module therefore teaches the structured-concurrency idea with final APIs and a small hand-written `TaskScope`. Once the API is final, the concepts carry over directly: `fork`, `join`, fail-fast, scope-bound lifetime.

**Not covered here:** virtual threads are for *blocking* work (I/O, waiting). They do not make CPU-bound code faster; for that, parallelism is still limited by the number of cores.

## Run

```bash
# demo + tasks
mvn test

# only the demo tests
mvn test -Dtest='VirtualThreadBasicsDemoTest,VirtualThreadPitfallsDemoTest,StructuredConcurrencyDemoTest'

# only your task tests
mvn test -Dtest='VirtualThreadBasicsTasksTest,VirtualThreadPitfallsTasksTest,StructuredConcurrencyTasksTest'

# run a demo with output
mvn exec:java -Dexec.mainClass="com.vbforge.virtual_threads_intro.VirtualThreadBasicsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.virtual_threads_intro.VirtualThreadPitfallsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.virtual_threads_intro.StructuredConcurrencyDemo"
```
