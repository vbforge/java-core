package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.basics.EmailNotifier;
import com.vbforge.oop_interfaces_and_abstraction.basics.MemoryStore;
import com.vbforge.oop_interfaces_and_abstraction.basics.Notifier;
import com.vbforge.oop_interfaces_and_abstraction.basics.ReadWriteStore;
import com.vbforge.oop_interfaces_and_abstraction.basics.ReadableStore;
import com.vbforge.oop_interfaces_and_abstraction.basics.SmsNotifier;
import com.vbforge.oop_interfaces_and_abstraction.basics.Version;
import com.vbforge.oop_interfaces_and_abstraction.basics.WritableStore;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * InterfaceBasicsDemoTest — verifies every concept shown in {@link InterfaceBasicsDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how interfaces behave.
 */
@DisplayName("InterfaceBasicsDemo — API contracts")
class InterfaceBasicsDemoTest {

    // ── 1. An interface is a contract ─────────────────────────────────────────

    @Nested
    @DisplayName("1. An interface is a contract")
    class ContractTest {

        @Test
        @DisplayName("each implementation answers through the same interface type")
        void polymorphismThroughInterface() {
            assertThat(InterfaceBasicsDemo.broadcast(InterfaceBasicsDemo.sampleNotifiers(), "Build finished"))
                    .containsExactly("EMAIL: Build finished", "SMS: Build finished");
        }

        @Test
        @DisplayName("an interface variable can hold any implementing object")
        void interfaceVariable() {
            Notifier n = new SmsNotifier();
            assertThat(n).isInstanceOf(SmsNotifier.class).isInstanceOf(Notifier.class);
            assertThat(n.send("x")).isEqualTo("SMS: x");
        }

        @Test
        @DisplayName("implementations need not share a class hierarchy (only Object)")
        void unrelatedImplementations() {
            assertThat(EmailNotifier.class.getSuperclass()).isEqualTo(Object.class);
            assertThat(SmsNotifier.class.getSuperclass()).isEqualTo(Object.class);
        }

        @Test
        @DisplayName("an interface type cannot be instantiated")
        void interfaceIsAbstract() {
            assertThat(Notifier.class.isInterface()).isTrue();
            assertThat(java.lang.reflect.Modifier.isAbstract(Notifier.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("broadcast with no notifiers returns no receipts")
        void emptyBroadcast() {
            assertThat(InterfaceBasicsDemo.broadcast(new ArrayList<Notifier>(), "x")).isEmpty();
        }
    }

    // ── 2. Implicit modifiers and constants ───────────────────────────────────

    @Nested
    @DisplayName("2. Implicit modifiers and constants")
    class ImplicitModifiersTest {

        @Test
        @DisplayName("an interface field is implicitly public static final")
        void constantModifiers() {
            assertThat(InterfaceBasicsDemo.constantModifiers()).isEqualTo("public static final");
        }

        @Test
        @DisplayName("an interface method is implicitly public abstract")
        void methodModifiers() {
            assertThat(InterfaceBasicsDemo.sendModifiers()).isEqualTo("public abstract");
        }

        @Test
        @DisplayName("the constant is reachable through the interface name")
        void constantValue() {
            assertThat(Notifier.MAX_LENGTH).isEqualTo(20);
        }

        @Test
        @DisplayName("an implementer uses the constant: SMS cuts long messages")
        void implementerUsesConstant() {
            String longMessage = "This message is far too long for one SMS";
            assertThat(new SmsNotifier().send(longMessage))
                    .isEqualTo("SMS: " + longMessage.substring(0, Notifier.MAX_LENGTH));
        }

        @Test
        @DisplayName("short messages and exactly-MAX_LENGTH messages are not cut")
        void boundary() {
            String exact = "12345678901234567890";                      // 20 characters
            assertThat(new SmsNotifier().send("short")).isEqualTo("SMS: short");
            assertThat(new SmsNotifier().send(exact)).isEqualTo("SMS: " + exact);
        }

        @Test
        @DisplayName("email does not cut anything")
        void emailKeepsEverything() {
            String longMessage = "This message is far too long for one SMS";
            assertThat(new EmailNotifier().send(longMessage)).isEqualTo("EMAIL: " + longMessage);
        }
    }

    // ── 3. Interface inheritance ──────────────────────────────────────────────

    @Nested
    @DisplayName("3. Interface inheritance")
    class InterfaceInheritanceTest {

        @Test
        @DisplayName("one object, three types")
        void oneObjectManyTypes() {
            MemoryStore store = new MemoryStore();
            assertThat(store).isInstanceOf(ReadWriteStore.class)
                             .isInstanceOf(ReadableStore.class)
                             .isInstanceOf(WritableStore.class);
        }

        @Test
        @DisplayName("an interface may extend several interfaces")
        void interfaceExtendsMany() {
            assertThat(ReadWriteStore.class.getInterfaces())
                    .containsExactly(ReadableStore.class, WritableStore.class);
            assertThat(ReadWriteStore.class.isInterface()).isTrue();
        }

        @Test
        @DisplayName("getInterfaces() lists only DIRECT interfaces")
        void onlyDirectInterfaces() {
            assertThat(MemoryStore.class.getInterfaces()).containsExactly(ReadWriteStore.class);
            assertThat(InterfaceBasicsDemo.interfaceNames(MemoryStore.class)).containsExactly("ReadWriteStore");
        }

        @Test
        @DisplayName("a read-only parameter type can still read what was written")
        void leastPrivilegeView() {
            MemoryStore store = new MemoryStore();
            store.write("lang", "Java");
            assertThat(InterfaceBasicsDemo.lookup(store, "lang")).isEqualTo("Java");
            assertThat(InterfaceBasicsDemo.lookup(store, "missing")).isNull();
        }

        @Test
        @DisplayName("ArrayList implements several interfaces at once")
        void jdkMultipleInterfaces() {
            assertThat(InterfaceBasicsDemo.interfaceNames(ArrayList.class))
                    .containsExactly("List", "RandomAccess", "Cloneable", "Serializable");
            assertThat(ArrayList.class.getInterfaces()).contains(List.class, RandomAccess.class, Serializable.class);
        }

        @Test
        @DisplayName("a class has exactly one superclass but may have many interfaces")
        void oneSuperclassManyInterfaces() {
            assertThat(ArrayList.class.getSuperclass()).isEqualTo(java.util.AbstractList.class);
            assertThat(ArrayList.class.getInterfaces().length).isGreaterThan(1);
        }
    }

    // ── 4. Contracts built into the JDK ───────────────────────────────────────

    @Nested
    @DisplayName("4. Contracts built into the JDK")
    class JdkContractsTest {

        private final List<Version> versions =
                Arrays.asList(new Version(1, 10), new Version(1, 2), new Version(0, 9));

        @Test
        @DisplayName("Comparable gives Collections.sort a natural order")
        void naturalOrder() {
            assertThat(InterfaceBasicsDemo.sortVersions(versions).toString()).isEqualTo("[0.9, 1.2, 1.10]");
        }

        @Test
        @DisplayName("sorting works on a copy: the input order is untouched")
        void inputUntouched() {
            InterfaceBasicsDemo.sortVersions(versions);
            assertThat(versions.get(0)).isEqualTo(new Version(1, 10));
        }

        @Test
        @DisplayName("minor numbers compare numerically, not as text (1.10 > 1.2)")
        void numericComparison() {
            assertThat(new Version(1, 10).compareTo(new Version(1, 2))).isPositive();
            assertThat(new Version(1, 2).compareTo(new Version(1, 10))).isNegative();
            assertThat(new Version(1, 2).compareTo(new Version(1, 2))).isZero();
        }

        @Test
        @DisplayName("a Comparator can reverse the natural order")
        void reversed() {
            assertThat(InterfaceBasicsDemo.newestFirst(versions).toString()).isEqualTo("[1.10, 1.2, 0.9]");
        }

        @Test
        @DisplayName("Iterable makes a class usable in the enhanced for loop")
        void iterable() {
            assertThat(InterfaceBasicsDemo.countdown(3)).containsExactly(3, 2, 1);
            assertThat(InterfaceBasicsDemo.countdown(0)).isEmpty();
        }

        @Test
        @DisplayName("an exhausted iterator throws NoSuchElementException")
        void exhaustedIterator() {
            Iterator<Integer> it = new com.vbforge.oop_interfaces_and_abstraction.basics.Countdown(1).iterator();
            it.next();
            assertThatThrownBy(it::next).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("BigDecimal: equals and compareTo disagree, so HashSet keeps 2 and TreeSet keeps 1")
        void compareToVersusEquals() {
            int[] sizes = InterfaceBasicsDemo.bigDecimalSetSizes();
            assertThat(sizes[0]).isEqualTo(2);
            assertThat(sizes[1]).isEqualTo(1);
        }

        @Test
        @DisplayName("Version keeps equals consistent with compareTo")
        void consistentEquals() {
            assertThat(new Version(1, 2)).isEqualTo(new Version(1, 2));
            assertThat(new Version(1, 2).hashCode()).isEqualTo(new Version(1, 2).hashCode());
            assertThat(new Version(1, 2)).isNotEqualTo(new Version(1, 3));
        }
    }

    // ── 5. Marker interfaces ──────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Marker interfaces")
    class MarkerInterfacesTest {

        @Test
        @DisplayName("ArrayList is a RandomAccess list")
        void arrayList() {
            assertThat(InterfaceBasicsDemo.isRandomAccess(new ArrayList<String>())).isTrue();
        }

        @Test
        @DisplayName("LinkedList is not")
        void linkedList() {
            assertThat(InterfaceBasicsDemo.isRandomAccess(new LinkedList<String>())).isFalse();
        }

        @Test
        @DisplayName("a marker interface declares no methods")
        void noMethods() {
            assertThat(RandomAccess.class.getMethods()).isEmpty();
        }

        @Test
        @DisplayName("a list view created by Arrays.asList is RandomAccess too")
        void arraysAsList() {
            assertThat(InterfaceBasicsDemo.isRandomAccess(Arrays.asList(1, 2, 3))).isTrue();
        }
    }

    // ── 6. Anonymous classes and lambdas ──────────────────────────────────────

    @Nested
    @DisplayName("6. Anonymous classes and lambdas")
    class AnonymousAndLambdaTest {

        @Test
        @DisplayName("an anonymous class implements the interface inline")
        void anonymous() {
            assertThat(InterfaceBasicsDemo.anonymousNotifier().send("hi")).isEqualTo("ANON: hi");
        }

        @Test
        @DisplayName("a lambda implements a single-abstract-method interface")
        void lambda() {
            assertThat(InterfaceBasicsDemo.lambdaNotifier().send("hi")).isEqualTo("LAMBDA: hi");
        }

        @Test
        @DisplayName("an anonymous class has no name of its own")
        void anonymousHasNoSimpleName() {
            assertThat(InterfaceBasicsDemo.anonymousNotifier().getClass().getSimpleName()).isEmpty();
            assertThat(InterfaceBasicsDemo.anonymousNotifier().getClass().isAnonymousClass()).isTrue();
        }

        @Test
        @DisplayName("Notifier has one abstract method; ReadWriteStore has two")
        void abstractMethodCounts() {
            assertThat(InterfaceBasicsDemo.abstractMethodCount(Notifier.class)).isEqualTo(1);
            assertThat(InterfaceBasicsDemo.abstractMethodCount(ReadWriteStore.class)).isEqualTo(2);
        }
    }
}
