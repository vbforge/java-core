# collections-framework-advanced

> **Phase 4 — Exceptions & Collections** | Java 8 | Package: `com.vbforge.collections_framework_advanced`

## What's covered

| Topic | Key points |
|---|---|
| `Queue` | FIFO, `offer`/`poll`/`peek` (special value) vs `add`/`remove`/`element` (exception), bounded queues, `null` ambiguity |
| `Deque` | `addFirst`/`addLast`/`pollFirst`/`pollLast`, `push`/`pop` as a stack, `descendingIterator`, `ArrayDeque` instead of `Stack` |
| Comparators | `Comparable` vs `Comparator`, `comparing`/`thenComparing`, `reversed()` scope, `nullsFirst`/`nullsLast`, stable `List.sort`, the `a - b` trap |
| `PriorityQueue` | Min-heap, max-heap via comparator, unsorted iteration order, unstable ties, `null`/non-`Comparable` rejected |
| `TreeSet` | Sorted + unique, `floor`/`ceiling`/`lower`/`higher`, live `subSet`/`headSet`/`tailSet` views, `pollFirst`/`pollLast` |
| `TreeMap` | Sorted keys, `floorEntry`/`ceilingKey` range lookups, `firstKey` vs `firstEntry`, live `subMap` views, snapshot entries |
| Comparator-defined equality | `TreeSet`/`TreeMap` use `compare`, never `equals` — `CASE_INSENSITIVE_ORDER` merges `"Key"` and `"KEY"` |

> ℹ️ **Java 8 content** — the sources use only Java 8 APIs and were compiled with `--release 8`.
> The build itself uses the parent's Java 17 default, so Java 9+ conveniences (`List.of`, `Map.of`, `var`)
> would compile here by accident but fail on a real Java 8 toolchain — the tasks avoid them.
> Big-O internals (heap layout, red-black rotations) are the subject of `collections-internals-and-big-o`;
> this module teaches BEHAVIOUR and usage.

## Files

```
src/
├── main/java/com/vbforge/collections_framework_advanced/
│   ├── QueueDemo.java               ← theory: FIFO, offer/poll/peek, bounded queues, round robin
│   ├── QueueTasks.java              ← 🎯 your tasks — implement these methods
│   ├── DequeDemo.java               ← theory: both ends, stack via push/pop, iteration order vs Stack
│   ├── DequeTasks.java              ← 🎯 your tasks — implement these methods
│   ├── ComparatorsDemo.java         ← theory: Comparable vs Comparator, chaining, reversed(), nulls
│   ├── ComparatorsTasks.java        ← 🎯 your tasks — implement these methods
│   ├── PriorityQueueDemo.java       ← theory: min/max-heap, iteration order, unstable ties
│   ├── PriorityQueueTasks.java      ← 🎯 your tasks — implement these methods
│   ├── TreeSetDemo.java             ← theory: navigation, range views, comparator-defined equality
│   ├── TreeSetTasks.java            ← 🎯 your tasks — implement these methods
│   ├── TreeMapDemo.java             ← theory: range lookups, first/last entries, sub-map views
│   ├── TreeMapTasks.java            ← 🎯 your tasks — implement these methods
│   └── model/
│       ├── Employee.java            ← tiny model for comparator demos/tasks (no equals/Comparable on purpose)
│       └── Ticket.java              ← tiny model for PriorityQueue demos/tasks (smaller priority = more urgent)
└── test/java/com/vbforge/collections_framework_advanced/
    ├── QueueDemoTest.java               ← tests for the demo
    ├── QueueTasksTest.java              ← 🧪 tests for your tasks — remove @Disabled per task
    ├── DequeDemoTest.java               ← tests for the demo
    ├── DequeTasksTest.java              ← 🧪 tests for your tasks — remove @Disabled per task
    ├── ComparatorsDemoTest.java         ← tests for the demo
    ├── ComparatorsTasksTest.java        ← 🧪 tests for your tasks — remove @Disabled per task
    ├── PriorityQueueDemoTest.java       ← tests for the demo
    ├── PriorityQueueTasksTest.java      ← 🧪 tests for your tasks — remove @Disabled per task
    ├── TreeSetDemoTest.java             ← tests for the demo
    ├── TreeSetTasksTest.java            ← 🧪 tests for your tasks — remove @Disabled per task
    ├── TreeMapDemoTest.java             ← tests for the demo
    └── TreeMapTasksTest.java            ← 🧪 tests for your tasks — remove @Disabled per task
```

## 🎯 Tasks

Open each `*Tasks.java` and implement the methods. Every method has a Javadoc with
examples and hints. Each task has its own `@Nested` test class marked `@Disabled` —
remove the annotation from the task you are working on.

