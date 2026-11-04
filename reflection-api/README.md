# reflection-api

> **Phase 9 — Advanced Topics** | Java 8 | Tags: `REFLECTION` | Package: `com.vbforge.reflection_api`

Class introspection, dynamic field/method access, annotation processing — plus JDK dynamic proxies, the mechanism that turns all three into real frameworks.

## What's covered

| Topic | Key points |
|---|---|
| Class introspection | `Class.forName`, `getClass()`, `X.class`, `getName` vs `getCanonicalName`, `getSuperclass`, `getInterfaces`, `isAssignableFrom`, `Modifier`, `getFields` vs `getDeclaredFields`, `getMethods` vs `getDeclaredMethods`, bridge methods, `getGenericType`, `ParameterizedType` |
| Dynamic field / method access | `getDeclaredField`, `setAccessible(true)`, `Field.get/set/getInt/setInt`, `Method.invoke`, `InvocationTargetException`, `getDeclaredConstructor().newInstance()`, `java.lang.reflect.Array`, final-field behaviour, Java 9+ `InaccessibleObjectException` |
| Annotation processing | `@Retention`, `AnnotatedElement`, `getAnnotation`, `getDeclaredAnnotations`, `@Inherited`, `@Repeatable`, `getAnnotationsByType`, annotation-driven validator and command dispatcher |
| Dynamic proxies | `Proxy.newProxyInstance`, `InvocationHandler`, `UndeclaredThrowableException`, `toString`/`equals`/`hashCode` interception, annotation-driven security (mini Spring AOP) |

## Files

```
src/
├── main/java/com/vbforge/reflection_api/
│   ├── ClassIntrospectionDemo.java      ← Class objects, hierarchy, members, generics (read this first)
│   ├── ClassIntrospectionTasks.java     ← 🎯 your tasks (4)
│   ├── DynamicAccessDemo.java           ← fields, methods, constructors, arrays, module encapsulation
│   ├── DynamicAccessTasks.java          ← 🎯 your tasks (5)
│   ├── AnnotationProcessingDemo.java    ← retention, reading, @Inherited, @Repeatable, validator, dispatcher
│   ├── AnnotationProcessingTasks.java   ← 🎯 your tasks (4)
│   ├── DynamicProxyDemo.java            ← Proxy, handlers, exception unwrapping, annotation-driven guard
│   └── DynamicProxyTasks.java           ← 🎯 your tasks (3)
└── test/java/com/vbforge/reflection_api/
    ├── ClassIntrospectionDemoTest.java     ← 🧪 tests for the demo (already green)
    ├── ClassIntrospectionTasksTest.java    ← 🧪 tests for YOUR implementation
    ├── DynamicAccessDemoTest.java          ← 🧪 tests for the demo (already green)
    ├── DynamicAccessTasksTest.java         ← 🧪 tests for YOUR implementation
    ├── AnnotationProcessingDemoTest.java   ← 🧪 tests for the demo (already green)
    ├── AnnotationProcessingTasksTest.java  ← 🧪 tests for YOUR implementation
    ├── DynamicProxyDemoTest.java           ← 🧪 tests for the demo (already green)
    └── DynamicProxyTasksTest.java          ← 🧪 tests for YOUR implementation
```

## 🎯 Tasks

| # | Method | Topic |
|---|---|---|
| 1 | `ClassIntrospectionTasks.sortedDeclaredFieldNames` | `getDeclaredFields`, synthetic fields, sorting |
| 2 | `ClassIntrospectionTasks.allInterfaces` | `getInterfaces` + `getSuperclass` recursion |
| 3 | `ClassIntrospectionTasks.isPlainConcreteClass` | `Modifier.isAbstract`, `isInterface`, `isEnum`, `isArray` |
| 4 | `ClassIntrospectionTasks.genericTypeArguments` | `getGenericType`, `ParameterizedType` |
| 5 | `DynamicAccessTasks.readField` | private fields up the class hierarchy |
| 6 | `DynamicAccessTasks.writeField` | `setAccessible`, `Field.set`, unboxing |
| 7 | `DynamicAccessTasks.invokeNoArg` | `Method.invoke`, unwrapping `InvocationTargetException` |
| 8 | `DynamicAccessTasks.newInstance` | private constructors, exception translation |
| 9 | `DynamicAccessTasks.toFieldMap` | walking superclasses, `Modifier.isStatic`, hidden fields |
| 10 | `AnnotationProcessingTasks.annotationNames` | `AnnotatedElement.getAnnotations`, `@Inherited` |
| 11 | `AnnotationProcessingTasks.fieldsWithAnnotation` | `isAnnotationPresent` on fields |
| 12 | `AnnotationProcessingTasks.columnMapping` | reading `@Column`, defaults, static fields |
| 13 | `AnnotationProcessingTasks.tagValues` | `@Repeatable`, `getAnnotationsByType` |
| 14 | `DynamicProxyTasks.countingProxy` | `Proxy.newProxyInstance`, `InvocationHandler` |
| 15 | `DynamicProxyTasks.memoizingProxy` | cache keys, `null` args, exceptions not cached |
| 16 | `DynamicProxyTasks.readOnlyView` | method-name rules, delegation |

```bash
mvn test -Dtest=ClassIntrospectionTasksTest
mvn test -Dtest=DynamicAccessTasksTest
mvn test -Dtest=AnnotationProcessingTasksTest
mvn test -Dtest=DynamicProxyTasksTest
```

