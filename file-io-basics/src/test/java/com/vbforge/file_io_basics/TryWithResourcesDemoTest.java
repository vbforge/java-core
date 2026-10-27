package com.vbforge.file_io_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * TryWithResourcesDemoTest — verifies every concept shown in {@link TryWithResourcesDemo}.
 *
 * <p>These tests are always GREEN. They use the in-memory helper resources of the demo, so
 * the order of events can be asserted exactly without touching the disk.
 */
@SuppressWarnings("try")
@DisplayName("TryWithResourcesDemo — API contracts")
class TryWithResourcesDemoTest {

    @TempDir
    File workDir;

    // ── 1. Manual finally vs try-with-resources ───────────────────────────────

    @Nested
    @DisplayName("1. Manual finally vs try-with-resources")
    class ManualVersusAutomaticTest {

        @Test
        @DisplayName("try-with-resources closes the resource after the body")
        void closesAfterBody() {
            List<String> log = new ArrayList<>();
            try (TryWithResourcesDemo.NamedResource r = new TryWithResourcesDemo.NamedResource("auto", log)) {
                log.add("work");
            }
            assertThat(log).containsExactly("open auto", "work", "close auto");
        }

        @Test
        @DisplayName("a manual try/finally produces the same events")
        void manualFinally() {
            List<String> log = new ArrayList<>();
            TryWithResourcesDemo.NamedResource r = null;
            try {
                r = new TryWithResourcesDemo.NamedResource("manual", log);
                log.add("work");
            } finally {
                if (r != null) {
                    r.close();
                }
            }
            assertThat(log).containsExactly("open manual", "work", "close manual");
        }

        @Test
        @DisplayName("the resource is closed even when the body throws")
        void closesOnException() {
            List<String> log = new ArrayList<>();
            assertThatThrownBy(() -> {
                try (TryWithResourcesDemo.NamedResource r = new TryWithResourcesDemo.NamedResource("r", log)) {
                    throw new IllegalStateException("boom");
                }
            }).isInstanceOf(IllegalStateException.class).hasMessage("boom");
            assertThat(log).containsExactly("open r", "close r");
        }
    }

    // ── 2. Closing order ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Closing order")
    class CloseOrderTest {

        @Test
        @DisplayName("resources are closed in reverse order of declaration")
        void reverseOrder() {
            List<String> log = new ArrayList<>();
            try (TryWithResourcesDemo.NamedResource a = new TryWithResourcesDemo.NamedResource("first", log);
                 TryWithResourcesDemo.NamedResource b = new TryWithResourcesDemo.NamedResource("second", log)) {
                log.add("body");
            }
            assertThat(log).containsExactly("open first", "open second", "body", "close second", "close first");
        }

        @Test
        @DisplayName("close happens before catch and finally")
        void closeBeforeCatch() {
            List<String> log = new ArrayList<>();
            try (TryWithResourcesDemo.NamedResource r = new TryWithResourcesDemo.NamedResource("r", log)) {
                log.add("body");
                throw new IllegalStateException("x");
            } catch (IllegalStateException e) {
                log.add("catch");
            } finally {
                log.add("finally");
            }
            assertThat(log).containsExactly("open r", "body", "close r", "catch", "finally");
        }

        @Test
        @DisplayName("a resource whose constructor fails is not closed, earlier ones are")
        void failingSecondResource() {
            List<String> log = new ArrayList<>();
            assertThatThrownBy(() -> {
                try (TryWithResourcesDemo.NamedResource a = new TryWithResourcesDemo.NamedResource("a", log);
                     TryWithResourcesDemo.NamedResource b = failToOpen()) {
                    log.add("body");
                }
            }).isInstanceOf(IllegalStateException.class);
            assertThat(log).containsExactly("open a", "close a");
        }

        private TryWithResourcesDemo.NamedResource failToOpen() {
            throw new IllegalStateException("cannot open");
        }
    }

    // ── 3. Suppressed exceptions ──────────────────────────────────────────────

    @Nested
    @DisplayName("3. Suppressed exceptions")
    class SuppressedTest {

        @Test
        @DisplayName("the body's exception wins; the close failure is suppressed")
        void bodyWins() {
            Throwable thrown = null;
            try (TryWithResourcesDemo.FailingResource r = new TryWithResourcesDemo.FailingResource("close failed")) {
                throw new IllegalStateException("body failed");
            } catch (IllegalStateException e) {
                thrown = e;
            }
            assertThat(thrown.getMessage()).isEqualTo("body failed");
            assertThat(thrown.getSuppressed()).hasSize(1);
            assertThat(thrown.getSuppressed()[0].getMessage()).isEqualTo("close failed");
        }

