# oop-interfaces-and-abstraction

> **Phase 2 — Core OOP & Design** | Java 8–9 | Package: `com.vbforge.oop_interfaces_and_abstraction`

Tags: `OOP` — Interfaces, default/static methods, multiple inheritance via interfaces

> **Java 9 note:** everything is plain Java 8 except `private` interface methods (section 4 of
> `DefaultAndStaticMethodsDemo`), which need Java 9. The build uses the parent's Java 17, so no override is needed.

## What's covered

| Topic | Key points |
|---|---|
| Interface as a contract | `implements`, interface-typed variables, no `new Interface()`, dynamic dispatch through the interface type |
| Implicit modifiers | Fields are `public static final`, methods are `public abstract`; an implementer must declare `public` |
| Interface inheritance | `interface A extends B, C`, one object with many types, `Class.getInterfaces()` (direct only), least-privilege parameter types |
| JDK contracts | `Comparable` + `Collections.sort`, `Iterable` + enhanced `for`, consistency of `compareTo` and `equals` (`BigDecimal`) |
| Marker interfaces | `RandomAccess`, `Serializable`, `Cloneable` — a type with no methods, tested with `instanceof` |
| Anonymous classes & lambdas | Single-abstract-method interfaces; a second abstract method breaks every lambda |
| Default methods | Evolve an interface without breaking implementers (`removeIf`, `getOrDefault`, `forEach`); `Method.isDefault()` |
| `Interface.super.method()` | Reuse an interface's default from an override; legal only for DIRECT superinterfaces |
| Static interface methods | Called on the interface name, NOT inherited (`Logger.prefix`, `Comparator.reverseOrder()`, `Comparator.comparing`) |
| Private interface methods | Java 9; shared code for defaults, invisible to implementers |
| Conflict rules | Class wins → most specific interface wins → otherwise the class must override (the diamond) |
| Abstraction | Program to an interface, `Collections.unmodifiableList` live view, Strategy via constructor injection |
| Hiding the HOW | Package-private implementations behind a factory that returns the interface |
| Interface segregation | Small role interfaces (`CanPrint`, `CanScan`) vs a fat interface that forces `UnsupportedOperationException` |
| Skeletal implementation | Interface + `abstract` class (`Sensor` + `AbstractSensor`), like `List` + `AbstractList` |

## Files

Pattern B — one Demo/Tasks pair per concept. The supporting types of each demo live in their own
sub-package, one file per type, so you can read them side by side with the demo.

```
src/
├── main/java/com/vbforge/oop_interfaces_and_abstraction/
│   ├── InterfaceBasicsDemo.java              ← theory + annotated examples (contracts, JDK interfaces, markers)
│   ├── InterfaceBasicsTasks.java             ← 🎯 your tasks — implement these methods
│   ├── DefaultAndStaticMethodsDemo.java      ← theory + annotated examples (default/static/private, diamond)
│   ├── DefaultAndStaticMethodsTasks.java     ← 🎯 your tasks — implement these methods
│   ├── AbstractionDemo.java                  ← theory + annotated examples (strategy, hiding, ISP, skeletal)
│   ├── AbstractionTasks.java                 ← 🎯 your tasks — implement these methods
│   ├── basics/                               ← types for InterfaceBasicsDemo
│   │   ├── Notifier, EmailNotifier, SmsNotifier          (contract + constants)
│   │   ├── ReadableStore, WritableStore, ReadWriteStore, MemoryStore   (interface inheritance)
│   │   └── Version, Countdown                            (Comparable, Iterable)
│   ├── defaults/                             ← types for DefaultAndStaticMethodsDemo
│   │   ├── Logger, PlainLogger, ShoutingLogger           (default / static / private methods)
│   │   └── Flyer, Swimmer, Duck, Animal, Bird, Seabird, Gull   (conflict rules and the diamond)
│   └── abstraction/                          ← types for AbstractionDemo
│       ├── DiscountPolicy, DiscountPolicies, Checkout    (+ package-private NoDiscount, PercentDiscount, ThresholdDiscount)
│       └── Sensor, AbstractSensor, TemperatureSensor, HumiditySensor
└── test/java/com/vbforge/oop_interfaces_and_abstraction/
    ├── InterfaceBasicsDemoTest.java          ← tests for the demo (already green)
    ├── InterfaceBasicsTasksTest.java         ← 🧪 tests for your tasks — all must pass
    ├── DefaultAndStaticMethodsDemoTest.java  ← tests for the demo (already green)
    ├── DefaultAndStaticMethodsTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── AbstractionDemoTest.java              ← tests for the demo (already green)
    └── AbstractionTasksTest.java             ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement every stub. Each has a detailed Javadoc. The interfaces are
already declared — you write the bodies of the overridden, default, static and helper methods.

| # | Method | Topic |
|---|---|---|
| **InterfaceBasicsTasks** | | |
| 1 | `Temperature.compareTo(other)` | Implementing `Comparable` correctly (`Double.compare`) |
| 2 | `Playlist.iterator()` | Implementing `Iterable` so the class works in a `for` loop |
| 3 | `totalPrice(items)` | Programming against an interface — no `instanceof` |
| 4 | `combine(channels)` | Returning a new implementation (lambda) + `String.join` |
| 5 | `interfaceNames(type)` | `Class.getInterfaces()` — direct interfaces only |
| 6 | `implementsInterface(type, iface)` | `isAssignableFrom` + `isInterface` |
| **DefaultAndStaticMethodsTasks** | | |
| 1 | `Taxable.priceWithTax(rate)` | A default method built on an abstract one |
| 2 | `Validator.and(other)` | Default method returning a lambda, short-circuiting |
| 3 | `Validator.negate()` | Default method returning a lambda |
| 4 | `Validator.notEmpty()` | Static factory method in an interface |
| 5 | `AmphibiousRobot.move()` | Resolving the diamond with `Walker.super` / `Swimmer.super` |
| 6 | `defaultMethodNames(iface)` | `Method.isDefault()`, `getDeclaredMethods()` + sorting (order is unspecified) |
| **AbstractionTasks** | | |
| 1 | `flatRate(rate)` | Strategy: a policy hidden behind the interface |
| 2 | `freeOver(threshold, rate)` | Strategy with captured configuration |
| 3 | `totalShipping(orderTotals, policy)` | Depending on the abstraction, not a concrete policy |
| 4 | `FixedCounter.step()` | Filling in the abstract step of a skeletal implementation |
| 5 | `DoublingCounter.step()` | Abstract step that reads the parent's state |
| 6 | `capabilities(device)` | `instanceof` against role interfaces (segregation) |

```bash
mvn test -Dtest=InterfaceBasicsTasksTest
mvn test -Dtest=DefaultAndStaticMethodsTasksTest
mvn test -Dtest=AbstractionTasksTest
```

Every task group is independent — do them in any order. All 18 task groups must go **GREEN** ✅ —
remove the `@Disabled` on each `@Nested` class as you go.

## Key things to remember

```java
// 1. Interface members are implicitly public — the implementing method MUST be public too
interface Notifier { String send(String message); }          // = public abstract String send(String)
class Email implements Notifier {
    String send(String m) { ... }                            // ❌ compile error: weaker access privileges
    public String send(String m) { ... }                     // ✅
}

