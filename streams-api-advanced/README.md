# streams-api-advanced

> **Phase 3 — Generics, Functional & Streams** | Java 8 | Package: `com.vbforge.streams_api_advanced`

flatMap, groupingBy, partitioningBy, parallel streams, custom collectors — tag: `STREAMS`

## What's covered

| Topic | Key points |
|---|---|
| `flatMap` | One-to-many; flattens `Stream<List<T>>`; a `null` result counts as empty; `"".split(" ")` gives one empty string |
| Dropping empties | `filter(Optional::isPresent).map(Optional::get)` or map to a zero-or-one element stream (Java 8: no `Optional.stream()`) |
| Combinations | Outer `flatMap` + inner `map` = cartesian product |
| `flatMapToInt` | `words.stream().flatMapToInt(String::chars)` instead of a `Stream<IntStream>` |
| `groupingBy` | Classifier → `Map<K, List<T>>`; no empty buckets; map factory (`TreeMap::new`, `LinkedHashMap::new`); `null` key → `NullPointerException` |
| Downstream collectors | `counting` (→ `Long`), `summingInt`, `averagingInt` (→ `Double`), `mapping`, `maxBy` (→ `Optional`) |
| Multi-level grouping | A downstream `groupingBy` gives `Map<K1, Map<K2, V>>` |
| `partitioningBy` | `Map<Boolean, …>` that **always** has both keys; accepts a downstream collector |
| `Collector.of` | supplier, accumulator, combiner, finisher; accumulator type may differ from the result type |
| `collectingAndThen` | Post-process a collector: read-only list, `Long::intValue`, `Optional::get` |
| Built-in composition | `summarizingInt`, `reducing`, `toMap` with merge function and map factory |
| Combiners | A wrong combiner is invisible on sequential streams and wrong on parallel ones |
| Parallel streams | `parallelStream()`, `parallel()`, `sequential()`; the last call wins for the whole pipeline |
| Parallel results | Same sum/count; `collect` and `forEachOrdered` keep encounter order; `forEach` does not |
| Safe accumulation | `collect`/`reduce` instead of a shared `ArrayList`; neutral `reduce` identity; `LongAdder` |
| Concurrent collectors | `groupingByConcurrent`; `findAny` vs `findFirst` |

## Files

```
src/
├── main/java/com/vbforge/streams_api_advanced/
│   ├── FlatMapDemo.java                  ← theory + annotated examples (flatMap, empties, combinations, flatMapToInt)
│   ├── FlatMapTasks.java                 ← 🎯 your tasks — implement these methods
│   ├── GroupingDemo.java                 ← theory + annotated examples (groupingBy, downstream, multi-level, partitioningBy)
│   ├── GroupingTasks.java                ← 🎯 your tasks — implement these methods
│   ├── CustomCollectorsDemo.java         ← theory + annotated examples (Collector.of, collectingAndThen, built-ins, combiners)
│   ├── CustomCollectorsTasks.java        ← 🎯 your tasks — implement these methods
│   ├── ParallelStreamsDemo.java          ← theory + annotated examples (parallel streams, safe accumulation, concurrent collectors)
│   └── ParallelStreamsTasks.java         ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/streams_api_advanced/
    ├── FlatMapDemoTest.java              ← tests for the demo (already green)
    ├── FlatMapTasksTest.java             ← 🧪 tests for your tasks — all must pass
    ├── GroupingDemoTest.java             ← tests for the demo (already green)
    ├── GroupingTasksTest.java            ← 🧪 tests for your tasks — all must pass
    ├── CustomCollectorsDemoTest.java     ← tests for the demo (already green)
    ├── CustomCollectorsTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── ParallelStreamsDemoTest.java      ← tests for the demo (already green)
    └── ParallelStreamsTasksTest.java     ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement its methods. Each has a detailed Javadoc.
Every task is meant to be solved with a **stream pipeline**, not a loop.
The `#` column is the task number inside its own file.

| # | Method | Topic |
|---|---|---|
| 1 | `FlatMapTasks.allWords(sentences)` | `flatMap` + `split`, empty-string gotcha |
| 2 | `FlatMapTasks.flatten(nested)` | `flatMap(List::stream)` |
| 3 | `FlatMapTasks.combine(first, second)` | nested `flatMap` + `map` (cartesian product) |
| 4 | `FlatMapTasks.presentValues(optionals)` | dropping empty `Optional`s in Java 8 |
| 5 | `FlatMapTasks.distinctLetters(words)` | `flatMapToInt(String::chars)` + `distinct` + `sorted` |
| 1 | `GroupingTasks.groupByLength(words)` | basic `groupingBy` |
| 2 | `GroupingTasks.countByFirstLetter(words)` | `groupingBy` + `counting()` (→ `Long`) |
| 3 | `GroupingTasks.partitionEvenOdd(numbers)` | `partitioningBy` always has both keys |
| 4 | `GroupingTasks.joinByFirstLetter(words)` | `groupingBy` + `mapping` + `joining` |
| 5 | `GroupingTasks.countLongWords(words, minLength)` | `partitioningBy` + `counting()` |
| 6 | `GroupingTasks.groupByLengthThenInitial(words)` | multi-level `groupingBy` |
| 1 | `CustomCollectorsTasks.unmodifiableEvens(numbers)` | `collectingAndThen` + `Collections::unmodifiableList` |
| 2 | `CustomCollectorsTasks.longestWordByInitial(words)` | `groupingBy` + `maxBy` + `Optional::get` |
| 3 | `CustomCollectorsTasks.longestStringCollector()` | your own `Collector.of`, parallel-safe combiner |
| 4 | `CustomCollectorsTasks.lengthStats(words)` | `summarizingInt` |
| 5 | `CustomCollectorsTasks.averageCollector()` | custom collector with a finisher (`long[]` → `Double`) |
| 1 | `ParallelStreamsTasks.sumUpTo(n)` | parallel `LongStream.rangeClosed` |
| 2 | `ParallelStreamsTasks.squaresInOrder(numbers)` | parallel `collect` keeps encounter order |
| 3 | `ParallelStreamsTasks.countByRemainder(numbers, divisor)` | `groupingByConcurrent` + `counting` |
| 4 | `ParallelStreamsTasks.totalLength(words)` | parallel reduction without shared state |
| 5 | `ParallelStreamsTasks.anyEven(numbers)` | `findAny` on a parallel stream |

