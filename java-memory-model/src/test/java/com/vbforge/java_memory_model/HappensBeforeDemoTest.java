package com.vbforge.java_memory_model;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;

import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("HappensBeforeDemo — API contracts")
@Timeout(value = 60, unit = TimeUnit.SECONDS)
class HappensBeforeDemoTest {

    private final HappensBeforeDemo demo = new HappensBeforeDemo();

    @Nested
    @DisplayName("1. Program order")
    class ProgramOrder {

        @Test
        @DisplayName("dependent statements see each other's writes")
        void dependentStatements() {
            assertThat(HappensBeforeDemo.programOrderDemo()).isEqualTo(3);
        }
    }

    @Nested
    @DisplayName("2. Volatile write -> volatile read")
    class Volatile {

        @RepeatedTest(20)
        @DisplayName("plain data written before the volatile flag is visible after the flag is seen")
        void dataVisibleAfterFlag() {
            assertThat(demo.volatileDemo()).isEqualTo(42);
        }
    }

    @Nested
    @DisplayName("3. Monitor unlock -> lock")
    class Monitor {

        @RepeatedTest(20)
        @DisplayName("plain fields written under a lock are visible to the next thread holding it")
        void fieldsVisibleAfterLock() {
            assertThat(demo.monitorDemo()).isEqualTo(7);
        }
    }

    @Nested
    @DisplayName("4. Thread start / join")
    class StartAndJoin {

        @Test
        @DisplayName("thread sees the input written before start(); caller sees the output after join()")
        void startAndJoin() {
            assertThat(demo.startAndJoinDemo()).containsExactly(10, 20);
        }
    }

    @Nested
    @DisplayName("5. Transitivity")
    class Transitivity {

        @RepeatedTest(20)
        @DisplayName("writes of thread A reach main through thread B's volatile hand-off")
        void chainOfEdges() {
            assertThat(demo.transitivityDemo()).containsExactly(5, 6);
        }
    }

    @Nested
    @DisplayName("6. Executor + Future")
    class ExecutorAndFuture {

        @Test
        @DisplayName("task sees the input set before submit(); caller sees the output after get()")
        void submitAndGet() throws Exception {
            assertThat(demo.executorDemo()).containsExactly(3, 9);
        }
    }
}
