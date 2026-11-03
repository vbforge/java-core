# java-memory-model

> **Phase 6 — Concurrency** | Java 8 | Package: `com.vbforge.java_memory_model`

Visibility vs atomicity, happens-before, reordering, safe publication, double-checked locking.
Tags: `CONCURRENCY`, `JVM`

---

## What's covered

| Topic | Key points |
|---|---|
| Visibility vs atomicity | `volatile` stop flag, `count++` is read-modify-write, `synchronized`, `AtomicLong`, `compareAndSet`, `updateAndGet`, check-then-act |
| Happens-before | program order, volatile write → read, monitor unlock → lock, `Thread.start` / `join`, `ExecutorService.submit` → `Future.get`, transitivity ("piggybacking"), `wait` in a `while` loop |
| Reordering | as-if-serial, store-buffering litmus test, message-passing litmus test, volatile = one total order, why a lock rules out the stale outcome |
| Safe publication | unsafe publication via a plain field, `final` fields (JLS 17.5), `volatile`, `AtomicReference`, lock, `ConcurrentHashMap`, `this` escape from a constructor, static factory |
| Double-checked locking | `synchronized` lazy holder, DCL with `volatile` + local variable, initialization-on-demand holder, `null`-returning factories, why the non-volatile version is broken |

> **How to read the demos.** Memory-model bugs depend on the JIT, the CPU and the scheduler.
> The "broken" demos are *allowed* to misbehave, not *required* to — a run that looks fine proves
> nothing. Tests therefore assert only what the JMM **guarantees** (never "the bug was observed").

---

## Files

```
src/
├── main/java/com/vbforge/java_memory_model/
│   ├── VisibilityAndAtomicityDemo.java     ← volatile flag, lost updates, synchronized, atomics, CAS
│   ├── VisibilityAndAtomicityTasks.java    ← 🎯 your tasks
│   ├── HappensBeforeDemo.java              ← the seven edges you use every day
│   ├── HappensBeforeTasks.java             ← 🎯 your tasks
│   ├── ReorderingDemo.java                 ← store-buffering and message-passing litmus tests
│   ├── ReorderingTasks.java                ← 🎯 your tasks
│   ├── SafePublicationDemo.java            ← plain vs final vs volatile/atomic/lock/map, this-escape
│   ├── SafePublicationTasks.java           ← 🎯 your tasks
│   ├── DoubleCheckedLockingDemo.java       ← synchronized, DCL + volatile, holder idiom
│   └── DoubleCheckedLockingTasks.java      ← 🎯 your tasks
└── test/java/com/vbforge/java_memory_model/
    ├── VisibilityAndAtomicityDemoTest.java     ← 🧪 tests (already green)
    ├── VisibilityAndAtomicityTasksTest.java    ← 🧪 tests for your implementations
    ├── HappensBeforeDemoTest.java              ← 🧪 tests (already green)
    ├── HappensBeforeTasksTest.java             ← 🧪 tests for your implementations
    ├── ReorderingDemoTest.java                 ← 🧪 tests (already green)
    ├── ReorderingTasksTest.java                ← 🧪 tests for your implementations
    ├── SafePublicationDemoTest.java            ← 🧪 tests (already green)
    ├── SafePublicationTasksTest.java           ← 🧪 tests for your implementations
    ├── DoubleCheckedLockingDemoTest.java       ← 🧪 tests (already green)
    └── DoubleCheckedLockingTasksTest.java      ← 🧪 tests for your implementations
```

---

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `VisibilityAndAtomicityTasks.countWithLock` | Shared counter protected by a lock |
| 2 | `VisibilityAndAtomicityTasks.countWithAtomic` | Lock-free shared counter |
| 3 | `VisibilityAndAtomicityTasks.workerStopsOnFlag` | Stop flag the worker is guaranteed to see |
| 4 | `VisibilityAndAtomicityTasks.incrementIfBelow` | Atomic check-then-act with CAS |
| 5 | `HappensBeforeTasks.sumPublishedViaVolatile` | Plain array published by a volatile flag |
| 6 | `HappensBeforeTasks.squaresFromWorker` | `Thread.join` as a happens-before edge |
| 7 | `HappensBeforeTasks.relayThroughThree` | Transitivity across three threads |
| 8 | `HappensBeforeTasks.pingPong` | Monitor hand-off with `wait` / `notifyAll` |
| 9 | `ReorderingTasks.storeBufferingOutcomes` | Store-buffering litmus test with volatile |
| 10 | `ReorderingTasks.countStaleReads` | Message-passing litmus test with a volatile flag |
| 11 | `ReorderingTasks.countStaleReadsSynchronized` | Message-passing litmus test under one lock |
| 12 | `SafePublicationTasks.publishViaVolatileField` | Publish a mutable list via a volatile field |
| 13 | `SafePublicationTasks.publishViaConcurrentMap` | Publish a mutable list via `ConcurrentHashMap` |
| 14 | `SafePublicationTasks.valuesSeenByEscapedThis` | `this` escape vs static factory |
| 15 | `DoubleCheckedLockingTasks.lazyWithSynchronized` | Thread-safe lazy `Supplier`, lock on every call |
| 16 | `DoubleCheckedLockingTasks.lazyWithDoubleCheckedLocking` | Correct DCL |
| 17 | `DoubleCheckedLockingTasks.lazyNullable` | Lazy value that may legitimately be `null` |
| 18 | `DoubleCheckedLockingTasks.holderIdiomInstance` | Initialization-on-demand holder |

