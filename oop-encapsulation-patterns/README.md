# oop-encapsulation-patterns

> **Phase 2 — Core OOP & Design** | Java 8–16+ | Package: `com.vbforge.oop_encapsulation_patterns`

Tags: `OOP` — Access modifiers, immutability, records (Java 16+), data hiding

> The module builds with the parent's Java 17. Access modifiers, data hiding and immutability are
> plain Java 8 concepts (a few calls such as `List.copyOf` need 10+ and `String.isBlank` needs 11+;
> on Java 8 use `Collections.unmodifiableList(new ArrayList<>(src))` and `trim().isEmpty()`).
> The **records** pair needs **Java 16+**.

## What's covered

| Topic | Key points |
|---|---|
| Access modifiers | `private`, package-private (no keyword), `protected`, `public`; `Modifier.isPrivate`, `Member.getModifiers()` |
| Access on classes | top-level: `public` or package-private only; nested classes can be `private` / `protected` |
| `private` is per class | same-class instances read each other's private fields (`equals`, `compareTo`); nested-class access |
| `protected` | package-private **plus** subclasses; an override may widen (`protected` → `public`), never narrow |
| Small public surface | one public method, private helper steps; every public method is a promise |
| Gotchas | interface members are implicitly `public`; `private` constructor; `setAccessible(true)` bypasses `private` |
| Guarded state | invariants, validating constructor and mutators, no public fields |
| Leaking out / in | getter returning a mutable `List`/array; constructor keeping the caller's array; "copy in, copy out" |
| Views vs snapshots | `Collections.unmodifiableList` (view) vs `List.copyOf` (independent copy) |
| Behaviour over setters | private field + public setter is not encapsulation; "tell, don't ask" |
| Hiding the representation | `HashMap` vs two lists behind one interface — change the inside freely |
| `final` ≠ immutable | `final` freezes the reference, not the object |
| Immutable class recipe | `final` class, `private final` fields, no setters, copy mutable parts, return new objects |
| Shallow vs deep | `List.copyOf` copies the list, not the elements; immutable hash keys |
| JDK immutables | `String`, `BigDecimal`, `LocalDate`, `List.of`; the ignored-return-value bug; `Arrays.asList` ≠ immutable |
| Records | generated constructor, accessors (`x()`), `equals`/`hashCode`/`toString`; compact constructor |
| Records: traps | shallow immutability, array components compare by identity, no instance fields, implicitly `final` |
| Records vs classes | one line vs ~30; when NOT to use a record (mutable entities, inheritance, identity) |

## Files

Pattern B — one Demo/Tasks pair per concept.

```
src/
├── main/java/com/vbforge/oop_encapsulation_patterns/
│   ├── AccessModifiersDemo.java      ← theory + annotated examples (four levels, protected, small API)
│   ├── AccessModifiersTasks.java     ← 🎯 your tasks — implement these methods
│   ├── DataHidingDemo.java           ← theory + annotated examples (invariants, defensive copies, views)
│   ├── DataHidingTasks.java          ← 🎯 your tasks — implement these methods
│   ├── ImmutabilityDemo.java         ← theory + annotated examples (recipe, shallow vs deep, safe keys)
│   ├── ImmutabilityTasks.java        ← 🎯 your tasks — implement these methods
│   ├── RecordsDemo.java              ← theory + annotated examples (compact constructor, traps, vs class)
│   └── RecordsTasks.java             ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/oop_encapsulation_patterns/
    ├── AccessModifiersDemoTest.java  ← tests for the demo (already green)
    ├── AccessModifiersTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── DataHidingDemoTest.java       ← tests for the demo (already green)
    ├── DataHidingTasksTest.java      ← 🧪 tests for your tasks — all must pass
    ├── ImmutabilityDemoTest.java     ← tests for the demo (already green)
    ├── ImmutabilityTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── RecordsDemoTest.java          ← tests for the demo (already green)
    └── RecordsTasksTest.java         ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement every stub. Each has a detailed Javadoc.
The sample classes (`Levels`, `Account`, `Money`, `Rectangle`, …) are already written — you fill in
the stubs inside them and the helper methods.

| # | Method | Topic |
|---|---|---|
| **AccessModifiersTasks** | | |
| 1 | `accessLevel(member)` | `Member.getModifiers()` → four level names |
| 2 | `privateFieldNames(type)` | `getDeclaredFields()` + sorting (order is unspecified) |
| 3 | `isEncapsulated(type)` | every instance field `private`, statics ignored |
| 4 | `Temperature.isWarmerThan(other)` | `private` is per class, not per object |
| 5 | `publicApiSize(type)` | counting the public API surface |
| **DataHidingTasks** | | |
| 1 | `Playlist.tracks()` | copy OUT — independent read-only snapshot |
| 2 | `Playlist.addAll(newTracks)` | validate everything first (all-or-nothing) |
| 3 | `Account.withdraw(amount)` | guarding an invariant, two kinds of failure |
| 4 | `Account.transferTo(target, amount)` | reuse guarded methods; fail before changing anything |
| 5 | `Roster.of(names)` | copy IN, validate the copy |
| **ImmutabilityTasks** | | |
| 1 | `Money.plus(other)` | return a new object, `Math.addExact` |
| 2 | `Money.times(factor)` | exact arithmetic, `int` → `long` |
| 3 | `Money.sum(values, currency)` | reassigning a running total of immutable values |
| 4 | `Profile.withTag(tag)` | copy-on-write "wither" |
| 5 | `isImmutableShape(type)` | final class + private final instance fields |
| **RecordsTasks** | | |
| 1 | `Email` | compact constructor: normalise + validate |
| 2 | `Team` | compact constructor: validate + defensive copy |
| 3 | `Rectangle.square(side)` | static factory on a record |
| 4 | `Rectangle.scaledBy(factor)` | "wither" returning a new record |
| 5 | `Samples.equals(o)` | array component compared by content |
| 6 | `Samples.hashCode()` | hash code consistent with `equals` |
| 7 | `componentNames(type)` | `Class.isRecord()` + `getRecordComponents()` |

```bash
mvn test -Dtest=AccessModifiersTasksTest
mvn test -Dtest=DataHidingTasksTest
mvn test -Dtest=ImmutabilityTasksTest
mvn test -Dtest=RecordsTasksTest
```

Every task group is independent, except where a test name says "(also needs Task N)" — for example
`transferTo` reuses `withdraw`, and `Money.sum` reuses `plus`.

All 22 task groups must go **GREEN** ✅ — remove the `@Disabled` on each `@Nested` class as you go.

## Key things to remember

```java
// 1. Package-private has NO keyword — and `default` is not it
class Helper { }                       // ✅ package-private
default class Helper { }               // ❌ compile error: `default` is not an access modifier

