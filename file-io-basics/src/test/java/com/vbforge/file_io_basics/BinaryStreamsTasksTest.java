package com.vbforge.file_io_basics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * BinaryStreamsTasksTest — tests for every method in {@link BinaryStreamsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code BinaryStreamsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=BinaryStreamsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 6 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("BinaryStreamsTasks — your implementations")
class BinaryStreamsTasksTest {

    @TempDir
    File tmp;

    private BinaryStreamsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BinaryStreamsTasks();
    }

    private File fileWith(String name, byte... content) throws IOException {
        File file = new File(tmp, name);
        Files.write(file.toPath(), content);
        return file;
    }

    private byte[] pattern(int length) {
        byte[] data = new byte[length];
        for (int i = 0; i < length; i++) {
            data[i] = (byte) (i * 31 + 7);
        }
        return data;
    }

    private byte[] bytesOf(File file) throws IOException {
        return Files.readAllBytes(file.toPath());
    }

    // ── Task 1: copy ──────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement copy()")
    @DisplayName("Task 1 — copy()")
    class CopyTest {

        @Test
        @DisplayName("copies a small file and returns its length")
        void small() throws IOException {
            byte[] data = pattern(10);
            File source = fileWith("in.bin", data);
            File target = new File(tmp, "out.bin");
            assertThat(tasks.copy(source, target)).isEqualTo(10L);
            assertThat(bytesOf(target)).isEqualTo(data);
        }

        @Test
        @DisplayName("an empty file copies to an empty file, returning 0")
        void empty() throws IOException {
            File source = fileWith("in.bin");
            File target = new File(tmp, "out.bin");
            assertThat(tasks.copy(source, target)).isZero();
            assertThat(target.exists()).isTrue();
            assertThat(target.length()).isZero();
        }

        @ParameterizedTest(name = "{0} bytes")
        @ValueSource(ints = {1, 4095, 4096, 4097, 100_000})
        @DisplayName("sizes around and far above any buffer size are copied exactly")
        void largerFiles(int size) throws IOException {
            byte[] data = pattern(size);
            File source = fileWith("in.bin", data);
            File target = new File(tmp, "out.bin");
            assertThat(tasks.copy(source, target)).isEqualTo((long) size);
            assertThat(bytesOf(target)).isEqualTo(data);
        }

        @Test
        @DisplayName("bytes above 127 are copied unchanged")
        void highBytes() throws IOException {
            byte[] data = {-1, -128, 127, 0, -2};
            File source = fileWith("in.bin", data);
            File target = new File(tmp, "out.bin");
            tasks.copy(source, target);
            assertThat(bytesOf(target)).isEqualTo(data);
        }

        @Test
        @DisplayName("an existing target is overwritten, not extended")
        void overwrites() throws IOException {
            File source = fileWith("in.bin", (byte) 1, (byte) 2);
            File target = fileWith("out.bin", (byte) 9, (byte) 9, (byte) 9, (byte) 9, (byte) 9);
            tasks.copy(source, target);
            assertThat(bytesOf(target)).isEqualTo(new byte[]{1, 2});
        }
    }

    // ── Task 2: readAllBytes ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement readAllBytes()")
    @DisplayName("Task 2 — readAllBytes()")
    class ReadAllBytesTest {

        @Test
        @DisplayName("returns the bytes of the file")
        void basic() throws IOException {
            assertThat(tasks.readAllBytes(fileWith("a.bin", (byte) 1, (byte) 2, (byte) 3)))
                    .isEqualTo(new byte[]{1, 2, 3});
        }

        @Test
        @DisplayName("an empty file gives an empty array, not null")
        void empty() throws IOException {
            byte[] result = tasks.readAllBytes(fileWith("a.bin"));
            assertThat(result).isNotNull();
            assertThat(result.length).isZero();
        }

        @Test
        @DisplayName("negative byte values survive")
        void signedValues() throws IOException {
            byte[] data = {0, -1, 127, -128};
            assertThat(tasks.readAllBytes(fileWith("a.bin", data))).isEqualTo(data);
        }

        @Test
        @DisplayName("a file larger than any single read comes back complete")
        void large() throws IOException {
            byte[] data = pattern(20_000);
            byte[] result = tasks.readAllBytes(fileWith("a.bin", data));
            assertThat(result.length).isEqualTo(20_000);
            assertThat(result).isEqualTo(data);
        }
    }

    // ── Task 3: writeInts ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement writeInts()")
    @DisplayName("Task 3 — writeInts()")
    class WriteIntsTest {

        @Test
        @DisplayName("writes each int as 4 big-endian bytes")
        void basic() throws IOException {
            File file = new File(tmp, "ints.bin");
            tasks.writeInts(file, new int[]{1, 2, 3});
            assertThat(bytesOf(file)).isEqualTo(new byte[]{0, 0, 0, 1, 0, 0, 0, 2, 0, 0, 0, 3});
        }

        @Test
        @DisplayName("258 is 0 0 1 2 — the most significant byte comes first")
        void bigEndian() throws IOException {
            File file = new File(tmp, "ints.bin");
            tasks.writeInts(file, new int[]{258});
            assertThat(bytesOf(file)).isEqualTo(new byte[]{0, 0, 1, 2});
        }

        @Test
        @DisplayName("-1 is four 0xFF bytes")
        void negative() throws IOException {
            File file = new File(tmp, "ints.bin");
            tasks.writeInts(file, new int[]{-1});
            assertThat(bytesOf(file)).isEqualTo(new byte[]{-1, -1, -1, -1});
        }

        @Test
        @DisplayName("an empty array gives an empty file")
        void empty() throws IOException {
            File file = new File(tmp, "ints.bin");
            tasks.writeInts(file, new int[0]);
            assertThat(file.exists()).isTrue();
            assertThat(file.length()).isZero();
        }

        @Test
        @DisplayName("an existing file is overwritten")
        void overwrites() throws IOException {
            File file = fileWith("ints.bin", pattern(40));
            tasks.writeInts(file, new int[]{5});
            assertThat(file.length()).isEqualTo(4L);
        }

        @Test
        @DisplayName("Integer.MIN_VALUE and MAX_VALUE use the full 4 bytes")
        void extremes() throws IOException {
            File file = new File(tmp, "ints.bin");
            tasks.writeInts(file, new int[]{Integer.MIN_VALUE, Integer.MAX_VALUE});
            assertThat(bytesOf(file)).isEqualTo(new byte[]{-128, 0, 0, 0, 127, -1, -1, -1});
        }
    }

    // ── Task 4: readInts ──────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement readInts()")
    @DisplayName("Task 4 — readInts()")
    class ReadIntsTest {

        @Test
        @DisplayName("every 4 bytes become one big-endian int")
        void basic() throws IOException {
            File file = fileWith("ints.bin", (byte) 0, (byte) 0, (byte) 0, (byte) 1,
                    (byte) 0, (byte) 0, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 3);
            assertThat(tasks.readInts(file)).isEqualTo(new int[]{1, 2, 3});
        }

        @Test
        @DisplayName("0 0 1 2 is 258")
        void bigEndian() throws IOException {
            assertThat(tasks.readInts(fileWith("ints.bin", (byte) 0, (byte) 0, (byte) 1, (byte) 2)))
                    .isEqualTo(new int[]{258});
        }

        @Test
        @DisplayName("four 0xFF bytes are -1")
        void negative() throws IOException {
            assertThat(tasks.readInts(fileWith("ints.bin", (byte) -1, (byte) -1, (byte) -1, (byte) -1)))
                    .isEqualTo(new int[]{-1});
        }

        @Test
        @DisplayName("an empty file gives an empty array")
        void empty() throws IOException {
            assertThat(tasks.readInts(fileWith("ints.bin")).length).isZero();
        }

        @Test
        @DisplayName("writeInts followed by readInts returns the original numbers")
        void roundTrip() throws IOException {
            int[] values = {0, 1, -1, 255, 256, 65_536, Integer.MIN_VALUE, Integer.MAX_VALUE, 123_456_789};
            File file = new File(tmp, "ints.bin");
            tasks.writeInts(file, values);
            assertThat(tasks.readInts(file)).isEqualTo(values);
        }
    }

    // ── Task 5: countByte ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countByte()")
    @DisplayName("Task 5 — countByte()")
    class CountByteTest {

        @Test
        @DisplayName("counts the occurrences of an ASCII byte")
        void ascii() throws IOException {
            File file = fileWith("a.bin", "banana".getBytes(StandardCharsets.US_ASCII));
            assertThat(tasks.countByte(file, (byte) 'a')).isEqualTo(3);
        }

        @Test
        @DisplayName("a byte that does not occur gives 0")
        void absent() throws IOException {
            File file = fileWith("a.bin", "banana".getBytes(StandardCharsets.US_ASCII));
            assertThat(tasks.countByte(file, (byte) 'z')).isZero();
        }

        @Test
        @DisplayName("0xFF (-1 as a byte) is counted and is not mistaken for end-of-stream")
        void minusOne() throws IOException {
            File file = fileWith("a.bin", (byte) -1, (byte) 5, (byte) -1, (byte) -1);
            assertThat(tasks.countByte(file, (byte) -1)).isEqualTo(3);
        }

        @Test
        @DisplayName("the zero byte can be counted")
        void zeroByte() throws IOException {
            File file = fileWith("a.bin", (byte) 0, (byte) 0, (byte) 1);
            assertThat(tasks.countByte(file, (byte) 0)).isEqualTo(2);
        }

        @Test
        @DisplayName("an empty file gives 0")
        void empty() throws IOException {
            assertThat(tasks.countByte(fileWith("a.bin"), (byte) 1)).isZero();
        }

        @Test
        @DisplayName("works across a large file")
        void large() throws IOException {
            byte[] data = new byte[50_000];
            for (int i = 0; i < data.length; i += 5) {
                data[i] = (byte) 200;
            }
            assertThat(tasks.countByte(fileWith("a.bin", data), (byte) 200)).isEqualTo(10_000);
        }
    }

    // ── Task 6: sameContent ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement sameContent()")
    @DisplayName("Task 6 — sameContent()")
    class SameContentTest {

        @Test
        @DisplayName("identical content → true")
        void same() throws IOException {
            File a = fileWith("a.bin", (byte) 1, (byte) 2, (byte) 3);
            File b = fileWith("b.bin", (byte) 1, (byte) 2, (byte) 3);
            assertThat(tasks.sameContent(a, b)).isTrue();
        }

        @Test
        @DisplayName("one different byte → false")
        void different() throws IOException {
            File a = fileWith("a.bin", (byte) 1, (byte) 2, (byte) 3);
            File b = fileWith("b.bin", (byte) 1, (byte) 2, (byte) 4);
            assertThat(tasks.sameContent(a, b)).isFalse();
        }

        @Test
        @DisplayName("a file and its prefix are different")
        void prefix() throws IOException {
            File a = fileWith("a.bin", (byte) 1, (byte) 2, (byte) 3);
            File b = fileWith("b.bin", (byte) 1, (byte) 2, (byte) 3, (byte) 4);
            assertThat(tasks.sameContent(a, b)).isFalse();
            assertThat(tasks.sameContent(b, a)).isFalse();
        }

        @Test
        @DisplayName("two empty files are the same")
        void bothEmpty() throws IOException {
            assertThat(tasks.sameContent(fileWith("a.bin"), fileWith("b.bin"))).isTrue();
        }

        @Test
        @DisplayName("a file compared with itself is the same")
        void itself() throws IOException {
            File a = fileWith("a.bin", pattern(100));
            assertThat(tasks.sameContent(a, a)).isTrue();
        }

        @Test
        @DisplayName("a difference at the very end of a large file is found")
        void lateDifference() throws IOException {
            byte[] one = pattern(30_000);
            byte[] two = pattern(30_000);
            two[29_999] = (byte) (two[29_999] + 1);
            assertThat(tasks.sameContent(fileWith("a.bin", one), fileWith("b.bin", two))).isFalse();
        }

        @Test
        @DisplayName("neither file is modified")
        void notModified() throws IOException {
            byte[] one = pattern(64);
            byte[] two = pattern(65);
            File a = fileWith("a.bin", one);
            File b = fileWith("b.bin", two);
            tasks.sameContent(a, b);
            assertThat(bytesOf(a)).isEqualTo(one);
            assertThat(bytesOf(b)).isEqualTo(two);
        }
    }
}
