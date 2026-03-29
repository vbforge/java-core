package com.vbforge;

import java.util.List;

/**
 * StringsTasks — implement every method below to make {@code StringsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code StringsDemo.java} to understand the relevant concept.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=StringsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 */
public class StringsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Reverses the words in a sentence while preserving a single space between them.
     *
     * <p>Examples:
     * <pre>
     *   reverseWords("Hello World")          → "World Hello"
     *   reverseWords("  Java is   great  ")  → "great is Java"
     *   reverseWords("single")               → "single"
     *   reverseWords("")                     → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code trim()} or {@code strip()} to remove leading/trailing whitespace.</li>
     *   <li>Use {@code split("\\s+")} to split on any whitespace run.</li>
     *   <li>Walk the resulting array in reverse, assembling with {@link StringBuilder}.</li>
     * </ul>
     *
     * @param sentence the input sentence (may have extra whitespace)
     * @return the sentence with word order reversed, single-space-separated
     */
    public String reverseWords(String sentence) {
        throw new UnsupportedOperationException("TODO: implement reverseWords");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if {@code s} is a palindrome (reads the same forwards
     * and backwards), ignoring case and all non-alphanumeric characters.
     *
     * <p>Examples:
     * <pre>
     *   isPalindrome("racecar")          → true
     *   isPalindrome("A man a plan a canal Panama") → true
     *   isPalindrome("hello")            → false
     *   isPalindrome("")                 → true
     *   isPalindrome("a")                → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code replaceAll("[^a-zA-Z0-9]", "")} to strip non-alphanumeric chars.</li>
     *   <li>Lower-case the cleaned string.</li>
     *   <li>Compare it to its {@link StringBuilder#reverse()} result.</li>
     * </ul>
     *
     * @param s the input string
     * @return {@code true} if {@code s} is a palindrome by the above rules
     */
    public boolean isPalindrome(String s) {
        throw new UnsupportedOperationException("TODO: implement isPalindrome");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Counts how many times {@code word} appears in {@code text} as a whole word
     * (case-insensitive, surrounded by non-word characters or string boundaries).
     *
     * <p>Examples:
     * <pre>
     *   countWordOccurrences("the cat sat on the mat", "the") → 2
     *   countWordOccurrences("catalog the cat", "cat")        → 2  (catalog does NOT count)
     *   countWordOccurrences("Hello hello HELLO", "hello")    → 3
     *   countWordOccurrences("no match here", "xyz")          → 0
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build a {@link java.util.regex.Pattern} with {@code \\b} word boundaries
     *       and {@link java.util.regex.Pattern#CASE_INSENSITIVE}.</li>
     *   <li>Use a {@link java.util.regex.Matcher} and count {@code find()} calls.</li>
     * </ul>
     *
     * @param text the haystack
     * @param word the word to search for (treated as a literal word)
     * @return number of whole-word, case-insensitive occurrences
     */
    public int countWordOccurrences(String text, String word) {
        throw new UnsupportedOperationException("TODO: implement countWordOccurrences");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Converts a {@code camelCase} or {@code PascalCase} string to
     * {@code snake_case} (all lowercase, words separated by underscores).
     *
     * <p>Examples:
     * <pre>
     *   toSnakeCase("helloWorld")       → "hello_world"
     *   toSnakeCase("MyClassName")      → "my_class_name"
     *   toSnakeCase("parseHTTPResponse") → "parse_h_t_t_p_response"
     *   toSnakeCase("alreadylower")     → "alreadylower"
     *   toSnakeCase("A")               → "a"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code replaceAll("([A-Z])", "_$1")} to insert underscores before
     *       each uppercase letter.</li>
     *   <li>Then {@code toLowerCase()} and strip any leading underscore if the
     *       original string started with a capital.</li>
     * </ul>
     *
     * @param camel the camelCase or PascalCase input
     * @return the snake_case equivalent
     */
    public String toSnakeCase(String camel) {
        throw new UnsupportedOperationException("TODO: implement toSnakeCase");
    }

    // ── Task 5 ─────────────────────────────────────────────────────────────────

    /**
     * Compresses consecutive repeated characters using run-length encoding.
     * If the compressed form is not shorter than the original, return the original.
     *
     * <p>Examples:
     * <pre>
     *   compress("aabcccdddd")   → "a2bc3d4"
     *   compress("abcd")         → "abcd"      (compressed "a1b1c1d1" is longer)
     *   compress("aabb")         → "aabb"      (compressed "a2b2" is the same length — return original)
     *   compress("aaaa")         → "a4"        (compressed is shorter)
     *   compress("")             → ""
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Walk the string with a {@code for} loop, tracking the current character
     *       and a run counter.</li>
     *   <li>Use {@link StringBuilder} to build the compressed form.</li>
     *   <li>Append the character; only append the count if it is greater than 1.</li>
     *   <li>Compare lengths at the end and return the shorter one.</li>
     * </ul>
     *
     * @param s the input string (only ASCII letters, non-empty or empty)
     * @return RLE-compressed form if strictly shorter, otherwise original
     */
    public String compress(String s) {
        throw new UnsupportedOperationException("TODO: implement compress");
    }

    // ── Task 6 ─────────────────────────────────────────────────────────────────

    /**
     * Extracts all valid email addresses from a free-form text string and returns
     * them as a {@link List} in the order they appear. Duplicates are preserved.
     *
     * <p>A "valid email" for this task matches the pattern:
     * <pre>
     *   [a-zA-Z0-9._%+\-]+  @  [a-zA-Z0-9.\-]+  \.  [a-zA-Z]{2,}
     * </pre>
     *
     * <p>Examples:
     * <pre>
     *   extractEmails("Contact alice@example.com or bob@test.org for help")
     *       → ["alice@example.com", "bob@test.org"]
     *
     *   extractEmails("No emails here!")
     *       → []
     *
     *   extractEmails("Send to user.name+tag@sub.domain.io and admin@site.co")
     *       → ["user.name+tag@sub.domain.io", "admin@site.co"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Compile a {@link java.util.regex.Pattern} with the email pattern above.</li>
     *   <li>Use {@link java.util.regex.Matcher#find()} in a loop, collecting
     *       each {@link java.util.regex.Matcher#group()} into a {@link java.util.ArrayList}.</li>
     * </ul>
     *
     * @param text free-form input that may contain zero or more email addresses
     * @return a list of extracted email addresses, in appearance order
     */
    public List<String> extractEmails(String text) {
        throw new UnsupportedOperationException("TODO: implement extractEmails");
    }

    // ── Task 7 ─────────────────────────────────────────────────────────────────

    /**
     * Formats a list of strings as a neatly aligned two-column table.
     * The left column holds 1-based index labels ({@code "1."}, {@code "2."}, …),
     * the right column holds each item left-aligned within a field of
     * {@code columnWidth} characters.
     *
     * <p>The separator between the index and the item is a single space.
     * Each row ends with a newline {@code '\n'}.
     *
     * <p>Example — {@code columnWidth = 10}, items = ["apple", "banana", "kiwi"]:
     * <pre>
     * 1. apple
     * 2. banana
     * 3. kiwi
     * </pre>
     * (Each item padded/truncated to 10 characters in its column.)
     *
     * <p>Hints:
     * <ul>
     *   <li>Use {@code String.format("%-" + columnWidth + "s", item)} to left-align
     *       each item in a field of exactly {@code columnWidth} characters.</li>
     *   <li>Use {@link StringBuilder} to assemble all rows.</li>
     *   <li>Use {@code String.format("%d. ", i)} for the index prefix.</li>
     * </ul>
     *
     * @param items       the list of strings to display
     * @param columnWidth the fixed width of the item column
     * @return the formatted table as a single string with trailing newline
     */
    public String formatTable(List<String> items, int columnWidth) {
        throw new UnsupportedOperationException("TODO: implement formatTable");
    }
}