```bash
mvn test -Dtest=FlatMapTasksTest
mvn test -Dtest=GroupingTasksTest
mvn test -Dtest=CustomCollectorsTasksTest
mvn test -Dtest=ParallelStreamsTasksTest
```

All 21 task groups must go **GREEN** ✅

## Key things to remember

```java
// map is one-to-one, flatMap is one-to-many
lines.stream().map(l -> l.split(" "));                          // ❌ Stream<String[]>
lines.stream().flatMap(l -> Arrays.stream(l.split(" ")));       // ✅ Stream<String>
"".split(" ").length;                                           // ❌ 1 — one EMPTY string, not zero words
```

```java
// Java 8 has no Optional.stream() — unwrap by hand
optionals.stream().filter(Optional::isPresent).map(Optional::get);                            // ✅
optionals.stream().flatMap(o -> o.isPresent() ? Stream.of(o.get()) : Stream.<String>empty()); // ✅
// Java 9+: optionals.stream().flatMap(Optional::stream)
```

```java
// Strings -> chars: map gives a Stream of IntStreams
words.stream().map(String::chars);               // ❌ Stream<IntStream>
words.stream().flatMapToInt(String::chars);      // ✅ IntStream of every character (as int!)
// "abc".chars() is 97, 98, 99 — cast with (char) c to get letters back
```

```java
// groupingBy: no empty buckets, null keys are forbidden, HashMap order is unspecified
Map<String, List<E>> m = list.stream().collect(Collectors.groupingBy(E::getDept));
m.get("Legal");                                                   // null — the key was never created
Stream.of("a").collect(Collectors.groupingBy(s -> null));         // ❌ NullPointerException
list.stream().collect(Collectors.groupingBy(E::getDept, TreeMap::new, Collectors.toList()));  // ✅ sorted keys
```

```java
// Downstream collectors have their own result types
Map<String, Long>   n = stream.collect(groupingBy(E::getDept, counting()));        // Long, not Integer
Map<String, Double> a = stream.collect(groupingBy(E::getDept, averagingInt(E::getSalary)));  // Double
Map<String, Optional<E>> t = stream.collect(groupingBy(E::getDept, maxBy(cmp)));    // Optional!
Map<String, E> t2 = stream.collect(groupingBy(E::getDept,
        collectingAndThen(maxBy(cmp), Optional::get)));                             // ✅ plain values
```

```java
// partitioningBy ALWAYS has both keys, groupingBy only the keys that occur
Stream.<Integer>empty().collect(partitioningBy(n -> n > 0));   // {false=[], true=[]}
Stream.<Integer>empty().collect(groupingBy(n -> n > 0));       // {}
```

```java
// Collector.of(supplier, accumulator, combiner, finisher)
Collector<Integer, long[], Double> avg = Collector.of(
        () -> new long[2],                                  // [sum, count]
        (acc, n) -> { acc[0] += n; acc[1]++; },             // fold one element in
        (a, b) -> { a[0] += b[0]; a[1] += b[1]; return a; },// merge two partial results (parallel!)
        acc -> acc[1] == 0 ? 0.0 : (double) acc[0] / acc[1]);   // finisher: long[] -> Double
```

```java
// A broken combiner is INVISIBLE on a sequential stream
Collector.of(() -> new long[1], (acc, n) -> acc[0] += n, (a, b) -> a, acc -> acc[0]);   // ❌ forgets b
list.stream().collect(c);           // looks fine
list.parallelStream().collect(c);   // silently loses data
```

```java
// Parallel streams: the LAST call decides for the whole pipeline
list.stream().parallel().map(f).sequential().isParallel();   // false — even the map runs sequentially
```

```java
// Never accumulate into a shared non-thread-safe list from a parallel stream
List<Integer> out = new ArrayList<>();
IntStream.range(0, 10_000).parallel().forEach(out::add);                      // ❌ lost / duplicated elements, exceptions
IntStream.range(0, 10_000).boxed().parallel().collect(Collectors.toList());   // ✅ ordered and complete
```

```java
// reduce: the identity must be NEUTRAL — it is applied once per chunk on a parallel stream
Stream.of(1, 2, 3).parallel().reduce(10, Integer::sum);   // ❌ 6 + 10 * (number of chunks)
Stream.of(1, 2, 3).parallel().reduce(0, Integer::sum);    // ✅ 6
```

```java
// Order guarantees on parallel streams
list.parallelStream().map(f).collect(toList());   // ✅ encounter order kept
list.parallelStream().forEachOrdered(action);     // ✅ encounter order, one at a time
list.parallelStream().forEach(action);            // ❌ arbitrary order
list.parallelStream().filter(p).findFirst();      // ✅ deterministic
list.parallelStream().filter(p).findAny();        // ⚠️ any match — do not assert a specific element
```

```java
// Java 8 only: Collectors.flatMapping/filtering/teeing, Stream.toList(), takeWhile, Optional.stream() do not exist here
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
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_advanced.FlatMapDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_advanced.GroupingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_advanced.CustomCollectorsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.streams_api_advanced.ParallelStreamsDemo"
```
