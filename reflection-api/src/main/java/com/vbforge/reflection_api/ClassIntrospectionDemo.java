package com.vbforge.reflection_api;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Class introspection — asking a class "what are you made of?" at runtime.
 *
 * <p>Everything in the reflection API starts from a {@link Class} object.
 * From it you can discover fields, methods, constructors, modifiers, the
 * super-class chain, implemented interfaces and even generic signatures —
 * without knowing the class at compile time.
 *
 * <p>The fixture types ({@link Named}, {@link Animal}, {@link Dog},
 * {@link Inventory}) are nested here so the demo, its tests and the task tests
 * share them.
 */
public class ClassIntrospectionDemo {

    // ── Fixtures ────────────────────────────────────────────────────────

    /** A tiny interface used to show interface discovery. */
    public interface Named {
        String getName();
    }

    /** Abstract base class: protected field, abstract method. */
    public static abstract class Animal implements Named {
        protected String name;

        protected Animal(String name) {
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public abstract String sound();
    }

    /** Concrete class with every visibility level and a generic interface. */
    public static class Dog extends Animal implements Comparable<Dog> {
        public static final int LEGS = 4;
        public String nickname;
        private int age;

        public Dog() {
            this("Rex", 1);
        }

        public Dog(String name, int age) {
            super(name);
            this.age = age;
        }

        private Dog(String name) {
            this(name, 0);
        }

        @Override
        public String sound() {
            return "Woof";
        }

        @Override
        public int compareTo(Dog other) {
            return Integer.compare(age, other.age);
        }

        public int getAge() {
            return age;
        }

        public static Dog puppy(String name) {
            return new Dog(name, 0);
        }

        private String secret() {
            return "bone";
        }
    }

    /** Fixture with generic fields — used for the generics section. */
    public static class Inventory {
        public List<String> tags;
        public Map<String, Integer> counts;
        public String label;
    }

    // ── Demo entry point ────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        classObjectsDemo();
        System.out.println("---");
        hierarchyDemo();
        System.out.println("---");
        modifiersDemo();
        System.out.println("---");
        fieldsDemo();
        System.out.println("---");
        methodsDemo();
        System.out.println("---");
        constructorsDemo();
        System.out.println("---");
        genericsDemo();
    }

    // ── 1. Getting a Class object ───────────────────────────────────────

    /**
     * <b>Three ways to obtain a {@code Class<?>}.</b>
     *
     * <ul>
     *   <li>{@code Dog.class} — a literal; checked at compile time, works for primitives too</li>
     *   <li>{@code obj.getClass()} — the <em>runtime</em> type of an instance</li>
     *   <li>{@code Class.forName("pkg.Name")} — by string; the only option when the
     *       name comes from config, a plugin file or a framework</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> frameworks (Spring, Jackson, JUnit, Hibernate) receive
     * class names or instances and must work with types they have never seen.
     *
     * <p><b>Complexity:</b>
     * <ul>
     *   <li>literal or {@code getClass()} — O(1)</li>
     *   <li>{@code forName} — may trigger class loading, which is far more expensive the first time</li>
     * </ul>
     *
     * <p><b>Gotcha:</b> {@code getName()} of a nested class uses {@code $}
     * ({@code Outer$Inner}) while {@code getCanonicalName()} uses {@code .}
     * ({@code Outer.Inner}) — {@code forName} needs the {@code $} form.
     */
    static void classObjectsDemo() throws ClassNotFoundException {
        Class<?> byLiteral = Dog.class;
        Animal pet = new Dog();                       // static type Animal, runtime type Dog
        Class<?> byInstance = pet.getClass();
        Class<?> byName = Class.forName(Dog.class.getName());

        System.out.println("literal == getClass() == forName : "
                + (byLiteral == byInstance && byInstance == byName));
        System.out.println("getName()          : " + byLiteral.getName());
        System.out.println("getSimpleName()    : " + byLiteral.getSimpleName());
        System.out.println("getCanonicalName() : " + byLiteral.getCanonicalName());

        System.out.println("int.class.isPrimitive()   : " + int.class.isPrimitive());
        System.out.println("int[].class.isArray()     : " + int[].class.isArray());
        System.out.println("Named.class.isInterface() : " + Named.class.isInterface());
        System.out.println("Thread.State is enum      : " + Thread.State.class.isEnum());
        System.out.println("Deprecated is annotation  : " + Deprecated.class.isAnnotation());

        try {
            Class.forName("com.vbforge.DoesNotExist");
        } catch (ClassNotFoundException e) {
            System.out.println("forName(unknown) -> ClassNotFoundException");
        }
    }

    // ── 2. Hierarchy ────────────────────────────────────────────────────

