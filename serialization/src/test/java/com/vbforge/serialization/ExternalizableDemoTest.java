package com.vbforge.serialization;

import com.vbforge.serialization.ExternalizableDemo.ExternalizableGeoPoint;
import com.vbforge.serialization.ExternalizableDemo.HiddenCtorExternal;
import com.vbforge.serialization.ExternalizableDemo.NoDefaultCtorExternal;
import com.vbforge.serialization.ExternalizableDemo.SerializableGeoPoint;
import com.vbforge.serialization.ExternalizableDemo.UserProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Externalizable;
import java.io.InvalidClassException;
import java.io.Serializable;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ExternalizableDemoTest — verifies every concept shown in {@link ExternalizableDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * {@link Externalizable}.
 */
@DisplayName("ExternalizableDemo — API contracts")
class ExternalizableDemoTest {

    // ── 1. Externalizable basics ──────────────────────────────────────────────

    @Nested
    @DisplayName("1. Externalizable basics")
    class ExternalizableBasicsTest {

        @Test
        @DisplayName("Externalizable extends Serializable")
        void extendsSerializable() {
            assertThat(Serializable.class.isAssignableFrom(Externalizable.class)).isTrue();
            assertThat(new ExternalizableGeoPoint()).isInstanceOf(Serializable.class);
        }

        @Test
        @DisplayName("fields written by writeExternal are restored by readExternal")
        void writtenFieldsRestored() throws Exception {
            ExternalizableGeoPoint copy =
                    ObjectStreamsDemo.roundTrip(new ExternalizableGeoPoint(50, 30, 179, "original-label"));

            assertThat(copy.getLatitude()).isEqualTo(50);
            assertThat(copy.getLongitude()).isEqualTo(30);
            assertThat(copy.getAltitude()).isEqualTo(179);
        }

        @Test
        @DisplayName("a field that is not written gets its value from the no-arg constructor")
        void unwrittenFieldFromConstructor() throws Exception {
            ExternalizableGeoPoint copy =
                    ObjectStreamsDemo.roundTrip(new ExternalizableGeoPoint(50, 30, 179, "original-label"));

            assertThat(copy.getLabel()).isEqualTo(ExternalizableGeoPoint.DEFAULT_LABEL);
        }

        @Test
        @DisplayName("with plain Serializable no constructor runs, so a skipped field is null")
        void serializableSkipsConstructor() throws Exception {
            SerializableGeoPoint copy =
                    ObjectStreamsDemo.roundTrip(new SerializableGeoPoint(50, 30, 179, "original-label"));

            assertThat(copy.getLatitude()).isEqualTo(50);
            assertThat(copy.getLabel()).isNull();
        }
    }

    // ── 2. Stream size ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Stream size")
    class StreamSizeTest {

        @Test
        @DisplayName("Externalizable stream is smaller than the default Serializable one for the same data")
        void externalizableIsSmaller() throws Exception {
            int externalSize = ObjectStreamsDemo.toBytes(new ExternalizableGeoPoint(50, 30, 179, "x")).length;
            int defaultSize = ObjectStreamsDemo.toBytes(new SerializableGeoPoint(50, 30, 179, "x")).length;

            assertThat(externalSize).isLessThan(defaultSize);
        }

        @Test
        @DisplayName("the label is not written, so it does not influence the Externalizable size")
        void unwrittenFieldNotInStream() throws Exception {
            int shortLabel = ObjectStreamsDemo.toBytes(new ExternalizableGeoPoint(1, 2, 3, "x")).length;
            int longLabel = ObjectStreamsDemo.toBytes(new ExternalizableGeoPoint(1, 2, 3, "a-much-longer-label")).length;

            assertThat(longLabel).isEqualTo(shortLabel);
        }
    }

    // ── 3. The public no-arg constructor rule ─────────────────────────────────

    @Nested
    @DisplayName("3. Public no-arg constructor rule")
    class NoArgConstructorRuleTest {

        @Test
        @DisplayName("without any no-arg constructor, writing works but reading fails")
        void noNoArgConstructor() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new NoDefaultCtorExternal(1));

            assertThat(data).isNotEmpty();
            assertThatThrownBy(() -> ObjectStreamsDemo.fromBytes(data))
                    .isInstanceOf(InvalidClassException.class)
                    .hasMessageContaining("no valid constructor");
        }

        @Test
        @DisplayName("a package-private no-arg constructor is not enough")
        void nonPublicNoArgConstructor() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new HiddenCtorExternal(2));

            assertThat(data).isNotEmpty();
            assertThatThrownBy(() -> ObjectStreamsDemo.fromBytes(data))
                    .isInstanceOf(InvalidClassException.class)
                    .hasMessageContaining("no valid constructor");
        }
    }

    // ── 4. Custom format and versioning ───────────────────────────────────────

    @Nested
    @DisplayName("4. Custom format and versioning")
    class VersioningTest {

        @Test
        @DisplayName("current format round-trips every field")
        void currentFormat() throws Exception {
            UserProfile copy = ObjectStreamsDemo.roundTrip(new UserProfile("alice", "alice@example.com"));

            assertThat(copy.getName()).isEqualTo("alice");
            assertThat(copy.getEmail()).isEqualTo("alice@example.com");
        }

        @Test
        @DisplayName("an old version-1 stream is still readable; the new field gets a default")
        void legacyFormatStillReadable() throws Exception {
            UserProfile copy = (UserProfile) ObjectStreamsDemo.fromBytes(
                    ObjectStreamsDemo.toBytes(UserProfile.legacyV1("bob")));

            assertThat(copy.getName()).isEqualTo("bob");
            assertThat(copy.getEmail()).isEqualTo(UserProfile.UNKNOWN_EMAIL);
        }

        @Test
        @DisplayName("the version-1 layout is shorter than the current one")
        void legacyIsShorter() throws Exception {
            int legacy = ObjectStreamsDemo.toBytes(UserProfile.legacyV1("bob")).length;
            int current = ObjectStreamsDemo.toBytes(new UserProfile("bob", "bob@example.com")).length;

            assertThat(legacy).isLessThan(current);
        }
    }
}
