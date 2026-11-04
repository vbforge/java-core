package com.vbforge.reflection_api;

import com.vbforge.reflection_api.ClassIntrospectionDemo.Animal;
import com.vbforge.reflection_api.ClassIntrospectionDemo.Dog;
import com.vbforge.reflection_api.ClassIntrospectionDemo.Inventory;
import com.vbforge.reflection_api.ClassIntrospectionDemo.Named;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ClassIntrospectionDemo — API contracts")
class ClassIntrospectionDemoTest {

    private static List<String> sortedNames(Field[] fields) {
        List<String> names = new ArrayList<String>();
        for (Field f : fields) {
            names.add(f.getName());
        }
        Collections.sort(names);
        return names;
    }

    @Nested
    @DisplayName("1. Getting a Class object")
    class ClassObjects {

        @Test
        @DisplayName("literal, getClass() and forName() yield the very same Class object")
        void threeWaysSameObject() throws Exception {
            Class<?> byName = Class.forName("com.vbforge.reflection_api.ClassIntrospectionDemo$Dog");

            assertThat(new Dog().getClass()).isSameAs(Dog.class);
            assertThat(byName).isSameAs(Dog.class);
        }

        @Test
        @DisplayName("getClass() reports the runtime type, not the declared type")
        void runtimeType() {
            Animal pet = new Dog();

            assertThat(pet.getClass()).isEqualTo(Dog.class);
            assertThat(pet.getClass()).isNotEqualTo(Animal.class);
        }

        @Test
        @DisplayName("getName uses '$', getCanonicalName uses '.', getSimpleName has no package")
        void names() {
            assertThat(Dog.class.getName())
                    .isEqualTo("com.vbforge.reflection_api.ClassIntrospectionDemo$Dog");
            assertThat(Dog.class.getCanonicalName())
                    .isEqualTo("com.vbforge.reflection_api.ClassIntrospectionDemo.Dog");
            assertThat(Dog.class.getSimpleName()).isEqualTo("Dog");
        }

        @Test
        @DisplayName("forName needs the '$' form for nested classes")
        void forNameNeedsBinaryName() {
            assertThatThrownBy(() -> Class.forName(Dog.class.getCanonicalName()))
                    .isInstanceOf(ClassNotFoundException.class);
        }

        @Test
        @DisplayName("forName of an unknown class throws ClassNotFoundException")
        void forNameUnknown() {
            assertThatThrownBy(() -> Class.forName("com.vbforge.DoesNotExist"))
                    .isInstanceOf(ClassNotFoundException.class);
        }

        @Test
        @DisplayName("Class flags identify primitives, arrays, interfaces, enums and annotations")
        void flags() {
            assertThat(int.class.isPrimitive()).isTrue();
            assertThat(Integer.class.isPrimitive()).isFalse();
            assertThat(int[].class.isArray()).isTrue();
            assertThat(Named.class.isInterface()).isTrue();
            assertThat(Thread.State.class.isEnum()).isTrue();
            assertThat(Deprecated.class.isAnnotation()).isTrue();
        }
    }

    @Nested
    @DisplayName("2. Hierarchy")
    class Hierarchy {

        @Test
        @DisplayName("getSuperclass walks one step up and ends with null")
        void superclassChain() {
            assertThat(Dog.class.getSuperclass()).isEqualTo(Animal.class);
            assertThat(Animal.class.getSuperclass()).isEqualTo(Object.class);
            assertThat(Object.class.getSuperclass()).isNull();
        }

        @Test
        @DisplayName("interfaces and primitives have no superclass")
        void noSuperclass() {
            assertThat(Named.class.getSuperclass()).isNull();
            assertThat(int.class.getSuperclass()).isNull();
        }

        @Test
        @DisplayName("getInterfaces returns only directly declared interfaces")
        void directInterfacesOnly() {
            assertThat(Dog.class.getInterfaces()).containsExactly(Comparable.class);
            assertThat(Animal.class.getInterfaces()).containsExactly(Named.class);
        }

