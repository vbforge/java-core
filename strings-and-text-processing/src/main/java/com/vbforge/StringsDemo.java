package com.vbforge;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * StringsDemo — annotated reference for every string topic in this module.
 *
 * <p>Read top-to-bottom. Each section covers one concept with concrete examples
 * and a note explaining <em>why</em> the behaviour is what it is.
 *
 * <p>Topics:
 * <ol>
 *   <li>String immutability and the String pool</li>
 *   <li>Common String methods</li>
 *   <li>String comparison pitfalls</li>
 *   <li>StringBuilder — mutable string assembly</li>
 *   <li>String.format and printf</li>
 *   <li>Text blocks (Java 15+)</li>
 *   <li>String conversion and parsing</li>
 *   <li>Regex basics with Pattern / Matcher</li>
 *   <li>String.join, String.valueOf, chars()</li>
 * </ol>
 */
public class StringsDemo {

    public static void main(String[] args) {
        stringPoolDemo();
        System.out.println("---");
        commonMethodsDemo();
        System.out.println("---");
        comparisonDemo();
        System.out.println("---");
        stringBuilderDemo();
        System.out.println("---");
        formattingDemo();
        System.out.println("---");
        textBlockDemo();
        System.out.println("---");
        conversionDemo();
        System.out.println("---");
        regexDemo();
        System.out.println("---");
        utilitiesDemo();
    }

    // ── 1. String pool ────────────────────────────────────────────────────────

    /**
     * String literals are interned — the JVM stores them in a special pool and
     * reuses the same object for identical content. {@code new String()} bypasses
     * the pool, creating a heap object even when content matches a pooled literal.
     *
     * <p>WHY this matters: {@code ==} compares <em>references</em>. Two literals
     * with the same content are {@code ==} because they point to the same pool
     * object, but a {@code new String()} is never {@code ==} to anything else.
     * Always use {@code .equals()} for string content comparison.
     */
    static void stringPoolDemo() {
        System.out.println("=== String Pool ===");

        String a = "hello";          // pooled
        String b = "hello";          // same pool entry → same reference
        String c = new String("hello"); // heap object — NOT pooled

        System.out.println("a == b           : " + (a == b));        // true  — same pool entry
        System.out.println("a == c           : " + (a == c));        // false — c is on the heap
        System.out.println("a.equals(c)      : " + a.equals(c));     // true  — same content

        // intern() moves a heap string into the pool
        String d = c.intern();
        System.out.println("a == c.intern()  : " + (a == d));        // true  — now pooled

        // String is immutable — every "modification" returns a NEW object
        String original = "Java";
        String modified = original.toUpperCase();
        System.out.println("original unchanged: " + original);       // Java
        System.out.println("modified          : " + modified);       // JAVA
    }

    // ── 2. Common String methods ───────────────────────────────────────────────

    /**
     * Demonstrates the most-used String API methods. All return new Strings —
     * the original is never changed.
     */
    static void commonMethodsDemo() {
        System.out.println("=== Common Methods ===");

        String s = "  Hello, World!  ";

        // Length and character access
        System.out.println("length()      : " + s.length());        // 18
        System.out.println("charAt(2)     : " + s.charAt(2));       // H

        // Whitespace trimming
        System.out.println("trim()        : '" + s.trim() + "'");   // strips ASCII spaces
        System.out.println("strip()       : '" + s.strip() + "'");  // strips Unicode whitespace (Java 11+)

        // Case conversion
        String word = "Hello";
        System.out.println("toLowerCase() : " + word.toLowerCase());
        System.out.println("toUpperCase() : " + word.toUpperCase());

        // Searching
        String text = "the cat sat on the mat";
        System.out.println("contains()    : " + text.contains("sat"));       // true
        System.out.println("indexOf()     : " + text.indexOf("at"));         // 5 (first)
        System.out.println("lastIndexOf() : " + text.lastIndexOf("at"));     // 20 (last)
        System.out.println("startsWith()  : " + text.startsWith("the"));     // true
        System.out.println("endsWith()    : " + text.endsWith("mat"));       // true

        // Extraction
        System.out.println("substring(4)     : " + text.substring(4));       // cat sat on the mat
        System.out.println("substring(4,7)   : " + text.substring(4, 7));    // cat

        // Replacement
        System.out.println("replace()        : " + text.replace("cat", "dog"));
        System.out.println("replaceAll()     : " + text.replaceAll("\\bsat\\b", "lay"));
        System.out.println("replaceFirst()   : " + text.replaceFirst("at", "AT"));

        // Splitting
        String csv = "a,b,,c,d";
        String[] parts = csv.split(",");
        System.out.println("split() length   : " + parts.length);   // 5 — trailing empty kept
        // split with limit -1 keeps trailing empty strings too
        String[] partsKeep = csv.split(",", -1);
        System.out.println("split(-1) length : " + partsKeep.length);

        // isEmpty / isBlank (Java 11+)
        System.out.println("isEmpty()        : " + "".isEmpty());           // true
        System.out.println("isBlank()        : " + "   ".isBlank());        // true
        System.out.println("isBlank()        : " + " a ".isBlank());        // false

        // repeat (Java 11+)
        System.out.println("repeat()         : " + "ab".repeat(3));         // ababab

        // chars() stream
        long upperCount = "Hello World".chars()
                .filter(Character::isUpperCase)
                .count();
        System.out.println("uppercase chars  : " + upperCount);             // 2
    }

