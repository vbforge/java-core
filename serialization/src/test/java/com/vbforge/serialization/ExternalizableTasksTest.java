package com.vbforge.serialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ExternalizableTasksTest — tests for every method in {@link ExternalizableTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ExternalizableTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ExternalizableTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ExternalizableTasks — your implementations")
class ExternalizableTasksTest {

    private ExternalizableTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ExternalizableTasks();
    }

    // ── Test fixtures (independent of the code you are implementing) ───────────

    public static class WithPublicNoArg {
        public WithPublicNoArg() { }
    }

    public static class OnlyArgCtor {
        public OnlyArgCtor(int value) { }
    }

    public static class HiddenNoArg {
        HiddenNoArg() { }                       // package-private

        public HiddenNoArg(int value) { }
    }

    public static class PrivateNoArg {
        @SuppressWarnings("unused")
        private PrivateNoArg() { }
    }

    /** Externalizable: writes only the three ints. */
    public static class ExternalPoint implements Externalizable {
        private static final long serialVersionUID = 1L;

        private int latitude;
        private int longitude;
        private int altitude;

        public ExternalPoint() { }

        public ExternalPoint(int latitude, int longitude, int altitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }

        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            out.writeInt(latitude);
            out.writeInt(longitude);
            out.writeInt(altitude);
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException {
            latitude = in.readInt();
            longitude = in.readInt();
            altitude = in.readInt();
        }
    }

    /** The same data with default serialization. */
    public static class DefaultPoint implements Serializable {
        private static final long serialVersionUID = 1L;

        private final int latitude;
        private final int longitude;
        private final int altitude;

        public DefaultPoint(int latitude, int longitude, int altitude) {
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
        }
    }

    private static byte[] streamOf(Object obj) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(obj);
        }
        return bytes.toByteArray();
    }

    /** Writes the documented layout (int count, then UTF strings) and an optional trailing UTF string. */
    private static byte[] layoutOf(List<String> items, String trailer) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeInt(items.size());
            for (String item : items) {
                out.writeUTF(item);
            }
            if (trailer != null) {
                out.writeUTF(trailer);
            }
        }
        return bytes.toByteArray();
    }

    // ── Task 1: serializedSize ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement serializedSize()")
    @DisplayName("Task 1 — serializedSize()")
    class SerializedSizeTest {

        @Test
        @DisplayName("equals the length of the serialized bytes")
        void matchesStreamLength() throws Exception {
            assertThat(tasks.serializedSize("hello")).isEqualTo(streamOf("hello").length);
        }

        @Test
        @DisplayName("works for objects of a custom class")
        void customClass() throws Exception {
            DefaultPoint point = new DefaultPoint(1, 2, 3);

            assertThat(tasks.serializedSize(point)).isEqualTo(streamOf(point).length);
        }

        @Test
        @DisplayName("a longer string has a bigger serialized size")
        void longerIsBigger() throws Exception {
            String longText = String.join("", Collections.nCopies(100, "a"));

            assertThat(tasks.serializedSize(longText)).isGreaterThan(tasks.serializedSize("a"));
        }

        @Test
        @DisplayName("an Externalizable object is smaller than the same data in default Serializable form")
        void externalizableIsSmaller() throws Exception {
            int external = tasks.serializedSize(new ExternalPoint(50, 30, 179));
            int standard = tasks.serializedSize(new DefaultPoint(50, 30, 179));

            assertThat(external).isLessThan(standard);
        }
    }

    // ── Task 2: hasPublicNoArgConstructor ─────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement hasPublicNoArgConstructor()")
    @DisplayName("Task 2 — hasPublicNoArgConstructor()")
    class HasPublicNoArgConstructorTest {

        @Test
        @DisplayName("public no-arg constructor → true")
        void publicNoArg() {
            assertThat(tasks.hasPublicNoArgConstructor(WithPublicNoArg.class)).isTrue();
            assertThat(tasks.hasPublicNoArgConstructor(String.class)).isTrue();
        }

        @Test
        @DisplayName("only constructors with parameters → false")
        void onlyArgConstructor() {
            assertThat(tasks.hasPublicNoArgConstructor(OnlyArgCtor.class)).isFalse();
            assertThat(tasks.hasPublicNoArgConstructor(Integer.class)).isFalse();
        }

        @Test
        @DisplayName("package-private no-arg constructor → false")
        void packagePrivateNoArg() {
            assertThat(tasks.hasPublicNoArgConstructor(HiddenNoArg.class)).isFalse();
        }

        @Test
        @DisplayName("private no-arg constructor → false")
        void privateNoArg() {
            assertThat(tasks.hasPublicNoArgConstructor(PrivateNoArg.class)).isFalse();
        }
    }

    // ── Task 3: serializationKind ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement serializationKind()")
    @DisplayName("Task 3 — serializationKind()")
    class SerializationKindTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "java.lang.String,    SERIALIZABLE",
                "java.util.ArrayList, SERIALIZABLE",
                "java.lang.Integer,   SERIALIZABLE",
                "java.lang.Object,    NONE",
                "java.lang.Thread,    NONE"
        })
        @DisplayName("classification of JDK classes")
        void jdkClasses(String className, String expected) throws Exception {
            assertThat(tasks.serializationKind(Class.forName(className))).isEqualTo(expected);
        }

        @Test
        @DisplayName("Externalizable class → EXTERNALIZABLE (even though it is also Serializable)")
        void externalizable() {
            assertThat(Serializable.class.isAssignableFrom(ExternalPoint.class)).isTrue();
            assertThat(tasks.serializationKind(ExternalPoint.class)).isEqualTo("EXTERNALIZABLE");
        }

        @Test
        @DisplayName("class with plain Serializable → SERIALIZABLE")
        void plainSerializable() {
            assertThat(tasks.serializationKind(DefaultPoint.class)).isEqualTo("SERIALIZABLE");
        }

        @Test
        @DisplayName("class implementing neither → NONE")
        void neither() {
            assertThat(tasks.serializationKind(WithPublicNoArg.class)).isEqualTo("NONE");
        }
    }

    // ── Task 4: writeStringList ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement writeStringList()")
    @DisplayName("Task 4 — writeStringList()")
    class WriteStringListTest {

        private ObjectInputStream written(List<String> items) throws Exception {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bytes);
            tasks.writeStringList(items, out);
            out.close();
            return new ObjectInputStream(new ByteArrayInputStream(bytes.toByteArray()));
        }

        @Test
        @DisplayName("writes the count first, then every string")
        void countThenItems() throws Exception {
            try (ObjectInputStream in = written(Arrays.asList("a", "b"))) {
                assertThat(in.readInt()).isEqualTo(2);
                assertThat(in.readUTF()).isEqualTo("a");
                assertThat(in.readUTF()).isEqualTo("b");
            }
        }

        @Test
        @DisplayName("empty list writes only the count 0")
        void emptyList() throws Exception {
            try (ObjectInputStream in = written(Collections.<String>emptyList())) {
                assertThat(in.readInt()).isZero();
                assertThat(in.read()).isEqualTo(-1);   // nothing else in the stream
            }
        }

        @Test
        @DisplayName("keeps list order and writes nothing extra")
        void orderAndNothingElse() throws Exception {
            try (ObjectInputStream in = written(Arrays.asList("z", "m", "a"))) {
                assertThat(in.readInt()).isEqualTo(3);
                assertThat(in.readUTF()).isEqualTo("z");
                assertThat(in.readUTF()).isEqualTo("m");
                assertThat(in.readUTF()).isEqualTo("a");
                assertThat(in.read()).isEqualTo(-1);
            }
        }

        @Test
        @DisplayName("non-ASCII text survives")
        void unicode() throws Exception {
            try (ObjectInputStream in = written(Arrays.asList("héllo"))) {
                assertThat(in.readInt()).isEqualTo(1);
                assertThat(in.readUTF()).isEqualTo("héllo");
            }
        }
    }

    // ── Task 5: readStringList ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement readStringList()")
    @DisplayName("Task 5 — readStringList()")
    class ReadStringListTest {

        @Test
        @DisplayName("reads the count and then every string")
        void readsItems() throws Exception {
            try (ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(layoutOf(Arrays.asList("a", "b"), null)))) {
                assertThat(tasks.readStringList(in)).containsExactly("a", "b");
            }
        }

        @Test
        @DisplayName("count 0 gives an empty list")
        void emptyList() throws Exception {
            try (ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(layoutOf(Collections.<String>emptyList(), null)))) {
                assertThat(tasks.readStringList(in)).isEmpty();
            }
        }

        @Test
        @DisplayName("reads exactly the list — the following data is untouched")
        void leavesTrailingData() throws Exception {
            try (ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(layoutOf(Arrays.asList("x"), "y")))) {
                assertThat(tasks.readStringList(in)).containsExactly("x");
                assertThat(in.readUTF()).isEqualTo("y");
            }
        }

        @Test
        @DisplayName("preserves order and non-ASCII text")
        void orderAndUnicode() throws Exception {
            List<String> items = Arrays.asList("héllo", "wörld", "!");

            try (ObjectInputStream in = new ObjectInputStream(
                    new ByteArrayInputStream(layoutOf(items, null)))) {
                assertThat(tasks.readStringList(in)).containsExactlyElementsOf(items);
            }
        }
    }
}
