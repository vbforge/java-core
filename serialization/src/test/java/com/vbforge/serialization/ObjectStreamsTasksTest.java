package com.vbforge.serialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.NotSerializableException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ObjectStreamsTasksTest — tests for every method in {@link ObjectStreamsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ObjectStreamsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ObjectStreamsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ObjectStreamsTasks — your implementations")
class ObjectStreamsTasksTest {

    private ObjectStreamsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ObjectStreamsTasks();
    }

    // ── Test fixtures (independent of the code you are implementing) ───────────

    /** Small serializable value class. */
    static class Point implements Serializable {
        private static final long serialVersionUID = 1L;

        final int x;
        final int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof Point)) return false;
            Point other = (Point) o;
            return x == other.x && y == other.y;
        }

        @Override
        public int hashCode() { return Objects.hash(x, y); }

        @Override
        public String toString() { return "Point(" + x + "," + y + ")"; }
    }

    /** Holds a list of points and a reference to the first one (shared reference). */
    static class Box implements Serializable {
        private static final long serialVersionUID = 1L;

        final List<Point> points = new ArrayList<>();
        Point first;
    }

    /** Builds a stream the straightforward way, so tests do not depend on your implementation. */
    private static byte[] streamOf(Object... objects) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            for (Object o : objects) {
                out.writeObject(o);
            }
        }
        return bytes.toByteArray();
    }

    /** Reads {@code count} objects from a stream. */
    private static List<Object> readExactly(byte[] data, int count) throws Exception {
        List<Object> result = new ArrayList<>();
        try (ObjectInputStream in = new ObjectInputStream(new ByteArrayInputStream(data))) {
            for (int i = 0; i < count; i++) {
                result.add(in.readObject());
            }
        }
        return result;
    }

    // ── Task 1: serialize ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement serialize()")
    @DisplayName("Task 1 — serialize()")
    class SerializeTest {

        @Test
        @DisplayName("result starts with the stream magic number 0xACED")
        void startsWithMagic() throws Exception {
            byte[] data = tasks.serialize("hi");

            assertThat(data[0] & 0xFF).isEqualTo(0xAC);
            assertThat(data[1] & 0xFF).isEqualTo(0xED);
        }

        @Test
        @DisplayName("bytes can be read back by a plain ObjectInputStream")
        void readableByObjectInputStream() throws Exception {
            byte[] data = tasks.serialize(new Point(1, 2));

            assertThat(readExactly(data, 1)).containsExactly(new Point(1, 2));
        }

        @Test
        @DisplayName("each call returns a new array")
        void newArrayEachTime() throws Exception {
            Point p = new Point(1, 2);

            byte[] first = tasks.serialize(p);
            byte[] second = tasks.serialize(p);

            assertThat(first).isNotSameAs(second);
            assertThat(first).isEqualTo(second);
        }

        @Test
        @DisplayName("a non-serializable element makes it throw NotSerializableException")
        void notSerializableElement() {
            ArrayList<Object> bad = new ArrayList<>(Arrays.asList("ok", new Object()));

            assertThatThrownBy(() -> tasks.serialize(bad))
                    .isInstanceOf(NotSerializableException.class);
        }
    }

    // ── Task 2: deserialize ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement deserialize()")
    @DisplayName("Task 2 — deserialize()")
    class DeserializeTest {

        @Test
        @DisplayName("reads a String")
        void readsString() throws Exception {
            assertThat(tasks.deserialize(streamOf("hello"))).isEqualTo("hello");
        }

        @Test
        @DisplayName("reads an object as a NEW equal instance")
        void readsObject() throws Exception {
            Point original = new Point(3, 4);

            Object result = tasks.deserialize(streamOf(original));

            assertThat(result).isEqualTo(new Point(3, 4));
            assertThat(result).isNotSameAs(original);
        }

        @Test
        @DisplayName("returns only the first object of a multi-object stream")
        void onlyFirstObject() throws Exception {
            assertThat(tasks.deserialize(streamOf("first", "second"))).isEqualTo("first");
        }

        @Test
        @DisplayName("garbage bytes throw StreamCorruptedException")
        void garbage() {
            assertThatThrownBy(() -> tasks.deserialize(new byte[]{1, 2, 3, 4}))
                    .isInstanceOf(StreamCorruptedException.class);
        }
    }

    // ── Task 3: deepCopy ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement deepCopy()")
    @DisplayName("Task 3 — deepCopy()")
    class DeepCopyTest {

        @Test
        @DisplayName("copy is equal to the original but not the same instance")
        void equalButNotSame() throws Exception {
            ArrayList<String> original = new ArrayList<>(Arrays.asList("a", "b"));

            ArrayList<String> copy = tasks.deepCopy(original);

            assertThat(copy).containsExactly("a", "b");
            assertThat(copy).isNotSameAs(original);
        }

        @Test
        @DisplayName("nested objects are copied too (deep, not shallow)")
        void nestedObjectsCopied() throws Exception {
            Box box = new Box();
            box.points.add(new Point(1, 1));

            Box copy = tasks.deepCopy(box);
            copy.points.add(new Point(2, 2));

            assertThat(box.points).hasSize(1);
            assertThat(copy.points.get(0)).isNotSameAs(box.points.get(0));
            assertThat(copy.points.get(0)).isEqualTo(box.points.get(0));
        }

        @Test
        @DisplayName("shared references inside the graph stay shared in the copy")
        void sharedReferencesPreserved() throws Exception {
            Box box = new Box();
            Point shared = new Point(5, 5);
            box.points.add(shared);
            box.first = shared;

            Box copy = tasks.deepCopy(box);

            assertThat(copy.first).isSameAs(copy.points.get(0));
            assertThat(copy.first).isNotSameAs(shared);
        }

        @Test
        @DisplayName("empty collection is copied as an empty collection")
        void emptyList() throws Exception {
            ArrayList<String> copy = tasks.deepCopy(new ArrayList<String>());

            assertThat(copy).isEmpty();
        }

        @Test
        @DisplayName("a non-serializable element throws NotSerializableException")
        void notSerializableElement() {
            ArrayList<Object> bad = new ArrayList<>(Arrays.asList("ok", new Object()));

            assertThatThrownBy(() -> tasks.deepCopy(bad))
                    .isInstanceOf(NotSerializableException.class);
        }
    }

    // ── Task 4: isSerializable ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isSerializable()")
    @DisplayName("Task 4 — isSerializable()")
    class IsSerializableTest {

        @Test
        @DisplayName("String and Point are serializable")
        void serializableObjects() throws Exception {
            assertThat(tasks.isSerializable("text")).isTrue();
            assertThat(tasks.isSerializable(new Point(1, 2))).isTrue();
        }

        @Test
        @DisplayName("a plain Object is not serializable")
        void plainObject() throws Exception {
            assertThat(tasks.isSerializable(new Object())).isFalse();
        }

        @Test
        @DisplayName("list of strings is serializable")
        void listOfStrings() throws Exception {
            assertThat(tasks.isSerializable(new ArrayList<>(Arrays.asList("a", "b")))).isTrue();
        }

        @Test
        @DisplayName("list containing a non-serializable element is NOT serializable, although the list is")
        void listWithBadElement() throws Exception {
            List<Object> bad = new ArrayList<>(Arrays.asList("a", new Object()));

            assertThat(bad).isInstanceOf(Serializable.class);
            assertThat(tasks.isSerializable(bad)).isFalse();
        }

        @Test
        @DisplayName("null counts as serializable")
        void nullIsSerializable() throws Exception {
            assertThat(tasks.isSerializable(null)).isTrue();
        }
    }

    // ── Task 5: serializeAll ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement serializeAll()")
    @DisplayName("Task 5 — serializeAll()")
    class SerializeAllTest {

        @Test
        @DisplayName("objects are written in list order into one stream")
        void writesInOrder() throws Exception {
            byte[] data = tasks.serializeAll(Arrays.asList("a", "b", "c"));

            assertThat(readExactly(data, 3)).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("empty list gives a valid stream with no objects")
        void emptyList() throws Exception {
            byte[] data = tasks.serializeAll(Collections.<Serializable>emptyList());

            assertThat(data[0] & 0xFF).isEqualTo(0xAC);
            assertThat(readExactly(data, 0)).isEmpty();
        }

        @Test
        @DisplayName("the same instance twice is read back as one shared instance")
        void sameInstanceTwice() throws Exception {
            Point p = new Point(1, 2);

            List<Object> result = readExactly(tasks.serializeAll(Arrays.asList(p, p)), 2);

            assertThat(result.get(0)).isEqualTo(new Point(1, 2));
            assertThat(result.get(0)).isSameAs(result.get(1));
        }

        @Test
        @DisplayName("mixed element types keep their order")
        void mixedTypes() throws Exception {
            List<Serializable> items = Arrays.<Serializable>asList("a", 42, new Point(3, 4));

            assertThat(readExactly(tasks.serializeAll(items), 3))
                    .containsExactly("a", 42, new Point(3, 4));
        }

        @Test
        @DisplayName("a non-serializable element throws NotSerializableException")
        void notSerializableElement() {
            List<Serializable> items = Arrays.<Serializable>asList("ok", new ArrayList<Object>(Arrays.asList(new Object())));

            assertThatThrownBy(() -> tasks.serializeAll(items))
                    .isInstanceOf(NotSerializableException.class);
        }
    }

    // ── Task 6: deserializeAll ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement deserializeAll()")
    @DisplayName("Task 6 — deserializeAll()")
    class DeserializeAllTest {

        @Test
        @DisplayName("reads every object in stream order")
        void readsAllInOrder() throws Exception {
            List<Object> result = tasks.deserializeAll(streamOf("a", 42, new Point(1, 2)));

            assertThat(result).containsExactly("a", 42, new Point(1, 2));
        }

        @Test
        @DisplayName("stream with no objects gives an empty list")
        void emptyStream() throws Exception {
            assertThat(tasks.deserializeAll(streamOf())).isEmpty();
        }

        @Test
        @DisplayName("single object stream gives a one-element list")
        void singleObject() throws Exception {
            assertThat(tasks.deserializeAll(streamOf("only"))).containsExactly("only");
        }

        @Test
        @DisplayName("the same instance written twice is read back as one shared instance")
        void sharedInstance() throws Exception {
            Point p = new Point(7, 8);

            List<Object> result = tasks.deserializeAll(streamOf(p, p));

            assertThat(result).hasSize(2);
            assertThat(result.get(0)).isSameAs(result.get(1));
        }

        @Test
        @DisplayName("garbage bytes throw StreamCorruptedException")
        void garbage() {
            assertThatThrownBy(() -> tasks.deserializeAll(new byte[]{1, 2, 3, 4}))
                    .isInstanceOf(StreamCorruptedException.class);
        }
    }
}
