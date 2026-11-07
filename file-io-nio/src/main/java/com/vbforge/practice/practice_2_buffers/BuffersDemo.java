package com.vbforge.practice.practice_2_buffers;

import java.nio.*;
import java.nio.charset.StandardCharsets;

/**
 * Demonstrates NIO Buffers — the data containers that Channel I/O always uses.
 *
 * What is a Buffer?
 *   A fixed-capacity container for a SEQUENCE OF PRIMITIVE VALUES of one type.
 *   Types: ByteBuffer, CharBuffer, ShortBuffer, IntBuffer, LongBuffer,
 *          FloatBuffer, DoubleBuffer.
 *   ByteBuffer is by far the most important — it's what FileChannel uses.
 *
 * Core state — three pointers that govern every operation:
 *
 *   capacity  — total slots allocated; NEVER changes
 *   limit     — first index that must not be read/written; ≤ capacity
 *   position  — next index to read/write; ≤ limit
 *
 *   Invariant: 0 ≤ position ≤ limit ≤ capacity
 *
 * Mode transitions — the most common source of bugs:
 *
 *   NEW BUFFER:  position=0, limit=capacity  (write mode: fill from 0)
 *   flip()    :  limit=position, position=0  (switch to read mode)
 *   clear()   :  position=0, limit=capacity  (discard data, write mode)
 *   compact() :  copy [position..limit) to front, position=remaining, limit=capacity
 *                (preserves unread data, ready for more writing)
 *   rewind()  :  position=0  (re-read from beginning, limit unchanged)
 *   mark()    :  mark=position
 *   reset()   :  position=mark  (go back to marked position)
 *
 * Two kinds of ByteBuffer:
 *   Heap buffer   — ByteBuffer.allocate(n)     backed by a byte[] on the JVM heap
 *   Direct buffer — ByteBuffer.allocateDirect(n) backed by native OS memory
 *
 * Direct buffers:
 *   - Not on the GC heap — OS memory, outside GC reach
 *   - Faster for Channel I/O (no copy from heap to OS buffer needed)
 *   - Slower to allocate; GC does not manage them — risk of off-heap memory leaks
 *   - Use for: large, long-lived buffers used repeatedly with channels
 */
public class BuffersDemo {

    // =========================================================================
    // 1. State model — capacity, limit, position
    // =========================================================================

    static void stateModelDemo() {
        System.out.println("\n--- 1. Buffer state model ---");

        ByteBuffer buf = ByteBuffer.allocate(10);
        printState("after allocate(10)", buf);

        // Write 4 bytes
        buf.put((byte) 'A');
        buf.put((byte) 'B');
        buf.put((byte) 'C');
        buf.put((byte) 'D');
        printState("after put x4", buf);

        // flip — switch to read mode
        buf.flip();
        printState("after flip()", buf);

        // Read 2 bytes
        System.out.println("  read: " + (char) buf.get() + (char) buf.get());
        printState("after get x2", buf);

        // compact — keep unread bytes, ready to write more
        buf.compact();
        printState("after compact()", buf);  // C and D moved to front

        // clear — discard all, full write mode
        buf.clear();
        printState("after clear()", buf);
    }

    static void printState(String label, Buffer buf) {
        System.out.printf("  %-30s pos=%-3d lim=%-3d cap=%d  rem=%d%n",
            label, buf.position(), buf.limit(), buf.capacity(), buf.remaining());
    }

    // =========================================================================
    // 2. ByteBuffer — put/get variants
    // =========================================================================

