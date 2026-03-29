package com.vbforge;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StringsDemoTest — verifies every concept shown in {@link StringsDemo}.
 *
 * <p>These tests are always GREEN. They document the expected behaviour of
 * Java's String API so you can read them as executable specification.
 */
@DisplayName("StringsDemo — API contracts")
class StringsDemoTest {

    // ── 1. String pool ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("1. String pool and immutability")
    class StringPoolTest {

        @Test
        @DisplayName("literals with same content are == (interned)")
        void literalsSameReference() {
            String a = "hello";
            String b = "hello";
            assertThat(a).isSameAs(b);
        }

        @Test
        @DisplayName("new String() bypasses pool — not ==")
        void newStringNotSameRef() {
            String a = "hello";
            String c = new String("hello");
            assertThat(a).isNotSameAs(c);
            assertThat(a).isEqualTo(c);  // same content
        }

        @Test
        @DisplayName("intern() brings heap string back to pool")
        void internRestoresPoolReference() {
            String a = "hello";
            String c = new String("hello").intern();
            assertThat(a).isSameAs(c);
        }

        @Test
        @DisplayName("toUpperCase() returns new String — original unchanged")
        void immutabilityPreservesOriginal() {
            String original = "Java";
            String modified = original.toUpperCase();
            assertThat(original).isEqualTo("Java");
            assertThat(modified).isEqualTo("JAVA");
        }
    }

    // ── 2. Common methods ─────────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Common String methods")
    class CommonMethodsTest {

        @Test
        @DisplayName("length() and charAt()")
        void lengthAndCharAt() {
            String s = "Hello";
            assertThat(s.length()).isEqualTo(5);
            assertThat(s.charAt(1)).isEqualTo('e');
        }

        @Test
        @DisplayName("trim() strips ASCII whitespace; strip() strips Unicode whitespace")
        void trimVsStrip() {
            String s = "  hello  ";
            assertThat(s.trim()).isEqualTo("hello");
            assertThat(s.strip()).isEqualTo("hello");
        }

        @Test
        @DisplayName("contains(), indexOf(), lastIndexOf()")
        void searchMethods() {
            String s = "the cat sat on the mat";
            assertThat(s.contains("sat")).isTrue();
            assertThat(s.indexOf("at")).isEqualTo(5);
            assertThat(s.lastIndexOf("at")).isEqualTo(20);
        }

        @Test
        @DisplayName("startsWith() and endsWith()")
        void startsEnds() {
            assertThat("Hello World".startsWith("Hello")).isTrue();
            assertThat("Hello World".endsWith("World")).isTrue();
        }

        @Test
        @DisplayName("substring(start) and substring(start, end)")
        void substring() {
            String s = "Hello World";
            assertThat(s.substring(6)).isEqualTo("World");
            assertThat(s.substring(0, 5)).isEqualTo("Hello");
        }

        @Test
        @DisplayName("replace() replaces all literal occurrences")
        void replace() {
            assertThat("aabbcc".replace("b", "X")).isEqualTo("aaXXcc");
        }

        @Test
        @DisplayName("split() on delimiter — trailing empties dropped by default")
        void split() {
            String[] parts = "a,b,c".split(",");
            assertThat(parts).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("split(-1) preserves trailing empty strings")
        void splitNegativeLimit() {
            String[] parts = "a,,".split(",", -1);
            assertThat(parts).hasSize(3);
            assertThat(parts[2]).isEmpty();
        }

        @Test
        @DisplayName("isEmpty() and isBlank() (Java 11+)")
        void isEmptyIsBlank() {
            assertThat("".isEmpty()).isTrue();
            assertThat("  ".isEmpty()).isFalse();  // has characters (spaces)
            assertThat("  ".isBlank()).isTrue();   // only whitespace
            assertThat(" a".isBlank()).isFalse();
        }

        @Test
        @DisplayName("repeat() (Java 11+)")
        void repeat() {
            assertThat("ab".repeat(3)).isEqualTo("ababab");
            assertThat("x".repeat(0)).isEmpty();
        }
    }

    // ── 3. Comparison ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("3. String comparison")
    class ComparisonTest {

        @Test
        @DisplayName("equals() is case-sensitive")
        void equalsCaseSensitive() {
            assertThat("Apple".equals("apple")).isFalse();
            assertThat("Apple".equals("Apple")).isTrue();
        }

        @Test
        @DisplayName("equalsIgnoreCase() ignores case")
        void equalsIgnoreCase() {
            assertThat("Apple".equalsIgnoreCase("APPLE")).isTrue();
        }

