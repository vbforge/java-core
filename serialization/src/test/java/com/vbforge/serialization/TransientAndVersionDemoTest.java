package com.vbforge.serialization;

import com.vbforge.serialization.TransientAndVersionDemo.Account;
import com.vbforge.serialization.TransientAndVersionDemo.BrokenRegistry;
import com.vbforge.serialization.TransientAndVersionDemo.Circle;
import com.vbforge.serialization.TransientAndVersionDemo.Level;
import com.vbforge.serialization.TransientAndVersionDemo.NonFinalVersion;
import com.vbforge.serialization.TransientAndVersionDemo.NonPrivateHook;
import com.vbforge.serialization.TransientAndVersionDemo.PlainCircle;
import com.vbforge.serialization.TransientAndVersionDemo.Registry;
import com.vbforge.serialization.TransientAndVersionDemo.Report;
import com.vbforge.serialization.TransientAndVersionDemo.Settings;
import com.vbforge.serialization.TransientAndVersionDemo.Unversioned;
import com.vbforge.serialization.TransientAndVersionDemo.User;
import com.vbforge.serialization.TransientAndVersionDemo.Versioned;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.InvalidClassException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TransientAndVersionDemoTest — verifies every concept shown in {@link TransientAndVersionDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * {@code transient}, {@code serialVersionUID}, the serialization hooks and {@code readResolve}.
 */
@DisplayName("TransientAndVersionDemo — API contracts")
class TransientAndVersionDemoTest {

    // ── 1. transient ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. transient")
    class TransientTest {

        @Test
        @DisplayName("the transient modifier is visible through reflection")
        void modifierVisible() throws Exception {
            assertThat(Modifier.isTransient(User.class.getDeclaredField("password").getModifiers())).isTrue();
            assertThat(Modifier.isTransient(User.class.getDeclaredField("username").getModifiers())).isFalse();
        }

        @Test
        @DisplayName("transient fields come back as null / 0 / false; others are kept")
        void transientFieldsAreDefaults() throws Exception {
            User copy = ObjectStreamsDemo.roundTrip(new User("alice", "s3cret", 2, true));

            assertThat(copy.getUsername()).isEqualTo("alice");
            assertThat(copy.getPassword()).isNull();
            assertThat(copy.getLoginAttempts()).isZero();
            assertThat(copy.isAuthenticated()).isFalse();
        }

        @Test
        @DisplayName("the transient value never appears in the stream")
        void secretNotInStream() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new User("alice", "s3cret", 2, true));
            String text = new String(data, StandardCharsets.ISO_8859_1);

            assertThat(text).contains("alice").doesNotContain("s3cret");
        }

        @Test
        @DisplayName("static fields are not serialized — you see the current value after reading")
        void staticFieldNotSerialized() throws Exception {
            String previous = Settings.theme;
            try {
                Settings.theme = "light";
                byte[] data = ObjectStreamsDemo.toBytes(new Settings("alice"));
                Settings.theme = "dark";

                Settings restored = (Settings) ObjectStreamsDemo.fromBytes(data);

                assertThat(restored.getOwner()).isEqualTo("alice");
                assertThat(Settings.theme).isEqualTo("dark");
            } finally {
                Settings.theme = previous;
            }
        }

        @Test
        @DisplayName("a transient cache is empty after reading but can be recomputed")
        void transientCacheRecomputed() throws Exception {
            Report report = new Report(Arrays.asList(1, 2, 3, 4));
            report.sum();
            assertThat(report.isCached()).isTrue();

            Report copy = ObjectStreamsDemo.roundTrip(report);

            assertThat(copy.isCached()).isFalse();
            assertThat(copy.sum()).isEqualTo(10);
            assertThat(copy.isCached()).isTrue();
        }
    }

    // ── 2. serialVersionUID ───────────────────────────────────────────────────

    @Nested
    @DisplayName("2. serialVersionUID")
    class SerialVersionUidTest {

        @Test
        @DisplayName("a declared serialVersionUID is the one the JVM uses")
        void declaredUid() {
            assertThat(ObjectStreamClass.lookup(Versioned.class).getSerialVersionUID()).isEqualTo(42L);
        }

        @Test
        @DisplayName("without a declaration the JVM computes a stable value")
        void computedUid() {
            long first = ObjectStreamClass.lookup(Unversioned.class).getSerialVersionUID();
            long second = ObjectStreamClass.lookup(Unversioned.class).getSerialVersionUID();

            assertThat(first).isNotZero();
            assertThat(first).isEqualTo(second);
        }

        @Test
        @DisplayName("a serialVersionUID that is not final is ignored")
        void nonFinalUidIgnored() {
            assertThat(ObjectStreamClass.lookup(NonFinalVersion.class).getSerialVersionUID()).isNotEqualTo(42L);
        }

        @Test
        @DisplayName("a matching UID reads back normally")
        void matchingUid() throws Exception {
            Versioned copy = ObjectStreamsDemo.roundTrip(new Versioned("payload"));

            assertThat(copy.getData()).isEqualTo("payload");
        }

        @Test
        @DisplayName("a stream written with a different UID is rejected with InvalidClassException")
        void mismatchingUid() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new Versioned("payload"));
            byte[] tampered = TransientAndVersionDemo.patchSerialVersionUID(data, Versioned.UID, Versioned.UID + 1);

            assertThatThrownBy(() -> ObjectStreamsDemo.fromBytes(tampered))
                    .isInstanceOf(InvalidClassException.class)
                    .hasMessageContaining("local class incompatible")
                    .hasMessageContaining("stream classdesc serialVersionUID = 43")
                    .hasMessageContaining("local class serialVersionUID = 42");
        }

        @Test
        @DisplayName("patchSerialVersionUID returns a modified copy and leaves the input untouched")
        void patchReturnsCopy() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new Versioned("payload"));
            byte[] original = data.clone();

            byte[] patched = TransientAndVersionDemo.patchSerialVersionUID(data, Versioned.UID, 7L);

            assertThat(data).isEqualTo(original);
            assertThat(patched).isNotEqualTo(original);
            assertThat(patched).hasSameSizeAs(original);
        }

        @Test
        @DisplayName("patchSerialVersionUID rejects a UID that is not in the stream")
        void patchMissingUid() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new Versioned("payload"));

            assertThatThrownBy(() -> TransientAndVersionDemo.patchSerialVersionUID(data, 123456789L, 1L))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── 3. Custom writeObject / readObject ────────────────────────────────────

    @Nested
    @DisplayName("3. Custom writeObject / readObject")
    class CustomSerializationTest {

        @Test
        @DisplayName("a transient field can be persisted by hand and is restored")
        void transientRestoredByHook() throws Exception {
            Account restored = ObjectStreamsDemo.roundTrip(new Account("alice", "hunter2"));

            assertThat(restored.getOwner()).isEqualTo("alice");
            assertThat(restored.getSecret()).isEqualTo("hunter2");
        }

        @Test
        @DisplayName("the stream holds the obfuscated form, never the plain one")
        void obfuscatedInStream() throws Exception {
            byte[] data = ObjectStreamsDemo.toBytes(new Account("alice", "hunter2"));
            String text = new String(data, StandardCharsets.ISO_8859_1);

            assertThat(text).contains("2retnuh").doesNotContain("hunter2");
        }

        @Test
        @DisplayName("a null transient field survives the custom hooks")
        void nullSecret() throws Exception {
            Account restored = ObjectStreamsDemo.roundTrip(new Account("alice", null));

            assertThat(restored.getSecret()).isNull();
        }

        @Test
        @DisplayName("readObject can rebuild a derived transient field")
        void derivedFieldRebuilt() throws Exception {
            Circle original = new Circle(2.0);
            Circle copy = ObjectStreamsDemo.roundTrip(original);

            assertThat(copy.getRadius()).isEqualTo(2.0);
            assertThat(copy.getArea()).isEqualTo(original.getArea());
            assertThat(copy.getArea()).isGreaterThan(0.0);
        }

        @Test
        @DisplayName("without the hook the constructor-computed transient field is lost")
        void derivedFieldLostWithoutHook() throws Exception {
            PlainCircle original = new PlainCircle(2.0);
            PlainCircle copy = ObjectStreamsDemo.roundTrip(original);

            assertThat(original.getArea()).isGreaterThan(0.0);
            assertThat(copy.getArea()).isZero();
        }

        @Test
        @DisplayName("a public writeObject is ignored — the hook must be private")
        void publicHookIgnored() throws Exception {
            boolean before = NonPrivateHook.hookCalled;
            try {
                NonPrivateHook.hookCalled = false;
                NonPrivateHook copy = ObjectStreamsDemo.roundTrip(new NonPrivateHook("v"));

                assertThat(copy.getValue()).isEqualTo("v");
                assertThat(NonPrivateHook.hookCalled).isFalse();
            } finally {
                NonPrivateHook.hookCalled = before;
            }
        }
    }

    // ── 4. readResolve ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. readResolve")
    class ReadResolveTest {

        @Test
        @DisplayName("readResolve keeps the singleton unique")
        void singletonPreserved() throws Exception {
            assertThat(ObjectStreamsDemo.roundTrip(Registry.INSTANCE)).isSameAs(Registry.INSTANCE);
        }

        @Test
        @DisplayName("without readResolve deserialization creates a second instance")
        void singletonBroken() throws Exception {
            assertThat(ObjectStreamsDemo.roundTrip(BrokenRegistry.INSTANCE)).isNotSameAs(BrokenRegistry.INSTANCE);
        }

        @Test
        @DisplayName("enum constants keep their identity")
        void enumIdentity() throws Exception {
            assertThat(ObjectStreamsDemo.roundTrip(Level.HIGH)).isSameAs(Level.HIGH);
            assertThat(ObjectStreamsDemo.roundTrip(Level.LOW)).isSameAs(Level.LOW);
        }
    }
}
