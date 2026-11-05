package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ThreadDumpDemo — API contracts")
class ThreadDumpDemoTest {

    @Nested
    @DisplayName("1. Taking a snapshot in-process")
    class Snapshot {

        @Test
        @DisplayName("the calling thread is RUNNABLE in its own snapshot")
        void currentThreadRunnable() {
            Map<Thread.State, Integer> counts = ThreadDumpDemo.threadStateCounts();
            assertThat(counts.get(Thread.State.RUNNABLE)).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("state counts are never zero or negative")
        void countsPositive() {
            assertThat(ThreadDumpDemo.threadStateCounts().values()).allMatch(count -> count > 0);
        }

        @Test
        @DisplayName("getAllStackTraces contains the current thread")
        void currentThreadInSnapshot() {
            assertThat(ThreadDumpDemo.currentThreadInSnapshot()).isTrue();
        }

        @Test
        @DisplayName("first element of getStackTrace is getStackTrace itself")
        void firstFrameIsGetStackTrace() {
            assertThat(ThreadDumpDemo.firstFrameMethodName()).isEqualTo("getStackTrace");
        }
    }

    @Nested
    @DisplayName("2. The thread states, observed for real")
    class States {

        @Test
        @DisplayName("each scenario produces the expected state")
        void observedStates() throws InterruptedException {
            Map<String, Thread.State> observed = ThreadDumpDemo.observeThreadStates();
            assertThat(observed)
                    .containsEntry("new", Thread.State.NEW)
                    .containsEntry("sleeping", Thread.State.TIMED_WAITING)
                    .containsEntry("waiting", Thread.State.WAITING)
                    .containsEntry("blocked", Thread.State.BLOCKED)
                    .containsEntry("finished", Thread.State.TERMINATED);
        }

        @Test
        @DisplayName("exactly five scenarios are reported")
        void fiveScenarios() throws InterruptedException {
            assertThat(ThreadDumpDemo.observeThreadStates()).hasSize(5);
        }
    }

    @Nested
    @DisplayName("3. Reading a jstack dump")
    class ReadingDump {

        @Test
        @DisplayName("splitBlocks finds every thread, including JVM-internal ones")
        void splitsIntoFiveBlocks() {
            assertThat(ThreadDumpDemo.splitBlocks(ThreadDumpDemo.SAMPLE_DUMP)).hasSize(5);
        }

        @Test
        @DisplayName("each block starts with the quoted thread name")
        void blocksStartWithQuote() {
            assertThat(ThreadDumpDemo.splitBlocks(ThreadDumpDemo.SAMPLE_DUMP)).allMatch(b -> b.startsWith("\""));
        }

        @Test
        @DisplayName("stateOf returns only the state word, without the detail")
        void stateWithoutDetail() {
            List<String> blocks = ThreadDumpDemo.splitBlocks(ThreadDumpDemo.SAMPLE_DUMP);
            assertThat(ThreadDumpDemo.stateOf(blocks.get(0))).isEqualTo("TIMED_WAITING");
            assertThat(ThreadDumpDemo.stateOf(blocks.get(1))).isEqualTo("BLOCKED");
            assertThat(ThreadDumpDemo.stateOf(blocks.get(2))).isEqualTo("RUNNABLE");
        }

        @Test
        @DisplayName("JVM-internal threads have no state line")
        void internalThreadHasNoState() {
            List<String> blocks = ThreadDumpDemo.splitBlocks(ThreadDumpDemo.SAMPLE_DUMP);
            assertThat(ThreadDumpDemo.stateOf(blocks.get(4))).isNull();
        }

        @Test
        @DisplayName("splitBlocks ignores text that is not a thread block")
        void ignoresGarbage() {
            assertThat(ThreadDumpDemo.splitBlocks("Full thread dump OpenJDK 64-Bit Server VM:\n\n")).isEmpty();
        }
    }

    @Nested
    @DisplayName("4. Deadlock detection")
    class Deadlock {

        @Test
        @DisplayName("the JVM reports both deadlocked threads")
        void detectsBothThreads() throws InterruptedException {
            assertThat(ThreadDumpDemo.provokeAndDetectDeadlock("deadlock-a"))
                    .containsExactly("deadlock-a-1", "deadlock-a-2");
        }

        @Test
        @DisplayName("results are filtered by prefix, so a second deadlock is reported separately")
        void filteredByPrefix() throws InterruptedException {
            assertThat(ThreadDumpDemo.provokeAndDetectDeadlock("deadlock-b"))
                    .containsExactly("deadlock-b-1", "deadlock-b-2");
        }
    }
}
