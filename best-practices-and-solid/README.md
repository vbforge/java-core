# best-practices-and-solid

> **Phase 2 — Core OOP & Design** | Java 8+ | Tags: BEST-PRACTICES | Package: `com.vbforge.best_practices_and_solid`

## What's covered

| Topic | Key points |
|---|---|
| Single Responsibility (S) | one reason to change; split validate / hash / store / notify; pure `total` + `formatTotal` instead of a method that prints |
| Open/Closed (O) | strategy interface + `Map` registry instead of `if/else` chains; `instanceof` chain vs polymorphic `Shape`; defensive copy of the registry |
| Liskov Substitution (L) | `Square extends Rectangle` trap; contract = preconditions / postconditions / invariants; throwing `UnsupportedOperationException` in an override; `Collections.unmodifiableList` |
| Interface Segregation (I) | fat `FatWorker` vs role interfaces `Workable` / `Eatable`; intersection bound `T extends A & B`; `printReport(Printer)` |
| Dependency Inversion (D) | constructor injection, `MessageSender` abstraction, `Clock.fixed` for deterministic time, composition root |
| Clean code | intention-revealing names, guard clauses, named constants instead of magic numbers, no boolean flag arguments |
| Code smells & refactoring | parameter object (`IntRange`), value object (`Money`), feature envy, characterization tests as a refactoring safety net |

> ℹ️ Source is valid **Java 8**. The module inherits the parent's Java 17 build setting — no override needed.

## Files

```
src/
├── main/java/com/vbforge/best_practices_and_solid/
│   ├── SingleResponsibilityDemo.java        ← theory + annotated examples (S)
│   ├── SingleResponsibilityTasks.java       ← 🎯 your tasks — implement these methods
│   ├── OpenClosedDemo.java                  ← theory + annotated examples (O)
│   ├── OpenClosedTasks.java                 ← 🎯 your tasks
│   ├── LiskovSubstitutionDemo.java          ← theory + annotated examples (L)
│   ├── LiskovSubstitutionTasks.java         ← 🎯 your tasks
│   ├── InterfaceSegregationDemo.java        ← theory + annotated examples (I)
│   ├── InterfaceSegregationTasks.java       ← 🎯 your tasks
│   ├── DependencyInversionDemo.java         ← theory + annotated examples (D)
│   ├── DependencyInversionTasks.java        ← 🎯 your tasks
│   ├── CleanCodeDemo.java                   ← theory + annotated examples
│   ├── CleanCodeTasks.java                  ← 🎯 your tasks
│   ├── CodeSmellsAndRefactoringDemo.java    ← theory + annotated examples
│   └── CodeSmellsAndRefactoringTasks.java   ← 🎯 your tasks
└── test/java/com/vbforge/best_practices_and_solid/
    ├── SingleResponsibilityDemoTest.java    ← tests for the demo (already green)
    ├── SingleResponsibilityTasksTest.java   ← 🧪 tests for your tasks — all must pass
    ├── OpenClosedDemoTest.java
    ├── OpenClosedTasksTest.java             ← 🧪
    ├── LiskovSubstitutionDemoTest.java
    ├── LiskovSubstitutionTasksTest.java     ← 🧪
    ├── InterfaceSegregationDemoTest.java
    ├── InterfaceSegregationTasksTest.java   ← 🧪
    ├── DependencyInversionDemoTest.java
    ├── DependencyInversionTasksTest.java    ← 🧪
    ├── CleanCodeDemoTest.java
    ├── CleanCodeTasksTest.java              ← 🧪
    ├── CodeSmellsAndRefactoringDemoTest.java
    └── CodeSmellsAndRefactoringTasksTest.java ← 🧪
```

## 🎯 Tasks

Each `*Tasks.java` method has a Javadoc with examples and hints. Every nested class in a
`*TasksTest` is `@Disabled` — remove the annotation from the one you are solving.

