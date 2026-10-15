# enums-and-nested-classes

> **Phase 2 — Core OOP & Design** | Java 8 | Tags: OOP, ADVANCED | Package: `com.vbforge.enums_and_nested_classes`

## What's covered

| Topic | Key points |
|---|---|
| Enum basics | `values()`, `valueOf`, `name()`, `ordinal()`, `compareTo`, `==` is safe, classic `switch` on an enum |
| Enums with fields + methods | private constructor, `final` fields, static lookup map filled in a `static` block, `Optional` instead of a throwing `valueOf` |
| Constant-specific bodies | `abstract` method in the enum, one body per constant, state machine (`next()`), `getClass()` vs `getDeclaringClass()` |
| Enums implementing interfaces | enums can `implements` but never `extends`, strategy per constant |
| `EnumMap` / `EnumSet` | array / bit-mask backed, O(1), declaration order, `range`, `complementOf`, `allOf`, `noneOf`, no `null` |
| Enum singleton | one instance guaranteed, reflection blocked (`IllegalArgumentException`), shared mutable state caveat |
| Static nested classes | namespacing, Builder pattern, private access in both directions, no outer reference |
| Inner classes | bound to an outer instance, iterator over outer state, hidden outer reference (leak risk) |
| `Outer.this` / `outer.new Inner()` | disambiguate shadowed names, create inner objects from outside |
| Local classes | declared in a method, capture effectively-final locals (Java 8) |
| Telling the kinds apart | `isMemberClass`, `isLocalClass`, `isAnonymousClass`, `Modifier.isStatic`, binary names with `$` |
| Anonymous classes | implement an interface, extend a class with constructor args, own mutable fields, multi-method callbacks |
| `this`: anonymous vs lambda | anonymous `this` = the anonymous object (empty `getSimpleName()`), lambda `this` = enclosing object |
| Double-brace initialisation | anonymous subclass of `ArrayList`, extra class per use, breaks `getClass()` checks |

> ℹ️ The source is valid **Java 8**. The module inherits the parent's Java 17 build default — no override needed.

## Files

```
src/
├── main/java/com/vbforge/enums_and_nested_classes/
│   ├── EnumDemo.java               ← theory + annotated examples (enums)
│   ├── EnumTasks.java              ← 🎯 your tasks — implement these methods
│   ├── NestedClassDemo.java        ← theory + annotated examples (static nested, inner, local)
│   ├── NestedClassTasks.java       ← 🎯 your tasks — implement these methods
│   ├── AnonymousClassDemo.java     ← theory + annotated examples (anonymous classes)
│   └── AnonymousClassTasks.java    ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/enums_and_nested_classes/
    ├── EnumDemoTest.java               ← tests for the enum demo
    ├── EnumTasksTest.java              ← 🧪 tests for your tasks — all must pass
    ├── NestedClassDemoTest.java        ← tests for the nested-class demo
    ├── NestedClassTasksTest.java       ← 🧪 tests for your tasks — all must pass
    ├── AnonymousClassDemoTest.java     ← tests for the anonymous-class demo
    └── AnonymousClassTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open a `*Tasks.java` file and implement every method that throws `UnsupportedOperationException`.
Each method has a Javadoc with examples and hints. Tasks marked *(inside …)* live in a nested type of the Tasks class.

| # | Method | Topic |
|---|---|---|
| 1 | `EnumTasks.parseLevel(text)` | Safe enum parsing — case, whitespace, `Locale.ROOT`, fallback value |
| 2 | `EnumTasks.nextLevel(current)` | `ordinal()` + `values()` with wrap-around |
| 3 | `EnumTasks.countByLevel(levels)` | `EnumMap` pre-filled with every constant |
| 4 | `EnumTasks.daysBetween(from, to)` | `EnumSet.range` / `noneOf`, ordering by `compareTo` |
| 5 | `EnumTasks.Operation.apply(a, b)` *(inside `Operation`)* | Constant-specific bodies, `ArithmeticException` |
| 6 | `EnumTasks.operationFromSymbol(symbol)` | Look up a constant by a field, not by name |
| 7 | `NestedClassTasks.countdown(from)` | Write your own `Iterator` as a member / local class |
| 8 | `NestedClassTasks.Ticket.Builder.build()` *(inside `Builder`)* | Static nested Builder, private constructor access, validation |
| 9 | `NestedClassTasks.Team.Member.addPoints(points)` *(inside `Member`)* | Inner class reaching its outer instance's state |
| 10 | `AnonymousClassTasks.sortByLastChar(words)` | Anonymous `Comparator`, tie-breaking, copy instead of mutation |
| 11 | `AnonymousClassTasks.fibonacciSupplier()` | Anonymous class with mutable fields |
| 12 | `AnonymousClassTasks.multiplesOf(factor, count)` | Capturing a loop variable correctly |
| 13 | `AnonymousClassTasks.emailNotifier(address)` | Anonymous subclass of an abstract class with constructor args |

Every task test class starts `@Disabled` so that `mvn clean install` is green before you begin.
**Remove `@Disabled` from the `@Nested` class of the task you are solving**, then run:

```bash
mvn test -Dtest=EnumTasksTest
mvn test -Dtest=NestedClassTasksTest
mvn test -Dtest=AnonymousClassTasksTest
```

All 13 task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ valueOf is case-sensitive and throws on a miss — never feed it raw input
Level.valueOf("high");                            // IllegalArgumentException
// ✅ normalise first; Locale.ROOT because "i".toUpperCase() is "İ" under a Turkish locale
Level.valueOf(text.trim().toUpperCase(Locale.ROOT));
```

