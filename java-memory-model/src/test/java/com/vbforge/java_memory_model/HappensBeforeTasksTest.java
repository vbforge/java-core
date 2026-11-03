package com.vbforge.java_memory_model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HappensBeforeTasks — your implementations")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class HappensBeforeTasksTest {

    private HappensBeforeTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new HappensBeforeTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement sumPublishedViaVolatile()")
    @DisplayName("sumPublishedViaVolatile")
    class SumPublishedViaVolatileTest {

        @Test
        @DisplayName("n = 4 gives 10")
        void javadocExample() {
            assertThat(tasks.sumPublishedViaVolatile(4)).isEqualTo(10L);
        }

        @Test
        @DisplayName("n = 0 gives 0")
        void empty() {
            assertThat(tasks.sumPublishedViaVolatile(0)).isZero();
        }

        @Test
        @DisplayName("n = 1 gives 1")
        void single() {
            assertThat(tasks.sumPublishedViaVolatile(1)).isEqualTo(1L);
        }

        @ParameterizedTest(name = "n = {0} -> {1}")
        @DisplayName("sum matches the closed formula, run after run")
        @CsvSource({"10,55", "100,5050", "1000,500500", "100000,5000050000"})
        void parametrized(int n, long expected) {
            for (int run = 0; run < 20; run++) {
                assertThat(tasks.sumPublishedViaVolatile(n)).isEqualTo(expected);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement squaresFromWorker()")
    @DisplayName("squaresFromWorker")
    class SquaresFromWorkerTest {

        @Test
        @DisplayName("n = 5 gives [0, 1, 4, 9, 16]")
        void javadocExample() {
            assertThat(tasks.squaresFromWorker(5)).containsExactly(0, 1, 4, 9, 16);
        }

        @Test
        @DisplayName("n = 0 gives an empty array")
        void empty() {
            assertThat(tasks.squaresFromWorker(0)).isEmpty();
        }

        @Test
        @DisplayName("n = 1 gives [0]")
        void single() {
            assertThat(tasks.squaresFromWorker(1)).containsExactly(0);
        }

        @Test
        @DisplayName("large arrays are completely filled, run after run")
        void largeArray() {
            for (int run = 0; run < 20; run++) {
                int[] result = tasks.squaresFromWorker(10_000);

                assertThat(result).hasSize(10_000);
                assertThat(result[9_999]).isEqualTo(9_999 * 9_999);
                assertThat(result[1234]).isEqualTo(1234 * 1234);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement relayThroughThree()")
    @DisplayName("relayThroughThree")
    class RelayThroughThreeTest {

        @Test
        @DisplayName("seed 1 gives [1, 2, 3]")
        void javadocExample() {
            assertThat(tasks.relayThroughThree(1)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("seed 0 gives [0, 0, 0]")
        void zeroSeed() {
            assertThat(tasks.relayThroughThree(0)).containsExactly(0, 0, 0);
        }

        @ParameterizedTest(name = "seed = {0}")
        @DisplayName("result is always [seed, 2*seed, 3*seed], run after run")
        @CsvSource({"10", "-3", "12345"})
        void parametrized(int seed) {
            for (int run = 0; run < 20; run++) {
                assertThat(tasks.relayThroughThree(seed)).containsExactly(seed, 2 * seed, 3 * seed);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement pingPong()")
    @DisplayName("pingPong")
    class PingPongTest {

        @Test
        @DisplayName("3 rounds alternate ping, pong")
        void javadocExample() {
            assertThat(tasks.pingPong(3))
                    .containsExactly("ping", "pong", "ping", "pong", "ping", "pong");
        }

        @Test
        @DisplayName("0 rounds give an empty list")
        void noRounds() {
            assertThat(tasks.pingPong(0)).isEmpty();
        }

        @Test
        @DisplayName("1 round gives [ping, pong]")
        void singleRound() {
            assertThat(tasks.pingPong(1)).containsExactly("ping", "pong");
        }

        @Test
        @DisplayName("1000 rounds strictly alternate and no append is lost")
        void manyRounds() {
            List<String> expected = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                expected.add("ping");
                expected.add("pong");
            }

            assertThat(tasks.pingPong(1000)).containsExactlyElementsOf(expected);
        }
    }
}
