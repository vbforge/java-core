package com.vbforge.file_io_basics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * BinaryStreamsDemoTest — verifies every concept shown in {@link BinaryStreamsDemo}.
 *
 * <p>These tests are always GREEN. Buffering is verified by <em>counting</em> calls to the
 * underlying stream — never by measuring time — so the results are deterministic.
 */
@DisplayName("BinaryStreamsDemo — API contracts")
class BinaryStreamsDemoTest {

    @TempDir
    File workDir;

    /** An output stream that counts how many times write(...) reaches it. */
    private static class CountingStream extends OutputStream {
        int calls;
        int bytes;

        @Override
        public void write(int b) {
            calls++;
            bytes++;
        }

        @Override
        public void write(byte[] b, int off, int len) {
            calls++;
            bytes += len;
        }
    }

    private interface IoAction {
        void run(DataOutputStream out) throws IOException;
    }

    // ── 1. File streams ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. File streams")
    class FileStreamsTest {

        @Test
        @DisplayName("write(int) stores only the low 8 bits")
        void writeStoresLowByte() throws IOException {
            File file = new File(workDir, "b.bin");
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(65);
                out.write(256 + 66);
            }
            assertThat(file.length()).isEqualTo(2L);
            try (FileInputStream in = new FileInputStream(file)) {
                assertThat(in.read()).isEqualTo(65);
                assertThat(in.read()).isEqualTo(66);
            }
        }

