# exception-handling

> **Phase 4 — Exceptions & I/O** | Java 8 | Package: `com.vbforge.exception_handling`

## What's covered

| Topic | Key points |
|---|---|
| `try / catch` | Catch the most specific type you can handle; first matching block wins |
| Multi-catch | `catch (A \| B e)` — one handler for unrelated types |
| `finally` | Runs after catch, even after `return`; return value is fixed before it runs |
| `finally` gotchas | `return` or `throw` inside `finally` discards/replaces the in-flight exception |
| Checked vs unchecked | `Exception` (checked) vs `RuntimeException` and `Error` (unchecked); handle-or-declare |
| Wrapping | Checked → unchecked with `UncheckedIOException` / a cause; checked exceptions inside lambdas |
| Custom exceptions | Checked for recoverable failures, unchecked for contract violations; carry data, always allow a `cause` |
| Cause chains | `getCause()`, walking to the root, the "lost cause" mistake |
| try-with-resources | `AutoCloseable`, reverse close order, close runs before `catch`/`finally` |
| Suppressed exceptions | Body exception wins, close failure goes to `getSuppressed()`; contrast with `try/finally` |
| Edge cases | Partially opened resources, `null` resources |

> ℹ️ **Java 8 content** — the sources use only Java 8 APIs and were compiled with `--release 8`.
> The build itself uses the parent's Java 17 default. Resource variables declared outside the
> `try (...)` header (`try (existing)`) are a Java 9 feature — declare them inside the parentheses.

## Files

```
src/
├── main/java/com/vbforge/exception_handling/
│   ├── TryCatchFinallyDemo.java          ← theory: catch order, multi-catch, finally and its gotchas
│   ├── TryCatchFinallyTasks.java         ← 🎯 your tasks — implement these methods
│   ├── CheckedVsUncheckedDemo.java       ← theory: hierarchy, handle-or-declare, wrapping, lambdas
│   ├── CheckedVsUncheckedTasks.java      ← 🎯 your tasks — implement these methods
│   ├── CustomExceptionsDemo.java         ← theory: custom checked/unchecked types, cause chains
│   ├── CustomExceptionsTasks.java        ← 🎯 your tasks — implement these methods
│   ├── TryWithResourcesDemo.java         ← theory: auto-close, order, suppressed exceptions
│   ├── TryWithResourcesTasks.java        ← 🎯 your tasks — implement these methods
│   ├── ThrowingFunction.java             ← functional interface whose method may throw a checked exception
│   ├── TrackedResource.java              ← fake AutoCloseable that logs open/use/close events
│   └── bank/
│       ├── Account.java                  ← tiny account used by the custom-exception demo and tasks
│       ├── InsufficientFundsException.java   ← checked, carries balance/requested/shortfall
│       ├── InvalidAmountException.java       ← unchecked, extends IllegalArgumentException
│       └── AccountServiceException.java      ← unchecked wrapper with a cause
└── test/java/com/vbforge/exception_handling/
    ├── TryCatchFinallyDemoTest.java          ← tests for the demo
    ├── TryCatchFinallyTasksTest.java         ← 🧪 tests for your tasks — remove @Disabled per task
    ├── CheckedVsUncheckedDemoTest.java       ← tests for the demo
    ├── CheckedVsUncheckedTasksTest.java      ← 🧪 tests for your tasks — remove @Disabled per task
    ├── CustomExceptionsDemoTest.java         ← tests for the demo
    ├── CustomExceptionsTasksTest.java        ← 🧪 tests for your tasks — remove @Disabled per task
    ├── TryWithResourcesDemoTest.java         ← tests for the demo
    └── TryWithResourcesTasksTest.java        ← 🧪 tests for your tasks — remove @Disabled per task
```

## 🎯 Tasks

Open each `*Tasks.java` and implement the methods. Every method has a Javadoc with
examples and hints. Each task has its own `@Nested` test class marked `@Disabled` —
remove the annotation from the task you are working on.

