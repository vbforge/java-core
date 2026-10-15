# equals-hashcode-and-object-contract

> **Phase 2 — Core OOP & Design** | Java 8–16+ | Package: `com.vbforge.equals_hashcode_and_object_contract`

Tags: `OOP` — equals/hashCode/toString/Comparable contracts, mutable keys in hash collections, records

> The module builds with the parent's Java 17. The contracts themselves are plain Java 8; the few
> places that need more are marked in the demo comments (`List.copyOf` needs 10+, `String.isBlank`
> needs 11+, records and `instanceof` patterns need 16+).

## What's covered

| Topic | Key points |
|---|---|
| `equals` contract | reflexive, symmetric, transitive, consistent, `null` → `false`; `instanceof` before the cast |
| Default `equals` | `Object.equals` is identity (`==`); `==` vs `equals` for `String` |
| Overload trap | `equals(Point)` is NOT an override — `List.contains` ignores it; always write `@Override` |
| Symmetry | never be "equal to a `String`"; `x.equals(y)` must agree with `y.equals(x)` |
| Inheritance | `instanceof` breaks transitivity, `getClass()` breaks substitution — prefer `final` or composition |
| Doubles and records | `NaN` and `-0.0` with `Double.compare`; generated record `equals`; null components |
| Null fields, arrays | `Objects.equals`, `Arrays.equals`, `Arrays.deepEquals` |
| `hashCode` contract | equal objects → equal hashes; unequal objects may collide; stable within one run |
| `equals` without `hashCode` | `HashSet` duplicates, `contains` fails — the classic bug |
| Hash quality | constant hash is legal but O(n); distinct-hash count; the 31 recipe, `Objects.hash`, `Arrays.hashCode` |
| Mutable keys | the vanished `HashMap` entry; remove → mutate → re-insert; immutable keys |
| Caching | immutable classes cache their hash (`String`); never for mutable fields |
| `toString` | default `Class@hex`; readable stable format; implicit calls; null-safe (`Objects.toString`) |
| `toString` traps | cyclic references → `StackOverflowError`; sensitive data and masking; arrays print `[I@…` |
| `Comparable` contract | antisymmetry, transitivity, `compareTo(null)` throws; numeric not textual order |
| Subtraction trap | `a - b` overflows — use `Integer.compare` |
| Consistent with `equals` | the `BigDecimal` `1.0` vs `1.00` surprise; `HashSet` vs `TreeSet` |
| `Comparator` | `comparing`, `thenComparing`, `reversed`, `nullsFirst/Last`; stable sorts; `TreeSet` uses `compareTo` |
| Records | `Comparable` via a `static final Comparator`; consistency with the generated `equals` |

## Files

Pattern B — one Demo/Tasks pair per concept.

```
src/
├── main/java/com/vbforge/equals_hashcode_and_object_contract/
│   ├── EqualsContractDemo.java       ← theory + annotated examples (five rules, traps, doubles, records)
│   ├── EqualsContractTasks.java      ← 🎯 your tasks — implement these methods
│   ├── HashCodeDemo.java             ← theory + annotated examples (contract, collisions, mutable keys)
│   ├── HashCodeTasks.java            ← 🎯 your tasks — implement these methods
│   ├── ToStringDemo.java             ← theory + annotated examples (default, cycles, secrets, arrays)
│   ├── ToStringTasks.java            ← 🎯 your tasks — implement these methods
│   ├── ComparableDemo.java           ← theory + annotated examples (compareTo, Comparator, TreeSet)
│   └── ComparableTasks.java          ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/equals_hashcode_and_object_contract/
    ├── EqualsContractDemoTest.java   ← tests for the demo (already green)
    ├── EqualsContractTasksTest.java  ← 🧪 tests for your tasks — all must pass
    ├── HashCodeDemoTest.java         ← tests for the demo (already green)
    ├── HashCodeTasksTest.java        ← 🧪 tests for your tasks — all must pass
    ├── ToStringDemoTest.java         ← tests for the demo (already green)
    ├── ToStringTasksTest.java        ← 🧪 tests for your tasks — all must pass
    ├── ComparableDemoTest.java       ← tests for the demo (already green)
    └── ComparableTasksTest.java      ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement every stub. Each has a detailed Javadoc.
The classes (`Point`, `Name`, `Measurement`, `Version`, `Person`, …) are already written — you fill in
the stubs. In `HashCodeTasks` the `equals` of every class is given; your `hashCode` must stay consistent with it.

| # | Method | Topic |
|---|---|---|
| **EqualsContractTasks** | | |
| 1 | `Point.equals(o)` | `instanceof` before the cast, `null` → `false` |
| 2 | `Name.equals(o)` | case-insensitive, never equal to a `String` |
| 3 | `Measurement.equals(o)` | `double` via `Double.compare`, nullable field via `Objects.equals` |
| 4 | `UnorderedPair.equals(o)` | both element orders match, careful with `(x,x)` |
| 5 | `violatesSymmetry(a, b)` | calling `equals` in both directions |
| 6 | `overridesEquals(type)` | `getDeclaredMethod("equals", Object.class)` — override vs overload |
| **HashCodeTasks** | | |
| 1 | `Point.hashCode()` | the 31 recipe / `Objects.hash`; order-sensitive, good spread |
| 2 | `Name.hashCode()` | consistent with a case-insensitive `equals` |
| 3 | `Measurement.hashCode()` | `NaN`, `null` unit, `Double.hashCode` |
| 4 | `UnorderedPair.hashCode()` | order-independent combination |
| 5 | `isConsistentWithEquals(a, b)` | "equal → same hash" as `!A \|\| B` |
| 6 | `isLostAfterMutation(key)` | add → mutate → `contains` in a `HashSet` |
| **ToStringTasks** | | |
| 1 | `Point.toString()` | an exact, stable format |
| 2 | `Account.toString()` | masking sensitive data, `null` field |
| 3 | `Node.toString()` | break a parent/child cycle by printing only the name |
| 4 | `defaultFormat(o)` | `getClass().getName() + "@" + hex(hashCode())` |
| 5 | `describe(o)` | null-safe, array-aware (reflection, recursion) |
| 6 | `Money.toString()` | two decimals, sign of negative amounts, `Locale.ROOT` |
| **ComparableTasks** | | |
| 1 | `Version.compareTo(other)` | field by field with `Integer.compare`, no overflow |
| 2 | `byAgeThenName()` | `comparingInt` + `thenComparing` |
| 3 | `byNameNullsLast()` | `comparing(key, nullsLast(...))` |
| 4 | `safeCompare(a, b)` | the subtraction trap |
| 5 | `isConsistentWithEquals(a, b)` | `(compareTo == 0) == equals` — `BigDecimal` fails it |
| 6 | `distinctSorted(items)` | `TreeSet` removes duplicates by `compareTo` |

```bash
mvn test -Dtest=EqualsContractTasksTest
mvn test -Dtest=HashCodeTasksTest
mvn test -Dtest=ToStringTasksTest
mvn test -Dtest=ComparableTasksTest
```

Every task group is independent, except where a test name says "(also needs Task N)" — for example
`isConsistentWithEquals` with `Version` needs `Version.compareTo`.

All 24 task groups must go **GREEN** ✅ — remove the `@Disabled` on each `@Nested` class as you go.

## Key things to remember

```java
// 1. equals: instanceof BEFORE the cast; it also covers null
if (!(o instanceof Point)) return false;                    // ✅ null instanceof Point is false
Point p = (Point) o;                                        // ✅ safe now

