# strings-and-text-processing

> **Phase 1 — Basics** | Java 8–17+ | Package: `com.vbforge.basics.strings`

## What's covered

| Topic | Key points |
|---|---|
| String pool & immutability | Literals are interned, `new String()` bypasses the pool, `==` vs `.equals()` |
| Common String methods | `trim/strip`, `contains/indexOf`, `substring`, `replace`, `split`, `isBlank`, `repeat` |
| String comparison | `equals`, `equalsIgnoreCase`, `compareTo`, null-safe `Objects.equals` |
| StringBuilder | Mutable buffer — O(n) vs O(n²) for loop concatenation; `append/insert/delete/reverse` |
| String.format & formatted() | `%s %d %.2f`, padding, hex, comma-format; Java 15+ `.formatted()` shorthand |
| Text blocks (Java 15+) | Multi-line strings, automatic indentation stripping, `formatted()` interpolation |
| Conversion & parsing | `parseInt`/`parseDouble`, radix parsing, `toHexString`, `NumberFormatException` |
| Regex — Pattern & Matcher | `find()` iteration, capturing groups (indexed + named), `CASE_INSENSITIVE` flag |
| String utilities | `String.join`, `toCharArray()`, `chars()` stream, `intern()` |

> ⚠️ **Java 17 used** (text blocks are standard since Java 15; no override needed).

## Files
```
src/
├── main/java/com/vbforge/basics/strings/
│   ├── StringsDemo.java        ← theory + annotated examples
│   └── StringsTasks.java       ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/basics/strings/
    ├── StringsDemoTest.java     ← tests for the demo (already green)
    └── StringsTasksTest.java    ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Open `StringsTasks.java` and implement all 7 methods. Each has a detailed Javadoc.

| # | Method | Topic |
|---|---|---|
| 1 | `reverseWords(sentence)` | split + StringBuilder reverse traversal |
| 2 | `isPalindrome(s)` | strip non-alphanumeric, compare to reversed form |
| 3 | `countWordOccurrences(text, word)` | regex `\b` word boundaries + `CASE_INSENSITIVE` |
| 4 | `toSnakeCase(camel)` | regex replace + toLowerCase |
| 5 | `compress(s)` | run-length encoding — return shorter of original vs compressed |
| 6 | `extractEmails(text)` | email regex + Matcher.find() loop |
| 7 | `formatTable(items, width)` | `String.format("%-Ns", …)` + StringBuilder |
```bash
mvn test -Dtest=StringsTasksTest
```

All 7 task groups must go **GREEN** ✅

## Key things to remember
```java
// ALWAYS use .equals() for content comparison — NEVER ==
"hello" == "hello"          // may be true (pool) — unreliable
"hello".equals("hello")     // always true — use this

// StringBuilder for loop concatenation
StringBuilder sb = new StringBuilder();
for (String s : list) sb.append(s).append(",");

// Text block — indentation stripped relative to closing """
String json = """
        {"key": "value"}
        """;   // → "{\"key\": \"value\"}\n"

// Regex: compile once, reuse many times
Pattern p = Pattern.compile("\\d+");    // expensive — do this once
Matcher m = p.matcher(input);           // cheap — create per string
while (m.find()) System.out.println(m.group());

// split() drops trailing empty strings — use limit -1 to keep them
"a,,".split(",")       // → ["a"]       (trailing "" dropped)
"a,,".split(",", -1)   // → ["a","",""] (all kept)

// isBlank() (Java 11+) vs isEmpty()
"".isEmpty()    // true
"  ".isEmpty()  // false — has characters!
"  ".isBlank()  // true  — only whitespace
```

## Run
```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest=StringsDemoTest

# Task tests only
mvn test -Dtest=StringsTasksTest

# Run main demo
mvn exec:java -Dexec.mainClass="com.vbforge.basics.strings.StringsDemo"
```