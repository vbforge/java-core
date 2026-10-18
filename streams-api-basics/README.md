# streams-api-basics

> **Phase 3 — Generics, Functional & Streams** | Java 8 | Package: `com.vbforge.streams_api_basics`

Stream creation, filter/map/sorted/collect/forEach/reduce — tag: `STREAMS`

## What's covered

| Topic | Key points |
|---|---|
| Stream creation | `collection.stream()`, `Stream.of`, `Arrays.stream`, `Stream.empty`, `Stream.concat`; `Map` is streamed via `keySet`/`values`/`entrySet` |
| Infinite streams | `Stream.iterate(seed, f)`, `Stream.generate(supplier)`, always cut with `limit`; `skip` + `limit` = window |
| Primitive streams | `IntStream.range` / `rangeClosed`, `String.chars()`, `boxed()`, `mapToObj`; no boxing, built-in `sum`/`average`/`max` |
| Single use | A consumed stream throws `IllegalStateException`; use a `Supplier<Stream<T>>`; the source is never modified |
| `filter` / `map` | Select and transform; `map` changes the element type; `mapToInt` switches to `IntStream` |
| `sorted` | Natural order, `Comparator.comparing`, `reversed`, `thenComparing`; stable; throws `ClassCastException` if not `Comparable` |
| `distinct` / `skip` / `limit` | `distinct` uses `equals`/`hashCode`; `skip` + `limit` = pagination |
| Laziness | No work without a terminal operation; element-by-element flow; `limit` short-circuits; `sorted` is a barrier; `peek` for debugging |
| `forEach` | Side effects at the end; `forEachOrdered`; no `break`; do not build results with it |
| `collect` | `Collectors.toList`, `toSet`, `toMap` (+ merge function), `joining`, `toCollection` |
| `reduce` | Identity form, `Optional` form, three-argument form; identity must be neutral; accumulator must be associative |
| Match / find | `anyMatch`, `allMatch`, `noneMatch`, `findFirst`, `findAny`; short-circuit; vacuous truth on empty streams |
| `count` / `min` / `max` | `count()` returns `long`; `min`/`max` take a `Comparator`; `summaryStatistics()`; empty `IntStream` → empty optionals |

## Files

```
src/
├── main/java/com/vbforge/streams_api_basics/
│   ├── StreamCreationDemo.java              ← theory + annotated examples (sources, infinite, primitive, single use)
│   ├── StreamCreationTasks.java             ← 🎯 your tasks — implement these methods
│   ├── IntermediateOperationsDemo.java      ← theory + annotated examples (filter/map/sorted/distinct/limit/laziness)
│   ├── IntermediateOperationsTasks.java     ← 🎯 your tasks — implement these methods
│   ├── TerminalOperationsDemo.java          ← theory + annotated examples (forEach/collect/reduce/match/aggregate)
│   └── TerminalOperationsTasks.java         ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/streams_api_basics/
    ├── StreamCreationDemoTest.java          ← tests for the demo (already green)
    ├── StreamCreationTasksTest.java         ← 🧪 tests for your tasks — all must pass
    ├── IntermediateOperationsDemoTest.java  ← tests for the demo (already green)
    ├── IntermediateOperationsTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── TerminalOperationsDemoTest.java      ← tests for the demo (already green)
    └── TerminalOperationsTasksTest.java     ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement its methods. Each has a detailed Javadoc.
Every task is meant to be solved with a **stream pipeline**, not a loop.
The `#` column is the task number inside its own file.

| # | Method | Topic |
|---|---|---|
| 1 | `StreamCreationTasks.powersOfTwo(count)` | `Stream.iterate` + `limit` |
| 2 | `StreamCreationTasks.sumOfRange(from, to)` | `IntStream.rangeClosed` + `sum` (empty range) |
| 3 | `StreamCreationTasks.repeatText(text, times)` | `Stream.generate` + `limit` |
| 4 | `StreamCreationTasks.toCharacterList(text)` | `String.chars()` → `mapToObj` |
| 5 | `StreamCreationTasks.concatLists(first, second)` | `Stream.concat` |
| 6 | `StreamCreationTasks.boxArray(numbers)` | `Arrays.stream(int[])` + `boxed` |
| 1 | `IntermediateOperationsTasks.evenSquares(numbers)` | `filter` → `map` |
| 2 | `IntermediateOperationsTasks.cleanAndUpperCase(words)` | `filter` + several `map` steps (Java 8: no `isBlank`) |
| 3 | `IntermediateOperationsTasks.sortByLengthThenAlphabetically(words)` | `sorted` + `Comparator.comparing().thenComparing()` |
| 4 | `IntermediateOperationsTasks.distinctDescending(numbers)` | `distinct` + `Comparator.reverseOrder()` |
| 5 | `IntermediateOperationsTasks.page(items, pageIndex, pageSize)` | `skip` + `limit`, `long` offset |
| 6 | `IntermediateOperationsTasks.sumOfOddSquares(numbers)` | `IntStream` pipeline, negative `%` gotcha |
| 7 | `IntermediateOperationsTasks.firstNMatches(numbers, predicate, n)` | laziness: `filter` + `limit` short-circuit |
| 1 | `TerminalOperationsTasks.joinUpperCase(words, delimiter)` | `Collectors.joining` |
| 2 | `TerminalOperationsTasks.wordLengths(words)` | `Collectors.toMap` with a merge function |
| 3 | `TerminalOperationsTasks.largest(numbers)` | `reduce` / `max` → `Optional` |
| 4 | `TerminalOperationsTasks.product(numbers)` | `reduce` with identity, `long` result |
| 5 | `TerminalOperationsTasks.allPositive(numbers)` | `allMatch` and vacuous truth |
| 6 | `TerminalOperationsTasks.firstLongWord(words, minLength)` | `filter` + `findFirst` |
| 7 | `TerminalOperationsTasks.averageLength(words)` | `mapToInt` + `average().orElse` |
| 8 | `TerminalOperationsTasks.uniqueInitials(words)` | `filter` + `map` + `Collectors.toSet` |