    // ── 3. String comparison ──────────────────────────────────────────────────

    /**
     * The single most common string bug in Java: using {@code ==} instead of
     * {@code equals()}. Learn the rule once, never forget it.
     */
    static void comparisonDemo() {
        System.out.println("=== Comparison ===");

        String x = "Apple";
        String y = "apple";

        // equals() — case-sensitive content comparison
        System.out.println("equals()            : " + x.equals(y));             // false
        System.out.println("equalsIgnoreCase()  : " + x.equalsIgnoreCase(y));  // true

        // compareTo() — lexicographic order (negative/zero/positive like Comparator)
        System.out.println("compareTo()         : " + x.compareTo(y));         // negative (A < a in ASCII)
        System.out.println("compareToIgnoreCase : " + x.compareToIgnoreCase(y)); // 0

        // Null-safe comparison — Objects.equals() handles nulls
        String nullStr = null;
        System.out.println("null.equals()  throws NPE — use Objects.equals()");
        System.out.println("Objects.equals : " + java.util.Objects.equals(nullStr, "Apple")); // false
    }

    // ── 4. StringBuilder ───────────────────────────────────────────────────────

    /**
     * {@link StringBuilder} is a mutable sequence of characters — changes happen
     * in-place without allocating new String objects.
     *
     * <p>WHY use it: concatenating strings with {@code +} inside a loop creates
     * a new String object on every iteration — O(n²) allocations. StringBuilder
     * amortises this to O(n).
     *
     * <p>Note: {@link StringBuffer} is the thread-safe (synchronized) alternative.
     * Use StringBuilder unless you specifically need cross-thread sharing.
     */
    static void stringBuilderDemo() {
        System.out.println("=== StringBuilder ===");

        // BAD — O(n²) — each + creates a new String
        String bad = "";
        for (int i = 0; i < 5; i++) {
            bad += i;   // new String on every iteration
        }

        // GOOD — O(n) — single mutable buffer
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i);
        }
        System.out.println("sb result : " + sb.toString());   // 01234

        // StringBuilder API
        StringBuilder b = new StringBuilder("Hello");
        b.append(", ").append("World").append('!');  // fluent chaining
        System.out.println("append    : " + b);       // Hello, World!

        b.insert(5, " Beautiful");
        System.out.println("insert    : " + b);       // Hello Beautiful, World!

        b.delete(5, 15);
        System.out.println("delete    : " + b);       // Hello, World!

        b.reverse();
        System.out.println("reverse   : " + b);       // !dlroW ,olleH

        b.replace(0, 6, "Goodbye");
        System.out.println("replace   : " + b);       // Goodbye,olleH

        // length vs capacity
        StringBuilder cap = new StringBuilder(64); // pre-allocate — avoids resizing
        System.out.println("capacity  : " + cap.capacity()); // 64
        System.out.println("length    : " + cap.length());   // 0
    }

    // ── 5. String.format and formatted output ─────────────────────────────────

    /**
     * {@link String#format} uses printf-style format specifiers.
     * Java 15+ adds {@link String#formatted} as an instance-method shorthand.
     */
    static void formattingDemo() {
        System.out.println("=== Formatting ===");

        // Common format specifiers
        //   %s  — string         %d  — integer       %f  — float/double
        //   %n  — newline        %b  — boolean        %c  — char
        //   %x  — hex integer    %05d — zero-padded   %-10s — left-aligned

        String name = "Alice";
        int age     = 30;
        double score = 98.567;

        System.out.println(String.format("Name: %-10s Age: %d Score: %.2f", name, age, score));
        // Name: Alice      Age: 30 Score: 98.57

        // Java 15+ instance method — identical result
        String line = "Name: %-10s Age: %d Score: %.2f".formatted(name, age, score);
        System.out.println(line);

        // Hex, padding, sign
        System.out.printf("Hex:    %08x%n", 255);      // 000000ff
        System.out.printf("Signed: %+d%n",  42);       // +42
        System.out.printf("Comma:  %,d%n",  1_000_000); // 1,000,000

        // String.valueOf — null-safe toString for any type
        //version-1
        Object obj = null;
        System.out.println(String.valueOf(obj));   // "null"  (no NPE)

        //version-2
        System.out.println(String.valueOf((Object) null));

        //version-3
        System.out.println((String) null);

        System.out.println(String.valueOf(42));     // "42"
        System.out.println(String.valueOf(true));   // "true"
    }

    // ── 6. Text blocks (Java 15+) ────────────────────────────────────────────

    /**
     * Text blocks preserve multi-line string content without manual escaping.
     * Indentation is stripped relative to the closing {@code """} delimiter.
     */
    static void textBlockDemo() {
        System.out.println("=== Text Blocks (Java 15+) ===");

        // JSON without escape hell
        String json = """
                {
                    "name": "Alice",
                    "age": 30
                }
                """;
        System.out.println("JSON text block:");
        System.out.println(json);

        // HTML template
        String html = """
                <html>
                    <body>
                        <p>Hello, World!</p>
                    </body>
                </html>
                """;
        System.out.println("HTML text block length: " + html.length());

        // Text blocks still support interpolation via formatted()
        String name = "Bob";
        String greeting = """
                Hello, %s!
                Welcome to Java text blocks.
                """.formatted(name);
        System.out.println(greeting);

        // Trailing whitespace — use \s to preserve a trailing space on a line
        // \ at end of line suppresses the newline (Java 14+ escape)
        String compact = "line one \\\nline two";
        System.out.println("backslash-newline: " + compact);
    }

    // ── 7. Conversion and parsing ─────────────────────────────────────────────

    /**
     * Converting between String and primitive/wrapper types is a daily Java task.
     * Every primitive has a {@code parseXxx(String)} method on its wrapper class.
     */
    static void conversionDemo() {
        System.out.println("=== Conversion & Parsing ===");

        // String → primitive
        int    i = Integer.parseInt("42");
        double d = Double.parseDouble("3.14");
        boolean b = Boolean.parseBoolean("true");   // case-insensitive "true"
        long   l = Long.parseLong("9876543210");

        System.out.printf("parseInt     : %d%n", i);
        System.out.printf("parseDouble  : %.2f%n", d);
        System.out.printf("parseBoolean : %b%n", b);
        System.out.printf("parseLong    : %d%n", l);

        // parseInt with radix
        int hex  = Integer.parseInt("FF", 16);    // 255
        int bin  = Integer.parseInt("1010", 2);   // 10
        System.out.println("parseInt hex  : " + hex);
        System.out.println("parseInt bin  : " + bin);

        // Primitive / object → String
        System.out.println(String.valueOf(42));       // "42"
        System.out.println(Integer.toString(42, 2)); // "101010" — binary string
        System.out.println(Integer.toHexString(255)); // "ff"
        System.out.println(Integer.toBinaryString(10)); // "1010"

        // charAt and char → int
        char ch = 'A';
        int code = ch;                           // implicit widening — gives 65
        char back = (char)(code + 1);            // explicit cast  — gives 'B'
        System.out.println("char to int : " + code);
        System.out.println("int to char : " + back);

        // NumberFormatException on bad input
        try {
            Integer.parseInt("not-a-number");
        } catch (NumberFormatException e) {
            System.out.println("NFE caught: " + e.getMessage());
        }
    }

    // ── 8. Regex with Pattern and Matcher ────────────────────────────────────

    /**
     * Java's regex engine is in {@link java.util.regex}. The two main classes:
     * <ul>
     *   <li>{@link Pattern} — compiled regex, thread-safe and reusable</li>
     *   <li>{@link Matcher} — stateful result for one match operation</li>
     * </ul>
     *
     * <p>Compile once, match many times — {@code Pattern.compile()} is expensive.
     */
    static void regexDemo() {
        System.out.println("=== Regex ===");

        // String convenience methods (fine for one-off use)
        System.out.println("matches()    : " + "hello123".matches("[a-z]+\\d+"));  // true
        System.out.println("replaceAll() : " + "a1b2c3".replaceAll("\\d", "#"));    // a#b#c#

        // Pattern + Matcher — reusable, powerful
        Pattern emailPattern = Pattern.compile(
                "^[a-zA-Z0-9._%+\\-]+@[a-zA-Z0-9.\\-]+\\.[a-zA-Z]{2,}$"
        );
        System.out.println("valid email  : " + emailPattern.matcher("alice@example.com").matches()); // true
        System.out.println("invalid email: " + emailPattern.matcher("not-an-email").matches());      // false

        // find() — iterates over all non-overlapping matches
        Pattern wordPattern = Pattern.compile("\\b\\w{4}\\b"); // 4-letter words
        Matcher m = wordPattern.matcher("the quick brown fox");
        System.out.print("4-letter words: ");
        while (m.find()) {
            System.out.print("[" + m.group() + "] ");
        }
        System.out.println();

        // Capturing groups
        Pattern datePattern = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
        Matcher dateMatcher = datePattern.matcher("Event on 2024-07-15, repeated 2025-01-01");
        while (dateMatcher.find()) {
            System.out.printf("  date=%s  year=%s  month=%s  day=%s%n",
                    dateMatcher.group(0),   // whole match
                    dateMatcher.group(1),   // group 1 — year
                    dateMatcher.group(2),   // group 2 — month
                    dateMatcher.group(3));  // group 3 — day
        }

        // Named groups — clearer than numbered
        Pattern named = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
        Matcher nm = named.matcher("2024-07-15");
        if (nm.matches()) {
            System.out.println("named year  : " + nm.group("year"));
            System.out.println("named month : " + nm.group("month"));
        }

        // split() via Pattern — same as String.split but reusable
        Pattern comma = Pattern.compile(",\\s*");
        String[] tokens = comma.split("one, two,   three,four");
        System.out.println("split count : " + tokens.length);   // 4
    }

    // ── 9. String utilities ───────────────────────────────────────────────────

    /**
     * Handy one-liners that come up constantly in real code.
     */
    static void utilitiesDemo() {
        System.out.println("=== Utilities ===");

        // String.join — joins with a delimiter, handles Iterable
        String joined = String.join(", ", "one", "two", "three");
        System.out.println("join varargs : " + joined);   // one, two, three

        List<String> list = List.of("a", "b", "c");
        System.out.println("join list    : " + String.join("-", list)); // a-b-c

        // String.valueOf — null-safe (unlike ""+null which gives "null" with ==)
        Object obj = null;
        System.out.println("valueOf null : " + String.valueOf(obj));  // "null"

        // toCharArray() — useful for character-level manipulation
        char[] chars = "hello".toCharArray();
        chars[0] = 'H';
        System.out.println("from charArr : " + new String(chars));     // Hello

        // chars() IntStream — functional character processing
        String sentence = "Hello World";
        long vowels = sentence.toLowerCase().chars()
                .filter(c -> "aeiou".indexOf(c) >= 0)
                .count();
        System.out.println("vowel count  : " + vowels);   // 3

        // intern() — deduplicate runtime strings into the pool
        String s1 = new StringBuilder("shared").toString(); // heap, not pooled
        String s2 = new StringBuilder("shared").toString(); // different heap object
        System.out.println("before intern s1==s2 : " + (s1 == s2));          // false
        System.out.println("after  intern        : " + (s1.intern() == s2.intern())); // true
    }
}