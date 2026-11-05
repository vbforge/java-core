package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JpsAndJcmdTasks — your implementations")
class JpsAndJcmdTasksTest {

    private JpsAndJcmdTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new JpsAndJcmdTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parsePids()")
    @DisplayName("parsePids")
    class ParsePids {

        @Test
        @DisplayName("lists the pids and drops the jps line (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.parsePids(JpsAndJcmdDemo.SAMPLE_JPS_OUTPUT)).containsExactly(4711L, 5120L);
        }

        @Test
        @DisplayName("a lone Jps line gives an empty list")
        void onlyJps() {
            assertThat(tasks.parsePids("6003 Jps\n")).isEmpty();
        }

        @Test
        @DisplayName("empty text gives an empty list")
        void emptyText() {
            assertThat(tasks.parsePids("")).isEmpty();
        }

        @Test
        @DisplayName("blank lines are ignored")
        void blankLines() {
            assertThat(tasks.parsePids("\n42 App\n\n  \n43 Other\n")).containsExactly(42L, 43L);
        }

        @Test
        @DisplayName("a line with only a pid still counts")
        void pidOnly() {
            assertThat(tasks.parsePids("42\n")).containsExactly(42L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement findPidByMainClass()")
    @DisplayName("findPidByMainClass")
    class FindPidByMainClass {

        @ParameterizedTest(name = "\"{0}\" → {1}")
        @CsvSource({
                "Order, 4711",
                "Gradle, 5120",
                "Jps, -1",
                "Missing, -1"
        })
        @DisplayName("matches by part of the main class name (Javadoc examples)")
        void javadocExamples(String part, long expectedPid) {
            assertThat(tasks.findPidByMainClass(JpsAndJcmdDemo.SAMPLE_JPS_OUTPUT, part)).isEqualTo(expectedPid);
        }

        @Test
        @DisplayName("returns the first match when several JVMs match")
        void firstMatchWins() {
            assertThat(tasks.findPidByMainClass("10 com.a.App\n11 com.b.App\n", "App")).isEqualTo(10L);
        }

        @Test
        @DisplayName("empty output finds nothing")
        void emptyOutput() {
            assertThat(tasks.findPidByMainClass("", "App")).isEqualTo(-1L);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement jcmdCommand()")
    @DisplayName("jcmdCommand")
    class JcmdCommand {

        @Test
        @DisplayName("command without arguments has no trailing space")
        void noArguments() {
            assertThat(tasks.jcmdCommand(4711, "Thread.print")).isEqualTo("jcmd 4711 Thread.print");
        }

        @Test
        @DisplayName("one argument is appended")
        void oneArgument() {
            assertThat(tasks.jcmdCommand(4711, "GC.heap_dump", "/tmp/app.hprof"))
                    .isEqualTo("jcmd 4711 GC.heap_dump /tmp/app.hprof");
        }

        @Test
        @DisplayName("several arguments are separated by single spaces")
        void manyArguments() {
            assertThat(tasks.jcmdCommand(4711, "JFR.start", "name=leak", "duration=60s"))
                    .isEqualTo("jcmd 4711 JFR.start name=leak duration=60s");
        }

        @Test
        @DisplayName("null or blank command is rejected")
        void invalidCommand() {
            assertThatThrownBy(() -> tasks.jcmdCommand(1, null)).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.jcmdCommand(1, "  ")).isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parseUptimeSeconds()")
    @DisplayName("parseUptimeSeconds")
    class ParseUptimeSeconds {

        @Test
        @DisplayName("reads the seconds from the second line (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.parseUptimeSeconds("4711:\n123.456 s\n")).isEqualTo(123.456);
        }

        @Test
        @DisplayName("works without a trailing newline")
        void noTrailingNewline() {
            assertThat(tasks.parseUptimeSeconds("4711:\n0.5 s")).isEqualTo(0.5);
        }

        @Test
        @DisplayName("works for whole numbers")
        void wholeNumber() {
            assertThat(tasks.parseUptimeSeconds("9:\n3600 s")).isEqualTo(3600.0);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement maxHeapBytes()")
    @DisplayName("maxHeapBytes")
    class MaxHeapBytes {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "-Xmx512m, 536870912",
                "-Xmx512M, 536870912",
                "-Xmx2g, 2147483648",
                "-Xmx1024k, 1048576",
                "-Xmx1000, 1000"
        })
        @DisplayName("understands the k, m and g suffixes (Javadoc examples)")
        void suffixes(String argument, long expected) {
            assertThat(tasks.maxHeapBytes(Collections.singletonList(argument))).isEqualTo(expected);
        }

        @Test
        @DisplayName("other arguments are ignored")
        void otherArgumentsIgnored() {
            assertThat(tasks.maxHeapBytes(Arrays.asList("-Xmx2g", "-Dfoo=bar"))).isEqualTo(2147483648L);
        }

        @Test
        @DisplayName("the last -Xmx wins")
        void lastWins() {
            assertThat(tasks.maxHeapBytes(Arrays.asList("-Xmx512m", "-Xmx1g"))).isEqualTo(1073741824L);
        }

        @Test
        @DisplayName("no -Xmx gives -1")
        void absent() {
            assertThat(tasks.maxHeapBytes(Collections.singletonList("-Dfoo=bar"))).isEqualTo(-1L);
            assertThat(tasks.maxHeapBytes(Collections.<String>emptyList())).isEqualTo(-1L);
        }

        @Test
        @DisplayName("-Xms is not -Xmx")
        void xmsIgnored() {
            assertThat(tasks.maxHeapBytes(Collections.singletonList("-Xms256m"))).isEqualTo(-1L);
        }
    }
}
