package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.InterfaceBasicsTasks.Channel;
import com.vbforge.oop_interfaces_and_abstraction.InterfaceBasicsTasks.Playlist;
import com.vbforge.oop_interfaces_and_abstraction.InterfaceBasicsTasks.Priced;
import com.vbforge.oop_interfaces_and_abstraction.InterfaceBasicsTasks.Temperature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.RandomAccess;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InterfaceBasicsTasksTest — tests for every method in {@link InterfaceBasicsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code InterfaceBasicsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=InterfaceBasicsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("InterfaceBasicsTasks — your implementations")
class InterfaceBasicsTasksTest {

    private InterfaceBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new InterfaceBasicsTasks();
    }

    private static Priced priced(final double price) {
        return () -> price;
    }

    private static Channel channel(final String tag) {
        return message -> tag + ":" + message;
    }

    // ── Task 1: Temperature.compareTo ─────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Temperature.compareTo()")
    @DisplayName("Task 1 — Temperature.compareTo()")
    class TemperatureCompareToTest {

        @Test
        @DisplayName("10 vs 20 → negative; 20 vs 10 → positive; 15 vs 15 → 0")
        void basicSigns() {
            assertThat(new Temperature(10).compareTo(new Temperature(20))).isNegative();
            assertThat(new Temperature(20).compareTo(new Temperature(10))).isPositive();
            assertThat(new Temperature(15).compareTo(new Temperature(15))).isZero();
        }

        @Test
        @DisplayName("negative temperatures are ordered correctly")
        void negativeValues() {
            assertThat(new Temperature(-5).compareTo(new Temperature(3))).isNegative();
            assertThat(new Temperature(-5).compareTo(new Temperature(-10))).isPositive();
        }

        @Test
        @DisplayName("tiny differences are not rounded away (0.5 is not 0)")
        void fractionalDifference() {
            assertThat(new Temperature(20.5).compareTo(new Temperature(20.0))).isPositive();
            assertThat(new Temperature(20.0).compareTo(new Temperature(20.5))).isNegative();
        }

        @ParameterizedTest(name = "{0} vs {1} → sign {2}")
        @CsvSource({
                "1.0,   2.0,   -1",
                "2.0,   1.0,    1",
                "0.0,   0.0,    0",
                "-3.5, -3.5,    0",
                "100.0, -100.0, 1",
        })
        @DisplayName("parametrized signs")
        void parametrized(double a, double b, int expectedSign) {
            assertThat(Integer.signum(new Temperature(a).compareTo(new Temperature(b)))).isEqualTo(expectedSign);
        }

        @Test
        @DisplayName("Collections.sort orders coldest to hottest")
        void sortsInAList() {
            List<Temperature> temps = new ArrayList<>(Arrays.asList(
                    new Temperature(30), new Temperature(-5), new Temperature(12.5)));
            Collections.sort(temps);
            assertThat(temps).extracting(Temperature::getDegrees).containsExactly(-5.0, 12.5, 30.0);
        }
    }

    // ── Task 2: Playlist.iterator ─────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Playlist.iterator()")
    @DisplayName("Task 2 — Playlist.iterator()")
    class PlaylistIteratorTest {

        @Test
        @DisplayName("for-each visits the songs in order")
        void forEachInOrder() {
            List<String> visited = new ArrayList<>();
            for (String song : new Playlist(Arrays.asList("Intro", "Verse", "Outro"))) {
                visited.add(song);
            }
            assertThat(visited).containsExactly("Intro", "Verse", "Outro");
        }

        @Test
        @DisplayName("an empty playlist visits nothing")
        void emptyPlaylist() {
            assertThat(new Playlist(new ArrayList<String>()).iterator().hasNext()).isFalse();
        }

        @Test
        @DisplayName("every call returns a fresh iterator that starts at the first song")
        void freshIteratorEachTime() {
            Playlist playlist = new Playlist(Arrays.asList("A", "B"));
            Iterator<String> first = playlist.iterator();
            first.next();
            first.next();
            assertThat(first.hasNext()).isFalse();
            assertThat(playlist.iterator().next()).isEqualTo("A");
        }

        @Test
        @DisplayName("the playlist can be looped over twice")
        void loopTwice() {
            Playlist playlist = new Playlist(Arrays.asList("A", "B", "C"));
            int count = 0;
            for (int pass = 0; pass < 2; pass++) {
                Iterator<String> it = playlist.iterator();
                while (it.hasNext()) {
                    it.next();
                    count++;
                }
            }
            assertThat(count).isEqualTo(6);
        }

        @Test
        @DisplayName("a single-song playlist yields exactly one song")
        void singleSong() {
            Iterator<String> it = new Playlist(Collections.singletonList("Solo")).iterator();
            assertThat(it.next()).isEqualTo("Solo");
            assertThat(it.hasNext()).isFalse();
        }
    }

    // ── Task 3: totalPrice ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement totalPrice()")
    @DisplayName("Task 3 — totalPrice()")
    class TotalPriceTest {

        @Test
        @DisplayName("[10.0, 2.5, 0.5] → 13.0")
        void basic() {
            List<Priced> items = Arrays.asList(priced(10.0), priced(2.5), priced(0.5));
            assertThat(tasks.totalPrice(items)).isEqualTo(13.0);
        }

        @Test
        @DisplayName("single item → its price")
        void singleItem() {
            assertThat(tasks.totalPrice(Collections.singletonList(priced(7.0)))).isEqualTo(7.0);
        }

        @Test
        @DisplayName("empty list → 0.0")
        void emptyList() {
            assertThat(tasks.totalPrice(new ArrayList<Priced>())).isEqualTo(0.0);
        }

        @Test
        @DisplayName("works for different implementation classes at once")
        void mixedImplementations() {
            Priced anonymous = new Priced() {
                @Override
                public double price() {
                    return 4.0;
                }
            };
            List<Priced> items = Arrays.asList(anonymous, priced(6.0));
            assertThat(tasks.totalPrice(items)).isEqualTo(10.0);
        }

        @Test
        @DisplayName("zero-priced items do not change the total")
        void zeroPrices() {
            List<Priced> items = Arrays.asList(priced(0.0), priced(5.0), priced(0.0));
            assertThat(tasks.totalPrice(items)).isEqualTo(5.0);
        }
    }

    // ── Task 4: combine ───────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement combine()")
    @DisplayName("Task 4 — combine()")
    class CombineTest {

        @Test
        @DisplayName("two channels → receipts joined with \" | \"")
        void twoChannels() {
            Channel both = tasks.combine(Arrays.asList(channel("A"), channel("B")));
            assertThat(both.send("hi")).isEqualTo("A:hi | B:hi");
        }

        @Test
        @DisplayName("one channel → just its receipt")
        void oneChannel() {
            Channel one = tasks.combine(Collections.singletonList(channel("A")));
            assertThat(one.send("hi")).isEqualTo("A:hi");
        }

        @Test
        @DisplayName("no channels → empty string")
        void noChannels() {
            assertThat(tasks.combine(new ArrayList<Channel>()).send("hi")).isEmpty();
        }

        @Test
        @DisplayName("channels are used in list order")
        void order() {
            Channel both = tasks.combine(Arrays.asList(channel("B"), channel("A")));
            assertThat(both.send("x")).isEqualTo("B:x | A:x");
        }

        @Test
        @DisplayName("the combined channel can be reused and is itself combinable")
        void reusableAndNestable() {
            Channel inner = tasks.combine(Arrays.asList(channel("A"), channel("B")));
            Channel outer = tasks.combine(Arrays.asList(inner, channel("C")));
            assertThat(outer.send("1")).isEqualTo("A:1 | B:1 | C:1");
            assertThat(outer.send("2")).isEqualTo("A:2 | B:2 | C:2");
        }
    }

    // ── Task 5: interfaceNames ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement interfaceNames()")
    @DisplayName("Task 5 — interfaceNames()")
    class InterfaceNamesTest {

        @Test
        @DisplayName("ArrayList → [List, RandomAccess, Cloneable, Serializable]")
        void arrayList() {
            assertThat(tasks.interfaceNames(ArrayList.class))
                    .containsExactly("List", "RandomAccess", "Cloneable", "Serializable");
        }

        @Test
        @DisplayName("HashMap → [Map, Cloneable, Serializable]")
        void hashMap() {
            assertThat(tasks.interfaceNames(java.util.HashMap.class))
                    .containsExactly("Map", "Cloneable", "Serializable");
        }

        @Test
        @DisplayName("Temperature → [Comparable]")
        void ownClass() {
            assertThat(tasks.interfaceNames(Temperature.class)).containsExactly("Comparable");
        }

        @Test
        @DisplayName("Object implements nothing → []")
        void object() {
            assertThat(tasks.interfaceNames(Object.class)).isEmpty();
        }

        @Test
        @DisplayName("interfaces inherited from a superclass are not listed")
        void inheritedNotListed() {
            assertThat(tasks.interfaceNames(Base.class)).containsExactly("Runnable");
            assertThat(tasks.interfaceNames(Child.class)).isEmpty();
        }

        @Test
        @DisplayName("a class with one interface and an interface without super-interfaces")
        void ownTypes() {
            assertThat(tasks.interfaceNames(Playlist.class)).containsExactly("Iterable");
            assertThat(tasks.interfaceNames(Priced.class)).isEmpty();
        }

        class Base implements Runnable {
            @Override
            public void run() {
            }
        }

        class Child extends Base {
        }
    }

    // ── Task 6: implementsInterface ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement implementsInterface()")
    @DisplayName("Task 6 — implementsInterface()")
    class ImplementsInterfaceTest {

        @ParameterizedTest(name = "{0} implements {1} → {2}")
        @CsvSource({
                "java.util.ArrayList,  java.util.Collection,       true",
                "java.util.HashMap,    java.util.Map,              true",
                "java.lang.String,     java.lang.CharSequence,     true",
                "java.util.LinkedList, java.util.RandomAccess,     false",
                "java.util.ArrayList,  java.util.AbstractList,     false",
                "java.lang.Integer,    java.lang.Comparable,       true",
                "java.lang.Object,     java.lang.Runnable,         false",
                "java.lang.Runnable,   java.lang.Runnable,         true",
        })
        @DisplayName("parametrized JDK types")
        void jdkTypes(String type, String iface, boolean expected) throws ClassNotFoundException {
            assertThat(tasks.implementsInterface(Class.forName(type), Class.forName(iface))).isEqualTo(expected);
        }

        @Test
        @DisplayName("the same checks written with class literals")
        void classLiterals() {
            assertThat(tasks.implementsInterface(ArrayList.class, Collection.class)).isTrue();
            assertThat(tasks.implementsInterface(LinkedList.class, RandomAccess.class)).isFalse();
            assertThat(tasks.implementsInterface(ArrayList.class, AbstractList.class)).isFalse();
        }

        @Test
        @DisplayName("an interface counts as implementing itself")
        void interfaceItself() {
            assertThat(tasks.implementsInterface(Priced.class, Priced.class)).isTrue();
        }

        @Test
        @DisplayName("a lambda object implements its target interface")
        void lambdaObject() {
            Channel lambda = message -> message;
            assertThat(tasks.implementsInterface(lambda.getClass(), Channel.class)).isTrue();
        }
    }
}
