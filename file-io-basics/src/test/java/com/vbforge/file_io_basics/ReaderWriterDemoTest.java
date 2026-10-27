package com.vbforge.file_io_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * ReaderWriterDemoTest — verifies every concept shown in {@link ReaderWriterDemo}.
 *
 * <p>These tests are always GREEN. Files live in a {@code @TempDir}; every read and write
 * names its charset, and the assertions read lines back (never raw line separators), so the
 * results are the same on every operating system.
 */
@DisplayName("ReaderWriterDemo — API contracts")
class ReaderWriterDemoTest {

    @TempDir
    File workDir;

    private BufferedWriter writer(File file, boolean append) throws IOException {
        return new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, append), StandardCharsets.UTF_8));
    }

    private BufferedReader reader(File file) throws IOException {
        return new BufferedReader(new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8));
    }

    private List<String> lines(File file) throws IOException {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    private void writeRaw(File file, String text) throws IOException {
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
    }

    // ── 1. Writing text ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. Writing text")
    class WritingTest {

        @Test
        @DisplayName("write + newLine produce one line per call")
        void writeLines() throws IOException {
            File file = new File(workDir, "poem.txt");
            try (BufferedWriter w = writer(file, false)) {
                w.write("roses are red");
                w.newLine();
                w.write("violets are blue");
                w.newLine();
            }
            assertThat(lines(file)).containsExactly("roses are red", "violets are blue");
        }

        @Test
        @DisplayName("append mode keeps the existing content")
        void append() throws IOException {
            File file = new File(workDir, "poem.txt");
            try (BufferedWriter w = writer(file, false)) {
                w.write("first");
                w.newLine();
            }
            try (BufferedWriter w = writer(file, true)) {
                w.write("second");
                w.newLine();
            }
            assertThat(lines(file)).containsExactly("first", "second");
        }

        @Test
        @DisplayName("overwrite mode replaces the existing content")
        void overwrite() throws IOException {
            File file = new File(workDir, "poem.txt");
            writeRaw(file, "old content that is longer\n");
            try (BufferedWriter w = writer(file, false)) {
                w.write("new");
                w.newLine();
            }
            assertThat(lines(file)).containsExactly("new");
        }

        @Test
        @DisplayName("opening in overwrite mode empties the file immediately")
        void truncatesOnOpen() throws IOException {
            File file = new File(workDir, "poem.txt");
            writeRaw(file, "something");
            try (BufferedWriter w = writer(file, false)) {
                assertThat(file.length()).isZero();
            }
        }

        @Test
        @DisplayName("buffered text is only complete in the file after close")
        void closeFlushes() throws IOException {
            File file = new File(workDir, "buffered.txt");
            BufferedWriter w = writer(file, false);
            w.write("data");
            assertThat(file.length()).isZero();
            w.close();
            assertThat(file.length()).isEqualTo(4L);
        }
    }

    // ── 2. Reading text ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Reading text")
    class ReadingTest {

        @Test
        @DisplayName("the readLine loop collects every line, blank ones included")
        void classicLoop() throws IOException {
            File file = new File(workDir, "lines.txt");
            writeRaw(file, "a\n\nb\n");
            List<String> result = new ArrayList<>();
            try (BufferedReader r = reader(file)) {
                String line;
                while ((line = r.readLine()) != null) {
                    result.add(line);
                }
            }
            assertThat(result).containsExactly("a", "", "b");
        }

        @Test
        @DisplayName("a final line break does not add an empty last line")
        void trailingNewline() throws IOException {
            File file = new File(workDir, "lines.txt");
            writeRaw(file, "a\nb\n");
            List<String> withNewline = lines(file);
            writeRaw(file, "a\nb");
            List<String> withoutNewline = lines(file);
            assertThat(withNewline).containsExactly("a", "b");
            assertThat(withoutNewline).containsExactly("a", "b");
        }

        @Test
        @DisplayName("readLine returns null on an empty file")
        void emptyFile() throws IOException {
            File file = new File(workDir, "empty.txt");
            writeRaw(file, "");
            try (BufferedReader r = reader(file)) {
                assertThat(r.readLine()).isNull();
            }
        }

        @Test
        @DisplayName("read() returns characters as int and -1 at the end")
        void readSingleCharacters() throws IOException {
            File file = new File(workDir, "hi.txt");
            writeRaw(file, "hi");
            try (BufferedReader r = reader(file)) {
                assertThat((char) r.read()).isEqualTo('h');
                assertThat((char) r.read()).isEqualTo('i');
                assertThat(r.read()).isEqualTo(-1);
            }
        }

        @Test
        @DisplayName("BufferedReader.lines() gives the same lines as a stream")
        void linesStream() throws IOException {
            File file = new File(workDir, "lines.txt");
            writeRaw(file, "x\ny\n");
            try (BufferedReader r = reader(file)) {
                assertThat(r.lines().toArray()).containsExactly("x", "y");
            }
        }
    }

    // ── 3. Charsets ───────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Charsets")
    class CharsetTest {

        private final String text = "héllo €";

        @Test
        @DisplayName("character count and UTF-8 byte count differ for non-ASCII text")
        void lengthVersusBytes() {
            assertThat(text.length()).isEqualTo(7);
            assertThat(text.getBytes(StandardCharsets.UTF_8).length).isEqualTo(10);
        }

        @Test
        @DisplayName("ISO-8859-1 uses one byte per character and cannot represent €")
        void latin1() {
            byte[] latin1 = text.getBytes(StandardCharsets.ISO_8859_1);
            assertThat(latin1.length).isEqualTo(7);
            assertThat((char) latin1[6]).isEqualTo('?');
        }

        @Test
        @DisplayName("writing and reading with the same charset is lossless")
        void sameCharset() throws IOException {
            File file = new File(workDir, "utf8.txt");
            writeRaw(file, text);
            try (BufferedReader r = reader(file)) {
                assertThat(r.readLine()).isEqualTo(text);
            }
        }

        @Test
        @DisplayName("reading UTF-8 bytes as ISO-8859-1 does not fail — it produces different text")
        void wrongCharset() throws IOException {
            File file = new File(workDir, "utf8.txt");
            writeRaw(file, text);
            try (BufferedReader r = new BufferedReader(
                    new InputStreamReader(new FileInputStream(file), StandardCharsets.ISO_8859_1))) {
                String wrong = r.readLine();
                assertThat(wrong.length()).isEqualTo(10);
                assertThat(wrong).isNotEqualTo(text);
            }
        }
    }

    // ── 4. PrintWriter ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. PrintWriter")
    class PrintWriterTest {

        @Test
        @DisplayName("println, printf and print write formatted text")
        void formattedOutput() throws IOException {
            File file = new File(workDir, "report.txt");
            try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
                out.println("Report");
                out.printf("items: %d%n", 3);
                out.print("done");
            }
            assertThat(lines(file)).containsExactly("Report", "items: 3", "done");
        }

        @Test
        @DisplayName("writing after close does not throw")
        void silentFailure() throws IOException {
            File file = new File(workDir, "report.txt");
            PrintWriter out = new PrintWriter(file, "UTF-8");
            out.close();
            out.println("too late");
            assertThat(out.checkError()).isTrue();
        }

        @Test
        @DisplayName("a healthy writer reports no error")
        void noErrorWhenHealthy() throws IOException {
            File file = new File(workDir, "ok.txt");
            try (PrintWriter out = new PrintWriter(file, "UTF-8")) {
                out.println("fine");
                assertThat(out.checkError()).isFalse();
            }
        }
    }

    // ── 5. Scanner ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Scanner")
    class ScannerTest {

        private File numbers() throws IOException {
            File file = new File(workDir, "numbers.txt");
            writeRaw(file, "10 20\n30\nhello world\n");
            return file;
        }

        @Test
        @DisplayName("nextInt reads tokens across line breaks")
        void tokensAcrossLines() throws IOException {
            try (Scanner scanner = new Scanner(numbers(), "UTF-8")) {
                assertThat(scanner.nextInt() + scanner.nextInt() + scanner.nextInt()).isEqualTo(60);
            }
        }

        @Test
        @DisplayName("nextLine right after nextInt returns the rest of the current line (empty)")
        void nextLineTrap() throws IOException {
            try (Scanner scanner = new Scanner(numbers(), "UTF-8")) {
                scanner.nextInt();
                scanner.nextInt();
                scanner.nextInt();
                assertThat(scanner.nextLine()).isEmpty();
                assertThat(scanner.nextLine()).isEqualTo("hello world");
            }
        }

        @Test
        @DisplayName("hasNextLine is false once the input is consumed")
        void endOfInput() throws IOException {
            try (Scanner scanner = new Scanner(numbers(), "UTF-8")) {
                while (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
                assertThat(scanner.hasNextLine()).isFalse();
            }
        }

        @Test
        @DisplayName("tokens can be collected with hasNext/next")
        void wordTokens() throws IOException {
            File file = new File(workDir, "words.txt");
            writeRaw(file, "one  two\nthree\n");
            List<String> words = new ArrayList<>();
            try (Scanner scanner = new Scanner(file, "UTF-8")) {
                while (scanner.hasNext()) {
                    words.add(scanner.next());
                }
            }
            assertThat(words).isEqualTo(Arrays.asList("one", "two", "three"));
        }
    }
}
