# multithreading-advanced

> **Phase 6 — Concurrency** | Java 8 | Package: `com.vbforge.multithreading_advanced`

`ExecutorService`, `ThreadPool`, `Callable`/`Future`, `CountDownLatch`, `Semaphore` — the `java.util.concurrent` tools that replace hand-managed `Thread` objects.

## What's covered

| Topic | Key points |
|---|---|
| `ExecutorService` & thread pools | `Executors.newFixedThreadPool`, `newSingleThreadExecutor`, `newCachedThreadPool`, `ScheduledExecutorService` (`schedule`, `scheduleAtFixedRate`), `shutdown` vs `shutdownNow` vs `awaitTermination`, `ThreadPoolExecutor` (core / queue / max), rejection policies (`AbortPolicy`, `CallerRunsPolicy`, `DiscardPolicy`), custom `ThreadFactory` |
| `Callable` / `Future` | `Callable<V>` vs `Runnable`, `Future.get` / `get(timeout)` / `isDone`, `ExecutionException`, `submit` vs `execute`, `cancel(true/false)`, `invokeAll` / `invokeAny`, `FutureTask` |
| `CountDownLatch` | wait-for-N-workers, start gate (count 1), `await(timeout)` returns `boolean`, single-use, `countDown` in `finally` |
| `Semaphore` | `acquire` / `release`, limiting concurrency, `tryAcquire`, `acquire(n)`, `drainPermits`, fairness, the stray-`release()` trap |

## Files

```
src/
├── main/java/com/vbforge/multithreading_advanced/
│   ├── ExecutorServiceDemo.java      ← annotated reference: pools, shutdown, ThreadPoolExecutor, rejection, ThreadFactory
│   ├── ExecutorServiceTasks.java     ← 🎯 your tasks (5 methods)
│   ├── CallableFutureDemo.java       ← annotated reference: Callable, Future, exceptions, cancel, invokeAll/Any
│   ├── CallableFutureTasks.java      ← 🎯 your tasks (5 methods)
│   ├── SynchronizersDemo.java        ← annotated reference: CountDownLatch, Semaphore
│   └── SynchronizersTasks.java       ← 🎯 your tasks (4 methods)
└── test/java/com/vbforge/multithreading_advanced/
    ├── ExecutorServiceDemoTest.java  ← 🧪 tests (green, verify every claim in the demo)
    ├── ExecutorServiceTasksTest.java ← 🧪 tests (remove @Disabled per method to activate)
    ├── CallableFutureDemoTest.java   ← 🧪 tests (green)
    ├── CallableFutureTasksTest.java  ← 🧪 tests (remove @Disabled per method to activate)
    ├── SynchronizersDemoTest.java    ← 🧪 tests (green)
    └── SynchronizersTasksTest.java   ← 🧪 tests (remove @Disabled per method to activate)
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ExecutorServiceTasks.squareAll` | Fixed pool, ordered results, shutting the pool down in `finally` |
| 2 | `ExecutorServiceTasks.shutdownGracefully` | `shutdown` → `awaitTermination` → `shutdownNow`, interrupt handling |
| 3 | `ExecutorServiceTasks.newNamedFixedPool` | Custom `ThreadFactory` with a thread-safe counter |
| 4 | `ExecutorServiceTasks.newBoundedPool` | `ThreadPoolExecutor` with a bounded queue and abort-on-saturation |
| 5 | `ExecutorServiceTasks.scheduleGreeting` | `ScheduledExecutorService.schedule` with a `Callable` |
| 6 | `CallableFutureTasks.runAll` | `invokeAll`, results in input order |
| 7 | `CallableFutureTasks.firstSuccessful` | `invokeAny`, ignoring failed jobs |
| 8 | `CallableFutureTasks.getWithTimeout` | `get(timeout)`, `TimeoutException`, `cancel(true)` |
| 9 | `CallableFutureTasks.outcome` | Telling success, cancellation and failure apart |
| 10 | `CallableFutureTasks.parallelSum` | Splitting work into `Callable` slices, collecting futures |
| 11 | `SynchronizersTasks.runAndWaitForAll` | `CountDownLatch` + `countDown` in `finally` |
| 12 | `SynchronizersTasks.runWithTimeout` | `CountDownLatch.await(timeout)` |
| 13 | `SynchronizersTasks.callWithPermit` | `Semaphore` `acquire` / `release` in `finally` |
| 14 | `SynchronizersTasks.tryCallWithPermit` | `tryAcquire` and never releasing an unacquired permit |

```bash
mvn test -Dtest=ExecutorServiceTasksTest
mvn test -Dtest=CallableFutureTasksTest
mvn test -Dtest=SynchronizersTasksTest
```

> Every task test lives in a `@Nested` class marked `@Disabled("Remove @Disabled when you implement <method>()")`.
> Implement a method, delete that one `@Disabled`, run the test class.

