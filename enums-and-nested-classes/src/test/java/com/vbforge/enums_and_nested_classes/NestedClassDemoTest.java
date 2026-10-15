package com.vbforge.enums_and_nested_classes;

import com.vbforge.enums_and_nested_classes.NestedClassDemo.Car;
import com.vbforge.enums_and_nested_classes.NestedClassDemo.NumberRange;
import com.vbforge.enums_and_nested_classes.NestedClassDemo.Pizza;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("NestedClassDemo — API contracts")
class NestedClassDemoTest {

    @Nested
    @DisplayName("1. Static nested class")
    class StaticNested {

        @Test
        @DisplayName("builder applies its defaults")
        void builderDefaults() {
            Pizza pizza = new Pizza.Builder().build();
            assertThat(pizza.size()).isEqualTo("medium");
            assertThat(pizza.toppings()).isEmpty();
        }

        @Test
        @DisplayName("builder collects size and toppings in order")
        void builderCollectsValues() {
            Pizza pizza = new Pizza.Builder().size("large").topping("ham").topping("olives").build();
            assertThat(pizza.size()).isEqualTo("large");
            assertThat(pizza.toppings()).containsExactly("ham", "olives");
        }

        @Test
        @DisplayName("builder methods return the same builder for chaining")
        void builderIsFluent() {
            Pizza.Builder builder = new Pizza.Builder();
            assertThat(builder.size("small")).isSameAs(builder);
            assertThat(builder.topping("cheese")).isSameAs(builder);
        }

