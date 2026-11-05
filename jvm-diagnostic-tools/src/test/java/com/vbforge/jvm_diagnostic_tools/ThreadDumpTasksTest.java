package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ThreadDumpTasks — your implementations")
class ThreadDumpTasksTest {

    private static final String DUMP = ThreadDumpDemo.SAMPLE_DUMP;

    private ThreadDumpTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ThreadDumpTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement extractThreadName()")
    @DisplayName("extractThreadName")
    class ExtractThreadName {

        @Test
        @DisplayName("reads a simple name (Javadoc example)")
        void simpleName() {
            assertThat(tasks.extractThreadName("\"worker-1\" #12 prio=5 os_prio=0 tid=0x00007f1c nid=0x1a40 runnable"))
                    .isEqualTo("worker-1");
        }

        @Test
        @DisplayName("keeps spaces and # inside the name (Javadoc example)")
        void nameWithSpaces() {
            assertThat(tasks.extractThreadName("\"GC Thread#0\" os_prio=0 tid=0x00007f1c nid=0x1a2c runnable"))
                    .isEqualTo("GC Thread#0");
        }

        @Test
        @DisplayName("a frame line is not a header (Javadoc example)")
        void frameLine() {
            assertThat(tasks.extractThreadName("   at java.lang.Thread.sleep(Native Method)")).isNull();
        }

        @Test
        @DisplayName("an empty line is not a header")
        void emptyLine() {
            assertThat(tasks.extractThreadName("")).isNull();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement nidToDecimal()")
    @DisplayName("nidToDecimal")
    class NidToDecimal {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "'... nid=0x1a40 waiting for monitor entry [0x00007f]', 6720",
                "'... nid=0x1a2b runnable', 6699",
                "'... nid=0xff', 255",
                "'\"no-id-here\" prio=5', -1"
        })
        @DisplayName("converts hexadecimal to decimal (Javadoc examples)")
        void javadocExamples(String header, long expected) {
            assertThat(tasks.nidToDecimal(header)).isEqualTo(expected);
        }

        @Test
        @DisplayName("works on a real header from the sample dump")
        void realHeader() {
            String header = DUMP.split("\n")[0];
            assertThat(tasks.nidToDecimal(header)).isEqualTo(6699L);
        }

        @Test
        @DisplayName("uppercase hex digits are accepted")
        void uppercaseHex() {
            assertThat(tasks.nidToDecimal("\"t\" nid=0x1A2B runnable")).isEqualTo(6699L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement countByState()")
    @DisplayName("countByState")
    class CountByState {

        @Test
        @DisplayName("counts the sample dump (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.countByState(DUMP))
                    .hasSize(3)
                    .containsEntry("TIMED_WAITING", 1)
                    .containsEntry("BLOCKED", 2)
                    .containsEntry("RUNNABLE", 1);
        }

        @Test
        @DisplayName("empty text gives an empty map (Javadoc example)")
        void emptyText() {
            assertThat(tasks.countByState("")).isEmpty();
        }

        @Test
        @DisplayName("JVM-internal threads without a state line are not counted")
        void internalThreadsIgnored() {
            String dump = "\"GC Thread#0\" os_prio=0 nid=0x1 runnable\n";
            assertThat(tasks.countByState(dump)).isEmpty();
        }

        @Test
        @DisplayName("the detail in parentheses is not part of the state")
        void detailIgnored() {
            String dump = "\"a\" nid=0x1 waiting\n   java.lang.Thread.State: WAITING (on object monitor)\n\n"
                    + "\"b\" nid=0x2 waiting\n   java.lang.Thread.State: WAITING (parking)\n";
            assertThat(tasks.countByState(dump)).hasSize(1).containsEntry("WAITING", 2);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement findBlockedThreads()")
    @DisplayName("findBlockedThreads")
    class FindBlockedThreads {

        @Test
        @DisplayName("lists the blocked threads in dump order (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.findBlockedThreads(DUMP)).containsExactly("worker-1", "worker-3");
        }

        @Test
        @DisplayName("no blocked thread gives an empty list (Javadoc example)")
        void noneBlocked() {
            String dump = "\"a\" nid=0x1 runnable\n   java.lang.Thread.State: RUNNABLE\n\n"
                    + "\"b\" nid=0x2 waiting\n   java.lang.Thread.State: TIMED_WAITING (sleeping)\n";
            assertThat(tasks.findBlockedThreads(dump)).isEmpty();
        }

        @Test
        @DisplayName("empty text gives an empty list")
        void emptyText() {
            assertThat(tasks.findBlockedThreads("")).isEmpty();
        }

        @Test
        @DisplayName("WAITING threads are not BLOCKED")
        void waitingIsNotBlocked() {
            String dump = "\"w\" nid=0x1 waiting\n   java.lang.Thread.State: WAITING (parking)\n";
            assertThat(tasks.findBlockedThreads(dump)).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement topFrame()")
    @DisplayName("topFrame")
    class TopFrame {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "main, java.lang.Thread.sleep(Native Method)",
                "worker-1, com.example.Account.withdraw(Account.java:30)",
                "worker-2, java.net.SocketInputStream.socketRead0(Native Method)"
        })
        @DisplayName("returns the first frame (Javadoc examples)")
        void javadocExamples(String thread, String expected) {
            assertThat(tasks.topFrame(DUMP, thread)).isEqualTo(expected);
        }

        @Test
        @DisplayName("an internal thread has no frames (Javadoc example)")
        void internalThread() {
            assertThat(tasks.topFrame(DUMP, "GC Thread#0")).isNull();
        }

        @Test
        @DisplayName("an unknown thread gives null (Javadoc example)")
        void unknownThread() {
            assertThat(tasks.topFrame(DUMP, "nobody")).isNull();
        }

        @Test
        @DisplayName("lock lines are not mistaken for frames")
        void lockLinesSkipped() {
            String dump = "\"t\" nid=0x1 waiting\n   java.lang.Thread.State: BLOCKED\n"
                    + "\t- waiting to lock <0x1> (a X)\n\tat A.b(A.java:1)\n";
            assertThat(tasks.topFrame(dump, "t")).isEqualTo("A.b(A.java:1)");
        }
    }
}