        @Test
        @DisplayName("isAssignableFrom: Animal can be assigned from Dog, not the other way round")
        void assignableDirection() {
            assertThat(Animal.class.isAssignableFrom(Dog.class)).isTrue();
            assertThat(Dog.class.isAssignableFrom(Animal.class)).isFalse();
            assertThat(Named.class.isAssignableFrom(Dog.class)).isTrue();
        }

        @Test
        @DisplayName("isInstance is the reflective instanceof")
        void isInstance() {
            assertThat(Named.class.isInstance(new Dog())).isTrue();
            assertThat(Dog.class.isInstance("text")).isFalse();
            assertThat(Dog.class.isInstance(null)).isFalse();
        }

        @Test
        @DisplayName("array classes expose their component type")
        void componentType() {
            assertThat(int[].class.getComponentType()).isEqualTo(int.class);
            assertThat(String.class.getComponentType()).isNull();
        }
    }

    @Nested
    @DisplayName("3. Modifiers")
    class Modifiers {

        @Test
        @DisplayName("Modifier.toString renders the modifiers of a class")
        void modifierToString() {
            assertThat(Modifier.toString(Dog.class.getModifiers())).isEqualTo("public static");
            assertThat(Modifier.toString(Animal.class.getModifiers())).isEqualTo("public abstract static");
        }

        @Test
        @DisplayName("abstract classes and interfaces are both abstract")
        void abstractness() {
            assertThat(Modifier.isAbstract(Animal.class.getModifiers())).isTrue();
            assertThat(Modifier.isAbstract(Named.class.getModifiers())).isTrue();
            assertThat(Modifier.isAbstract(Dog.class.getModifiers())).isFalse();
        }

