# java-modules-jpms

> **Phase 8 — Optional Extras** | Java 9+ | Package: `com.vbforge.java_modules_jpms`

## What's covered

| Topic | Key points |
|---|---|
| `module-info.java` | The declaration file; `requires`, `requires transitive`, `exports`, `opens`, `uses`, `provides` |
| Runtime `Module` API | `Class.getModule()`, `Module.isNamed()`, `Module.getName()`, `ModuleLayer.boot()` |
| `ModuleDescriptor` | `requires()`, `exports()` — reading a real module's dependency graph |
| Named vs unnamed | Classpath = unnamed; module path = named; JDK is always named |

## Files

```
src/
├── main/java/com/vbforge/java_modules_jpms/
│   ├── ModulesDemo.java        ← theory + annotated examples
│   └── ModulesTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/java_modules_jpms/
├── ModulesDemoTest.java     ← tests for the demo (already green)
└── ModulesTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `ModulesTasks.java` and implement all 5 methods. Each has a detailed Javadoc.

| # | Method | Topic |
|---|---|---|
| 1 | `isNamedModule(Class<?>)` | `Module.isNamed()` — the named/unnamed distinction |
| 2 | `moduleNameOf(Class<?>)` | `Module.getName()` — null for unnamed |
| 3 | `requiresOf(String)` | `ModuleDescriptor.requires()` — reading `requires` |
| 4 | `exportsOf(String)` | `ModuleDescriptor.exports()` — reading `exports` |
| 5 | `bootLayerModuleNames()` | `ModuleLayer.boot()` — enumerate loaded modules |

```bash
mvn test -Dtest=ModulesTasksTest
```

All 5 task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ Missing requires — compile error at module boundary
// module com.example {
//     exports com.example;   // uses java.sql but never declared it
// }
import java.sql.Connection;   // ❌ module com.example: package java.sql is not visible

// ✅ Declare every module you use
module com.example {
    requires java.sql;
    exports com.example;
}

// ❌ exports vs opens confusion
// exports  → other modules may READ public types at compile time
// opens    → other modules may REFLECT into the package at runtime
// A package can be both; opens alone is for reflection-only access
module com.example {
    exports com.example.api;         // ✅ public API
    opens   com.example.internal;    // ✅ reflection allowed, no compile-time read
}

// ❌ requires transitive — use only for API leakage
// If com.example returns java.sql.Connection from a public method,
// every caller of com.example must also require java.sql.
// requires transitive java.sql;   // ✅ propagates the dependency
// Plain `requires java.sql;` leaves callers broken on the module boundary.

// ❌ Assuming a class has a module descriptor
Module m = someClass.getModule();
m.getDescriptor().name();   // ❌ NPE if m is unnamed

// ✅ Check isNamed() first (or null-check the descriptor)
if (m.isNamed()) {
    String name = m.getDescriptor().name();
}

// ❌ Trying to reflect on a package that isn't exported or opened
// Class<?> c = Class.forName("com.example.internal.Hidden");
// c.getDeclaredField("secret");     // ❌ InaccessibleObjectException

// ✅ Add `opens com.example.internal;` to the module-info

// ❌ Expecting module path behaviour from the classpath
// Running with `java -cp ...` puts every class in the *unnamed* module.
// `--module-path` is required for the module system to engage.

// ✅ Both work at runtime — but only --module-path gives you named modules
// java -cp target/classes com.vbforge.java_modules_jpms.ModulesDemo      ← unnamed
// java --module-path target/classes --module com.vbforge.java_modules_jpms/com.vbforge.java_modules_jpms.ModulesDemo  ← named
```

## Trying JPMS on your own project

This module ships **without** a real `module-info.java` on purpose — that
would force `maven-surefire` onto the module path and complicate the JUnit 5 /
AssertJ setup. The full declaration you'd write is shown in
`ModulesDemo.MODULE_INFO_EXAMPLE` and printed when you run the demo.

To make your own project modular, save this to
`src/main/java/module-info.java`:

```java
module com.example.myapp {
    requires java.sql;
    exports com.example.myapp;
}
```

Then build and run on the module path:

```bash
mvn clean package
java --module-path target/classes --module com.example.myapp/com.example.myapp.Main
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=ModulesDemoTest

# Task tests only
mvn test -Dtest=ModulesTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.java_modules_jpms.ModulesDemo"
```