# mockito-and-tdd

> **Phase 4 — Exceptions & Collections** | Java 8 | Package: `com.vbforge.mockito_and_tdd`

## What's covered

| Topic | Key points |
|---|---|
| Test doubles | Dummy / Stub / Fake / Spy / Mock — five roles, each hand-written first; state vs interaction verification |
| Mockito basics | `@Mock`, `@InjectMocks`, `when/thenReturn/thenThrow`, `doThrow` for `void`, `verify`, `times`, `never`, `InOrder`, `lenient()`, `spy` + `doReturn` |
| Argument captors | `ArgumentCaptor`, `@Captor`, `capture()` inside `verify`, `getValue` vs `getAllValues`, `argThat` vs captor, captors store references |
| Red-green-refactor | Write a failing test → simplest code to pass → refactor on green; fake-it + triangulation; String Calculator kata |
| When not to mock | Value objects, types you don't own, outcomes over call counts, fake vs mock, wrap `Clock`-style APIs in your own interface |

## Files

```
src/
├── main/java/com/vbforge/mockito_and_tdd/
│   ├── TestDoublesDemo.java         ← theory + hand-written dummy/stub/fake/spy/mock
│   ├── TestDoublesTasks.java        ← 🎯 your tasks — implement these methods
│   ├── MockitoBasicsDemo.java       ← SUT + collaborators (live Mockito code is in the test)
│   ├── MockitoBasicsTasks.java      ← 🎯 your tasks — implement these methods
│   ├── ArgumentCaptorsDemo.java     ← SUT + hand-written recorders (what a captor is)
│   ├── ArgumentCaptorsTasks.java    ← 🎯 your tasks — implement these methods
│   ├── TddCycleDemo.java            ← String Calculator: red → green → refactor, frozen as V1/V2/V3
│   ├── TddCycleTasks.java           ← 🎯 your tasks — implement these methods
│   ├── WhenNotToMockDemo.java       ← value objects, own-your-boundaries, fake vs mock
│   └── WhenNotToMockTasks.java      ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/mockito_and_tdd/
    ├── TestDoublesDemoTest.java     ← tests for the demo (already green)
    ├── TestDoublesTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── MockitoBasicsDemoTest.java   ← tests for the demo (already green) — real Mockito lives here
    ├── MockitoBasicsTasksTest.java  ← 🧪 tests for your tasks — all must pass
    ├── ArgumentCaptorsDemoTest.java ← tests for the demo (already green) — real ArgumentCaptor lives here
    ├── ArgumentCaptorsTasksTest.java← 🧪 tests for your tasks — all must pass
    ├── TddCycleDemoTest.java        ← tests for the demo (already green)
    ├── TddCycleTasksTest.java       ← 🧪 tests for your tasks — all must pass
    ├── WhenNotToMockDemoTest.java   ← tests for the demo (already green)
    └── WhenNotToMockTasksTest.java  ← 🧪 tests for your tasks — all must pass
```

> Mockito is a **test-scope** dependency, so the `*Demo.java` files (in `src/main`) cannot call it.
> They hold the system under test and run it with hand-written doubles; read each `*DemoTest.java`
> next to its Demo to see the same ideas with real Mockito.
>
> Mockito 5 needs Java 11+ at runtime, so the module builds on the repo's inherited Java 17.
> The sources themselves stay Java 8 compatible.

## 🎯 Tasks

Open each `*Tasks.java` and implement all 15 methods. Each has a detailed Javadoc.

