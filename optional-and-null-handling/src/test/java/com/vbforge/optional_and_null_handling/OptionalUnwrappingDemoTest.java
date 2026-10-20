package com.vbforge.optional_and_null_handling;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("OptionalUnwrappingDemo — API contracts")
class OptionalUnwrappingDemoTest {

    @Nested
    @DisplayName("1. orElse(default)")
    class OrElseTest {

        @Test
        @DisplayName("returns the value when present")
        void presentValue() {
            assertThat(OptionalUnwrappingDemo.orElseDemo(Optional.of("v"), new AtomicInteger())).isEqualTo("v");
        }

        @Test
        @DisplayName("returns the default when empty")
        void emptyDefault() {
            assertThat(OptionalUnwrappingDemo.orElseDemo(Optional.<String>empty(), new AtomicInteger()))
                    .isEqualTo("default");
        }

        @Test
        @DisplayName("default is evaluated EAGERLY — even when the value is present")
        void defaultIsEager() {
            AtomicInteger calls = new AtomicInteger();

            OptionalUnwrappingDemo.orElseDemo(Optional.of("v"), calls);

            assertThat(calls.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("2. orElseGet(supplier)")
    class OrElseGetTest {

        @Test
        @DisplayName("returns the value and never runs the supplier when present")
        void presentIsLazy() {
            AtomicInteger calls = new AtomicInteger();

            String result = OptionalUnwrappingDemo.orElseGetDemo(Optional.of("v"), calls);

            assertThat(result).isEqualTo("v");
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("runs the supplier exactly once when empty")
        void emptyRunsSupplierOnce() {
            AtomicInteger calls = new AtomicInteger();

            String result = OptionalUnwrappingDemo.orElseGetDemo(Optional.<String>empty(), calls);

            assertThat(result).isEqualTo("default");
            assertThat(calls.get()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("3. orElseThrow(supplier)")
    class OrElseThrowTest {

        @Test
        @DisplayName("returns the value when present")
        void presentValue() {
            assertThat(OptionalUnwrappingDemo.orElseThrowDemo(Optional.of("v"), "unused")).isEqualTo("v");
        }

        @Test
        @DisplayName("throws the supplied exception with its message when empty")
        void emptyThrowsWithMessage() {
            assertThatThrownBy(() -> OptionalUnwrappingDemo.orElseThrowDemo(Optional.<String>empty(), "user not found"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("user not found");
        }

        @Test
        @DisplayName("a constructor reference can act as the supplier")
        void constructorReference() {
            assertThatThrownBy(() -> OptionalUnwrappingDemo.orElseThrowRefDemo(Optional.<String>empty()))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    @Nested
    @DisplayName("4. orElse(null)")
    class OrElseNullTest {

        @Test
        @DisplayName("hands an absent value back as null")
        void emptyBecomesNull() {
            assertThat(OptionalUnwrappingDemo.orElseNullDemo(Optional.<String>empty())).isNull();
        }

        @Test
        @DisplayName("still returns the value when present")
        void presentValue() {
            assertThat(OptionalUnwrappingDemo.orElseNullDemo(Optional.of("v"))).isEqualTo("v");
        }
    }

    @Nested
    @DisplayName("5. ifPresent(consumer)")
    class IfPresentTest {

        @Test
        @DisplayName("runs the action when present")
        void presentRuns() {
            List<String> sink = new ArrayList<String>();

            OptionalUnwrappingDemo.ifPresentDemo(Optional.of("a"), sink);

            assertThat(sink).containsExactly("a");
        }

        @Test
        @DisplayName("does nothing when empty")
        void emptyDoesNothing() {
            List<String> sink = new ArrayList<String>();

            OptionalUnwrappingDemo.ifPresentDemo(Optional.<String>empty(), sink);

            assertThat(sink).isEmpty();
        }

        @Test
        @DisplayName("Java 8 if/else emulation of ifPresentOrElse handles both branches")
        void ifPresentOrElseEmulation() {
            List<String> sink = new ArrayList<String>();

            OptionalUnwrappingDemo.ifPresentOrElseJava8Demo(Optional.of("b"), sink);
            OptionalUnwrappingDemo.ifPresentOrElseJava8Demo(Optional.<String>empty(), sink);

            assertThat(sink).containsExactly("found:b", "missing");
        }
    }

    @Nested
    @DisplayName("6. Fallback to another Optional")
    class FallbackTest {

        @Test
        @DisplayName("primary wins and the secondary supplier is never called")
        void primaryWins() {
            AtomicInteger calls = new AtomicInteger();

            Optional<String> result = OptionalUnwrappingDemo.fallbackDemo(Optional.of("first"), () -> {
                calls.incrementAndGet();
                return Optional.of("second");
            });

            assertThat(result).contains("first");
            assertThat(calls.get()).isZero();
        }

        @Test
        @DisplayName("secondary is used when the primary is empty")
        void secondaryUsed() {
            Optional<String> result = OptionalUnwrappingDemo.fallbackDemo(
                    Optional.<String>empty(), () -> Optional.of("second"));

            assertThat(result).contains("second");
        }

        @Test
        @DisplayName("both empty gives empty")
        void bothEmpty() {
            Optional<String> result = OptionalUnwrappingDemo.fallbackDemo(
                    Optional.<String>empty(), () -> Optional.<String>empty());

            assertThat(result).isEmpty();
        }
    }
}