Task tests ship with `@Disabled` on every `@Nested` class so `mvn clean install` is green before you start.
Remove `@Disabled` from one nested class, implement the method, and run just that concept:

```bash
mvn test -Dtest=VisibilityAndAtomicityTasksTest
mvn test -Dtest=HappensBeforeTasksTest
mvn test -Dtest=ReorderingTasksTest
mvn test -Dtest=SafePublicationTasksTest
mvn test -Dtest=DoubleCheckedLockingTasksTest
```

---

## Key things to remember

### 1. `volatile` gives visibility, not atomicity

```java
private volatile long count;

count++;                       // ❌ read, add, write: two threads can both read 5 and both write 6

private final AtomicLong count = new AtomicLong();
count.incrementAndGet();       // ✅ one atomic step
```

### 2. A plain stop flag is a data race, not just "slow"

```java
private boolean stop;                          // ❌ the JIT may hoist the read out of the loop
while (!stop) { work(); }                      //    -> may never terminate

private volatile boolean stop;                 // ✅ every read sees the latest write
while (!stop) { work(); }
```

A short test run often hides this: the loop has not been JIT-compiled yet.

### 3. No happens-before edge, no guarantee

```java
data = 42;                     // plain write
ready = true;                  // ✅ volatile write publishes everything written before it

if (ready) {                   // the reader may only trust `data` AFTER it has seen ready == true
    use(data);
}

use(data);                     // ❌ reading data first: nothing orders it after the write
```

### 4. `wait()` belongs in a `while` loop, and the lock must be shared

```java
synchronized (lock) {
    if (!ready) lock.wait();           // ❌ spurious wake-ups, missed conditions
}

synchronized (lock) {
    while (!ready) lock.wait();        // ✅ re-check the condition after every wake-up
}
// ❌ Reading `ready` under a DIFFERENT lock object creates no edge at all.
```

### 5. Check-then-act is two operations, even with atomics

```java
if (counter.get() < limit) counter.incrementAndGet();    // ❌ each call is atomic, the pair is not

int cur;
do {
    cur = counter.get();
    if (cur >= limit) return false;
} while (!counter.compareAndSet(cur, cur + 1));          // ✅ retry until the swap succeeds
```

### 6. Publish objects safely — or make them immutable

```java
shared = new MutablePoint(1, 2);       // ❌ plain field: a reader may see the reference with x == 0, y == 0

private volatile Point shared;         // ✅ volatile / AtomicReference / lock / ConcurrentHashMap

private final int x;                   // ✅ final fields are safe even through a plain field
```

Safe publication covers the state at publication time. If the object is modified later,
readers and writers still need synchronization.

### 7. Never let `this` escape from a constructor

```java
Counter(Consumer<Counter> registry) {
    registry.accept(this);             // ❌ listener sees value == 0 even though the field is final
    this.value = 42;
}

static Counter create(Consumer<Counter> registry) {
    Counter c = new Counter();         // ✅ finish construction first, then register
    registry.accept(c);
    return c;
}
```

### 8. Double-checked locking is correct only with `volatile`

```java
private Helper helper;                 // ❌ without volatile the unlocked fast path has no edge:
if (helper == null) {                  //    it may see a non-null reference to a half-built object
    synchronized (this) { if (helper == null) helper = new Helper(); }
}

private volatile Helper helper;        // ✅ volatile adds the missing edge
Helper h = helper;                     // ✅ read the volatile once into a local variable
if (h == null) {
    synchronized (this) {
        h = helper;
        if (h == null) { h = new Helper(); helper = h; }
    }
}
return h;

// ✅ Static singleton? The holder idiom needs no volatile and no lock in your code:
private static class Holder { static final Helper INSTANCE = new Helper(); }
```

If the factory may return `null`, `value == null` can no longer mean "not initialised" — keep a separate flag.

---

## Run

```bash
# Everything in this module (demos + tasks; tasks are @Disabled until you enable them)
mvn test

# Demo tests only (always green)
mvn test -Dtest='*DemoTest'
mvn test -Dtest=HappensBeforeDemoTest

# Your task implementations
mvn test -Dtest='*TasksTest'
mvn test -Dtest=ReorderingTasksTest

# Run a demo's main() and read the output
mvn exec:java -Dexec.mainClass="com.vbforge.java_memory_model.VisibilityAndAtomicityDemo"
```

The other demos work the same way — swap the class name for `HappensBeforeDemo`, `ReorderingDemo`,
`SafePublicationDemo`, or `DoubleCheckedLockingDemo`.

### Going further

For real memory-model testing at scale, look at OpenJDK's **jcstress** harness. It runs litmus tests
millions of times across many thread interleavings and reports every outcome it observed.
