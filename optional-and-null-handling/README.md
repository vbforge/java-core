# optional-and-null-handling

> **Phase 3 — Generics, Functional & Streams** | Java 8 | Package: `com.vbforge.optional_and_null_handling`

## What's covered

| Topic | Key points |
|---|---|
| Creation | `Optional.empty`, `Optional.of` (throws on `null`), `Optional.ofNullable`, wrapping `Map.get` |
| Inspecting | `isPresent`, `get` (throws `NoSuchElementException` on empty), `toString`, value-based `equals` |
| Primitive optionals | `OptionalInt`, `OptionalDouble` — no boxing, but no `map`/`flatMap` |
| Unwrapping with defaults | `orElse` (eager), `orElseGet` (lazy `Supplier`), `orElse(null)` smell |
| Unwrapping with failure | `orElseThrow(Supplier)`, constructor reference as supplier |
| Consuming | `ifPresent`, Java 8 `if/else` instead of `ifPresentOrElse`, hand-written fallback instead of `or` |
| `map` | Transforms the inner value; a `null` result becomes `Optional.empty()` |
| `flatMap` | Flattens `Optional<Optional<T>>` — use when the function already returns an `Optional` |
| `filter` | A present value can turn empty when the predicate is false |
| Chains | `user → address → city` without `if (x != null)` pyramids; stages are lazy |
| Where NOT to use it | Empty collection instead of `Optional<List<T>>` |

> ℹ️ **Java 8 content** — the sources use only Java 8 APIs and were compiled with `--release 8`.
> The build itself uses the parent's Java 17 default, so Java 9+ methods (`isEmpty`, `ifPresentOrElse`, `or`, `stream`)
> will compile here by accident but fail on a real Java 8 toolchain — the tasks ask you to avoid them.

## Files

```
src/
├── main/java/com/vbforge/optional_and_null_handling/
│   ├── OptionalCreationDemo.java       ← theory: empty/of/ofNullable, get, equals, OptionalInt
│   ├── OptionalCreationTasks.java      ← 🎯 your tasks — implement these methods
│   ├── OptionalUnwrappingDemo.java     ← theory: orElse vs orElseGet, orElseThrow, ifPresent
│   ├── OptionalUnwrappingTasks.java    ← 🎯 your tasks — implement these methods
│   ├── OptionalChainingDemo.java       ← theory: map, flatMap, filter, null-check pyramid vs chain
│   ├── OptionalChainingTasks.java      ← 🎯 your tasks — implement these methods
│   └── model/
│       ├── Address.java                ← tiny model: legacy getCity() + Optional getZip()
│       └── User.java                   ← tiny model: Optional getAddress()
└── test/java/com/vbforge/optional_and_null_handling/
    ├── OptionalCreationDemoTest.java       ← tests for the demo
    ├── OptionalCreationTasksTest.java      ← 🧪 tests for your tasks — remove @Disabled per task
    ├── OptionalUnwrappingDemoTest.java     ← tests for the demo
    ├── OptionalUnwrappingTasksTest.java    ← 🧪 tests for your tasks — remove @Disabled per task
    ├── OptionalChainingDemoTest.java       ← tests for the demo
    └── OptionalChainingTasksTest.java      ← 🧪 tests for your tasks — remove @Disabled per task
```

## 🎯 Tasks

Open each `*Tasks.java` and implement the methods. Every method has a Javadoc with
examples and hints. Each task has its own `@Nested` test class marked `@Disabled` —
remove the annotation from the task you are working on.

| # | Method | Topic |
|---|---|---|
| 1 | `OptionalCreationTasks.parseInteger(text)` | Turning a failing parse into `Optional` |
| 2 | `OptionalCreationTasks.nonBlank(text)` | `null`/blank text → `Optional.empty()` |
| 3 | `OptionalCreationTasks.lookup(map, key)` | Wrapping a null-returning API |
| 4 | `OptionalCreationTasks.firstNegative(numbers)` | `OptionalInt` |
| 5 | `OptionalCreationTasks.average(numbers)` | `OptionalDouble` for "no answer" |
| 6 | `OptionalUnwrappingTasks.valueOrComputed(value, fallback)` | Lazy default with `orElseGet` |
| 7 | `OptionalUnwrappingTasks.requireValue(value, fieldName)` | `orElseThrow(Supplier)` |
| 8 | `OptionalUnwrappingTasks.collectIfPresent(value, sink)` | `ifPresent` |
| 9 | `OptionalUnwrappingTasks.firstPresent(first, second, third)` | Fallback chain without `Optional.or` |
| 10 | `OptionalChainingTasks.trimmedLength(text)` | `map` |
| 11 | `OptionalChainingTasks.zipOf(user)` | `flatMap` |
| 12 | `OptionalChainingTasks.cityStartingWith(user, prefix)` | `flatMap` + `map` + `filter` |
| 13 | `OptionalChainingTasks.mailingLabel(user)` | Nested chains |
| 14 | `OptionalChainingTasks.sumIfBothPresent(a, b)` | `flatMap` + `map` on two Optionals |
| 15 | `OptionalChainingTasks.citiesOf(users)` | Optional inside a loop |

Run your solutions with:
```bash
mvn test -Dtest=OptionalCreationTasksTest
mvn test -Dtest=OptionalUnwrappingTasksTest
mvn test -Dtest=OptionalChainingTasksTest
```
All 15 task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ Optional.of(null) throws NullPointerException
Optional.of(maybeNull);
// ✅ ofNullable converts null into Optional.empty()
Optional.ofNullable(maybeNull);

// ❌ get() without a check just swaps NPE for NoSuchElementException
String name = opt.get();
// ✅ decide what "missing" means: default, computed default, or failure
String name = opt.orElse("anonymous");
String name = opt.orElseThrow(() -> new IllegalArgumentException("name is required"));

// ❌ orElse evaluates its argument EVERY time — even when the value is present
opt.orElse(loadFromDatabase());          // database is hit regardless
// ✅ orElseGet takes a Supplier and runs it only when empty
opt.orElseGet(() -> loadFromDatabase());

// ❌ map with an Optional-returning function nests: Optional<Optional<Address>>
user.map(User::getAddress);
// ✅ flatMap flattens one level: Optional<Address>
user.flatMap(User::getAddress);

// ✅ map is null-safe: a null result becomes empty (no NPE in the next stage)
Optional.ofNullable(address).map(Address::getCity);     // getCity() may return null

// ✅ filter turns a present value into empty when the predicate fails
zip.filter(z -> z.length() == 5);

// ❌ Java 9+ methods compile in this build (Java 17) but do not exist on Java 8
opt.isEmpty();  opt.ifPresentOrElse(a, b);  opt.or(() -> other);  opt.orElseThrow();
// ✅ Java 8 equivalents
!opt.isPresent();  if (opt.isPresent()) { ... } else { ... };  opt.isPresent() ? opt : other.get();

// ❌ Optional<List<T>> gives callers two kinds of "nothing" to check
Optional<List<String>> tags();
// ✅ return an empty collection instead
List<String> tags();                      // Collections.emptyList() when there are none

// ❌ Optional.of(1).equals(Optional.of(1L)) is false — Integer is not Long
// ✅ compare values of the same type
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run a main demo
mvn exec:java -Dexec.mainClass="com.vbforge.optional_and_null_handling.OptionalCreationDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.optional_and_null_handling.OptionalUnwrappingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.optional_and_null_handling.OptionalChainingDemo"
```
