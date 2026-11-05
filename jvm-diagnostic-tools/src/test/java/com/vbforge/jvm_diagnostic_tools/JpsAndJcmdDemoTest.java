package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.management.ManagementFactory;
import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("JpsAndJcmdDemo — API contracts")
class JpsAndJcmdDemoTest {

    @Nested
    @DisplayName("1. Finding the pid")
    class Pid {

        @Test
        @DisplayName("parsePid reads the number before the @")
        void parsesNumber() {
            assertThat(JpsAndJcmdDemo.parsePid("4711@my-host")).isEqualTo(4711L);
        }

        @Test
        @DisplayName("parsePid returns -1 when there is no @")
        void noAt() {
            assertThat(JpsAndJcmdDemo.parsePid("4711")).isEqualTo(-1L);
        }

        @Test
        @DisplayName("parsePid returns -1 when the pid part is not a number")
        void notANumber() {
            assertThat(JpsAndJcmdDemo.parsePid("abc@host")).isEqualTo(-1L);
        }

        @Test
        @DisplayName("parsePid returns -1 when the pid part is empty")
        void emptyPid() {
            assertThat(JpsAndJcmdDemo.parsePid("@host")).isEqualTo(-1L);
        }

        @Test
        @DisplayName("currentPid is positive and matches the runtime name")
        void currentPidMatchesRuntimeName() {
            long expected = JpsAndJcmdDemo.parsePid(ManagementFactory.getRuntimeMXBean().getName());
            assertThat(JpsAndJcmdDemo.currentPid()).isPositive().isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("2. JVM arguments and uptime")
    class Arguments {

        @Test
        @DisplayName("hasInputArgument finds a flag by prefix")
        void findsByPrefix() {
            assertThat(JpsAndJcmdDemo.hasInputArgument(Arrays.asList("-Xmx512m", "-Dfoo=bar"), "-Xmx")).isTrue();
        }

        @Test
        @DisplayName("hasInputArgument is false for a missing prefix")
        void missingPrefix() {
            assertThat(JpsAndJcmdDemo.hasInputArgument(Arrays.asList("-Xmx512m"), "-Xms")).isFalse();
        }

        @Test
        @DisplayName("hasInputArgument is false for an empty list")
        void emptyList() {
            assertThat(JpsAndJcmdDemo.hasInputArgument(Collections.<String>emptyList(), "-Xmx")).isFalse();
        }

        @Test
        @DisplayName("jvmInputArguments returns a list (possibly empty)")
        void inputArgumentsList() {
            assertThat(JpsAndJcmdDemo.jvmInputArguments()).isNotNull();
        }

        @Test
        @DisplayName("uptime never goes backwards")
        void uptimeMonotonic() {
            long first = JpsAndJcmdDemo.uptimeMillis();
            long second = JpsAndJcmdDemo.uptimeMillis();
            assertThat(first).isGreaterThanOrEqualTo(0L);
            assertThat(second).isGreaterThanOrEqualTo(first);
        }

        @Test
        @DisplayName("start time lies in the past")
        void startTimeInPast() {
            assertThat(JpsAndJcmdDemo.startTimeMillis()).isPositive().isLessThanOrEqualTo(System.currentTimeMillis());
        }
    }

    @Nested
    @DisplayName("3. Reading jps output")
    class JpsOutput {

        @Test
        @DisplayName("parseJpsLine splits pid and main class")
        void splitsLine() {
            assertThat(JpsAndJcmdDemo.parseJpsLine("4711 com.example.OrderService"))
                    .containsExactly("4711", "com.example.OrderService");
        }

        @Test
        @DisplayName("parseJpsLine copes with a pid-only line")
        void pidOnly() {
            assertThat(JpsAndJcmdDemo.parseJpsLine("4711")).containsExactly("4711", "");
        }

        @Test
        @DisplayName("parseJpsLine trims surrounding whitespace")
        void trims() {
            assertThat(JpsAndJcmdDemo.parseJpsLine("  4711   MyApp  ")).containsExactly("4711", "MyApp");
        }

        @Test
        @DisplayName("the sample output lists jps itself")
        void sampleContainsJps() {
            assertThat(JpsAndJcmdDemo.SAMPLE_JPS_OUTPUT.split("\n")).hasSize(3);
            assertThat(JpsAndJcmdDemo.SAMPLE_JPS_OUTPUT).contains("sun.tools.jps.Jps");
        }
    }

    @Nested
    @DisplayName("4. jcmd — one tool, many commands")
    class Jcmd {

        @Test
        @DisplayName("cheat sheet covers thread dump and heap dump")
        void cheatSheetKeys() {
            assertThat(JpsAndJcmdDemo.JCMD_CHEAT_SHEET)
                    .containsKeys("Thread.print", "GC.heap_dump", "GC.class_histogram", "JFR.start");
        }

        @Test
        @DisplayName("cheat sheet cannot be modified")
        void cheatSheetImmutable() {
            assertThatThrownBy(() -> JpsAndJcmdDemo.JCMD_CHEAT_SHEET.put("x", "y"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("jvmSystemProperty matches System.getProperty")
        void systemProperty() {
            assertThat(JpsAndJcmdDemo.jvmSystemProperty("java.version")).isEqualTo(System.getProperty("java.version"));
        }

        @Test
        @DisplayName("jvmSystemProperty returns null for an unknown key")
        void unknownProperty() {
            assertThat(JpsAndJcmdDemo.jvmSystemProperty("no.such.property.key")).isNull();
        }
    }
}
