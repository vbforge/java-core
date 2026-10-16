# sealed-classes-pattern-matching

> **Phase 2 — Core OOP & Design** | Java 17–21+ | Package: `com.vbforge.sealed_classes_pattern_matching`

> ⚠️ **Java 21 required to build** — record patterns and pattern matching for `switch` are final in 21.
> The module `pom.xml` overrides the parent's Java 17 default with `<java.version>21</java.version>`.
> (Sealed types alone need only 17, `instanceof` patterns 16.)

## What's covered

| Topic | Key points |
|---|---|
| `sealed` / `permits` | Closed hierarchy; every permitted subtype must be `final`, `sealed` or `non-sealed` |
| Sealed classes, sub-sealed | `abstract sealed class` with a `sealed` branch; `Class#isSealed`, `Class#getPermittedSubclasses` |
| Implicit `permits` | Omit `permits` when all subtypes are in the same file; records are implicitly `final` |
| Exhaustive `switch` | No `default` needed over a sealed type — adding a subtype breaks the build, on purpose |
| `instanceof` patterns | `obj instanceof String s`; binding scope with `&&` and negated early return; compact `equals` |
| `switch` type patterns | `case Integer i ->`, `case int[] arr ->`; pattern switch over `Object` needs `default` |
| Guards | `case Integer i when i < 0 ->`; guarded / specific cases go before general ones (dominance) |
| `null` in `switch` | No `case null` → `NullPointerException`; `default` does not match null; `case null, default` |
| Record patterns | `obj instanceof Point(int x, int y)`; nesting; `var` components; nested pattern never matches `null` |
| Sealed + records (ADT) | `sealed interface Expr` + records → exhaustive `eval` / `show` / `simplify` |
| Generic records | `Result<T>` = `Success<T>` / `Failure<T>`; `case Success<T>(var v) ->` |

## Files

```
src/
├── main/java/com/vbforge/sealed_classes_pattern_matching/
│   ├── SealedTypesDemo.java          ← theory + annotated examples: sealed, permits, exhaustive switch
│   ├── SealedTypesTasks.java         ← 🎯 your tasks — implement these methods
│   ├── PatternMatchingDemo.java      ← theory: instanceof / switch patterns, guards, null
│   ├── PatternMatchingTasks.java     ← 🎯 your tasks — implement these methods
│   ├── RecordPatternsDemo.java       ← theory: record patterns, sealed + records, Result<T>
│   └── RecordPatternsTasks.java      ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/sealed_classes_pattern_matching/
    ├── SealedTypesDemoTest.java       ← tests for the demo
    ├── SealedTypesTasksTest.java      ← 🧪 tests for your tasks — remove @Disabled one nested class at a time
    ├── PatternMatchingDemoTest.java   ← tests for the demo
    ├── PatternMatchingTasksTest.java  ← 🧪 tests for your tasks — remove @Disabled one nested class at a time
    ├── RecordPatternsDemoTest.java    ← tests for the demo
    └── RecordPatternsTasksTest.java   ← 🧪 tests for your tasks — remove @Disabled one nested class at a time
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `SealedTypesTasks.shapeName(shape)` | Exhaustive `switch` over a sealed interface, no `default` |
| 2 | `SealedTypesTasks.vehicleKind(vehicle)` | Exhaustiveness across a two-level (sub-sealed) hierarchy |
| 3 | `SealedTypesTasks.countLeafTypes(type)` | `isSealed` / `getPermittedSubclasses` + recursion |
| 4 | `PatternMatchingTasks.parseToInt(value)` | Type patterns, `Math.toIntExact`, error cases |
| 5 | `PatternMatchingTasks.sizeLabel(obj)` | Pattern `switch` with `when` guards and `case null` |
| 6 | `PatternMatchingTasks.looselyEquals(a, b)` | Two `instanceof` patterns chained with `&&` |
| 7 | `PatternMatchingTasks.collectStrings(items)` | Binding reused inside the same condition |
| 8 | `RecordPatternsTasks.countNodes(expr)` | Record patterns in an exhaustive recursive `switch` |
| 9 | `RecordPatternsTasks.toPostfix(expr)` | Tree traversal with record patterns |
| 10 | `RecordPatternsTasks.lineKind(obj)` | Nested record patterns and `null` components |
| 11 | `RecordPatternsTasks.unwrapOr(result, fallback)` | Generic record patterns over `Result<T>` |
| 12 | `RecordPatternsTasks.mapResult(result, mapper)` | Rebuilding a generic sealed value with a new type argument |

Run your solutions with:
```bash
mvn test -Dtest=SealedTypesTasksTest
mvn test -Dtest=PatternMatchingTasksTest
mvn test -Dtest=RecordPatternsTasksTest
```
Every nested test class ships with `@Disabled("Remove @Disabled when you implement <method>()")` — delete the annotation on one class, implement the method, and watch it go **GREEN** ✅

## Key things to remember

```java
// ✅ Every permitted subtype must choose final / sealed / non-sealed
public sealed interface Shape permits Circle, Square, Polygon { }
public static final class Circle implements Shape { }       // closed leaf
public static non-sealed class Polygon implements Shape { } // deliberately re-opened
// ❌ a permitted subtype with no modifier does not compile
```

```java
// ✅ No default: the compiler proves every subtype is handled
double area = switch (shape) {
    case Circle c  -> Math.PI * c.radius() * c.radius();
    case Square s  -> s.side() * s.side();
    case Polygon p -> p.area();
};
// ❌ A "defensive" default hides the error when a new subtype is added —
//    the new type silently takes the default branch instead of failing the build
```

```java
// ✅ Specific / guarded cases first, general case last
switch (obj) {
    case Integer i when i < 0 -> "negative";
    case Integer i            -> "other int";
    default                   -> "?";
}
// ❌ 'case Integer i' placed BEFORE 'case Integer i when ...' does not compile (dominated)
```

```java
// ❌ switch (null) throws NullPointerException unless you handle it
String r = switch (obj) { case String s -> "s"; default -> "x"; };   // obj == null → NPE
// ✅ 'default' does NOT match null — use 'case null, default' to cover both
String r = switch (obj) { case String s -> "s"; case null, default -> "x"; };
```

```java
// ✅ Negated check + early exit keeps the binding in scope AFTER the if
if (!(obj instanceof String s)) { return -1; }
return s.length();
// ❌ Using 's' where the match is not proven (e.g. after '||') does not compile
```

```java
// ✅ A NESTED record pattern never matches null; 'var' / a total pattern does
case Line(Point(var x, var y), var to) -> "has start";   // from == null → skipped
case Line(var from, var to)            -> "no start";    // from == null → matches
// ❌ Assuming Line(Point(...), ...) also covers a Line whose 'from' is null
```

```java
// ✅ Records are implicitly final — ideal permitted subtypes
sealed interface Expr permits Num, Add { }
record Num(int value) implements Expr { }
// ❌ Sealed permitted types must be in the same module (or same package in the unnamed module)
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=SealedTypesDemoTest

# Task tests only
mvn test -Dtest=SealedTypesTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.sealed_classes_pattern_matching.SealedTypesDemo"
```

The other two pairs run the same way — swap in `PatternMatchingDemoTest` / `PatternMatchingTasksTest` / `PatternMatchingDemo`
or `RecordPatternsDemoTest` / `RecordPatternsTasksTest` / `RecordPatternsDemo`.