        @Test
        @DisplayName("read() returns 0..255 and -1 at the end of the stream")
        void readReturnsInt() throws IOException {
            File file = new File(workDir, "b.bin");
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(new byte[]{-1, 0});
            }
            try (FileInputStream in = new FileInputStream(file)) {
                assertThat(in.read()).isEqualTo(255);
                assertThat(in.read()).isEqualTo(0);
                assertThat(in.read()).isEqualTo(-1);
            }
        }

        @Test
        @DisplayName("casting the int to byte gives the signed Java value back")
        void signedBytes() {
            int fromStream = 255;
            assertThat((byte) fromStream).isEqualTo((byte) -1);
            assertThat((byte) -1 & 0xFF).isEqualTo(255);
        }

        @Test
        @DisplayName("read(buffer) returns how many bytes it filled, then -1")
        void readIntoBuffer() throws IOException {
            File file = new File(workDir, "b.bin");
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(new byte[]{1, 2, 3, 4, 5});
            }
            byte[] buffer = new byte[4];
            try (FileInputStream in = new FileInputStream(file)) {
                assertThat(in.read(buffer)).isEqualTo(4);
                assertThat(in.read(buffer)).isEqualTo(1);
                assertThat(buffer[0]).isEqualTo((byte) 5);
                assertThat(in.read(buffer)).isEqualTo(-1);
            }
        }

        @Test
        @DisplayName("append mode keeps the existing bytes")
        void append() throws IOException {
            File file = new File(workDir, "b.bin");
            try (FileOutputStream out = new FileOutputStream(file)) {
                out.write(new byte[]{1, 2});
            }
            try (FileOutputStream out = new FileOutputStream(file, true)) {
                out.write(3);
            }
            assertThat(file.length()).isEqualTo(3L);
        }

        @Test
        @DisplayName("opening a missing file for reading throws FileNotFoundException")
        void missing() {
            File missing = new File(workDir, "missing.bin");
            assertThatThrownBy(() -> new FileInputStream(missing)).isInstanceOf(java.io.FileNotFoundException.class);
        }
    }

    // ── 2. Buffering ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Buffering")
    class BufferingTest {

        @Test
        @DisplayName("unbuffered: one call per byte reaches the target")
        void unbuffered() throws IOException {
            CountingStream target = new CountingStream();
            for (int i = 0; i < 1000; i++) {
                target.write(i);
            }
            assertThat(target.calls).isEqualTo(1000);
        }

        @Test
        @DisplayName("buffered: 1000 single-byte writes reach the target as one call")
        void buffered() throws IOException {
            CountingStream target = new CountingStream();
            try (BufferedOutputStream out = new BufferedOutputStream(target, 8192)) {
                for (int i = 0; i < 1000; i++) {
                    out.write(i);
                }
                assertThat(target.calls).isZero();
            }
            assertThat(target.calls).isEqualTo(1);
            assertThat(target.bytes).isEqualTo(1000);
        }

        @Test
        @DisplayName("a buffer flushes by itself when it is full")
        void flushWhenFull() throws IOException {
            CountingStream target = new CountingStream();
            try (BufferedOutputStream out = new BufferedOutputStream(target, 100)) {
                for (int i = 0; i < 250; i++) {
                    out.write(i);
                }
            }
            assertThat(target.bytes).isEqualTo(250);
            assertThat(target.calls).isEqualTo(3);
        }

        @Test
        @DisplayName("flush pushes pending bytes without closing")
        void flush() throws IOException {
            CountingStream target = new CountingStream();
            BufferedOutputStream out = new BufferedOutputStream(target, 8192);
            out.write(1);
            assertThat(target.bytes).isZero();
            out.flush();
            assertThat(target.bytes).isEqualTo(1);
            out.close();
        }
    }

    // ── 3. Data streams ───────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. Data streams")
    class DataStreamsTest {

        private byte[] encode(IoAction action) throws IOException {
            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            try (DataOutputStream out = new DataOutputStream(bytes)) {
                action.run(out);
            }
            return bytes.toByteArray();
        }

        @Test
        @DisplayName("writeInt is 4 bytes, big-endian")
        void bigEndian() throws IOException {
            assertThat(encode(o -> o.writeInt(258))).containsExactly(0, 0, 1, 2);
            assertThat(encode(o -> o.writeInt(-1))).containsExactly(-1, -1, -1, -1);
        }

        @Test
        @DisplayName("values come back in the order they were written")
        void roundTrip() throws IOException {
            File file = new File(workDir, "data.bin");
            try (DataOutputStream out = new DataOutputStream(new FileOutputStream(file))) {
                out.writeInt(42);
                out.writeDouble(3.5);
                out.writeBoolean(true);
                out.writeUTF("héllo");
            }
            try (DataInputStream in = new DataInputStream(new FileInputStream(file))) {
                assertThat(in.readInt()).isEqualTo(42);
                assertThat(in.readDouble()).isEqualTo(3.5);
                assertThat(in.readBoolean()).isTrue();
                assertThat(in.readUTF()).isEqualTo("héllo");
            }
        }

        @Test
        @DisplayName("sizes: int 4, long 8, double 8, short 2, boolean 1")
        void sizes() throws IOException {
            assertThat(encode(o -> o.writeInt(1))).hasSize(4);
            assertThat(encode(o -> o.writeLong(1L))).hasSize(8);
            assertThat(encode(o -> o.writeDouble(1.0))).hasSize(8);
            assertThat(encode(o -> o.writeShort(1))).hasSize(2);
            assertThat(encode(o -> o.writeBoolean(true))).hasSize(1);
        }

        @Test
        @DisplayName("reading past the end throws EOFException instead of returning -1")
        void eof() throws IOException {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[]{0, 0, 0, 7}));
            assertThat(in.readInt()).isEqualTo(7);
            assertThatThrownBy(in::readInt).isInstanceOf(EOFException.class);
        }

        @Test
        @DisplayName("a partial int also fails with EOFException")
        void partial() {
            DataInputStream in = new DataInputStream(new ByteArrayInputStream(new byte[]{0, 0}));
            assertThatThrownBy(in::readInt).isInstanceOf(EOFException.class);
        }

        @Test
        @DisplayName("writeUTF prefixes the text with a 2-byte length")
        void writeUtfLength() throws IOException {
            assertThat(encode(o -> o.writeUTF("abc"))).containsExactly(0, 3, 'a', 'b', 'c');
        }
    }

    // ── 4. In-memory streams ──────────────────────────────────────────────────

    @Nested
    @DisplayName("4. In-memory streams")
    class InMemoryTest {

        @Test
        @DisplayName("ByteArrayOutputStream collects the bytes written to it")
        void collects() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(1);
            out.write(new byte[]{2, 3});
            assertThat(out.size()).isEqualTo(3);
            assertThat(out.toByteArray()).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("ByteArrayInputStream serves an array and then reports -1")
        void serves() throws IOException {
            InputStream in = new ByteArrayInputStream(new byte[]{9, -9});
            assertThat(in.read()).isEqualTo(9);
            assertThat(in.read()).isEqualTo(247);
            assertThat(in.read()).isEqualTo(-1);
        }

        @Test
        @DisplayName("toString(charset) decodes the collected bytes")
        void decode() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write("héllo".getBytes(StandardCharsets.UTF_8));
            assertThat(out.toString("UTF-8")).isEqualTo("héllo");
            assertThat(out.size()).isEqualTo(6);
        }

        @Test
        @DisplayName("toByteArray returns a copy; later writes do not change it")
        void copyOnRead() {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            out.write(1);
            byte[] snapshot = out.toByteArray();
            out.write(2);
            assertThat(snapshot).hasSize(1);
        }
    }

    // ── 5. Copying ────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. Copying")
    class CopyTest {

        private long copy(InputStream in, OutputStream out, int bufferSize) throws IOException {
            byte[] buffer = new byte[bufferSize];
            long total = 0;
            int n;
            while ((n = in.read(buffer)) != -1) {
                out.write(buffer, 0, n);
                total += n;
            }
            return total;
        }

        private byte[] sample(int length) {
            byte[] data = new byte[length];
            for (int i = 0; i < length; i++) {
                data[i] = (byte) (i * 31);
            }
            return data;
        }

        @Test
        @DisplayName("the read(buffer) loop copies every byte, also when the length is no multiple of the buffer")
        void copiesEverything() throws IOException {
            byte[] data = sample(1000);
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            long copied = copy(new ByteArrayInputStream(data), out, 64);
            assertThat(copied).isEqualTo(1000L);
            assertThat(out.toByteArray()).isEqualTo(data);
        }

        @Test
        @DisplayName("an empty source copies zero bytes")
        void empty() throws IOException {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            assertThat(copy(new ByteArrayInputStream(new byte[0]), out, 16)).isZero();
            assertThat(out.size()).isZero();
        }

        @Test
        @DisplayName("writing the whole buffer instead of n bytes would add stale bytes")
        void staleBufferBytes() throws IOException {
            byte[] data = sample(10);
            ByteArrayOutputStream wrong = new ByteArrayOutputStream();
            InputStream in = new ByteArrayInputStream(data);
            byte[] buffer = new byte[4];
            while (in.read(buffer) != -1) {
                wrong.write(buffer);
            }
            assertThat(wrong.size()).isEqualTo(12);
            assertThat(wrong.size()).isNotEqualTo(data.length);
        }

        @Test
        @DisplayName("copying between files preserves the bytes")
        void fileToFile() throws IOException {
            File source = new File(workDir, "in.bin");
            File target = new File(workDir, "out.bin");
            byte[] data = sample(5000);
            try (FileOutputStream out = new FileOutputStream(source)) {
                out.write(data);
            }
            try (InputStream in = new FileInputStream(source);
                 OutputStream out = new FileOutputStream(target)) {
                assertThat(copy(in, out, 1024)).isEqualTo(5000L);
            }
            assertThat(target.length()).isEqualTo(5000L);
        }
    }
}
