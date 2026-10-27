# file-io-basics

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 8 | Package: `com.vbforge.file_io_basics` | Tags: `I/O`

## What's covered

| Topic | Key points |
|---|---|
| `java.io.File` | An **abstract path** — creating a `File` touches nothing; failures return `false` instead of throwing; `mkdir` vs `mkdirs`; `list()` / `listFiles()` return `null` for a missing path or a regular file; no recursion built in |
| `BufferedReader` / `BufferedWriter` / `PrintWriter` / `Scanner` | `readLine()` returns `null` at the end; explicit **UTF-8** via `InputStreamReader` / `OutputStreamWriter`; append via `FileOutputStream(file, true)`; `FileWriter`/`FileReader` use the platform charset on Java 8; the `nextInt()` / `nextLine()` trap; `PrintWriter` swallows errors (`checkError`) |
| try-with-resources | Closed in **reverse** order and **before** `catch`; suppressed exceptions; wrapping constructors can leak; `null` resources are allowed; `Closeable.close()` is idempotent |
| `Path` / `Files` (NIO.2) | `resolve` / `relativize` / `normalize`; `Files.readAllLines`, `write`, `lines` (**must be closed**; `UncheckedIOException`), `copy` / `move` / `delete` with `REPLACE_EXISTING`, `list` vs `walk`; path-traversal safety |
| Byte streams | `read()` returns an `int` 0..255 or -1; signed bytes; `read(buffer)` returns *n*; buffering counted, not timed; `DataInput/OutputStream` is big-endian and throws `EOFException`; in-memory `ByteArray*Stream`; the copy loop |

## Files

```
src/
├── main/java/com/vbforge/file_io_basics/
│   ├── FileBasicsDemo.java             ← File object, create/delete, directories, tree walk
│   ├── FileBasicsTasks.java            ← 🎯 your tasks — implement these methods
│   ├── ReaderWriterDemo.java           ← BufferedReader/Writer, charsets, PrintWriter, Scanner
│   ├── ReaderWriterTasks.java          ← 🎯 your tasks — implement these methods
│   ├── TryWithResourcesDemo.java       ← close order, suppressed exceptions, wrapping leak
│   ├── TryWithResourcesTasks.java      ← 🎯 your tasks — implement these methods
│   ├── PathFilesDemo.java              ← Path, Files, lazy lines, copy/move, walk, path safety
│   ├── PathFilesTasks.java             ← 🎯 your tasks — implement these methods
│   ├── BinaryStreamsDemo.java          ← byte streams, buffering, Data streams, copy loop
│   └── BinaryStreamsTasks.java         ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/file_io_basics/
    ├── FileBasicsDemoTest.java         ← 🧪 tests for the demo (already green)
    ├── FileBasicsTasksTest.java        ← 🧪 tests for your tasks — all must pass
    ├── ReaderWriterDemoTest.java       ← 🧪 tests for the demo (already green)
    ├── ReaderWriterTasksTest.java      ← 🧪 tests for your tasks — all must pass
    ├── TryWithResourcesDemoTest.java   ← 🧪 tests for the demo (already green)
    ├── TryWithResourcesTasksTest.java  ← 🧪 tests for your tasks — all must pass
    ├── PathFilesDemoTest.java          ← 🧪 tests for the demo (already green)
    ├── PathFilesTasksTest.java         ← 🧪 tests for your tasks — all must pass
    ├── BinaryStreamsDemoTest.java      ← 🧪 tests for the demo (already green)
    └── BinaryStreamsTasksTest.java     ← 🧪 tests for your tasks — all must pass
```

> Every test works in its own temporary directory (`@TempDir`), names its charset explicitly
> and never asserts line separators, absolute paths or timings — so results are the same on
> every operating system.

## 🎯 Tasks

Five Demo / Tasks pairs, 28 task methods in total. Each has a detailed Javadoc.

### `FileBasicsTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `fileExtension(file)` | `getName`, `lastIndexOf`, hidden files |
| 2 | `ensureParentDirs(file)` | `getParentFile` may be `null`, `mkdirs` returning `false` |
| 3 | `listFileNames(dir)` | `listFiles` returns `null`, sorting, files only |
| 4 | `totalSize(root)` | recursion over a directory tree |
| 5 | `deleteRecursively(target)` | children first, then the directory |

### `ReaderWriterTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `writeLines(file, lines)` | `BufferedWriter` + UTF-8 + `newLine()` |
| 2 | `readLines(file)` | the `readLine() != null` loop |
| 3 | `appendLine(file, line)` | `FileOutputStream(file, true)` |
| 4 | `countWords(file)` | whitespace handling, `trim()` + `split` |
| 5 | `longestLine(file)` | single pass, strict `>` for ties, `Optional` |
| 6 | `copyWithLineNumbers(source, target)` | two resources at once |

### `TryWithResourcesTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `readFirstLine(file)` | reader in the try header, `null` for an empty file |
| 2 | `firstLineOrDefault(file, fallback)` | missing / empty file → fallback |
| 3 | `upperCaseCopy(in, out)` | two resources in one header, `Locale.ROOT` |
| 4 | `eventLog(first, second)` | reverse closing order |
| 5 | `failureSummary(bodyMessage, closeMessage)` | `getSuppressed()` |

