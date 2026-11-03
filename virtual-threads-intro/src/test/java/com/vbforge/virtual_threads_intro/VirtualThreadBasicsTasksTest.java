package com.vbforge.virtual_threads_intro;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("VirtualThreadBasicsTasks — your implementations")
class VirtualThreadBasicsTasksTest {

    private VirtualThreadBasicsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new VirtualThreadBasicsTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runOnVirtualThread()")
    @DisplayName("1. runOnVirtualThread")
    class RunOnVirtualThreadTest {

        @Test
        @DisplayName("example: 'worker-1' → 'worker-1:true'")
        void example() throws Exception {
            assertThat(tasks.runOnVirtualThread("worker-1")).isEqualTo("worker-1:true");
        }

        @Test
        @DisplayName("empty name → ':true'")
        void emptyName() throws Exception {
            assertThat(tasks.runOnVirtualThread("")).isEqualTo(":true");
        }

        @ParameterizedTest
        @CsvSource({
                "a,a:true",
                "job_2,job_2:true",
                "my thread,my thread:true"
        })
        @DisplayName("name is recorded from inside the thread")
        void parametrized(String name, String expected) throws Exception {
            assertThat(tasks.runOnVirtualThread(name)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement squaresOnVirtualThreads()")
    @DisplayName("2. squaresOnVirtualThreads")
    class SquaresOnVirtualThreadsTest {

        @Test
        @DisplayName("example: [1, 2, 3] → [1, 4, 9]")
        void example() throws Exception {
            assertThat(tasks.squaresOnVirtualThreads(List.of(1, 2, 3))).containsExactly(1, 4, 9);
        }

        @Test
        @DisplayName("negative numbers and zero")
        void negativesAndZero() throws Exception {
            assertThat(tasks.squaresOnVirtualThreads(List.of(-3, 0))).containsExactly(9, 0);
        }

        @Test
        @DisplayName("empty input → empty list")
        void emptyInput() throws Exception {
            assertThat(tasks.squaresOnVirtualThreads(List.of())).isEmpty();
        }

        @Test
        @DisplayName("1000 inputs keep their order")
        void keepsOrder() throws Exception {
            List<Integer> input = new ArrayList<>();
            for (int i = 0; i < 1000; i++) {
                input.add(i);
            }

            List<Integer> result = tasks.squaresOnVirtualThreads(input);

            assertThat(result).hasSize(1000);
            for (int i = 0; i < 1000; i++) {
                assertThat(result.get(i)).isEqualTo(i * i);
            }
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countDistinctThreads()")
    @DisplayName("3. countDistinctThreads")
    class CountDistinctThreadsTest {

        @Test
        @DisplayName("example: 100 tasks → 100 distinct threads")
        void example() {
            assertThat(tasks.countDistinctThreads(100)).isEqualTo(100);
        }

        @Test
        @DisplayName("zero tasks → 0")
        void zeroTasks() {
            assertThat(tasks.countDistinctThreads(0)).isZero();
        }

        @ParameterizedTest
        @ValueSource(ints = {1, 2, 10, 500})
        @DisplayName("distinct threads always equal the task count")
        void parametrized(int taskCount) {
            assertThat(tasks.countDistinctThreads(taskCount)).isEqualTo(taskCount);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement createUnstartedVirtual()")
    @DisplayName("4. createUnstartedVirtual")
    class CreateUnstartedVirtualTest {

        @Test
        @DisplayName("example: unstarted, virtual, named")
        void example() {
            Thread t = tasks.createUnstartedVirtual("job", () -> { });

            assertThat(t.getState()).isEqualTo(Thread.State.NEW);
            assertThat(t.isVirtual()).isTrue();
            assertThat(t.getName()).isEqualTo("job");
        }

        @Test
        @DisplayName("body does not run until start(), then runs exactly once")
        void runsOnlyWhenStarted() throws Exception {
            AtomicInteger runs = new AtomicInteger();
            Thread t = tasks.createUnstartedVirtual("job", runs::incrementAndGet);

            assertThat(runs.get()).isZero();

            t.start();
            t.join();

            assertThat(runs.get()).isEqualTo(1);
        }

        @Test
        @DisplayName("empty name is allowed")
        void emptyName() {
            assertThat(tasks.createUnstartedVirtual("", () -> { }).getName()).isEmpty();
        }
    }
}
