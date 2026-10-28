# logging-basics

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 8+ | Package: `com.vbforge.logging_basics`

## What's covered

| Topic | Key points |
|---|---|
| SLF4J API | `LoggerFactory.getLogger`, one `private static final` logger per class, levels `TRACE < DEBUG < INFO < WARN < ERROR`, `{}` placeholders, throwable as last argument, `isDebugEnabled`, fluent `atDebug().log(() -> …)` |
| Logback configuration | `logback.xml` vs `logback-test.xml`, logger hierarchy, `getLevel` vs `getEffectiveLevel`, `Level.toLevel(text, null)`, appenders, additivity, `PatternLayout`, `ThresholdFilter` |
| Structured logging | `MDC.put/remove/clear`, `MDC.putCloseable`, key-value pairs via `addKeyValue`, `%kvp`, `JsonEncoder`, `Marker` / `MarkerFactory` |
| Best practices | Choosing levels, keeping secrets out of logs, log **or** throw, wrap with cause, summaries instead of per-item floods |

## Files

```
src/
├── main/
│   ├── java/com/vbforge/logging_basics/
│   │   ├── Slf4jBasicsDemo.java                ← theory + annotated examples (SLF4J API)
│   │   ├── Slf4jBasicsTasks.java               ← 🎯 your tasks — implement these methods
│   │   ├── LogbackConfigurationDemo.java       ← theory + annotated examples (Logback setup)
│   │   ├── LogbackConfigurationTasks.java      ← 🎯 your tasks — implement these methods
│   │   ├── StructuredLoggingDemo.java          ← theory + annotated examples (MDC, JSON, markers)
│   │   ├── StructuredLoggingTasks.java         ← 🎯 your tasks — implement these methods
│   │   ├── LoggingBestPracticesDemo.java       ← theory + annotated examples (habits)
│   │   └── LoggingBestPracticesTasks.java      ← 🎯 your tasks — implement these methods
│   └── resources/
│       └── logback.xml                         ← default configuration (console, root INFO)
└── test/java/com/vbforge/logging_basics/
    ├── LogCapture.java                         ← helper: records log events in memory for assertions
    ├── Slf4jBasicsDemoTest.java                ← tests for the demo (already green)
    ├── Slf4jBasicsTasksTest.java               ← 🧪 tests for your tasks — all must pass
    ├── LogbackConfigurationDemoTest.java       ← tests for the demo (already green)
    ├── LogbackConfigurationTasksTest.java      ← 🧪 tests for your tasks — all must pass
    ├── StructuredLoggingDemoTest.java          ← tests for the demo (already green)
    ├── StructuredLoggingTasksTest.java         ← 🧪 tests for your tasks — all must pass
    ├── LoggingBestPracticesDemoTest.java       ← tests for the demo (already green)
    └── LoggingBestPracticesTasksTest.java      ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement its methods. Each has a detailed Javadoc.
Task tests assert on **what was logged** (level, message, attached exception, MDC, markers),
using the in-memory `LogCapture` helper — so you never need to read console output.

| # | Class | Method | Topic |
|---|---|---|---|
| 1 | `Slf4jBasicsTasks` | `logLogin(log, user, attempts)` | `{}` placeholders instead of concatenation |
| 2 | `Slf4jBasicsTasks` | `logFailure(log, operation, e)` | Throwable as the last argument keeps the stack trace |
| 3 | `Slf4jBasicsTasks` | `logExpensiveIfDebug(log, expensive)` | Guard / lazy evaluation of costly arguments |
| 4 | `Slf4jBasicsTasks` | `logAtLevel(log, levelName, message)` | Dynamic level via the SLF4J 2.x fluent API |
| 5 | `LogbackConfigurationTasks` | `setLevel(context, loggerName, levelName)` | Programmatic levels, safe level parsing |
| 6 | `LogbackConfigurationTasks` | `attachListAppender(context, loggerName)` | Creating, starting and attaching an appender |
| 7 | `LogbackConfigurationTasks` | `formatWithPattern(context, event, pattern)` | `PatternLayout` |
| 8 | `LogbackConfigurationTasks` | `addThreshold(appender, levelName)` | `ThresholdFilter` per appender |
| 9 | `LogbackConfigurationTasks` | `isolateLogger(context, loggerName, appender)` | Additivity |
| 10 | `StructuredLoggingTasks` | `runWithContext(requestId, userId, action)` | MDC set + guaranteed cleanup |
| 11 | `StructuredLoggingTasks` | `toLogfmt(fields)` | `key=value` rendering with quoting rules |
| 12 | `StructuredLoggingTasks` | `logOrderPlaced(log, orderId, itemCount)` | Key-value pairs (`addKeyValue`) |
| 13 | `StructuredLoggingTasks` | `escapeJson(raw)` | JSON string escaping |
| 14 | `StructuredLoggingTasks` | `logAudit(log, action, user)` | Markers |
| 15 | `LoggingBestPracticesTasks` | `maskCardNumber(cardNumber)` | Masking sensitive data |
| 16 | `LoggingBestPracticesTasks` | `maskEmail(email)` | Masking sensitive data |
| 17 | `LoggingBestPracticesTasks` | `redactSecrets(text)` | Regex redaction of `key=value` secrets |
| 18 | `LoggingBestPracticesTasks` | `logProgress(log, processed, total, every)` | Sparing, summary-style logging |
| 19 | `LoggingBestPracticesTasks` | `handleAtBoundary(log, work)` | Log once, at the boundary |

```bash
mvn test -Dtest=Slf4jBasicsTasksTest
mvn test -Dtest=LogbackConfigurationTasksTest
mvn test -Dtest=StructuredLoggingTasksTest
mvn test -Dtest=LoggingBestPracticesTasksTest
```

All 19 task groups must go **GREEN** ✅ (remove `@Disabled` from a `@Nested` class when you start on its task).

## Key things to remember

```java
// One logger per class: private static final, named after the class
private static final Logger log = LoggerFactory.getLogger(MyService.class);   // ✅
System.out.println("started");                                                // ❌ no level, no config, no context
e.printStackTrace();                                                          // ❌ bypasses logging entirely

