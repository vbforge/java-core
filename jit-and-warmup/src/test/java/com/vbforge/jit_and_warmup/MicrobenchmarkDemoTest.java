package com.vbforge.jit_and_warmup;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("MicrobenchmarkDemo — API contracts")
class MicrobenchmarkDemoTest {

    @Nested
    @DisplayName("1. The naive stopwatch")
    class NaiveStopwatch {

        @Test
        @DisplayName("timeOnceNanos runs the work exactly once")
        void runsOnce() {
            AtomicInteger calls = new AtomicInteger();
            MicrobenchmarkDemo.timeOnceNanos(() -> {
                calls.incrementAndGet();
                return 1L;
            });
            assertThat(calls.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("timeOnceNanos returns a non-negative time")
        void nonNegative() {
            assertThat(MicrobenchmarkDemo.timeOnceNanos(() -> 1L)).isGreaterThanOrEqualTo(0L);
        }

        @Test
        @DisplayName("timeOnceNanos consumes the result of the work")
        void consumesResult() {
            MicrobenchmarkDemo.timeOnceNanos(() -> 12_345L);
            assertThat(MicrobenchmarkDemo.sink()).isEqualTo(12_345L);
        }
    }

    @Nested
    @DisplayName("2. Dead-code elimination")
    class DeadCode {

        @ParameterizedTest
        @CsvSource({"0, 0", "1, 0", "4, 14", "100000, 333328333350000"})
        @DisplayName("squareSum adds i*i for i in 0..n-1")
        void squareSum(int n, long expected) {
            assertThat(MicrobenchmarkDemo.squareSum(n)).isEqualTo(expected);
        }

        @Test
        @DisplayName("consume stores the value in the sink")
        void consumeStores() {
            MicrobenchmarkDemo.consume(42L);
            assertThat(MicrobenchmarkDemo.sink()).isEqualTo(42L);
        }

        @Test
        @DisplayName("A discarded computation leaves no trace in the sink")
        void discardedLeavesNoTrace() {
            MicrobenchmarkDemo.consume(7L);
            MicrobenchmarkDemo.computeAndDiscard(1_000);
            assertThat(MicrobenchmarkDemo.sink()).isEqualTo(7L);
        }

        @Test
        @DisplayName("A consumed computation is observable")
        void consumedIsObservable() {
            MicrobenchmarkDemo.consume(MicrobenchmarkDemo.squareSum(10));
            assertThat(MicrobenchmarkDemo.sink()).isEqualTo(285L);
        }
    }

    @Nested
    @DisplayName("3. A minimal harness")
    class Harness {

        @Test
        @DisplayName("Work runs warm-up plus measured iterations")
        void totalCalls() {
            AtomicInteger calls = new AtomicInteger();
            MicrobenchmarkDemo.runHarness(() -> {
                calls.incrementAndGet();
                return 1L;
            }, 3, 5);
            assertThat(calls.get()).isEqualTo(8);
        }

        @Test
        @DisplayName("Only measured iterations are returned")
        void onlyMeasuredReturned() {
            long[] samples = MicrobenchmarkDemo.runHarness(() -> 1L, 10, 4);
            assertThat(samples).hasSize(4);
            for (long s : samples) {
                assertThat(s).isGreaterThanOrEqualTo(0L);
            }
        }

        @Test
        @DisplayName("Zero warm-up iterations is allowed")
        void zeroWarmup() {
            AtomicInteger calls = new AtomicInteger();
            long[] samples = MicrobenchmarkDemo.runHarness(() -> {
                calls.incrementAndGet();
                return 1L;
            }, 0, 2);
            assertThat(samples).hasSize(2);
            assertThat(calls.get()).isEqualTo(2);
        }

        @Test
        @DisplayName("Invalid iteration counts are rejected")
        void invalid() {
            assertThatThrownBy(() -> MicrobenchmarkDemo.runHarness(() -> 1L, -1, 5))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> MicrobenchmarkDemo.runHarness(() -> 1L, 5, 0))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("4. JMH, first look")
    class JmhFirstLook {

        @Test
        @DisplayName("The example uses the core JMH annotations")
        void coreAnnotations() {
            assertThat(MicrobenchmarkDemo.jmhExampleSource())
                    .contains("@State", "@Benchmark", "@Warmup", "@Measurement", "@Fork", "@Setup", "@Param");
        }

        @Test
        @DisplayName("The example shows both ways of consuming results")
        void consumesResults() {
            assertThat(MicrobenchmarkDemo.jmhExampleSource())
                    .contains("return sum;")
                    .contains("Blackhole bh")
                    .contains("bh.consume(sum)");
        }
    }
}
