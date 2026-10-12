# oop-inheritance-and-polymorphism

> **Phase 2 — Core OOP & Design** | Java 8+ | Package: `com.vbforge.oop_inheritance_and_polymorphism`

Tags: `OOP` — Inheritance, overriding, super, polymorphism, abstract classes

## What's covered

| Topic | Key points |
|---|---|
| Inheritance | `extends`, IS-A, single inheritance, `Object` root; `private` vs `protected` access; `Class.getSuperclass()` |
| Overriding | `@Override`, covariant return types, widening access (`protected` → `public`), overload ≠ override |
| `super` | `super.method()`, `super(args)`, one level up only; virtual calls inside `super.method()` still use the runtime class |
| Constructor chaining | Parent first → initializer block → constructor body; implicit `super()`; `this(...)` delegation |
| Constructor trap | Overridable method called from a constructor sees `null`/`0` subclass fields |
| Hiding | Fields and `static` methods are hidden, not overridden — the declared type decides |
| `final` & composition | `final` methods/classes, HAS-A vs IS-A, why `Stack extends Vector` is a design mistake |
| Dynamic dispatch | Runtime type decides the override; static type vs runtime type; `getClass()` |
| Casting | Upcast, downcast, `instanceof`, `ClassCastException`, `null instanceof T` is `false` |
| Overload vs override | Overloads resolved at compile time by static types; overrides at runtime by the receiver |
| Extensibility | Add a subtype without editing generic code; covariant arrays and `ArrayStoreException` |
| Abstract classes | `abstract` methods, no direct `new`, constructors and state, partial implementation, anonymous subclass |
| Template Method | `final` skeleton + `abstract` steps + overridable hooks; `AbstractList` as a JDK example |
| Abstract class vs interface | One superclass vs many interfaces; interface fields are `public static final` |

## Files

Pattern B — one Demo/Tasks pair per concept.

```
src/
├── main/java/com/vbforge/oop_inheritance_and_polymorphism/
│   ├── InheritanceDemo.java          ← theory + annotated examples (extends, super, constructors, hiding)
│   ├── InheritanceTasks.java         ← 🎯 your tasks — implement these methods
│   ├── PolymorphismDemo.java         ← theory + annotated examples (dispatch, casting, overload vs override)
│   ├── PolymorphismTasks.java        ← 🎯 your tasks — implement these methods
│   ├── AbstractClassDemo.java        ← theory + annotated examples (abstract, Template Method, AbstractList)
│   └── AbstractClassTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/oop_inheritance_and_polymorphism/
    ├── InheritanceDemoTest.java      ← tests for the demo (already green)
    ├── InheritanceTasksTest.java     ← 🧪 tests for your tasks — all must pass
    ├── PolymorphismDemoTest.java     ← tests for the demo (already green)
    ├── PolymorphismTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── AbstractClassDemoTest.java    ← tests for the demo (already green)
    └── AbstractClassTasksTest.java   ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open each `*Tasks.java` and implement every stub. Each has a detailed Javadoc.
The class hierarchies (`Employee`, `Shape`, `BankAccount`) are already written — you fill in
the overridden methods and the helper methods.

| # | Method | Topic |
|---|---|---|
| **InheritanceTasks** | | |
| 1 | `Manager.salary()` | Override that builds on the parent's result |
| 2 | `Manager.describe()` | `super.method()` — extend instead of replace |
| 3 | `Director.salary()` | Multi-level chain: reuse the class directly above |
| 4 | `Director.describe()` | `super` across two levels |
| 5 | `inheritanceChain(type)` | `Class.getSuperclass()` walk up to `Object` |
| 6 | `isSubtypeOf(child, parent)` | IS-A test with `Class.isAssignableFrom` |
| **PolymorphismTasks** | | |
| 1 | `totalArea(shapes)` | Dynamic dispatch — no `instanceof` needed |
| 2 | `largest(shapes)` | Polymorphic comparison, tie-breaking, `null` for empty |
| 3 | `countByType(shapes)` | Runtime type via `getClass()` + `merge()` |
| 4 | `countInstancesOf(shapes, type)` | `Class.isInstance` — subclasses count too |
| 5 | `rectanglesOnly(shapes)` | `instanceof` guard before a downcast |
| 6 | `sortByAreaDescending(shapes)` | `Comparator.comparingDouble`, stable sort, copy first |
| **AbstractClassTasks** | | |
| 1 | `SavingsAccount.fee(amount)` | Implementing an abstract step |
| 2 | `SavingsAccount.canDebit(total)` | Abstract step using the parent's public API |
| 3 | `CheckingAccount.fee(amount)` | Another subclass, a different fee policy |
| 4 | `CheckingAccount.canDebit(total)` | Abstract step with subclass state (overdraft) |
| 5 | `isAbstract(type)` | `Modifier.isAbstract` on `Class.getModifiers()` |
| 6 | `abstractMethodNames(type)` | `getDeclaredMethods()` + sorting (order is unspecified) |

```bash
mvn test -Dtest=InheritanceTasksTest
mvn test -Dtest=PolymorphismTasksTest
mvn test -Dtest=AbstractClassTasksTest
```

Do the Inheritance tasks in order 1 → 4 (`describe()` prints `salary()`, and `Director` builds on `Manager`); every other task group is independent.

All 18 task groups must go **GREEN** ✅ — remove the `@Disabled` on each `@Nested` class as you go.

## Key things to remember

```java
// 1. super.method() does NOT turn off dynamic dispatch INSIDE the parent method
class Animal { String describe() { return name + " says " + sound(); } }   // sound() is virtual
class Puppy extends Dog {
    @Override String sound()    { return super.sound() + " (squeaky)"; }
    @Override String describe() { return super.describe() + "!"; }
}
new Puppy("Bo").describe();   // ✅ "Bo says Woof (squeaky)!"  — Animal.describe() still calls Puppy.sound()

