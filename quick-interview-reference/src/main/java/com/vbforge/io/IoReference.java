package com.vbforge.io;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.stream.Stream;

/**
 * ═══════════════════════════════════════════════════════════════
 *  I/O INTERVIEW REFERENCE
 *  Topics: InputStream/OutputStream · Reader/Writer ·
 *          NIO.2 Path/Files · serialization · common gotchas
 * ═══════════════════════════════════════════════════════════════
 *
 *  TODO: expand with your own snippets as you study this topic.
 */
@SuppressWarnings("all")
public class IoReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. STREAMS vs READERS/WRITERS
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  InputStream / OutputStream : raw bytes — use for binary data (images, PDFs)
     *  Reader / Writer            : characters — use for text (handles encoding)
     *
     *  ALWAYS specify charset explicitly — never rely on platform default!
     *  ✅ new InputStreamReader(stream, StandardCharsets.UTF_8)
     *  ❌ new InputStreamReader(stream)  — platform-dependent encoding
     */

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. NIO.2 FILES API (Java 7+) — prefer over java.io.File
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void nioFilesApi() throws Exception {
        Path path = Path.of("data.txt");

        // Read all content (small files only)
        String content = Files.readString(path, StandardCharsets.UTF_8);   // Java 11+
        var lines = Files.readAllLines(path, StandardCharsets.UTF_8);

        // Write content
        Files.writeString(path, "hello", StandardCharsets.UTF_8);          // Java 11+
        Files.write(path, "hello".getBytes(StandardCharsets.UTF_8));

        // Streaming large files — lazy, memory efficient
        try (Stream<String> stream = Files.lines(path, StandardCharsets.UTF_8)) {
            stream.filter(l -> !l.isBlank()).forEach(System.out::println);
        }

        // Path operations
        Path parent   = path.getParent();
        Path absolute = path.toAbsolutePath();
        Path resolved = parent.resolve("other.txt");  // append filename to directory

        // File checks
        Files.exists(path);
        Files.isDirectory(path);
        Files.isReadable(path);

        // Copy / move
        Files.copy(path, Path.of("backup.txt"), StandardCopyOption.REPLACE_EXISTING);
        Files.move(path, Path.of("moved.txt"),  StandardCopyOption.ATOMIC_MOVE);

        // Create dirs
        Files.createDirectories(Path.of("a/b/c"));
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. SERIALIZATION GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  serialVersionUID: if not declared, JVM generates one from class structure.
     *  Adding a field without declaring serialVersionUID → deserialization fails!
     *  ✅ Always declare: private static final long serialVersionUID = 1L;
     *
     *  transient: field excluded from serialization (passwords, caches, streams)
     *
     *  SECURITY: never deserialize untrusted data — Java deserialization is a known attack vector.
     *  ✅ Prefer JSON (Jackson) or Protocol Buffers over Java serialization.
     */
    static class User implements Serializable {
        private static final long serialVersionUID = 1L;
        private String name;
        private transient String password;  // not serialized
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Not closing streams                      → resource leak
     *  ❌ Not using try-with-resources             → resource leak on exception
     *  ❌ Platform-default charset                 → different behavior on different OS
     *  ❌ Reading large files with readAllBytes()  → OutOfMemoryError
     *  ❌ Using java.io.File instead of Path       → less capable API
     *  ❌ Deserializing untrusted data             → security vulnerability
     *  ❌ Not declaring serialVersionUID           → brittle deserialization
     */

    public static void main(String[] args) throws Exception {
        nioFilesApi();
    }
}
