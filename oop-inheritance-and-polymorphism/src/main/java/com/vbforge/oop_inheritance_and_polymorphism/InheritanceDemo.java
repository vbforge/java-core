package com.vbforge.oop_inheritance_and_polymorphism;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * InheritanceDemo — annotated reference for class inheritance in Java.
 *
 * <p>Read top-to-bottom. Each section shows one rule of inheritance with concrete
 * examples and a note explaining <em>why</em> the language behaves that way.
 *
 * <p>Topics:
 * <ol>
 *   <li>extends, IS-A and the {@code Object} root — what a subclass inherits</li>
 *   <li>Overriding — {@code @Override}, covariant returns, widening access</li>
 *   <li>The {@code super} keyword — extending a method instead of replacing it</li>
 *   <li>Constructor chaining — the order in which an object is built</li>
 *   <li>The constructor trap — calling an overridable method from a constructor</li>
 *   <li>Hiding — fields and static methods are NOT overridden</li>
 *   <li>{@code final} and composition — when NOT to inherit</li>
 * </ol>
 */
public class InheritanceDemo {

    public static void main(String[] args) {
        extendsDemo();
        System.out.println("---");
        overridingDemo();
        System.out.println("---");
        superKeywordDemo();
        System.out.println("---");
        constructorChainingDemo();
        System.out.println("---");
        constructorTrapDemo();
        System.out.println("---");
        hidingDemo();
        System.out.println("---");
        finalAndCompositionDemo();
    }

    // ── Shared model for sections 1, 2, 3 and 7 ───────────────────────────────

    /** Root of a small hierarchy: Animal ← Dog ← Puppy, and Animal ← Fish. */
    static class Animal {
        protected int energy = 10;          // protected: visible to subclasses and same package
        private final String name;          // private: NOT accessible in subclasses

        Animal(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public String sound() {
            return "...";
        }

        public String describe() {
            return name + " says " + sound();      // sound() is a VIRTUAL call (see section 3)
        }

        Animal offspring(String childName) {
            return new Animal(childName);
        }

        protected String hook() {
            return "Animal.hook";
        }

        /** final: no subclass may override this method (section 7). */
        public final String species() {
            return getClass().getSimpleName();
        }
    }

    static class Dog extends Animal {
        Dog(String name) {
            super(name);                                    // must be the first statement
        }

        @Override
        public String sound() {
            return "Woof";
        }

        @Override
        Dog offspring(String childName) {                   // covariant return: Animal → Dog
            return new Dog(childName);
        }

        @Override
        public String hook() {                              // access WIDENED: protected → public
            return "Dog.hook";
        }

        String fetch() {                                    // a member only Dog has
            return getName() + " fetches the ball";
        }

        int tire() {
            energy -= 3;                                    // protected field: allowed in a subclass
            return energy;
        }
    }

    /** Fish does not override anything — it inherits Animal's behaviour as-is. */
    static class Fish extends Animal {
        Fish(String name) {
            super(name);
        }
    }

    static class Puppy extends Dog {
        Puppy(String name) {
            super(name);
        }

        @Override
        public String sound() {
            return super.sound() + " (squeaky)";           // extends Dog.sound(), does not replace it
        }

        @Override
        public String describe() {
            return super.describe() + "!";
        }
    }

    // ── 1. extends, IS-A and the Object root ──────────────────────────────────

    /**
     * {@code class Dog extends Animal} means "a Dog IS-A Animal": the subclass inherits every
     * non-private member of the superclass and may add its own.
     *
     * <p>WHY this matters: inheritance is Java's mechanism for reusing code AND for declaring
     * a type relationship. Anything that accepts an {@code Animal} accepts a {@code Dog}.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>A class has exactly ONE direct superclass (single inheritance)</li>
     *   <li>Every class without an explicit {@code extends} extends {@link Object}</li>
     *   <li>{@code private} members exist in the subclass object but are not accessible
     *       by name — use the parent's public/protected API</li>
     *   <li>{@code protected} members are accessible in subclasses (and the same package)</li>
     * </ul>
     *
     * <p>Common gotcha: "private is not inherited" is a statement about <em>access</em>,
     * not about memory — a Dog object still carries the parent's private {@code name} field.
     */
    static void extendsDemo() {
        System.out.println("=== extends / IS-A ===");

        Dog rex = new Dog("Rex");
        System.out.println("inherited getName()   : " + rex.getName());                 // Rex
        System.out.println("inherited species()   : " + rex.species());                 // Dog
        System.out.println("Dog-only fetch()      : " + rex.fetch());                   // Rex fetches the ball
        System.out.println("protected energy      : " + rex.tire());                    // 7
        System.out.println("rex instanceof Animal : " + (rex instanceof Animal));       // true
        System.out.println("Dog's superclass      : " + Dog.class.getSuperclass().getSimpleName());    // Animal
        System.out.println("Animal's superclass   : " + Animal.class.getSuperclass().getSimpleName()); // Object
    }

