# class-loading-and-jvm-architecture

> **Phase 7 — JVM Internals** | Java 8+ | Package: `com.vbforge.class_loading_and_jvm_architecture`

Class loaders and delegation, load/link/initialise, memory areas, bytecode with `javap`.

## What's covered

| Topic | Key points |
|---|---|
| Class loaders and delegation | `getClassLoader()` is `null` for bootstrap, parent-first `loadClass`, `findClass`, `defineClass`, child-first loader, identity = name + loader, `ClassCastException` across loaders, context class loader |
| Load / link / initialise | `Class.forName(name, false, loader)`, what triggers `<clinit>`, compile-time constants, superclass-first order, preparation defaults, `ExceptionInInitializerError` → `NoClassDefFoundError`, `ClassFormatError`, `UnsupportedClassVersionError` |
| Memory areas | per-thread stack and frames, `StackOverflowError`, heap, pass-by-value, String pool, `OutOfMemoryError`, Metaspace, static vs instance fields, `ClassLoadingMXBean`, `MemoryMXBean` |
| Bytecode with `javap` | `0xCAFEBABE`, major version, constant pool, `Code` attribute, `max_stack` / `max_locals`, slot 0 = `this`, default constructor, `javap -c -p -v` |

## Files

```
src/
├── main/java/com/vbforge/class_loading_and_jvm_architecture/
│   ├── ClassLoaderDemo.java        ← hierarchy, tracing + isolating loaders, context loader
│   ├── ClassLoaderTasks.java       ← 🎯 your tasks
│   ├── ClassLifecycleDemo.java     ← init triggers, preparation, failing <clinit>, class-file checks
│   ├── ClassLifecycleTasks.java    ← 🎯 your tasks
│   ├── MemoryAreasDemo.java        ← stack, heap, Metaspace, JMX beans
│   ├── MemoryAreasTasks.java       ← 🎯 your tasks
│   ├── BytecodeDemo.java           ← tiny class-file parser + opcode decoder
│   └── BytecodeTasks.java          ← 🎯 your tasks
└── test/java/com/vbforge/class_loading_and_jvm_architecture/
    ├── ClassLoaderDemoTest.java        ← 🧪 tests (green)
    ├── ClassLoaderTasksTest.java       ← 🧪 tests for your code (@Disabled until you solve)
    ├── ClassLifecycleDemoTest.java     ← 🧪 tests (green)
    ├── ClassLifecycleTasksTest.java    ← 🧪 tests for your code
    ├── MemoryAreasDemoTest.java        ← 🧪 tests (green)
    ├── MemoryAreasTasksTest.java       ← 🧪 tests for your code
    ├── BytecodeDemoTest.java           ← 🧪 tests (green)
    └── BytecodeTasksTest.java          ← 🧪 tests for your code
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ClassLoaderTasks.loaderDepth` | Walk the loader chain, bootstrap = 0 |
| 2 | `ClassLoaderTasks.definingLoaderName` | The `null` loader and `"<bootstrap>"` |
| 3 | `ClassLoaderTasks.isSameRuntimeType` | Class identity = name + loader |
| 4 | `ClassLifecycleTasks.loadWithoutInitializing` | `Class.forName` with `initialize = false` |
| 5 | `ClassLifecycleTasks.captureInitializationFailure` | `ExceptionInInitializerError` / `LinkageError` |
| 6 | `ClassLifecycleTasks.defineFromBytes` | Subclass `ClassLoader` to call `defineClass` |
| 7 | `MemoryAreasTasks.currentCallStack` | Frames via `Throwable.getStackTrace` |
| 8 | `MemoryAreasTasks.recursionDepth` | Provoke and survive `StackOverflowError` |
| 9 | `MemoryAreasTasks.swapContents` | Heap objects vs reference copies |
| 10 | `BytecodeTasks.hasValidMagic` | `0xCAFEBABE` and signed bytes |
| 11 | `BytecodeTasks.javaReleaseFor` | Major version → Java release |
| 12 | `BytecodeTasks.localSlotsFor` | Descriptors and local-variable slots |

```bash
mvn test -Dtest=ClassLoaderTasksTest
mvn test -Dtest=ClassLifecycleTasksTest
mvn test -Dtest=MemoryAreasTasksTest
mvn test -Dtest=BytecodeTasksTest
```

Each task test class has one `@Nested` class per method, all `@Disabled`. Remove `@Disabled` from the nested class you want to solve.

## Key things to remember

```java
// ❌ the bootstrap loader is null, not an object
String.class.getClassLoader().getParent();      // NullPointerException
// ✅ test for null first (also true for int.class and String[].class)
ClassLoader l = type.getClassLoader();
String name = (l == null) ? "<bootstrap>" : l.getClass().getName();
```

```java
// ❌ identity by name: two loaders can define the "same" class
a.getClass().getName().equals(b.getClass().getName());
// ✅ a class is (name + defining loader); the Class objects themselves tell you
a.getClass() == b.getClass();
// "X cannot be cast to X" = same name, different loader
```

```java
// ❌ forName(String) initialises the class: static blocks run NOW
Class.forName("com.acme.Plugin");
// ✅ load and link only; initialisation waits for first active use
Class.forName("com.acme.Plugin", false, loader);
// ✅ compile-time constants (static final int X = 42) never trigger init: javac inlines them
```

```java
// ❌ forward reference through a method sees the PREPARED default
static int first = peek();   // 0, because `second = 5` has not run yet
static int second = 5;
// ✅ initialisers run top to bottom: order your static fields on purpose
```

```java
// ❌ hunting the second error: NoClassDefFoundError: Could not initialize class X
// ✅ find the FIRST ExceptionInInitializerError in the log; its cause is the real bug.
//    A class whose <clinit> failed stays unusable for the life of its loader.
```

```java
// ❌ you cannot swap two caller variables from inside a method
static void swap(StringBuilder a, StringBuilder b) { StringBuilder t = a; a = b; b = t; } // no effect
// ✅ Java passes references BY VALUE: mutate the heap objects instead
```

```java
// ❌ override loadClass to add a source: you lose parent delegation by accident
// ✅ override findClass; it is only called after the parent failed
// ✅ restore the context class loader in finally after swapping it
```

```
// javap reading tips (instance method: slot 0 = this)
javap -c -p -cp target/classes 'com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo$SampleMath'
//   int add(int,int)   → iload_1, iload_2, iadd, ireturn     (max_locals = 3)
//   static square(int) → iload_0, iload_0, imul, ireturn     (max_locals = 1)
//   long and double take TWO local slots
```

## Run

```bash
# demo + tasks
mvn test

# demo tests only (all four Demo test classes)
mvn test -Dtest=ClassLoaderDemoTest
mvn test -Dtest=ClassLifecycleDemoTest
mvn test -Dtest=MemoryAreasDemoTest
mvn test -Dtest=BytecodeDemoTest

# task tests only
mvn test -Dtest='*TasksTest'

# run a demo
mvn exec:java -Dexec.mainClass="com.vbforge.class_loading_and_jvm_architecture.ClassLoaderDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.class_loading_and_jvm_architecture.ClassLifecycleDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.class_loading_and_jvm_architecture.MemoryAreasDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.class_loading_and_jvm_architecture.BytecodeDemo"
```
