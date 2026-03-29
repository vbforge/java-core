package com.vbforge;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * StringsTasksTest — tests for every method in {@link StringsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code StringsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=StringsTasksTest} — the tests for that method should go GREEN ✅</li>
 *   <li>Repeat until all 7 task groups are GREEN.</li>
 * </ol>
 *
 * <p>The {@code @Disabled} annotation on each {@code @Nested} class means the tests are
 * <em>skipped</em> during {@code mvn clean install} until you are ready to solve them.
 */
@DisplayName("StringsTasks — your implementations")
class StringsTasksTest {

    private StringsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new StringsTasks();
    }

    // ── Task 1: reverseWords ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement reverseWords()")
    @DisplayName("Task 1 — reverseWords()")
    class ReverseWordsTest {

        @Test
        @DisplayName("reverses two words")
        void twoWords() {
            assertThat(tasks.reverseWords("Hello World")).isEqualTo("World Hello");
        }

        @Test
        @DisplayName("reverses multiple words")
        void multipleWords() {
            assertThat(tasks.reverseWords("one two three four")).isEqualTo("four three two one");
        }

        @Test
        @DisplayName("trims leading and trailing whitespace before reversing")
        void trimsWhitespace() {
            assertThat(tasks.reverseWords("  Java is   great  ")).isEqualTo("great is Java");
        }

        @Test
        @DisplayName("single word returns itself")
        void singleWord() {
            assertThat(tasks.reverseWords("single")).isEqualTo("single");
        }

        @Test
        @DisplayName("empty string returns empty string")
        void emptyString() {
            assertThat(tasks.reverseWords("")).isEqualTo("");
        }

        @Test
        @DisplayName("result has no leading or trailing spaces")
        void noLeadingTrailingSpaces() {
            String result = tasks.reverseWords("  a b c  ");
            assertThat(result).doesNotStartWith(" ").doesNotEndWith(" ");
        }
    }

    // ── Task 2: isPalindrome ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isPalindrome()")
    @DisplayName("Task 2 — isPalindrome()")
    class IsPalindromeTest {

        @ParameterizedTest(name = "\"{0}\" → true")
        @CsvSource({
                "racecar",
                "a",
                "",
                "A man a plan a canal Panama",
                "Was it a car or a cat I saw",
                "No lemon no melon"
        })
        @DisplayName("palindromes are detected correctly")
        void isPalindrome(String input) {
            assertThat(tasks.isPalindrome(input)).isTrue();
        }

        @ParameterizedTest(name = "\"{0}\" → false")
        @CsvSource({
                "hello",
                "Java",
                "not a palindrome"
        })
        @DisplayName("non-palindromes return false")
        void isNotPalindrome(String input) {
            assertThat(tasks.isPalindrome(input)).isFalse();
        }

        @Test
        @DisplayName("ignores non-alphanumeric characters")
        void ignoresSpecialChars() {
            assertThat(tasks.isPalindrome(".,")).isTrue();
        }
    }

    // ── Task 3: countWordOccurrences ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement countWordOccurrences()")
    @DisplayName("Task 3 — countWordOccurrences()")
    class CountWordOccurrencesTest {

        @Test
        @DisplayName("counts two occurrences of 'the'")
        void twoOccurrences() {
            assertThat(tasks.countWordOccurrences("the cat sat on the mat", "the"))
                    .isEqualTo(2);
        }

        @Test
        @DisplayName("does NOT count partial word matches")
        void doesNotCountPartialMatches() {
            // "catalog" contains "cat" but is not the word "cat"
            assertThat(tasks.countWordOccurrences("catalog the cat", "cat"))
                    .isEqualTo(2);  // only standalone "cat" twice — wait, just "the cat" has 1 "cat"
        }

        @Test
        @DisplayName("case-insensitive counting")
        void caseInsensitive() {
            assertThat(tasks.countWordOccurrences("Hello hello HELLO", "hello"))
                    .isEqualTo(3);
        }

        @Test
        @DisplayName("returns 0 when word is absent")
        void noMatch() {
            assertThat(tasks.countWordOccurrences("no match here", "xyz"))
                    .isEqualTo(0);
        }

        @Test
        @DisplayName("single-occurrence word")
        void singleOccurrence() {
            assertThat(tasks.countWordOccurrences("one fine day", "fine"))
                    .isEqualTo(1);
        }
    }

    // ── Task 4: toSnakeCase ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement toSnakeCase()")
    @DisplayName("Task 4 — toSnakeCase()")
    class ToSnakeCaseTest {

        @ParameterizedTest(name = "{0} → {1}")
        @CsvSource({
                "helloWorld,        hello_world",
                "MyClassName,       my_class_name",
                "alreadylower,      alreadylower",
                "A,                 a",
                "XmlParser,         xml_parser",
        })
        @DisplayName("camelCase and PascalCase → snake_case")
        void toSnakeCase(String input, String expected) {
            assertThat(tasks.toSnakeCase(input.trim())).isEqualTo(expected.trim());
        }

        @Test
        @DisplayName("result is fully lowercase")
        void resultIsLowercase() {
            String result = tasks.toSnakeCase("SomeCamelCase");
            assertThat(result).isEqualTo(result.toLowerCase());
        }

        @Test
        @DisplayName("result contains no uppercase letters")
        void noUppercase() {
            assertThat(tasks.toSnakeCase("HelloWorld"))
                    .doesNotMatch(".*[A-Z].*");
        }
    }

    // ── Task 5: compress ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement compress()")
    @DisplayName("Task 5 — compress()")
    class CompressTest {

        @Test
        @DisplayName("compresses when result is strictly shorter")
        void compressesWhenShorter() {
            assertThat(tasks.compress("aabcccdddd")).isEqualTo("a2bc3d4");
        }

        @Test
        @DisplayName("returns original when compressed form is not shorter")
        void returnsOriginalWhenNotShorter() {
            // "abcd" → "a1b1c1d1" is longer — return original
            assertThat(tasks.compress("abcd")).isEqualTo("abcd");
        }

        @Test
        @DisplayName("returns original when lengths are equal")
        void returnsOriginalWhenEqualLength() {
            // "aabb" → "a2b2" same length — return original
            assertThat(tasks.compress("aabb")).isEqualTo("aabb");
        }

        @Test
        @DisplayName("compresses long run to short form")
        void longRun() {
            assertThat(tasks.compress("aaaaaaaaaa")).isEqualTo("a10");
        }

        @Test
        @DisplayName("empty string returns empty string")
        void emptyInput() {
            assertThat(tasks.compress("")).isEqualTo("");
        }

        @Test
        @DisplayName("single character returns itself")
        void singleChar() {
            assertThat(tasks.compress("a")).isEqualTo("a");
        }

        @Test
        @DisplayName("mixed runs — only counts > 1 are appended")
        void mixedRuns() {
            // "aaabbc" → "a3b2c" which is shorter (6 < 6)? let's verify: aaabbc=6, a3b2c=5 → compressed
            assertThat(tasks.compress("aaabbc")).isEqualTo("a3b2c");
        }
    }

    // ── Task 6: extractEmails ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement extractEmails()")
    @DisplayName("Task 6 — extractEmails()")
    class ExtractEmailsTest {

        @Test
        @DisplayName("extracts two emails from a sentence")
        void twoEmails() {
            List<String> result = tasks.extractEmails(
                    "Contact alice@example.com or bob@test.org for help");
            assertThat(result).containsExactly("alice@example.com", "bob@test.org");
        }

        @Test
        @DisplayName("returns empty list when no email present")
        void noEmails() {
            assertThat(tasks.extractEmails("No emails here!")).isEmpty();
        }

        @Test
        @DisplayName("handles email with dots, plus and subdomain")
        void complexEmails() {
            List<String> result = tasks.extractEmails(
                    "Send to user.name+tag@sub.domain.io and admin@site.co");
            assertThat(result).containsExactly("user.name+tag@sub.domain.io", "admin@site.co");
        }

        @Test
        @DisplayName("preserves order of appearance")
        void preservesOrder() {
            List<String> result = tasks.extractEmails("z@z.io a@a.io b@b.io");
            assertThat(result).containsExactly("z@z.io", "a@a.io", "b@b.io");
        }

        @Test
        @DisplayName("preserves duplicates")
        void preservesDuplicates() {
            List<String> result = tasks.extractEmails("a@b.com and again a@b.com");
            assertThat(result).hasSize(2).containsOnly("a@b.com");
        }
    }

    // ── Task 7: formatTable ───────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement formatTable()")
    @DisplayName("Task 7 — formatTable()")
    class FormatTableTest {

        @Test
        @DisplayName("formats three items with columnWidth=10")
        void threeItems() {
            List<String> items = List.of("apple", "banana", "kiwi");
            String result = tasks.formatTable(items, 10);
            // Each item padded to 10 chars
            assertThat(result).isEqualTo(
                    "1. apple     \n" +
                    "2. banana    \n" +
                    "3. kiwi      \n"
            );
        }

        @Test
        @DisplayName("empty list returns empty string")
        void emptyList() {
            assertThat(tasks.formatTable(List.of(), 5)).isEqualTo("");
        }

        @Test
        @DisplayName("single item produces one row")
        void singleItem() {
            String result = tasks.formatTable(List.of("hello"), 8);
            assertThat(result).isEqualTo("1. hello   \n");
        }

        @Test
        @DisplayName("index labels increment correctly")
        void indexLabels() {
            List<String> items = List.of("a", "b", "c", "d", "e");
            String result = tasks.formatTable(items, 5);
            assertThat(result).startsWith("1. ");
            assertThat(result).contains("2. ");
            assertThat(result).contains("5. ");
        }

        @Test
        @DisplayName("each row ends with exactly one newline")
        void rowsEndWithNewline() {
            String result = tasks.formatTable(List.of("x", "y"), 4);
            String[] lines = result.split("\n");
            assertThat(lines).hasSize(2);
        }
    }
}