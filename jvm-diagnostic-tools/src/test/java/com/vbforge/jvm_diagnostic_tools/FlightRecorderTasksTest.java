package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.within;

@DisplayName("FlightRecorderTasks — your implementations")
class FlightRecorderTasksTest {

    private static final String SUMMARY =
            " Version: 2.1\n"
          + " Duration: 60 s\n"
          + "\n"
          + " Event Type                 Count  Size (bytes)\n"
          + "====================================================\n"
          + " jdk.GCPhasePause             120         12000\n"
          + " jdk.CPULoad                   60          1500\n";

    private FlightRecorderTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new FlightRecorderTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement jfrStartCommand()")
    @DisplayName("jfrStartCommand")
    class JfrStartCommand {

        @Test
        @DisplayName("builds the full command (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.jfrStartCommand(4711, "leak", 60, "leak.jfr"))
                    .isEqualTo("jcmd 4711 JFR.start name=leak duration=60s filename=leak.jfr settings=profile");
        }

        @Test
        @DisplayName("keeps an absolute file path (Javadoc example)")
        void absolutePath() {
            assertThat(tasks.jfrStartCommand(1, "a", 5, "/tmp/a.jfr"))
                    .isEqualTo("jcmd 1 JFR.start name=a duration=5s filename=/tmp/a.jfr settings=profile");
        }

        @Test
        @DisplayName("a zero or negative duration is rejected (Javadoc example)")
        void invalidDuration() {
            assertThatThrownBy(() -> tasks.jfrStartCommand(4711, "leak", 0, "x.jfr"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.jfrStartCommand(4711, "leak", -1, "x.jfr"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a null or blank name or file name is rejected")
        void invalidNames() {
            assertThatThrownBy(() -> tasks.jfrStartCommand(1, null, 5, "x.jfr"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.jfrStartCommand(1, " ", 5, "x.jfr"))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.jfrStartCommand(1, "a", 5, null))
                    .isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> tasks.jfrStartCommand(1, "a", 5, ""))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement jmxServiceUrl()")
    @DisplayName("jmxServiceUrl")
    class JmxServiceUrl {

        @ParameterizedTest(name = "{0}:{1}")
        @CsvSource({
                "localhost, 9010, service:jmx:rmi:///jndi/rmi://localhost:9010/jmxrmi",
                "10.0.0.5, 1099, service:jmx:rmi:///jndi/rmi://10.0.0.5:1099/jmxrmi"
        })
        @DisplayName("builds the URL (Javadoc examples)")
        void javadocExamples(String host, int port, String expected) {
            assertThat(tasks.jmxServiceUrl(host, port)).isEqualTo(expected);
        }

        @ParameterizedTest(name = "port {0}")
        @CsvSource({"0", "-1", "65536", "70000"})
        @DisplayName("ports outside 1..65535 are rejected (Javadoc examples)")
        void invalidPorts(int port) {
            assertThatThrownBy(() -> tasks.jmxServiceUrl("localhost", port))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("the boundary ports 1 and 65535 are valid")
        void boundaries() {
            assertThat(tasks.jmxServiceUrl("h", 1)).endsWith("h:1/jmxrmi");
            assertThat(tasks.jmxServiceUrl("h", 65535)).endsWith("h:65535/jmxrmi");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement gcOverheadPercent()")
    @DisplayName("gcOverheadPercent")
    class GcOverheadPercent {

        @ParameterizedTest(name = "{0} ms of {1} ms → {2}%")
        @CsvSource({
                "500, 10000, 5.0",
                "0, 10000, 0.0",
                "2500, 10000, 25.0",
                "100, 0, 0.0"
        })
        @DisplayName("computes the percentage (Javadoc examples)")
        void javadocExamples(long gcMillis, long uptimeMillis, double expected) {
            assertThat(tasks.gcOverheadPercent(gcMillis, uptimeMillis)).isEqualTo(expected, within(0.0001));
        }

        @Test
        @DisplayName("keeps the fraction instead of using integer division")
        void fraction() {
            assertThat(tasks.gcOverheadPercent(1, 8)).isEqualTo(12.5, within(0.0001));
        }

        @Test
        @DisplayName("a negative uptime is treated like no uptime")
        void negativeUptime() {
            assertThat(tasks.gcOverheadPercent(100, -5)).isEqualTo(0.0, within(0.0001));
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement eventCounts()")
    @DisplayName("eventCounts")
    class EventCounts {

        @Test
        @DisplayName("reads the event rows (Javadoc example)")
        void javadocExample() {
            assertThat(tasks.eventCounts(SUMMARY))
                    .hasSize(2)
                    .containsEntry("jdk.GCPhasePause", 120L)
                    .containsEntry("jdk.CPULoad", 60L);
        }

        @Test
        @DisplayName("keeps the order of the input")
        void keepsOrder() {
            assertThat(tasks.eventCounts(SUMMARY).keySet()).containsExactly("jdk.GCPhasePause", "jdk.CPULoad");
        }

        @Test
        @DisplayName("the 'Version: 2.1' line is not an event")
        void versionLineIgnored() {
            assertThat(tasks.eventCounts(SUMMARY)).doesNotContainKey("Version:");
        }

        @Test
        @DisplayName("empty text gives an empty map")
        void emptyText() {
            assertThat(tasks.eventCounts("")).isEmpty();
        }

        @Test
        @DisplayName("header and separator lines are not events")
        void headerIgnored() {
            assertThat(tasks.eventCounts(" Event Type  Count  Size (bytes)\n=====\n")).isEmpty();
        }
    }
}
