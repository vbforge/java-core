package com.vbforge.reflection_api;

import com.vbforge.reflection_api.ClassIntrospectionDemo.Animal;
import com.vbforge.reflection_api.ClassIntrospectionDemo.Dog;
import com.vbforge.reflection_api.ClassIntrospectionDemo.Inventory;
import com.vbforge.reflection_api.ClassIntrospectionDemo.Named;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.RandomAccess;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ClassIntrospectionTasks — your implementations")
class ClassIntrospectionTasksTest {

    /** Non-static inner classes carry a synthetic reference to the outer instance. */
    static class Outer {
        class Inner {
            int x;

            Outer outer() {
                return Outer.this;
            }
        }
    }

    static class Empty { }

    private ClassIntrospectionTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ClassIntrospectionTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sortedDeclaredFieldNames()")
    @DisplayName("sortedDeclaredFieldNames")
    class SortedDeclaredFieldNamesTest {

        @Test
        @DisplayName("Dog → [LEGS, age, nickname] (all visibilities, static included, sorted)")
        void dog() {
            assertThat(tasks.sortedDeclaredFieldNames(Dog.class)).containsExactly("LEGS", "age", "nickname");
        }

        @Test
        @DisplayName("inherited fields are not included: Animal → [name]")
        void inheritedExcluded() {
            assertThat(tasks.sortedDeclaredFieldNames(Animal.class)).containsExactly("name");
            assertThat(tasks.sortedDeclaredFieldNames(Dog.class)).doesNotContain("name");
        }

        @Test
        @DisplayName("an interface without fields → empty list")
        void interfaceHasNoFields() {
            assertThat(tasks.sortedDeclaredFieldNames(Named.class)).isEmpty();
        }

        @Test
        @DisplayName("a class without fields → empty list")
        void emptyClass() {
            assertThat(tasks.sortedDeclaredFieldNames(Empty.class)).isEmpty();
        }

        @Test
        @DisplayName("synthetic fields such as this$0 are skipped")
        void syntheticSkipped() {
            assertThat(tasks.sortedDeclaredFieldNames(Outer.Inner.class)).containsExactly("x");
        }

        @Test
        @DisplayName("the result is a list (never null) sorted in natural order")
        void sortedNaturalOrder() {
            List<String> names = tasks.sortedDeclaredFieldNames(Inventory.class);

            assertThat(names).containsExactly("counts", "label", "tags");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement allInterfaces()")
    @DisplayName("allInterfaces")
    class AllInterfacesTest {

        @Test
        @DisplayName("Dog → {Comparable, Named} (one direct, one inherited from Animal)")
        void dog() {
            Set<Class<?>> result = tasks.allInterfaces(Dog.class);

            assertThat(result).containsExactlyInAnyOrder(Comparable.class, Named.class);
        }

        @Test
        @DisplayName("Animal → {Named}")
        void animal() {
            assertThat(tasks.allInterfaces(Animal.class)).containsExactlyInAnyOrder(Named.class);
        }

        @Test
        @DisplayName("Object → empty set")
        void object() {
            assertThat(tasks.allInterfaces(Object.class)).isEmpty();
        }

        @Test
        @DisplayName("ArrayList → includes List, Collection, Iterable and RandomAccess")
        void arrayList() {
            Set<Class<?>> result = tasks.allInterfaces(ArrayList.class);

            assertThat(result).contains(List.class, Collection.class, Iterable.class, RandomAccess.class);
        }

        @Test
        @DisplayName("an interface collects its super-interfaces but not itself")
        void interfaceItself() {
            Set<Class<?>> result = tasks.allInterfaces(List.class);

            assertThat(result).contains(Collection.class, Iterable.class);
            assertThat(result).doesNotContain(List.class);
        }

        @Test
        @DisplayName("an interface without parents → empty set")
        void rootInterface() {
            assertThat(tasks.allInterfaces(Named.class)).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isPlainConcreteClass()")
    @DisplayName("isPlainConcreteClass")
    class IsPlainConcreteClassTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "java.lang.String, true",
                "java.util.ArrayList, true",
                "com.vbforge.reflection_api.ClassIntrospectionDemo$Dog, true",
                "com.vbforge.reflection_api.ClassIntrospectionDemo$Animal, false",
                "com.vbforge.reflection_api.ClassIntrospectionDemo$Named, false",
                "java.util.List, false",
                "java.lang.Number, false",
                "java.lang.Thread$State, false",
                "java.lang.Override, false"
        })
        @DisplayName("classifies common reference types")
        void referenceTypes(String className, boolean expected) throws Exception {
            assertThat(tasks.isPlainConcreteClass(Class.forName(className))).isEqualTo(expected);
        }

        @Test
        @DisplayName("primitives are not plain concrete classes")
        void primitives() {
            assertThat(tasks.isPlainConcreteClass(int.class)).isFalse();
            assertThat(tasks.isPlainConcreteClass(void.class)).isFalse();
        }

        @Test
        @DisplayName("arrays are not plain concrete classes")
        void arrays() {
            assertThat(tasks.isPlainConcreteClass(int[].class)).isFalse();
            assertThat(tasks.isPlainConcreteClass(String[].class)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement genericTypeArguments()")
    @DisplayName("genericTypeArguments")
    class GenericTypeArgumentsTest {

        @Test
        @DisplayName("List<String> → [java.lang.String]")
        void listOfString() throws Exception {
            assertThat(tasks.genericTypeArguments(Inventory.class.getField("tags")))
                    .containsExactly("java.lang.String");
        }

        @Test
        @DisplayName("Map<String, Integer> → [java.lang.String, java.lang.Integer] in order")
        void mapOfStringInteger() throws Exception {
            assertThat(tasks.genericTypeArguments(Inventory.class.getField("counts")))
                    .containsExactly("java.lang.String", "java.lang.Integer");
        }

        @Test
        @DisplayName("a non-generic field → empty list")
        void nonGeneric() throws Exception {
            assertThat(tasks.genericTypeArguments(Inventory.class.getField("label"))).isEmpty();
        }

        @Test
        @DisplayName("a primitive field → empty list")
        void primitiveField() throws Exception {
            assertThat(tasks.genericTypeArguments(Dog.class.getDeclaredField("age"))).isEmpty();
        }
    }
}