    /**
     * <b>Walking the type hierarchy.</b>
     *
     * <ul>
     *   <li>{@code getSuperclass()} — one step up; {@code null} for {@code Object},
     *       interfaces, primitives and {@code void}</li>
     *   <li>{@code getInterfaces()} — only the <em>directly</em> declared interfaces</li>
     *   <li>{@code isAssignableFrom} — the reflective {@code instanceof} for types</li>
     *   <li>{@code isInstance(obj)} — the reflective {@code instanceof} for objects</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> dependency injection, serializers and mappers decide
     * what to do by asking "is this type assignable to {@code Collection}?".
     *
     * <p><b>Gotcha:</b> the argument order of {@code isAssignableFrom} is easy to flip:
     * {@code Animal.class.isAssignableFrom(Dog.class)} is <b>true</b> (read:
     * "Animal can be assigned from Dog"), the reverse is false.
     */
    static void hierarchyDemo() {
        System.out.println("Dog superclass               : " + Dog.class.getSuperclass().getSimpleName());
        System.out.println("Animal superclass            : " + Animal.class.getSuperclass().getSimpleName());
        System.out.println("Object superclass            : " + Object.class.getSuperclass());
        System.out.println("Named (interface) superclass : " + Named.class.getSuperclass());
        System.out.println("Dog interfaces               : " + Arrays.toString(Dog.class.getInterfaces()));
        System.out.println("Animal.isAssignableFrom(Dog) : " + Animal.class.isAssignableFrom(Dog.class));
        System.out.println("Dog.isAssignableFrom(Animal) : " + Dog.class.isAssignableFrom(Animal.class));
        System.out.println("Named.isInstance(new Dog())  : " + Named.class.isInstance(new Dog()));
        System.out.println("int[] component type         : " + int[].class.getComponentType());
    }

    // ── 3. Modifiers ────────────────────────────────────────────────────

    /**
     * <b>Modifiers are packed into an {@code int} bit mask.</b>
     *
     * <p>{@code getModifiers()} exists on {@code Class}, {@code Field},
     * {@code Method} and {@code Constructor}. Never inspect the bits by hand —
     * use the {@link Modifier} helpers ({@code isPublic}, {@code isStatic},
     * {@code isAbstract}, {@code isFinal}, …) or {@code Modifier.toString}.
     *
     * <p><b>Why it matters:</b> a framework must skip {@code static} fields when
     * serialising, refuse to instantiate {@code abstract} classes, and know
     * which members need {@code setAccessible(true)}.
     *
     * <p><b>Gotcha:</b> a static nested class reports {@code static} in its modifiers,
     * and an interface is automatically {@code abstract}.
     */
    static void modifiersDemo() throws NoSuchFieldException, NoSuchMethodException {
        System.out.println("Dog modifiers    : " + Modifier.toString(Dog.class.getModifiers()));
        System.out.println("Animal modifiers : " + Modifier.toString(Animal.class.getModifiers()));
        System.out.println("Animal abstract  : " + Modifier.isAbstract(Animal.class.getModifiers()));
        System.out.println("Named abstract   : " + Modifier.isAbstract(Named.class.getModifiers()));

        Field legs = Dog.class.getDeclaredField("LEGS");
        System.out.println("LEGS modifiers   : " + Modifier.toString(legs.getModifiers()));

        Method secret = Dog.class.getDeclaredMethod("secret");
        System.out.println("secret() private : " + Modifier.isPrivate(secret.getModifiers()));
    }

    // ── 4. Fields ───────────────────────────────────────────────────────

    /**
     * <b>{@code getFields()} vs {@code getDeclaredFields()}.</b>
     *
     * <ul>
     *   <li>{@code getFields()} — <em>public</em> fields, <em>including inherited</em> ones</li>
     *   <li>{@code getDeclaredFields()} — <em>all</em> fields (any visibility),
     *       but <em>only</em> those declared by this exact class</li>
     * </ul>
     *
     * <p>To collect every field of an object you must walk {@code getSuperclass()}
     * yourself and call {@code getDeclaredFields()} at each level.
     *
     * <p><b>Why it matters:</b> object mappers and debuggers need private state;
     * public-API tools only want the contract.
     *
     * <p><b>Complexity:</b> O(f) per call (f = number of fields); each call returns a
     * fresh copy of the {@code Field} objects.
     *
     * <p><b>Gotcha:</b> the <em>order</em> of the returned array is not specified —
     * sort by name if you need determinism. Also, {@code Animal.name} is
     * {@code protected}, so it is in neither {@code Dog.getFields()} nor
     * {@code Dog.getDeclaredFields()}.
     */
    static void fieldsDemo() {
        System.out.println("Dog.getFields()          : " + sortedNames(Dog.class.getFields()));
        System.out.println("Dog.getDeclaredFields()  : " + sortedNames(Dog.class.getDeclaredFields()));
        System.out.println("Animal.getDeclaredFields : " + sortedNames(Animal.class.getDeclaredFields()));
    }

    private static List<String> sortedNames(Field[] fields) {
        List<String> names = new ArrayList<String>();
        for (Field f : fields) {
            names.add(f.getName());
        }
        Collections.sort(names);
        return names;
    }

