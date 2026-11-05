# jit-and-warmup

> **Phase 7 — JVM Internals** | Java 8+ | Package: `com.vbforge.jit_and_warmup`

Tags: `JVM`, `PERFORMANCE`

Interpreter → C1 → C2, inlining, escape analysis, warm-up, first look at JMH.

## What's covered

| Topic | Key points |
|---|---|
| Tiered compilation | Tiers 0–4 (interpreter, C1 ×3, C2), typical path `0 → 3 → 4`, `-XX:+PrintCompilation`, `-XX:TieredStopAtLevel=1` |
| Warm-up curve | First batches slow, later batches fast; separate warm-up from steady state; `System.nanoTime` |
| On-stack replacement (OSR) | Hot loop in a once-called method is compiled mid-run; `%` marker in `PrintCompilation` |
| JIT introspection | `CompilationMXBean`, `-Xint`, `-Xcomp`, `-XX:-TieredCompilation` |
| Inlining | Callee body replaces the call; enabler for other optimisations; `MaxInlineSize=35`, `FreqInlineSize=325`, `HugeMethodLimit=8000` bytecode bytes |
| Call-site shapes | Monomorphic / bimorphic / megamorphic virtual calls, class-hierarchy analysis, deoptimisation, profile pollution |
| Escape analysis | `NoEscape` / `ArgEscape` / `GlobalEscape`, scalar replacement, C2 only, `-XX:-DoEscapeAnalysis` |
| Lock elision | `synchronized` / `StringBuffer` on a non-escaping object is removed by C2 |
| Microbenchmark pitfalls | Naive stopwatch, dead-code elimination, constant folding, `Blackhole`-style sink |
| JMH first look | `@Benchmark`, `@State`, `@Warmup`, `@Measurement`, `@Fork`, `@Param`, `Blackhole` |

## Files

```
src/
├── main/java/com/vbforge/jit_and_warmup/
│   ├── TieredCompilationDemo.java               ← annotated reference: tiers, warm-up curve, OSR, JIT flags
│   ├── TieredCompilationTasks.java              ← 🎯 your tasks (tier model, median, warm-up index)
│   ├── InliningAndEscapeAnalysisDemo.java       ← annotated reference: inlining, call sites, escape analysis, lock elision
│   ├── InliningAndEscapeAnalysisTasks.java      ← 🎯 your tasks (inline limits, call-site kind, escape state, no-allocation code)
│   ├── MicrobenchmarkDemo.java                  ← annotated reference: stopwatch traps, dead code, mini harness, JMH listing
│   └── MicrobenchmarkTasks.java                 ← 🎯 your tasks (trimmed mean, throughput, coefficient of variation)
└── test/java/com/vbforge/jit_and_warmup/
    ├── TieredCompilationDemoTest.java           ← 🧪 tests (already green)
    ├── TieredCompilationTasksTest.java          ← 🧪 tests for your implementations (start @Disabled)
    ├── InliningAndEscapeAnalysisDemoTest.java   ← 🧪 tests (already green)
    ├── InliningAndEscapeAnalysisTasksTest.java  ← 🧪 tests for your implementations (start @Disabled)
    ├── MicrobenchmarkDemoTest.java              ← 🧪 tests (already green)
    └── MicrobenchmarkTasksTest.java             ← 🧪 tests for your implementations (start @Disabled)
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `TieredCompilationTasks.tierForInvocations` | Simplified tier model: 200 / 5000 invocation thresholds |
| 2 | `TieredCompilationTasks.median` | Outlier-resistant statistic, copy before sorting |
| 3 | `TieredCompilationTasks.findWarmupIndex` | Where does warm-up end in a timing series? |
| 4 | `InliningAndEscapeAnalysisTasks.isInlineCandidate` | `MaxInlineSize` vs `FreqInlineSize` |
| 5 | `InliningAndEscapeAnalysisTasks.callSiteKind` | Mono-, bi- and megamorphic call sites |
| 6 | `InliningAndEscapeAnalysisTasks.escapeState` | `NoEscape` / `ArgEscape` / `GlobalEscape` |
| 7 | `InliningAndEscapeAnalysisTasks.distanceSquaredSum` | Allocation-free code with `long` arithmetic |
| 8 | `MicrobenchmarkTasks.trimmedMean` | Dropping outliers from benchmark samples |
| 9 | `MicrobenchmarkTasks.opsPerSecond` | Nanoseconds → throughput |
| 10 | `MicrobenchmarkTasks.coefficientOfVariation` | Is the measurement stable? |

```bash
mvn test -Dtest=TieredCompilationTasksTest
mvn test -Dtest=InliningAndEscapeAnalysisTasksTest
mvn test -Dtest=MicrobenchmarkTasksTest
```

Each task lives in a `@Nested` class marked `@Disabled("Remove @Disabled when you implement …()")`.
Delete that one annotation, implement the method, and run the test class.

## Key things to remember

### 1. A single timing of Java code means nothing

```java
// ❌ measures interpreter + compilation + one lucky run
long start = System.nanoTime();
work();
System.out.println(System.nanoTime() - start);

