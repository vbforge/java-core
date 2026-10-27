package com.vbforge.file_io_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * ReaderWriterTasksTest — tests for every method in {@link ReaderWriterTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code ReaderWriterTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=ReaderWriterTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("ReaderWriterTasks — your implementations")
class ReaderWriterTasksTest {

    @TempDir
    File tmp;

    private ReaderWriterTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ReaderWriterTasks();
    }

    /** Creates a UTF-8 file with exactly the given text. */
    private File fileWith(String name, String text) throws IOException {
        File file = new File(tmp, name);
        Files.write(file.toPath(), text.getBytes(StandardCharsets.UTF_8));
        return file;
    }

    /** Reads the lines of a file with the JDK, independent of the task implementations. */
    private List<String> linesOf(File file) throws IOException {
        return Files.readAllLines(file.toPath(), StandardCharsets.UTF_8);
    }

    // ── Task 1: writeLines ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement writeLines()")
    @DisplayName("Task 1 — writeLines()")
    class WriteLinesTest {

        @Test
        @DisplayName("writes one line per element")
        void basic() throws IOException {
            File file = new File(tmp, "out.txt");
            tasks.writeLines(file, Arrays.asList("roses", "violets"));
            assertThat(linesOf(file)).containsExactly("roses", "violets");
        }

        @Test
        @DisplayName("writes UTF-8")
        void utf8() throws IOException {
            File file = new File(tmp, "out.txt");
            tasks.writeLines(file, Collections.singletonList("héllo €"));
            assertThat(linesOf(file)).containsExactly("héllo €");
        }

        @Test
        @DisplayName("an empty list produces an empty file")
        void emptyList() throws IOException {
            File file = new File(tmp, "out.txt");
            tasks.writeLines(file, Collections.<String>emptyList());
            assertThat(file.exists()).isTrue();
            assertThat(file.length()).isZero();
        }

        @Test
        @DisplayName("an existing file is overwritten, not extended")
        void overwrites() throws IOException {
            File file = new File(tmp, "out.txt");
            tasks.writeLines(file, Collections.singletonList("old content"));
            tasks.writeLines(file, Collections.singletonList("new"));
            assertThat(linesOf(file)).containsExactly("new");
        }

        @Test
        @DisplayName("blank lines are kept")
        void blankLines() throws IOException {
            File file = new File(tmp, "out.txt");
            tasks.writeLines(file, Arrays.asList("a", "", "b"));
            assertThat(linesOf(file)).containsExactly("a", "", "b");
        }
    }

    // ── Task 2: readLines ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement readLines()")
    @DisplayName("Task 2 — readLines()")
    class ReadLinesTest {

        @Test
        @DisplayName("reads all lines in order; a final line break adds nothing")
        void basic() throws IOException {
            assertThat(tasks.readLines(fileWith("a.txt", "a\nb\n"))).containsExactly("a", "b");
        }

        @Test
        @DisplayName("keeps blank lines as empty strings")
        void blankLines() throws IOException {
            assertThat(tasks.readLines(fileWith("a.txt", "a\n\nb"))).containsExactly("a", "", "b");
        }

        @Test
        @DisplayName("decodes UTF-8")
        void utf8() throws IOException {
            assertThat(tasks.readLines(fileWith("a.txt", "héllo €\n"))).containsExactly("héllo €");
        }

        @Test
        @DisplayName("an empty file gives an empty list")
        void emptyFile() throws IOException {
            assertThat(tasks.readLines(fileWith("a.txt", ""))).isEmpty();
        }

        @Test
        @DisplayName("a missing file throws an IOException")
        void missingFile() {
            File missing = new File(tmp, "missing.txt");
            assertThatThrownBy(() -> tasks.readLines(missing))
                    .isInstanceOf(IOException.class);
        }
    }

    // ── Task 3: appendLine ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement appendLine()")
    @DisplayName("Task 3 — appendLine()")
    class AppendLineTest {

        @Test
        @DisplayName("creates the file when it is missing")
        void createsFile() throws IOException {
            File file = new File(tmp, "log.txt");
            tasks.appendLine(file, "one");
            assertThat(linesOf(file)).containsExactly("one");
        }

        @Test
        @DisplayName("keeps existing content and adds the line at the end")
        void appends() throws IOException {
            File file = fileWith("log.txt", "one\n");
            tasks.appendLine(file, "two");
            assertThat(linesOf(file)).containsExactly("one", "two");
        }

        @Test
        @DisplayName("several calls accumulate in order")
        void severalCalls() throws IOException {
            File file = new File(tmp, "log.txt");
            tasks.appendLine(file, "a");
            tasks.appendLine(file, "b");
            tasks.appendLine(file, "c");
            assertThat(linesOf(file)).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("an empty text adds one blank line")
        void blankLine() throws IOException {
            File file = fileWith("log.txt", "a\n");
            tasks.appendLine(file, "");
            assertThat(linesOf(file)).containsExactly("a", "");
        }

        @Test
        @DisplayName("UTF-8 text survives appending")
        void utf8() throws IOException {
            File file = new File(tmp, "log.txt");
            tasks.appendLine(file, "héllo €");
            assertThat(linesOf(file)).containsExactly("héllo €");
        }
    }

    // ── Task 4: countWords ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countWords()")
    @DisplayName("Task 4 — countWords()")
    class CountWordsTest {

        @ParameterizedTest(name = "{0} → {1} words")
        @CsvSource(delimiter = '|', value = {
                "one two three|3",
                "  spaced    out  |2",
                "single|1",
                "tab\tseparated words|3",
                "a b c d e f g|7"
        })
        @DisplayName("counts runs of non-whitespace characters")
        void counts(String line, int expected) throws IOException {
            assertThat(tasks.countWords(fileWith("w.txt", line + "\n"))).isEqualTo(expected);
        }

        @Test
        @DisplayName("blank and whitespace-only lines have no words")
        void blankLines() throws IOException {
            assertThat(tasks.countWords(fileWith("w.txt", "\n\n   \n"))).isZero();
        }

        @Test
        @DisplayName("words are summed across lines")
        void acrossLines() throws IOException {
            assertThat(tasks.countWords(fileWith("w.txt", "  spaced    out  \n\nlast\n"))).isEqualTo(3);
        }

        @Test
        @DisplayName("an empty file has no words")
        void emptyFile() throws IOException {
            assertThat(tasks.countWords(fileWith("w.txt", ""))).isZero();
        }
    }

    // ── Task 5: longestLine ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement longestLine()")
    @DisplayName("Task 5 — longestLine()")
    class LongestLineTest {

        @Test
        @DisplayName("returns the line with the most characters")
        void basic() throws IOException {
            assertThat(tasks.longestLine(fileWith("l.txt", "a\nccc\nbb\n"))).isEqualTo(Optional.of("ccc"));
        }

        @Test
        @DisplayName("on a tie the first line wins")
        void tie() throws IOException {
            assertThat(tasks.longestLine(fileWith("l.txt", "ab\ncd\ne\n"))).isEqualTo(Optional.of("ab"));
        }

        @Test
        @DisplayName("blank lines lose against any text")
        void blanksLose() throws IOException {
            assertThat(tasks.longestLine(fileWith("l.txt", "\n\nx\n"))).isEqualTo(Optional.of("x"));
        }

        @Test
        @DisplayName("a single blank line is still a line")
        void singleBlank() throws IOException {
            assertThat(tasks.longestLine(fileWith("l.txt", "\n"))).isEqualTo(Optional.of(""));
        }

        @Test
        @DisplayName("an empty file gives Optional.empty()")
        void emptyFile() throws IOException {
            assertThat(tasks.longestLine(fileWith("l.txt", ""))).isEqualTo(Optional.empty());
        }
    }

    // ── Task 6: copyWithLineNumbers ───────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement copyWithLineNumbers()")
    @DisplayName("Task 6 — copyWithLineNumbers()")
    class CopyWithLineNumbersTest {

        @Test
        @DisplayName("prefixes every line with its 1-based number")
        void basic() throws IOException {
            File source = fileWith("in.txt", "alpha\nbeta\n");
            File target = new File(tmp, "out.txt");
            tasks.copyWithLineNumbers(source, target);
            assertThat(linesOf(target)).containsExactly("1: alpha", "2: beta");
        }

        @Test
        @DisplayName("blank lines are numbered too")
        void blankLines() throws IOException {
            File source = fileWith("in.txt", "a\n\nc\n");
            File target = new File(tmp, "out.txt");
            tasks.copyWithLineNumbers(source, target);
            assertThat(linesOf(target)).containsExactly("1: a", "2: ", "3: c");
        }

        @Test
        @DisplayName("UTF-8 text is preserved")
        void utf8() throws IOException {
            File source = fileWith("in.txt", "héllo €\n");
            File target = new File(tmp, "out.txt");
            tasks.copyWithLineNumbers(source, target);
            assertThat(linesOf(target)).containsExactly("1: héllo €");
        }

        @Test
        @DisplayName("an empty source gives an empty target")
        void emptySource() throws IOException {
            File source = fileWith("in.txt", "");
            File target = new File(tmp, "out.txt");
            tasks.copyWithLineNumbers(source, target);
            assertThat(target.exists()).isTrue();
            assertThat(target.length()).isZero();
        }

        @Test
        @DisplayName("an existing target is overwritten and the source is untouched")
        void overwriteTarget() throws IOException {
            File source = fileWith("in.txt", "x\n");
            File target = fileWith("out.txt", "old\nold\nold\n");
            tasks.copyWithLineNumbers(source, target);
            assertThat(linesOf(target)).containsExactly("1: x");
            assertThat(linesOf(source)).containsExactly("x");
        }
    }
}
