# collections-internals-and-big-o

> **Phase 4 — Exceptions & Collections** | Java 8 | Package: `com.vbforge.collections_internals_and_big_o`

## What's covered

| Topic | Key points |
|---|---|
| `ArrayList` | Backing array allocated lazily (capacity 10), growth by 50 % (`old + old/2`), amortised O(1) `add`, O(n) shifts for `add(index)`/`remove(index)` |
| Fail-fast iterators | Hidden `modCount`, `ConcurrentModificationException`, the "second-to-last element" silent exit, `Iterator.remove()` |
| `remove(int)` vs `remove(Object)` | Overload trap on `List<Integer>`; `subList` is a live view that a parent change invalidates |
| `HashMap` buckets | Power-of-two table, `h ^ (h >>> 16)` spreading, `& (length - 1)` index, `null` key in bucket 0, collision chains |
| `HashMap` resize | Load factor 0.75, doubling at `size > threshold`, lazy allocation, presizing, iteration order changing after a resize |
| `HashMap` treeification | `TREEIFY_THRESHOLD = 8`, `MIN_TREEIFY_CAPACITY = 64` — resize a small table, tree a big one |
| `hashCode` quality | Constant `hashCode` → O(n) chain walks; mutating a key after insertion loses the entry |
| `PriorityQueue` | Binary min-heap in an array, index arithmetic, sift-up / sift-down, bottom-up heapify (O(n)), heap sort |
| `TreeMap` / `TreeSet` | Binary search trees, why insertion order matters, the degenerate chain (O(n²) to build), why red-black trees guarantee O(log n) |
| Big-O | Counting steps instead of timing, linear vs binary search, growth rates, amortised analysis, time/space trade-offs |

> ℹ️ **Java 8 content** — the sources use only Java 8 APIs and were compiled with `--release 8`.
> The real `ArrayList`/`HashMap`/`PriorityQueue` keep their arrays private (and Java 17+ forbids reflecting into `java.base`),
> so this module **re-implements their core algorithms** in `mini/` — `MiniArrayList`, `MiniHashMap`, `MiniMinHeap` — with counters
> that make the hidden costs visible. The tests prove the re-implementations are faithful: `MiniHashMap` and `MiniMinHeap`
> reproduce the exact iteration order / array layout of the real `HashMap` and `PriorityQueue`.
> Behaviour of the collections themselves (usage, comparators, views) is the subject of `collections-framework-advanced`.

## Files

```
src/
├── main/java/com/vbforge/collections_internals_and_big_o/
│   ├── ArrayListInternalsDemo.java     ← theory: growth, amortised O(1), shifting, modCount, remove overload, subList
│   ├── ArrayListInternalsTasks.java    ← 🎯 your tasks — implement these methods
│   ├── HashMapInternalsDemo.java       ← theory: spread + mask, chains, load factor, resize, treeify, bad hashCode
│   ├── HashMapInternalsTasks.java      ← 🎯 your tasks — implement these methods
│   ├── HeapInternalsDemo.java          ← theory: heap in an array, sift up/down, heapify, heap sort
│   ├── HeapInternalsTasks.java         ← 🎯 your tasks — implement these methods
│   ├── TreeInternalsDemo.java          ← theory: BST shape vs insertion order, TreeMap's guaranteed balance
│   ├── TreeInternalsTasks.java         ← 🎯 your tasks — implement these methods
│   ├── BigODemo.java                   ← theory: step counting, growth rates, quadratic vs linear, amortised cost
│   ├── BigOTasks.java                  ← 🎯 your tasks — implement these methods
│   ├── mini/
│   │   ├── MiniArrayList.java          ← ArrayList's storage strategy with shift/copy/grow counters
│   │   ├── MiniHashMap.java            ← HashMap's buckets, spread, resize with chain/probe inspection
│   │   └── MiniMinHeap.java            ← PriorityQueue's binary heap with a move counter
│   └── model/
│       └── BstNode.java                ← plain binary-search-tree node (public mutable left/right)
└── test/java/com/vbforge/collections_internals_and_big_o/
    ├── ArrayListInternalsDemoTest.java     ← tests for the demo
    ├── ArrayListInternalsTasksTest.java    ← 🧪 tests for your tasks — remove @Disabled per task
    ├── HashMapInternalsDemoTest.java       ← tests for the demo (incl. Mini vs real HashMap order)
    ├── HashMapInternalsTasksTest.java      ← 🧪 tests for your tasks — remove @Disabled per task
    ├── HeapInternalsDemoTest.java          ← tests for the demo (incl. Mini vs real PriorityQueue layout)
    ├── HeapInternalsTasksTest.java         ← 🧪 tests for your tasks — remove @Disabled per task
    ├── TreeInternalsDemoTest.java          ← tests for the demo
    ├── TreeInternalsTasksTest.java         ← 🧪 tests for your tasks — remove @Disabled per task
    ├── BigODemoTest.java                   ← tests for the demo
    └── BigOTasksTest.java                  ← 🧪 tests for your tasks — remove @Disabled per task
```