## Key things to remember

**1. Always shut the pool down — the default threads are non-daemon.**

```java
// ❌ the JVM never exits: pool-1-thread-1 is a non-daemon thread
ExecutorService pool = Executors.newFixedThreadPool(2);
pool.submit(() -> doWork());

// ✅ shutdown() in finally; it still lets queued tasks finish
ExecutorService pool = Executors.newFixedThreadPool(2);
try {
    pool.submit(() -> doWork());
} finally {
    pool.shutdown();               // no new tasks; running + queued ones complete
}                                  // shutdownNow() = interrupt running, hand back the queue
```

**2. `submit()` swallows exceptions — `get()` is where they appear.**

```java
// ❌ the failure is captured in the Future and nobody ever looks
pool.submit(() -> { throw new IllegalStateException("boom"); });

// ✅ ExecutionException wraps the real error — read getCause()
try {
    future.get();
} catch (ExecutionException e) {
    Throwable real = e.getCause();     // IllegalStateException: boom
}
// execute() is different: the exception goes to the thread's UncaughtExceptionHandler
```

**3. `Executors` factories hide unbounded resources.**

```java
// ❌ queue is Integer.MAX_VALUE long → a slow consumer ends in OutOfMemoryError
Executors.newFixedThreadPool(4);
// ❌ maximumPoolSize = Integer.MAX_VALUE → a burst of blocking tasks = thousands of threads
Executors.newCachedThreadPool();

// ✅ bounded queue + explicit rejection policy
new ThreadPoolExecutor(2, 4, 30, TimeUnit.SECONDS,
        new ArrayBlockingQueue<Runnable>(100),
        new ThreadPoolExecutor.CallerRunsPolicy());   // back-pressure instead of failure
// order of decisions: core thread → queue → extra thread (only when queue is FULL) → reject
```

**4. A `TimeoutException` does not stop the task.**

```java
// ❌ you gave up waiting, the task keeps running and burning a thread
try { future.get(1, TimeUnit.SECONDS); } catch (TimeoutException e) { /* ... */ }

// ✅ cancel it — cancel(true) interrupts, but the task must react to the interrupt
catch (TimeoutException e) { future.cancel(true); }
// get() on a cancelled future throws CancellationException (unchecked!)
```

**5. A failing periodic task silently stops repeating.**

```java
// ❌ one exception and scheduleAtFixedRate never runs the task again — and logs nothing
scheduler.scheduleAtFixedRate(() -> refreshCache(), 0, 1, TimeUnit.SECONDS);

// ✅ catch inside the task if the schedule must survive failures
scheduler.scheduleAtFixedRate(() -> {
    try { refreshCache(); } catch (RuntimeException e) { log(e); }
}, 0, 1, TimeUnit.SECONDS);
```

**6. `CountDownLatch`: count down in `finally`, check `await(timeout)`'s result, it is single-use.**

```java
// ❌ if work() throws, countDown() is skipped and await() waits forever
pool.execute(() -> { work(); latch.countDown(); });

// ✅
pool.execute(() -> {
    try { work(); } finally { latch.countDown(); }
});

// ❌ await(timeout) does not throw on timeout — it returns false
latch.await(5, TimeUnit.SECONDS);
// ✅
if (!latch.await(5, TimeUnit.SECONDS)) { /* some workers are still running */ }
// a latch never resets: for a reusable barrier use CyclicBarrier
```

**7. `Semaphore` does not track owners — never release a permit you did not acquire.**

```java
// ❌ release() runs even when tryAcquire() failed → the limit silently grows
boolean got = semaphore.tryAcquire();
try { if (got) { use(); } } finally { semaphore.release(); }

// ✅ release only after a successful acquire
if (semaphore.tryAcquire()) {
    try { use(); } finally { semaphore.release(); }
}
// new Semaphore(1).release() leaves availablePermits() == 2
```

**8. Update your bookkeeping *before* releasing the permit.**

```java
// ❌ another thread can enter and increment before we decrement → counter briefly exceeds the limit
permits.release();
running.decrementAndGet();

// ✅
running.decrementAndGet();
permits.release();
```

## Run

```bash
# demo + tasks (task tests are @Disabled until you activate them)
mvn test

# demo tests
mvn test -Dtest=ExecutorServiceDemoTest
mvn test -Dtest=CallableFutureDemoTest
mvn test -Dtest=SynchronizersDemoTest

# your task implementations
mvn test -Dtest=ExecutorServiceTasksTest
mvn test -Dtest=CallableFutureTasksTest
mvn test -Dtest=SynchronizersTasksTest

# run a demo with output
mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_advanced.ExecutorServiceDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_advanced.CallableFutureDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.multithreading_advanced.SynchronizersDemo"
```
