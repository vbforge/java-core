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
│   └── VariablesDemo.java      ← theory + annotated examples
└── test/java/com/vbforge/basics/variables/
    └── VariablesDemoTest.java  ← JUnit 5 + AssertJ tests
```

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
# From module directory
mvn test

# Single test class
mvn test -Dtest=VariablesDemoTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.variables.VariablesDemo"
```