// ✅ warm up first (results discarded), then measure many iterations, report median
long[] samples = MicrobenchmarkDemo.runHarness(() -> work(), 200, 20);
// ...and for real decisions use JMH, which also forks a fresh JVM
```

### 2. Discarded results can be optimised away (dead-code elimination)

```java
// ❌ the JIT may delete the whole computation: "0 ns per operation"
static void bench() { squareSum(100_000); }

// ✅ consume the result: return it from a JMH @Benchmark, use a Blackhole,
//    or (by hand) write it to a volatile field
static long bench() { return squareSum(100_000); }
```

### 3. One loop in `main` is not how your code will really run

```java
// ❌ a method called once with a hot loop is compiled by on-stack replacement (OSR) mid-run;
//    OSR code is often less optimised than a normal compile
public static void main(String[] args) { for (long i = 0; i < 1_000_000_000L; i++) { ... } }

// ✅ put the work in a method and call it many times
```

### 4. Inlining limits are bytecode bytes, not lines

```java
// ≤ 35 bytecode bytes → inlined if executed at all   (-XX:MaxInlineSize)
// ≤ 325 bytes         → inlined only at HOT call sites (-XX:FreqInlineSize)
// ≥ 8000 bytes        → never compiled; stays in the interpreter (HugeMethodLimit)
// ✅ keep hot methods small; check real sizes with `javap -c`
// ✅ see decisions with: -XX:+UnlockDiagnosticVMOptions -XX:+PrintInlining
```

### 5. Megamorphic call sites cannot be inlined — and profiles get polluted

```java
// ✅ 1 receiver type → inlined behind a cheap type check (≈ direct call)
// ✅ 2 receiver types → both inlined
// ❌ 3+ receiver types → real virtual dispatch, nothing is inlined
for (Op op : ops) acc += op.apply(i);   // one call site, shared by every caller

// ❌ running benchmark A then B in the same JVM: B inherits A's type profile
// ✅ JMH @Fork gives every benchmark a fresh JVM
```

### 6. Escape analysis is a bonus, not a contract

```java
// ✅ NoEscape: C2 removes the allocation (scalar replacement) once minus() is inlined
long d = new Point(x1, y1).minus(new Point(x2, y2)).lengthSquared();

// ❌ stored in a static / returned → GlobalEscape: always a real heap object
escaped = new Point(x, y);

// ❌ only C2 does it: the interpreter and C1 still allocate; don't design code that *needs* it
// ✅ StringBuilder over a local StringBuffer — lock elision works, but only after warm-up
```

### 7. Deoptimisation is normal

```java
// The JIT speculates (e.g. "only one implementation is loaded").
// If the speculation breaks, compiled code is discarded and execution falls back to the interpreter.
// ✅ -XX:+PrintCompilation shows "made not entrant" lines — a brief slowdown is expected, not a bug
```

### Adding JMH to a project (not used in this module)

This module stays dependency-free, so `MicrobenchmarkDemo` prints the benchmark source instead of running it.
In your own project:

```xml
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-core</artifactId>
    <version>1.37</version>
</dependency>
<dependency>
    <groupId>org.openjdk.jmh</groupId>
    <artifactId>jmh-generator-annprocess</artifactId>
    <version>1.37</version>
    <scope>provided</scope>
</dependency>
```

Build a runnable benchmarks jar (the JMH archetype does this for you) and run `java -jar target/benchmarks.jar`.

## Run

```bash
# Demo tests + (skipped) task tests
mvn test

# Only the demo tests
mvn test -Dtest=TieredCompilationDemoTest
mvn test -Dtest=InliningAndEscapeAnalysisDemoTest
mvn test -Dtest=MicrobenchmarkDemoTest

# Only your task tests
mvn test -Dtest=TieredCompilationTasksTest
mvn test -Dtest=InliningAndEscapeAnalysisTasksTest
mvn test -Dtest=MicrobenchmarkTasksTest

# Run a demo
mvn exec:java -Dexec.mainClass="com.vbforge.jit_and_warmup.TieredCompilationDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.jit_and_warmup.InliningAndEscapeAnalysisDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.jit_and_warmup.MicrobenchmarkDemo"
```