// 2. ALWAYS write @Override — it turns a silent bug into a compile error
class Dog extends Animal {
    @Override public String sond() { ... }   // ❌ compile error: does not override anything (typo)
    public String sond() { ... }             // ❌ compiles, silently adds a NEW method; sound() is never overridden
}

// 3. Never call an overridable method from a constructor
class Greeter {
    Greeter() { seen = greeting(); }         // ❌ runs LoudGreeter.greeting() before suffix is assigned
}
class LoudGreeter extends Greeter {
    private final String suffix;
    LoudGreeter(String s) { super(); this.suffix = s; }
    @Override String greeting() { return "Hello" + suffix; }   // "Hellonull" during super()!
}

// 4. Fields and static methods are HIDDEN, not overridden — the DECLARED type wins
Vehicle v = new Car();
v.label;          // ❌ "vehicle-field"  (field access ignores the runtime type)
v.getLabel();     // ✅ "car-field"      (instance method → runtime type)
v.kind();         // ❌ Vehicle.kind()   (static via instance: compiles, uses the declared type)

// 5. Overloads are chosen by the STATIC type of the argument, overrides by the RUNTIME type of the receiver
Shape s = new Circle(1);
printer.print(s);                     // ❌ expects print(Circle)? → print(Shape) is called
printer.print((Circle) s);            // ✅ print(Circle) — now the static type is Circle

// 6. Downcast only after an instanceof check
Circle c = (Circle) shape;            // ❌ ClassCastException if shape is a Square
if (shape instanceof Circle) {        // ✅ guard first
    Circle c2 = (Circle) shape;
}
// null instanceof Anything → false, so the guard is also null-safe

// 7. Arrays are covariant — the compiler will not save you
Object[] objects = new String[1];
objects[0] = Integer.valueOf(1);      // ❌ compiles, throws ArrayStoreException at runtime
List<Object> list = new ArrayList<String>();   // ✅ rejected at compile time — generics are invariant

// 8. An abstract class cannot be instantiated — but it can have constructors and state
abstract class Document {
    private final String title;
    Document(String title) { this.title = title; }   // ✅ runs via super(title) from every subclass
}
Document d = new Document("x");                      // ❌ compile error: Document is abstract
Document d2 = new Document("x") { String content() { return ""; } };   // ✅ anonymous subclass

// 9. abstract cannot be combined with final, private or static
abstract final   void a();     // ❌ a final method can never be overridden — contradiction
abstract private void b();     // ❌ subclasses cannot see a private method
abstract static  void c();     // ❌ static methods are not overridden

// 10. Template Method: make the skeleton final, the steps abstract, the extras hooks
public final String export(List<String> rows) { ... header() ... format(row) ... footer() ... }   // ✅ order cannot be changed
protected abstract String format(String row);                                                     // ✅ required
protected String header() { return ""; }                                                          // ✅ optional hook

// 11. Prefer composition when it is not truly IS-A
class Stack<E> extends Vector<E> { }   // ❌ JDK mistake: inherits add(index, e), get(index) … LIFO can be broken
class Truck { private final Engine engine; }   // ✅ HAS-A — expose only what you want

// 12. Interface methods must be public in the implementing class
interface Printable { String print(); }
class Memo implements Printable { String print() { ... } }          // ❌ weaker access (package-private)
class Memo implements Printable { public String print() { ... } }   // ✅
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
mvn exec:java -Dexec.mainClass="com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_inheritance_and_polymorphism.PolymorphismDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.oop_inheritance_and_polymorphism.AbstractClassDemo"
```