## 🎯 Tasks

Open each `*Tasks.java` and implement the methods. Every method has a Javadoc with
examples and hints. Each task has its own `@Nested` test class marked `@Disabled` —
remove the annotation from the task you are working on.

| # | Method | Topic |
|---|---|---|
| 1 | `ArrayListInternalsTasks.grow(oldCapacity, minCapacity)` | The 50 % growth rule |
| 2 | `ArrayListInternalsTasks.reallocations(adds)` | Counting array allocations — why it is logarithmic |
| 3 | `ArrayListInternalsTasks.insertAt(elements, size, index, value)` | Shift right with `System.arraycopy`, grow when full |
| 4 | `ArrayListInternalsTasks.removeAt(elements, size, index)` | Shift left, clear the freed slot |
| 5 | `ArrayListInternalsTasks.totalShiftsForFrontInserts(count)` | Why front inserts are O(n²) |
| 6 | `HashMapInternalsTasks.spread(hashCode)` | `h ^ (h >>> 16)` |
| 7 | `HashMapInternalsTasks.bucketIndex(key, tableSize)` | Spread + mask, `null` key |
| 8 | `HashMapInternalsTasks.tableSizeFor(capacity)` | Next power of two, with a cap |
| 9 | `HashMapInternalsTasks.resizeCount(entries)` | Load factor 0.75 and doubling |
| 10 | `HashMapInternalsTasks.iterationOrder(keys, tableSize)` | Predicting `HashMap` iteration order |
| 11 | `HashMapInternalsTasks.binAction(nodesInBinBeforeInsert, tableSize)` | none / resize / treeify |
| 12 | `HeapInternalsTasks.relatives(index)` | Parent and children of a heap index |
| 13 | `HeapInternalsTasks.siftUp(heap, index)` | What `offer` does |
| 14 | `HeapInternalsTasks.siftDown(heap, size, index)` | What `poll` does |
| 15 | `HeapInternalsTasks.heapify(values)` | Bottom-up O(n) heap construction |
| 16 | `HeapInternalsTasks.heapSort(values)` | Heap sort in O(n log n) |
| 17 | `TreeInternalsTasks.height(root)` | Tree height |
| 18 | `TreeInternalsTasks.contains(root, value)` | One path from the root, iteratively |
| 19 | `TreeInternalsTasks.remove(root, value)` | BST delete: leaf, one child, two children |
| 20 | `TreeInternalsTasks.isValidBst(root)` | Bounds instead of parent/child checks |
| 21 | `TreeInternalsTasks.kthSmallest(root, k)` | In-order traversal with early stop |
| 22 | `BigOTasks.binarySearchWorstCase(n)` | floor(log₂ n) + 1 by halving |
| 23 | `BigOTasks.hasDuplicates(data)` | O(n) with a hash set — tested on 500 000 elements |
| 24 | `BigOTasks.twoSum(numbers, target)` | O(n) with a hash map — tested on 500 000 elements |
| 25 | `BigOTasks.complexityOf(structure, operation)` | The Big-O table below |

Run your solutions with:
```bash
mvn test -Dtest=ArrayListInternalsTasksTest
mvn test -Dtest=HashMapInternalsTasksTest
mvn test -Dtest=HeapInternalsTasksTest
mvn test -Dtest=TreeInternalsTasksTest
mvn test -Dtest=BigOTasksTest
```
All 25 task groups must go **GREEN** ✅

