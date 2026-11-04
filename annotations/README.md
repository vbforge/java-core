# annotations

> **Phase 9 — Advanced Topics** | Java 8 | Package: `com.vbforge.annotations`

Built-in annotations, writing your own, and the `@Retention` / `@Target` policies that decide
who can see an annotation and where it may be written. An annotation by itself does **nothing** —
it is a typed label that the compiler or a framework reads. This module shows both halves.

## What's covered

| Topic | Key points |
|---|---|
| `@Override` | compile-time check only (`SOURCE`); catches `equals(MyType)` overload bug |
| `@Deprecated` | still works, flagged for callers; `RUNTIME`; pair with Javadoc `@deprecated` |
| `@SuppressWarnings` | silences `unchecked`/`rawtypes`; smallest scope; hides real `ClassCastException` risk |
| `@FunctionalInterface` | guard rail for exactly one abstract method; `default`/`static`/`Object` methods don't count |
| `@SafeVarargs` | removes generic-varargs warnings; never leak the `T...` array |
| Declaring annotations | `@interface`, elements, `default`, `value` shorthand, allowed element types |
| Reading annotations | `getAnnotation`, `isAnnotationPresent`, `getDeclaredAnnotations`, `getAnnotationsByType` |
| `@Repeatable` | container annotation (`@Roles`); `getAnnotation` returns `null` when repeated |
| `@Inherited` | classes only (not interfaces); `getDeclaredAnnotation` ignores it |
| Runtime processing | mini validator with `@NotBlank` / `@Range` using `Field.get`, `setAccessible` |
| `@Retention` | `SOURCE` / `CLASS` (default) / `RUNTIME`; only `RUNTIME` is visible to reflection |
| `@Target` | `ElementType` locations, `getParameterAnnotations`, no `@Target` = any declaration |
| Meta-annotations | `@Documented`, composed annotations (`@Stereotype`), annotation types are interfaces |
| Type annotations (Java 8) | `TYPE_USE`, `TYPE_PARAMETER`, `AnnotatedType`, `getAnnotatedReturnType` |

## Files

```
src/
├── main/java/com/vbforge/annotations/
│   ├── BuiltInAnnotationsDemo.java      ← @Override, @Deprecated, @SuppressWarnings, @FunctionalInterface, @SafeVarargs
│   ├── BuiltInAnnotationsTasks.java     ← 🎯 your tasks
│   ├── CustomAnnotationsDemo.java       ← @interface, elements, reading, @Repeatable, @Inherited, validator
│   ├── CustomAnnotationsTasks.java      ← 🎯 your tasks
│   ├── RetentionAndTargetDemo.java      ← @Retention, @Target, meta-annotations, type annotations
│   └── RetentionAndTargetTasks.java     ← 🎯 your tasks
└── test/java/com/vbforge/annotations/
    ├── BuiltInAnnotationsDemoTest.java      ← 🧪 tests (green)
    ├── BuiltInAnnotationsTasksTest.java     ← 🧪 tests (start disabled → enable → make green)
    ├── CustomAnnotationsDemoTest.java       ← 🧪 tests (green)
    ├── CustomAnnotationsTasksTest.java      ← 🧪 tests (start disabled → enable → make green)
    ├── RetentionAndTargetDemoTest.java      ← 🧪 tests (green)
    └── RetentionAndTargetTasksTest.java     ← 🧪 tests (start disabled → enable → make green)
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `BuiltInAnnotationsTasks.isDeprecatedMethod` | find a public method, check `@Deprecated` |
| 2 | `BuiltInAnnotationsTasks.deprecatedMethodNames` | declared vs inherited methods, sorted names |
| 3 | `BuiltInAnnotationsTasks.isStructurallyFunctional` | exactly one abstract method |
| 4 | `BuiltInAnnotationsTasks.overridesObjectEquals` | override vs overload of `equals` |
| 5 | `CustomAnnotationsTasks.ticketId` | read a required element, `Optional` |
| 6 | `CustomAnnotationsTasks.ticketLabels` | array-valued element with default |
| 7 | `CustomAnnotationsTasks.roleNames` | `@Repeatable` and its container |
| 8 | `CustomAnnotationsTasks.fieldsAnnotatedWith` | scan private fields by annotation |
| 9 | `CustomAnnotationsTasks.isDeclaredDirectly` | `@Inherited` vs declared |
| 10 | `CustomAnnotationsTasks.describeRanges` | read element values and defaults |
| 11 | `RetentionAndTargetTasks.retentionOf` | effective retention incl. the default |
| 12 | `RetentionAndTargetTasks.targetsOf` | read `@Target` |
| 13 | `RetentionAndTargetTasks.canApplyTo` | target rules incl. "no `@Target`" |
| 14 | `RetentionAndTargetTasks.runtimeVisibleAnnotationNames` | what reflection can actually see |
| 15 | `RetentionAndTargetTasks.parameterAnnotationCounts` | `getParameterAnnotations` |
| 16 | `RetentionAndTargetTasks.returnTypeAnnotationNames` | type annotations via `AnnotatedType` |

```bash
mvn test -Dtest=BuiltInAnnotationsTasksTest
mvn test -Dtest=CustomAnnotationsTasksTest
mvn test -Dtest=RetentionAndTargetTasksTest
```

## Key things to remember

```java
// ❌ Overload, not override: collections call equals(Object) and never see this
public boolean equals(Point other) { ... }

