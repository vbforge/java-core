package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.DeepSnapshot;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.FixedPoint;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.Money;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.MovingPoint;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.MutablePoint;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.OpenPoint;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityDemo.ShallowSnapshot;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ImmutabilityDemoTest — verifies every concept shown in {@link ImmutabilityDemo}.
 *
 * <p>These tests are always GREEN. Read them as executable specification of
 * how immutable objects behave.
 */
@DisplayName("ImmutabilityDemo — API contracts")
class ImmutabilityDemoTest {

    // ── 1. final is not immutable ─────────────────────────────────────────────

    @Nested
    @DisplayName("1. final is not immutable")
    class FinalIsNotImmutableTest {

        @Test
        @DisplayName("a final array reference still allows element changes")
        void finalArray() {
            assertThat(ImmutabilityDemo.finalArrayStillMutable()).containsExactly(99, 2, 3);
        }

        @Test
        @DisplayName("a final reference to a StringBuilder can still be appended to")
        void finalStringBuilder() {
            final StringBuilder builder = new StringBuilder("a");
            builder.append("b");
            assertThat(builder.toString()).isEqualTo("ab");
        }
    }

    // ── 2. The recipe ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. The recipe")
    class RecipeTest {

        @Test
        @DisplayName("plus returns a new object and leaves both operands unchanged")
        void plusReturnsNew() {
            Money ten = new Money(1000, "EUR");
            Money five = new Money(500, "EUR");
            Money sum = ten.plus(five);
            assertThat(sum.cents()).isEqualTo(1500);
            assertThat(sum).isNotSameAs(ten).isNotSameAs(five);
            assertThat(ten.cents()).isEqualTo(1000);
            assertThat(five.cents()).isEqualTo(500);
        }

        @Test
        @DisplayName("times returns a new object")
        void timesReturnsNew() {
            Money base = new Money(250, "EUR");
            assertThat(base.times(3)).isEqualTo(new Money(750, "EUR"));
            assertThat(base.cents()).isEqualTo(250);
        }

        @Test
        @DisplayName("different currencies cannot be added")
        void currencyMismatch() {
            assertThatThrownBy(() -> new Money(1, "EUR").plus(new Money(1, "USD")))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("currency mismatch");
        }

