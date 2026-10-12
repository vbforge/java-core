package com.vbforge.oop_inheritance_and_polymorphism;

import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Animal;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Car;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Counted;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.CountedChild;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Dog;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Engine;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Fish;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Puppy;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Truck;
import com.vbforge.oop_inheritance_and_polymorphism.InheritanceDemo.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.Stack;
import java.util.Vector;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * InheritanceDemoTest — verifies every concept shown in {@link InheritanceDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * Java's inheritance rules.
 */
@DisplayName("InheritanceDemo — API contracts")
class InheritanceDemoTest {

    // ── 1. extends, IS-A and the Object root ──────────────────────────────────

    @Nested
    @DisplayName("1. extends, IS-A and the Object root")
    class ExtendsTest {

        @Test
        @DisplayName("subclass inherits the public API of its parent")
        void inheritsPublicApi() {
            Dog rex = new Dog("Rex");
            assertThat(rex.getName()).isEqualTo("Rex");
            assertThat(rex.species()).isEqualTo("Dog");
        }

        @Test
        @DisplayName("a subclass can add members the parent does not have")
        void addsOwnMembers() {
            assertThat(new Dog("Rex").fetch()).isEqualTo("Rex fetches the ball");
        }

        @Test
        @DisplayName("Dog IS-A Animal")
        void isA() {
            Dog rex = new Dog("Rex");
            assertThat(rex).isInstanceOf(Animal.class);
            assertThat(Animal.class.isAssignableFrom(Dog.class)).isTrue();
            assertThat(Dog.class.getSuperclass()).isEqualTo(Animal.class);
        }

        @Test
        @DisplayName("every class sits on top of Object; Object has no superclass")
        void objectRoot() {
            assertThat(Animal.class.getSuperclass()).isEqualTo(Object.class);
            assertThat(Object.class.getSuperclass()).isNull();
        }

        @Test
        @DisplayName("without an override, toString() comes from Object (ClassName@hash)")
        void defaultToString() {
            assertThat(new Fish("Nemo").toString()).startsWith(Fish.class.getName() + "@");
        }

        @Test
        @DisplayName("private parent field stays private; the subclass declares no fields of its own")
        void privateFieldNotAccessibleByName() throws NoSuchFieldException {
            assertThat(Modifier.isPrivate(Animal.class.getDeclaredField("name").getModifiers())).isTrue();
            assertThat(Dog.class.getDeclaredFields()).isEmpty();
        }

        @Test
        @DisplayName("protected parent field is usable inside a subclass")
        void protectedFieldAccessible() {
            assertThat(new Dog("Rex").tire()).isEqualTo(7);
        }
    }

    // ── 2. Overriding ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Overriding")
    class OverridingTest {

        @Test
        @DisplayName("the runtime class decides which override runs")
        void runtimeTypeWins() {
            Animal pet = new Dog("Rex");
            assertThat(pet.sound()).isEqualTo("Woof");
        }

        @Test
        @DisplayName("a method that is not overridden behaves as in the parent")
        void inheritedBehaviour() {
            assertThat(new Fish("Nemo").sound()).isEqualTo("...");
        }

        @Test
        @DisplayName("parent method sees the override: describe() uses Dog.sound()")
        void parentMethodUsesOverride() {
            assertThat(new Dog("Rex").describe()).isEqualTo("Rex says Woof");
            assertThat(new Fish("Nemo").describe()).isEqualTo("Nemo says ...");
        }

        @Test
        @DisplayName("covariant return: Dog.offspring() returns Dog without a cast")
        void covariantReturn() {
            Dog baby = new Dog("Rex").offspring("Bit");
            assertThat(baby.getName()).isEqualTo("Bit");
            assertThat(baby.fetch()).isEqualTo("Bit fetches the ball");
        }

        @Test
        @DisplayName("covariant override is still used through the parent type")
        void covariantThroughParentReference() {
            Animal pet = new Dog("Rex");
            assertThat(pet.offspring("Bit")).isExactlyInstanceOf(Dog.class);
            assertThat(new Fish("Nemo").offspring("Dot")).isExactlyInstanceOf(Animal.class);
        }

        @Test
        @DisplayName("an override may widen access (protected → public)")
        void wideningAccess() {
            assertThat(InheritanceDemo.hookModifiers(Animal.class)).isEqualTo("protected");
            assertThat(InheritanceDemo.hookModifiers(Dog.class)).isEqualTo("public");
        }

        @Test
        @DisplayName("widened override is what runs through the parent reference")
        void wideningStillDispatchesDynamically() {
            Animal pet = new Dog("Rex");
            assertThat(pet.hook()).isEqualTo("Dog.hook");
        }
    }

    // ── 3. The super keyword ──────────────────────────────────────────────────

    @Nested
    @DisplayName("3. The super keyword")
    class SuperTest {

        @Test
        @DisplayName("super.method() reuses the parent's logic")
        void superMethod() {
            assertThat(new Puppy("Bo").sound()).isEqualTo("Woof (squeaky)");
        }

        @Test
        @DisplayName("virtual calls INSIDE super.describe() still use the runtime class")
        void superDoesNotDisableDynamicDispatchInside() {
            assertThat(new Puppy("Bo").describe()).isEqualTo("Bo says Woof (squeaky)!");
        }

        @Test
        @DisplayName("super(name) initialises the parent part of the object")
        void superConstructor() {
            assertThat(new Puppy("Bo").getName()).isEqualTo("Bo");
        }

        @Test
        @DisplayName("hierarchy: Puppy IS-A Dog IS-A Animal")
        void hierarchy() {
            Puppy bo = new Puppy("Bo");
            assertThat(bo).isInstanceOf(Dog.class).isInstanceOf(Animal.class);
        }
    }

    // ── 4. Constructor chaining ───────────────────────────────────────────────

    @Nested
    @DisplayName("4. Constructor chaining")
    class ConstructorChainingTest {

        @Test
        @DisplayName("parent first, then initialiser block, then each body down the chain")
        void order() {
            assertThat(InheritanceDemo.constructionLog())
                    .containsExactly("Base()", "Middle initializer block", "Middle()", "Leaf()");
        }

        @Test
        @DisplayName("the topmost parent constructor always runs first")
        void parentFirst() {
            assertThat(InheritanceDemo.constructionLog().get(0)).isEqualTo("Base()");
        }

        @Test
        @DisplayName("this(...) delegates first, then the rest of the body runs")
        void thisDelegation() {
            assertThat(InheritanceDemo.constructionLogWithThis())
                    .containsExactly("Base()", "Middle initializer block", "Middle()", "Leaf()", "Leaf(x)");
        }

        @Test
        @DisplayName("the compiler inserts super() when no constructor is declared")
        void implicitSuper() {
            int before = Counted.instances;
            new CountedChild();
            assertThat(Counted.instances).isEqualTo(before + 1);
        }
    }

    // ── 5. The constructor trap ───────────────────────────────────────────────

    @Nested
    @DisplayName("5. The constructor trap")
    class ConstructorTrapTest {

        @Test
        @DisplayName("overridable call in a constructor sees default (null) subclass fields")
        void seesUninitialisedField() {
            assertThat(InheritanceDemo.greetingSeenByConstructor()).isEqualTo("Hellonull");
        }

        @Test
        @DisplayName("after construction the same call sees the initialised field")
        void seesFieldAfterConstruction() {
            assertThat(InheritanceDemo.greetingAfterConstruction()).isEqualTo("Hello!!!");
        }

        @Test
        @DisplayName("the two results differ — that is the bug")
        void resultsDiffer() {
            assertThat(InheritanceDemo.greetingSeenByConstructor())
                    .isNotEqualTo(InheritanceDemo.greetingAfterConstruction());
        }
    }

    // ── 6. Hiding: fields and static methods ──────────────────────────────────

    @Nested
    @DisplayName("6. Hiding: fields and static methods")
    class HidingTest {

        @Test
        @DisplayName("field access uses the DECLARED type of the variable")
        void fieldViaParentReference() {
            assertThat(InheritanceDemo.fieldViaParentReference()).isEqualTo("vehicle-field");
            assertThat(InheritanceDemo.fieldViaChildReference()).isEqualTo("car-field");
        }

        @Test
        @DisplayName("a hidden field is a second, separate field in the same object")
        void bothFieldsExist() {
            Car car = new Car();
            Vehicle asVehicle = car;
            assertThat(car.label).isEqualTo("car-field");
            assertThat(asVehicle.label).isEqualTo("vehicle-field");
        }

        @Test
        @DisplayName("instance methods are overridden: runtime type decides")
        void methodUsesRuntimeType() {
            Vehicle v = new Car();
            assertThat(v.who()).isEqualTo("Car instance");
            assertThat(v.getLabel()).isEqualTo("car-field");
        }

        @Test
        @DisplayName("static methods are hidden: a static call through an instance uses the declared type")
        void staticUsesDeclaredType() {
            assertThat(InheritanceDemo.staticViaInstanceRef()).isEqualTo("Vehicle static");
            assertThat(Car.kind()).isEqualTo("Car static");
            assertThat(Vehicle.kind()).isEqualTo("Vehicle static");
        }
    }

    // ── 7. final and composition ──────────────────────────────────────────────

    @Nested
    @DisplayName("7. final and composition")
    class FinalAndCompositionTest {

        @Test
        @DisplayName("a final method exists in the parent and cannot be overridden")
        void finalMethod() {
            assertThat(Modifier.isFinal(InheritanceDemo.speciesModifiers())).isTrue();
            assertThat(new Dog("Rex").species()).isEqualTo("Dog");
            assertThat(new Puppy("Bo").species()).isEqualTo("Puppy");
        }

        @Test
        @DisplayName("String is a final class")
        void finalClass() {
            assertThat(Modifier.isFinal(String.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("composition: Truck HAS-A Engine and delegates to it")
        void composition() {
            assertThat(new Truck(new Engine(300)).start()).isEqualTo("Truck: Engine(300hp) started");
        }

        @Test
        @DisplayName("a Truck is not an Engine")
        void notAnEngine() {
            assertThat(Engine.class.isAssignableFrom(Truck.class)).isFalse();
        }

        @Test
        @DisplayName("Stack extends Vector, so inherited methods can break LIFO")
        void stackBackdoor() {
            Stack<Integer> stack = InheritanceDemo.stackWithBackdoor();
            assertThat(Vector.class.isAssignableFrom(Stack.class)).isTrue();
            assertThat(stack).containsExactly(99, 1, 2);
            assertThat(stack.get(0)).isEqualTo(99);
        }
    }
}