    // ── 2. Overriding ─────────────────────────────────────────────────────────

    /**
     * A subclass <em>overrides</em> an inherited instance method by declaring one with the
     * same name and parameter types. Which version runs is decided at RUNTIME by the
     * actual class of the object, not by the type of the variable.
     *
     * <p>WHY this matters: overriding lets a subclass specialise behaviour while callers keep
     * using the parent type.
     *
     * <p>Overriding rules shown here:
     * <ul>
     *   <li>{@code @Override} asks the compiler to verify you really override something —
     *       a typo such as {@code sond()} becomes a compile error instead of a silent new method</li>
     *   <li>Covariant return: the overriding method may return a SUBTYPE
     *       ({@code Animal offspring()} → {@code Dog offspring()})</li>
     *   <li>Access may be widened ({@code protected} → {@code public}) but never narrowed</li>
     *   <li>An inherited method that is not overridden behaves exactly as in the parent</li>
     * </ul>
     *
     * <p>Common gotcha: a method with the same name but DIFFERENT parameters is an overload,
     * not an override — without {@code @Override} nothing warns you.
     */
    static void overridingDemo() {
        System.out.println("=== Overriding ===");

        Animal pet = new Dog("Rex");                        // static type Animal, runtime type Dog
        System.out.println("pet.sound()           : " + pet.sound());                    // Woof
        System.out.println("fish.sound()          : " + new Fish("Nemo").sound());       // ...
        System.out.println("pet.describe()        : " + pet.describe());                 // Rex says Woof

        Dog puppy = new Dog("Rex").offspring("Bit");        // covariant: no cast to Dog needed
        System.out.println("offspring is a Dog    : " + puppy.fetch());                  // Bit fetches the ball
        System.out.println("fish offspring class  : " + new Fish("Nemo").offspring("Dot").getClass().getSimpleName()); // Animal

        System.out.println("Animal.hook modifiers : " + hookModifiers(Animal.class));    // protected
        System.out.println("Dog.hook modifiers    : " + hookModifiers(Dog.class));       // public
    }