        @Test
        @DisplayName("arithmetic overflow is reported, not wrapped")
        void overflow() {
            assertThatThrownBy(() -> new Money(Long.MAX_VALUE, "EUR").plus(new Money(1, "EUR")))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("value equality: same cents and currency are equal, with equal hash codes")
        void valueEquality() {
            assertThat(new Money(5, "EUR")).isEqualTo(new Money(5, "EUR"));
            assertThat(new Money(5, "EUR").hashCode()).isEqualTo(new Money(5, "EUR").hashCode());
            assertThat(new Money(5, "EUR")).isNotEqualTo(new Money(5, "USD"));
        }

        @Test
        @DisplayName("the constructor rejects an empty currency")
        void rejectsEmptyCurrency() {
            assertThatThrownBy(() -> new Money(1, "")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the class has no setter-like methods: every non-static field is private and final")
        void fieldsArePrivateFinal() {
            for (java.lang.reflect.Field field : Money.class.getDeclaredFields()) {
                assertThat(java.lang.reflect.Modifier.isPrivate(field.getModifiers())).isTrue();
                assertThat(java.lang.reflect.Modifier.isFinal(field.getModifiers())).isTrue();
            }
        }
    }

    // ── 3. Shallow vs deep ────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Shallow vs deep")
    class ShallowVsDeepTest {

        @Test
        @DisplayName("a mutable element inside a frozen list still changes the 'immutable' holder")
        void shallowLeaks() {
            StringBuilder part = new StringBuilder("ab");
            ShallowSnapshot shallow = new ShallowSnapshot(List.of(part));
            part.append("c");
            assertThat(shallow.text()).isEqualTo("abc");
        }

        @Test
        @DisplayName("immutable elements make the holder truly frozen")
        void deepIsFrozen() {
            StringBuilder part = new StringBuilder("ab");
            DeepSnapshot deep = new DeepSnapshot(List.of(part.toString()));
            part.append("c");
            assertThat(deep.text()).isEqualTo("ab");
        }

        @Test
        @DisplayName("List.copyOf copies the list, not the elements")
        void copyOfIsShallow() {
            StringBuilder element = new StringBuilder("x");
            List<StringBuilder> copy = List.copyOf(Arrays.asList(element));
            assertThat(copy.get(0)).isSameAs(element);
        }
    }

    // ── 4. Safe keys ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Safe keys")
    class SafeKeysTest {

        @Test
        @DisplayName("a mutable key that changed after insertion is 'lost' by the set")
        void mutableKeyLost() {
            Set<MutablePoint> set = new HashSet<>();
            MutablePoint point = new MutablePoint(1, 2);
            set.add(point);
            point.x = 99;
            assertThat(set.contains(point)).isFalse();
            assertThat(set).hasSize(1);
            assertThat(set.iterator().next()).isSameAs(point);   // it IS in there
        }

        @Test
        @DisplayName("an immutable key is always found")
        void immutableKeyFound() {
            Set<FixedPoint> set = new HashSet<>();
            FixedPoint point = new FixedPoint(1, 2);
            set.add(point);
            assertThat(set.contains(point)).isTrue();
            assertThat(set.contains(new FixedPoint(1, 2))).isTrue();
        }

        @Test
        @DisplayName("withX returns a new object and leaves the key untouched")
        void withXReturnsNew() {
            FixedPoint point = new FixedPoint(1, 2);
            FixedPoint moved = point.withX(99);
            assertThat(moved).isNotSameAs(point).isNotEqualTo(point);
            assertThat(point).isEqualTo(new FixedPoint(1, 2));
        }
    }

    // ── 5. JDK immutables ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. JDK immutables")
    class JdkImmutablesTest {

        @Test
        @DisplayName("ignoring the result of toUpperCase changes nothing")
        void ignoredReturnValue() {
            assertThat(ImmutabilityDemo.ignoredReturnValue()).isEqualTo("java");
        }

        @Test
        @DisplayName("using the result gives the new String")
        void keptReturnValue() {
            assertThat(ImmutabilityDemo.keptReturnValue()).isEqualTo("JAVA");
        }

        @Test
        @DisplayName("BigDecimal.add does not modify the receiver")
        void bigDecimal() {
            BigDecimal total = new BigDecimal("10.00");
            BigDecimal result = total.add(new BigDecimal("5.00"));
            assertThat(total).isEqualByComparingTo("10.00");
            assertThat(result).isEqualByComparingTo("15.00");
        }

        @Test
        @DisplayName("LocalDate.plusMonths returns a new date")
        void localDate() {
            LocalDate jan31 = LocalDate.of(2024, 1, 31);
            assertThat(jan31.plusMonths(1)).isEqualTo(LocalDate.of(2024, 2, 29));
            assertThat(jan31).isEqualTo(LocalDate.of(2024, 1, 31));
        }

        @Test
        @DisplayName("List.of is immutable, Arrays.asList is only fixed-size")
        void listOfVsAsList() {
            assertThatThrownBy(() -> List.of(1, 2).set(0, 9)).isInstanceOf(UnsupportedOperationException.class);
            List<Integer> fixedSize = Arrays.asList(1, 2);
            fixedSize.set(0, 9);
            assertThat(fixedSize).containsExactly(9, 2);
            assertThatThrownBy(() -> fixedSize.add(3)).isInstanceOf(UnsupportedOperationException.class);
        }
    }

    // ── 6. Gotchas ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Gotchas")
    class GotchasTest {

        @Test
        @DisplayName("a non-final 'immutable' class can be subverted by a subclass")
        void subclassBreaksImmutability() {
            OpenPoint point = new MovingPoint();
            int first = point.x();
            int second = point.x();
            assertThat(first).isEqualTo(1);
            assertThat(second).isEqualTo(2);
        }

        @Test
        @DisplayName("a final class cannot be subclassed; a non-final one can")
        void finalClassCheck() {
            assertThat(ImmutabilityDemo.isFinalClass(Money.class)).isTrue();
            assertThat(ImmutabilityDemo.isFinalClass(OpenPoint.class)).isFalse();
        }
    }
}
