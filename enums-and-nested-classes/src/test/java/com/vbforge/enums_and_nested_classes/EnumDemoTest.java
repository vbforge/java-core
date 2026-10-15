package com.vbforge.enums_and_nested_classes;

import com.vbforge.enums_and_nested_classes.EnumDemo.AppRegistry;
import com.vbforge.enums_and_nested_classes.EnumDemo.CustomerTier;
import com.vbforge.enums_and_nested_classes.EnumDemo.Discount;
import com.vbforge.enums_and_nested_classes.EnumDemo.Operation;
import com.vbforge.enums_and_nested_classes.EnumDemo.Planet;
import com.vbforge.enums_and_nested_classes.EnumDemo.Season;
import com.vbforge.enums_and_nested_classes.EnumDemo.TrafficLight;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("EnumDemo — API contracts")
class EnumDemoTest {

    @Nested
    @DisplayName("1. Enum basics")
    class Basics {

        @Test
        @DisplayName("values() lists the constants in declaration order")
        void valuesInDeclarationOrder() {
            assertThat(Season.values())
                    .containsExactly(Season.WINTER, Season.SPRING, Season.SUMMER, Season.AUTUMN);
        }

        @Test
        @DisplayName("name() and ordinal() reflect the declaration")
        void nameAndOrdinal() {
            assertThat(Season.SUMMER.name()).isEqualTo("SUMMER");
            assertThat(Season.SUMMER.ordinal()).isEqualTo(2);
        }

        @Test
        @DisplayName("valueOf returns the one and only instance")
        void valueOfReturnsSameInstance() {
            assertThat(Season.valueOf("SPRING")).isSameAs(Season.SPRING);
        }