// 2. `protected` is wider than package-private; an override may widen, never narrow
class Base    { protected String id() { return "base"; } }
class Derived extends Base { @Override public String id() { return "d"; } }     // ✅ widened
class Bad     extends Base { @Override private String id() { return "x"; } }   // ❌ weaker access

// 3. `private` is per CLASS — a method may read another instance's private field
boolean sameReading(Meter other) { return this.reading == other.reading; }      // ✅ no getter needed

// 4. `final` freezes the reference, not the object
final List<String> items = new ArrayList<>();
items.add("still works");              // ✅ compiles — the LIST is mutable
items = new ArrayList<>();             // ❌ compile error — the REFERENCE is final

// 5. A getter returns a reference — copy OUT; a constructor keeps a reference — copy IN
List<String> slots() { return slots; }                  // ❌ caller can clear() your list
List<String> slots() { return List.copyOf(slots); }     // ✅ independent, read-only snapshot
SafeBox(int[] sides) { int[] copy = sides.clone(); validate(copy); this.sides = copy; }   // ✅ validate the COPY

// 6. unmodifiableList is a VIEW, List.copyOf is a SNAPSHOT
List<String> view = Collections.unmodifiableList(source);   // ❌ still follows later source.add(...)
List<String> snap = List.copyOf(source);                    // ✅ never changes again (null elements → NPE)

// 7. Private field + public setter is NOT encapsulation
void setBalance(double b) { this.balance = b; }         // ❌ anyone can put -50 in
void withdraw(double amount) { /* check the rule, then change */ }   // ✅ behaviour owns the invariant

// 8. Ignoring the result of an "immutable" method does nothing
text.toUpperCase();                    // ❌ the new String is thrown away; `text` is unchanged
text = text.toUpperCase();             // ✅

// 9. Immutable class recipe: final class, private final fields, no setters, copy mutable parts
public final class Money { private final long cents; Money plus(Money o) { return new Money(...); } }   // ✅
public class OpenPoint { private final int x; int x() { return x; } }   // ❌ a subclass can override x() and change on every call

// 10. A mutable hash key silently disappears
set.add(point); point.x = 99;          // ❌ hash changed after insertion
set.contains(point);                   // false — it IS in the set, in the wrong bucket

// 11. A record is only SHALLOWLY immutable; array components compare by identity
record Team(String name, List<String> members) { }                              // ❌ shares the caller's list
record Team(String name, List<String> members) { Team { members = List.copyOf(members); } }   // ✅
record Samples(int[] values) { }       // ❌ new Samples({1}).equals(new Samples({1})) is false

// 12. Compact constructor: reassign the PARAMETER, never the field
record Username(String value) { Username { value = value.trim(); } }            // ✅
record Username(String value) { Username { this.value = value.trim(); } }       // ❌ compile error

// 13. A record has accessors, not getters, and no setters — and no extra instance fields
new Point(1, 2).x();                   // ✅
new Point(1, 2).getX();                // ❌ does not exist
record Point(int x, int y) { int z; }  // ❌ compile error: instance fields are not allowed

// 14. Access modifiers are a design tool, not security
field.setAccessible(true);             // ⚠️ reflection reads a private field — never hide secrets behind `private`
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
mvn exec:java -Dexec.mainClass="com.vbforge.oop_encapsulation_patterns.AccessModifiersDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_encapsulation_patterns.DataHidingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_encapsulation_patterns.RecordsDemo"
```
