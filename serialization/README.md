# serialization

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 8 | Package: `com.vbforge.serialization`

**Tags:** `I/O`, `SERIALIZATION`

## What's covered

| Topic | Key points |
|---|---|
| `Serializable` | Marker interface (no methods); stream starts with magic `0xACED`; copy is a new object; constructors do not run |
| Object streams | `ObjectOutputStream.writeObject` / `ObjectInputStream.readObject`; read in write order; `EOFException` at the end; primitives stay buffered until `flush`/`close` |
| Object graphs | Shared references and cycles preserved; round trip = deep copy; same object written twice = back-reference; `reset()` |
| `NotSerializableException` | Every reachable object must be serializable; checked at runtime; `instanceof Serializable` is not enough |
| Inheritance | Subclass of a serializable class is serializable; non-serializable parent needs a no-arg constructor (`InvalidClassException` otherwise) |
| `transient` | Skipped by default serialization → `null`/`0`/`false` after reading; `static` fields are never serialized |
| `serialVersionUID` | Declared (`static final long`) or computed by the JVM; mismatch → `InvalidClassException`; `ObjectStreamClass.lookup` |
| `writeObject` / `readObject` | Private hooks; `defaultWriteObject` / `defaultReadObject` first; rebuild derived transient state |
| `readResolve` | Keeps singletons unique after deserialization; enums keep identity automatically |
| `Externalizable` | `writeExternal` / `readExternal`; public no-arg constructor runs first; smaller streams; your own version byte |

## Files

```
src/
├── main/java/com/vbforge/serialization/
│   ├── ObjectStreamsDemo.java            ← theory: Serializable, object streams, graphs, inheritance
│   ├── ObjectStreamsTasks.java           ← 🎯 your tasks — implement these methods
│   ├── TransientAndVersionDemo.java      ← theory: transient, serialVersionUID, hooks, readResolve
│   ├── TransientAndVersionTasks.java     ← 🎯 your tasks — implement these methods
│   ├── ExternalizableDemo.java           ← theory: Externalizable, no-arg constructor, versioning
│   └── ExternalizableTasks.java          ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/serialization/
    ├── ObjectStreamsDemoTest.java        ← tests for the demo (already green)
    ├── ObjectStreamsTasksTest.java       ← 🧪 tests for your tasks — all must pass
    ├── TransientAndVersionDemoTest.java  ← tests for the demo (already green)
    ├── TransientAndVersionTasksTest.java ← 🧪 tests for your tasks — all must pass
    ├── ExternalizableDemoTest.java       ← tests for the demo (already green)
    └── ExternalizableTasksTest.java      ← 🧪 tests for your tasks — all must pass
```

All streams are in-memory (`byte[]`) — no files, so nothing depends on your machine.

## 🎯 Tasks

Open each `*Tasks.java` and implement its methods. Each has a detailed Javadoc.

| # | Class | Method | Topic |
|---|---|---|---|
| 1 | `ObjectStreamsTasks` | `serialize(obj)` | `ObjectOutputStream` over `ByteArrayOutputStream` |
| 2 | `ObjectStreamsTasks` | `deserialize(data)` | `ObjectInputStream`, casting the result |
| 3 | `ObjectStreamsTasks` | `deepCopy(original)` | Round trip = deep copy, shared references kept |
| 4 | `ObjectStreamsTasks` | `isSerializable(obj)` | Catching `NotSerializableException` |
| 5 | `ObjectStreamsTasks` | `serializeAll(objects)` | Many objects in one stream |
| 6 | `ObjectStreamsTasks` | `deserializeAll(data)` | Reading until `EOFException` |
| 7 | `TransientAndVersionTasks` | `transientFieldNames(type)` | `Modifier.isTransient` / `isStatic` |
| 8 | `TransientAndVersionTasks` | `persistedFieldValues(obj)` | What default serialization would write |
| 9 | `TransientAndVersionTasks` | `effectiveSerialVersionUID(type)` | `ObjectStreamClass.lookup` |
| 10 | `TransientAndVersionTasks` | `deserializeOrNull(data)` | Handling `InvalidClassException` |
| 11 | `ExternalizableTasks` | `serializedSize(obj)` | Measuring stream size |
| 12 | `ExternalizableTasks` | `hasPublicNoArgConstructor(type)` | `Class.getConstructor()` (public only) |
| 13 | `ExternalizableTasks` | `serializationKind(type)` | `isAssignableFrom`; Externalizable ⊂ Serializable |
| 14 | `ExternalizableTasks` | `writeStringList(items, out)` | What goes inside `writeExternal` |
| 15 | `ExternalizableTasks` | `readStringList(in)` | What goes inside `readExternal` |

```bash
mvn test -Dtest=ObjectStreamsTasksTest
mvn test -Dtest=TransientAndVersionTasksTest
mvn test -Dtest=ExternalizableTasksTest
```

All 15 tasks must go **GREEN** ✅ (remove `@Disabled` from one `@Nested` class at a time).

## Key things to remember

```java
// ✅ Close (or flush) the ObjectOutputStream BEFORE taking the bytes
ByteArrayOutputStream bytes = new ByteArrayOutputStream();
try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
    out.writeObject(obj);
}
byte[] data = bytes.toByteArray();     // ✅ after close
// ❌ toByteArray() inside the try block can miss buffered primitives (writeInt, writeUTF)

// ❌ instanceof Serializable proves nothing about the CONTENT
List<Object> list = new ArrayList<>(Arrays.asList("ok", new Object()));
list instanceof Serializable;          // true
// writeObject(list) → NotSerializableException (checked at RUNTIME, not compile time)

// ✅ transient = skipped; after reading you get null / 0 / false — NOT the constructor value
private transient String password;     // null after deserialization
private static String theme;           // static is never serialized either

// ✅ Declare the version yourself — it must be static AND final
private static final long serialVersionUID = 1L;
// ❌ without 'final' it is silently ignored and the JVM computes one instead

// ❌ A public hook is never called — it MUST be private
public void writeObject(ObjectOutputStream out) { ... }    // ignored!
private void writeObject(ObjectOutputStream out) throws IOException {   // ✅
    out.defaultWriteObject();          // first: all non-transient fields
    out.writeObject(extra);            // then your own data — read back in the SAME order
}

// ❌ Same object written twice in one stream: the 2nd write is only a back-reference
out.writeObject(p);
p.setAge(2);
out.writeObject(p);                    // age 2 is NOT written …
out.reset();                           // ✅ … unless you call reset() before the second write

// ✅ Singleton? Add readResolve, otherwise you get a second instance (enums need nothing)
private Object readResolve() { return INSTANCE; }

// ❌ Non-serializable parent WITHOUT a no-arg constructor:
//    writing works, reading throws InvalidClassException ("no valid constructor")
// ✅ With a no-arg constructor, it runs on read — parent fields get ITS values, not the original's

// ❌ Externalizable needs a PUBLIC no-arg constructor (package-private is not enough)
public MyClass() { }                   // ✅ runs first, then readExternal(in)
// Read in readExternal in EXACTLY the order you wrote in writeExternal

// ⚠️ Never deserialize bytes you do not trust — deserialization can run attacker-chosen code paths.
//    For data exchange with other systems prefer a data format such as JSON (see json-jackson-basics).
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=*DemoTest

# Task tests only
mvn test -Dtest=*TasksTest

# Run the demos
mvn exec:java -Dexec.mainClass="com.vbforge.serialization.ObjectStreamsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.serialization.TransientAndVersionDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.serialization.ExternalizableDemo"
```
