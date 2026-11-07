package com.vbforge.practice.practice_17_serialization_advanced;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Demonstrates Java Object Serialization — converting an object graph to bytes
 * and back. Introduced in Java 1.1; still widely used in caching, RMI, and sessions.
 *
 * How it works:
 *   ObjectOutputStream traverses the object graph depth-first, writes class
 *   metadata (class descriptor) + field values to the stream.
 *   ObjectInputStream reads the descriptor, loads the class, creates an instance
 *   WITHOUT calling any constructor, and sets field values via reflection.
 *
 * Contracts:
 *   1. The class must implement java.io.Serializable (marker interface, no methods).
 *   2. All non-transient, non-static fields must also be Serializable.
 *   3. serialVersionUID should be declared explicitly — without it the JVM generates
 *      one from class structure; any change breaks deserialization of old data.
 *
 * transient keyword:
 *   - Marks a field as NOT serializable (excluded from the stream).
 *   - Use for: passwords, open sockets/streams, cached computed values.
 *   - transient fields receive their default value on deserialization (null / 0 / false).
 *
 * Customization hooks:
 *   - writeObject(ObjectOutputStream) / readObject(ObjectInputStream) — custom logic.
 *   - writeReplace() / readResolve() — substitute a different object.
 *   - Externalizable — full manual control (implements readExternal/writeExternal).
 *
 * Security warning:
 *   Deserializing untrusted data is a CRITICAL security risk (gadget chains).
 *   Modern recommendation: prefer JSON/Protobuf for cross-system serialization.
 *   For JVM-only caching, consider Kryo or modern alternatives.
 */
public class SerializationDemo {

    static final Path DATA_DIR = Path.of("file-io-basics/data_files");

    // =========================================================================
    // Domain objects
    // =========================================================================

    static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final int    age;
        private transient String cachedLabel;

        Person(String name, int age) {
            this.name = name;
            this.age  = age;
            this.cachedLabel = buildLabel();
        }

        private String buildLabel() { return name + " (" + age + ")"; }

        private Object readResolve() throws ObjectStreamException {
            this.cachedLabel = buildLabel();
            return this;
        }

        String getName()       { return name; }
        int    getAge()        { return age; }
        String getCachedLabel(){ return cachedLabel; }

        @Override
        public String toString() {
            return "Person{name='" + name + "', age=" + age
                + ", label='" + cachedLabel + "'}";
        }
    }

    static class SecureEmployee implements Serializable {
        private static final long serialVersionUID = 2L;

        private final String name;
        private transient String password;

        SecureEmployee(String name, String password) {
            this.name = name;
            this.password = password;
        }

        String getName()     { return name; }
        String getPassword() { return password; }

        private void writeObject(ObjectOutputStream oos) throws IOException {
            oos.defaultWriteObject();
        }

        private void readObject(ObjectInputStream ois)
                throws IOException, ClassNotFoundException {
            ois.defaultReadObject();
            this.password = "<redacted>";
        }

        @Override
        public String toString() {
            return "SecureEmployee{name='" + name + "', password='" + password + "'}";
        }
    }

    // =========================================================================
    // 1. Basic serialize / deserialize
    // =========================================================================

    static void basicSerializationDemo() throws IOException, ClassNotFoundException {
        System.out.println("\n--- 1. Basic Serialize / Deserialize ---");

        Person original = new Person("Alice", 30);
        System.out.println("original:     " + original);

        Path out = DATA_DIR.resolve("person.ser");

        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(out.toFile()))) {
            oos.writeObject(original);
            System.out.println("serialized to: " + out.getFileName()
                + " (" + Files.size(out) + " bytes)");
        }

        Person restored;
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(out.toFile()))) {
            restored = (Person) ois.readObject();
        }

        System.out.println("restored:     " + restored);
        System.out.println("same object?  " + (original == restored));
        System.out.println("equal fields? " + original.getName().equals(restored.getName())
            + " / " + (original.getAge() == restored.getAge()));

        Files.deleteIfExists(out);
    }

    // =========================================================================
    // 2. transient fields — excluded from stream
    // =========================================================================

    static void transientFieldDemo() throws IOException, ClassNotFoundException {
        System.out.println("\n--- 2. transient fields ---");

        Person p = new Person("Bob", 25);
        System.out.println("before: cachedLabel = " + p.getCachedLabel());

        byte[] bytes = serialize(p);
        Person restored = (Person) deserialize(bytes);

        System.out.println("after:  cachedLabel = " + restored.getCachedLabel());
        System.out.println("(without readResolve it would be: null)");
    }

    // =========================================================================
    // 3. Custom writeObject / readObject
    // =========================================================================

    static void customReadWriteDemo() throws IOException, ClassNotFoundException {
        System.out.println("\n--- 3. Custom writeObject / readObject ---");

        SecureEmployee emp = new SecureEmployee("Charlie", "s3cr3t!");
        System.out.println("original:   " + emp);

        byte[] bytes = serialize(emp);
        SecureEmployee restored = (SecureEmployee) deserialize(bytes);

        System.out.println("restored:   " + restored);
        System.out.println("password was transient → restored as: " + restored.getPassword());
    }

    // =========================================================================
    // 4. Object graphs — serialization follows references
    // =========================================================================

    static class Department implements Serializable {
        private static final long serialVersionUID = 3L;

        final String name;
        final java.util.List<Person> members = new java.util.ArrayList<>();

        Department(String name) { this.name = name; }

        @Override
        public String toString() {
            return "Department{" + name + ", members=" + members.size() + "}";
        }
    }

    static void objectGraphDemo() throws IOException, ClassNotFoundException {
        System.out.println("\n--- 4. Object graphs ---");

        Department dept = new Department("Engineering");
        dept.members.add(new Person("Dave",  33));
        dept.members.add(new Person("Eve",   27));
        dept.members.add(new Person("Frank", 41));

        byte[] bytes = serialize(dept);
        Department restored = (Department) deserialize(bytes);

        System.out.println("original:  " + dept);
        System.out.println("restored:  " + restored);
        System.out.println("first member: " + restored.members.get(0).getName());
    }

    // =========================================================================
    // 5. Externalizable — full manual control
    // =========================================================================

    static class Point implements Externalizable {
        public Point() {}

        private int x, y;

        Point(int x, int y) { this.x = x; this.y = y; }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(x);
            out.writeInt(y);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            this.x = in.readInt();
            this.y = in.readInt();
        }

        @Override
        public String toString() { return "Point(" + x + ", " + y + ")"; }
    }

    static void externalizableDemo() throws IOException, ClassNotFoundException {
        System.out.println("\n--- 5. Externalizable ---");

        Point original = new Point(10, 20);
        System.out.println("original:  " + original);

        byte[] bytes = serialize(original);
        Point restored = (Point) deserialize(bytes);
        System.out.println("restored:  " + restored);
    }

    // =========================================================================
    // Helpers — serialize/deserialize to/from byte[]
    // =========================================================================

    static byte[] serialize(Object obj) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(obj);
        }
        return baos.toByteArray();
    }

    @SuppressWarnings("unchecked")
    static <T> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new ByteArrayInputStream(bytes))) {
            return (T) ois.readObject();
        }
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        System.out.println("=== Serialization Demo ===");
        basicSerializationDemo();
        transientFieldDemo();
        customReadWriteDemo();
        objectGraphDemo();
        externalizableDemo();
    }
}