```java
// ❌ a constant WITH a body is an instance of an anonymous subclass
Operation.ADD.getClass() == Operation.class;               // false
// ✅ ask for the declaring enum type instead
Operation.ADD.getDeclaringClass() == Operation.class;      // true
```

```java
// ❌ HashMap on enum keys: no order, hashing for nothing
Map<Season, String> slow = new HashMap<>();
// ✅ EnumMap: array indexed by ordinal(), O(1), iterates in declaration order
Map<Season, String> fast = new EnumMap<>(Season.class);   // null keys → NullPointerException
```

```java
// ❌ constants are built BEFORE static fields — the constructor cannot touch them
enum Bad { A; static int counter = 0; Bad() { counter++; } }   // compile error
// ✅ fill static lookups in a static block, after all constants exist
private static final Map<String, Planet> BY_NAME = new HashMap<>();
static { for (Planet p : values()) BY_NAME.put(p.name().toLowerCase(Locale.ROOT), p); }
```

```java
// ❌ switch on a null enum → NullPointerException (there is no "case null" in Java 8)
switch (maybeNullSeason) { case WINTER: ... }
// ✅ guard first, or use if (season == Season.WINTER) — == is null-safe
```

```java
// ❌ an inner class instance silently keeps its outer object alive
class Cache { Object inner = new Outer().new Inner(); }   // leaks the whole Outer
// ✅ make it static unless it truly needs the outer instance
static class Inner { }
```

```java
// ❌ no outer instance exists inside a static method
static void run() { Engine e = new Engine("V8"); }        // compile error
// ✅ create it through an outer object
Engine e = car.new Engine("V8");
// ✅ and use Outer.this when a field name is shadowed
return label + " of " + Car.this.label;
```

```java
// ❌ a loop counter is not effectively final — the anonymous class cannot capture it
for (int i = 0; i < n; i++) {
    suppliers.add(new Supplier<Integer>() { public Integer get() { return i; } });   // compile error
}
// ✅ copy it into a variable that is assigned exactly once per iteration
final int k = i;
```

```java
// ❌ code that relies on "this" changes meaning when you swap anonymous class ↔ lambda
new Supplier<String>() { public String get() { return this.getClass().getSimpleName(); } };  // ""
() -> this.getClass().getSimpleName();                                                        // "Probe"
```

```java
// ❌ double-brace initialisation: an anonymous ArrayList SUBCLASS per use-site
List<String> trap = new ArrayList<String>() {{ add("a"); }};   // getClass() != ArrayList.class
// ✅ plain and boring
List<String> ok = new ArrayList<>(Arrays.asList("a"));
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='EnumDemoTest,NestedClassDemoTest,AnonymousClassDemoTest'

# Task tests only
mvn test -Dtest='EnumTasksTest,NestedClassTasksTest,AnonymousClassTasksTest'

# Run a main demo (repeat for NestedClassDemo and AnonymousClassDemo)
mvn exec:java -Dexec.mainClass="com.vbforge.enums_and_nested_classes.EnumDemo"
```