        @Test
        @DisplayName("valueOf is case-sensitive and throws on a miss")
        void valueOfIsCaseSensitive() {
            assertThatThrownBy(() -> Season.valueOf("summer")).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> Season.valueOf("MONSOON")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("compareTo is the difference of the ordinals")
        void compareToUsesOrdinals() {
            assertThat(Season.WINTER.compareTo(Season.AUTUMN)).isEqualTo(-3);
            assertThat(Season.AUTUMN.compareTo(Season.WINTER)).isEqualTo(3);
            assertThat(Season.SPRING.compareTo(Season.SPRING)).isEqualTo(0);
        }

        @Test
        @DisplayName("values() returns a fresh array on every call")
        void valuesReturnsFreshArray() {
            Season[] first = Season.values();
            first[0] = null;                                   // damage our copy only
            assertThat(Season.values()).isNotSameAs(first);
            assertThat(Season.values()[0]).isEqualTo(Season.WINTER);
        }

        @Test
        @DisplayName("classic switch maps every constant")
        void classicSwitch() {
            assertThat(EnumDemo.activity(Season.WINTER)).isEqualTo("skiing");
            assertThat(EnumDemo.activity(Season.SPRING)).isEqualTo("hiking");
            assertThat(EnumDemo.activity(Season.SUMMER)).isEqualTo("swimming");
            assertThat(EnumDemo.activity(Season.AUTUMN)).isEqualTo("mushroom picking");
        }

        @Test
        @DisplayName("switch on a null enum throws NullPointerException")
        void switchOnNullThrows() {
            assertThatThrownBy(() -> EnumDemo.activity(null)).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("2. Fields, constructors and methods")
    class FieldsAndMethods {

        @Test
        @DisplayName("each constant carries its own field values")
        void constantsCarryFields() {
            assertThat(Planet.EARTH.mass()).isEqualTo(5.976e+24);
            assertThat(Planet.EARTH.radius()).isEqualTo(6.37814e6);
        }

        @Test
        @DisplayName("surfaceGravity is computed from the fields")
        void surfaceGravity() {
            assertThat(Planet.EARTH.surfaceGravity()).isCloseTo(9.80, within(0.01));
            assertThat(Planet.MERCURY.surfaceGravity()).isCloseTo(3.70, within(0.01));
            assertThat(Planet.JUPITER.surfaceGravity()).isCloseTo(24.81, within(0.01));
        }

        @Test
        @DisplayName("surfaceWeight scales the mass by the gravity")
        void surfaceWeight() {
            assertThat(Planet.EARTH.surfaceWeight(70.0))
                    .isEqualTo(70.0 * Planet.EARTH.surfaceGravity());
            assertThat(Planet.JUPITER.surfaceWeight(70.0))
                    .isGreaterThan(Planet.EARTH.surfaceWeight(70.0));
        }

        @Test
        @DisplayName("lookup is case-insensitive and trims whitespace")
        void lookupIsForgiving() {
            assertThat(Planet.lookup(" earth ")).contains(Planet.EARTH);
            assertThat(Planet.lookup("JUPITER")).contains(Planet.JUPITER);
        }

        @Test
        @DisplayName("lookup finds every constant through the static map")
        void lookupFindsEveryConstant() {
            for (Planet planet : Planet.values()) {
                assertThat(Planet.lookup(planet.name())).contains(planet);
            }
        }

        @Test
        @DisplayName("lookup returns empty instead of throwing")
        void lookupReturnsEmpty() {
            assertThat(Planet.lookup("pluto")).isEmpty();
            assertThat(Planet.lookup(null)).isEmpty();
        }
    }

    @Nested
    @DisplayName("3. Constant-specific class bodies")
    class ConstantSpecificBodies {

        @Test
        @DisplayName("each constant applies its own operation")
        void operationsApply() {
            assertThat(Operation.ADD.apply(6, 3)).isEqualTo(9);
            assertThat(Operation.SUBTRACT.apply(6, 3)).isEqualTo(3);
            assertThat(Operation.MULTIPLY.apply(6, 3)).isEqualTo(18);
        }

        @Test
        @DisplayName("symbols come from the constructor argument")
        void operationSymbols() {
            assertThat(Operation.ADD.symbol()).isEqualTo("+");
            assertThat(Operation.SUBTRACT.symbol()).isEqualTo("-");
            assertThat(Operation.MULTIPLY.symbol()).isEqualTo("*");
        }

        @Test
        @DisplayName("a constant with a body is an instance of an anonymous subclass")
        void constantWithBodyHasOwnClass() {
            assertThat(Operation.ADD.getClass()).isNotEqualTo(Operation.class);
            assertThat(Operation.ADD.getClass().isAnonymousClass()).isTrue();
        }

        @Test
        @DisplayName("getDeclaringClass() still returns the enum type")
        void declaringClassIsTheEnum() {
            assertThat(Operation.ADD.getDeclaringClass()).isEqualTo(Operation.class);
            assertThat(Operation.MULTIPLY.getDeclaringClass()).isEqualTo(Operation.class);
        }

        @Test
        @DisplayName("TrafficLight cycles RED → GREEN → YELLOW → RED")
        void trafficLightCycle() {
            assertThat(TrafficLight.RED.next()).isSameAs(TrafficLight.GREEN);
            assertThat(TrafficLight.GREEN.next()).isSameAs(TrafficLight.YELLOW);
            assertThat(TrafficLight.YELLOW.next()).isSameAs(TrafficLight.RED);
        }

        @Test
        @DisplayName("TrafficLight keeps a duration per constant")
        void trafficLightSeconds() {
            assertThat(TrafficLight.RED.seconds()).isEqualTo(30);
            assertThat(TrafficLight.GREEN.seconds()).isEqualTo(25);
            assertThat(TrafficLight.YELLOW.seconds()).isEqualTo(5);
        }
    }

    @Nested
    @DisplayName("4. Enums implementing interfaces")
    class InterfaceImplementation {

        @Test
        @DisplayName("tiers apply their percentage discount")
        void tiersApplyDiscount() {
            assertThat(CustomerTier.REGULAR.applyTo(100.0)).isEqualTo(100.0);
            assertThat(CustomerTier.SILVER.applyTo(100.0)).isEqualTo(95.0);
            assertThat(CustomerTier.GOLD.applyTo(80.0)).isEqualTo(72.0);
        }

        @Test
        @DisplayName("callers can depend on the interface only")
        void usableThroughInterface() {
            Discount discount = CustomerTier.GOLD;
            assertThat(discount.applyTo(200.0)).isEqualTo(180.0);
        }

        @Test
        @DisplayName("percentOff is exposed for each constant")
        void percentOffField() {
            assertThat(CustomerTier.REGULAR.percentOff()).isEqualTo(0);
            assertThat(CustomerTier.GOLD.percentOff()).isEqualTo(10);
        }
    }

    @Nested
    @DisplayName("5. EnumMap and EnumSet")
    class EnumCollections {

        @Test
        @DisplayName("EnumMap iterates in declaration order, not insertion order")
        void enumMapKeepsDeclarationOrder() {
            EnumMap<Season, String> calendar = EnumDemo.scrambledCalendar();
            assertThat(calendar.keySet())
                    .containsExactly(Season.WINTER, Season.SPRING, Season.SUMMER, Season.AUTUMN);
        }

        @Test
        @DisplayName("EnumMap stores and returns values per key")
        void enumMapLookup() {
            Map<Season, String> calendar = EnumDemo.scrambledCalendar();
            assertThat(calendar).containsEntry(Season.SUMMER, "Jun-Aug");
            assertThat(calendar).hasSize(4);
        }

        @Test
        @DisplayName("EnumMap rejects null keys")
        void enumMapRejectsNullKey() {
            EnumMap<Season, String> calendar = new EnumMap<>(Season.class);
            assertThatThrownBy(() -> calendar.put(null, "x")).isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("EnumSet.range is inclusive and ordered")
        void enumSetRange() {
            assertThat(EnumDemo.warmSeasons()).containsExactly(Season.SPRING, Season.SUMMER);
        }

        @Test
        @DisplayName("EnumSet.complementOf returns the remaining constants")
        void enumSetComplement() {
            assertThat(EnumDemo.coldSeasons()).containsExactly(Season.WINTER, Season.AUTUMN);
        }

        @Test
        @DisplayName("allOf and noneOf build full and empty sets")
        void allOfAndNoneOf() {
            assertThat(EnumSet.allOf(Season.class)).hasSize(4);
            assertThat(EnumSet.noneOf(Season.class)).isEmpty();
        }

        @Test
        @DisplayName("EnumSet rejects null elements")
        void enumSetRejectsNull() {
            EnumSet<Season> set = EnumSet.noneOf(Season.class);
            assertThatThrownBy(() -> set.add(null)).isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("6. Enum singleton")
    class Singleton {

        @Test
        @DisplayName("there is exactly one instance")
        void exactlyOneInstance() {
            assertThat(AppRegistry.valueOf("INSTANCE")).isSameAs(AppRegistry.INSTANCE);
            assertThat(AppRegistry.values()).containsExactly(AppRegistry.INSTANCE);
        }

        @Test
        @DisplayName("state is shared by every caller")
        void stateIsShared() {
            AppRegistry.INSTANCE.clear();
            AppRegistry.INSTANCE.put("mode", "test");
            assertThat(AppRegistry.valueOf("INSTANCE").get("mode")).isEqualTo("test");
            AppRegistry.INSTANCE.clear();
            assertThat(AppRegistry.INSTANCE.get("mode")).isNull();
        }

        @Test
        @DisplayName("reflection cannot create a second instance")
        void reflectionIsBlocked() {
            assertThat(EnumDemo.reflectiveCreationFailure()).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("7. main() smoke run")
    class MainMethod {

        @Test
        @DisplayName("main runs all sections without throwing")
        void mainRuns() {
            PrintStream original = System.out;
            ByteArrayOutputStream captured = new ByteArrayOutputStream();
            System.setOut(new PrintStream(captured));
            try {
                EnumDemo.main(new String[0]);
            } finally {
                System.setOut(original);
            }
            assertThat(captured.toString()).contains("---").contains("EARTH").contains("reflection copy");
        }
    }
}
