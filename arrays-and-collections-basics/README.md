# arrays-and-collections-basics

> **Phase 1 — Basics** | Java 8+ | Package: `com.vbforge.basics.collections`

## What's covered

| Topic | Key points |
|---|---|
| Arrays | Fixed-size, zero-indexed, contiguous memory; `Arrays.sort/copyOf/fill/equals/binarySearch` |
| `ArrayList` | Dynamic array, O(1) get/set, O(n) insert/remove in middle; `subList` is a live view |
| `LinkedList` | Doubly-linked list + Deque; O(1) head/tail ops; O(n) random access |
| `HashSet` | Unique elements, no order guarantee; O(1) add/remove/contains; union/intersection/diff |
| `HashMap` | Key→value, O(1) get/put; `getOrDefault`, `putIfAbsent`, `merge`; `null` key allowed |
| Iteration | for-each, index loop, `Iterator` (safe removal), `forEach` lambda, `entrySet` |
| `Collections` utility | `sort`, `reverse`, `min`/`max`, `frequency`, `nCopies`, `unmodifiableList`, `disjoint` |

## Files

```
src/
├── main/java/com/vbforge/basics/collections/
│   ├── CollectionsDemo.java        ← theory + annotated examples
│   └── CollectionsTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/basics/collections/
    ├── CollectionsDemoTest.java     ← tests for the demo (already green)
    └── CollectionsTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `CollectionsTasks.java` and implement all 7 methods. Each has a detailed Javadoc.

| # | Method | Topic |
|---|---|---|
| 1 | `rotateRight(arr, k)` | Array manipulation — three-reversal or copyOfRange |
| 2 | `removeDuplicates(list)` | `Iterator` safe removal + `HashSet` seen-tracker |
| 3 | `wordFrequency(words)` | `HashMap` accumulation with `merge()` |
| 4 | `intersection(a, b)` | Set-lookup + order-preserving dedup |
| 5 | `groupByFirstChar(words)` | `computeIfAbsent` bucketing pattern |
| 6 | `twoSum(list, target)` | O(n) `HashMap` complement-lookup |
| 7 | `deduplicateSorted(sorted)` | Adjacent-duplicate scan on sorted array |

```bash
mvn test -Dtest=CollectionsTasksTest
```

All 7 task groups must go **GREEN** ✅

## Key things to remember

```java
// Array length is a FIELD, not a method
arr.length         // ✅
arr.length()       // ❌ compile error

// Arrays.asList() is FIXED SIZE — no add/remove
List<String> fixed = Arrays.asList("a", "b");
fixed.add("c");    // UnsupportedOperationException!
// Wrap in new ArrayList<> for a mutable copy:
List<String> mutable = new ArrayList<>(Arrays.asList("a", "b"));

// Never remove from a List during for-each — use Iterator
Iterator<String> it = list.iterator();
while (it.hasNext()) {
    if (condition(it.next())) it.remove();  // ✅ safe
}

// HashMap accumulation patterns
map.put(key, map.getOrDefault(key, 0) + 1);  // verbose but clear
map.merge(key, 1, Integer::sum);              // cleaner — prefer this

// computeIfAbsent — init a list bucket only if key is new
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);

// HashSet for O(1) membership — far faster than List.contains()
Set<Integer> lookup = new HashSet<>(listB);
for (int v : listA) {
    if (lookup.contains(v)) { ... }  // O(1) per check
}
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=CollectionsDemoTest

# Task tests only
mvn test -Dtest=CollectionsTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.collections.CollectionsDemo"
```
