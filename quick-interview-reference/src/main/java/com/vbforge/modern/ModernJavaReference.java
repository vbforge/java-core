package com.vbforge.modern;

import java.net.URI;
import java.net.http.*;
import java.time.Duration;

/**
 * ═══════════════════════════════════════════════════════════════
 *  MODERN JAVA REFERENCE (Java 9–21)
 *  Topics: text blocks · records · sealed classes ·
 *          pattern matching · switch expressions ·
 *          HTTP Client · useful small features
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class ModernJavaReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. TEXT BLOCKS (Java 15+) ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Text blocks use triple quotes — preserve line breaks, handle indentation.
     *  Indentation is STRIPPED relative to the closing """ position.
     *  Trailing whitespace on each line is stripped by default.
     */
    static void textBlocks() {
        // ✅ Text block — readable multiline string
        String json = """
                {
                    "name": "Alice",
                    "age": 30
                }
                """;
        // Note: leading spaces stripped to column of closing """
        // The closing """ on its own line = trailing newline included

        // ⚠️ No trailing newline — put closing """ on last content line
        String noTrailing = """
                hello\
                world""";  // backslash \ at end of line = line continuation (no newline)

        // ⚠️ Indentation gotcha — aligning closing """ matters
        String a = """
            hello
            """;          // 0 leading spaces (closing """ at col 12 — stripped)

        // String.formatted() works nicely with text blocks
        String template = """
                Hello, %s!
                You have %d messages.
                """.formatted("Alice", 5);
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. SWITCH EXPRESSIONS (Java 14+) ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Arrow syntax:   case X -> expression  (no fall-through, no break needed)
     *  yield keyword:  used to return value from a block case
     *
     *  ⚠️ Switch EXPRESSION must be EXHAUSTIVE — compiler enforces it for enums/sealed.
     *  ⚠️ yield is only valid inside a switch EXPRESSION block — not switch statement.
     */
    enum Day { MON, TUE, WED, THU, FRI, SAT, SUN }

    static void switchExpressions() {
        Day day = Day.MON;

        // Arrow syntax — no fall-through, returns value
        String type = switch (day) {
            case MON, TUE, WED, THU, FRI -> "weekday";
            case SAT, SUN -> "weekend";
        };

        // yield for multi-statement blocks
        int score = switch (day) {
            case MON -> 1;
            case FRI -> {
                System.out.println("almost weekend!");
                yield 5;   // yield = return from switch block
            }
            default -> 3;
        };

        // ⚠️ Classic switch vs switch expression
        // Classic: fall-through, side-effects, statement
        // Expression: no fall-through, returns value, exhaustiveness checked
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. PATTERN MATCHING (Java 16-21+)
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void patternMatching() {
        Object obj = "Hello, Java!";

        // instanceof pattern matching (Java 16+) — no explicit cast needed
        if (obj instanceof String s) {
            System.out.println(s.length());   // 's' is scoped to if block
        }

        // ⚠️ Pattern variable is NOT available in else
        if (!(obj instanceof String s)) {
            // s NOT accessible here
        } else {
            System.out.println(s.toUpperCase());  // s accessible here
        }

        // Switch pattern matching with guards (Java 21)
        Object value = 42;
        String description = switch (value) {
            case Integer i when i < 0   -> "negative int: " + i;
            case Integer i when i == 0  -> "zero";
            case Integer i              -> "positive int: " + i;
            case String s when s.isEmpty() -> "empty string";
            case String s               -> "string: " + s;
            case null                   -> "null";
            default                     -> "other";
        };

        // Guarded patterns replace complex if-else chains cleanly
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. HTTP CLIENT (Java 11+) — sync & async
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Replaces the old HttpURLConnection — cleaner API, HTTP/2, WebSocket support.
     *  HttpClient is thread-safe and designed to be reused.
     */
    static void httpClientExamples() throws Exception {
        // Build a reusable client
        HttpClient client = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

        // Build a request
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/data"))
            .header("Accept", "application/json")
            .GET()
            .timeout(Duration.ofSeconds(30))
            .build();

        // Synchronous send
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        System.out.println(response.statusCode());  // 200
        System.out.println(response.body());

        // Asynchronous send — returns CompletableFuture
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenApply(HttpResponse::body)
            .thenAccept(System.out::println)
            .exceptionally(e -> { System.err.println("Error: " + e); return null; });

        // POST with body
        HttpRequest post = HttpRequest.newBuilder()
            .uri(URI.create("https://api.example.com/items"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString("{\"name\":\"test\"}"))
            .build();
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 5. SMALL BUT USEFUL FEATURES
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void smallFeatures() {
        // String methods (Java 11+)
        "  hello  ".strip();          // like trim() but Unicode-aware
        "  ".isBlank();               // true — whitespace only
        "a\nb\nc".lines().toList();   // ["a", "b", "c"]
        "ab".repeat(3);               // "ababab"
        "hello".indent(4);            // adds 4 spaces indentation per line

        // Collection factory methods (Java 9+) — IMMUTABLE
        var list = java.util.List.of(1, 2, 3);
        var set  = java.util.Set.of("a", "b");
        var map  = java.util.Map.of("k1", 1, "k2", 2);
        var map2 = java.util.Map.ofEntries(          // for >10 entries
            java.util.Map.entry("k1", 1),
            java.util.Map.entry("k2", 2)
        );

        // Stream.toList() (Java 16+) — unmodifiable, shorter than collect(Collectors.toList())
        var names = java.util.stream.Stream.of("a","b","c").toList();

        // instanceof without variable (still useful)
        Object o = "test";
        if (o instanceof String) { /* just type check, no binding variable */ }

        // Enhanced NullPointerException messages (Java 14+)
        // Before: NullPointerException (no message)
        // After:  Cannot invoke "String.length()" because "str" is null

        // Records as local classes (Java 16+)
        record Point(int x, int y) {}
        var p = new Point(1, 2);

        // Sealed interfaces summary — see OopReference for full details
        // var shape = new Circle(5.0);  // compiler knows all subtypes
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ Using HttpURLConnection instead of HttpClient  → legacy, verbose
     *  ❌ Mutating List.of() / Map.of() collections      → UnsupportedOperationException
     *  ❌ yield inside switch STATEMENT                   → syntax error
     *  ❌ Non-exhaustive switch expression                → compile error for enums/sealed
     *  ❌ Creating new HttpClient per request             → use shared instance
     */

    public static void main(String[] args) throws Exception {
        textBlocks();
        switchExpressions();
        patternMatching();
        smallFeatures();
    }
}