// Placeholders are {} — not %s, not {0}; the String is built only if the level is enabled
log.info("User {} logged in after {} attempt(s)", user, attempts);            // ✅
log.info("User " + user + " logged in");                                      // ❌ concatenates even when INFO is off

// The exception goes LAST and does not need a placeholder
log.error("Cannot parse '{}'", input, e);                                     // ✅ message + stack trace
log.error("Cannot parse: " + e.getMessage());                                 // ❌ the stack trace is gone

// Placeholders defer formatting, NOT argument evaluation
log.debug("dump={}", buildHugeDump());                                        // ❌ buildHugeDump() always runs
log.atDebug().log(() -> buildHugeDump());                                     // ✅ runs only if DEBUG is on (SLF4J 2.x)
if (log.isDebugEnabled()) { log.debug("dump={}", buildHugeDump()); }          // ✅ the classic guard

// Level.valueOf / toLevel(String) silently turn typos into DEBUG
Level.valueOf("WARNING");           // ❌ DEBUG — your service becomes a firehose, no error
Level.toLevel("WARNING", null);     // ✅ null — you can reject it

// getLevel() is the level set on THIS logger (often null); getEffectiveLevel() is what applies
logger.getLevel();                  // null if inherited
logger.getEffectiveLevel();         // never null

// Additivity: an event also goes to every ancestor's appenders
child.addAppender(consoleAppender); // ❌ if root already has the same appender → every line twice
child.setAdditive(false);           // ✅ when the child must NOT also reach the root appenders

// Components must be started — an unstarted filter/appender/layout is silently inert
ThresholdFilter filter = new ThresholdFilter();
filter.setLevel("WARN");
filter.start();                     // ✅ forget this and EVERYTHING passes, with no warning

// MDC is per thread and lives until removed — always clean up
try (MDC.MDCCloseable ignored = MDC.putCloseable("requestId", id)) {         // ✅ removed automatically
    handle(request);
}
MDC.put("requestId", id); handle(request);                                    // ❌ leaks into the next request on this pooled thread

// Facts of ONE event → key-value pairs; context of MANY events → MDC
log.atInfo().addKeyValue("orderId", id).log("Order placed");                  // ✅ constant message, searchable field

// Log OR throw — never both
catch (IOException e) { log.error("failed", e); throw new UncheckedIOException(e); }       // ❌ same failure logged at every layer
catch (IOException e) { throw new UncheckedIOException("Cannot load " + path, e); }        // ✅ add context, keep the cause, log once at the boundary

// Secrets: make the safe output the default
log.info("Login attempt: {}", request);   // ❌ if request.toString() prints the password
// ✅ give such classes a toString() that omits the secret, and mask what you must show

// Logback ignores logback.xml if a logback-test.xml is on the classpath
// → when tests log differently from the app, look for that file first
```

> **Version note:** this module uses the SLF4J **2.x** API (fluent API, key-value pairs) with Logback **1.5.x**
> (`JsonEncoder`, `%kvp`) — both are managed by the parent `pom.xml`. The source code itself is plain Java 8 syntax;
> Logback 1.5 needs a Java 11+ runtime, which the repository's Java 17 baseline provides.

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run a main demo (one per concept)
mvn exec:java -Dexec.mainClass="com.vbforge.logging_basics.Slf4jBasicsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.logging_basics.LogbackConfigurationDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.logging_basics.StructuredLoggingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.logging_basics.LoggingBestPracticesDemo"
```
