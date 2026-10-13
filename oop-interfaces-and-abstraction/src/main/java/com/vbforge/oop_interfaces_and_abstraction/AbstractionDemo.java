package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.abstraction.AbstractSensor;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.Checkout;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.DiscountPolicies;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.DiscountPolicy;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.HumiditySensor;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.Sensor;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.TemperatureSensor;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * AbstractionDemo — annotated reference for ABSTRACTION: exposing WHAT something does while
 * hiding HOW it does it.
 *
 * <p>The supporting types live in the {@code abstraction} sub-package: {@code DiscountPolicy},
 * {@code DiscountPolicies} (the factory), the package-private {@code NoDiscount},
 * {@code PercentDiscount} and {@code ThresholdDiscount}, {@code Checkout}, {@code Sensor},
 * {@code AbstractSensor}, {@code TemperatureSensor} and {@code HumiditySensor}.
 *
 * <p>Topics:
 * <ol>
 *   <li>Program to an interface, not to an implementation</li>
 *   <li>Strategy: swap behaviour by injecting a different implementation</li>
 *   <li>Hiding the implementation behind a factory</li>
 *   <li>Small interfaces (interface segregation)</li>
 *   <li>Skeletal implementation: interface + abstract class together</li>
 * </ol>
 */
public class AbstractionDemo {

    public static void main(String[] args) {
        programToInterfaceDemo();
        System.out.println("---");
        strategyDemo();
        System.out.println("---");
        hidingImplementationDemo();
        System.out.println("---");
        interfaceSegregationDemo();
        System.out.println("---");
        skeletalImplementationDemo();
    }

    // ── 1. Program to an interface ────────────────────────────────────────────

    /**
     * Declare variables, parameters and return types with the most general TYPE that offers what you
     * need — usually an interface — and mention the concrete class only where you create the object.
     *
     * <p>WHY this matters: {@code sumFirstTwo(List<Integer>)} works for an {@code ArrayList}, a
     * {@code LinkedList} or any future list. A signature that demands {@code ArrayList} would shut all
     * of them out for no benefit. Returning an interface also lets you hand out a restricted view:
     * {@code Collections.unmodifiableList} gives callers a read-only {@code List}.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>One method, two different list implementations, same result</li>
     *   <li>The unmodifiable view is a LIVE view: it shows later changes to the source but rejects its own changes</li>
     * </ul>
     *
     * <p>Common gotcha: an unmodifiable view is not a copy. If the owner keeps changing the source, the
     * view changes too — copy with {@code new ArrayList<>(source)} when you need a snapshot.
     */
    static void programToInterfaceDemo() {
        System.out.println("=== Program to an interface ===");

        System.out.println("ArrayList             : " + sumFirstTwo(new ArrayList<>(Arrays.asList(4, 5, 6))));    // 9
        System.out.println("LinkedList            : " + sumFirstTwo(new LinkedList<>(Arrays.asList(4, 5, 6))));   // 9

        List<String> source = new ArrayList<>(Arrays.asList("a", "b"));
        List<String> view = readOnlyView(source);
        source.add("c");
        System.out.println("live read-only view   : " + view);                                                   // [a, b, c]
    }

    static int sumFirstTwo(List<Integer> list) {
        return list.get(0) + list.get(1);                   // knows only the List contract
    }

    static List<String> readOnlyView(List<String> source) {
        return Collections.unmodifiableList(source);
    }

    // ── 2. Strategy through injection ─────────────────────────────────────────

    /**
     * The STRATEGY pattern: put a varying rule behind an interface and pass the chosen implementation
     * in from outside. {@code Checkout} never mentions a concrete policy.
     *
     * <p>WHY this matters: new rules (a Black Friday discount) are new classes — or lambdas — not edits
     * to {@code Checkout}. Existing code stays closed for modification and open for extension.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>The same {@code Checkout} and the same prices give different totals with different policies</li>
     *   <li>A policy can also be a LAMBDA, because {@code DiscountPolicy} has one abstract method</li>
     * </ul>
     *
     * <p>Common gotcha: if {@code Checkout} created its policy itself ({@code new PercentDiscount(10)}),
     * you could neither swap it nor test the class in isolation.
     */
    static void strategyDemo() {
        System.out.println("=== Strategy ===");

        System.out.println("no discount           : " + totalWith(DiscountPolicies.none()));                // 200.0
        System.out.println("10% off               : " + totalWith(DiscountPolicies.percent(10)));           // 180.0
        System.out.println("15 off over 100       : " + totalWith(DiscountPolicies.threshold(100, 15)));    // 185.0
        System.out.println("lambda: half price    : " + totalWith(subtotal -> subtotal / 2));               // 100.0
    }

    static double totalWith(DiscountPolicy policy) {
        return new Checkout(policy).total(Arrays.asList(50.0, 150.0));
    }

    // ── 3. Hiding the implementation ──────────────────────────────────────────

    /**
     * The implementation classes ({@code NoDiscount}, {@code PercentDiscount}, ...) are
     * package-private. Outside code can obtain a policy only through {@code DiscountPolicies}, which
     * returns the INTERFACE type.
     *
     * <p>WHY this matters: what callers cannot see, they cannot depend on. The classes can be renamed,
     * merged or replaced without a single caller changing — the interface and the factory are the
     * whole public surface.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>At runtime the object really is a {@code PercentDiscount}...</li>
     *   <li>...but that class is not {@code public}, so this package could not even write
     *       {@code new PercentDiscount(10)}</li>
     * </ul>
     *
     * <p>Common gotcha: abstraction (hiding the HOW behind a contract) and encapsulation (hiding the
     * DATA behind methods) are related but different. Here, no field is involved — it is the
     * implementation class itself that is hidden.
     */
    static void hidingImplementationDemo() {
        System.out.println("=== Hiding the implementation ===");

        DiscountPolicy policy = DiscountPolicies.percent(10);
        System.out.println("runtime class         : " + concreteClassName(policy));              // PercentDiscount
        System.out.println("class is public       : " + isImplementationPublic(policy));         // false
    }

