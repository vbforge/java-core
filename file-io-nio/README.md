# file-io-nio

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 8 | Package: `com.vbforge.file_io_nio`

## What's covered

| Topic | Key points |
|---|---|
| `Path` basics | Immutable value, never touches the disk; `getFileName`, `getParent`, `subpath`; `startsWith` is per name element |
| `Path` operations | `resolve` (absolute arg wins), `relativize` (no mixing absolute/relative), `normalize` (lexical only); path traversal check |
| `Files` read/write | `write`, `readAllLines`, `newBufferedReader/Writer`, `Files.lines` (close it!); `APPEND`, `CREATE_NEW`; explicit UTF-8 |
| Copy / move / delete | `REPLACE_EXISTING`, `ATOMIC_MOVE`, shallow directory copy, `delete` vs `deleteIfExists`, `createTempFile` |
| `DirectoryStream` | One level, lazy, glob or `Filter`; unspecified order; single `iterator()` |
| `Files.list/walk/find` | `walk` includes the start dir; `maxDepth`; `find` receives `BasicFileAttributes` |
| `walkFileTree` | `preVisitDirectory` / `visitFile` / `postVisitDirectory`; `SKIP_SUBTREE`, `TERMINATE`; recursive copy/delete |
| `PathMatcher` | `glob:` vs `regex:`; `*` vs `**`; match against `getFileName()` |
| File attributes | `BasicFileAttributes` snapshot, `FileTime` ⇄ `Instant`, attribute views, string API (`"basic:*"`) |
| POSIX & symlinks | `PosixFilePermissions.fromString/toString`; `NOFOLLOW_LINKS`; dangling links |
| `WatchService` | `register` → `poll/take` → `pollEvents` → **`reset`**; `ENTRY_CREATE/MODIFY/DELETE`, `OVERFLOW`; not recursive |

## Files

```
src/
├── main/java/com/vbforge/file_io_nio/
│   ├── PathsAndFilesDemo.java         ← Path, Files read/write, copy/move/delete
│   ├── PathsAndFilesTasks.java        ← 🎯 your tasks — implement these methods
│   ├── DirectoryStreamDemo.java       ← DirectoryStream, list/walk/find, walkFileTree, PathMatcher
│   ├── DirectoryStreamTasks.java      ← 🎯 your tasks — implement these methods
│   ├── FileAttributesDemo.java        ← attributes, FileTime, views, POSIX, symlinks
│   ├── FileAttributesTasks.java       ← 🎯 your tasks — implement these methods
│   ├── WatchServiceDemo.java          ← watch loop, event kinds, key lifecycle
│   └── WatchServiceTasks.java         ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/file_io_nio/
    ├── PathsAndFilesDemoTest.java      ← tests for the demo (already green)
    ├── PathsAndFilesTasksTest.java     ← 🧪 tests for your tasks — all must pass
    ├── DirectoryStreamDemoTest.java    ← tests for the demo (already green)
    ├── DirectoryStreamTasksTest.java   ← 🧪 tests for your tasks — all must pass
    ├── FileAttributesDemoTest.java     ← tests for the demo (already green)
    ├── FileAttributesTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── WatchServiceDemoTest.java       ← tests for the demo (already green)
    └── WatchServiceTasksTest.java      ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Four Demo/Tasks pairs, 20 task methods in total. Each has a detailed Javadoc.

**`PathsAndFilesTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `relativeBetween(from, to)` | `normalize` + `relativize` |
| 2 | `resolveSafely(baseDir, userInput)` | Path-traversal guard: `resolve` + `normalize` + `startsWith` on `Path` |
| 3 | `writeLines(file, lines)` | `createDirectories` + `Files.write` with explicit UTF-8 |
| 4 | `readNonBlankLines(file)` | Read text, handle `NoSuchFileException`, close streams |
| 5 | `replaceAtomically(target, content)` | Temp file in same dir + `ATOMIC_MOVE`, cleanup on failure |

**`DirectoryStreamTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `listFileNames(dir, glob)` | `DirectoryStream` with glob, files only, sorted |
| 2 | `findByExtension(root, ext)` | `Files.walk` + `relativize`, case-insensitive match |
| 3 | `sizeByExtension(root)` | `walkFileTree` + attributes + `Map.merge` |
| 4 | `copyDirectory(source, target)` | Recursive copy with a `FileVisitor`, merge semantics |
| 5 | `listFilesSkipping(root, skipDirName)` | `SKIP_SUBTREE` pruning |

**`FileAttributesTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `kind(path)` | Non-throwing `Files.isRegularFile/isDirectory` checks |
| 2 | `isNewerThan(a, b)` | `FileTime` comparison |
| 3 | `touch(file, when)` | Create-if-missing + `setLastModifiedTime` |
| 4 | `newestFile(dir)` | Directory listing + `Comparator` over `FileTime`, `Optional` |
| 5 | `namesLargerThan(root, minBytes)` | `Files.find` with `BasicFileAttributes` |
| 6 | `permissionsToOctal(rwx)` | `PosixFilePermissions.fromString` → bit math |