        @Test
        @DisplayName("compareTo() returns negative/zero/positive")
        void compareTo() {
            assertThat("apple".compareTo("banana")).isNegative();
            assertThat("banana".compareTo("apple")).isPositive();
            assertThat("apple".compareTo("apple")).isZero();
        }

        @Test
        @DisplayName("Objects.equals() handles null without NPE")
        void objectsEqualsNullSafe() {
            assertThat(java.util.Objects.equals(null, "hello")).isFalse();
            assertThat(java.util.Objects.equals(null, null)).isTrue();
        }
    }

    // ── 4. StringBuilder ──────────────────────────────────────────────────────

    @Nested
    @DisplayName("4. StringBuilder")
    class StringBuilderTest {

        @Test
        @DisplayName("append() builds string in-place")
        void append() {
            StringBuilder sb = new StringBuilder();
            sb.append("Hello").append(", ").append("World");
            assertThat(sb.toString()).isEqualTo("Hello, World");
        }

        @Test
        @DisplayName("insert() places content at given index")
        void insert() {
            StringBuilder sb = new StringBuilder("Hello World");
            sb.insert(5, " Beautiful");
            assertThat(sb.toString()).isEqualTo("Hello Beautiful World");
        }

        @Test
        @DisplayName("delete() removes a range of characters")
        void delete() {
            StringBuilder sb = new StringBuilder("Hello World");
            sb.delete(5, 11);
            assertThat(sb.toString()).isEqualTo("Hello");
        }

        @Test
        @DisplayName("reverse() reverses the content in-place")
        void reverse() {
            StringBuilder sb = new StringBuilder("abcde");
            sb.reverse();
            assertThat(sb.toString()).isEqualTo("edcba");
        }

        @Test
        @DisplayName("initial capacity avoids resizing")
        void capacity() {
            StringBuilder sb = new StringBuilder(128);
            assertThat(sb.capacity()).isEqualTo(128);
            assertThat(sb.length()).isZero();
        }
    }

    // ── 5. Formatting ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("5. String.format and formatted()")
    class FormattingTest {

        @Test
        @DisplayName("%s, %d, %.2f produce expected output")
        void basicFormat() {
            String result = String.format("%-5s %04d %.1f", "Hi", 7, 3.14);
            assertThat(result).isEqualTo("Hi    0007 3.1");
        }

        @Test
        @DisplayName(".formatted() (Java 15+) is shorthand for String.format")
        void formattedInstance() {
            String result = "Hello, %s! You are %d years old.".formatted("Alice", 30);
            assertThat(result).isEqualTo("Hello, Alice! You are 30 years old.");
        }

        @Test
        @DisplayName("String.valueOf(null) returns \"null\" without NPE")
        void valueOfNull() {
            Object obj = null;
            assertThat(String.valueOf(obj)).isEqualTo("null");
        }
    }

    // ── 6. Text blocks ────────────────────────────────────────────────────────

    @Nested
    @DisplayName("6. Text blocks (Java 15+)")
    class TextBlockTest {

        @Test
        @DisplayName("text block content matches expected string")
        void textBlockContent() {
            String json = """
                    {"name":"Alice"}
                    """;
            assertThat(json).isEqualTo("{\"name\":\"Alice\"}\n");
        }

        @Test
        @DisplayName("text block with formatted() for interpolation")
        void textBlockFormatted() {
            String result = """
                    Hello, %s!
                    """.formatted("World");
            assertThat(result).isEqualTo("Hello, World!\n");
        }

        @Test
        @DisplayName("indentation is stripped relative to closing delimiter")
        void indentationStripping() {
            // Closing """ at column 0 → nothing stripped
            // Closing """ indented → that much indentation stripped from every line
            String block = """
                    line one
                    line two
                    """;
            assertThat(block).isEqualTo("line one\nline two\n");
        }
    }

    // ── 7. Conversion ─────────────────────────────────────────────────────────

    @Nested
    @DisplayName("7. Conversion and parsing")
    class ConversionTest {

        @Test
        @DisplayName("Integer.parseInt converts decimal string to int")
        void parseInt() {
            assertThat(Integer.parseInt("42")).isEqualTo(42);
            assertThat(Integer.parseInt("-7")).isEqualTo(-7);
        }

        @Test
        @DisplayName("parseInt with radix: hex and binary")
        void parseIntRadix() {
            assertThat(Integer.parseInt("FF", 16)).isEqualTo(255);
            assertThat(Integer.parseInt("1010", 2)).isEqualTo(10);
        }

        @Test
        @DisplayName("Double.parseDouble parses floating-point strings")
        void parseDouble() {
            assertThat(Double.parseDouble("3.14")).isEqualTo(3.14);
        }

