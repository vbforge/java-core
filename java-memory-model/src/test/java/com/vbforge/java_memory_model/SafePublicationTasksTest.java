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

@DisplayName("SafePublicationTasks — your implementations")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class SafePublicationTasksTest {

    private SafePublicationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new SafePublicationTasks();
    }

    private static List<Integer> oneTo(int n) {
        List<Integer> expected = new ArrayList<>();
        for (int i = 1; i <= n; i++) {
            expected.add(i);
        }
        return expected;
    }

    @Nested
    @Disabled("Remove @Disabled when you implement publishViaVolatileField()")
    @DisplayName("publishViaVolatileField")
    class PublishViaVolatileFieldTest {

        @Test
        @DisplayName("n = 3 gives [1, 2, 3]")
        void javadocExample() {
            assertThat(tasks.publishViaVolatileField(3)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("n = 0 gives an empty list")
        void empty() {
            assertThat(tasks.publishViaVolatileField(0)).isEmpty();
        }

        @Test
        @DisplayName("n = 1 gives [1]")
        void single() {
            assertThat(tasks.publishViaVolatileField(1)).containsExactly(1);
        }

        @ParameterizedTest(name = "n = {0}")
        @DisplayName("the consumer always receives the complete list, run after run")
        @CsvSource({"10", "1000", "50000"})
        void parametrized(int n) {
            List<Integer> expected = oneTo(n);
            for (int run = 0; run < 20; run++) {
                assertThat(tasks.publishViaVolatileField(n)).containsExactlyElementsOf(expected);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement publishViaConcurrentMap()")
    @DisplayName("publishViaConcurrentMap")
    class PublishViaConcurrentMapTest {

        @Test
        @DisplayName("n = 3 gives [1, 2, 3]")
        void javadocExample() {
            assertThat(tasks.publishViaConcurrentMap(3)).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("n = 0 gives an empty list")
        void empty() {
            assertThat(tasks.publishViaConcurrentMap(0)).isEmpty();
        }

        @Test
        @DisplayName("n = 1 gives [1]")
        void single() {
            assertThat(tasks.publishViaConcurrentMap(1)).containsExactly(1);
        }

        @ParameterizedTest(name = "n = {0}")
        @DisplayName("the consumer always receives the complete list, run after run")
        @CsvSource({"10", "1000", "50000"})
        void parametrized(int n) {
            List<Integer> expected = oneTo(n);
            for (int run = 0; run < 20; run++) {
                assertThat(tasks.publishViaConcurrentMap(n)).containsExactlyElementsOf(expected);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement valuesSeenByEscapedThis()")
    @DisplayName("valuesSeenByEscapedThis")
    class ValuesSeenByEscapedThisTest {

        @Test
        @DisplayName("99 gives [0, 99]")
        void javadocExample() {
            assertThat(tasks.valuesSeenByEscapedThis(99)).containsExactly(0, 99);
        }

        @Test
        @DisplayName("-5 gives [0, -5]")
        void negativeValue() {
            assertThat(tasks.valuesSeenByEscapedThis(-5)).containsExactly(0, -5);
        }

        @Test
        @DisplayName("0 gives [0, 0]")
        void zeroValue() {
            assertThat(tasks.valuesSeenByEscapedThis(0)).containsExactly(0, 0);
        }

        @Test
        @DisplayName("Integer.MAX_VALUE survives the safe variant; the leaked read is still 0")
        void boundaryValue() {
            assertThat(tasks.valuesSeenByEscapedThis(Integer.MAX_VALUE)).containsExactly(0, Integer.MAX_VALUE);
        }
    }
}