### `PathFilesTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `relativeBetween(base, target)` | `Path.relativize` |
| 2 | `resolveInside(base, userPath)` | resolve → normalize → `startsWith` |
| 3 | `nonBlankLines(file)` | `Files.readAllLines`, `trim().isEmpty()` |
| 4 | `countLinesContaining(file, needle)` | `Files.lines` inside try-with-resources |
| 5 | `writeText(file, text)` | `createDirectories`, UTF-8 `Files.write` |
| 6 | `backup(file)` | `resolveSibling`, `REPLACE_EXISTING` |

### `BinaryStreamsTasks`

| # | Method | Topic |
|---|---|---|
| 1 | `copy(source, target)` | `read(buffer)` → `write(buffer, 0, n)` |
| 2 | `readAllBytes(file)` | `ByteArrayOutputStream` |
| 3 | `writeInts(file, values)` | `DataOutputStream.writeInt`, big-endian |
| 4 | `readInts(file)` | `DataInputStream.readInt`, `EOFException` |
| 5 | `countByte(file, target)` | `read()` returns 0..255 — compare as bytes |
| 6 | `sameContent(a, b)` | length shortcut + buffered byte comparison |

```bash
mvn test -Dtest=FileBasicsTasksTest
mvn test -Dtest=ReaderWriterTasksTest
mvn test -Dtest=TryWithResourcesTasksTest
mvn test -Dtest=PathFilesTasksTest
mvn test -Dtest=BinaryStreamsTasksTest
```

All 28 task groups must go **GREEN** ✅

## Key things to remember

```java
// java.io.File reports most failures with a boolean — ignoring it hides the problem
file.delete();                          // ❌ false means "did not work", nothing is thrown
Files.delete(path);                     // ✅ throws NoSuchFileException / DirectoryNotEmptyException

new File(dir, "a/b").mkdir();           // ❌ false when the parent is missing
new File(dir, "a/b").mkdirs();          // ✅ creates every level (false also for "already exists")

// list() / listFiles() return null — not an empty array
for (String n : file.list()) { }        // ❌ NPE for a missing path or a regular file
String[] names = file.list();
if (names != null) { }                  // ✅

// readLine() ends with null, not with an empty string
while ((line = reader.readLine()) != null) { }          // ✅ blank lines are "" and keep going

// Always name the charset — the default depends on the machine
new FileReader(file);                                   // ❌ platform default on Java 8
new BufferedReader(new InputStreamReader(
        new FileInputStream(file), StandardCharsets.UTF_8));   // ✅

// Append is decided when the BYTE stream is opened
new OutputStreamWriter(new FileOutputStream(file, true), StandardCharsets.UTF_8);   // ✅ append + UTF-8

// Scanner: nextInt() leaves the line break behind
int n = scanner.nextInt();
String rest = scanner.nextLine();       // ❌ "" — the end of the number's line
scanner.nextLine();                     // ✅ consume it first, then read the real line

// try-with-resources: reverse order, closed BEFORE catch, one resource per declaration
try (Reader in = new FileReader(f);
     BufferedReader r = new BufferedReader(in)) { }     // ✅ if the outer constructor fails, 'in' is still closed
try (BufferedReader r = new BufferedReader(new FileReader(f))) { }   // ❌ inner reader can leak

// Body exception wins; close() failures are attached via getSuppressed()
e.getSuppressed();                      // the exceptions from close()

// Files.lines holds an open file — close it
long n = Files.lines(p).count();                        // ❌ leaked handle
try (Stream<String> s = Files.lines(p, UTF_8)) { }      // ✅ also throws UncheckedIOException while consuming

// Path.resolve with an absolute argument DROPS the base
Paths.get("/srv/files").resolve("/etc/passwd");         // /etc/passwd

// Path safety: resolve → normalize → THEN check
base.resolve("../etc/passwd").startsWith(base);                 // ❌ true — looks safe, is not
base.resolve("../etc/passwd").normalize().startsWith(base);     // ✅ false
path.startsWith(base);                  // ✅ compares whole name elements (files2 is not inside files)
path.toString().startsWith(base.toString());            // ❌ "/srv/files2/x" starts with "/srv/files"

// Byte streams: read() returns an int 0..255, -1 means the end
int b = in.read();                      // byte 0xFF arrives as 255, never as -1
byte signed = (byte) b;                 // -1 as a Java byte
// read(buffer) returns how many bytes were filled
while ((n = in.read(buffer)) != -1) out.write(buffer, 0, n);    // ✅ write n bytes, not buffer.length

// Buffering: wrap file streams in Buffered* for one-byte-at-a-time work
new DataOutputStream(new BufferedOutputStream(new FileOutputStream(f)));   // ✅

// DataInputStream is big-endian and signals the end with EOFException
in.readInt();                           // EOFException, not -1
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run a main demo (one per concept)
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_basics.FileBasicsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_basics.ReaderWriterDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_basics.TryWithResourcesDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_basics.PathFilesDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_basics.BinaryStreamsDemo"
```
