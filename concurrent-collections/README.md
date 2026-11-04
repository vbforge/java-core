# concurrent-collections

> **Phase 6 — Concurrency** | Java 8 | Package: `com.vbforge.concurrent_collections`

Thread-safe collections and atomic variables from `java.util.concurrent`: what each one guarantees, what it does **not**, and why a thread-safe collection alone does not make your program thread-safe.

## What's covered

| Topic | Key points |
|---|---|
| `ConcurrentHashMap` | No `null` keys or values, `putIfAbsent`, `computeIfAbsent`, `computeIfPresent`, `compute`, `merge`, conditional `replace`/`remove`, weakly consistent iterators, bulk ops (`reduceValues`, `search`, `forEach` with a parallelism threshold), `mappingCount`, `newKeySet` |
| `CopyOnWriteArrayList` / `CopyOnWriteArraySet` | Snapshot iterators, never `ConcurrentModificationException`, read-only `Iterator.remove`, `addIfAbsent`, `addAllAbsent`, `removeIf`, O(n) writes, listener lists |
| `BlockingQueue` family | Four flavours of insert/remove (`add`/`offer`/`put`/timed `offer`, `remove`/`poll`/`take`/timed `poll`), `ArrayBlockingQueue`, `LinkedBlockingQueue`, back-pressure, poison pill, `drainTo`, `PriorityBlockingQueue`, `SynchronousQueue`, `DelayQueue`, `InterruptedException` |
| Atomic classes | `AtomicInteger`, `compareAndSet`, CAS retry loop, `updateAndGet`, `accumulateAndGet`, `AtomicBoolean`, `AtomicReference` (`==` not `equals`), ABA and `AtomicStampedReference`, `LongAdder`, `LongAccumulator`, `AtomicIntegerArray` |

## Files

```
src/
├── main/java/com/vbforge/concurrent_collections/
│   ├── ConcurrentMapDemo.java        ← ConcurrentHashMap, annotated reference (has main)
│   ├── ConcurrentMapTasks.java       ← 🎯 your tasks
│   ├── CopyOnWriteDemo.java          ← CopyOnWriteArrayList / Set, annotated reference (has main)
│   ├── CopyOnWriteTasks.java         ← 🎯 your tasks
│   ├── BlockingQueueDemo.java        ← BlockingQueue family, annotated reference (has main)
│   ├── BlockingQueueTasks.java       ← 🎯 your tasks
│   ├── AtomicDemo.java               ← atomic classes, annotated reference (has main)
│   └── AtomicTasks.java              ← 🎯 your tasks
└── test/java/com/vbforge/concurrent_collections/
    ├── ConcurrentMapDemoTest.java    ← 🧪 tests for the demo (green)
    ├── ConcurrentMapTasksTest.java   ← 🧪 tests for your tasks (@Disabled until you start)
    ├── CopyOnWriteDemoTest.java      ← 🧪 tests for the demo (green)
    ├── CopyOnWriteTasksTest.java     ← 🧪 tests for your tasks (@Disabled until you start)
    ├── BlockingQueueDemoTest.java    ← 🧪 tests for the demo (green)
    ├── BlockingQueueTasksTest.java   ← 🧪 tests for your tasks (@Disabled until you start)
    ├── AtomicDemoTest.java           ← 🧪 tests for the demo (green)
    └── AtomicTasksTest.java          ← 🧪 tests for your tasks (@Disabled until you start)
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ConcurrentMapTasks.countWordsConcurrently` | Shared map, atomic `merge` instead of get + put |
| 2 | `ConcurrentMapTasks.getOrLoad` | Lazy cache, loader runs once per key |
| 3 | `ConcurrentMapTasks.removeIfUnchanged` | Conditional `remove(key, value)` |
| 4 | `ConcurrentMapTasks.incrementIfPresent` | `computeIfPresent`, absent key stays absent |
| 5 | `ConcurrentMapTasks.sumOfValues` | Bulk `reduceValues`, `null` on an empty map |
| 6 | `CopyOnWriteTasks.iterateWhileAppending` | Snapshot iteration, no `ConcurrentModificationException` |
| 7 | `CopyOnWriteTasks.addUnique` | `addAllAbsent` instead of contains + add |
| 8 | `CopyOnWriteTasks.notifyListeners` | Listeners that (un)register while being notified |
| 9 | `CopyOnWriteTasks.uniqueInOrder` | `CopyOnWriteArraySet` keeps insertion order |
| 10 | `BlockingQueueTasks.sumWithProducerConsumer` | `put` / `take` with a poison pill |
| 11 | `BlockingQueueTasks.drainBatch` | Non-blocking `drainTo(collection, max)` |
| 12 | `BlockingQueueTasks.pollWithTimeout` | Timed `poll` and `Optional` |
| 13 | `BlockingQueueTasks.serveByPriority` | `PriorityBlockingQueue` with a `Comparator` |
| 14 | `BlockingQueueTasks.processInParallel` | N consumers, N poison pills |
| 15 | `AtomicTasks.countConcurrently` | `AtomicLong` / `LongAdder` counter |
| 16 | `AtomicTasks.generateUniqueIds` | `incrementAndGet` returns *your* value |
| 17 | `AtomicTasks.atomicMax` | `accumulateAndGet` or a CAS loop |
| 18 | `AtomicTasks.initOnce` | `compareAndSet(false, true)` — one winner |
| 19 | `AtomicTasks.transition` | `AtomicReference` CAS is `==`, you want `equals` |
| 20 | `AtomicTasks.histogram` | `AtomicIntegerArray` |