        @Test
        @DisplayName("Boolean.parseBoolean is case-insensitive for \"true\"")
        void parseBoolean() {
            assertThat(Boolean.parseBoolean("true")).isTrue();
            assertThat(Boolean.parseBoolean("TRUE")).isTrue();
            assertThat(Boolean.parseBoolean("yes")).isFalse();   // only "true" returns true
        }

        @Test
        @DisplayName("Integer.toHexString / toBinaryString produce correct representations")
        void toStringRadix() {
            assertThat(Integer.toHexString(255)).isEqualTo("ff");
            assertThat(Integer.toBinaryString(10)).isEqualTo("1010");
        }

        @Test
        @DisplayName("NumberFormatException thrown on invalid numeric string")
        void numberFormatException() {
            org.junit.jupiter.api.Assertions.assertThrows(
                    NumberFormatException.class,
                    () -> Integer.parseInt("abc")
            );
        }
    }

    // ── 8. Regex ──────────────────────────────────────────────────────────────

    @Nested
    @DisplayName("8. Regex with Pattern and Matcher")
    class RegexTest {

        @Test
        @DisplayName("matches() tests the whole string against the pattern")
        void matches() {
            assertThat("hello123".matches("[a-z]+\\d+")).isTrue();
            assertThat("hello".matches("[a-z]+\\d+")).isFalse();
        }

        @Test
        @DisplayName("replaceAll() replaces all regex matches")
        void replaceAll() {
            assertThat("a1b2c3".replaceAll("\\d", "#")).isEqualTo("a#b#c#");
        }

        @Test
        @DisplayName("Pattern.compile + Matcher.find iterates over matches")
        void findAllMatches() {
            Pattern p = Pattern.compile("\\d+");
            java.util.regex.Matcher m = p.matcher("a1b22c333");
            java.util.List<String> found = new java.util.ArrayList<>();
            while (m.find()) found.add(m.group());
            assertThat(found).containsExactly("1", "22", "333");
        }

        @Test
        @DisplayName("Capturing groups are accessible by index")
        void capturingGroups() {
            Pattern p = Pattern.compile("(\\d{4})-(\\d{2})-(\\d{2})");
            java.util.regex.Matcher m = p.matcher("2024-07-15");
            assertThat(m.matches()).isTrue();
            assertThat(m.group(1)).isEqualTo("2024");
            assertThat(m.group(2)).isEqualTo("07");
            assertThat(m.group(3)).isEqualTo("15");
        }

        @Test
        @DisplayName("Named groups are accessible by name")
        void namedGroups() {
            Pattern p = Pattern.compile("(?<year>\\d{4})-(?<month>\\d{2})-(?<day>\\d{2})");
            java.util.regex.Matcher m = p.matcher("2024-07-15");
            assertThat(m.matches()).isTrue();
            assertThat(m.group("year")).isEqualTo("2024");
            assertThat(m.group("month")).isEqualTo("07");
        }

        @Test
        @DisplayName("CASE_INSENSITIVE flag makes match case-insensitive")
        void caseInsensitiveFlag() {
            Pattern p = Pattern.compile("hello", Pattern.CASE_INSENSITIVE);
            assertThat(p.matcher("HELLO world").find()).isTrue();
        }
    }

    // ── 9. Utilities ──────────────────────────────────────────────────────────

    @Nested
    @DisplayName("9. String utilities")
    class UtilitiesTest {

        @Test
        @DisplayName("String.join concatenates with delimiter")
        void join() {
            assertThat(String.join(", ", "one", "two", "three"))
                    .isEqualTo("one, two, three");
        }

        @Test
        @DisplayName("String.join works with an Iterable")
        void joinIterable() {
            assertThat(String.join("-", java.util.List.of("a", "b", "c")))
                    .isEqualTo("a-b-c");
        }

        @Test
        @DisplayName("toCharArray() and back via new String(char[])")
        void toCharArrayRoundtrip() {
            char[] chars = "hello".toCharArray();
            chars[0] = 'H';
            assertThat(new String(chars)).isEqualTo("Hello");
        }

        @Test
        @DisplayName("chars() stream counts vowels")
        void charsStream() {
            long vowels = "Hello World".toLowerCase().chars()
                    .filter(c -> "aeiou".indexOf(c) >= 0)
                    .count();
            assertThat(vowels).isEqualTo(3L);
        }

        @Test
        @DisplayName("intern() makes independently created equal strings ==")
        void intern() {
            String s1 = new StringBuilder("shared").toString();
            String s2 = new StringBuilder("shared").toString();
            assertThat(s1).isNotSameAs(s2);
            assertThat(s1.intern()).isSameAs(s2.intern());
        }
    }
}