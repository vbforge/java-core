# completablefuture-and-async

> **Phase 6 — Concurrency** | Java 8 | Package: `com.vbforge.completablefuture_and_async`

## What's covered

| Topic | Key points |
|---|---|
| Starting & composing | `supplyAsync`, `runAsync`, `completedFuture`, manual `complete`, `thenApply`, `thenAccept`, `thenRun`, `thenCompose` (flatMap), `thenCombine`, `join` vs `get` |
| Many futures | `allOf` (returns `Void`, no fail-fast), `anyOf` (returns `Object`, first *completed* not first *successful*), `applyToEither` / `acceptEither` |
| Error handling | failure skips dependents, `exceptionally`, `handle`, `whenComplete`, `CompletionException` wrapping, Java 8 "recover with another future", `cancel` |
| Executors | `ForkJoinPool.commonPool()`, custom `Executor`, `thenApply` vs `thenApplyAsync`, `shutdown`, daemon vs non-daemon threads |
| Timeouts | no `orTimeout` in Java 8 → `ScheduledExecutorService` + `applyToEither`, timeout does not stop the source |

## Files

```
src/
├── main/java/com/vbforge/completablefuture_and_async/
│   ├── ComposingDemo.java                ← supplyAsync, thenApply/Compose/Combine, join vs get
│   ├── ComposingTasks.java               ← 🎯 your tasks
│   ├── AllOfAnyOfDemo.java               ← allOf, anyOf, applyToEither
│   ├── AllOfAnyOfTasks.java              ← 🎯 your tasks
│   ├── ErrorHandlingDemo.java            ← exceptionally, handle, whenComplete, cancel
│   ├── ErrorHandlingTasks.java           ← 🎯 your tasks
│   ├── ExecutorsAndTimeoutsDemo.java     ← executors, *Async variants, Java 8 timeouts
│   └── ExecutorsAndTimeoutsTasks.java    ← 🎯 your tasks
└── test/java/com/vbforge/completablefuture_and_async/
    ├── ComposingDemoTest.java            ← 🧪 tests (green)
    ├── ComposingTasksTest.java           ← 🧪 tests for your tasks
    ├── AllOfAnyOfDemoTest.java           ← 🧪 tests (green)
    ├── AllOfAnyOfTasksTest.java          ← 🧪 tests for your tasks
    ├── ErrorHandlingDemoTest.java        ← 🧪 tests (green)
    ├── ErrorHandlingTasksTest.java       ← 🧪 tests for your tasks
    ├── ExecutorsAndTimeoutsDemoTest.java ← 🧪 tests (green)
    └── ExecutorsAndTimeoutsTasksTest.java← 🧪 tests for your tasks
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ComposingTasks.asyncTrimUpper` | `supplyAsync` + `thenApply` |
| 2 | `ComposingTasks.emailForUserId` | `thenCompose` for dependent async calls |
| 3 | `ComposingTasks.describePerson` | `thenCombine` for independent futures |
| 4 | `ComposingTasks.recordWhenDone` | `thenAccept` as the end of a pipeline |
| 5 | `AllOfAnyOfTasks.allAsList` | `allOf` + gathering values in input order |
| 6 | `AllOfAnyOfTasks.firstCompleted` | typed wrapper around `anyOf` |
| 7 | `AllOfAnyOfTasks.countSuccessful` | `allOf` without failing, inspecting outcomes |
| 8 | `AllOfAnyOfTasks.firstSuccessful` | "first success" — what `anyOf` cannot do |
| 9 | `ErrorHandlingTasks.unwrap` | peeling `CompletionException` / `ExecutionException` |
| 10 | `ErrorHandlingTasks.parseOrDefault` | `exceptionally` as an async catch |
| 11 | `ErrorHandlingTasks.describeOutcome` | `handle` + the wrapping rules |
| 12 | `ErrorHandlingTasks.fallbackChain` | Java 8 "recover with another future" |
| 13 | `ErrorHandlingTasks.alwaysCleanup` | `whenComplete` keeps the outcome |
| 14 | `ExecutorsAndTimeoutsTasks.namedDaemonThreadFactory` | `ThreadFactory`, daemon threads |
| 15 | `ExecutorsAndTimeoutsTasks.currentThreadName` | `supplyAsync(supplier, executor)` |
| 16 | `ExecutorsAndTimeoutsTasks.sumOfSquares` | fan-out on an executor + fan-in |
| 17 | `ExecutorsAndTimeoutsTasks.withFallbackOnTimeout` | Java 8 `completeOnTimeout` |
| 18 | `ExecutorsAndTimeoutsTasks.completesWithin` | racing a future against a timer |