```bash
mvn test -Dtest=ConcurrentMapTasksTest
mvn test -Dtest=CopyOnWriteTasksTest
mvn test -Dtest=BlockingQueueTasksTest
mvn test -Dtest=AtomicTasksTest
```

Every `@Nested` class in a `*TasksTest` starts with `@Disabled("Remove @Disabled when you implement <method>()")`. Remove it for the task you are working on. Tests that use threads have a time-out, so a deadlock in your code fails the test instead of hanging the build.

## Key things to remember

```java
// ❌ Thread-safe calls, NOT a thread-safe sequence: two threads can both see "absent"
if (map.get(key) == null) {
    map.put(key, expensiveCreate());
}

// ✅ One atomic step
map.computeIfAbsent(key, k -> expensiveCreate());
// Same lesson for lists: list.contains(x) + list.add(x)  →  cowList.addIfAbsent(x)
```

```java
// ❌ volatile gives visibility, not atomicity: two threads read 0, both write 1
volatile int count;
count++;

// ✅ read-modify-write as one step (LongAdder if many threads only add and read at the end)
AtomicInteger count = new AtomicInteger();
count.incrementAndGet();      // returns the NEW value; getAndIncrement() returns the OLD one
```

```java
// ❌ ConcurrentHashMap is null-hostile: null would be ambiguous with "absent"
map.put("key", null);                     // NullPointerException

// ❌ reduceValues returns null for an EMPTY map; unboxing it throws NullPointerException
int sum = map.reduceValues(Long.MAX_VALUE, Integer::sum);
// ✅ handle the empty case
Integer total = map.reduceValues(Long.MAX_VALUE, Integer::sum);
int sum = total == null ? 0 : total;
```

```java
// ❌ Iterator.remove() on a CopyOnWriteArrayList iterator: UnsupportedOperationException
// ✅ modify the LIST, not the iterator
list.removeIf(x -> x < 0);

// Writes copy the whole array (O(n)) -> only for read-mostly data such as listener lists.
// The iterator is a snapshot: it never sees later changes, not even your own.
```

```java
// ❌ add() on a full BlockingQueue THROWS IllegalStateException - it does not wait
queue.add(item);
// ✅ put() waits (back-pressure), offer(item, 1, SECONDS) waits at most one second
queue.put(item);

// ❌ new LinkedBlockingQueue<>() is NOT bounded (Integer.MAX_VALUE): no back-pressure
// ✅ new LinkedBlockingQueue<>(1_000)

// ❌ swallowing the interrupt: the flag was cleared when the exception was thrown
catch (InterruptedException e) { /* ignore */ }
// ✅ restore it so callers can still see it
catch (InterruptedException e) { Thread.currentThread().interrupt(); }
```

```java
// ❌ N consumers need N poison pills; one pill stops only one consumer and the rest wait forever
queue.put(POISON_PILL);
// ✅ one per consumer
for (int i = 0; i < consumers; i++) queue.put(POISON_PILL);
```

```java
// ❌ AtomicReference.compareAndSet compares with ==, not equals()
ref.compareAndSet(new String("NEW"), "RUNNING");   // false: different object
// ✅ read the current reference, compare by value, then CAS with THAT reference
String current = ref.get();
if (current.equals("NEW")) ref.compareAndSet(current, "RUNNING");
```

## Run

```bash
# demo + tasks (task tests are @Disabled until you start)
mvn test

# demo tests only
mvn test -Dtest=ConcurrentMapDemoTest
# (also: CopyOnWriteDemoTest, BlockingQueueDemoTest, AtomicDemoTest)

# your task tests only
mvn test -Dtest=ConcurrentMapTasksTest
# (also: CopyOnWriteTasksTest, BlockingQueueTasksTest, AtomicTasksTest)

# run a demo's main()
mvn exec:java -Dexec.mainClass="com.vbforge.concurrent_collections.ConcurrentMapDemo"
# (also: CopyOnWriteDemo, BlockingQueueDemo, AtomicDemo)
```