| # | Method | Topic |
|---|---|---|
| 1 | `SingleResponsibilityTasks.validateUsername(username)` | validation only — return all errors, no printing |
| 2 | `SingleResponsibilityTasks.calculateTotal(prices, taxRate)` | pure calculation, rounded to 2 decimals |
| 3 | `SingleResponsibilityTasks.renderReceipt(names, prices, total)` | pure formatting, `Locale.ROOT` |
| 4 | `OpenClosedTasks.totalArea(shapes)` | polymorphism instead of `instanceof` |
| 5 | `OpenClosedTasks.applyTransformations(text, transformations)` | open pipeline of `UnaryOperator`s |
| 6 | `OpenClosedTasks.shippingCost(method, weightKg, rules)` | `Map` of rules instead of `if/else` |
| 7 | `LiskovSubstitutionTasks.largestShape(shapes)` | code that works for every `Shape` |
| 8 | `LiskovSubstitutionTasks.honoursRectangleContract(candidate)` | a contract probe that exposes LSP violations |
| 9 | `LiskovSubstitutionTasks.addAllOrCopy(target, items)` | coping with read-only `List`s |
| 10 | `InterfaceSegregationTasks.totalWorkUnits(workers)` | depend on `Workable` only |
| 11 | `InterfaceSegregationTasks.lunchBreak(beings)` | depend on `Eatable` only |
| 12 | `InterfaceSegregationTasks.shift(worker)` | intersection bound `T extends Workable & Eatable` |
| 13 | `DependencyInversionTasks.notifyAll(recipients, message, sender)` | program against `MessageSender` |
| 14 | `DependencyInversionTasks.isExpired(expiresAt, clock)` | injected `Clock`, exact boundary |
| 15 | `DependencyInversionTasks.greeting(clock)` | time zone comes from the clock, not the machine |
| 16 | `CleanCodeTasks.classifyAge(age)` | guard clauses |
| 17 | `CleanCodeTasks.overtimePay(hoursWorked, hourlyRate)` | named constants instead of magic numbers |
| 18 | `CleanCodeTasks.isStrongPassword(password)` | small, readable rules |
| 19 | `CodeSmellsAndRefactoringTasks.firstDifference(legacy, refactored, from, to)` | characterization test / safety net |
| 20 | `CodeSmellsAndRefactoringTasks.sum(amounts, currency)` | value object `Money` |
| 21 | `CodeSmellsAndRefactoringTasks.overlaps(a, b)` | parameter object `IntRange` |

Run one concept's tasks with:
```bash
mvn test -Dtest=SingleResponsibilityTasksTest
```
Swap the class name for the concept you are working on. All task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ SRP: validation, hashing, storage and e-mail all change for different reasons — one method
public boolean register(String user, String email, String pw) { /* validate, hash, save, send */ }

// ✅ one small class per reason to change; the service only coordinates
List<String> errors = validator.validate(user, email, pw);
if (errors.isEmpty()) { store.save(user, hasher.hash(pw)); notifier.sendWelcome(email, user); }
// SRP != "one method per class": group what changes together, split what changes for different reasons
```

```java
// ❌ OCP: every new customer type edits tested code
if ("GOLD".equals(type)) { return price * 0.90; } else if ("SILVER".equals(type)) { ... }

// ✅ new behaviour = new map entry, the calculator is never edited
policies.put("PLATINUM", price -> price * 0.20);
// ✅ copy the map in the constructor — otherwise callers can change the rules behind your back
this.policies = new HashMap<>(policies);
```

```java
// ❌ LSP: compiles, but silently breaks code written against the base class
class MutableSquare extends MutableRectangle { /* setWidth also changes height */ }
resizeAndGetArea(new MutableSquare(1), 5, 4);   // 16, not 20

// ✅ share an abstraction (Shape.area()), not mutable state
// ❌ an override that throws UnsupportedOperationException = the hierarchy is too wide
// ⚠️ the JDK does it too: Collections.unmodifiableList(...).add(x) throws — be defensive
```

```java
// ❌ ISP: robots forced to implement eat() and sleep()
interface FatWorker { int work(); String eat(); String sleep(); }

// ✅ small role interfaces; the signature states exactly what is needed
<T extends Workable & Eatable> String shift(T worker)   // a Robot no longer compiles here
```

```java
// ❌ DIP: hidden dependencies — cannot be tested at the exact boundary
private final SmtpClient client = new SmtpClient();
return !Instant.now().isBefore(expiresAt);

// ✅ inject them; tests use Clock.fixed(...) and a lambda sender
return !clock.instant().isBefore(expiresAt);
// ✅ call `new` in one place only: the composition root
```

```java
// ❌ nested arrow code, magic numbers, boolean flag argument
if (age >= 0) { if (age < 6) { ... } else { ... } }   if (total >= 50.0) ...   format(price, true)

// ✅ guard clauses, named constants, two intention-named methods
if (age < 0 || age > MAX_AGE) throw new IllegalArgumentException(...);
if (orderTotal >= FREE_SHIPPING_THRESHOLD) return 0.0;
formatWithCurrency(price);
// ⚠️ String.format uses the default locale — pass Locale.ROOT or "12.50" becomes "12,50" in CI
```

```java
// ❌ primitive obsession: 0.1 + 0.2 == 0.30000000000000004, and dollars + euros compiles
double total = a + b;

// ✅ value object: exact cents, currency checked, equals/hashCode defined
Money.of("USD", 10).plus(Money.of("USD", 20));
// ✅ refactor with a safety net: compare old and new over many inputs BEFORE deleting the old code
// ❌ never change structure and behaviour in the same commit
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only (example: Single Responsibility)
mvn test -Dtest=SingleResponsibilityDemoTest

# Task tests only (example: Single Responsibility)
mvn test -Dtest=SingleResponsibilityTasksTest

# Run main demo (example: Single Responsibility)
mvn exec:java -Dexec.mainClass="com.vbforge.best_practices_and_solid.SingleResponsibilityDemo"
```

> Other concepts follow the same naming: `OpenClosed`, `LiskovSubstitution`, `InterfaceSegregation`,
> `DependencyInversion`, `CleanCode`, `CodeSmellsAndRefactoring` + `Demo` / `DemoTest` / `TasksTest`.
