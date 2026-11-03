# multithreading-basics

> **Phase 6 — Concurrency** | Java 8 | Package: `com.vbforge.multithreading_basics` | Tag: `CONCURRENCY`

Thread/Runnable, lifecycle, synchronization, `volatile`, thread-safety.

## What's covered

| Topic | Key points |
|---|---|
| Thread & Runnable | `extends Thread` vs `Runnable`, `start()` vs `run()`, `join`, daemon threads, `interrupt()`, `UncaughtExceptionHandler` |
| Thread lifecycle | `Thread.State`: `NEW`, `RUNNABLE`, `BLOCKED`, `WAITING`, `TIMED_WAITING`, `TERMINATED`; `isAlive()`; no restart |
| Synchronization | race condition, `synchronized` method/block, private lock object, instance vs class lock, reentrancy, `wait`/`notifyAll`, lock ordering |
| `volatile` | visibility, happens-before publication via a flag, not atomic for `count++`, volatile reference to an immutable snapshot |
| Thread-safety | stateless code, immutability, `ThreadLocal`, `AtomicInteger`/`compareAndSet`, check-then-act, safe lazy init |

## Files

```
multithreading-basics/
├── pom.xml
├── README.md
└── src/
    ├── main/java/com/vbforge/multithreading_basics/
    │   ├── ThreadBasicsDemo.java          ← Thread/Runnable, start vs run, join, daemon, interrupt
    │   ├── ThreadBasicsTasks.java         ← 🎯 your tasks
    │   ├── ThreadLifecycleDemo.java       ← the six Thread.State values, awaitState() helper
    │   ├── ThreadLifecycleTasks.java      ← 🎯 your tasks
    │   ├── SynchronizationDemo.java       ← race, synchronized, locks, wait/notifyAll, hammer() helper
    │   ├── SynchronizationTasks.java      ← 🎯 your tasks
    │   ├── VolatileDemo.java              ← visibility, publication, volatile is not atomic
    │   ├── VolatileTasks.java             ← 🎯 your tasks
    │   ├── ThreadSafetyDemo.java          ← stateless, immutable, ThreadLocal, atomics, check-then-act
    │   └── ThreadSafetyTasks.java         ← 🎯 your tasks
    └── test/java/com/vbforge/multithreading_basics/
        ├── ThreadBasicsDemoTest.java      ← 🧪 tests (green)
        ├── ThreadBasicsTasksTest.java     ← 🧪 tests (your tasks)
        ├── ThreadLifecycleDemoTest.java   ← 🧪 tests (green)
        ├── ThreadLifecycleTasksTest.java  ← 🧪 tests (your tasks)
        ├── SynchronizationDemoTest.java   ← 🧪 tests (green)
        ├── SynchronizationTasksTest.java  ← 🧪 tests (your tasks)
        ├── VolatileDemoTest.java          ← 🧪 tests (green)
        ├── VolatileTasksTest.java         ← 🧪 tests (your tasks)
        ├── ThreadSafetyDemoTest.java      ← 🧪 tests (green)
        └── ThreadSafetyTasksTest.java     ← 🧪 tests (your tasks)
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ThreadBasicsTasks.startNamedThread` | create, name and start a thread |
| 2 | `ThreadBasicsTasks.runOnNewThread` | get a result back from another thread (`join`) |
| 3 | `ThreadBasicsTasks.parallelSquares` | one thread per element, disjoint slots |
| 4 | `ThreadBasicsTasks.startCancellableCounter` | cooperative cancellation with `interrupt` |
| 5 | `ThreadBasicsTasks.captureUncaught` | `UncaughtExceptionHandler` |
| 6 | `ThreadLifecycleTasks.finishesWithin` | timed `join` + `isAlive` |
| 7 | `ThreadLifecycleTasks.observeStateWhileSleeping` | `TIMED_WAITING` |
| 8 | `ThreadLifecycleTasks.observeStateWhileWaiting` | `WAITING` via `Object.wait()` |
| 9 | `ThreadLifecycleTasks.observeStateWhileBlocked` | `BLOCKED` on a monitor |
| 10 | `ThreadLifecycleTasks.tryRestart` | `IllegalThreadStateException` |
| 11 | `SynchronizationTasks.incrementSafely` | shared lock, atomic read-modify-write |
| 12 | `SynchronizationTasks.transfer` | one critical section for two writes |
| 13 | `SynchronizationTasks.putValue` | `notifyAll` while owning the monitor |
| 14 | `SynchronizationTasks.takeValue` | `wait` in a `while` loop |
| 15 | `SynchronizationTasks.transferOrdered` | lock ordering, no deadlock |
| 16 | `VolatileTasks.requestStop` | volatile stop flag |
| 17 | `VolatileTasks.runUntilStopped` | spin-loop that must see the flag |
| 18 | `VolatileTasks.publish` | data first, volatile flag last |
| 19 | `VolatileTasks.awaitPayload` | flag first, data afterwards |
| 20 | `VolatileTasks.countConcurrently` | `volatile` is not enough → atomics |
| 21 | `ThreadSafetyTasks.incrementIfBelow` | atomic check-then-act (CAS loop) |
| 22 | `ThreadSafetyTasks.perThread` | `ThreadLocal.withInitial` |
| 23 | `ThreadSafetyTasks.unmodifiableCopy` | defensive copy + read-only wrapper |
| 24 | `ThreadSafetyTasks.getOrCreate` | create exactly once under concurrency |
| 25 | `ThreadSafetyTasks.sumConcurrently` | confinement: no sharing, no locks |

