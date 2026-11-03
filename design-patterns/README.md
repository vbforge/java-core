# design-patterns

> **Phase 9 — Advanced Topics** | Java 8+ | Package: `com.vbforge.design_patterns`

## What's covered

| Topic | Key points |
|---|---|
| Singleton | Eager, lazy, double-checked locking with `volatile`, holder idiom, enum singleton — the safest |
| Factory | Factory method `switch`, `Supplier<Shape>` factory, batch creation; adding a type means touching one place |
| Builder | Immutable product + fluent builder; `build()` validates; use when constructors have many params |
| Observer | `Subject` with `attach`/`detach`/`notifyObservers`; observers get notified of every event |
| Strategy | Interface `PricingStrategy` + concrete impls; `Checkout` delegates at runtime — `if`/`switch` avoided |
| Decorator | `TextProcessor` wrappers stack around a base impl; each adds one behaviour; order matters |
| Proxy | Same interface as the real object — caching, protection, logging proxies delegate to a real subject |

## Files

```
src/
├── main/java/com/vbforge/design_patterns/
│   ├── singleton_pattern/   SingletonDemo.java     ← theory + annotated examples
│   │                        SingletonTasks.java    ← 🎯 your tasks
│   ├── factory_pattern/     FactoryDemo.java
│   │                        FactoryTasks.java
│   ├── builder_pattern/     BuilderDemo.java
│   │                        BuilderTasks.java
│   ├── observer_pattern/    ObserverDemo.java
│   │                        ObserverTasks.java
│   ├── strategy_pattern/    StrategyDemo.java
│   │                        StrategyTasks.java
│   ├── decorator_pattern/   DecoratorDemo.java
│   │                        DecoratorTasks.java
│   └── proxy_pattern/       ProxyDemo.java
│                            ProxyTasks.java
└── test/java/com/vbforge/design_patterns/
├── singleton_pattern/   SingletonDemoTest.java  ← tests for the demo (already green)
│                        SingletonTasksTest.java ← 🧪 tests for your tasks — all must pass
├── factory_pattern/     FactoryDemoTest.java   + FactoryTasksTest.java
├── builder_pattern/     BuilderDemoTest.java   + BuilderTasksTest.java
├── observer_pattern/    ObserverDemoTest.java  + ObserverTasksTest.java
├── strategy_pattern/    StrategyDemoTest.java  + StrategyTasksTest.java
├── decorator_pattern/   DecoratorDemoTest.java + DecoratorTasksTest.java
└── proxy_pattern/       ProxyDemoTest.java     + ProxyTasksTest.java
```

## 🎯 Tasks

Each sub-package has its own `Tasks` file. Implement one at a time — remove that `@Nested` class's `@Disabled` when you start.

| # | Pattern | Methods |
|---|---|---|
| 1 | Singleton | `getDoubleCheckedSingleton`, `getHolderSingleton`, `getEnumSingleton` |
| 2 | Factory | `createShape`, `shapeSupplier`, `createAll` |
| 3 | Builder | `buildMargherita`, `buildPepperoni`, `buildCustom` (+ implement `PizzaBuilder`) |
| 4 | Observer | `createSubject`, `createListObserver` |
| 5 | Strategy | `noDiscount`, `tenPercentOff`, `percentage`, `checkout` |
| 6 | Decorator | `base`, `uppercase`, `trim`, `reverse` |
| 7 | Proxy | `realSource`, `cachingProxy`, `protectionProxy` |

```bash
mvn test -Dtest=SingletonTasksTest
mvn test -Dtest=FactoryTasksTest
# … one pattern at a time
```

Every task group must go **GREEN** ✅

## Key things to remember

```java
// ❌ Lazy singleton without synchronisation races:
if (instance == null) instance = new Singleton();       // two threads both enter

// ✅ Double-checked locking — `volatile` is mandatory, otherwise reordering breaks it
private static volatile Singleton instance;
if (instance == null) {
    synchronized (Singleton.class) {
        if (instance == null) instance = new Singleton();
    }
}

// ✅ Holder idiom — no lock, lazy, thread-safe by JVM class-init guarantee
private static class Holder { static final Singleton INSTANCE = new Singleton(); }
static Singleton getInstance() { return Holder.INSTANCE; }

// ✅ Enum singleton — safest, serialisation-proof, reflection-proof
enum Instance { SINGLETON }

// ❌ Factory switch with default returning null — caller gets NPE far from the cause
switch (type) { case "circle": return new Circle(); default: return null; }

// ✅ Throw early, throw loudly
default: throw new IllegalArgumentException("Unknown shape: " + type);

// ❌ Telescoping constructor — unreadable at the call site, easy to swap args
new User("alice", 30, true, false, true, null, "admin");

// ✅ Fluent builder — named, order-free, validated once at build()
User.builder().name("alice").age(30).admin(true).build();

// ❌ Forgetting to detach an observer — leak + unexpected callbacks
subject.attach(observer);

// ✅ Decorator order matters — composition is not commutative
new Trim(new Upper(" hi ")).process(" x ")   // = "X"
new Upper(new Trim(" hi ")).process(" x ")   // = "X"

// ❌ Proxy that skips delegation — you're now shadowing the real object
public String fetch(String k) { return "stub"; }   // real never called

// ✅ Proxy holds the delegate and forwards on cache miss
public String fetch(String k) {
    if (!cache.containsKey(k)) cache.put(k, delegate.fetch(k));
    return cache.get(k);
}
```

## Run

```bash
# All tests across all 7 patterns
mvn test

# One pattern (demo + tasks)
mvn test -Dtest='Singleton*Test'

# Just one pattern's demo tests
mvn test -Dtest=SingletonDemoTest

# Just one pattern's task tests
mvn test -Dtest=SingletonTasksTest

# Run one demo's main()
mvn exec:java -Dexec.mainClass="com.vbforge.design_patterns.singleton_pattern.SingletonDemo"
```