> Every `@Nested` class in a `*TasksTest` starts with `@Disabled("Remove @Disabled when you implement <method>()")`.
> Remove it from the one you want to solve.

## Key things to remember

**`getXxx()` vs `getDeclaredXxx()` — "public + inherited" vs "everything + this class only"**

```java
Dog.class.getFields();            // ✅ public fields, inherited ones included
Dog.class.getDeclaredFields();    // ✅ ALL fields of Dog — but nothing from Animal
// ❌ expecting Animal's private/protected fields here — walk getSuperclass() yourself:
for (Class<?> c = type; c != null; c = c.getSuperclass()) { c.getDeclaredFields(); }
```

**Private members need `setAccessible(true)` — and the JDK is off limits since Java 16/17**

```java
Field f = Account.class.getDeclaredField("balance");
f.get(account);                 // ❌ IllegalAccessException from another class
f.setAccessible(true);          // ✅ fine for YOUR classes
String.class.getDeclaredField("value").setAccessible(true);
                                // ❌ InaccessibleObjectException on Java 17+ (worked on Java 8)
```

**Look-ups use exact parameter types — no boxing**

```java
Account.class.getMethod("deposit", int.class);      // ✅
Account.class.getMethod("deposit", Integer.class);  // ❌ NoSuchMethodException
method.invoke(account, 25);                         // ✅ BUT passing the argument boxes/unboxes for you
```

**`InvocationTargetException` hides the real exception**

```java
try {
    method.invoke(target, args);
} catch (InvocationTargetException e) {
    throw e.getCause();            // ✅ the exception the method really threw
}                                  // ❌ rethrowing `e` — callers can no longer catch the business exception
```

**`final` can be bypassed by reflection — but a constant is inlined by the compiler**

```java
private final String currency = "USD";   // constant variable → javac copies "USD" into every reader
field.set(account, "EUR");               // ✅ field now holds "EUR"
account.getCurrency();                   // ❌ still "USD" — two diverging truths
private static final int MAX = 10;       // ❌ Field.set → IllegalAccessException, even after setAccessible
```

**Bridge methods and unspecified order show up in `getDeclaredMethods()` / `getDeclaredFields()`**

```java
class Dog implements Comparable<Dog> { public int compareTo(Dog o) {…} }
// ✅ two compareTo methods exist: compareTo(Dog) and a synthetic bridge compareTo(Object)
if (m.isBridge() || m.isSynthetic()) continue;   // filter them when listing "real" methods
// ❌ relying on the ORDER of getDeclaredFields() — sort by name for deterministic output
```

**An annotation is invisible at runtime unless it has `@Retention(RUNTIME)`**

```java
@interface Foo {}                                      // ❌ default is CLASS retention → getAnnotation returns null
@Retention(RetentionPolicy.RUNTIME) @interface Foo {}  // ✅ visible to reflection
```

**Repeated annotations hide in a container; `@Inherited` only works on classes**

```java
@Tag("a") @Tag("b") class X {}
X.class.getAnnotation(Tag.class);          // ❌ null — the compiler wrapped both into @Tags
X.class.getAnnotationsByType(Tag.class);   // ✅ works for 0, 1 or many
// @Inherited: a sub-class sees the class-level annotation, but NOT on methods, fields or interfaces
```

**Dynamic proxies: interfaces only, unwrap exceptions, beware `equals`**

```java
Proxy.newProxyInstance(loader, new Class<?>[] {SimpleCalculator.class}, h); // ❌ IllegalArgumentException — a class
Proxy.newProxyInstance(loader, new Class<?>[] {Calculator.class}, h);        // ✅ interface

// in the handler:
return method.invoke(target, args);     // ❌ ITE escapes → caller gets UndeclaredThrowableException
catch (InvocationTargetException e) { throw e.getCause(); }  // ✅

proxy.equals(proxy);   // ❌ false when equals is naively delegated to the target
args == null           // ✅ remember: no-arg methods get null, not an empty array
```

**The `Method` given to a proxy handler is the interface's — annotations must be there**

```java
interface UserService { @RequiresRole("ADMIN") void delete(int id); }   // ✅ handler sees it
class Impl implements UserService { @RequiresRole("ADMIN") public void delete(int id) {…} }
// ❌ annotation on the implementation only → handler never sees it; method annotations are not inherited from interfaces
```

**Cache what you look up**

```java
method.invoke(...)                                    // ✅ cheap once you hold the Method
Account.class.getMethod("deposit", int.class)         // ❌ searches every call and returns a fresh COPY
// copies are equals() but not ==  → cache by name/signature, never compare with ==
```

## Run

```bash
# Demo + task tests (task tests are @Disabled until you enable them)
mvn test

# Only the demo tests (one per concept)
mvn test -Dtest=ClassIntrospectionDemoTest
mvn test -Dtest='*DemoTest'

# Only your task tests
mvn test -Dtest=ClassIntrospectionTasksTest
mvn test -Dtest='*TasksTest'

# Run a demo with console output
mvn exec:java -Dexec.mainClass="com.vbforge.reflection_api.ClassIntrospectionDemo"
```

The other demos: `DynamicAccessDemo`, `AnnotationProcessingDemo`, `DynamicProxyDemo` (same package).
