package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("FlightRecorderDemo — API contracts")
class FlightRecorderDemoTest {

    @Nested
    @DisplayName("1. Controlling JFR")
    class Controlling {

        @Test
        @DisplayName("startupFlag builds the -XX:StartFlightRecording option")
        void startupFlag() {
            assertThat(FlightRecorderDemo.startupFlag(60, "app.jfr"))
                    .isEqualTo("-XX:StartFlightRecording=duration=60s,filename=app.jfr");
        }

        @Test
        @DisplayName("jfrStopCommand names the recording and the file")
        void stopCommand() {
            assertThat(FlightRecorderDemo.jfrStopCommand(4711, "leak", "leak.jfr"))
                    .isEqualTo("jcmd 4711 JFR.stop name=leak filename=leak.jfr");
        }

        @Test
        @DisplayName("jfrDumpCommand uses JFR.dump, which keeps the recording running")
        void dumpCommand() {
            assertThat(FlightRecorderDemo.jfrDumpCommand(4711, "leak", "leak.jfr"))
                    .isEqualTo("jcmd 4711 JFR.dump name=leak filename=leak.jfr");
        }
    }

    @Nested
    @DisplayName("2. Which events to look at")
    class Events {

        @Test
        @DisplayName("the short list covers CPU, GC, locks and leaks")
        void shortList() {
            assertThat(FlightRecorderDemo.JFR_EVENTS).containsKeys(
                    "jdk.ExecutionSample", "jdk.GarbageCollection", "jdk.CPULoad",
                    "jdk.JavaMonitorEnter", "jdk.OldObjectSample");
        }

        @Test
        @DisplayName("describeEvent returns the description of a known event")
        void knownEvent() {
            assertThat(FlightRecorderDemo.describeEvent("jdk.OldObjectSample")).contains("leak");
        }

        @Test
        @DisplayName("describeEvent points to jfr summary for unknown events")
        void unknownEvent() {
            assertThat(FlightRecorderDemo.describeEvent("jdk.Nope")).contains("jfr summary");
        }

        @Test
        @DisplayName("the event list cannot be modified")
        void immutable() {
            assertThatThrownBy(() -> FlightRecorderDemo.JFR_EVENTS.put("x", "y"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }

    @Nested
    @DisplayName("3. What VisualVM's Monitor tab reads")
    class Monitor {

        @Test
        @DisplayName("a sample has positive heap, thread and class counts")
        void positiveCounters() {
            FlightRecorderDemo.MonitorSample sample = FlightRecorderDemo.sample();
            assertThat(sample.heapUsedBytes).isPositive();
            assertThat(sample.liveThreads).isPositive();
            assertThat(sample.loadedClasses).isPositive();
        }

        @Test
        @DisplayName("GC counters are never negative, even when a collector reports -1")
        void gcCountersNonNegative() {
            FlightRecorderDemo.MonitorSample sample = FlightRecorderDemo.sample();
            assertThat(sample.gcCount).isGreaterThanOrEqualTo(0L);
            assertThat(sample.gcTimeMillis).isGreaterThanOrEqualTo(0L);
        }

        @Test
        @DisplayName("GC count never goes down between samples")
        void gcCountMonotonic() {
            FlightRecorderDemo.MonitorSample first = FlightRecorderDemo.sample();
            System.gc();
            FlightRecorderDemo.MonitorSample second = FlightRecorderDemo.sample();
            assertThat(second.gcCount).isGreaterThanOrEqualTo(first.gcCount);
        }
    }

    @Nested
    @DisplayName("4. Attaching VisualVM to a remote JVM")
    class RemoteJmx {

        @Test
        @DisplayName("jmxRemoteFlags contains the port and switches auth and ssl off")
        void flags() {
            assertThat(FlightRecorderDemo.jmxRemoteFlags(9010)).containsExactly(
                    "-Dcom.sun.management.jmxremote",
                    "-Dcom.sun.management.jmxremote.port=9010",
                    "-Dcom.sun.management.jmxremote.authenticate=false",
                    "-Dcom.sun.management.jmxremote.ssl=false");
        }

        @Test
        @DisplayName("the port is the only part that changes")
        void portVaries() {
            assertThat(FlightRecorderDemo.jmxRemoteFlags(1099)).contains("-Dcom.sun.management.jmxremote.port=1099");
        }
    }
}
