# locks-and-conditions

> **Phase 6 — Concurrency** | Java 8 | Package: `com.vbforge.locks_and_conditions`

Explicit locking from `java.util.concurrent.locks`: when `synchronized` is not enough,
how to use each lock correctly, and how locking goes wrong (deadlock, livelock, starvation).

## What's covered

| Topic | Key points |
|---|---|
| `ReentrantLock` | `lock`/`unlock` in `try`/`finally`, hold count, `tryLock()`, `tryLock(timeout)`, `lockInterruptibly()`, fair vs non-fair |
| `ReadWriteLock` | `ReentrantReadWriteLock`, shared `readLock()` vs exclusive `writeLock()`, no upgrade, downgrade, double-checked `getOrCompute` |
| `StampedLock` | stamps, `tryOptimisticRead` + `validate`, `tryConvertToWriteLock`, **not reentrant**, `unlockRead`/`unlockWrite(stamp)` |
| `Condition` | `newCondition`, `await`/`signal`/`signalAll`, guarded wait in a `while` loop, `awaitNanos`, one condition per predicate |
| Deadlock / livelock / starvation | Coffman conditions, `ThreadMXBean.findDeadlockedThreads`, lock ordering, `tryLock` + back-off, exponential back-off, fair locks |

## Files

```
src/
├── main/java/com/vbforge/locks_and_conditions/
│   ├── ReentrantLockDemo.java      ← annotated reference: lock, reentrancy, tryLock, interruptibly, fairness
│   ├── ReentrantLockTasks.java     ← 🎯 your tasks
│   ├── ReadWriteLockDemo.java      ← annotated reference: read/write cache, upgrade vs downgrade
│   ├── ReadWriteLockTasks.java     ← 🎯 your tasks
│   ├── StampedLockDemo.java        ← annotated reference: optimistic reads, conversion, not reentrant
│   ├── StampedLockTasks.java       ← 🎯 your tasks
│   ├── ConditionDemo.java          ← annotated reference: bounded buffer, await timeout, signalAll
│   ├── ConditionTasks.java         ← 🎯 your tasks
│   ├── LivenessDemo.java           ← annotated reference: real deadlock, lock ordering, livelock, fair lock
│   └── LivenessTasks.java          ← 🎯 your tasks
└── test/java/com/vbforge/locks_and_conditions/
    ├── ReentrantLockDemoTest.java  ← 🧪 tests (always green)
    ├── ReentrantLockTasksTest.java ← 🧪 tests for YOUR code (@Disabled until you start)
    ├── ReadWriteLockDemoTest.java  ← 🧪 tests (always green)
    ├── ReadWriteLockTasksTest.java ← 🧪 tests for YOUR code
    ├── StampedLockDemoTest.java    ← 🧪 tests (always green)
    ├── StampedLockTasksTest.java   ← 🧪 tests for YOUR code
    ├── ConditionDemoTest.java      ← 🧪 tests (always green)
    ├── ConditionTasksTest.java     ← 🧪 tests for YOUR code
    ├── LivenessDemoTest.java       ← 🧪 tests (always green)
    └── LivenessTasksTest.java      ← 🧪 tests for YOUR code
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ReentrantLockTasks.incrementConcurrently` | Guard a shared counter with `lock`/`try`/`finally` |
| 2 | `ReentrantLockTasks.runIfFree` | Non-blocking `tryLock()`; never unlock a lock you did not get |
| 3 | `ReentrantLockTasks.nestedHoldCount` | Reentrancy and `getHoldCount()` |
| 4 | `ReentrantLockTasks.acquireWithTimeout` | Timed `tryLock`, restoring the interrupt flag |
| 5 | `ReadWriteLockTasks.getCached` | Read under the shared read lock |
| 6 | `ReadWriteLockTasks.putCached` | Write under the exclusive write lock |
| 7 | `ReadWriteLockTasks.getOrCompute` | Read first, then write, then **re-check** (no upgrade) |
| 8 | `ReadWriteLockTasks.canWriteWhileHoldingRead` | Why read → write upgrade is impossible |
| 9 | `ReadWriteLockTasks.downgradeToReadLock` | Write → read downgrade, in the right order |
| 10 | `StampedLockTasks.writePoint` | Write lock stamp, released with the same stamp |
| 11 | `StampedLockTasks.snapshotOptimistic` | Optimistic read, `validate`, pessimistic fallback |
| 12 | `StampedLockTasks.isStampStillValid` | What invalidates an optimistic stamp |
| 13 | `StampedLockTasks.upgradeSoleReader` | `tryConvertToWriteLock` and its failure case |
| 14 | `StampedLockTasks.writeLockIsReentrant` | `StampedLock` is **not** reentrant |
| 15 | `ConditionTasks.Mailbox` (`put`, `take`) | One lock, two conditions, guarded waits |
| 16 | `ConditionTasks.awaitFlag` | Timed wait with `awaitNanos` and a predicate loop |
| 17 | `ConditionTasks.TurnGate` (`awaitTurn`, `passTurn`) | Two threads alternating; `signalAll` on a shared condition |
| 18 | `LivenessTasks.transferOrdered` | Deadlock prevention by global lock order |
| 19 | `LivenessTasks.transferWithTimeout` | Deadlock avoidance with `tryLock` and clean release |
| 20 | `LivenessTasks.findDeadlockedThreadIds` | Detecting deadlocks with `ThreadMXBean` |
| 21 | `LivenessTasks.backoffDelayMillis` | Exponential back-off against livelock (overflow-safe) |

