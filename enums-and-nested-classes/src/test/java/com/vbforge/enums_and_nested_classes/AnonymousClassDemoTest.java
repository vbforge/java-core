package com.vbforge.enums_and_nested_classes;

import com.vbforge.enums_and_nested_classes.AnonymousClassDemo.Greeter;
import com.vbforge.enums_and_nested_classes.AnonymousClassDemo.Probe;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnonymousClassDemo — API contracts")
class AnonymousClassDemoTest {

    @Nested
    @DisplayName("1. Implementing an interface inline")
    class InterfaceImplementation {

        @Test
        @DisplayName("sortByLength orders shortest first")
        void sortsByLength() {
            assertThat(AnonymousClassDemo.sortByLength(Arrays.asList("pear", "fig", "banana", "kiwi")))
                    .containsExactly("fig", "pear", "kiwi", "banana");
        }

        @Test
        @DisplayName("equal lengths keep their original order (stable sort)")
        void sortIsStable() {
            assertThat(AnonymousClassDemo.sortByLength(Arrays.asList("kiwi", "pear", "fig")))
                    .containsExactly("fig", "kiwi", "pear");
        }

        @Test
        @DisplayName("sortByLength returns a copy and leaves the input alone")
        void sortReturnsCopy() {
            List<String> input = new ArrayList<>(Arrays.asList("ccc", "a", "bb"));
            List<String> sorted = AnonymousClassDemo.sortByLength(input);
            assertThat(sorted).isNotSameAs(input);
            assertThat(input).containsExactly("ccc", "a", "bb");
        }

        @Test
        @DisplayName("sorting an empty list gives an empty list")
        void sortEmpty() {
            assertThat(AnonymousClassDemo.sortByLength(Collections.<String>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("the anonymous listener records start and finish events")
        void listenerRecordsEvents() {
            assertThat(AnonymousClassDemo.runWithListener("backup"))
                    .containsExactly("start:backup", "finish:backup");
        }

        @Test
        @DisplayName("each call captures its own events list")
        void eachCallHasItsOwnLog() {
            assertThat(AnonymousClassDemo.runWithListener("a")).hasSize(2);
            assertThat(AnonymousClassDemo.runWithListener("b")).containsExactly("start:b", "finish:b");
        }
    }

    @Nested
    @DisplayName("2. Extending a class with constructor arguments")
    class ClassExtension {

        @Test
        @DisplayName("the constructor argument reaches the superclass")
        void superclassReceivesArgument() {
            assertThat(AnonymousClassDemo.casualGreeter("Ann").name()).isEqualTo("Ann");
        }

        @Test
        @DisplayName("the anonymous body implements the abstract method")
        void implementsAbstractMethod() {
            assertThat(AnonymousClassDemo.casualGreeter("Ann").greet()).isEqualTo("Hey Ann!");
        }

        @Test
        @DisplayName("the result is an anonymous subclass of Greeter")
        void isAnonymousSubclass() {
            Greeter greeter = AnonymousClassDemo.casualGreeter("Ann");
            assertThat(greeter.getClass().isAnonymousClass()).isTrue();
            assertThat(greeter.getClass().getSuperclass()).isEqualTo(Greeter.class);
        }

        @Test
        @DisplayName("two greeters keep separate state")
        void greetersAreIndependent() {
            assertThat(AnonymousClassDemo.casualGreeter("Ann").greet()).isEqualTo("Hey Ann!");
            assertThat(AnonymousClassDemo.casualGreeter("Bob").greet()).isEqualTo("Hey Bob!");
        }
    }

    @Nested
    @DisplayName("3. Own state and captured values")
    class OwnState {

        @Test
        @DisplayName("the supplier advances its own field on every call")
        void advancesState() {
            Supplier<Integer> evens = AnonymousClassDemo.counterFrom(0, 2);
            assertThat(evens.get()).isEqualTo(0);
            assertThat(evens.get()).isEqualTo(2);
            assertThat(evens.get()).isEqualTo(4);
        }

        @Test
        @DisplayName("two suppliers do not share their counter")
        void suppliersAreIndependent() {
            Supplier<Integer> first = AnonymousClassDemo.counterFrom(10, 10);
            Supplier<Integer> second = AnonymousClassDemo.counterFrom(10, 10);
            first.get();
            first.get();
            assertThat(second.get()).isEqualTo(10);
            assertThat(first.get()).isEqualTo(30);
        }

        @Test
        @DisplayName("a negative step counts down")
        void negativeStep() {
            Supplier<Integer> down = AnonymousClassDemo.counterFrom(5, -1);
            assertThat(down.get()).isEqualTo(5);
            assertThat(down.get()).isEqualTo(4);
            assertThat(down.get()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("4. \"this\" — anonymous class vs lambda")
    class ThisKeyword {

        @Test
        @DisplayName("inside an anonymous class, this is the anonymous instance (empty simple name)")
        void anonymousThis() {
            assertThat(new Probe().anonymousThis()).isEmpty();
        }

        @Test
        @DisplayName("inside a lambda, this is the enclosing instance")
        void lambdaThis() {
            assertThat(new Probe().lambdaThis()).isEqualTo("Probe");
        }

        @Test
        @DisplayName("an anonymous class object reports an anonymous class")
        void anonymousObjectClass() {
            Supplier<String> supplier = AnonymousClassDemo.anonymousSupplier();
            assertThat(supplier.get()).isEqualTo("anonymous");
            assertThat(supplier.getClass().isAnonymousClass()).isTrue();
        }

        @Test
        @DisplayName("a lambda object is not an anonymous class")
        void lambdaObjectClass() {
            Supplier<String> supplier = AnonymousClassDemo.lambdaSupplier();
            assertThat(supplier.get()).isEqualTo("lambda");
            assertThat(supplier.getClass().isAnonymousClass()).isFalse();
        }
    }

    @Nested
    @DisplayName("5. The double-brace initialisation trap")
    class DoubleBrace {

        @Test
        @DisplayName("the trap list equals a normal list with the same elements")
        void equalAsLists() {
            assertThat(AnonymousClassDemo.doubleBraceList()).isEqualTo(AnonymousClassDemo.plainList());
        }

        @Test
        @DisplayName("but its runtime class is an anonymous subclass of ArrayList")
        void runtimeClassDiffers() {
            List<String> trap = AnonymousClassDemo.doubleBraceList();
            assertThat(trap.getClass()).isNotEqualTo(ArrayList.class);
            assertThat(trap.getClass().isAnonymousClass()).isTrue();
            assertThat(trap.getClass().getSuperclass()).isEqualTo(ArrayList.class);
        }

        @Test
        @DisplayName("a plain list keeps the real ArrayList class")
        void plainListIsArrayList() {
            assertThat(AnonymousClassDemo.plainList().getClass()).isEqualTo(ArrayList.class);
        }

        @Test
        @DisplayName("both lists hold a and b in order")
        void contents() {
            assertThat(AnonymousClassDemo.doubleBraceList()).containsExactly("a", "b");
            assertThat(AnonymousClassDemo.plainList()).containsExactly("a", "b");
        }
    }

    @Nested
    @DisplayName("6. main() smoke run")
    class MainMethod {

        @Test
        @DisplayName("main runs all sections without throwing")
        void mainRuns() {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured));
            try {
                AnonymousClassDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }
            assertThat(captured.toString()).contains("---").contains("Hey Ann!");
        }
    }
}