| # | Method | Topic |
|---|---|---|
| 1 | `QueueTasks.drainToList(queue)` | `poll` until the queue is empty |
| 2 | `QueueTasks.interleave(first, second)` | Consuming two queues alternately |
| 3 | `QueueTasks.lastSurvivor(n, k)` | Josephus problem — rotate with `poll` + `offer` |
| 4 | `QueueTasks.recentCounts(timestamps, windowMs)` | Sliding time window, expire from the head |
| 5 | `DequeTasks.areBracketsBalanced(text)` | `ArrayDeque` as a stack |
| 6 | `DequeTasks.evaluateRpn(tokens)` | Stack evaluation, operand order |
| 7 | `DequeTasks.simplifyPath(path)` | Stack + iteration order of a deque |
| 8 | `DequeTasks.slidingWindowMax(numbers, k)` | Monotonic deque of indices |
| 9 | `ComparatorsTasks.byLengthThenAlphabetical()` | Key extractor + tie-breaker |
| 10 | `ComparatorsTasks.sortBySalaryDescThenName(employees)` | Reversing only one key, sorting a copy |
| 11 | `ComparatorsTasks.caseInsensitiveNullsLast()` | `CASE_INSENSITIVE_ORDER` + `nullsLast` |
| 12 | `ComparatorsTasks.versionComparator()` | Writing a comparator by hand, no subtraction |
| 13 | `ComparatorsTasks.highestPaid(employees, department)` | Filter + max with a comparator, ties |
| 14 | `PriorityQueueTasks.serviceOrder(tickets)` | Comparator with a tie-breaker |
| 15 | `PriorityQueueTasks.lastStoneWeight(stones)` | Max-heap simulation |
| 16 | `PriorityQueueTasks.mergeSorted(lists)` | k-way merge with a heap of positions |
| 17 | `PriorityQueueTasks.kthLargest(numbers, k)` | Bounded min-heap of size k |
| 18 | `TreeSetTasks.uniqueSortedWords(text)` | Sorted + unique in one structure |
| 19 | `TreeSetTasks.closest(numbers, target)` | `floor` + `ceiling` |
| 20 | `TreeSetTasks.countInRange(numbers, from, to)` | Range views, reversed-range gotcha |
| 21 | `TreeSetTasks.removeSmallest(numbers, count)` | `pollFirst` |
| 22 | `TreeMapTasks.shippingCost(tiers, weight)` | `floorEntry` range lookup |
| 23 | `TreeMapTasks.countWords(words)` | Comparator-defined key equality + `merge` |
| 24 | `TreeMapTasks.eventsBetween(timeline, from, to)` | `subMap` view, reversed-range gotcha |
| 25 | `TreeMapTasks.book(bookings, start, end)` | `floorEntry` + `ceilingKey` overlap check |

Run your solutions with:
```bash
mvn test -Dtest=QueueTasksTest
mvn test -Dtest=DequeTasksTest
mvn test -Dtest=ComparatorsTasksTest
mvn test -Dtest=PriorityQueueTasksTest
mvn test -Dtest=TreeSetTasksTest
mvn test -Dtest=TreeMapTasksTest
```
All 25 task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ ArrayDeque (and PriorityQueue/TreeSet) reject null; LinkedList accepts it and makes poll()/peek() ambiguous
queue.offer(null);
// ✅ never store null in a queue — null is the "empty" signal of poll()/peek()

// ❌ add()/remove()/element() throw on a full/empty queue
String head = queue.element();
// ✅ offer()/poll()/peek() return false/null — use them when "empty" is a normal case
String head = queue.peek();

// ❌ legacy Stack iterates bottom → top, ArrayDeque used as a stack iterates top → bottom
Stack<Integer> legacy = new Stack<>();
// ✅ prefer one consistent model: Deque with push/pop/peek all working on the HEAD
Deque<Integer> stack = new ArrayDeque<>();

// ❌ a PriorityQueue is NOT sorted when iterated — only the head is guaranteed to be the minimum
System.out.println(priorityQueue);          // [1, 3, 8, 5]
// ✅ poll() to read in order; and add a sequence number to the comparator if ties must stay first-come-first-served
Comparator.comparingInt(Entry::priority).thenComparingLong(Entry::sequence);

// ❌ subtraction comparators overflow for values that are far apart
(a, b) -> a - b                              // Integer.MIN_VALUE - 1 > 0 !
// ✅ Integer.compare / Comparator.comparingInt
Comparator.comparingInt(Employee::getSalary);

// ❌ reversed() flips EVERYTHING chained before it (department too)
Comparator.comparing(Employee::getDepartment).thenComparingInt(Employee::getSalary).reversed();
// ✅ reverse only the key you mean
Comparator.comparing(Employee::getDepartment)
          .thenComparing(Comparator.comparingInt(Employee::getSalary).reversed());

// ❌ TreeSet/TreeMap never call equals(): with a case-insensitive comparator "Key" and "KEY" are ONE key
TreeMap<String, Integer> map = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
// ✅ make sure the comparator is consistent with the equality you want — the first spelling is kept

// ❌ subSet/headMap/tailMap are LIVE VIEWS, and a reversed range throws IllegalArgumentException
set.subSet(10, true, 5, false);
// ✅ copy if you need a snapshot, and guard reversed ranges
new ArrayList<>(set.subSet(5, true, 10, false));

// ❌ firstKey()/lastKey()/first() throw NoSuchElementException on an empty collection
map.firstKey();
// ✅ firstEntry()/pollFirst() return null — and their entries are immutable snapshots (use put to change a value)
Map.Entry<String, Integer> first = map.firstEntry();
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
mvn exec:java -Dexec.mainClass="com.vbforge.collections_framework_advanced.QueueDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_framework_advanced.DequeDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_framework_advanced.ComparatorsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_framework_advanced.PriorityQueueDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_framework_advanced.TreeSetDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.collections_framework_advanced.TreeMapDemo"
```