    /**
     * Relative put/get: advance position by the element size.
     * Absolute put/get: specify index; position unchanged.
     *
     * Typed putX/getX: write/read Java primitives directly
     *   putInt, putLong, putDouble, putFloat, putShort, putChar
     *   (takes the number of bytes of that type)
     */
    static void putGetVariantsDemo() {
        System.out.println("\n--- 2. put/get variants ---");

        ByteBuffer buf = ByteBuffer.allocate(32);

        // Relative — position advances
        buf.put((byte) 0x01);
        buf.putInt(100);
        buf.putLong(999_999_999_999L);
        buf.putDouble(Math.PI);
        System.out.println("position after writes: " + buf.position()); // 1+4+8+8=21

        // Read back
        buf.flip();
        System.out.println("byte:   " + buf.get());
        System.out.println("int:    " + buf.getInt());
        System.out.println("long:   " + buf.getLong());
        System.out.printf ("double: %.5f%n", buf.getDouble());

        // Absolute — position does NOT change
        ByteBuffer abs = ByteBuffer.allocate(8);
        abs.putInt(0, 42);      // write int at index 0
        abs.putInt(4, 99);      // write int at index 4
        System.out.println("\nabsolute getInt(0): " + abs.getInt(0));
        System.out.println("absolute getInt(4): " + abs.getInt(4));
        System.out.println("position unchanged: " + abs.position()); // still 0
    }

    // =========================================================================
    // 3. flip / rewind / mark / reset
    // =========================================================================

    static void navigationDemo() {
        System.out.println("\n--- 3. flip / rewind / mark / reset ---");

        ByteBuffer buf = ByteBuffer.allocate(8);
        buf.put(new byte[]{'H', 'e', 'l', 'l', 'o'});
        buf.flip();  // prepare to read

        // rewind — re-read from the beginning
        System.out.print("first pass:  ");
        while (buf.hasRemaining()) System.out.print((char) buf.get());
        System.out.println();

        buf.rewind();  // position=0, limit unchanged
        System.out.print("after rewind:");
        while (buf.hasRemaining()) System.out.print((char) buf.get());
        System.out.println();

        // mark / reset — save and restore a position
        buf.rewind();
        buf.get(); buf.get(); // read H, e
        buf.mark();            // mark position 2 (before 'l')
        buf.get(); buf.get();  // read l, l
        System.out.println("after mark+read2 more: pos=" + buf.position());

        buf.reset();           // back to mark
        System.out.println("after reset:           pos=" + buf.position()); // 2

        // slice() — a sub-buffer sharing the same backing array
        buf.position(2); buf.limit(5);
        ByteBuffer slice = buf.slice();
        System.out.println("slice capacity:  " + slice.capacity()); // 3 (l, l, o)
        System.out.print("slice content:   ");
        while (slice.hasRemaining()) System.out.print((char) slice.get());
        System.out.println();
    }

    // =========================================================================
    // 4. ByteBuffer views — treat bytes as another primitive type
    // =========================================================================

    /**
     * asIntBuffer(), asLongBuffer(), asDoubleBuffer() etc. create a VIEW
     * over the same backing memory in a different type.
     *
     * Changes to the view are reflected in the original ByteBuffer and vice versa.
     * The view's position / limit are independent of the original's.
     */
    static void viewBuffersDemo() {
        System.out.println("\n--- 4. ByteBuffer views ---");

        ByteBuffer bb = ByteBuffer.allocate(16); // 4 ints × 4 bytes

        // Write via IntBuffer view
        IntBuffer ib = bb.asIntBuffer();
        ib.put(new int[]{10, 20, 30, 40});
        System.out.println("IntBuffer position: " + ib.position()); // 4

        // Read back via ByteBuffer (raw bytes)
        System.out.println("ByteBuffer position: " + bb.position()); // 0 — unchanged

        // Read back via the same view
        ib.rewind();
        System.out.print("via IntBuffer:  ");
        while (ib.hasRemaining()) System.out.print(ib.get() + " ");
        System.out.println();

        // Read as raw int from ByteBuffer (big-endian by default)
        System.out.print("via ByteBuffer: ");
        for (int i = 0; i < 4; i++) System.out.print(bb.getInt(i * 4) + " ");
        System.out.println();
    }

    // =========================================================================
    // 5. Byte order — big-endian vs little-endian
    // =========================================================================

