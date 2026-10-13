package com.vbforge.oop_interfaces_and_abstraction;

import com.vbforge.oop_interfaces_and_abstraction.AbstractionDemo.AllInOne;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionDemo.BasicPrinter;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionDemo.CanPrint;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionDemo.CanScan;
import com.vbforge.oop_interfaces_and_abstraction.AbstractionDemo.OldPrinter;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.AbstractSensor;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.Checkout;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.DiscountPolicies;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.DiscountPolicy;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.HumiditySensor;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.Sensor;
import com.vbforge.oop_interfaces_and_abstraction.abstraction.TemperatureSensor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * AbstractionDemoTest — verifies every concept shown in {@link AbstractionDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * programming against abstractions.
 */
@DisplayName("AbstractionDemo — API contracts")
class AbstractionDemoTest {

    // ── 1. Program to an interface ────────────────────────────────────────────

    @Nested
    @DisplayName("1. Program to an interface")
    class ProgramToInterfaceTest {

        @Test
        @DisplayName("the same method works for ArrayList and LinkedList")
        void anyListImplementation() {
            assertThat(AbstractionDemo.sumFirstTwo(new ArrayList<>(Arrays.asList(4, 5, 6)))).isEqualTo(9);
            assertThat(AbstractionDemo.sumFirstTwo(new LinkedList<>(Arrays.asList(4, 5, 6)))).isEqualTo(9);
        }

        @Test
        @DisplayName("it also works for lists nobody wrote the method for, such as Arrays.asList")
        void futureImplementations() {
            assertThat(AbstractionDemo.sumFirstTwo(Arrays.asList(1, 2))).isEqualTo(3);
            assertThat(AbstractionDemo.sumFirstTwo(Collections.unmodifiableList(Arrays.asList(7, 8)))).isEqualTo(15);
        }

        @Test
        @DisplayName("the read-only view rejects changes")
        void viewRejectsChanges() {
            List<String> view = AbstractionDemo.readOnlyView(new ArrayList<>(Arrays.asList("a", "b")));
            assertThatThrownBy(() -> view.add("c")).isInstanceOf(UnsupportedOperationException.class);
            assertThatThrownBy(() -> view.remove(0)).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("the read-only view is LIVE: it shows later changes of its source")
        void viewIsLive() {
            List<String> source = new ArrayList<>(Arrays.asList("a", "b"));
            List<String> view = AbstractionDemo.readOnlyView(source);
            source.add("c");
            assertThat(view).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("a copy, unlike a view, is a snapshot")
        void copyIsSnapshot() {
            List<String> source = new ArrayList<>(Arrays.asList("a"));
            List<String> snapshot = new ArrayList<>(source);
            source.add("b");
            assertThat(snapshot).containsExactly("a");
        }
    }

    // ── 2. Strategy through injection ─────────────────────────────────────────

    @Nested
    @DisplayName("2. Strategy through injection")
    class StrategyTest {

        @Test
        @DisplayName("no discount → the plain subtotal")
        void none() {
            assertThat(AbstractionDemo.totalWith(DiscountPolicies.none())).isEqualTo(200.0);
        }

        @Test
        @DisplayName("percent(10) → 10% off")
        void percent() {
            assertThat(AbstractionDemo.totalWith(DiscountPolicies.percent(10))).isEqualTo(180.0);
        }

        @Test
        @DisplayName("threshold(100, 15) → 15 off because 200 reaches 100")
        void thresholdReached() {
            assertThat(AbstractionDemo.totalWith(DiscountPolicies.threshold(100, 15))).isEqualTo(185.0);
        }

        @Test
        @DisplayName("threshold not reached → no discount; reaching it exactly counts")
        void thresholdBoundary() {
            assertThat(DiscountPolicies.threshold(500, 15).apply(200.0)).isEqualTo(200.0);
            assertThat(DiscountPolicies.threshold(200, 15).apply(200.0)).isEqualTo(185.0);
        }

        @Test
        @DisplayName("a lambda is a valid policy because the interface has one abstract method")
        void lambdaPolicy() {
            assertThat(AbstractionDemo.totalWith(subtotal -> subtotal / 2)).isEqualTo(100.0);
        }

        @Test
        @DisplayName("Checkout needs no change: the policy can be swapped between orders")
        void swapPolicies() {
            List<Double> prices = Arrays.asList(50.0, 150.0);
            assertThat(new Checkout(DiscountPolicies.none()).total(prices)).isEqualTo(200.0);
            assertThat(new Checkout(DiscountPolicies.percent(50)).total(prices)).isEqualTo(100.0);
        }

        @Test
        @DisplayName("an empty basket totals zero under every policy")
        void emptyBasket() {
            assertThat(new Checkout(DiscountPolicies.percent(10)).total(new ArrayList<Double>())).isEqualTo(0.0);
        }
    }

    // ── 3. Hiding the implementation ──────────────────────────────────────────

    @Nested
    @DisplayName("3. Hiding the implementation")
    class HidingImplementationTest {

        @Test
        @DisplayName("the runtime object is a concrete class...")
        void runtimeClass() {
            assertThat(AbstractionDemo.concreteClassName(DiscountPolicies.percent(10))).isEqualTo("PercentDiscount");
            assertThat(AbstractionDemo.concreteClassName(DiscountPolicies.none())).isEqualTo("NoDiscount");
            assertThat(AbstractionDemo.concreteClassName(DiscountPolicies.threshold(1, 1))).isEqualTo("ThresholdDiscount");
        }

        @Test
        @DisplayName("...which is NOT public, so outside code cannot depend on it")
        void notPublic() {
            assertThat(AbstractionDemo.isImplementationPublic(DiscountPolicies.percent(10))).isFalse();
            assertThat(AbstractionDemo.isImplementationPublic(DiscountPolicies.none())).isFalse();
        }

        @Test
        @DisplayName("the interface and the factory are the public surface")
        void publicSurface() {
            assertThat(Modifier.isPublic(DiscountPolicy.class.getModifiers())).isTrue();
            assertThat(Modifier.isPublic(DiscountPolicies.class.getModifiers())).isTrue();
            assertThat(Modifier.isFinal(DiscountPolicies.class.getModifiers())).isTrue();
        }

        @Test
        @DisplayName("every implementation is used only through the interface type")
        void throughInterface() {
            DiscountPolicy policy = DiscountPolicies.percent(25);
            assertThat(policy.apply(80.0)).isEqualTo(60.0);
        }
    }

    // ── 4. Interface segregation ──────────────────────────────────────────────

    @Nested
    @DisplayName("4. Interface segregation")
    class InterfaceSegregationTest {

        @Test
        @DisplayName("the fat interface forces an implementer to throw")
        void fatInterfaceThrows() {
            assertThatThrownBy(() -> AbstractionDemo.scanWith(new OldPrinter()))
                    .isInstanceOf(UnsupportedOperationException.class)
                    .hasMessage("this printer cannot scan");
        }

        @Test
        @DisplayName("the fat interface still works for the part the printer supports")
        void fatInterfacePrint() {
            assertThat(new OldPrinter().print("doc")).isEqualTo("printed doc");
        }

        @Test
        @DisplayName("a segregated implementer only promises what it can do")
        void basicPrinterOnlyPrints() {
            assertThat(new BasicPrinter()).isInstanceOf(CanPrint.class).isNotInstanceOf(CanScan.class);
            assertThat(new BasicPrinter().print("doc")).isEqualTo("printed doc");
        }

        @Test
        @DisplayName("a class can combine several small interfaces")
        void allInOne() {
            AllInOne device = new AllInOne();
            assertThat(device).isInstanceOf(CanPrint.class).isInstanceOf(CanScan.class);
            assertThat(device.scan()).isEqualTo("scanned page");
        }

        @Test
        @DisplayName("instanceof asks \"can you scan?\" before trying")
        void askBeforeUsing() {
            assertThat(AbstractionDemo.canScan(new BasicPrinter())).isFalse();
            assertThat(AbstractionDemo.canScan(new AllInOne())).isTrue();
            assertThat(AbstractionDemo.canScan("not a device")).isFalse();
            assertThat(AbstractionDemo.canScan(null)).isFalse();
        }
    }

    // ── 5. Skeletal implementation ────────────────────────────────────────────

    @Nested
    @DisplayName("5. Skeletal implementation")
    class SkeletalImplementationTest {

        @Test
        @DisplayName("shared describe() works for every concrete sensor")
        void describeAll() {
            List<Sensor> sensors = Arrays.<Sensor>asList(new TemperatureSensor("t1", 21.5), new HumiditySensor("h1", 40.0));
            assertThat(AbstractionDemo.describeAll(sensors)).containsExactly("t1 = 21.5 C", "h1 = 40.0 %");
        }

        @Test
        @DisplayName("the abstract class is abstract and leaves read() unimplemented")
        void abstractClass() throws NoSuchMethodException {
            assertThat(Modifier.isAbstract(AbstractSensor.class.getModifiers())).isTrue();
            assertThat(Modifier.isAbstract(AbstractSensor.class.getMethod("read").getModifiers())).isTrue();
        }

        @Test
        @DisplayName("the abstract class holds state; the interface cannot")
        void stateLivesInTheClass() {
            assertThat(AbstractionDemo.instanceFieldCount(AbstractSensor.class)).isEqualTo(1);
            assertThat(Sensor.class.getDeclaredFields()).isEmpty();
        }

        @Test
        @DisplayName("shared id() is stored once by the abstract parent")
        void sharedId() {
            assertThat(new TemperatureSensor("t1", 0).id()).isEqualTo("t1");
            assertThat(new HumiditySensor("h9", 0).id()).isEqualTo("h9");
        }

        @Test
        @DisplayName("a class that cannot extend the skeleton may implement the interface directly")
        void directImplementation() {
            Sensor fixed = new Sensor() {
                @Override
                public String id() {
                    return "x";
                }

                @Override
                public double read() {
                    return 1.0;
                }

                @Override
                public String describe() {
                    return "custom";
                }
            };
            assertThat(AbstractionDemo.describeAll(Collections.singletonList(fixed))).containsExactly("custom");
        }

        @Test
        @DisplayName("the abstract class is a Sensor, so clients depend only on the interface")
        void isASensor() {
            assertThat(Sensor.class.isAssignableFrom(AbstractSensor.class)).isTrue();
            assertThat(new TemperatureSensor("t", 1)).isInstanceOf(Sensor.class).isInstanceOf(AbstractSensor.class);
        }
    }
}