    // ── 5. Methods ──────────────────────────────────────────────────────

    /**
     * <b>{@code getMethods()} vs {@code getDeclaredMethods()}.</b>
     *
     * <ul>
     *   <li>{@code getMethods()} — public methods, including inherited ones
     *       <em>and everything from {@code Object}</em> ({@code hashCode}, {@code wait}, …)</li>
     *   <li>{@code getDeclaredMethods()} — every method declared by this class
     *       (private included), nothing inherited</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> test runners find {@code @Test} methods this way;
     * serializers find getters.
     *
     * <p><b>Gotcha — bridge methods:</b> {@code Dog implements Comparable<Dog>}, but at
     * the bytecode level {@code Comparable.compareTo} takes an {@code Object}
     * (type erasure). The compiler therefore generates a hidden
     * <em>bridge</em> {@code compareTo(Object)} next to your {@code compareTo(Dog)}.
     * {@code getDeclaredMethods()} returns <b>both</b>; {@code Method.isBridge()}
     * tells them apart. Filter bridges out when listing "real" methods.
     */
    static void methodsDemo() {
        List<String> publicNames = new ArrayList<String>();
        for (Method m : Dog.class.getMethods()) {
            publicNames.add(m.getName());
        }
        System.out.println("getMethods() has getName (inherited) : " + publicNames.contains("getName"));
        System.out.println("getMethods() has hashCode (Object)   : " + publicNames.contains("hashCode"));
        System.out.println("getMethods() has secret (private)    : " + publicNames.contains("secret"));

        int compareToCount = 0;
        int bridgeCount = 0;
        for (Method m : Dog.class.getDeclaredMethods()) {
            if (m.getName().equals("compareTo")) {
                compareToCount++;
                if (m.isBridge()) {
                    bridgeCount++;
                }
            }
        }
        System.out.println("declared compareTo methods : " + compareToCount);
        System.out.println("  of which bridge methods  : " + bridgeCount);
    }

    // ── 6. Constructors ─────────────────────────────────────────────────

    /**
     * <b>Constructors are not inherited, and come in two flavours.</b>
     *
     * <ul>
     *   <li>{@code getConstructors()} — public constructors only</li>
     *   <li>{@code getDeclaredConstructors()} — every constructor, private included</li>
     * </ul>
     *
     * <p><b>Why it matters:</b> dependency-injection containers pick a constructor
     * by parameter types and create objects without a compile-time {@code new}.
     *
     * <p><b>Gotcha:</b> constructors belong to exactly one class — {@code Dog}
     * never "inherits" {@code Animal}'s constructor, so walking up
     * {@code getSuperclass()} does not help when you need a {@code Dog}.
     */
    static void constructorsDemo() {
        Constructor<?>[] publicOnes = Dog.class.getConstructors();
        Constructor<?>[] all = Dog.class.getDeclaredConstructors();
        System.out.println("public constructors   : " + publicOnes.length);
        System.out.println("declared constructors : " + all.length);
        for (Constructor<?> c : all) {
            System.out.println("  " + Modifier.toString(c.getModifiers())
                    + " params=" + c.getParameterCount());
        }
    }

    // ── 7. Generics at runtime ──────────────────────────────────────────

    /**
     * <b>Erasure removes generics from <em>instances</em>, not from <em>declarations</em>.</b>
     *
     * <p>{@code new ArrayList<String>().getClass()} and
     * {@code new ArrayList<Integer>().getClass()} are the same object — the type
     * argument is gone at runtime. But the compiler stores generic signatures of
     * <em>fields, method parameters, return types and super-types</em> in the class file,
     * and {@code getGenericType()} / {@code getGenericInterfaces()} read them back.
     *
     * <p><b>Why it matters:</b> this is how Jackson knows a field is a
     * {@code List<String>} and not a {@code List<Order>}.
     *
     * <p><b>Gotcha:</b> local variables have no generic metadata at all, and
     * {@code getType()} always returns the <em>raw</em> type ({@code List}) —
     * use {@code getGenericType()} when you need the arguments.
     */
    static void genericsDemo() throws NoSuchFieldException {
        System.out.println("ArrayList<String> and ArrayList<Integer> same class : "
                + (new ArrayList<String>().getClass() == new ArrayList<Integer>().getClass()));

        Field tags = Inventory.class.getField("tags");
        System.out.println("tags.getType()        : " + tags.getType().getName());
        System.out.println("tags.getGenericType() : " + tags.getGenericType().getTypeName());

        Type generic = Inventory.class.getField("counts").getGenericType();
        if (generic instanceof ParameterizedType) {
            for (Type arg : ((ParameterizedType) generic).getActualTypeArguments()) {
                System.out.println("counts type argument  : " + arg.getTypeName());
            }
        }
        System.out.println("Dog generic interfaces: " + Arrays.toString(Dog.class.getGenericInterfaces()));
    }
}
