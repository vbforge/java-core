# quick-interview-reference

> **Reference Module** | Java 21 | Package root: `com.vbforge`
>
> 🗒️ This is NOT a learning module — it's a living cheat sheet.
> No tasks, no tests. Read the code, run the `main()` methods, add your own snippets.

---

## 📦 Structure

```
src/main/java/com/vbforge/
├── basics/
│   └── BasicsReference.java          ← primitives, casting, Integer cache, String pool, operators, var
├── oop/
│   └── OopReference.java             ← equals/hashCode, abstract vs interface, records, sealed, immutability
├── collections/
│   └── CollectionsReference.java     ← Big-O tables, HashMap internals, Comparable vs Comparator, fail-fast
├── streams/
│   └── StreamsReference.java         ← laziness, flatMap, collectors, parallel pitfalls
├── functional/
│   └── FunctionalReference.java      ← functional interfaces, method references, Optional
├── exceptions/
│   └── ExceptionsReference.java      ← hierarchy, checked vs unchecked, try-with-resources, chaining
├── generics/
│   └── GenericsReference.java        ← type erasure, wildcards, PECS rule
├── concurrency/
│   ├── ConcurrencyReference.java     ← Thread lifecycle, synchronized vs volatile, happens-before, deadlock
│   └── ExecutorReference.java        ← thread pools, Callable/Future, CountDownLatch, virtual threads
├── jvm/
│   └── JvmReference.java             ← memory areas, GC algorithms, reference types, memory leaks
├── datetime/
│   └── DateTimeReference.java        ← LocalDate/Time, ZonedDateTime, Period/Duration  [expand me]
├── io/
│   └── IoReference.java              ← NIO.2, streams vs readers, serialization gotchas [expand me]
└── modern/
    └── ModernJavaReference.java      ← text blocks, switch expressions, pattern matching, HTTP Client
└── patterns/
    └── PatternsReference.java        ← Singleton, Builder, Strategy, Observer, SOLID
```

---

## 🎯 What's in each file

Every reference class follows the same structure:

- **Comments with tables** — Big-O complexity, feature comparisons, API comparisons
- **⚠️ Gotchas** — the tricky bits that trip up even experienced devs
- **Side-by-side comparisons** — `ArrayList vs LinkedList`, `synchronized vs volatile`, etc.
- **Anti-patterns ❌** — what NOT to do and why
- **Runnable `main()`** — see the output, not just read about it

---

## 🚀 How to use

### Run any reference class
```bash
# From module directory
mvn compile exec:java -Dexec.mainClass="com.vbforge.basics.BasicsReference"
mvn compile exec:java -Dexec.mainClass="com.vbforge.collections.CollectionsReference"
mvn compile exec:java -Dexec.mainClass="com.vbforge.concurrency.ConcurrencyReference"
# ... etc
```

### Quick compile check
```bash
mvn compile
```

### Add your own snippets
Each class has a clear section structure. Find the relevant section and add a new method with:
```java
// ── Your topic name ───────────────────────────────────────────
static void yourTopic() {
    // your annotated code here
}
```
Then call it from `main()`.

---

## 📋 Interview Topics Coverage

| File | Key interview questions covered |
|---|---|
| `BasicsReference` | Primitive sizes · narrowing cast truncation · Integer cache trap · String pool · `==` vs `equals()` |
| `OopReference` | `equals()`/`hashCode()` contract · abstract vs interface · immutability rules · records · sealed classes |
| `CollectionsReference` | HashMap internals · Big-O for all collections · fail-fast vs fail-safe · PECS wildcards |
| `StreamsReference` | Laziness · map vs flatMap · parallel stream pitfalls · common collectors |
| `FunctionalReference` | All 4 method reference types · Optional anti-patterns · lambda capture rules |
| `ExceptionsReference` | Checked vs unchecked · try-with-resources · suppressed exceptions · exception chaining |
| `GenericsReference` | Type erasure consequences · PECS rule · wildcard gotchas |
| `ConcurrencyReference` | synchronized vs volatile · happens-before rules · deadlock prevention |
| `ExecutorReference` | Thread pool types · Callable vs Runnable · virtual threads vs platform threads |
| `JvmReference` | Stack vs Heap · GC algorithms · 4 reference types · OOM error types · memory leaks |
| `ModernJavaReference` | Text block indentation · switch expression yield · pattern matching guards · HTTP Client |
| `PatternsReference` | Thread-safe Singleton · Builder · SOLID with examples |

---

## ➕ Suggested additions (expand as you study)

- `datetime/` — more formatting patterns, DST gotchas, legacy Date migration
- `io/` — buffered streams, WatchService, custom serialization
- `concurrency/` — CompletableFuture chains, ConcurrentHashMap operations
- `patterns/` — Decorator, Template Method, Proxy
- New packages: `reflection/`, `annotations/`, `modules/`

---

*Not a Maven submodule with tests — reference only. Add freely, break nothing.*
