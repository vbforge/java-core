package com.vbforge.serialization;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TransientAndVersionTasksTest — tests for every method in {@link TransientAndVersionTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code TransientAndVersionTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=TransientAndVersionTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 4 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("TransientAndVersionTasks — your implementations")
class TransientAndVersionTasksTest {

    private TransientAndVersionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new TransientAndVersionTasks();
    }

    // ── Test fixtures ─────────────────────────────────────────────────────────

    /** Two transient instance fields, one static transient field, two persistent fields. */
    @SuppressWarnings("unused")
    static class Sample implements Serializable {
        private static final long serialVersionUID = 7L;
        private static transient int ignoredCounter;       // static → never serialized anyway

        private String name;
        private transient String password;
        private transient int attempts;
        private int visits;

        Sample(String name, String password, int attempts, int visits) {
            this.name = name;
            this.password = password;
            this.attempts = attempts;
            this.visits = visits;
        }
    }

    /** Adds only a non-transient field of its own. */
    @SuppressWarnings("unused")
    static class SubSample extends Sample {
        private static final long serialVersionUID = 8L;

        private String extra;

        SubSample(String name, String password, int attempts, int visits, String extra) {
            super(name, password, attempts, visits);
            this.extra = extra;
        }
    }

    /** No transient fields at all. */
    @SuppressWarnings("unused")
    static class Plain implements Serializable {
        private static final long serialVersionUID = 1L;

        private String a;
        private int b;
    }

    /** Serializable without a declared serialVersionUID. */
    @SuppressWarnings("unused")
    static class NoUid implements Serializable {
        private String data;
    }

    private static byte[] bytesOf(Object obj) throws Exception {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bytes)) {
            out.writeObject(obj);
        }
        return bytes.toByteArray();
    }

    // ── Task 1: transientFieldNames ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement transientFieldNames()")
    @DisplayName("Task 1 — transientFieldNames()")
    class TransientFieldNamesTest {

        @Test
        @DisplayName("returns the transient instance fields, sorted alphabetically")
        void sortedTransientFields() {
            assertThat(tasks.transientFieldNames(Sample.class)).containsExactly("attempts", "password");
        }

        @Test
        @DisplayName("static transient fields are ignored")
        void staticIgnored() {
            assertThat(tasks.transientFieldNames(Sample.class)).doesNotContain("ignoredCounter");
        }

        @Test
        @DisplayName("class without transient fields gives an empty list")
        void noTransientFields() {
            assertThat(tasks.transientFieldNames(Plain.class)).isEmpty();
        }

        @Test
        @DisplayName("inherited fields are not included")
        void inheritedIgnored() {
            assertThat(tasks.transientFieldNames(SubSample.class)).isEmpty();
        }
    }

    // ── Task 2: persistedFieldValues ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement persistedFieldValues()")
    @DisplayName("Task 2 — persistedFieldValues()")
    class PersistedFieldValuesTest {

        @Test
        @DisplayName("contains only the non-static, non-transient fields")
        void onlyPersistedFields() throws Exception {
            Map<String, Object> values = tasks.persistedFieldValues(new Sample("ann", "pw", 3, 5));

            assertThat(values).containsEntry("name", "ann")
                              .containsEntry("visits", 5)
                              .hasSize(2);
        }

        @Test
        @DisplayName("transient and static fields are absent")
        void excludesTransientAndStatic() throws Exception {
            Map<String, Object> values = tasks.persistedFieldValues(new Sample("ann", "pw", 3, 5));

            assertThat(values).doesNotContainKeys("password", "attempts", "ignoredCounter", "serialVersionUID");
        }

        @Test
        @DisplayName("only fields declared in the object's own class are included")
        void superclassFieldsIgnored() throws Exception {
            Map<String, Object> values = tasks.persistedFieldValues(new SubSample("ann", "pw", 3, 5, "extra"));

            assertThat(values).containsEntry("extra", "extra").hasSize(1);
        }

        @Test
        @DisplayName("null field values are kept in the map")
        void nullValuesKept() throws Exception {
            Map<String, Object> values = tasks.persistedFieldValues(new Sample(null, "pw", 3, 5));

            assertThat(values).containsKey("name");
            assertThat(values.get("name")).isNull();
        }
    }

    // ── Task 3: effectiveSerialVersionUID ─────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement effectiveSerialVersionUID()")
    @DisplayName("Task 3 — effectiveSerialVersionUID()")
    class EffectiveSerialVersionUidTest {

        @Test
        @DisplayName("returns the declared value")
        void declaredValue() {
            assertThat(tasks.effectiveSerialVersionUID(Sample.class)).isEqualTo(7L);
            assertThat(tasks.effectiveSerialVersionUID(SubSample.class)).isEqualTo(8L);
        }

        @Test
        @DisplayName("works for JDK classes with a declared value")
        void jdkClass() {
            assertThat(tasks.effectiveSerialVersionUID(String.class)).isEqualTo(-6849794470754667710L);
        }

        @Test
        @DisplayName("returns a non-zero computed value when none is declared")
        void computedValue() {
            long first = tasks.effectiveSerialVersionUID(NoUid.class);

            assertThat(first).isNotZero();
            assertThat(tasks.effectiveSerialVersionUID(NoUid.class)).isEqualTo(first);
        }

        @Test
        @DisplayName("returns 0 for a class that is not serializable")
        void notSerializable() {
            assertThat(tasks.effectiveSerialVersionUID(Object.class)).isZero();
        }
    }

    // ── Task 4: deserializeOrNull ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement deserializeOrNull()")
    @DisplayName("Task 4 — deserializeOrNull()")
    class DeserializeOrNullTest {

        @Test
        @DisplayName("a valid stream gives the restored object")
        void validStream() throws Exception {
            Object result = tasks.deserializeOrNull(bytesOf(new Sample("ann", "pw", 3, 5)));

            assertThat(result).isInstanceOf(Sample.class);
            Sample sample = (Sample) result;
            assertThat(sample.name).isEqualTo("ann");
            assertThat(sample.visits).isEqualTo(5);
        }

        @Test
        @DisplayName("transient fields are defaults in the restored object")
        void transientFieldsDefault() throws Exception {
            Sample sample = (Sample) tasks.deserializeOrNull(bytesOf(new Sample("ann", "pw", 3, 5)));

            assertThat(sample.password).isNull();
            assertThat(sample.attempts).isZero();
        }

        @Test
        @DisplayName("a changed serialVersionUID in the stream gives null")
        void incompatibleVersion() throws Exception {
            byte[] data = bytesOf(new Sample("ann", "pw", 3, 5));
            byte[] tampered = TransientAndVersionDemo.patchSerialVersionUID(data, 7L, 8L);

            assertThat(tasks.deserializeOrNull(tampered)).isNull();
        }

        @Test
        @DisplayName("corrupted data still throws StreamCorruptedException")
        void corruptedData() {
            assertThatThrownBy(() -> tasks.deserializeOrNull(new byte[]{1, 2, 3, 4}))
                    .isInstanceOf(StreamCorruptedException.class);
        }
    }
}
