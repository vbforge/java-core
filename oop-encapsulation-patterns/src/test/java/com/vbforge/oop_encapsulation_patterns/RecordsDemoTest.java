package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Circle;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Point;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.PointClass;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.PointRecord;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.RawSamples;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Range;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Rectangle;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.SafeTeam;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Samples;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Shape;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Team;
import com.vbforge.oop_encapsulation_patterns.RecordsDemo.Username;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

/**
 * RecordsDemoTest — verifies every concept shown in {@link RecordsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how records behave.
 */
@DisplayName("RecordsDemo — API contracts")
class RecordsDemoTest {

    // ── 1. Record basics ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Record basics")
    class BasicsTest {

        @Test
        @DisplayName("accessors are named after the components, without get")
        void accessors() {
            Point p = new Point(1, 2);
            assertThat(p.x()).isEqualTo(1);
            assertThat(p.y()).isEqualTo(2);
        }

        @Test
        @DisplayName("toString lists the components")
        void toStringFormat() {
            assertThat(new Point(1, 2).toString()).isEqualTo("Point[x=1, y=2]");
        }

        @Test
        @DisplayName("equals and hashCode are based on all components")
        void valueEquality() {
            assertThat(new Point(1, 2)).isEqualTo(new Point(1, 2));
            assertThat(new Point(1, 2).hashCode()).isEqualTo(new Point(1, 2).hashCode());
            assertThat(new Point(1, 2)).isNotEqualTo(new Point(2, 1));
        }

        @Test
        @DisplayName("a record works as a hash-set element")
        void inHashSet() {
            Set<Point> set = new HashSet<>();
            set.add(new Point(1, 2));
            assertThat(set).contains(new Point(1, 2));
        }

        @Test
        @DisplayName("the fields are private and final")
        void fieldsPrivateFinal() throws Exception {
            int modifiers = Point.class.getDeclaredField("x").getModifiers();
            assertThat(Modifier.isPrivate(modifiers)).isTrue();
            assertThat(Modifier.isFinal(modifiers)).isTrue();
        }
    }

    // ── 2. The compact constructor ────────────────────────────────────────────

    @Nested
    @DisplayName("2. The compact constructor")
    class CompactConstructorTest {

        @Test
        @DisplayName("validation rejects an invalid range")
        void validates() {
            assertThatThrownBy(() -> new Range(5, 1))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("from must not exceed to");
        }

        @Test
        @DisplayName("a valid range works, and an empty one (from == to) is allowed")
        void validRange() {
            assertThat(new Range(2, 7).length()).isEqualTo(5);
            assertThat(new Range(3, 3).length()).isZero();
        }

        @Test
        @DisplayName("the constructor can normalise: trim and lower-case")
        void normalises() {
            assertThat(new Username("  BoB ").value()).isEqualTo("bob");
        }