        @Test
        @DisplayName("a manual finally loses the original exception")
        void manualLosesOriginal() {
            Throwable thrown = null;
            try {
                TryWithResourcesDemo.FailingResource r = new TryWithResourcesDemo.FailingResource("close failed");
                try {
                    throw new IllegalStateException("body failed");
                } finally {
                    r.close();
                }
            } catch (IllegalStateException e) {
                thrown = e;
            }
            assertThat(thrown.getMessage()).isEqualTo("close failed");
            assertThat(thrown.getSuppressed()).isEmpty();
        }

        @Test
        @DisplayName("when only close() fails, that exception propagates normally")
        void onlyCloseFails() {
            assertThatThrownBy(() -> {
                try (TryWithResourcesDemo.FailingResource r = new TryWithResourcesDemo.FailingResource("close failed")) {
                    assertThat(r).isNotNull();
                }
            }).isInstanceOf(IllegalStateException.class).hasMessage("close failed");
        }
    }

    // ── 4. Wrapping constructors ──────────────────────────────────────────────

    @Nested
    @DisplayName("4. Wrapping constructors")
    class WrappingTest {

        private class Tracking extends StringReader {
            boolean closed;

            Tracking() {
                super("");
            }

            @Override
            public void close() {
                closed = true;
                super.close();
            }
        }

        @Test
        @DisplayName("a failing outer constructor leaks the inner reader")
        void singleExpressionLeaks() {
            Tracking inner = new Tracking();
            assertThatThrownBy(() -> {
                try (BufferedReader r = new BufferedReader(inner, 0)) {
                    assertThat(r).isNotNull();
                }
            }).isInstanceOf(IllegalArgumentException.class);
            assertThat(inner.closed).isFalse();
        }

        @Test
        @DisplayName("declaring the inner reader as its own resource closes it")
        void twoDeclarationsDoNotLeak() {
            Tracking inner = new Tracking();
            assertThatThrownBy(() -> {
                try (Reader in = inner;
                     BufferedReader r = new BufferedReader(in, 0)) {
                    assertThat(r).isNotNull();
                }
            }).isInstanceOf(IllegalArgumentException.class);
            assertThat(inner.closed).isTrue();
        }

        @Test
        @DisplayName("closing the outer wrapper closes the inner reader as well")
        void outerClosesInner() throws IOException {
            Tracking inner = new Tracking();
            try (BufferedReader r = new BufferedReader(inner)) {
                assertThat(r).isNotNull();
            }
            assertThat(inner.closed).isTrue();
        }
    }

    // ── 5. Files, null, AutoCloseable ─────────────────────────────────────────

    @Nested
    @DisplayName("5. Files, null, AutoCloseable")
    class FilesAndNullTest {

        @Test
        @DisplayName("a file read through two resources returns the first line")
        void readFirstLine() throws IOException {
            File file = new File(workDir, "f.txt");
            Files.write(file.toPath(), Arrays.asList("first", "second"), StandardCharsets.UTF_8);
            try (FileReader fileReader = new FileReader(file);
                 BufferedReader reader = new BufferedReader(fileReader)) {
                assertThat(reader.readLine()).isEqualTo("first");
            }
        }

        @Test
        @DisplayName("a null resource is allowed: the body runs and close is skipped")
        void nullResource() {
            List<String> log = new ArrayList<>();
            try (TryWithResourcesDemo.NamedResource nothing = null) {
                log.add("body ran");
            }
            assertThat(log).containsExactly("body ran");
        }

        @Test
        @DisplayName("Closeable.close() can be called twice")
        void doubleClose() throws IOException {
            Reader reader = new StringReader("x");
            reader.close();
            reader.close();
            assertThatThrownBy(reader::read).isInstanceOf(IOException.class);
        }

        @Test
        @DisplayName("a missing file fails when opened, before the body runs")
        void missingFile() {
            File missing = new File(workDir, "missing.txt");
            List<String> log = new ArrayList<>();
            assertThatThrownBy(() -> {
                try (FileReader r = new FileReader(missing)) {
                    log.add("body");
                }
            }).isInstanceOf(java.io.FileNotFoundException.class);
            assertThat(log).isEmpty();
        }
    }
}
