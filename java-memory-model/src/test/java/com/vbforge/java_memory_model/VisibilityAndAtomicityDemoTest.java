package com.vbforge.java_memory_model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("VisibilityAndAtomicityDemo — API contracts")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class VisibilityAndAtomicityDemoTest {

    @Nested
    @DisplayName("1. Visibility")
    class Visibility {

        @Test
        @DisplayName("volatile flag stops a worker that has been spinning for a while")
        void volatileFlagStopsWorker() {
            assertThat(VisibilityAndAtomicityDemo.volatileFlagStopsWorker(50)).isTrue();
        }

        @Test
        @DisplayName("volatile flag stops a worker even if the flag is raised immediately")
        void volatileFlagStopsWorkerImmediately() {
            assertThat(VisibilityAndAtomicityDemo.volatileFlagStopsWorker(0)).isTrue();
        }

        @Test
        @DisplayName("plain-flag demo always terminates and cleans up its thread (outcome itself is not asserted)")
        void plainFlagDemoTerminates() {
            assertThatCode(() -> VisibilityAndAtomicityDemo.plainFlagStopsWorker(50))
                    .doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Atomicity")
    class Atomicity {

        @ParameterizedTest(name = "synchronized: {0} threads x {1} increments")
        @DisplayName("synchronized counter never loses an update")
        @CsvSource({"1,1000", "2,20000", "8,5000"})
        void synchronizedNeverLosesUpdates(int threads, int perThread) {
            assertThat(VisibilityAndAtomicityDemo.synchronizedIncrement(threads, perThread))
                    .isEqualTo((long) threads * perThread);
        }

        @ParameterizedTest(name = "atomic: {0} threads x {1} increments")
        @DisplayName("AtomicLong counter never loses an update")
        @CsvSource({"1,1000", "2,20000", "8,5000"})
        void atomicNeverLosesUpdates(int threads, int perThread) {
            assertThat(VisibilityAndAtomicityDemo.atomicIncrement(threads, perThread))
                    .isEqualTo((long) threads * perThread);
        }

        @Test
        @DisplayName("volatile counter can only lose updates, never invent them")
        void volatileCounterNeverExceedsMaximum() {
            long observed = VisibilityAndAtomicityDemo.volatileIncrement(4, 50_000);

            assertThat(observed).isBetween(1L, 200_000L);
        }

        @Test
        @DisplayName("zero increments leave every counter at zero")
        void zeroIncrements() {
            assertThat(VisibilityAndAtomicityDemo.volatileIncrement(4, 0)).isZero();
            assertThat(VisibilityAndAtomicityDemo.synchronizedIncrement(4, 0)).isZero();
            assertThat(VisibilityAndAtomicityDemo.atomicIncrement(4, 0)).isZero();
        }
    }

    @Nested
    @DisplayName("3. Atomic compound actions")
    class AtomicCompoundActions {

        @Test
        @DisplayName("first compareAndSet succeeds, second with a stale expectation fails")
        void compareAndSet() {
            assertThat(VisibilityAndAtomicityDemo.atomicCompoundActionsDemo())
                    .containsEntry("firstCas", true)
                    .containsEntry("secondCas", false);
        }

        @Test
        @DisplayName("updateAndGet returns the new value, getAndAdd returns the old one")
        void newVersusOldValue() {
            assertThat(VisibilityAndAtomicityDemo.atomicCompoundActionsDemo())
                    .containsEntry("updateAndGet", 10)
                    .containsEntry("getAndAdd", 10)
                    .containsEntry("final", 13);
        }
    }
}
