# functional-interfaces

> **Phase 3 — Generics, Functional & Streams** | Java 8 | Package: `com.vbforge.functional_interfaces`

## What's covered

| Topic | Key points |
|---|---|
| Functional contract | exactly one abstract method, `@FunctionalInterface`, default/static methods, `Object` methods don't count, generic `TriFunction<A, B, C, R>` |
| Lambdas | syntax forms, target typing, capturing effectively final variables, `this` in lambda vs anonymous class, deferred execution, closures |
| Method references | `Class::staticMethod`, `object::method`, `Class::instanceMethod`, `Class::new`, `int[]::new`, lambda or reference? |
| `Predicate<T>` | `test`, `and`, `or`, `negate`, `Predicate.isEqual`, short-circuiting |
| `Function<T, R>` | `apply`, `andThen` vs `compose`, `identity`, `UnaryOperator`, `BinaryOperator` |
| `Consumer<T>` / `Supplier<T>` | side effects, `andThen`, lazy values, eager vs lazy fallbacks |
| Primitive specialisations | `IntPredicate`, `IntBinaryOperator`, `ToIntFunction` — no boxing |
| Higher-order functions | passing and returning functions, `? super T` / `? extends R` in signatures |

## Files

```
src/
├── main/java/com/vbforge/functional_interfaces/
│   ├── FunctionalContractDemo.java       ← theory: single abstract method, default/static, TriFunction
│   ├── FunctionalContractTasks.java      ← 🎯 your tasks — allOf, anyOf, curry, onError
│   ├── LambdasDemo.java                  ← theory: syntax, capture, this, closures
│   ├── LambdasTasks.java                 ← 🎯 your tasks — multiplier, squareSuppliers, once, operation, byLengthThenAlphabetical
│   ├── MethodReferencesDemo.java         ← theory: the four kinds of method references
│   ├── MethodReferencesTasks.java        ← 🎯 your tasks — trimAndUpper, toPeople, collectInto, byNameIgnoringCase
│   ├── BuiltInInterfacesDemo.java        ← theory: Predicate, Function, Consumer, Supplier, primitives
│   └── BuiltInInterfacesTasks.java       ← 🎯 your tasks — noneOf, pipeline, broadcast, firstNonNull, sumIf, mapAndFilter
└── test/java/com/vbforge/functional_interfaces/
    ├── FunctionalContractDemoTest.java   ← tests for the demo
    ├── FunctionalContractTasksTest.java  ← 🧪 tests for your tasks — all must pass
    ├── LambdasDemoTest.java              ← tests for the demo
    ├── LambdasTasksTest.java             ← 🧪 tests for your tasks — all must pass
    ├── MethodReferencesDemoTest.java     ← tests for the demo
    ├── MethodReferencesTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── BuiltInInterfacesDemoTest.java    ← tests for the demo
    └── BuiltInInterfacesTasksTest.java   ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open a `*Tasks.java` file and implement its methods. Each method has a Javadoc with
examples and hints. Every task test class is `@Disabled` — remove the annotation from the
`@Nested` class of the method you are solving.

| # | Method | Topic |
|---|---|---|
| 1 | `FunctionalContractTasks.allOf(List<Validator>)` | Combining validators with a default method |
| 2 | `FunctionalContractTasks.anyOf(List<Validator>)` | Returning a lambda that loops and short-circuits |
| 3 | `FunctionalContractTasks.curry(TriFunction)` | Nested `Function`s, closures over earlier arguments |
| 4 | `FunctionalContractTasks.onError(TriFunction, R)` | Wrapping a custom functional interface |
| 5 | `LambdasTasks.multiplier(int)` | Capturing a parameter in a lambda |
| 6 | `LambdasTasks.squareSuppliers(int)` | Capturing a fresh copy of a loop variable |
| 7 | `LambdasTasks.once(Supplier)` | Lazy, cached evaluation with mutable captured state |
| 8 | `LambdasTasks.operation(String)` | Returning a different lambda per `switch` branch |
| 9 | `LambdasTasks.byLengthThenAlphabetical()` | A lambda as a `Comparator` |
| 10 | `MethodReferencesTasks.trimAndUpper(List)` | Unbound instance references (`String::trim`) |
| 11 | `MethodReferencesTasks.toPeople(List)` | Constructor reference (`Person::new`) |
| 12 | `MethodReferencesTasks.collectInto(List, Supplier)` | Constructor references as factories (`TreeSet::new`) |
| 13 | `MethodReferencesTasks.byNameIgnoringCase()` | `Comparator.comparing` with a key extractor reference |
| 14 | `BuiltInInterfacesTasks.noneOf(List<Predicate>)` | `Predicate` composition and short-circuiting |
| 15 | `BuiltInInterfacesTasks.pipeline(List<Function>)` | `andThen` order, `Function.identity()` |
| 16 | `BuiltInInterfacesTasks.broadcast(List<Consumer>)` | Calling several `Consumer`s in order |
| 17 | `BuiltInInterfacesTasks.firstNonNull(List<Supplier>)` | Lazy `Supplier`s that stop at the first hit |
| 18 | `BuiltInInterfacesTasks.sumIf(int[], IntPredicate)` | Primitive specialisation, no boxing |
| 19 | `BuiltInInterfacesTasks.mapAndFilter(List, Function, Predicate)` | Wildcards in higher-order signatures |

Run your solutions with (pick the class you are working on):

```bash
mvn test -Dtest=FunctionalContractTasksTest
mvn test -Dtest=LambdasTasksTest
mvn test -Dtest=MethodReferencesTasksTest
mvn test -Dtest=BuiltInInterfacesTasksTest
```

## Key things to remember

```java
// ✅ Functional = exactly ONE abstract method. Default, static and Object methods don't count
@FunctionalInterface
interface Validator<T> { boolean validate(T v); default Validator<T> negate() { … } }
// ❌ A second abstract method breaks every lambda — the annotation makes the compiler tell you early
```

```java
// ❌ Locals captured by a lambda must be effectively final
int count = 0;
list.forEach(x -> count++);                  // compile error
// ✅ Mutate an object the variable points to
AtomicInteger counter = new AtomicInteger();
list.forEach(x -> counter.incrementAndGet());
```

```java
// ❌ The classic loop variable cannot be captured
for (int i = 0; i < n; i++) { suppliers.add(() -> i * 10); }     // compile error
// ✅ Capture a fresh copy per iteration
for (int i = 0; i < n; i++) { final int copy = i; suppliers.add(() -> copy * 10); }
```

```java
// ✅ "this" in a lambda is the ENCLOSING object; in an anonymous class it is the anonymous object
Supplier<String> viaLambda = () -> this.name;                      // Owner.name
Supplier<String> viaClass  = new Supplier<String>() { … this.name … };   // the anonymous class's own field
```

```java
// ✅ Method reference kinds
Integer::parseInt        // static            s -> Integer.parseInt(s)
prefix::concat           // bound instance    s -> prefix.concat(s)   (prefix evaluated NOW; null → NPE at creation)
String::toUpperCase      // unbound instance  s -> s.toUpperCase()
ArrayList::new           // constructor       () -> new ArrayList<>()
// ❌ Function<Integer, String> f = Integer::toString;   ambiguous (static AND instance toString fit) — use a lambda
```

```java
// ❌ andThen vs compose — same pieces, different order, no compiler warning
plusOne.compose(timesTwo).apply(5)   // plusOne(timesTwo(5)) = 11
plusOne.andThen(timesTwo).apply(5)   // timesTwo(plusOne(5)) = 12
```

```java
// ❌ Eager: the fallback is computed even when it is not needed
String name = valueOrElse(value, expensiveDefault());
// ✅ Lazy: a Supplier runs only when needed (like Optional.orElseGet)
String name = valueOrElseGet(value, () -> expensiveDefault());
```

```java
// ✅ Prefer primitive specialisations in hot loops — Function<Integer, Integer> boxes on every call
IntBinaryOperator sum = (a, b) -> a + b;
// ❌ Predicate.not(...) is Java 11+ — on Java 8 use predicate.negate()
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only (one concept)
mvn test -Dtest=LambdasDemoTest

# Task tests only (one concept)
mvn test -Dtest=LambdasTasksTest

# Run main demo (one concept)
mvn exec:java -Dexec.mainClass="com.vbforge.functional_interfaces.LambdasDemo"
```