// ✅ @Override turns the typo into a compile error
@Override public boolean equals(Object o) { ... }
```

```java
// ❌ Compiles, but getAnnotation(Audit.class) returns null: default retention is CLASS
public @interface Audit { }

// ✅ Reflection can only see RUNTIME annotations
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit { }
```

```java
// ❌ Returns null as soon as @Role is repeated (the element carries the @Roles container)
Role role = type.getAnnotation(Role.class);

// ✅ Handles zero, one and many uses
Role[] roles = type.getAnnotationsByType(Role.class);
```

```java
// ❌ @Inherited does not work through interfaces, and getDeclaredAnnotation ignores it
ContractImpl.class.isAnnotationPresent(Audited.class);   // false if @Audited is on the interface

// ✅ Only class-to-subclass inheritance counts
ChildService.class.isAnnotationPresent(Audited.class);   // true
ChildService.class.getDeclaredAnnotation(Audited.class); // null
```

```java
// ❌ Silenced warning, real bug: ClassCastException appears far from the cast
@SuppressWarnings("unchecked") List<String> s = (List<String>) rawListWithIntegers;

// ✅ Suppress on the smallest scope, and only when you can prove the cast is safe
```

```java
// ❌ TYPE_USE-only annotations are invisible to the usual declaration APIs
method.getAnnotation(NonNull.class);                    // null
method.getParameterAnnotations()[0];                    // empty

// ✅ Go through AnnotatedType
method.getAnnotatedReturnType().isAnnotationPresent(NonNull.class);
```

```java
// ❌ @SafeVarargs on a method that hands out its varargs array
static <T> T[] toArray(T... args) { return args; }      // caller gets Object[] -> ClassCastException

// ✅ Only read from the array
@SafeVarargs static <T> List<T> listOf(T... items) { return new ArrayList<>(Arrays.asList(items)); }
```

## Run

```bash
# Demo + tasks
mvn test

# Only the demo tests
mvn test -Dtest='*AnnotationsDemoTest,RetentionAndTargetDemoTest'

# Only your task tests
mvn test -Dtest='*TasksTest'

# Run a demo
mvn exec:java -Dexec.mainClass="com.vbforge.annotations.BuiltInAnnotationsDemo"
```

Other demos: `CustomAnnotationsDemo`, `RetentionAndTargetDemo` (same package).
Per-concept test runs: `-Dtest=BuiltInAnnotationsDemoTest`, `-Dtest=CustomAnnotationsDemoTest`,
`-Dtest=RetentionAndTargetDemoTest` (and the matching `*TasksTest` classes).