```bash
mvn test -Dtest=ComposingTasksTest
mvn test -Dtest=AllOfAnyOfTasksTest
mvn test -Dtest=ErrorHandlingTasksTest
mvn test -Dtest=ExecutorsAndTimeoutsTasksTest
```

## Key things to remember

```java
// ✅ thenCompose when the next step is itself async (flatMap)
findUser(id).thenCompose(user -> findEmail(user));       // CompletableFuture<String>
// ❌ thenApply would give you CompletableFuture<CompletableFuture<String>>
findUser(id).thenApply(user -> findEmail(user));

// ✅ start independent calls first, then combine: latency = max(a, b)
CompletableFuture<A> a = callA();
CompletableFuture<B> b = callB();
a.thenCombine(b, (x, y) -> merge(x, y));
// ❌ thenCompose here serialises them: latency = a + b
callA().thenCompose(x -> callB().thenApply(y -> merge(x, y)));
```

```java
// ✅ allOf returns Void — read the values from the inputs afterwards
CompletableFuture.allOf(f1, f2).thenApply(ignored -> f1.join() + f2.join());
// ❌ allOf(...).join() is null — it tells you WHEN, not WHAT
// ❌ allOf is not fail-fast: one failure still waits for the slowest input

// ❌ anyOf = first COMPLETED, not first SUCCESSFUL: an early error beats a later success
CompletableFuture.anyOf(failsFast, succeedsLater);        // fails!
// ✅ for "first success" complete your own future from callbacks (see AllOfAnyOfTasks.firstSuccessful)
```

```java
// ❌ the exception you receive is often wrapped — this check can be false
.exceptionally(e -> e instanceof IllegalStateException ? 0 : -1);
// ✅ unwrap CompletionException / ExecutionException first, then inspect the cause
.exceptionally(e -> unwrap(e) instanceof IllegalStateException ? 0 : -1);

// ✅ whenComplete observes (cleanup, logging) and keeps the outcome
// ✅ handle transforms value-or-error into a new result
// ❌ an unobserved failure is silent: no join() and no handler → nobody ever sees it
```

```java
// ❌ blocking work on the default pool starves parallel streams and other futures
CompletableFuture.supplyAsync(() -> httpClient.blockingCall());
// ✅ give blocking work its own executor ...
CompletableFuture.supplyAsync(() -> httpClient.blockingCall(), ioPool);
// ... and shut it down: Executors.newFixedThreadPool threads are NON-daemon,
//     forgetting shutdown() keeps the JVM alive forever
ioPool.shutdown();
```

```java
// ✅ thenApply runs on the completing thread (or the caller, if already complete)
// ✅ thenApplyAsync(fn, executor) moves heavy callbacks to a pool you control

// ❌ Java 8 has no orTimeout / completeOnTimeout / failedFuture / exceptionallyCompose
//    build them: ScheduledExecutorService + applyToEither, new CompletableFuture + completeExceptionally
// ❌ a timeout fails the WRAPPER only — the slow source keeps running
// ❌ cancel(true) does not interrupt: the boolean is ignored, a running supplier runs to the end
```

## Run

```bash
# demo + tasks
mvn test

# only the demos
mvn test -Dtest='*DemoTest'

# only task tests
mvn test -Dtest='*TasksTest'

# run a demo's main()
mvn exec:java -Dexec.mainClass="com.vbforge.completablefuture_and_async.ComposingDemo"
```