        @Test
        @DisplayName("field modifiers: LEGS is public static final")
        void fieldModifiers() throws Exception {
            Field legs = Dog.class.getDeclaredField("LEGS");

            assertThat(Modifier.isPublic(legs.getModifiers())).isTrue();
            assertThat(Modifier.isStatic(legs.getModifiers())).isTrue();
            assertThat(Modifier.isFinal(legs.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("method modifiers: secret() is private, puppy() is static")
        void methodModifiers() throws Exception {
            Method secret = Dog.class.getDeclaredMethod("secret");
            Method puppy = Dog.class.getMethod("puppy", String.class);

            assertThat(Modifier.isPrivate(secret.getModifiers())).isTrue();
            assertThat(Modifier.isStatic(puppy.getModifiers())).isTrue();
        }
    }

    @Nested
    @DisplayName("4. Fields")
    class Fields {

        @Test
        @DisplayName("getFields returns public fields only")
        void publicFields() {
            assertThat(sortedNames(Dog.class.getFields())).containsExactly("LEGS", "nickname");
        }

        @Test
        @DisplayName("getDeclaredFields returns every field of exactly this class")
        void declaredFields() {
            assertThat(sortedNames(Dog.class.getDeclaredFields()))
                    .containsExactly("LEGS", "age", "nickname");
        }

        @Test
        @DisplayName("an inherited protected field is in neither list for the sub-class")
        void inheritedProtectedFieldInvisible() {
            assertThat(sortedNames(Dog.class.getFields())).doesNotContain("name");
            assertThat(sortedNames(Dog.class.getDeclaredFields())).doesNotContain("name");
            assertThat(sortedNames(Animal.class.getDeclaredFields())).containsExactly("name");
        }

        @Test
        @DisplayName("getField finds public fields, getDeclaredField finds private ones")
        void lookupByName() throws Exception {
            assertThat(Dog.class.getField("nickname").getName()).isEqualTo("nickname");
            assertThat(Dog.class.getDeclaredField("age").getType()).isEqualTo(int.class);
            assertThatThrownBy(() -> Dog.class.getField("age"))
                    .isInstanceOf(NoSuchFieldException.class);
        }
    }

    @Nested
    @DisplayName("5. Methods")
    class Methods {

        private List<String> publicMethodNames() {
            List<String> names = new ArrayList<String>();
            for (Method m : Dog.class.getMethods()) {
                names.add(m.getName());
            }
            return names;
        }

        @Test
        @DisplayName("getMethods includes inherited public methods and those of Object")
        void getMethodsIncludesInherited() {
            assertThat(publicMethodNames()).contains("sound", "getAge", "puppy", "getName", "hashCode", "getClass");
        }

        @Test
        @DisplayName("getMethods excludes private methods")
        void getMethodsExcludesPrivate() {
            assertThat(publicMethodNames()).doesNotContain("secret");
        }

        @Test
        @DisplayName("getDeclaredMethods includes private methods but not inherited ones")
        void getDeclaredMethods() {
            List<String> declared = new ArrayList<String>();
            for (Method m : Dog.class.getDeclaredMethods()) {
                declared.add(m.getName());
            }

            assertThat(declared).contains("secret", "sound", "puppy");
            assertThat(declared).doesNotContain("getName", "hashCode");
        }

        @Test
        @DisplayName("Comparable<Dog> produces a hidden bridge method compareTo(Object)")
        void bridgeMethod() {
            int compareTo = 0;
            int bridges = 0;
            for (Method m : Dog.class.getDeclaredMethods()) {
                if (m.getName().equals("compareTo")) {
                    compareTo++;
                    if (m.isBridge()) {
                        bridges++;
                        assertThat(m.getParameterTypes()).containsExactly(Object.class);
                    }
                }
            }

            assertThat(compareTo).isEqualTo(2);
            assertThat(bridges).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("6. Constructors")
    class Constructors {

        @Test
        @DisplayName("getConstructors returns only the public constructors")
        void publicConstructors() {
            assertThat(Dog.class.getConstructors()).hasSize(2);
        }

        @Test
        @DisplayName("getDeclaredConstructors also returns the private one")
        void declaredConstructors() {
            Constructor<?>[] all = Dog.class.getDeclaredConstructors();
            int privateOnes = 0;
            for (Constructor<?> c : all) {
                if (Modifier.isPrivate(c.getModifiers())) {
                    privateOnes++;
                }
            }

            assertThat(all).hasSize(3);
            assertThat(privateOnes).isEqualTo(1);
        }

        @Test
        @DisplayName("constructors belong to one class: Animal's protected constructor is not Dog's")
        void notInherited() {
            assertThat(Animal.class.getConstructors()).isEmpty();
            assertThat(Animal.class.getDeclaredConstructors()).hasSize(1);
            assertThat(Dog.class.getConstructors()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("7. Generics at runtime")
    class Generics {

        @Test
        @DisplayName("erasure: ArrayList<String> and ArrayList<Integer> share one Class")
        void erasure() {
            assertThat(new ArrayList<String>().getClass())
                    .isSameAs(new ArrayList<Integer>().getClass());
        }

        @Test
        @DisplayName("getType returns the raw type, getGenericType keeps the arguments")
        void rawVersusGeneric() throws Exception {
            Field tags = Inventory.class.getField("tags");

            assertThat(tags.getType()).isEqualTo(List.class);
            assertThat(tags.getGenericType().getTypeName()).isEqualTo("java.util.List<java.lang.String>");
        }

        @Test
        @DisplayName("ParameterizedType exposes the actual type arguments in order")
        void typeArguments() throws Exception {
            Type generic = Inventory.class.getField("counts").getGenericType();

            assertThat(generic instanceof ParameterizedType).isTrue();
            Type[] args = ((ParameterizedType) generic).getActualTypeArguments();
            assertThat(args).containsExactly(String.class, Integer.class);
        }

        @Test
        @DisplayName("a non-generic field is not a ParameterizedType")
        void nonGenericField() throws Exception {
            Type type = Inventory.class.getField("label").getGenericType();

            assertThat(type instanceof ParameterizedType).isFalse();
            assertThat(type).isEqualTo(String.class);
        }

        @Test
        @DisplayName("getGenericInterfaces keeps Comparable<Dog>")
        void genericInterfaces() {
            Type[] interfaces = Dog.class.getGenericInterfaces();

            assertThat(interfaces).hasSize(1);
            assertThat(interfaces[0].getTypeName())
                    .isEqualTo("java.lang.Comparable<com.vbforge.reflection_api.ClassIntrospectionDemo$Dog>");
        }
    }
}
