# Java Core

🎯 **Comprehensive Java Core learning modules with practical examples, tests, and hands-on tasks**

[![Java](https://img.shields.io/badge/Java-17%2B-orange?style=flat-square&logo=openjdk)](https://openjdk.org/)
[![Maven](https://img.shields.io/badge/Maven-3.6%2B-blue?style=flat-square&logo=apachemaven)](https://maven.apache.org/)
[![JUnit](https://img.shields.io/badge/JUnit-5-green?style=flat-square&logo=junit5)](https://junit.org/junit5/)
[![License](https://img.shields.io/badge/License-unlicensed-lightgrey?style=flat-square)]()

🌐 **[Interactive Roadmap & Progress Tracker →](https://vbforge.github.io/java-core)**

---

## 🎓 What's inside

- **Fundamentals** — variables, operators, control flow, strings, collections basics
- **Core OOP** — inheritance, interfaces, encapsulation, enums, sealed classes
- **Functional & Streams** — lambdas, method references, Stream API, Optional
- **Exceptions, Generics & Collections** — advanced collection patterns, wildcard types
- **I/O & Date-Time** — NIO.2, serialization, modern date-time API, logging
- **Concurrency** — multithreading, ExecutorService, concurrent collections, virtual threads
- **JVM Internals** — memory model, GC basics, reference types
- **Modern Java** — HTTP Client, JPMS modules, pattern matching
- **Advanced Topics** — annotations, reflection, design patterns, SOLID principles
- **Reference** — curated interview preparation docs and code snippets

Each module contains:
- Annotated source code explaining *why*, not just *what*
- JUnit 5 + AssertJ tests that verify every concept
- **🎯 Tasks** — method stubs for you to implement
- **🧪 Task tests** — your implementation must make these pass
- A focused `README.md` with key gotchas and run instructions

---

## 🎯 How the Tasks work

Every module includes two extra files alongside the theory:

```
SomethingDemo.java       ← annotated reference code (read this first)
SomethingTasks.java      ← method stubs with UnsupportedOperationException
SomethingDemoTest.java   ← tests for the demo (already green)
SomethingTasksTest.java  ← tests for YOUR implementation (start red → make green)
```

**The workflow:**
1. Read `*Demo.java` to understand the concept
2. Open `*Tasks.java` — each method has a Javadoc explaining what to implement
3. Implement the method, remove the `UnsupportedOperationException`
4. Run `mvn test -Dtest=*TasksTest` and watch tests go green ✅

> Task tests ship with `@Disabled` — they are skipped during `mvn clean install`
> until you are ready to solve them. Remove `@Disabled` to activate.

---

## 📚 Modules

| Module | Description | Java | Tags |
|---|---|---|---|
| [`basics-variables-and-types`](./basics-variables-and-types/README.md) | Primitive types, type casting, literals, constants, variable scope, `var` type inference | 8–17+ | BASICS |
| [`basics-operators-and-expressions`](./basics-operators-and-expressions/README.md) | Arithmetic, logical, bitwise, ternary operators, operator precedence | 8 | BASICS |
| [`control-flow-statements`](./control-flow-statements/README.md) | if-else, switch (classic + expressions + pattern matching), loops, break/continue | **21+** ¹ | CONTROL-FLOW |
| [`strings-and-text-processing`](./strings-and-text-processing/README.md) | String manipulation, StringBuilder, text blocks, String pool, regex, formatting | 8–15+ | STRINGS |
| [`arrays-and-collections-basics`](./arrays-and-collections-basics/README.md) | Arrays, ArrayList, LinkedList, HashSet, HashMap, iteration patterns | 8 | COLLECTIONS |
| [`oop-inheritance-and-polymorphism`](./oop-inheritance-and-polymorphism/README.md) | Inheritance, overriding, super, polymorphism, abstract classes | 8 | OOP |
| [`oop-interfaces-and-abstraction`](./oop-interfaces-and-abstraction/README.md) | Interfaces, default/static methods, multiple inheritance via interfaces | 8–9 | OOP |
| [`oop-encapsulation-patterns`](./oop-encapsulation-patterns/README.md) | Access modifiers, immutability, records (Java 16+), data hiding | 8–16+ | OOP |
| [`enums-and-nested-classes`](./enums-and-nested-classes/README.md) | Enums with fields/methods, inner/static nested/anonymous classes | 8 | OOP, ADVANCED |
| [`sealed-classes-pattern-matching`](./sealed-classes-pattern-matching/README.md) | Sealed classes/interfaces, permits, pattern matching (switch + instanceof) | **17–21+** | OOP, MODERN-JAVA |
| [`exception-handling`](./exception-handling/README.md) | try-catch-finally, custom exceptions, checked vs unchecked, try-with-resources | 8 | EXCEPTIONS |
| [`generics-fundamentals`](./generics-fundamentals/README.md) | Generic classes/methods, bounded types, wildcards (`?`, `extends`, `super`) | 8 | GENERICS |
| [`collections-framework-advanced`](./collections-framework-advanced/README.md) | Queue, Deque, TreeSet/TreeMap, PriorityQueue, custom comparators | 8 | COLLECTIONS |
| [`functional-interfaces`](./functional-interfaces/README.md) | `@FunctionalInterface`, lambdas, method references, Predicate/Function/Consumer/Supplier | 8 | FUNCTIONAL |
| [`streams-api-basics`](./streams-api-basics/README.md) | Stream creation, filter/map/sorted/collect/forEach/reduce | 8 | STREAMS |
| [`streams-api-advanced`](./streams-api-advanced/README.md) | flatMap, groupingBy, partitioningBy, parallel streams, custom collectors | 8 | STREAMS |
| [`optional-and-null-handling`](./optional-and-null-handling/README.md) | Optional creation, orElse/orElseGet/orElseThrow, map/flatMap chains | 8 | FUNCTIONAL |
| [`date-time-api`](./date-time-api/README.md) | LocalDate/Time/DateTime, ZonedDateTime, Period/Duration, formatting/parsing | 8 | DATE-TIME |
| [`file-io-basics`](./file-io-basics/README.md) | File/BufferedReader/Writer, try-with-resources, Path/Files API | 8 | I/O |
| [`file-io-nio`](./file-io-nio/README.md) | NIO.2 Files/Paths, DirectoryStream, WatchService, file attributes | 8 | I/O, NIO |
| [`serialization`](./serialization/README.md) | Serializable, Object streams, transient, serialVersionUID, Externalizable | 8 | I/O, SERIALIZATION |
| [`logging-basics`](./logging-basics/README.md) | SLF4J + Logback, configuration, best practices, structured logging | 8+ | LOGGING |
| [`http-client-api`](./http-client-api/README.md) | Modern `java.net.http.HttpClient` (sync/async), request/response handling | 11+ | NETWORKING, MODERN-JAVA |
| [`annotations`](./annotations/README.md) | Built-in, custom annotations, retention & target policies | 8 | ANNOTATIONS |
| [`reflection-api`](./reflection-api/README.md) | Class introspection, dynamic field/method access, annotation processing | 8 | REFLECTION |
| [`jvm-basics-memory-and-gc`](./jvm-basics-memory-and-gc/README.md) | Stack vs Heap, object lifecycle, GC basics, strong/soft/weak/phantom references | 8+ | JVM, PERFORMANCE |
| [`multithreading-basics`](./multithreading-basics/README.md) | Thread/Runnable, lifecycle, synchronization, volatile, thread-safety | 8 | CONCURRENCY |
| [`multithreading-advanced`](./multithreading-advanced/README.md) | ExecutorService, ThreadPool, Callable/Future, CountDownLatch, Semaphore | 8 | CONCURRENCY |
| [`concurrent-collections`](./concurrent-collections/README.md) | ConcurrentHashMap, CopyOnWriteArrayList, BlockingQueue family, atomic classes | 8 | CONCURRENCY |
| [`virtual-threads-intro`](./virtual-threads-intro/README.md) | Project Loom / Virtual Threads basics, structured concurrency intro | **21+** | CONCURRENCY, MODERN-JAVA |
| [`design-patterns`](./design-patterns/README.md) | Singleton, Factory, Builder, Observer, Strategy, Decorator | 8 | DESIGN-PATTERNS |
| [`best-practices-and-solid`](./best-practices-and-solid/README.md) | SOLID principles, clean code, common code smells, refactoring | 8+ | BEST-PRACTICES |
| [`java-modules-jpms`](./java-modules-jpms/README.md) | Java Platform Module System — `module-info.java`, requires/exports | 9+ | MODERN-JAVA |

> ¹ Modules marked **21+** override the parent Java 17 default in their own `pom.xml`.

---

## 📖 Reference

| Module | Description |
|---|---|
| [`quick-interview-reference`](./quick-interview-reference/README.md) | Curated docs and code snippets covering all major Java interview topics in one place |

> This module contains reference material only — no Maven submodule, no tasks, no tests.
> It is excluded from the progress rings on the interactive site.

---

## 🚀 Getting Started

### Prerequisites

- **Java 17+** (Java 21 for modules marked 21+)
- **Maven 3.6+**
- **JUnit 5** (managed via parent `pom.xml`)

### Clone & build

```bash
git clone https://github.com/vbforge/java-core.git
cd java-core

# Build and run all tests across all modules
mvn clean install

# Work on a single module
cd basics-variables-and-types
mvn test

# Run only the task tests for a module
mvn test -Dtest=VariablesTasksTest
```

### Open in IntelliJ IDEA

File → Open → select the root `java-core` folder.
IntelliJ detects the parent `pom.xml` and imports all modules automatically.

---

## 📦 Dependency management

All dependency versions are declared **once** in the parent `pom.xml` via `<dependencyManagement>`.
Child modules declare only **what** they need — never a version.

```xml
<!-- Parent pom.xml: defines versions -->
<dependencyManagement>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.2</version>
    </dependency>
</dependencyManagement>

<!-- Child pom.xml: declares usage, inherits version -->
<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <!-- no version here -->
    </dependency>
</dependencies>
```

To override Java version for a specific module, add to that module's `pom.xml`:

```xml
<properties>
    <java.version>21</java.version>
</properties>
```

| Module category | Dependencies used |
|---|---|
| All modules | `junit-jupiter`, `junit-jupiter-params`, `assertj-core` |
| OOP / Records | `lombok` (optional) |
| Collections / Streams | `guava` (optional) |
| File I/O & Serialization | `commons-io`, `slf4j-api`, `logback-classic`, `jackson-databind` |
| Logging | `slf4j-api`, `logback-classic` |
| HTTP Client | built-in since Java 11 |
| Testing (advanced) | `mockito-core`, `mockito-junit-jupiter` |

---

## 🗺️ Recommended Learning Order

```
Phase 1 — Basics
  variables & types → operators → control flow → strings → arrays/collections

Phase 2 — Core OOP
  inheritance → interfaces → encapsulation → enums → sealed classes

Phase 3 — Functional & Streams
  functional interfaces → streams basics → streams advanced → optional

Phase 4 — Exceptions, Generics & Collections
  exception handling → generics → collections advanced

Phase 5 — I/O, Date-Time & Logging
  date-time API → file I/O basics → NIO → serialization → logging

Phase 6 — Concurrency
  multithreading basics → advanced → concurrent collections → virtual threads

Phase 7 — JVM Internals
  memory model, GC basics, reference types

Phase 8 — Modern Java
  HTTP client → JPMS modules

Phase 9 — Advanced Topics
  annotations → reflection → design patterns → SOLID
```

> The module table above is **reference-sorted** (good for lookup).
> The phases above are **progression-sorted** (follow this order when learning).

---

## ☕ Java Version Coverage

| Version | What's introduced |
|---|---|
| Java 8 | Lambdas, Streams, Optional, Date-Time API, default interface methods — most foundational content |
| Java 9+ | JPMS modules |
| Java 10 | `var` local type inference |
| Java 11 | `HttpClient` API |
| Java 15+ | Text blocks |
| Java 16+ | Records |
| Java 17 | Sealed classes, pattern matching previews |
| Java 21+ | Virtual threads (Project Loom), switch pattern matching with guards |

---

## 🎯 Common Interview Topics

| Category | Topics to know |
|---|---|
| Basics / OOP | `==` vs `equals()` vs `hashCode()`, variable scope, immutability, records vs classes |
| Collections / Streams | HashMap internals, Stream laziness, parallel stream pitfalls, fail-fast iterators |
| Concurrency | `synchronized` vs `Lock`, `volatile`, happens-before, virtual vs platform threads |
| JVM | Heap structure, GC algorithms (G1/ZGC), memory leak signs, reference types |
| Modern Java | Sealed classes, pattern matching benefits, virtual thread use cases |

---

## 📈 Learning path to job-readiness

Completing **~75–85% of these modules** + writing clean tests + solving **120–200 medium LeetCode problems in Java** + building **2–3 small REST projects** (e.g. Spring Boot) → strong pre-middle / middle-level candidate.

---

*Package root: `com.vbforge` · Happy coding! 🚀*