        @Test
        @DisplayName("a blank username is rejected after normalisation")
        void rejectsBlank() {
            assertThatThrownBy(() -> new Username("   ")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a null username is rejected")
        void rejectsNull() {
            assertThatThrownBy(() -> new Username(null)).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("equality is decided by the normalised value")
        void equalityAfterNormalisation() {
            assertThat(new Username("BOB")).isEqualTo(new Username(" bob "));
        }
    }

    // ── 3. Shallow immutability ───────────────────────────────────────────────

    @Nested
    @DisplayName("3. Shallow immutability")
    class ShallowImmutabilityTest {

        @Test
        @DisplayName("a record holding a mutable list follows later changes to that list")
        void leakyRecord() {
            List<String> source = new ArrayList<>(Arrays.asList("ann"));
            Team team = new Team("A", source);
            source.add("bob");
            assertThat(team.members()).containsExactly("ann", "bob");
        }

        @Test
        @DisplayName("a defensive copy in the compact constructor protects the record")
        void safeRecord() {
            List<String> source = new ArrayList<>(Arrays.asList("ann"));
            SafeTeam team = new SafeTeam("A", source);
            source.add("bob");
            assertThat(team.members()).containsExactly("ann");
            assertThatThrownBy(() -> team.members().add("x")).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("the generated equals compares array components by identity")
        void arrayEqualsByIdentity() {
            int[] shared = {1};
            assertThat(new RawSamples(new int[] {1})).isNotEqualTo(new RawSamples(new int[] {1}));
            assertThat(new RawSamples(shared)).isEqualTo(new RawSamples(shared));
        }

        @Test
        @DisplayName("overriding equals/hashCode/toString compares arrays by content")
        void arrayEqualsByContent() {
            assertThat(new Samples(new int[] {1, 2})).isEqualTo(new Samples(new int[] {1, 2}));
            assertThat(new Samples(new int[] {1, 2}).hashCode()).isEqualTo(new Samples(new int[] {1, 2}).hashCode());
            assertThat(new Samples(new int[] {1, 2})).isNotEqualTo(new Samples(new int[] {2, 1}));
            assertThat(new Samples(new int[] {1, 2}).toString()).isEqualTo("Samples[1, 2]");
        }

        @Test
        @DisplayName("copy in and copy out protect the array")
        void copyInCopyOut() {
            int[] input = {1, 2};
            Samples samples = new Samples(input);
            input[0] = 99;
            samples.values()[1] = 99;
            assertThat(samples.values()).containsExactly(1, 2);
        }
    }

    // ── 4. Extra members ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Extra members")
    class ExtraMembersTest {

        @Test
        @DisplayName("an instance method uses the components")
        void instanceMethod() {
            assertThat(new Rectangle(3, 4).area()).isEqualTo(12);
        }

        @Test
        @DisplayName("a static factory builds a record")
        void staticFactory() {
            assertThat(Rectangle.square(5)).isEqualTo(new Rectangle(5, 5));
        }

        @Test
        @DisplayName("an extra constructor delegates to the canonical one")
        void delegatingConstructor() {
            assertThat(new Rectangle(7)).isEqualTo(new Rectangle(7, 7));
        }

        @Test
        @DisplayName("a wither returns a new record and leaves the original untouched")
        void wither() {
            Rectangle original = new Rectangle(3, 4);
            Rectangle changed = original.withWidth(10);
            assertThat(changed).isEqualTo(new Rectangle(10, 4)).isNotSameAs(original);
            assertThat(original).isEqualTo(new Rectangle(3, 4));
        }

        @Test
        @DisplayName("a static field is allowed")
        void staticField() {
            assertThat(Rectangle.UNIT).isEqualTo(new Rectangle(1, 1));
        }

        @Test
        @DisplayName("the only instance fields are the components")
        void onlyComponentFields() {
            long instanceFields = Arrays.stream(Rectangle.class.getDeclaredFields())
                    .filter(f -> !Modifier.isStatic(f.getModifiers()))
                    .count();
            assertThat(instanceFields).isEqualTo(2);
        }
    }

    // ── 5. Records, interfaces and reflection ─────────────────────────────────

    @Nested
    @DisplayName("5. Records, interfaces and reflection")
    class InterfacesAndReflectionTest {

        @Test
        @DisplayName("a record can implement an interface")
        void implementsInterface() {
            Shape shape = new Circle(2);
            assertThat(shape.area()).isCloseTo(Math.PI * 4, within(1e-9));
        }

        @Test
        @DisplayName("a record extends java.lang.Record and is final")
        void superclassAndFinal() {
            assertThat(Circle.class.isRecord()).isTrue();
            assertThat(Circle.class.getSuperclass()).isEqualTo(Record.class);
            assertThat(Modifier.isFinal(Circle.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("component names come in declaration order")
        void componentNames() {
            assertThat(RecordsDemo.componentNames(Rectangle.class)).containsExactly("width", "height");
        }

        @Test
        @DisplayName("a non-record has no components")
        void nonRecord() {
            assertThat(RecordsDemo.componentNames(String.class)).isEmpty();
            assertThat(Circle.class.isInterface()).isFalse();
            assertThat(String.class.isRecord()).isFalse();
        }
    }

    // ── 6. Record vs hand-written class ───────────────────────────────────────

    @Nested
    @DisplayName("6. Record vs hand-written class")
    class RecordVsClassTest {

        @Test
        @DisplayName("both have value-based equality")
        void equality() {
            assertThat(new PointRecord(1, 2)).isEqualTo(new PointRecord(1, 2));
            assertThat(new PointClass(1, 2)).isEqualTo(new PointClass(1, 2));
            assertThat(new PointRecord(1, 2)).isNotEqualTo(new PointRecord(1, 3));
            assertThat(new PointClass(1, 2)).isNotEqualTo(new PointClass(1, 3));
        }

        @Test
        @DisplayName("both have the same accessors")
        void accessors() {
            assertThat(new PointRecord(1, 2).x()).isEqualTo(new PointClass(1, 2).x());
            assertThat(new PointRecord(1, 2).y()).isEqualTo(new PointClass(1, 2).y());
        }

        @Test
        @DisplayName("both print their components")
        void toStrings() {
            assertThat(new PointRecord(1, 2).toString()).isEqualTo("PointRecord[x=1, y=2]");
            assertThat(new PointClass(1, 2).toString()).isEqualTo("PointClass[x=1, y=2]");
        }

        @Test
        @DisplayName("equal objects have equal hash codes in both")
        void hashCodes() {
            assertThat(new PointRecord(1, 2).hashCode()).isEqualTo(new PointRecord(1, 2).hashCode());
            assertThat(new PointClass(1, 2).hashCode()).isEqualTo(new PointClass(1, 2).hashCode());
        }
    }
}