        @Test
        @DisplayName("the pizza's topping list is unmodifiable")
        void toppingsAreUnmodifiable() {
            Pizza pizza = new Pizza.Builder().topping("ham").build();
            assertThatThrownBy(() -> pizza.toppings().add("pineapple"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("later builder changes do not leak into a built pizza")
        void builtPizzaIsASnapshot() {
            Pizza.Builder builder = new Pizza.Builder().topping("ham");
            Pizza pizza = builder.build();
            builder.topping("olives");
            assertThat(pizza.toppings()).containsExactly("ham");
        }

        @Test
        @DisplayName("the nested Builder is static; the Pizza constructor is private")
        void modifiers() {
            assertThat(Modifier.isStatic(Pizza.Builder.class.getModifiers())).isTrue();
            Optional<Constructor<?>> declared = Arrays.stream(Pizza.class.getDeclaredConstructors())
                    .filter(constructor -> !constructor.isSynthetic())     // skip compiler-made accessors
                    .findFirst();
            assertThat(declared).isNotEmpty();
            assertThat(Modifier.isPrivate(declared.get().getModifiers())).isTrue();
        }
    }

    @Nested
    @DisplayName("2. Inner (non-static member) class")
    class InnerClass {

        @Test
        @DisplayName("iterator walks the range using the outer fields")
        void iteratesRange() {
            List<Integer> seen = new ArrayList<>();
            for (int n : new NumberRange(3, 7)) {
                seen.add(n);
            }
            assertThat(seen).containsExactly(3, 4, 5, 6);
        }

        @Test
        @DisplayName("an empty range has no elements")
        void emptyRange() {
            assertThat(new NumberRange(5, 5).iterator().hasNext()).isFalse();
            assertThat(new NumberRange(7, 3).iterator().hasNext()).isFalse();
        }

        @Test
        @DisplayName("next() past the end throws NoSuchElementException")
        void nextPastEnd() {
            Iterator<Integer> iterator = new NumberRange(0, 1).iterator();
            iterator.next();
            assertThatThrownBy(iterator::next).isInstanceOf(NoSuchElementException.class);
        }

        @Test
        @DisplayName("each iterator has its own position")
        void independentIterators() {
            NumberRange range = new NumberRange(1, 4);
            Iterator<Integer> first = range.iterator();
            Iterator<Integer> second = range.iterator();
            first.next();
            first.next();
            assertThat(second.next()).isEqualTo(1);
            assertThat(first.next()).isEqualTo(3);
        }

        @Test
        @DisplayName("the iterator class is a non-static member of NumberRange")
        void iteratorIsInner() {
            Class<?> iteratorClass = new NumberRange(0, 1).iterator().getClass();
            assertThat(Modifier.isStatic(iteratorClass.getModifiers())).isFalse();
            assertThat(iteratorClass.getEnclosingClass()).isEqualTo(NumberRange.class);
        }
    }

    @Nested
    @DisplayName("3. Outer.this and outer.new Inner()")
    class OuterThis {

        @Test
        @DisplayName("Outer.this reaches the enclosing instance past a shadowing field")
        void describeUsesOuterThis() {
            Car car = new Car("Tesla");
            Car.Engine engine = car.new Engine("electric");
            assertThat(engine.describe()).isEqualTo("electric engine of Tesla");
        }

        @Test
        @DisplayName("an inner object sees the live state of its outer object")
        void engineSeesLiveState() {
            Car car = new Car("Tesla");
            Car.Engine engine = car.newEngine("electric");
            assertThat(engine.kmOnCar()).isEqualTo(0);
            car.drive(120);
            assertThat(engine.kmOnCar()).isEqualTo(120);
        }

        @Test
        @DisplayName("two engines of one car share that car's state")
        void enginesOfOneCarShareState() {
            Car car = new Car("Tesla");
            Car.Engine first = car.new Engine("electric");
            Car.Engine second = car.newEngine("backup");
            car.drive(50);
            assertThat(first.kmOnCar()).isEqualTo(50);
            assertThat(second.kmOnCar()).isEqualTo(50);
        }

        @Test
        @DisplayName("engines of different cars are independent")
        void enginesOfDifferentCarsAreIndependent() {
            Car tesla = new Car("Tesla");
            Car ford = new Car("Ford");
            Car.Engine teslaEngine = tesla.newEngine("electric");
            Car.Engine fordEngine = ford.newEngine("V8");
            tesla.drive(10);
            assertThat(teslaEngine.kmOnCar()).isEqualTo(10);
            assertThat(fordEngine.kmOnCar()).isEqualTo(0);
            assertThat(fordEngine.describe()).isEqualTo("V8 engine of Ford");
        }
    }

    @Nested
    @DisplayName("4. Local class")
    class LocalClass {

        @Test
        @DisplayName("the local class captures the method parameter")
        void capturesParameter() {
            assertThat(NestedClassDemo.greetAll("Hello", Arrays.asList("Ann", "Bob")))
                    .containsExactly("Hello, Ann!", "Hello, Bob!");
        }

        @Test
        @DisplayName("each call captures its own value")
        void eachCallCapturesOwnValue() {
            assertThat(NestedClassDemo.greetAll("Hi", Arrays.asList("Ann"))).containsExactly("Hi, Ann!");
            assertThat(NestedClassDemo.greetAll("Yo", Arrays.asList("Ann"))).containsExactly("Yo, Ann!");
        }

        @Test
        @DisplayName("no names → no greetings")
        void emptyInput() {
            assertThat(NestedClassDemo.greetAll("Hello", Collections.<String>emptyList())).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. Telling the kinds apart at runtime")
    class Kinds {

        @Test
        @DisplayName("a top-level class")
        void topLevel() {
            assertThat(NestedClassDemo.kindOf(NestedClassDemo.class)).isEqualTo("top-level");
        }

        @Test
        @DisplayName("a static nested class")
        void staticNested() {
            assertThat(NestedClassDemo.kindOf(Pizza.Builder.class)).isEqualTo("static nested");
            assertThat(NestedClassDemo.kindOf(NumberRange.class)).isEqualTo("static nested");
        }

        @Test
        @DisplayName("an inner class")
        void inner() {
            assertThat(NestedClassDemo.kindOf(Car.Engine.class)).isEqualTo("inner");
        }

        @Test
        @DisplayName("a local class")
        void local() {
            assertThat(NestedClassDemo.kindOf(NestedClassDemo.localClassSample())).isEqualTo("local");
        }

        @Test
        @DisplayName("an anonymous class")
        void anonymous() {
            assertThat(NestedClassDemo.kindOf(NestedClassDemo.anonymousClassSample())).isEqualTo("anonymous");
        }

        @Test
        @DisplayName("nested enums and interfaces are implicitly static")
        void enumsAndInterfacesAreStatic() {
            assertThat(NestedClassDemo.kindOf(EnumDemo.Season.class)).isEqualTo("static nested");
            assertThat(NestedClassDemo.kindOf(AnonymousClassDemo.Listener.class)).isEqualTo("static nested");
        }

        @Test
        @DisplayName("binary names join the nesting levels with $")
        void binaryNames() {
            assertThat(Car.Engine.class.getName()).endsWith("NestedClassDemo$Car$Engine");
            assertThat(Car.Engine.class.getSimpleName()).isEqualTo("Engine");
            assertThat(Car.Engine.class.getEnclosingClass()).isEqualTo(Car.class);
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
                NestedClassDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }
            assertThat(captured.toString()).contains("---").contains("electric engine of Tesla");
        }
    }
}
