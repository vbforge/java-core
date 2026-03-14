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
│   └── OperatorsDemo.java      ← theory + annotated examples
└── test/java/com/vbforge/basics/operators/
    └── OperatorsDemoTest.java  ← JUnit 5 + AssertJ tests
```

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
2 + 3 * 4           // → 14  (not 20)
true || false && false  // → true  (false && false evaluated first)

// Bitwise trick: odd/even check (faster than % 2)
n & 1 == 1  // odd
n & 1 == 0  // even
```

## Run

```bash
# From module directory
mvn test

# Single test class
mvn test -Dtest=OperatorsDemoTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.operators.OperatorsDemo"
```
