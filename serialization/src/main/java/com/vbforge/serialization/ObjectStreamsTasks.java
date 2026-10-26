package com.vbforge.serialization;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

/**
 * ObjectStreamsTasks — implement every method below to make {@code ObjectStreamsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ObjectStreamsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ObjectStreamsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Work with in-memory byte arrays only; no files are involved.
 */
public class ObjectStreamsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Serializes {@code obj} with an {@link java.io.ObjectOutputStream} and returns the
     * resulting bytes. Any exception thrown while writing (for example
     * {@link java.io.NotSerializableException}) must propagate to the caller.
     *
     * <p>Examples:
     * <pre>
     *   serialize("hi")               → byte[] that starts with 0xAC 0xED (the stream magic number)
     *   serialize(new Point(1, 2))    → bytes that deserialize back to an equal Point
     *   serialize(listWithAnObject)   → throws NotSerializableException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Wrap a {@link java.io.ByteArrayOutputStream} in an {@code ObjectOutputStream}.</li>
     *   <li>Read the bytes only AFTER the object stream has been closed or flushed.</li>
     * </ul>
     *
     * @param obj the object to serialize
     * @return the serialized form of {@code obj}
     * @throws IOException if writing fails
     */
    public byte[] serialize(Serializable obj) throws IOException {
        throw new UnsupportedOperationException("TODO: implement serialize");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Reads ONE object from {@code data} and returns it. If the stream contains
     * several objects, only the first one is returned. Any exception thrown while
     * reading (for example {@link java.io.StreamCorruptedException}) must propagate.
     *
     * <p>Examples:
     * <pre>
     *   deserialize(bytesOf("hello"))           → "hello"
     *   deserialize(bytesOf(new Point(3, 4)))   → a NEW Point equal to (3, 4)
     *   deserialize(bytesOf("first", "second")) → "first"
     *   deserialize(new byte[]{1, 2, 3, 4})     → throws StreamCorruptedException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Wrap a {@link java.io.ByteArrayInputStream} in an {@code ObjectInputStream}.</li>
     *   <li>{@code readObject()} returns {@code Object}; the caller does the cast.</li>
     * </ul>
     *
     * @param data bytes produced by an {@code ObjectOutputStream}
     * @return the first object stored in {@code data}
     * @throws IOException            if the data is not a valid object stream
     * @throws ClassNotFoundException if the stored class cannot be found
     */
    public Object deserialize(byte[] data) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("TODO: implement deserialize");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a DEEP copy of {@code original} made by serializing it and reading it
     * back. The copy is a different object, and so is every object reachable from it.
     * References that were shared inside the original graph are still shared (the same
     * single object) inside the copy.
     *
     * <p>Examples:
     * <pre>
     *   deepCopy(["a", "b"])           → a new list ["a", "b"]  (not the same instance)
     *   deepCopy(box)                  → a Box whose point list can be changed without affecting {@code box}
     *   deepCopy(listWithAnObject)     → throws NotSerializableException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>An in-memory round trip through object streams is all you need.</li>
     *   <li>The unchecked cast to {@code T} is safe here — you may suppress the warning.</li>
     * </ul>
     *
     * @param original the object to copy
     * @param <T>      a serializable type
     * @return a deep copy of {@code original}
     * @throws IOException            if serialization fails
     * @throws ClassNotFoundException if deserialization cannot find a class
     */
    public <T extends Serializable> T deepCopy(T original) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("TODO: implement deepCopy");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code obj} can really be serialized, and {@code false}
     * if serialization fails with a {@link java.io.NotSerializableException}.
     * {@code null} counts as serializable. Other {@link IOException}s are not expected
     * for in-memory streams and may propagate.
     *
     * <p>Examples:
     * <pre>
     *   isSerializable("text")                    → true
     *   isSerializable(new Object())              → false
     *   isSerializable(["a", "b"])                → true
     *   isSerializable(["a", new Object()])       → false   (the list itself IS Serializable!)
     *   isSerializable(null)                      → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code obj instanceof Serializable} is not enough — it only checks the outer object.</li>
     *   <li>Actually try to write the object and react to the specific exception type.</li>
     * </ul>
     *
     * @param obj the object to test (may be {@code null})
     * @return whether the whole object graph can be serialized
     * @throws IOException for I/O failures other than {@code NotSerializableException}
     */
    public boolean isSerializable(Object obj) throws IOException {
        throw new UnsupportedOperationException("TODO: implement isSerializable");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Writes every element of {@code objects}, in order, into ONE object stream and
     * returns the bytes. If the same instance appears twice in the list, it is written
     * to the stream once in full and then referenced — so after reading, both positions
     * are the SAME instance.
     *
     * <p>Examples:
     * <pre>
     *   serializeAll(["a", "b", "c"])     → one stream; reading it gives "a", "b", "c" in that order
     *   serializeAll([])                  → a valid stream with no objects in it
     *   serializeAll([p, p])              → reading gives the same Point instance twice
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>One {@code ObjectOutputStream}, one {@code writeObject} call per element.</li>
     *   <li>Do not create a new stream per element — a stream is a single sequence.</li>
     * </ul>
     *
     * @param objects the objects to write (all must be serializable)
     * @return one byte array holding all objects, in list order
     * @throws IOException if writing fails (for example a non-serializable element)
     */
    public byte[] serializeAll(List<? extends Serializable> objects) throws IOException {
        throw new UnsupportedOperationException("TODO: implement serializeAll");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Reads EVERY object stored in {@code data} and returns them, in stream order, in a
     * new list. An empty stream (no objects) gives an empty list.
     *
     * <p>Examples:
     * <pre>
     *   deserializeAll(bytesOf("a", 42, point))   → ["a", 42, point']   (point' is an equal copy)
     *   deserializeAll(bytesOf())                 → []
     *   deserializeAll(bytesOf(p, p))             → [p', p']  where both entries are the same instance
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>You do not know the number of objects in advance.</li>
     *   <li>Look at what {@code readObject()} throws when the stream has no more data.</li>
     * </ul>
     *
     * @param data bytes produced by one {@code ObjectOutputStream}
     * @return all objects in the stream, in order
     * @throws IOException            if the data is not a valid object stream
     * @throws ClassNotFoundException if a stored class cannot be found
     */
    public List<Object> deserializeAll(byte[] data) throws IOException, ClassNotFoundException {
        throw new UnsupportedOperationException("TODO: implement deserializeAll");
    }
}
