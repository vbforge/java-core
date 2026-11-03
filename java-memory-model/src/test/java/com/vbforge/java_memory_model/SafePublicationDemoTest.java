package com.vbforge.java_memory_model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Only guarantees are asserted. A mutable object published through a plain field MAY be seen half-built;
 * the test bounds that count but never requires it to be non-zero.
 */
@DisplayName("SafePublicationDemo — API contracts")
@Timeout(value = 120, unit = TimeUnit.SECONDS)
class SafePublicationDemoTest {

    private static final int ROUNDS = 2_000;

    @Nested
    @DisplayName("1. Unsafe publication")
    class UnsafePublication {

        @Test
        @DisplayName("half-built count is bounded by the number of rounds (its value is hardware-dependent)")
        void countIsBounded() {
            assertThat(SafePublicationDemo.unsafePublicationDemo(ROUNDS)).isBetween(0, ROUNDS);
        }

        @Test
        @DisplayName("zero rounds give zero half-built objects")
        void zeroRounds() {
            assertThat(SafePublicationDemo.unsafePublicationDemo(0)).isZero();
        }
    }

    @Nested
    @DisplayName("2. Final fields")
    class FinalFields {

        @Test
        @DisplayName("an immutable point is never seen half-built, even through a plain field")
        void finalFieldsAreSafe() {
            assertThat(SafePublicationDemo.finalFieldsDemo(ROUNDS)).isZero();
        }
    }

    @Nested
    @DisplayName("3. Safe publication idioms")
    class SafeIdioms {

        @Test
        @DisplayName("volatile, AtomicReference, synchronized and ConcurrentMap never show a half-built point")
        void allIdiomsAreSafe() {
            assertThat(SafePublicationDemo.safeIdiomsDemo(ROUNDS)).containsExactly(0, 0, 0, 0);
        }

        @Test
        @DisplayName("harness reports zero for an empty run")
        void emptyRun() {
            assertThat(SafePublicationDemo.safeIdiomsDemo(0)).containsExactly(0, 0, 0, 0);
        }
    }

    @Nested
    @DisplayName("4. 'this' escape")
    class ThisEscape {

        @Test
        @DisplayName("a leaked 'this' exposes the default value; a factory exposes the real one")
        void escapedThisSeesDefault() {
            assertThat(SafePublicationDemo.thisEscapeDemo(99)).containsExactly(0, 99);
        }

        @Test
        @DisplayName("negative values behave the same way")
        void negativeValue() {
            assertThat(SafePublicationDemo.thisEscapeDemo(-5)).containsExactly(0, -5);
        }
    }
}