    /**
     * ByteBuffer defaults to BIG_ENDIAN (network byte order).
     * x86 CPUs use LITTLE_ENDIAN.
     *
     * order(ByteOrder) changes the interpretation for typed put/get.
     * This affects multi-byte reads/writes (putInt, putLong, etc.) but NOT put(byte).
     */
    static void byteOrderDemo() {
        System.out.println("\n--- 5. Byte order ---");

        int value = 0x01020304;

        ByteBuffer big = ByteBuffer.allocate(4).order(ByteOrder.BIG_ENDIAN);
        big.putInt(value);
        System.out.printf("BIG_ENDIAN:    %02X %02X %02X %02X%n",
            big.get(0), big.get(1), big.get(2), big.get(3)); // 01 02 03 04

        ByteBuffer little = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN);
        little.putInt(value);
        System.out.printf("LITTLE_ENDIAN: %02X %02X %02X %02X%n",
            little.get(0), little.get(1), little.get(2), little.get(3)); // 04 03 02 01

        System.out.println("native order:  " + ByteOrder.nativeOrder());
    }

    // =========================================================================
    // 6. Heap vs Direct buffer
    // =========================================================================

    /**
     * allocate(n)       — heap buffer;   hasArray()=true;  array() returns the byte[]
     * allocateDirect(n) — direct buffer; hasArray()=false; backed by native memory
     *
     * wrap(byte[])      — heap buffer wrapping an existing array (no copy!)
     *
     * Direct buffer is preferred for large buffers used repeatedly with Channels.
     * Heap buffer is simpler; use it for in-memory manipulation.
     */
    static void heapVsDirectDemo() {
        System.out.println("\n--- 6. Heap vs Direct buffer ---");

        ByteBuffer heap   = ByteBuffer.allocate(1024);
        ByteBuffer direct = ByteBuffer.allocateDirect(1024);

        System.out.println("heap.isDirect():    " + heap.isDirect());
        System.out.println("direct.isDirect():  " + direct.isDirect());
        System.out.println("heap.hasArray():    " + heap.hasArray());
        System.out.println("direct.hasArray():  " + direct.hasArray());

        // wrap — shares the backing array directly (no copy)
        byte[] arr = {1, 2, 3, 4, 5};
        ByteBuffer wrapped = ByteBuffer.wrap(arr);
        wrapped.put(0, (byte) 99);  // mutate via buffer
        System.out.println("arr[0] after buf.put(0,99): " + arr[0]); // 99 — shared!

        // wrap with offset
        ByteBuffer sub = ByteBuffer.wrap(arr, 2, 3); // offset=2, length=3
        System.out.println("wrap(arr,2,3) position: " + sub.position()); // 2
        System.out.println("wrap(arr,2,3) limit:    " + sub.limit());    // 5
    }

    // =========================================================================
    // 7. Encoding / decoding text via CharBuffer + Charset
    // =========================================================================

    /**
     * The standard way to encode a String to ByteBuffer and decode back:
     *
     *   encode: Charset.encode(CharBuffer) → ByteBuffer
     *   decode: Charset.decode(ByteBuffer) → CharBuffer  (then .toString())
     *
     * This handles multi-byte encodings (UTF-8, UTF-16) correctly.
     */
    static void textEncodingDemo() {
        System.out.println("\n--- 7. Text encoding / decoding ---");

        String text = "Hello, Буфер!";  // mix of ASCII and Cyrillic

        // Encode String → ByteBuffer (UTF-8)
        ByteBuffer encoded = StandardCharsets.UTF_8.encode(text);
        System.out.println("encoded bytes:  " + encoded.remaining());

        // Decode ByteBuffer → String
        String decoded = StandardCharsets.UTF_8.decode(encoded).toString();
        System.out.println("decoded text:   " + decoded);
        System.out.println("round-trip ok:  " + text.equals(decoded));
    }

    // =========================================================================
    // main
    // =========================================================================

    public static void main(String[] args) {
        System.out.println("=== Buffers Demo ===");
        stateModelDemo();
        putGetVariantsDemo();
        navigationDemo();
        viewBuffersDemo();
        byteOrderDemo();
        heapVsDirectDemo();
        textEncodingDemo();
    }
}