    static String concreteClassName(DiscountPolicy policy) {
        return policy.getClass().getSimpleName();
    }

    static boolean isImplementationPublic(DiscountPolicy policy) {
        return Modifier.isPublic(policy.getClass().getModifiers());
    }

    // ── 4. Interface segregation ──────────────────────────────────────────────

    /** A "fat" interface: every implementer must support everything. */
    interface FatMachine {
        String print(String document);

        String scan();
    }

    /** A printer forced to implement scan() — it can only lie or throw. */
    static class OldPrinter implements FatMachine {
        @Override
        public String print(String document) {
            return "printed " + document;
        }

        @Override
        public String scan() {
            throw new UnsupportedOperationException("this printer cannot scan");
        }
    }

    /** Small, focused interfaces. */
    interface CanPrint {
        String print(String document);
    }

    interface CanScan {
        String scan();
    }

    /** Only promises what it can do. */
    static class BasicPrinter implements CanPrint {
        @Override
        public String print(String document) {
            return "printed " + document;
        }
    }

    /** Combines both capabilities. */
    static class AllInOne implements CanPrint, CanScan {
        @Override
        public String print(String document) {
            return "printed " + document;
        }

        @Override
        public String scan() {
            return "scanned page";
        }
    }

    /**
     * INTERFACE SEGREGATION: many small, role-specific interfaces are better than one big one.
     * Clients should not be forced to depend on methods they never use.
     *
     * <p>WHY this matters: with {@code FatMachine}, {@code OldPrinter} must implement {@code scan()}
     * and can only throw, so a caller cannot trust the interface any more. With {@code CanPrint} and
     * {@code CanScan}, every implementer keeps its promises, and a class that can do both simply
     * implements both.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>Calling {@code scan()} through the fat interface blows up at RUNTIME</li>
     *   <li>With segregated interfaces the mistake is impossible: {@code BasicPrinter} has no
     *       {@code scan()}, so the compiler stops you</li>
     *   <li>{@code instanceof CanScan} lets generic code ask "can you scan?" before trying</li>
     * </ul>
     *
     * <p>Common gotcha: do not go to the other extreme — one-method interfaces for everything is just
     * noise. Group methods that change together and are used together.
     */
    static void interfaceSegregationDemo() {
        System.out.println("=== Interface segregation ===");

        try {
            scanWith(new OldPrinter());
        } catch (UnsupportedOperationException e) {
            System.out.println("fat interface         : UnsupportedOperationException — " + e.getMessage());
        }
        System.out.println("BasicPrinter can scan : " + canScan(new BasicPrinter()));              // false
        System.out.println("AllInOne can scan     : " + canScan(new AllInOne()));                  // true
    }

    static String scanWith(FatMachine machine) {
        return machine.scan();
    }

    static boolean canScan(Object device) {
        return device instanceof CanScan;
    }

    // ── 5. Skeletal implementation ────────────────────────────────────────────

    /**
     * A SKELETAL implementation pairs an interface with an abstract class: the interface is the
     * contract clients use, the abstract class does the boring shared work.
     * {@code AbstractSensor} implements {@code Sensor.id()} and {@code Sensor.describe()} once and
     * leaves {@code read()} and {@code unit()} to the concrete sensors.
     *
     * <p>WHY this matters: you keep the flexibility of an interface (clients depend on {@code Sensor};
     * a class that cannot extend {@code AbstractSensor} may still implement {@code Sensor} directly)
     * AND avoid copy-pasting the shared code into every implementer. The JDK does the same with
     * {@code List} + {@code AbstractList} — see {@code AbstractClassDemo} in the previous module.
     *
     * <p>Rules shown here:
     * <ul>
     *   <li>An abstract class that implements an interface need not implement every method —
     *       {@code AbstractSensor} leaves {@code read()} abstract</li>
     *   <li>The abstract class can hold STATE ({@code id}); the interface cannot</li>
     *   <li>Clients work with {@code List<Sensor>} and never see the abstract class</li>
     * </ul>
     *
     * <p>Common gotcha: put the skeleton in a separate class — do not force clients to extend it.
     * The interface must stay the contract.
     */
    static void skeletalImplementationDemo() {
        System.out.println("=== Skeletal implementation ===");

        List<Sensor> sensors = Arrays.<Sensor>asList(new TemperatureSensor("t1", 21.5), new HumiditySensor("h1", 40.0));
        for (String line : describeAll(sensors)) {
            System.out.println(line);                       // t1 = 21.5 C / h1 = 40.0 %
        }
        System.out.println("AbstractSensor abstract: " + Modifier.isAbstract(AbstractSensor.class.getModifiers()));   // true
        System.out.println("fields: Sensor/Abstract: " + Sensor.class.getDeclaredFields().length
                + "/" + instanceFieldCount(AbstractSensor.class));                                                      // 0/1
    }

    static List<String> describeAll(List<Sensor> sensors) {
        List<String> lines = new ArrayList<>();
        for (Sensor sensor : sensors) {
            lines.add(sensor.describe());
        }
        return lines;
    }

    static int instanceFieldCount(Class<?> type) {
        int count = 0;
        for (Field field : type.getDeclaredFields()) {
            if (!Modifier.isStatic(field.getModifiers())) {
                count++;
            }
        }
        return count;
    }
}