| # | Method | Topic |
|---|---|---|
| 1 | `TryCatchFinallyTasks.safeDivide(a, b, fallback)` | Catching one specific exception |
| 2 | `TryCatchFinallyTasks.sumValidNumbers(csv)` | try/catch inside a loop |
| 3 | `TryCatchFinallyTasks.firstOrNone(numbers)` | Multi-catch |
| 4 | `TryCatchFinallyTasks.retry(action, maxAttempts)` | Rethrowing the last failure |
| 5 | `TryCatchFinallyTasks.runWithCleanup(action, log)` | `finally` |
| 6 | `CheckedVsUncheckedTasks.classify(throwable)` | The exception hierarchy |
| 7 | `CheckedVsUncheckedTasks.readOrDefault(source, fallback)` | Catching `Exception` but not `Error` |
| 8 | `CheckedVsUncheckedTasks.loadRequired(source)` | Wrapping checked, passing unchecked through |
| 9 | `CheckedVsUncheckedTasks.collectFailures(inputs, function)` | Continuing after a failure |
| 10 | `CustomExceptionsTasks.shortfall(account, amount)` | Using data carried by a custom exception |
| 11 | `CustomExceptionsTasks.transfer(from, to, amount)` | Translating exceptions, keeping the cause |
| 12 | `CustomExceptionsTasks.tryDeposit(account, amount)` | Catching an unchecked custom exception |
| 13 | `CustomExceptionsTasks.rootCause(throwable)` | Walking the cause chain |
| 14 | `TryWithResourcesTasks.useAndClose(name, log)` | Basic try-with-resources |
| 15 | `TryWithResourcesTasks.useBoth(first, second, log)` | Several resources, reverse close order |
| 16 | `TryWithResourcesTasks.runCatching(name, log, body)` | Close happens before `catch` |
| 17 | `TryWithResourcesTasks.suppressedMessages(exception)` | `getSuppressed()` |
| 18 | `TryWithResourcesTasks.closeAllQuietly(resources)` | Manual closing without losing failures |

Run your solutions with:
```bash
mvn test -Dtest=TryCatchFinallyTasksTest
mvn test -Dtest=CheckedVsUncheckedTasksTest
mvn test -Dtest=CustomExceptionsTasksTest
mvn test -Dtest=TryWithResourcesTasksTest
```
All 18 task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ return inside finally silently discards the exception in flight
try { throw new IllegalStateException("lost"); } finally { return "x"; }
// ✅ keep finally for cleanup only — no return, no throw

// ❌ the returned value is computed BEFORE finally runs
int x = 1; try { return x; } finally { x = 2; }          // returns 1
// ✅ finally can run code, but cannot change an already computed return value

// ❌ catching the general type first does not compile (unreachable catch)
catch (RuntimeException e) { ... } catch (ArithmeticException e) { ... }
// ✅ most specific first
catch (ArithmeticException e) { ... } catch (RuntimeException e) { ... }

// ❌ catching Throwable (or Exception) everywhere swallows bugs and JVM Errors
try { ... } catch (Throwable t) { }
// ✅ catch only what you can handle; let Errors and bugs propagate

// ❌ a lambda in map()/forEach() cannot let a checked exception escape
list.stream().map(k -> load(k));                // load() throws IOException
// ✅ catch inside the lambda and rethrow an unchecked wrapper
list.stream().map(k -> { try { return load(k); } catch (IOException e) { throw new UncheckedIOException(e); } });

// ❌ copying only the message throws the cause and its stack trace away
throw new IllegalStateException(e.getMessage());
// ✅ pass the original exception as the cause
throw new IllegalStateException("storage failed", e);

// ❌ try/finally: an exception from close() REPLACES the one from the body
try { use(r); } finally { r.close(); }
// ✅ try-with-resources keeps the body exception and attaches close failures via getSuppressed()
try (Resource r = open()) { use(r); }

// ✅ resources close in REVERSE order, and BEFORE the catch/finally blocks run
try (A a = open(); B b = open()) { ... } catch (...) { /* a and b are already closed here */ }

// ✅ a null resource is skipped on close — no NullPointerException
try (Resource r = maybeNull()) { ... }

// ✅ custom exceptions: checked when the caller can recover, unchecked for contract violations;
//    carry structured data and always provide a (message, cause) constructor
class InsufficientFundsException extends Exception { long getShortfall() { ... } }
class InvalidAmountException extends IllegalArgumentException { ... }
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run a main demo
mvn exec:java -Dexec.mainClass="com.vbforge.exception_handling.TryCatchFinallyDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.exception_handling.CheckedVsUncheckedDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.exception_handling.CustomExceptionsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.exception_handling.TryWithResourcesDemo"
```
