package com.vbforge.java_memory_model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Only the guarantees of the Java Memory Model are asserted here. Whether PLAIN fields
 * ever produce a reordered outcome depends on hardware and JIT, so those results are bounded, never pinned.
 */
@DisplayName("ReorderingDemo — API contracts")
@Timeout(value = 120, unit = TimeUnit.SECONDS)
class ReorderingDemoTest {

    private static final int ROUNDS = 2_000;

    @Nested
    @DisplayName("1. As-if-serial")
    class AsIfSerial {

        @Test
        @DisplayName("a single thread always sees its own writes in program order")
        void singleThreadView() {
            assertThat(ReorderingDemo.asIfSerialDemo()).containsExactly(1, 2, 10);
        }
    }

    @Nested
    @DisplayName("2. Store buffering litmus")
    class StoreBuffering {

        @Test
        @DisplayName("volatile fields: (0,0) never occurs")
        void volatileForbidsBothZero() {
            int[] outcomes = ReorderingDemo.storeBuffering(true, ROUNDS);

            assertThat(outcomes[0]).isZero();
        }

        @Test
        @DisplayName("volatile fields: every round lands in exactly one outcome bucket")
        void volatileCountsAddUp() {
            int[] outcomes = ReorderingDemo.storeBuffering(true, ROUNDS);

            assertThat(outcomes).hasSize(4);
            assertThat(Arrays.stream(outcomes).sum()).isEqualTo(ROUNDS);
        }

        @Test
        @DisplayName("plain fields: every round lands in exactly one outcome bucket (distribution not asserted)")
        void plainCountsAddUp() {
            int[] outcomes = ReorderingDemo.storeBuffering(false, ROUNDS);

            assertThat(outcomes).hasSize(4);
            assertThat(Arrays.stream(outcomes).sum()).isEqualTo(ROUNDS);
        }

        @Test
        @DisplayName("zero iterations give all-zero counts")
        void zeroIterations() {
            assertThat(ReorderingDemo.storeBuffering(true, 0)).containsExactly(0, 0, 0, 0);
        }
    }

    @Nested
    @DisplayName("3. Message passing litmus")
    class MessagePassing {

        @Test
        @DisplayName("volatile flag: the reader never sees the flag with stale data")
        void volatileForbidsStaleData() {
            assertThat(ReorderingDemo.countStaleReads(true, ROUNDS)).isZero();
        }

        @Test
        @DisplayName("plain flag: result is bounded by the number of rounds (value itself is hardware-dependent)")
        void plainIsBounded() {
            assertThat(ReorderingDemo.countStaleReads(false, ROUNDS)).isBetween(0, ROUNDS);
        }

        @Test
        @DisplayName("zero iterations give zero stale reads")
        void zeroIterations() {
            assertThat(ReorderingDemo.countStaleReads(true, 0)).isZero();
            assertThat(ReorderingDemo.countStaleReads(false, 0)).isZero();
        }
    }

    @Nested
    @DisplayName("4. Same test under a lock")
    class UnderLock {

        @Test
        @DisplayName("a shared lock rules out the stale outcome")
        void lockForbidsStaleData() {
            assertThat(ReorderingDemo.countStaleReadsSynchronized(ROUNDS)).isZero();
        }
    }
}
