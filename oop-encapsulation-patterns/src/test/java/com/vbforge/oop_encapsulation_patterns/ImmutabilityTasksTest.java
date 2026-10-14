package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.ImmutabilityTasks.Counter;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityTasks.Money;
import com.vbforge.oop_encapsulation_patterns.ImmutabilityTasks.Profile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ImmutabilityTasksTest — tests for every method in {@link ImmutabilityTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ImmutabilityTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ImmutabilityTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 5 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ImmutabilityTasks — your implementations")
class ImmutabilityTasksTest {

    private ImmutabilityTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ImmutabilityTasks();
    }

    // Sample classes for Task 5 (declared here, outside the @Nested classes, on purpose)
    private static final class WithPublicFinalField {
        public final int x = 1;
    }

    private static final class WithMutablePrivateField {
        private int x;
    }

    private static class NotFinalButTidy {
        private final int x = 1;
    }

    private static final class NoFields { }

    private record Pair(int a, int b) { }

    // ── Task 1: Money.plus ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement plus()")
    @DisplayName("Task 1 — Money.plus()")
    class PlusTest {

        @ParameterizedTest(name = "{0} + {1} → {2}")
        @CsvSource({
                "1000,  500,  1500",
                "1000,  -300, 700",
                "0,     0,    0",
                "-100,  -200, -300",
        })
        @DisplayName("parametrized sums")
        void sums(long left, long right, long expected) {
            assertThat(new Money(left, "EUR").plus(new Money(right, "EUR")))
                    .isEqualTo(new Money(expected, "EUR"));
        }

        @Test
        @DisplayName("returns a new object and leaves both operands unchanged")
        void returnsNew() {
            Money a = new Money(1000, "EUR");
            Money b = new Money(500, "EUR");
            Money sum = a.plus(b);
            assertThat(sum).isNotSameAs(a).isNotSameAs(b);
            assertThat(a.cents()).isEqualTo(1000);
            assertThat(b.cents()).isEqualTo(500);
        }

        @Test
        @DisplayName("keeps the currency")
        void keepsCurrency() {
            assertThat(new Money(1, "USD").plus(new Money(2, "USD")).currency()).isEqualTo("USD");
        }

        @Test
        @DisplayName("different currencies → IllegalArgumentException")
        void currencyMismatch() {
            assertThatThrownBy(() -> new Money(1000, "EUR").plus(new Money(5, "USD")))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("overflow → ArithmeticException")
        void overflow() {
            assertThatThrownBy(() -> new Money(Long.MAX_VALUE, "EUR").plus(new Money(1, "EUR")))
                    .isInstanceOf(ArithmeticException.class);
        }
    }

    // ── Task 2: Money.times ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement times()")
    @DisplayName("Task 2 — Money.times()")
    class TimesTest {

        @ParameterizedTest(name = "250 × {0} → {1}")
        @CsvSource({
                "3,   750",
                "0,   0",
                "-2,  -500",
                "1,   250",
        })
        @DisplayName("parametrized products")
        void products(int factor, long expected) {
            assertThat(new Money(250, "EUR").times(factor)).isEqualTo(new Money(expected, "EUR"));
        }

        @Test
        @DisplayName("returns a new object and leaves the receiver unchanged")
        void returnsNew() {
            Money base = new Money(250, "EUR");
            Money result = base.times(2);
            assertThat(result).isNotSameAs(base);
            assertThat(base.cents()).isEqualTo(250);
        }

        @Test
        @DisplayName("overflow → ArithmeticException")
        void overflow() {
            assertThatThrownBy(() -> new Money(Long.MAX_VALUE, "EUR").times(2))
                    .isInstanceOf(ArithmeticException.class);
        }

        @Test
        @DisplayName("a large int factor is not truncated")
        void largeFactor() {
            assertThat(new Money(1_000_000, "EUR").times(Integer.MAX_VALUE).cents())
                    .isEqualTo(1_000_000L * Integer.MAX_VALUE);
        }
    }

    // ── Task 3: Money.sum ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sum()")
    @DisplayName("Task 3 — Money.sum()")
    class SumTest {

        @Test
        @DisplayName("adds all amounts (also needs Task 1)")
        void adds() {
            List<Money> values = Arrays.asList(new Money(100, "EUR"), new Money(250, "EUR"));
            assertThat(Money.sum(values, "EUR")).isEqualTo(new Money(350, "EUR"));
        }

        @Test
        @DisplayName("an empty list gives zero in the given currency")
        void empty() {
            assertThat(Money.sum(Collections.<Money>emptyList(), "EUR")).isEqualTo(new Money(0, "EUR"));
        }

        @Test
        @DisplayName("a single element gives the same amount")
        void single() {
            assertThat(Money.sum(Collections.singletonList(new Money(42, "USD")), "USD"))
                    .isEqualTo(new Money(42, "USD"));
        }

        @Test
        @DisplayName("a different currency → IllegalArgumentException (also needs Task 1)")
        void mismatch() {
            List<Money> values = Arrays.asList(new Money(100, "EUR"), new Money(5, "USD"));
            assertThatThrownBy(() -> Money.sum(values, "EUR")).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the input amounts are not changed (also needs Task 1)")
        void inputsUnchanged() {
            Money first = new Money(100, "EUR");
            Money second = new Money(250, "EUR");
            Money.sum(Arrays.asList(first, second), "EUR");
            assertThat(first.cents()).isEqualTo(100);
            assertThat(second.cents()).isEqualTo(250);
        }
    }

    // ── Task 4: Profile.withTag ───────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement withTag()")
    @DisplayName("Task 4 — Profile.withTag()")
    class WithTagTest {

        @Test
        @DisplayName("appends the tag and keeps the name")
        void appends() {
            Profile updated = new Profile("Ann", Arrays.asList("java")).withTag("spring");
            assertThat(updated.name()).isEqualTo("Ann");
            assertThat(updated.tags()).containsExactly("java", "spring");
        }

        @Test
        @DisplayName("works on an empty tag list")
        void emptyTags() {
            Profile updated = new Profile("Ann", new ArrayList<String>()).withTag("java");
            assertThat(updated.tags()).containsExactly("java");
        }

        @Test
        @DisplayName("returns a new object and leaves the original untouched")
        void returnsNew() {
            Profile original = new Profile("Ann", Arrays.asList("java"));
            Profile updated = original.withTag("spring");
            assertThat(updated).isNotSameAs(original);
            assertThat(original.tags()).containsExactly("java");
        }

        @Test
        @DisplayName("the returned profile's tags are read-only")
        void resultIsReadOnly() {
            Profile updated = new Profile("Ann", Arrays.asList("java")).withTag("spring");
            assertThatThrownBy(() -> updated.tags().add("x")).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("a blank tag → IllegalArgumentException")
        void blankTag() {
            assertThatThrownBy(() -> new Profile("Ann", Arrays.asList("java")).withTag(" "))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a null tag → IllegalArgumentException")
        void nullTag() {
            assertThatThrownBy(() -> new Profile("Ann", Arrays.asList("java")).withTag(null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 5: isImmutableShape ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isImmutableShape()")
    @DisplayName("Task 5 — isImmutableShape()")
    class IsImmutableShapeTest {

        @Test
        @DisplayName("Money and Profile → true")
        void immutableClasses() {
            assertThat(tasks.isImmutableShape(Money.class)).isTrue();
            assertThat(tasks.isImmutableShape(Profile.class)).isTrue();
        }

        @Test
        @DisplayName("Counter → false (not final, mutable field)")
        void counter() {
            assertThat(tasks.isImmutableShape(Counter.class)).isFalse();
        }

        @Test
        @DisplayName("JDK: Integer → true, ArrayList → false")
        void jdkTypes() {
            assertThat(tasks.isImmutableShape(Integer.class)).isTrue();
            assertThat(tasks.isImmutableShape(ArrayList.class)).isFalse();
        }

        @Test
        @DisplayName("a final class with a public field → false")
        void publicField() {
            assertThat(tasks.isImmutableShape(WithPublicFinalField.class)).isFalse();
        }

        @Test
        @DisplayName("a final class with a non-final private field → false")
        void mutablePrivateField() {
            assertThat(tasks.isImmutableShape(WithMutablePrivateField.class)).isFalse();
        }

        @Test
        @DisplayName("a non-final class with only private final fields → false")
        void notFinalClass() {
            assertThat(tasks.isImmutableShape(NotFinalButTidy.class)).isFalse();
        }

        @Test
        @DisplayName("a final class without fields → true; a record → true")
        void noFieldsAndRecord() {
            assertThat(tasks.isImmutableShape(NoFields.class)).isTrue();
            assertThat(tasks.isImmutableShape(Pair.class)).isTrue();
        }
    }
}
