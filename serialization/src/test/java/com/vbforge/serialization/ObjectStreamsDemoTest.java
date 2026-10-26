package com.vbforge.serialization;

import com.vbforge.serialization.ObjectStreamsDemo.Address;
import com.vbforge.serialization.ObjectStreamsDemo.BrokenChild;
import com.vbforge.serialization.ObjectStreamsDemo.Customer;
import com.vbforge.serialization.ObjectStreamsDemo.Dog;
import com.vbforge.serialization.ObjectStreamsDemo.Employee;
import com.vbforge.serialization.ObjectStreamsDemo.Node;
import com.vbforge.serialization.ObjectStreamsDemo.Order;
import com.vbforge.serialization.ObjectStreamsDemo.Person;
import com.vbforge.serialization.ObjectStreamsDemo.ShippingAddress;
import com.vbforge.serialization.ObjectStreamsDemo.Team;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.InvalidClassException;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ObjectStreamsDemoTest — verifies every concept shown in {@link ObjectStreamsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * the Java serialization basics.
 */
@DisplayName("ObjectStreamsDemo — API contracts")
class ObjectStreamsDemoTest {

    // ── 1. Serializable basics ────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Serializable basics")
    class SerializableBasicsTest {

        @Test
        @DisplayName("Serializable is a marker interface with no methods")
        void markerInterface() {
            assertThat(Serializable.class.getDeclaredMethods()).isEmpty();
        }

        @Test
        @DisplayName("stream starts with the magic number 0xACED")
        void streamMagic() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new Person("Alice", 30));
            assertThat(data[0] & 0xFF).isEqualTo(0xAC);
            assertThat(data[1] & 0xFF).isEqualTo(0xED);
        }

        @Test
        @DisplayName("round trip gives an equal but different object")
        void roundTripCopy() throws Exception {
            Person original = new Person("Alice", 30);
            Person copy = (Person) ObjectStreamsDemo.fromBytes(ObjectStreamsDemo.toBytes(original));

            assertThat(copy).isEqualTo(original);
            assertThat(copy).isNotSameAs(original);
        }

        @Test
        @DisplayName("all non-transient fields are restored")
        void fieldsRestored() throws Exception {
            Person copy = ObjectStreamsDemo.roundTrip(new Person("Alice", 30));

            assertThat(copy.getName()).isEqualTo("Alice");
            assertThat(copy.getAge()).isEqualTo(30);
        }

        @Test
        @DisplayName("the stream carries class and field names as text")
        void namesInStream() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new Person("Alice", 30));
            String text = new String(data, StandardCharsets.ISO_8859_1);

            assertThat(text).contains("ObjectStreamsDemo$Person", "name", "age", "Alice");
        }
    }

    // ── 2. Object streams ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Object streams")
    class ObjectStreamsTest {

        private byte[] mixedStream() throws Exception {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
                out.writeInt(2026);
                out.writeUTF("header");
                out.writeObject(new Person("Bob", 25));
                out.writeObject(new ArrayList<>(Arrays.asList("x", "y")));
            }
            return bytes.toByteArray();
        }

        @Test
        @DisplayName("primitives and objects are read back in the order they were written")
        void sameOrder() throws Exception {
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(mixedStream()))) {
                assertThat(in.readInt()).isEqualTo(2026);
                assertThat(in.readUTF()).isEqualTo("header");
                assertThat(in.readObject()).isEqualTo(new Person("Bob", 25));
                assertThat(in.readObject()).isEqualTo(Arrays.asList("x", "y"));
            }
        }

        @Test
        @DisplayName("reading past the last item throws EOFException")
        void eofAtEnd() throws Exception {
            try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(mixedStream()))) {
                in.readInt();
                in.readUTF();
                in.readObject();
                in.readObject();

                assertThatThrownBy(in::readObject).isInstanceOf(EOFException.class);
            }
        }

        @Test
        @DisplayName("readObject returns Object — the caller casts")
        void readObjectReturnsObject() throws Exception {
            Object result = ObjectStreamsDemo.fromBytes(ObjectStreamsDemo.toBytes(new Person("Bob", 25)));

            assertThat(result).isInstanceOf(Person.class);
        }

        @Test
        @DisplayName("null can be written and read back")
        void nullRoundTrip() throws Exception {
            assertThat(ObjectStreamsDemo.fromBytes(ObjectStreamsDemo.toBytes(null))).isNull();
        }

        @Test
        @DisplayName("primitives written with writeInt stay buffered until flush or close")
        void primitivesBufferedUntilFlush() throws Exception {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes);
            out.writeInt(42);

            int beforeFlush = bytes.size();
            out.flush();
            int afterFlush = bytes.size();
            out.close();

            assertThat(beforeFlush).isLessThan(afterFlush);
        }
    }

    // ── 3. Object graphs ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Object graphs")
    class ObjectGraphTest {

        @Test
        @DisplayName("shared references stay shared inside the copy")
        void sharedReferencePreserved() throws Exception {
            Person alice = new Person("Alice", 30);
            Team team = new Team("Core");
            team.addMember(alice);
            team.addMember(new Person("Bob", 25));
            team.setLead(alice);

            Team copy = ObjectStreamsDemo.roundTrip(team);

            assertThat(copy.getLead()).isSameAs(copy.getMembers().get(0));
            assertThat(copy.getLead()).isNotSameAs(alice);
        }

        @Test
        @DisplayName("round trip is a deep copy — changing the copy does not touch the original")
        void deepCopy() throws Exception {
            Team team = new Team("Core");
            team.addMember(new Person("Alice", 30));

            Team copy = ObjectStreamsDemo.roundTrip(team);
            copy.getMembers().add(new Person("Carol", 41));
            copy.getMembers().get(0).setAge(99);

            assertThat(team.getMembers()).hasSize(1);
            assertThat(team.getMembers().get(0).getAge()).isEqualTo(30);
        }

        @Test
        @DisplayName("cycles are preserved and do not cause infinite recursion")
        void cyclesPreserved() throws Exception {
            Node a = new Node("a");
            Node b = new Node("b");
            a.setNext(b);
            b.setNext(a);

            Node aCopy = ObjectStreamsDemo.roundTrip(a);

            assertThat(aCopy.getNext().getName()).isEqualTo("b");
            assertThat(aCopy.getNext().getNext()).isSameAs(aCopy);
        }

        @Test
        @DisplayName("writing the same object twice yields one instance — the second write is a back-reference")
        void sameObjectTwice() throws Exception {
            Person[] result = ObjectStreamsDemo.snapshotTwice(false);

            assertThat(result[0]).isSameAs(result[1]);
            assertThat(result[1].getAge()).isEqualTo(1);   // the later change (age 2) was lost
        }

        @Test
        @DisplayName("reset() makes the stream write the changed object again")
        void resetWritesFreshCopy() throws Exception {
            Person[] result = ObjectStreamsDemo.snapshotTwice(true);

            assertThat(result[0]).isNotSameAs(result[1]);
            assertThat(result[0].getAge()).isEqualTo(1);
            assertThat(result[1].getAge()).isEqualTo(2);
        }

        @Test
        @DisplayName("collections of serializable elements round trip")
        void collectionsRoundTrip() throws Exception {
            Map<String, Person> people = new HashMap<>();
            people.put("a", new Person("Alice", 30));
            people.put("b", new Person("Bob", 25));

            Map<String, Person> copy = ObjectStreamsDemo.roundTrip(people);

            assertThat(copy).containsEntry("a", new Person("Alice", 30))
                            .containsEntry("b", new Person("Bob", 25))
                            .hasSize(2);
            assertThat(copy).isNotSameAs(people);
        }
    }

    // ── 4. NotSerializableException ───────────────────────────────────────────

    @Nested
    @DisplayName("4. NotSerializableException")
    class NotSerializableTest {

        @Test
        @DisplayName("a non-serializable field type fails at runtime and names the offending class")
        void nonSerializableField() {
            Customer customer = new Customer("Ann", new Address("Paris"));

            assertThatThrownBy(() -> ObjectStreamsDemo.toBytes(customer))
                    .isInstanceOf(NotSerializableException.class)
                    .hasMessageContaining("ObjectStreamsDemo$Address");
        }

        @Test
        @DisplayName("a plain Object is not serializable")
        void plainObject() {
            assertThatThrownBy(() -> ObjectStreamsDemo.toBytes(new Object()))
                    .isInstanceOf(NotSerializableException.class)
                    .hasMessageContaining("java.lang.Object");
        }

        @Test
        @DisplayName("ArrayList is Serializable, but a non-serializable element still fails")
        void listWithBadElement() {
            List<Object> mixed = new ArrayList<>(Arrays.asList("fine", new Object()));

            assertThat(mixed).isInstanceOf(Serializable.class);
            assertThatThrownBy(() -> ObjectStreamsDemo.toBytes(mixed))
                    .isInstanceOf(NotSerializableException.class);
        }

        @Test
        @DisplayName("making the field type Serializable fixes it")
        void serializableFieldWorks() throws Exception {
            Order copy = ObjectStreamsDemo.roundTrip(new Order("A-1", new ShippingAddress("Paris")));

            assertThat(copy.getId()).isEqualTo("A-1");
            assertThat(copy.getAddress().getCity()).isEqualTo("Paris");
        }
    }

    // ── 5. Inheritance rules ──────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Inheritance rules")
    class InheritanceRulesTest {

        @Test
        @DisplayName("a subclass of a Serializable class is Serializable and keeps all fields")
        void serializableParent() throws Exception {
            Employee eve = new Employee("Eve", 28, "R&D");

            Employee copy = ObjectStreamsDemo.roundTrip(eve);

            assertThat(eve).isInstanceOf(Serializable.class);
            assertThat(copy.getName()).isEqualTo("Eve");
            assertThat(copy.getAge()).isEqualTo(28);
            assertThat(copy.getDepartment()).isEqualTo("R&D");
        }

        @Test
        @DisplayName("fields of a non-serializable parent are re-initialised by its no-arg constructor")
        void parentFieldReset() throws Exception {
            Dog copy = ObjectStreamsDemo.roundTrip(new Dog("mammal", "Rex"));

            assertThat(copy.getName()).isEqualTo("Rex");
            assertThat(copy.getKind()).isEqualTo("unknown");
        }

        @Test
        @DisplayName("a non-serializable parent without a no-arg constructor serializes but cannot be read")
        void missingParentConstructor() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new BrokenChild(7));

            assertThat(data).isNotEmpty();
            assertThatThrownBy(() -> ObjectStreamsDemo.fromBytes(data))
                    .isInstanceOf(InvalidClassException.class)
                    .hasMessageContaining("no valid constructor");
        }
    }
}