> ⏱️ Tasks 23 and 24 are tested with a time limit of two seconds: an O(n²) solution is *correct* but fails — and takes
> a long time to finish. That is the point of Big-O.

## Key things to remember

```java
// ✅ ArrayList: capacity 0 → 10 on the first add, then old + old/2: 10, 15, 22, 33, 49, 73, ...
//    add(e) is amortised O(1); add(0, e) / remove(0) shift EVERY element (O(n)); n front inserts cost O(n²)

// ❌ removing inside a for-each loop throws ConcurrentModificationException (hidden modCount) ...
for (Integer n : list) { if (n % 2 == 0) list.remove(n); }
// ⚠️ ... except when you remove the SECOND-TO-LAST element: hasNext() is just "cursor != size", the loop ends silently
// ✅ Iterator.remove() or removeIf
list.removeIf(n -> n % 2 == 0);

// ❌ on a List<Integer>, remove(7) removes the element at INDEX 7
list.remove(7);
// ✅ box the value to remove it by equality
list.remove(Integer.valueOf(7));

// ✅ HashMap: index = spread(hash) & (table.length - 1), spread(h) = h ^ (h >>> 16); table length is a power of two
//    resize when size > capacity * 0.75 (12 → 24 → 48 ...); each resize re-distributes EVERY entry (O(n))
// ✅ presize when you know the count: new HashMap<>((int) (expected / 0.75f) + 1)

// ❌ the iteration order of HashMap/HashSet is an implementation detail — it changes when the table resizes
// ✅ LinkedHashMap for insertion order, TreeMap for sorted order

// ❌ a constant (or badly distributed) hashCode degrades lookups to O(n)  (O(log n) once a chain is treeified: bin ≥ 8 nodes AND table ≥ 64)
public int hashCode() { return 42; }
// ✅ derive hashCode from the same fields as equals(); use Objects.hash(...)

// ❌ mutating a field that takes part in hashCode() after the key was inserted makes the entry unreachable (but it still counts in size())
// ✅ use immutable keys

// ✅ PriorityQueue = binary min-heap in an array: parent (i-1)/2, children 2i+1 and 2i+2
//    offer: add at the end + sift UP; poll: move the last element to the root + sift DOWN  → both O(log n), peek O(1)
//    new PriorityQueue<>(collection) heapifies bottom-up in O(n); iterating it shows the ARRAY, not sorted order

// ❌ a plain BST built from sorted input is a linked list: height n, building costs n(n-1)/2 comparisons
// ✅ TreeMap/TreeSet are red-black trees: height ≤ 2·log₂(n+1), every put/get/remove is O(log n)

// ✅ Big-O describes GROWTH: doubling n → O(log n) +1 step, O(n) ×2, O(n²) ×4; "amortised" and "average" mean O(1) in practice,
//    but a single operation may still be expensive (array copy, resize, hash collisions)
```

### Big-O cheat sheet

```
ArrayList      get, set, add(e) [amortised]                          O(1)
               add(index, e), remove(index), contains, indexOf       O(n)
LinkedList     addFirst, addLast, removeFirst, removeLast            O(1)
               get(index), add(index, e), contains                   O(n)
ArrayDeque     addFirst, addLast, pollFirst, pollLast, peekFirst     O(1)
               contains                                              O(n)
HashMap        get, put, remove, containsKey [average]               O(1)
               containsValue                                         O(n)
LinkedHashMap  get, put, remove, containsKey                         O(1)
HashSet        add, remove, contains [average]                       O(1)
TreeMap        get, put, remove, containsKey, firstKey, lastKey,
               floorKey, ceilingKey                                  O(log n)
               containsValue                                         O(n)
TreeSet        add, remove, contains, first, last, floor, ceiling    O(log n)
PriorityQueue  peek                                                  O(1)
               offer, poll                                           O(log n)
               contains, remove(Object)                              O(n)
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
mvn exec:java -Dexec.mainClass="com.vbforge.collections_internals_and_big_o.ArrayListInternalsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_internals_and_big_o.HashMapInternalsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_internals_and_big_o.HeapInternalsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_internals_and_big_o.TreeInternalsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_internals_and_big_o.BigODemo"
```
