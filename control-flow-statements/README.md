# control-flow-statements

> **Phase 1 — Basics** | Java 21+ | Package: `com.vbforge.basics.controlflow`

## What's covered

| Topic | Key points |
|---|---|
| `if / else if / else` | Condition must be `boolean`, chains evaluate top-down, dangling-else trap |
| Classic `switch` | Fall-through behaviour, `break`, `default`, works on int/String/enum |
| Switch expression (Java 14+) | Arrow syntax `->`, `yield`, no fall-through, returns a value |
| `instanceof` pattern matching (Java 16+) | Combines type check + binding variable in one expression |
| Switch pattern matching (Java 21+) | Type patterns + `when` guards in switch expressions |
| `for` loop | Classic indexed, reverse iteration |
| Enhanced `for` (for-each) | Arrays and `Iterable` — no index access |
| `while` loop | Condition checked before — may run 0 times |
| `do-while` loop | Condition checked after — runs at least once |
| `break` / `continue` | Exit loop early vs skip current iteration |
| Labeled `break` / `continue` | Target an outer loop from nested context |

> ⚠️ **Java 21 required** — this module uses switch pattern matching with `when` guards.
> The module `pom.xml` overrides the parent's Java 17 default with `<java.version>21</java.version>`.

## Files

```
src/
├── main/java/com/vbforge/basics/controlflow/
│   ├── ControlFlowDemo.java        ← theory + annotated examples
│   └── ControlFlowTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/basics/controlflow/
    ├── ControlFlowDemoTest.java     ← tests for the demo
    └── ControlFlowTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `ControlFlowTasks.java` and implement all 6 methods. Each method has a detailed
Javadoc explaining exactly what it should do.

| # | Method | Topic |
|---|---|---|
| 1 | `httpCategory(statusCode)` | Switch expression with multiple case labels |
| 2 | `letterGrade(score)` | if-else chain with boundary validation |
| 3 | `sumRange(start, end)` | `for` loop with range guard |
| 4 | `floorPowerOfTwo(n)` | `while` loop — keep doubling until threshold |
| 5 | `filterPositive(numbers)` | Loop + `continue` to skip unwanted elements |
| 6 | `formatTyped(Object)` | Pattern matching `instanceof` or switch patterns |

Run your solutions with:
```bash
mvn test -Dtest=ControlFlowTasksTest
```
All 6 task groups must go **GREEN** ✅

## Key things to remember

```java
// Switch expression — no fall-through, returns a value directly
String season = switch (month) {
    case 12, 1, 2 -> "winter";
    case 3, 4, 5  -> "spring";
    default       -> throw new IllegalArgumentException();
};

// yield — needed when case block has multiple statements
int days = switch (month) {
    case 2 -> {
        boolean isLeap = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
        yield isLeap ? 29 : 28;
    }
    default -> 31;
};

// Pattern matching instanceof — no explicit cast needed
if (obj instanceof String s) {
    System.out.println(s.length());
}

// Switch pattern matching with guard (Java 21+)
return switch (obj) {
    case Integer i when i < 0 -> "negative";
    case Integer i            -> "non-negative";
    case null                 -> "null";
    default                   -> "other";
};

// do-while — always runs at least once
do {
    n = (n % 2 == 0) ? n / 2 : 3 * n + 1;
} while (n != 1);
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=ControlFlowDemoTest

# Task tests only
mvn test -Dtest=ControlFlowTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.controlflow.ControlFlowDemo"
```