// 2. Always @Override — an overload compiles and silently does nothing
public boolean equals(Point other) { ... }                  // ❌ overload: list.contains(...) ignores it
@Override public boolean equals(Object o) { ... }           // ✅

// 3. Never be equal to another type — symmetry cannot be repaired from your side
new CaseInsensitiveText("Java").equals("java");             // true
"java".equals(new CaseInsensitiveText("Java"));             // ❌ false — String knows nothing about you

// 4. You cannot extend a class AND add a value field and keep equals correct
class ColorPoint extends Point { ... }                      // ❌ instanceof → not transitive; getClass → breaks substitution
final class Point { ... }                                   // ✅ or use composition

// 5. double fields: == breaks reflexivity (NaN) — compare like Double.equals
Double.compare(a, b) == 0                                   // ✅ NaN equals NaN, 0.0 differs from -0.0

// 6. equals and hashCode TOGETHER, using the same fields
class EqualsOnly { @Override public boolean equals(Object o) {...} }   // ❌ HashSet keeps duplicates
Objects.hash(x, y)                                          // ✅ order-sensitive, null-safe

// 7. A constant hashCode is legal, but every lookup becomes a scan
public int hashCode() { return 42; }                        // ⚠️ correct, O(n)

// 8. A mutable key vanishes from a HashMap — no exception, just null
map.put(key, "v"); key.id = 2; map.get(key);                // ❌ null; size() is still 1
map.remove(key); key.id = 2; map.put(key, "v");             // ✅ remove → mutate → re-insert

// 9. toString: null-safe building, no cycles, no secrets
name.toString()                                             // ❌ NPE when name is null
"name=" + name                                              // ✅ "name=null"
"Child(" + parent + ")" // parent prints children → ...      // ❌ StackOverflowError
"Child(" + parent.name + ")"                                // ✅ print the other side by name

// 10. Arrays do not print their content
"" + new int[] {1, 2}                                       // ❌ "[I@1b6d3586"
Arrays.toString(new int[] {1, 2})                           // ✅ "[1, 2]"

// 11. compareTo: never subtract
return a - b;                                               // ❌ overflows: MAX_VALUE - (-1) < 0
return Integer.compare(a, b);                               // ✅

// 12. compareTo is "recommended" to be consistent with equals — BigDecimal is not
new BigDecimal("1.0").equals(new BigDecimal("1.00"));       // false
new BigDecimal("1.0").compareTo(new BigDecimal("1.00"));    // 0 → HashSet has 2, TreeSet has 1

// 13. Sorted collections ignore equals/hashCode
new TreeSet<>(String.CASE_INSENSITIVE_ORDER)  // "a" and "A" are ONE element

// 14. A record generates equals/hashCode/toString — but not compareTo, and array components compare by identity
record Release(int major, int minor) implements Comparable<Release> { ... }   // ✅ delegate to a static final Comparator
record Samples(int[] values) { }                            // ❌ new Samples({1}).equals(new Samples({1})) is false
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run the four demos
mvn exec:java -Dexec.mainClass="com.vbforge.equals_hashcode_and_object_contract.EqualsContractDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.equals_hashcode_and_object_contract.HashCodeDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.equals_hashcode_and_object_contract.ToStringDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.equals_hashcode_and_object_contract.ComparableDemo"
```