// 2. You cannot instantiate an interface — but you can implement it inline
Notifier a = new Notifier();                                 // ❌ compile error: abstract
Notifier b = message -> "LAMBDA: " + message;                // ✅ only if there is exactly ONE abstract method
// adding a SECOND abstract method later silently breaks every lambda that implements it

// 3. Interface fields are constants, not state
interface Config { int MAX = 20; }                           // ✅ public static final — shared by all implementers
// there is no way to declare a per-object field in an interface → use an abstract class for state

// 4. Keep compareTo consistent with equals
new BigDecimal("1.0").equals(new BigDecimal("1.00"));        // ❌ false …
new BigDecimal("1.0").compareTo(new BigDecimal("1.00"));     // … but 0 → HashSet keeps 2, TreeSet keeps 1
Integer.compare(a, b);                                       // ✅ never `return a - b` (overflows) or `(int) (x - y)` (rounds 0.5 to 0)

// 5. Default methods: the three conflict rules, in this order
class Bird extends Animal implements Flyer { }               // 1. a CLASS method beats an interface default → "walk"
class Gull implements Flyer, Seabird { }                     // 2. the more specific interface wins (Seabird extends Flyer) → "glide"
class Duck implements Flyer, Swimmer { }                     // 3. ❌ compile error: unrelated defaults — you must choose
class Duck implements Flyer, Swimmer {
    public String move() { return Flyer.super.move() + " & " + Swimmer.super.move(); }   // ✅ explicit resolution
}

// 6. Interface.super is NOT the same as super
class S implements Logger { public void warn(String m) { super.warn(m); } }          // ❌ looks in the superCLASS
class S implements Logger { public void warn(String m) { Logger.super.warn(m); } }   // ✅ the interface's default
class T extends S { public void warn(String m) { Logger.super.warn(m); } }           // ❌ Logger is not a DIRECT superinterface of T

// 7. A default method cannot replace an Object method
interface L { default String toString() { return "x"; } }    // ❌ compile error: class methods (Object) always win

// 8. Static interface methods are not inherited
class PlainLogger implements Logger { }
new PlainLogger().prefix("x");                               // ❌ cannot find symbol
Logger.prefix("x");                                          // ✅ call it on the interface

// 9. Private interface methods need Java 9
interface Logger { private String decorate(String level, String m) { ... } }   // ✅ Java 9+, ❌ with -source 8

// 10. Declare the interface type, create the concrete one
List<String> list = new ArrayList<>();                       // ✅ swap for LinkedList without touching callers
ArrayList<String> list2 = new ArrayList<>();                 // ❌ every signature that takes it now demands ArrayList

// 11. An unmodifiable view is LIVE, not a snapshot
List<String> view = Collections.unmodifiableList(source);
source.add("c");                                             // ❌ surprises callers: the view now contains "c"
List<String> snapshot = new ArrayList<>(source);             // ✅ copy when you need a snapshot

// 12. Hide implementations, expose abstractions
class PercentDiscount implements DiscountPolicy { ... }      // ✅ package-private: callers can't depend on it
public static DiscountPolicy percent(double p) { ... }       // ✅ the factory returns the INTERFACE, not PercentDiscount

// 13. Segregate fat interfaces — otherwise implementers have to lie
interface Machine { String print(String d); String scan(); }
class OldPrinter implements Machine { public String scan() { throw new UnsupportedOperationException(); } }   // ❌ a broken promise
interface CanPrint { ... }  interface CanScan { ... }        // ✅ implement only what you can really do

// 14. Skeletal implementation: the interface stays the contract, the abstract class is only a helper
public abstract class AbstractSensor implements Sensor { ... }   // ✅ leaves read() abstract; holds the shared `id`
void show(AbstractSensor s) { ... }                              // ❌ clients should depend on `Sensor`, not on the helper
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run the three demos
mvn exec:java -Dexec.mainClass="com.vbforge.oop_interfaces_and_abstraction.InterfaceBasicsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_interfaces_and_abstraction.DefaultAndStaticMethodsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_interfaces_and_abstraction.AbstractionDemo"
```