**`WatchServiceTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `describeKind(kind)` | `StandardWatchEventKinds` |
| 2 | `waitForCreated(dir, trigger, n, timeout)` | Register first, deadline-based `poll(timeout)`, `reset` |
| 3 | `waitForDeletion(dir, name, trigger, timeout)` | Same loop, different kind and stop condition |
| 4 | `registerAll(watchService, root)` | Non-recursive watching → register each directory via `walkFileTree` |

```bash
mvn test -Dtest=PathsAndFilesTasksTest
mvn test -Dtest=DirectoryStreamTasksTest
mvn test -Dtest=FileAttributesTasksTest
mvn test -Dtest=WatchServiceTasksTest
```

All 20 task groups must go **GREEN** ✅ — remove `@Disabled` from one `@Nested` class at a time.

> `WatchService` tests wait for real OS events. On Linux/Windows they return instantly; on macOS the JDK polls, so they can take up to ~10 s.

## Key things to remember

```java
// Path is just a NAME — it never touches the disk, and startsWith works on name ELEMENTS
Paths.get("projects/java").startsWith("proj");   // ❌ false — "proj" is not a whole element
Paths.get("projects/java").startsWith("projects"); // ✅ true

// Path traversal: resolve() happily escapes the base. normalize FIRST, then compare Paths
Path p = base.resolve(userInput).normalize();
if (!p.startsWith(base)) throw new IllegalArgumentException();  // ✅
// ❌ p.toString().startsWith(base.toString())  — accepts "/srv/uploads-evil"

// resolve(absolute) REPLACES the base; relativize refuses to mix absolute + relative
Paths.get("a").resolve("/etc");                  // → /etc

// Files.lines / list / walk / find hold an open handle — ALWAYS try-with-resources
try (Stream<String> lines = Files.lines(file, StandardCharsets.UTF_8)) { ... }  // ✅
Files.lines(file).count();                       // ❌ leaks a file descriptor

// Always pass a charset — never rely on the platform default
Files.readAllLines(file, StandardCharsets.UTF_8);

// copy/move are strict by default: they THROW instead of overwriting
Files.copy(src, dst);                                         // FileAlreadyExistsException if dst exists
Files.copy(src, dst, StandardCopyOption.REPLACE_EXISTING);    // ✅ explicit
// Files.copy of a directory is SHALLOW — contents are not copied (use walkFileTree)

// Atomic replace: temp file in the SAME directory, then ATOMIC_MOVE (same file store only)
Files.move(tmp, target, StandardCopyOption.ATOMIC_MOVE);

// Files.walk(dir) includes dir itself as the first element
// Directory iteration order is UNSPECIFIED — sort before asserting or printing

// walkFileTree: delete a directory in postVisitDirectory (after its children), prune with SKIP_SUBTREE

// Glob: '*' never crosses a '/'. Match the file NAME, or use '**/'
PathMatcher m = FileSystems.getDefault().getPathMatcher("glob:*.java");
m.matches(Paths.get("src/Main.java"));                // ❌ false
m.matches(Paths.get("src/Main.java").getFileName());  // ✅ true

// Not every file system has every attribute view — only "basic" is guaranteed
if (FileSystems.getDefault().supportedFileAttributeViews().contains("posix")) { ... }
// Files.exists() is false BOTH for "no" and for "could not check" — readAttributes tells you why

// Symlinks are followed by default; a dangling link "does not exist" unless NOFOLLOW_LINKS
Files.exists(link);                                   // false for a dangling link
Files.exists(link, LinkOption.NOFOLLOW_LINKS);        // ✅ true — the link itself is there

// WatchService: register BEFORE causing the change, and ALWAYS reset() the key
WatchKey key = watcher.poll(10, TimeUnit.SECONDS);
for (WatchEvent<?> e : key.pollEvents()) { Path name = (Path) e.context(); }  // relative name!
if (!key.reset()) { /* directory gone or key cancelled — stop watching it */ }  // ❌ forget → watcher goes deaf
// Watching is NOT recursive — register every sub-directory yourself
// One write can produce several ENTRY_MODIFY events; OVERFLOW means "events lost — re-scan"
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=PathsAndFilesDemoTest
mvn test -Dtest=DirectoryStreamDemoTest
mvn test -Dtest=FileAttributesDemoTest
mvn test -Dtest=WatchServiceDemoTest

# Task tests only (all four pairs)
mvn test -Dtest='*TasksTest'

# Run main demos
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_nio.PathsAndFilesDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_nio.DirectoryStreamDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_nio.FileAttributesDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.file_io_nio.WatchServiceDemo"
```