```bash
# Work on one pair at a time: remove @Disabled from a @Nested class, then run
mvn test -Dtest=ThreadBasicsTasksTest
```

## Key things to remember

```java
// ❌ Calls run() on the CURRENT thread — nothing is concurrent
new Thread(task).run();

// ✅ start() creates the new thread, which then calls run()
new Thread(task).start();
// ❌ and a Thread can only be started once (IllegalThreadStateException)
```

```java
// ❌ Swallowing the interrupt: the request to stop is lost
try { Thread.sleep(1000); } catch (InterruptedException e) { /* ignored */ }

// ✅ Leave the loop (or restore the flag) — sleep() CLEARS the flag when it throws
try { Thread.sleep(1000); } catch (InterruptedException e) {
    Thread.currentThread().interrupt();   // restore, so callers up the stack can see it
    return;
}
```

```java
// ❌ State changes are asynchronous — one getState() right after start() proves nothing
thread.start();
assert thread.getState() == Thread.State.TIMED_WAITING;

// ✅ Poll until the state is reached (with a deadline)
while (thread.getState() != Thread.State.TIMED_WAITING) { Thread.sleep(1); }
```

```java
// ❌ count++ is read → add → write: two threads can lose an update
private int count;
void increment() { count++; }

// ✅ Same lock for EVERY access to the shared variable — reads included
private final Object lock = new Object();
void increment() { synchronized (lock) { count++; } }
int get()        { synchronized (lock) { return count; } }
```

```java
// ❌ if instead of while: after wake-up the condition may be false again
synchronized (box) { if (box[0] == null) box.wait(); }

// ✅ Always re-check the condition in a loop; wait() releases the monitor while waiting
synchronized (box) { while (box[0] == null) box.wait(); }
// wait()/notifyAll() without owning the monitor → IllegalMonitorStateException
```

```java
// ❌ Opposite lock order → deadlock (A→B in one thread, B→A in another)
synchronized (locks[from]) { synchronized (locks[to]) { ... } }

// ✅ Every thread takes the locks in the same global order (e.g. by index)
synchronized (locks[Math.min(from, to)]) { synchronized (locks[Math.max(from, to)]) { ... } }
```

```java
// ❌ volatile guarantees visibility, NOT atomicity — this is still a race
private volatile int count;
void increment() { count++; }

// ✅ Use an atomic for single-variable counters
private final AtomicInteger count = new AtomicInteger();
void increment() { count.incrementAndGet(); }

// ✅ volatile IS right for a flag written by one thread and read by others
private volatile boolean running = true;
```

```java
// ❌ Two thread-safe calls do not make a thread-safe sequence (check-then-act)
if (counter.get() < limit) counter.incrementAndGet();

// ✅ Make check + update one atomic step
int cur;
do { cur = counter.get(); if (cur >= limit) return false; }
while (!counter.compareAndSet(cur, cur + 1));
```

```java
// ❌ Races hide: "it passed on my machine" proves nothing about thread-safe code.
//    A broken counter can print the right total many times in a row.

// ✅ Design for safety (no sharing / immutability / one lock / atomics), then test with
//    many threads started TOGETHER (CountDownLatch/CyclicBarrier) and exact assertions.
```

## Run

```bash
mvn test                                  # demo + tasks tests
mvn test -Dtest=ThreadBasicsDemoTest      # one demo test (swap in any <Concept>DemoTest)
mvn test -Dtest=ThreadBasicsTasksTest     # one tasks test (swap in any <Concept>TasksTest)
mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_basics.ThreadBasicsDemo"   # run a demo
```

Concepts: `ThreadBasics`, `ThreadLifecycle`, `Synchronization`, `Volatile`, `ThreadSafety`.
