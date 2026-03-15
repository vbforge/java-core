# basics-variables-and-types

> **Phase 1 — Basics** | Java 8–17+ | Package: `com.vbforge.basics.variables`

## What's covered

| Topic | Key points |
|---|---|
| Primitive types | 8 types, sizes, ranges, default values |
| Numeric literals | Hex (`0xFF`), binary (`0b1111`), octal (`0377`), underscores (`1_000_000`) |
| Type casting | Widening (implicit) vs narrowing (explicit, may lose data) |
| Wrapper types | `Integer`, `Double`, etc. — autoboxing/unboxing |
| Integer cache | `-128..127` are cached — `==` trap outside this range |
| Variable scope | Class, instance, local, block |
| `final` variables | Constant reference — primitives are truly immutable |
| `var` (Java 10+) | Local type inference — still statically typed |

## Files

```
src/
├── main/java/com/vbforge/basics/variables/
│   ├── VariablesDemo.java        ← theory + annotated examples
│   └── VariablesTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/basics/variables/
    ├── VariablesDemoTest.java     ← tests for the demo
    └── VariablesTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `VariablesTasks.java` and implement all 5 methods. Each method has a detailed
Javadoc explaining exactly what it should do.

| # | Method | Topic |
|---|---|---|
| 1 | `primitiveSize(typeName)` | Know the byte size of each primitive type |
| 2 | `safeNarrow(double)` | Narrowing cast with boundary clamping |
| 3 | `isInIntegerCacheRange(Integer)` | Integer cache range -128..127 |
| 4 | `parseOrDefault(String, int)` | Null-safe string to int parsing |
| 5 | `shiftChar(char, int)` | Char casting + arithmetic + wrap-around |

Run your solutions with:
```bash
mvn test -Dtest=VariablesTasksTest
```
All 5 task groups must go **GREEN** ✅

## Key things to remember

```java
// Integer cache trap — always use .equals() for objects!
Integer a = 127;  Integer b = 127;  a == b  // true  (cached)
Integer x = 128;  Integer y = 128;  x == y  // false (NOT cached)
x.equals(y)                                  // true  ← always safe

// Narrowing truncates — does NOT round
(int) 3.99  // → 3
(int) -3.99 // → -3  (toward zero)

// var is compile-time — type is FIXED
var list = new ArrayList<String>();  // inferred as ArrayList<String>
// list = "hello";                   // compile error — type is fixed
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=VariablesDemoTest

# Task tests only
mvn test -Dtest=VariablesTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.variables.VariablesDemo"
```
