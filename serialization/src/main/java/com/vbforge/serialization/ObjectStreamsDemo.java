package com.vbforge.serialization;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * ObjectStreamsDemo — annotated reference for {@link Serializable} and the object streams
 * {@link ObjectOutputStream} / {@link ObjectInputStream}.
 *
 * <p>Read top-to-bottom. Every demo writes to an in-memory {@code byte[]} instead of a file.
 * The API is identical for files ({@code FileOutputStream}) or sockets — only the
 * underlying stream changes — and in-memory bytes keep the demo and the tests free of
 * file-system dependencies.
 *
 * <p>Topics:
 * <ol>
 *   <li>Serializable basics — the marker interface, the stream header, round trip</li>
 *   <li>Object streams — writing several values into one stream and reading them back</li>
 *   <li>Object graphs — shared references, cycles, deep copy, {@code reset()}</li>
 *   <li>NotSerializableException — what happens when one field is not serializable</li>
 *   <li>Inheritance rules — non-serializable parents and the no-arg constructor</li>
 * </ol>
 */
public class ObjectStreamsDemo {

    public static void main(String[] args) throws Exception {
        serializableBasicsDemo();
        System.out.println("---");
        objectStreamsDemo();
        System.out.println("---");
        objectGraphDemo();
        System.out.println("---");
        notSerializableDemo();
        System.out.println("---");
        inheritanceRulesDemo();
    }

    // ── Model classes used by the demos (public so tests can reuse them) ──────

    /** A plain value object. Serializable, with an explicit {@code serialVersionUID}. */
    public static class Person implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private int age;