```bash
mvn test -Dtest=StreamCreationTasksTest
mvn test -Dtest=IntermediateOperationsTasksTest
mvn test -Dtest=TerminalOperationsTasksTest
```

All 21 task groups must go **GREEN** ✅

## Key things to remember

```java
// A stream is a ONE-SHOT pipeline, not a collection
Stream<String> s = Stream.of("a", "b");
s.count();                  // ✅ first terminal operation
s.count();                  // ❌ IllegalStateException: stream has already been operated upon or closed
Supplier<Stream<String>> fresh = () -> Stream.of("a", "b");   // ✅ new stream per use
```

```java
// Nothing runs without a terminal operation
Stream.of(1, 2, 3).map(n -> { System.out.println(n); return n; });              // ❌ prints nothing
Stream.of(1, 2, 3).map(n -> { System.out.println(n); return n; })
        .collect(Collectors.toList());                                           // ✅ the terminal op starts the work
// Never rely on side effects inside map/peek — use them for debugging only
```

```java
// Stream.of(int[]) is ONE element — the array itself
int[] numbers = {1, 2, 3};
Stream.of(numbers).count();             // ❌ 1
Arrays.stream(numbers).count();         // ✅ 3  (IntStream — no boxing)
Arrays.stream(numbers).boxed().collect(Collectors.toList());   // ✅ List<Integer>
```

```java
// Infinite streams need a cut-off
Stream.iterate(1, n -> n + 1).collect(Collectors.toList());              // ❌ never returns
Stream.iterate(1, n -> n + 1).limit(5).collect(Collectors.toList());    // ✅ [1, 2, 3, 4, 5]
// Java 8 iterate() has no "stop" predicate — the 3-argument version is Java 9+
```

```java
// IntStream.range is END-EXCLUSIVE, rangeClosed is END-INCLUSIVE
IntStream.range(1, 5).sum();         // 10  (1+2+3+4)
IntStream.rangeClosed(1, 5).sum();   // 15  (1+2+3+4+5)
"abc".chars();                       // ints 97, 98, 99 — NOT chars; use mapToObj(c -> (char) c)
```

```java
// map vs mapToInt — avoid boxing when you need numbers
people.stream().map(Person::getAge).sum();          // ❌ compile error: Stream<Integer> has no sum()
people.stream().mapToInt(Person::getAge).sum();     // ✅ IntStream.sum()
people.stream().mapToInt(Person::getAge).average(); // OptionalDouble — the stream may be empty!
```

```java
// sorted(): stable, copies, and needs Comparable (or a Comparator)
people.stream().sorted();                                              // ❌ compiles, ClassCastException at runtime
people.stream().sorted(Comparator.comparingInt(Person::getAge)
                                 .thenComparing(Person::getName));     // ✅ sort by age, then name
// sorted() never touches the source list; it sorts inside the pipeline
```

```java
// Filter BEFORE you map or sort — fewer elements for the expensive step
list.stream().map(expensive).filter(cond);   // ❌ expensive() runs for every element
list.stream().filter(cond).map(expensive);   // ✅ expensive() runs only for matches
```

```java
// Pagination: skip() and limit() take a long — avoid int overflow in the offset
items.stream().skip(pageIndex * pageSize).limit(pageSize);         // ❌ int overflow for huge indexes
items.stream().skip((long) pageIndex * pageSize).limit(pageSize);  // ✅
```

```java
// Do not build results with forEach — that is what collect is for
List<String> out = new ArrayList<>();
stream.forEach(out::add);                       // ❌ outside mutation, unsafe with parallel streams
List<String> out2 = stream.collect(Collectors.toList());   // ✅
```

```java
// Collectors.toMap throws on duplicate keys — give it a merge function
words.stream().collect(Collectors.toMap(w -> w, String::length));              // ❌ IllegalStateException: Duplicate key
words.stream().collect(Collectors.toMap(w -> w, String::length, (a, b) -> a)); // ✅ keep the first value
// Several values per key? That is groupingBy — see streams-api-advanced
```

```java
// reduce: the identity must be NEUTRAL for the operation
Stream.of(1, 2, 3).reduce(0, Integer::sum);        // ✅ 6
Stream.of(1, 2, 3).reduce(10, Integer::sum);       // ❌ 16 — and wrong in parallel (10 added per chunk)
Stream.<Integer>empty().reduce(1, (a, b) -> a * b);// ✅ 1 — identity of multiplication
Stream.<Integer>empty().reduce(Integer::max);      // Optional.empty — no identity, so an Optional
```

```java
// Empty streams: vacuous truth and empty optionals
Stream.<Integer>empty().allMatch(n -> n > 0);      // true  (!) — nothing violates the rule
Stream.<Integer>empty().anyMatch(n -> n > 0);      // false
IntStream.empty().sum();                           // 0
IntStream.empty().max();                           // OptionalInt.empty — there is no sensible default
```

```java
// Java 8 only: Stream.toList(), takeWhile, Optional.isEmpty(), String.isBlank() do not exist here
list.stream().collect(Collectors.toList());        // ✅ the Java 8 way to get a List
```

## Run

```bash
# All tests (demos + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run the demos (one main() per concept)
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_basics.StreamCreationDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_basics.IntermediateOperationsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_basics.TerminalOperationsDemo"
```
