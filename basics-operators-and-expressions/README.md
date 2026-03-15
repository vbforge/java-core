# basics-operators-and-expressions

> **Phase 1 — Basics** | Java 8 | Package: `com.vbforge.basics.operators`

## What's covered

| Topic | Key points |
|---|---|
| Arithmetic | `+ - * / %` — integer division truncates, remainder sign follows dividend |
| Increment/Decrement | Pre (`++x`) vs post (`x++`) — when the value is returned matters |
| Compound assignment | `+=` `-=` `*=` `/=` `%=` — includes implicit narrowing cast |
| Comparison | `== != < > <= >=` — primitives by value, objects by reference! |
| Logical | `&&` `\|\|` `!` `^` — short-circuit vs non-short-circuit |
| Bitwise | `& \| ^ ~ << >> >>>` — operate on individual bits |
| Ternary | `condition ? a : b` — compact conditional expression |
| Precedence | `*` before `+`, `&&` before `\|\|`, parentheses always win |

## Files

```
src/
├── main/java/com/vbforge/basics/operators/
│   ├── OperatorsDemo.java        ← theory + annotated examples
│   └── OperatorsTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/basics/operators/
    ├── OperatorsDemoTest.java     ← tests for the demo
    └── OperatorsTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `OperatorsTasks.java` and implement all 6 methods. Each method has a detailed
Javadoc explaining exactly what it should do.

| # | Method | Topic |
|---|---|---|
| 1 | `isDivisibleByBoth(n, a, b)` | Remainder operator `%` |
| 2 | `intAverage(a, b, c)` | Integer arithmetic without floating-point |
| 3 | `absoluteValue(n)` | Ternary operator — no `if` or `Math.abs` allowed |
| 4 | `isPowerOfTwo(n)` | Bitwise trick: `n & (n-1)` |
| 5 | `swapWithoutTemp(a, b)` | Arithmetic or XOR swap — no temp variable |
| 6 | `powerOfTwoShift(exp)` | Left-shift operator `<<` only |

Run your solutions with:
```bash
mvn test -Dtest=OperatorsTasksTest
```
All 6 task groups must go **GREEN** ✅

## Key things to remember

```java
// Integer division always truncates
5 / 2       // → 2  (not 2.5!)
(double)5/2 // → 2.5

// Remainder sign follows the DIVIDEND (left side)
-7 % 3  // → -1  (not 2!)
 7 % -3 // →  1  (not -2!)

// Short-circuit: right side may NOT execute
false && (++counter > 0)  // counter never incremented
true  || (++counter > 0)  // counter never incremented

// Precedence surprises
2 + 3 * 4               // → 14  (not 20)
true || false && false  // → true  (false && false evaluated first)

// Bitwise trick: odd/even check
n & 1 == 1  // odd
n & 1 == 0  // even
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=OperatorsDemoTest

# Task tests only
mvn test -Dtest=OperatorsTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.operators.OperatorsDemo"
```
