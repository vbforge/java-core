package com.vbforge.jvm_diagnostic_tools;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("HeapDumpDemo — API contracts")
class HeapDumpDemoTest {

    @Nested
    @DisplayName("1. Heap numbers")
    class HeapNumbers {

        @Test
        @DisplayName("used heap is positive and fits into committed")
        void usedFitsCommitted() {
            assertThat(HeapDumpDemo.heapUsedBytes()).isPositive();
            assertThat(HeapDumpDemo.heapCommittedBytes()).isGreaterThanOrEqualTo(HeapDumpDemo.heapUsedBytes());
        }

        @Test
        @DisplayName("committed heap never exceeds the max")
        void committedFitsMax() {
            assertThat(HeapDumpDemo.heapMaxBytes()).isGreaterThanOrEqualTo(HeapDumpDemo.heapCommittedBytes());
        }

        @Test
        @DisplayName("formatBytes keeps small values in bytes")
        void smallValues() {
            assertThat(HeapDumpDemo.formatBytes(0)).isEqualTo("0 B");
            assertThat(HeapDumpDemo.formatBytes(1023)).isEqualTo("1023 B");
        }

        @Test
        @DisplayName("formatBytes uses binary units with one decimal")
        void binaryUnits() {
            assertThat(HeapDumpDemo.formatBytes(1024)).isEqualTo("1.0 KB");
            assertThat(HeapDumpDemo.formatBytes(1536)).isEqualTo("1.5 KB");
            assertThat(HeapDumpDemo.formatBytes(1_048_576)).isEqualTo("1.0 MB");
            assertThat(HeapDumpDemo.formatBytes(3L * 1024 * 1024 * 1024)).isEqualTo("3.0 GB");
        }
    }

    @Nested
    @DisplayName("2. The class histogram")
    class Histogram {

        @Test
        @DisplayName("parseHistogram skips header, separator and Total lines")
        void fiveDataRows() {
            assertThat(HeapDumpDemo.parseHistogram(HeapDumpDemo.SAMPLE_HISTOGRAM)).hasSize(5);
        }

        @Test
        @DisplayName("rows keep rank, counts and class name")
        void firstRow() {
            HeapDumpDemo.HistogramRow row = HeapDumpDemo.parseHistogram(HeapDumpDemo.SAMPLE_HISTOGRAM).get(0);
            assertThat(row.rank).isEqualTo(1);
            assertThat(row.instances).isEqualTo(1000L);
            assertThat(row.bytes).isEqualTo(5_000_000L);
            assertThat(row.className).isEqualTo("[B");
        }

        @Test
        @DisplayName("parseHistogramRow returns null for non-row lines")
        void nonRows() {
            assertThat(HeapDumpDemo.parseHistogramRow(" num     #instances         #bytes  class name")).isNull();
            assertThat(HeapDumpDemo.parseHistogramRow("Total         36100        8440000")).isNull();
            assertThat(HeapDumpDemo.parseHistogramRow("")).isNull();
        }

        @Test
        @DisplayName("the Total row equals the sum of the rows")
        void totalMatchesRows() {
            long sum = 0;
            for (HeapDumpDemo.HistogramRow row : HeapDumpDemo.parseHistogram(HeapDumpDemo.SAMPLE_HISTOGRAM)) {
                sum += row.bytes;
            }
            assertThat(sum).isEqualTo(8_440_000L);
        }

        @Test
        @DisplayName("JVM type codes map letters to primitive names")
        void typeCodes() {
            assertThat(HeapDumpDemo.JVM_TYPE_CODES)
                    .containsEntry('B', "byte")
                    .containsEntry('I', "int")
                    .containsEntry('J', "long")
                    .containsEntry('Z', "boolean")
                    .hasSize(8);
        }
    }

    @Nested
    @DisplayName("3. Writing a heap dump from code")
    class DumpFile {

        @Test
        @DisplayName("dumpHeap writes an HPROF file")
        void writesHprof(@TempDir Path dir) throws IOException {
            Assumptions.assumeTrue(HeapDumpDemo.isHeapDumpSupported(), "needs a HotSpot-based JVM");
            Path file = dir.resolve("test.hprof");

            HeapDumpDemo.dumpHeap(file, true);

            assertThat(file).exists();
            assertThat(Files.size(file)).isPositive();
            assertThat(HeapDumpDemo.hprofMagic(file)).isEqualTo("JAVA PROFILE");
        }

        @Test
        @DisplayName("dumpHeap refuses to overwrite an existing file")
        void refusesExistingFile(@TempDir Path dir) throws IOException {
            Assumptions.assumeTrue(HeapDumpDemo.isHeapDumpSupported(), "needs a HotSpot-based JVM");
            Path file = Files.createFile(dir.resolve("existing.hprof"));

            assertThatThrownBy(() -> HeapDumpDemo.dumpHeap(file, true)).isInstanceOf(IOException.class);
        }
    }

    @Nested
    @DisplayName("4. Command-line equivalents")
    class Commands {

        @Test
        @DisplayName("heapDumpCommands returns the jcmd and the jmap form")
        void bothForms() {
            List<String> commands = HeapDumpDemo.heapDumpCommands(4711, "/tmp/app.hprof");
            assertThat(commands).containsExactly(
                    "jcmd 4711 GC.heap_dump /tmp/app.hprof",
                    "jmap -dump:live,format=b,file=/tmp/app.hprof 4711");
        }

        @Test
        @DisplayName("OOM flags enable the dump and set its location")
        void oomFlags() {
            assertThat(HeapDumpDemo.OOM_DUMP_FLAGS).containsExactly(
                    "-XX:+HeapDumpOnOutOfMemoryError",
                    "-XX:HeapDumpPath=/var/dumps");
        }

        @Test
        @DisplayName("OOM flag list cannot be modified")
        void oomFlagsImmutable() {
            assertThatThrownBy(() -> HeapDumpDemo.OOM_DUMP_FLAGS.add("-X"))
                    .isInstanceOf(UnsupportedOperationException.class);
        }
    }
}