    /** Reads the declared modifiers of {@code hook()} so the widening rule can be asserted. */
    static String hookModifiers(Class<?> type) {
        try {
            return Modifier.toString(type.getDeclaredMethod("hook").getModifiers());
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    // ── 3. The super keyword ──────────────────────────────────────────────────

    /**
     * {@code super.method()} calls the parent's version of an overridden method;
     * {@code super(args)} calls a parent constructor.
     *
     * <p>WHY this matters: most overrides want to ADD to the parent's behaviour, not rewrite it.
     * {@code super} lets you reuse the parent logic instead of copy-pasting it.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>{@code super.sound()} runs {@code Dog.sound()} even though this object is a Puppy</li>
     *   <li>You can only reach ONE level up — {@code super.super.x()} does not compile</li>
     *   <li>{@code super(...)} in a constructor must be the very first statement</li>
     * </ul>
     *
     * <p>Common gotcha: {@code super.describe()} switches off the override for the
     * <em>call itself</em>, but NOT for the virtual calls made inside the parent's method.
     * {@code Animal.describe()} calls {@code sound()}, and {@code sound()} still resolves to
     * {@code Puppy.sound()} because the object is a Puppy.
     */
    static void superKeywordDemo() {
        System.out.println("=== super ===");

        Puppy bo = new Puppy("Bo");
        System.out.println("Puppy.sound()         : " + bo.sound());          // Woof (squeaky)
        System.out.println("Puppy.describe()      : " + bo.describe());       // Bo says Woof (squeaky)!
        System.out.println("name via super(name)  : " + bo.getName());        // Bo
    }

    // ── 4. Constructor chaining ───────────────────────────────────────────────

    /** Three-level chain that records the order in which its constructors and initialisers run. */
    static class Base {
        protected final List<String> log;

        Base(List<String> log) {
            this.log = log;
            log.add("Base()");
        }
    }

    static class Middle extends Base {
        {
            log.add("Middle initializer block");          // runs after super(...), before the ctor body
        }

        Middle(List<String> log) {
            super(log);
            log.add("Middle()");
        }
    }

    static class Leaf extends Middle {
        Leaf(List<String> log) {
            super(log);
            log.add("Leaf()");
        }

        Leaf(List<String> log, String tag) {
            this(log);                                      // delegate to the constructor above
            log.add("Leaf(" + tag + ")");
        }
    }

    /** Counts how many times its constructor ran — proves the implicit {@code super()} call. */
    static class Counted {
        static int instances;

        Counted() {
            instances++;
        }
    }

    /** Declares NO constructor: the compiler generates one that calls {@code super()}. */
    static class CountedChild extends Counted {
    }

    /**
     * Building an object always starts at the TOP of the hierarchy: the parent constructor runs
     * to completion before the child's field initialisers, initialiser blocks and constructor body.
     *
     * <p>WHY this matters: the child may rely on the parent part of the object being fully
     * initialised by the time the child's code runs.
     *
     * <p>Order for {@code new Leaf(log)}:
     * <ol>
     *   <li>{@code Leaf(...)} immediately delegates to {@code Middle(...)}, which delegates to {@code Base(...)}</li>
     *   <li>{@code Base} body runs, then {@code Middle}'s initialiser block, then {@code Middle} body, then {@code Leaf} body</li>
     * </ol>
     *
     * <p>Other rules shown here:
     * <ul>
     *   <li>If you write no {@code super(...)} call, the compiler inserts {@code super()}</li>
     *   <li>{@code this(...)} delegates to another constructor of the SAME class (also first statement,
     *       so {@code this(...)} and {@code super(...)} can never appear together)</li>
     * </ul>
     *
     * <p>Common gotcha: if the parent has no no-arg constructor, every child constructor MUST call
     * {@code super(args)} explicitly — otherwise the code does not compile.
     */
    static void constructorChainingDemo() {
        System.out.println("=== Constructor chaining ===");

        System.out.println("new Leaf(log)         : " + constructionLog());
        System.out.println("new Leaf(log, \"x\")    : " + constructionLogWithThis());

        int before = Counted.instances;
        new CountedChild();
        System.out.println("implicit super() ran  : " + (Counted.instances - before) + " time(s)");   // 1
    }

    static List<String> constructionLog() {
        List<String> log = new ArrayList<>();
        new Leaf(log);
        return log;
    }

    static List<String> constructionLogWithThis() {
        List<String> log = new ArrayList<>();
        new Leaf(log, "x");
        return log;
    }

    // ── 5. The constructor trap ───────────────────────────────────────────────

    static class Greeter {
        protected final String seenByConstructor;

        Greeter() {
            seenByConstructor = greeting();                // DANGER: calls an overridable method
        }

        String greeting() {
            return "Hello";
        }
    }

    static class LoudGreeter extends Greeter {
        private final String suffix;

        LoudGreeter(String suffix) {
            super();                                        // Greeter() runs BEFORE the line below
            this.suffix = suffix;
        }

        @Override
        String greeting() {
            return "Hello" + suffix;
        }
    }

    /**
     * Calling an overridable method from a constructor runs the SUBCLASS version before the
     * subclass has been initialised.
     *
     * <p>WHY this matters: it combines two rules you already know — parent constructors run first
     * (section 4) and virtual calls use the runtime class (section 2). The subclass's fields are
     * still at their defaults ({@code null}, {@code 0}, {@code false}) when its method is invoked.
     *
     * <p>Common gotcha: the bug does not show up when you read the parent class alone, and the
     * compiler gives no warning. Rule of thumb: constructors should only call {@code private},
     * {@code static} or {@code final} methods.
     */
    static void constructorTrapDemo() {
        System.out.println("=== Constructor trap ===");

        System.out.println("seen by constructor   : " + greetingSeenByConstructor());   // Hellonull
        System.out.println("after construction    : " + greetingAfterConstruction());   // Hello!!!
    }

    static String greetingSeenByConstructor() {
        return new LoudGreeter("!!!").seenByConstructor;
    }

    static String greetingAfterConstruction() {
        return new LoudGreeter("!!!").greeting();
    }

    // ── 6. Hiding: fields and static methods ──────────────────────────────────

    static class Vehicle {
        String label = "vehicle-field";

        static String kind() {
            return "Vehicle static";
        }

        String who() {
            return "Vehicle instance";
        }

        String getLabel() {
            return label;
        }
    }

    static class Car extends Vehicle {
        String label = "car-field";                         // HIDES Vehicle.label — a second, separate field

        static String kind() {                              // HIDES Vehicle.kind() — @Override would not compile
            return "Car static";
        }

        @Override
        String who() {
            return "Car instance";
        }

        @Override
        String getLabel() {
            return label;                                   // refers to Car.label
        }
    }

    /**
     * Only INSTANCE METHODS are overridden (resolved at runtime). Fields and static methods are
     * HIDDEN: the compiler picks the one that matches the declared type of the variable.
     *
     * <p>WHY this matters: it explains surprising results such as {@code vehicle.label} returning
     * the parent's value even though the object is a Car.
     *
     * <p>Common gotcha: never redeclare a field with the same name as a parent field, and never
     * call static methods through an instance — both compile (the second with a warning) and
     * both silently use the declared type.
     */
    static void hidingDemo() {
        System.out.println("=== Hiding ===");

        Vehicle v = new Car();                              // static type Vehicle, runtime type Car
        System.out.println("v.label (field)       : " + v.label);                      // vehicle-field
        System.out.println("((Car) v).label       : " + ((Car) v).label);              // car-field
        System.out.println("v.getLabel()          : " + v.getLabel());                 // car-field  (method → runtime type)
        System.out.println("v.who()               : " + v.who());                      // Car instance
        System.out.println("static via instance   : " + staticViaInstanceRef());       // Vehicle static
        System.out.println("Car.kind()            : " + Car.kind());                   // Car static
    }

    static String fieldViaParentReference() {
        Vehicle v = new Car();
        return v.label;
    }

    static String fieldViaChildReference() {
        Car c = new Car();
        return c.label;
    }

    @SuppressWarnings("static")
    static String staticViaInstanceRef() {
        Vehicle v = new Car();
        return v.kind();                                    // compiles, but resolves to Vehicle.kind()
    }

    // ── 7. final and composition ──────────────────────────────────────────────

    static class Engine {
        private final int horsepower;

        Engine(int horsepower) {
            this.horsepower = horsepower;
        }

        String start() {
            return "Engine(" + horsepower + "hp) started";
        }
    }

    /** HAS-A Engine — a truck uses an engine, it is not one. */
    static class Truck {
        private final Engine engine;

        Truck(Engine engine) {
            this.engine = engine;
        }

        String start() {
            return "Truck: " + engine.start();
        }
    }

    /**
     * {@code final} switches inheritance off: a {@code final} method cannot be overridden and a
     * {@code final} class ({@link String}, for example) cannot be extended. Inheritance is also
     * the tightest coupling Java offers, so prefer COMPOSITION (HAS-A) unless the IS-A test
     * ("can a Dog be used everywhere an Animal is expected?") really holds.
     *
     * <p>WHY this matters: a subclass depends on the parent's implementation details. The JDK's own
     * {@link Stack} extends {@link java.util.Vector}, so it inherits {@code add(index, e)} and
     * {@code get(index)} — operations that let callers break the LIFO promise a stack should make.
     *
     * <p>Common gotcha: "I want the code from class X" is NOT a reason to extend X. Extend only to
     * model IS-A; reuse code through fields (composition) instead.
     */
    static void finalAndCompositionDemo() {
        System.out.println("=== final and composition ===");

        System.out.println("species() is final    : " + Modifier.isFinal(speciesModifiers()));   // true
        System.out.println("String is final       : " + Modifier.isFinal(String.class.getModifiers())); // true
        System.out.println("Truck (composition)   : " + new Truck(new Engine(300)).start());
        System.out.println("Truck IS-A Engine?    : " + Engine.class.isAssignableFrom(Truck.class));    // false
        System.out.println("Stack with a backdoor : " + stackWithBackdoor());   // [99, 1, 2]
    }

    static int speciesModifiers() {
        try {
            return Animal.class.getDeclaredMethod("species").getModifiers();
        } catch (NoSuchMethodException e) {
            throw new IllegalStateException(e);
        }
    }

    /** Pushes 1 and 2, then uses an INHERITED Vector method to slip 99 under the "bottom". */
    static Stack<Integer> stackWithBackdoor() {
        Stack<Integer> stack = new Stack<>();
        stack.push(1);
        stack.push(2);
        stack.add(0, 99);                                   // not a stack operation — inherited from Vector
        return stack;
    }
}