| # | Method | Topic |
|---|---|---|
| 1 | `TestDoublesTasks.registerUser(repo, sender, name, email)` | Fake repo + spy sender; check-before-act ordering |
| 2 | `TestDoublesTasks.countActiveUsers(repo)` | Works against a stub as well as a fake |
| 3 | `TestDoublesTasks.sendNewsletter(repo, sender, subject, body)` | Spy records calls in order; dummy proves "never called" |
| 4 | `MockitoBasicsTasks.checkout(gateway, notifier, id, cents)` | `when/thenReturn`, `verify`, `never()` |
| 5 | `MockitoBasicsTasks.chargeWithRetry(gateway, id, cents, max)` | `thenThrow` chains, `times(n)`, rethrow last exception |
| 6 | `MockitoBasicsTasks.chargeAll(gateway, amounts)` | `InOrder`, scripted answers per argument |
| 7 | `ArgumentCaptorsTasks.splitAndSave(repo, customer, lines)` | `getAllValues()` on a `void` collaborator call |
| 8 | `ArgumentCaptorsTasks.publishSummary(publisher, lines)` | Capture a `String` payload next to `eq(...)` |
| 9 | `ArgumentCaptorsTasks.applyDiscount(repo, order, percent)` | Capture a built immutable object; validation before side effects |
| 10 | `TddCycleTasks.fizzBuzz(n)` | One red at a time; order of checks |
| 11 | `TddCycleTasks.isValidPassword(pw)` | One rule per red-green step; `null`-safe |
| 12 | `TddCycleTasks.toRoman(n)` | Triangulation → "making change" with subtractive pairs |
| 13 | `WhenNotToMockTasks.totalWithTax(tax, items, region)` | Real value objects + a fake; no mocks |
| 14 | `WhenNotToMockTasks.cheapest(items)` | Pure function — nothing to mock |
| 15 | `WhenNotToMockTasks.splitEvenly(total, ways)` | Property-style assertions on outcomes |

```bash
mvn test -Dtest=TestDoublesTasksTest
mvn test -Dtest=MockitoBasicsTasksTest
mvn test -Dtest=ArgumentCaptorsTasksTest
mvn test -Dtest=TddCycleTasksTest
mvn test -Dtest=WhenNotToMockTasksTest
```

All 5 task files (15 task groups) must go **GREEN** ✅

## Key things to remember

```java
// Void methods can't use when(...) — use the do...when form
when(gateway.refund("a", 1)).thenThrow(ex);                  // ❌ compile error
doThrow(ex).when(gateway).refund("a", 1);                    // ✅

// Matchers are all-or-nothing within ONE call
when(gateway.charge(anyString(), 500L));                     // ❌ InvalidUseOfMatchersException
when(gateway.charge(anyString(), eq(500L)));                 // ✅ wrap raw values in eq()

// Unstubbed mocks return "zero values": 0, false, empty collections/Optional — and null for objects/enums
ChargeStatus s = gateway.charge("a", 1);                     // null — a test can pass for the wrong reason
// Stub what the test depends on; prefer a fake when a silent default could hide a bug

// capture() belongs INSIDE verify(...); @Captor keeps generic types
@Captor ArgumentCaptor<List<String>> captor;                 // ✅ forClass(List.class) loses <String>
verify(log, times(2)).record(captor.capture());              // times(n) must match the real call count
captor.getValue();                                           // LAST value only
captor.getAllValues();                                       // every value, in call order

// A captor stores REFERENCES, not snapshots
log.record(buffer); buffer.clear();                          // ❌ the captured list is now empty
log.record(new ArrayList<>(buffer));                         // ✅ pass a copy

// Stub spies with doReturn — when(spy.x()) runs the REAL method first
when(spyList.get(0)).thenReturn("x");                        // ❌ IndexOutOfBoundsException
doReturn("x").when(spyList).get(0);                          // ✅

// Verify behaviour that IS the requirement, not incidental calls
verify(notifier).send("acc-1", "Renewed");                   // ✅ the customer was told
verify(taxService, times(2)).basisPointsFor("DE");           // ❌ breaks the day you add a cache
assertThat(calculator.withTax(price, "DE")).isEqualTo(gross);// ✅ survives refactoring

// Don't mock what you don't own — wrap it, then fake/mock YOUR interface
interface TimeSource { long nowMillis(); }                   // ✅ production adapter is one line
List<String> l = mock(List.class); l.add("x");               // ❌ l.size() is still 0 — not a list

// Red must fail for the RIGHT reason
// expected 3 but was 1   → ✅ a meaningful red
// NullPointerException   → ❌ fix the test/setup first, then re-run to get a real red
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=TestDoublesDemoTest   # or MockitoBasicsDemoTest, ArgumentCaptorsDemoTest, TddCycleDemoTest, WhenNotToMockDemoTest

# Task tests only
mvn test -Dtest='*TasksTest'

# Run main demo (repeat for each *Demo class)
mvn exec:java -Dexec.mainClass="com.vbforge.mockito_and_tdd.TestDoublesDemo"
```