```bash
# one pair at a time
mvn test -Dtest=ReentrantLockTasksTest
mvn test -Dtest=ReadWriteLockTasksTest
mvn test -Dtest=StampedLockTasksTest
mvn test -Dtest=ConditionTasksTest
mvn test -Dtest=LivenessTasksTest
```

Remove `@Disabled` from the `@Nested` class of the task you are working on.

## Key things to remember

```java
// ✅ lock() BEFORE try — unlock() in finally
lock.lock();
try {
    // critical section
} finally {
    lock.unlock();            // runs even if the section throws
}

// ❌ lock() inside try: if lock() fails, finally unlocks a lock you never held
try {
    lock.lock();
} finally {
    lock.unlock();            // IllegalMonitorStateException
}
```

```java
// ✅ tryLock: unlock ONLY if you got it
if (lock.tryLock()) {
    try { work(); } finally { lock.unlock(); }
}

// ❌ unlocking after a failed tryLock
lock.tryLock();
lock.unlock();                // IllegalMonitorStateException when tryLock returned false
```

```java
// ✅ Condition: always wait in a while loop (spurious wakeups, stolen items)
while (queue.isEmpty()) {
    notEmpty.await();
}

// ❌ if: the predicate may be false again when the thread wakes up
if (queue.isEmpty()) {
    notEmpty.await();
}
```

```java
// ✅ ReadWriteLock: release the read lock, take the write lock, RE-CHECK
rw.readLock().lock();
try { value = map.get(key); } finally { rw.readLock().unlock(); }
if (value == null) {
    rw.writeLock().lock();
    try {
        value = map.get(key);                 // another thread may have filled it
        if (value == null) { value = load(); map.put(key, value); }
    } finally { rw.writeLock().unlock(); }
}

// ❌ upgrading while holding the read lock: the thread waits for ITSELF forever
rw.readLock().lock();
rw.writeLock().lock();                        // deadlock
```

```java
// ✅ StampedLock optimistic read: copy to locals, validate, then use the locals
long stamp = lock.tryOptimisticRead();
double cx = x, cy = y;                        // may be torn until validated
if (!lock.validate(stamp)) {                  // a writer interfered
    stamp = lock.readLock();
    try { cx = x; cy = y; } finally { lock.unlockRead(stamp); }
}

// ❌ StampedLock is NOT reentrant: the second writeLock() blocks forever
long s1 = lock.writeLock();
long s2 = lock.writeLock();                   // self-deadlock
```

```java
// ✅ Prevent deadlock: one global lock order (unique, immutable id)
Account first  = from.getId() < to.getId() ? from : to;
Account second = first == from ? to : from;

// ❌ locking in argument order: transfer(a, b) vs transfer(b, a) can deadlock
from.getLock().lock();
to.getLock().lock();
```

```java
// ✅ lockInterruptibly / timed tryLock make waiting cancellable
if (!lock.tryLock(50, TimeUnit.MILLISECONDS)) { /* back off, retry later */ }

// ❌ plain lock() ignores interrupt() while it waits — the thread cannot be cancelled
lock.lock();
```

- **Deadlock** = frozen, no CPU, visible to `ThreadMXBean`. **Livelock** = busy, no progress, *invisible* to it.
  Break symmetry with exponential back-off plus jitter. **Starvation** = a waiting thread never wins; a fair lock
  (`new ReentrantLock(true)`) trades throughput for FIFO order — and the untimed `tryLock()` still barges.
- Prefer the simplest tool: `synchronized` for plain mutual exclusion, `ReentrantLock` when you need `tryLock`,
  timeouts, interruptibility or several conditions, `ReadWriteLock`/`StampedLock` only for read-heavy data
  (measure first).

## Run

```bash
mvn test                                    # demos + tasks tests (tasks are @Disabled)
mvn test -Dtest=ReentrantLockDemoTest       # demo tests (also: ReadWriteLockDemoTest, StampedLockDemoTest, ConditionDemoTest, LivenessDemoTest)
mvn test -Dtest=ReentrantLockTasksTest      # your tasks (also: ReadWriteLockTasksTest, StampedLockTasksTest, ConditionTasksTest, LivenessTasksTest)
mvn exec:java -Dexec.mainClass="com.vbforge.locks_and_conditions.ReentrantLockDemo"
```

Other demos: `ReadWriteLockDemo`, `StampedLockDemo`, `ConditionDemo`, `LivenessDemo` — same package.
