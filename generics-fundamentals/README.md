# generics-fundamentals

> **Phase 3 — Generics, Functional & Streams** | Java 8 | Package: `com.vbforge.generics_fundamentals`

## What's covered

| Topic | Key points |
|---|---|
| Generic classes | `Box<T>`, `Pair<A, B>`, `SimpleStack<T>`, generic interfaces (`Converter<F, T>`), diamond `<>`, naming (`T`, `E`, `K`, `V`) |
| Type erasure | `List<String>` and `List<Integer>` share one runtime class, raw types, heap pollution, `ClassCastException` on read |
| Generic methods | `<T> T identity(T)`, static generic methods, type inference, explicit witness `Class.<Type>method()`, `@SafeVarargs` |
| Generic varargs | `T...` is a `T[]` created as `Object[]` — never return it as `T[]` |
| Bounded types | `<T extends Number>`, `<T extends Comparable<T>>`, multiple bounds `<T extends Number & Comparable<T>>`, erasure to the bound |
| Wildcards | invariance, `? extends T` (producer), `? super T` (consumer), PECS, unbounded `?`, wildcard capture, `Comparable<? super T>` |

## Files

```
src/
├── main/java/com/vbforge/generics_fundamentals/
│   ├── GenericClassesDemo.java      ← theory: classes, interfaces, erasure, raw types
│   ├── GenericClassesTasks.java     ← 🎯 your tasks — swap, mapBox, firstPresent, zip
│   ├── GenericMethodsDemo.java      ← theory: generic methods, inference, varargs
│   ├── GenericMethodsTasks.java     ← 🎯 your tasks — swap, countOccurrences, repeat, invertMap, removeDuplicates
│   ├── BoundedTypesDemo.java        ← theory: extends bounds, Comparable, multiple bounds
│   ├── BoundedTypesTasks.java       ← 🎯 your tasks — max, isSorted, clamp, average, sumAbove
│   ├── WildcardsDemo.java           ← theory: ? extends, ? super, PECS, capture
│   └── WildcardsTasks.java          ← 🎯 your tasks — sumOfSquares, addRange, transferMatching, countNonNull, reverseInPlace, maxOf
└── test/java/com/vbforge/generics_fundamentals/
    ├── GenericClassesDemoTest.java  ← tests for the demo
    ├── GenericClassesTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── GenericMethodsDemoTest.java  ← tests for the demo
    ├── GenericMethodsTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── BoundedTypesDemoTest.java    ← tests for the demo
    ├── BoundedTypesTasksTest.java   ← 🧪 tests for your tasks — all must pass
    ├── WildcardsDemoTest.java       ← tests for the demo
    └── WildcardsTasksTest.java      ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open a `*Tasks.java` file and implement its methods. Each method has a Javadoc with
examples and hints. Every task test class is `@Disabled` — remove the annotation from the
`@Nested` class of the method you are solving.

| # | Method | Topic |
|---|---|---|
| 1 | `GenericClassesTasks.swap(Pair)` | Generic method returning `Pair<B, A>` |
| 2 | `GenericClassesTasks.mapBox(Box, Function)` | Changing the type parameter with `<T, R>` |
| 3 | `GenericClassesTasks.firstPresent(List<Box<T>>)` | Nested generics, returning the same instance |
| 4 | `GenericClassesTasks.zip(List, List)` | Two independent type parameters |
| 5 | `GenericMethodsTasks.swap(T[], int, int)` | Generic arrays, in-place update |
| 6 | `GenericMethodsTasks.countOccurrences(Collection, T)` | Null-safe equality with `<T>` |
| 7 | `GenericMethodsTasks.repeat(T, int)` | Building a `List<T>` from a single value |
| 8 | `GenericMethodsTasks.invertMap(Map)` | `Map<K, V>` → `Map<V, K>` |
| 9 | `GenericMethodsTasks.removeDuplicates(List)` | Order-preserving de-duplication |
| 10 | `BoundedTypesTasks.max(List)` | `<T extends Comparable<T>>` |
| 11 | `BoundedTypesTasks.isSorted(List)` | Comparing neighbours via the bound |
| 12 | `BoundedTypesTasks.clamp(T, T, T)` | Range limiting for any `Comparable` |
| 13 | `BoundedTypesTasks.average(T[])` | `<T extends Number>` |
| 14 | `BoundedTypesTasks.sumAbove(List, T)` | Multiple bounds `Number & Comparable<T>` |
| 15 | `WildcardsTasks.sumOfSquares(Collection)` | `? extends Number` (producer) |
| 16 | `WildcardsTasks.addRange(List, int, int)` | `? super Integer` (consumer) |
| 17 | `WildcardsTasks.transferMatching(List, List, Predicate)` | PECS with three wildcards |
| 18 | `WildcardsTasks.countNonNull(List)` | Unbounded `?` |
| 19 | `WildcardsTasks.reverseInPlace(List)` | Wildcard capture helper |
| 20 | `WildcardsTasks.maxOf(Collection)` | `Comparable<? super T>` like `Collections.max` |

Run your solutions with (pick the class you are working on):

```bash
mvn test -Dtest=GenericClassesTasksTest
mvn test -Dtest=GenericMethodsTasksTest
mvn test -Dtest=BoundedTypesTasksTest
mvn test -Dtest=WildcardsTasksTest
```

## Key things to remember

```java
// ❌ Generics are INVARIANT — a List<Apple> is not a List<Fruit>
List<Apple> apples = new ArrayList<>();
List<Fruit> fruits = apples;                  // compile error — otherwise you could add an Orange
// ✅ A wildcard describes a family of types (read-only view)
List<? extends Fruit> view = apples;
```

```java
// ❌ Arrays are covariant: compiles, fails at RUNTIME
Fruit[] basket = new Apple[1];
basket[0] = new Orange(150);                  // ArrayStoreException
// ✅ Generics move the same mistake to compile time — prefer List<T> over T[]
```

```java
// ✅ PECS — Producer Extends, Consumer Super
static <T> void copyInto(List<? super T> dest, List<? extends T> src) { … }
// ❌ Cannot ADD to a "? extends" list (only null) — the real element type is unknown
List<? extends Number> numbers = new ArrayList<Integer>();
numbers.add(1);                               // compile error
// ❌ Cannot READ a specific type from a "? super" list — you only get Object
```

```java
// ❌ Raw types switch the compiler's protection off ("heap pollution")
List<String> strings = new ArrayList<>();
((List) strings).add(42);                     // compiles, no failure here…
String s = strings.get(0);                    // …ClassCastException far away, on READ
// ✅ Never use raw types in new code; keep unchecked casts private and tiny
```

```java
// ❌ Returning a generic varargs array as T[] blows up at the call site
static <T> T[] twoOf(T a, T b) { return arrayOf(a, b); }     // really an Object[]
String[] pair = twoOf("a", "b");              // ClassCastException
// ✅ @SafeVarargs only when the method merely READS the array (e.g. listOf)
```

```java
// ❌ <T extends Comparable<T>> rejects subclasses that inherit compareTo
Apple heaviest = BoundedTypesDemo.maxOf(a1, a2);   // Apple is Comparable<Fruit>, not Comparable<Apple>
// ✅ Prefer Comparable<? super T> — the signature of Collections.max / sort
static <T extends Comparable<? super T>> T maxOf(T a, T b) { … }
```

```java
// ✅ Writing into List<?> needs a capture helper — the compiler names the unknown type E
static void swapFirstAndLast(List<?> list) { helper(list); }
private static <E> void helper(List<E> list) { E tmp = list.get(0); … }
// ❌ list.set(0, list.get(1)) directly on a List<?> does not compile
```

```java
// ✅ Bounded T is erased to its bound; unbounded T is erased to Object
class NumberBox<T extends Number> { T value; }   // field type at runtime: Number
// ❌ T cannot be instantiated or used as an array type: new T(), new T[n], T.class
// ✅ Multiple bounds: class FIRST, interfaces after &  →  <T extends Number & Comparable<T>>
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only (one concept)
mvn test -Dtest=GenericClassesDemoTest

# Task tests only (one concept)
mvn test -Dtest=GenericClassesTasksTest

# Run main demo (one concept)
mvn exec:java -Dexec.mainClass="com.vbforge.generics_fundamentals.GenericClassesDemo"
```