        public Person(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() { return name; }
        public int getAge()     { return age; }
        public void setAge(int age) { this.age = age; }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Person)) return false;
            Person other = (Person) o;
            return age == other.age && Objects.equals(name, other.name);
        }

        @Override
        public int hashCode() { return Objects.hash(name, age); }

        @Override
        public String toString() { return "Person{" + name + ", " + age + "}"; }
    }

    /** A team whose {@code lead} is the SAME object as one of the {@code members}. */
    public static class Team implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final List<Person> members = new ArrayList<>();
        private Person lead;

        public Team(String name) { this.name = name; }

        public String getName()          { return name; }
        public List<Person> getMembers() { return members; }
        public Person getLead()          { return lead; }
        public void addMember(Person p)  { members.add(p); }
        public void setLead(Person p)    { this.lead = p; }
    }

    /** A node of a (possibly cyclic) linked structure. */
    public static class Node implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private Node next;

        public Node(String name) { this.name = name; }

        public String getName()        { return name; }
        public Node getNext()          { return next; }
        public void setNext(Node next) { this.next = next; }
    }

    /** NOT serializable — it does not implement {@link Serializable}. */
    public static class Address {
        private final String city;

        public Address(String city) { this.city = city; }

        public String getCity() { return city; }
    }

    /** Serializable class that holds a NON-serializable field. */
    public static class Customer implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;
        private final Address address;       // ← not serializable!

        public Customer(String name, Address address) {
            this.name = name;
            this.address = address;
        }

        public String getName() { return name; }
        public Address getAddress() { return address; }
    }

    /** Serializable replacement for {@link Address}. */
    public static class ShippingAddress implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String city;

        public ShippingAddress(String city) { this.city = city; }

        public String getCity() { return city; }
    }

    /** Serializable class whose fields are all serializable — works fine. */
    public static class Order implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String id;
        private final ShippingAddress address;

        public Order(String id, ShippingAddress address) {
            this.id = id;
            this.address = address;
        }

        public String getId() { return id; }
        public ShippingAddress getAddress() { return address; }
    }

    /** A NON-serializable parent that has a no-arg constructor. */
    public static class Animal {
        protected String kind;

        public Animal() { this.kind = "unknown"; }                 // runs on deserialization!
        public Animal(String kind) { this.kind = kind; }

        public String getKind() { return kind; }
    }

    /** Serializable child of a non-serializable parent. */
    public static class Dog extends Animal implements Serializable {
        private static final long serialVersionUID = 1L;

        private final String name;

        public Dog(String kind, String name) {
            super(kind);
            this.name = name;
        }

        public String getName() { return name; }
    }

    /** A non-serializable parent WITHOUT a no-arg constructor. */
    public static class NoDefaultCtorParent {
        protected final int id;

        public NoDefaultCtorParent(int id) { this.id = id; }
    }

    /** Serializes fine, but can never be deserialized — its parent has no no-arg constructor. */
    public static class BrokenChild extends NoDefaultCtorParent implements Serializable {
        private static final long serialVersionUID = 1L;

        public BrokenChild(int id) { super(id); }
    }

    /** Subclass of an already-serializable class — no need to repeat {@code implements Serializable}. */
    public static class Employee extends Person {
        private static final long serialVersionUID = 1L;

        private final String department;

        public Employee(String name, int age, String department) {
            super(name, age);
            this.department = department;
        }

        public String getDepartment() { return department; }
    }

    // ── Helpers (package-private: reused by the other demos and by the tests) ──

    /**
     * Serializes {@code obj} to a byte array.
     *
     * <p>The stream is closed BEFORE {@code toByteArray()} is called — closing flushes the
     * stream's internal buffer. Primitives written with {@code writeInt}/{@code writeUTF}
     * stay in that buffer until a flush, so reading the bytes too early can truncate them.
     */
    static byte[] toBytes(Object obj) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(obj);
        }
        return bytes.toByteArray();
    }

    /** Reads the first object from {@code data}. The caller must cast the result. */
    static Object fromBytes(byte[] data) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            return in.readObject();
        }
    }

    /** Serialize + deserialize in one step: the classic "deep copy via serialization" trick. */
    @SuppressWarnings("unchecked")
    static <T> T roundTrip(T obj) throws IOException, ClassNotFoundException {
        return (T) fromBytes(toBytes(obj));
    }

    /**
     * Writes the same {@code Person} twice into ONE stream, changing its age in between,
     * and returns the two objects read back.
     *
     * @param reset if {@code true}, calls {@code ObjectOutputStream.reset()} between the writes
     */
    static Person[] snapshotTwice(boolean reset) throws IOException, ClassNotFoundException {
        Person p = new Person("Dave", 1);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(p);
            p.setAge(2);
            if (reset) {
                out.reset();
            }
            out.writeObject(p);
        }
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            return new Person[]{(Person) in.readObject(), (Person) in.readObject()};
        }
    }

    // ── 1. Serializable basics ────────────────────────────────────────────────

    /**
     * {@link Serializable} is a MARKER interface — it declares no methods. Implementing it
     * only tells the JVM "instances of this class may be turned into bytes".
     *
     * <p>WHY this matters: serialization converts an object graph into a byte stream that
     * can be stored, sent over a network, or copied, and later rebuilt into an equivalent
     * graph. Every Java-native cache, session replication, or RMI call relies on it.
     *
     * <p>What you get:
     * <ul>
     *   <li>The stream always starts with the magic number {@code 0xACED} followed by version {@code 0x0005}</li>
     *   <li>The copy is a NEW object: {@code equals} is true, {@code ==} is false</li>
     * </ul>
     *
     * <p>Common gotcha: the stream carries the class name and the field names as text,
     * not just the values — so the bytes are tightly coupled to your class structure.
     */
    static void serializableBasicsDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Serializable basics ===");

        Person original = new Person("Alice", 30);
        byte[] data = toBytes(original);

        System.out.println("declared methods in Serializable : " + Serializable.class.getDeclaredMethods().length); // 0
        System.out.println("stream length                     : " + data.length + " bytes");
        System.out.printf("first two bytes (magic)           : %02X%02X%n", data[0], data[1]);                    // ACED

        Person copy = (Person) fromBytes(data);
        System.out.println("copy                              : " + copy);
        System.out.println("copy == original                  : " + (copy == original));      // false — new object
        System.out.println("copy.equals(original)             : " + copy.equals(original));   // true  — same state
    }

    // ── 2. Object streams ─────────────────────────────────────────────────────

    /**
     * {@link ObjectOutputStream} writes primitives ({@code writeInt}, {@code writeUTF}, …)
     * AND objects ({@code writeObject}) into one stream; {@link ObjectInputStream} must read
     * them back in EXACTLY the same order.
     *
     * <p>WHY this matters: a single stream can carry a whole "file format" — a header, some
     * counters, then a series of objects.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>{@code readObject()} returns {@code Object} — you must cast it yourself</li>
     *   <li>Reading past the last item throws {@link EOFException}</li>
     *   <li>Primitives ({@code writeInt}, {@code writeUTF}) stay buffered until you flush or
     *       close the output stream — do that before using the bytes</li>
     * </ul>
     */
    static void objectStreamsDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Object streams ===");

        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeInt(2026);                                       // primitive
            out.writeUTF("header");                                   // String, compact encoding
            out.writeObject(new Person("Bob", 25));                   // object
            out.writeObject(new ArrayList<>(Arrays.asList("x", "y"))); // collection (ArrayList is Serializable)
        }

        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()))) {
            int number    = in.readInt();
            String header = in.readUTF();
            Person bob    = (Person) in.readObject();                 // explicit cast
            @SuppressWarnings("unchecked")
            List<String> letters = (List<String>) in.readObject();

            System.out.println("int      : " + number);   // 2026
            System.out.println("UTF      : " + header);   // header
            System.out.println("object   : " + bob);      // Person{Bob, 25}
            System.out.println("list     : " + letters);  // [x, y]

            try {
                in.readObject();                          // nothing left
            } catch (EOFException e) {
                System.out.println("past the end : EOFException");
            }
        }
    }

    // ── 3. Object graphs ──────────────────────────────────────────────────────

    /**
     * Serialization does not copy a single object — it walks the WHOLE graph of objects
     * reachable from it, and it remembers every object it has already written.
     *
     * <p>WHY this matters: because of that memory, the shape of the graph survives:
     * <ul>
     *   <li>Two fields pointing to the same object still point to one (new) shared object</li>
     *   <li>Cycles (a → b → a) do not cause infinite recursion</li>
     *   <li>The result is a complete DEEP copy — changing the copy never affects the original</li>
     * </ul>
     *
     * <p>Common gotcha: the same mechanism means that writing one object TWICE into the same
     * stream writes it fully only the first time; the second write is just a back-reference.
     * If you mutated the object in between, the change is silently lost — unless you call
     * {@code reset()}, which makes the stream forget everything it has written.
     */
    static void objectGraphDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Object graphs ===");

        // Shared reference + deep copy
        Person alice = new Person("Alice", 30);
        Team team = new Team("Core");
        team.addMember(alice);
        team.addMember(new Person("Bob", 25));
        team.setLead(alice);

        Team copy = roundTrip(team);
        System.out.println("lead is members[0] in copy  : " + (copy.getLead() == copy.getMembers().get(0))); // true
        System.out.println("copy's lead == original lead: " + (copy.getLead() == alice));                    // false

        copy.getMembers().add(new Person("Carol", 41));
        System.out.println("original / copy member count: " + team.getMembers().size()
                + " / " + copy.getMembers().size());                                                          // 2 / 3

        // Cycle: a → b → a
        Node a = new Node("a");
        Node b = new Node("b");
        a.setNext(b);
        b.setNext(a);
        Node aCopy = roundTrip(a);
        System.out.println("cycle preserved            : " + (aCopy.getNext().getNext() == aCopy));         // true

        // Same object written twice into one stream
        Person[] withoutReset = snapshotTwice(false);
        System.out.println("no reset : same instance = " + (withoutReset[0] == withoutReset[1])
                + ", ages = " + withoutReset[0].getAge() + " and " + withoutReset[1].getAge());           // true, 1 and 1
        Person[] withReset = snapshotTwice(true);
        System.out.println("reset()  : same instance = " + (withReset[0] == withReset[1])
                + ", ages = " + withReset[0].getAge() + " and " + withReset[1].getAge());                 // false, 1 and 2
    }

    // ── 4. NotSerializableException ───────────────────────────────────────────

    /**
     * Declaring {@code implements Serializable} is not enough: EVERY object reachable from
     * the one you write must be serializable too (or its field must be {@code transient}).
     *
     * <p>WHY this matters: the check happens at RUNTIME, while writing. The compiler accepts
     * {@code writeObject(anything)}, so the error shows up late — often in production.
     *
     * <p>Common gotchas:
     * <ul>
     *   <li>The exception message is the class name of the offending object</li>
     *   <li>{@code ArrayList} is Serializable, but an {@code ArrayList} containing a
     *       non-serializable element still fails</li>
     *   <li>So {@code obj instanceof Serializable} does NOT guarantee that serialization works</li>
     * </ul>
     */
    static void notSerializableDemo() throws IOException {
        System.out.println("=== NotSerializableException ===");

        try {
            toBytes(new Customer("Ann", new Address("Paris")));
        } catch (NotSerializableException e) {
            System.out.println("Customer with Address     : NotSerializableException -> " + e.getMessage());
        }

        try {
            toBytes(new Object());
        } catch (NotSerializableException e) {
            System.out.println("new Object()              : NotSerializableException -> " + e.getMessage());
        }

        List<Object> mixed = new ArrayList<>(Arrays.asList("fine", new Object()));
        System.out.println("list instanceof Serializable: " + (mixed instanceof Serializable));          // true
        try {
            toBytes(mixed);
        } catch (NotSerializableException e) {
            System.out.println("list containing an Object : NotSerializableException -> " + e.getMessage());
        }

        // The fix: make the field's type Serializable.
        byte[] ok = toBytes(new Order("A-1", new ShippingAddress("Paris")));
        System.out.println("Order with ShippingAddress: OK (" + ok.length + " bytes)");
    }

    // ── 5. Inheritance rules ──────────────────────────────────────────────────

    /**
     * How inheritance interacts with serialization:
     * <ul>
     *   <li>If the PARENT is serializable, every subclass is automatically serializable
     *       and both parent and child fields are written</li>
     *   <li>If the parent is NOT serializable, its fields are NOT written. When the child is
     *       rebuilt, the parent's NO-ARG constructor runs to initialise those fields</li>
     *   <li>If that parent has no accessible no-arg constructor, writing succeeds but
     *       reading fails with {@link InvalidClassException}</li>
     * </ul>
     *
     * <p>WHY this matters: it is the one situation where a constructor DOES run during
     * deserialization — and the field values you get back are whatever that constructor sets,
     * not what the original object had.
     *
     * <p>Common gotcha: the failure is delayed — {@code toBytes} succeeds, so you only find out
     * the object is unreadable when you try to load it back.
     */
    static void inheritanceRulesDemo() throws IOException, ClassNotFoundException {
        System.out.println("=== Inheritance rules ===");

        // Serializable parent → child is serializable too
        Employee eve = new Employee("Eve", 28, "R&D");
        Employee eveCopy = roundTrip(eve);
        System.out.println("Employee is Serializable    : " + (eve instanceof Serializable));            // true
        System.out.println("Employee copy               : " + eveCopy + ", " + eveCopy.getDepartment()); // Person{Eve, 28}, R&D

        // Non-serializable parent → parent field reset by the no-arg constructor
        Dog rex = new Dog("mammal", "Rex");
        Dog rexCopy = roundTrip(rex);
        System.out.println("Dog.name (own field)        : " + rexCopy.getName());                        // Rex
        System.out.println("Dog.kind (parent field)     : " + rexCopy.getKind() + "  (was " + rex.getKind() + ")"); // unknown (was mammal)

        // Non-serializable parent without a no-arg constructor
        byte[] data = toBytes(new BrokenChild(7));                                                       // writing works
        System.out.println("BrokenChild written         : " + data.length + " bytes");
        try {
            fromBytes(data);
        } catch (InvalidClassException e) {
            System.out.println("BrokenChild read            : InvalidClassException -> " + e.getMessage());
        }
    }
}
