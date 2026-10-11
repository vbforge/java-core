package com.vbforge.unit_testing_junit5_assertj;

/**
 * LifecycleTasks — implement every method below to make {@code LifecycleTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code LifecycleDemo.java} and {@code LifecycleDemoTest.java} for the pattern.</li>
 *   <li>Implement a method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Remove {@code @Disabled} from the corresponding {@code @Nested} class in the test.</li>
 *   <li>Run {@code mvn test -Dtest=LifecycleTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Do not change method signatures or add external libraries — only the Java
 * standard library is needed.
 */
public class LifecycleTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns {@code true} if the given string is a palindrome.
     *
     * <p>Rules:
     * <ul>
     *   <li>Case-insensitive</li>
     *   <li>Non-letter characters (spaces, punctuation, digits) are ignored</li>
     *   <li>{@code null} or empty → {@code true} (vacuously a palindrome)</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   isPalindrome("racecar")               → true
     *   isPalindrome("A man, a plan, a canal: Panama") → true
     *   isPalindrome("hello")                 → false
     *   isPalindrome("")                      → true
     *   isPalindrome(null)                    → true
     *   isPalindrome("Was it a car or a cat I saw?") → true
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>First filter the input to letters only, lowercased — e.g.
     *       {@code s.replaceAll("[^A-Za-z]", "").toLowerCase()}.</li>
     *   <li>Then compare the filtered string with its reverse.</li>
     *   <li>{@code StringBuilder#reverse()} does the reversal cleanly.</li>
     * </ul>
     *
     * @param s the string to check (may be null)
     * @return {@code true} if {@code s} is a palindrome
     */
    public boolean isPalindrome(String s) {
        throw new UnsupportedOperationException("TODO: implement isPalindrome");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the reverse of the given string. {@code null} returns {@code null}.
     *
     * <p>Examples:
     * <pre>
     *   reverse("hello")   → "olleh"
     *   reverse("")        → ""
     *   reverse("a")       → "a"
     *   reverse(null)      → null
     *   reverse("abc cba") → "abc cba"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Guard against {@code null} first.</li>
     *   <li>{@code new StringBuilder(s).reverse().toString()} does the work.</li>
     * </ul>
     *
     * @param s the string to reverse (may be null)
     * @return the reversed string, or {@code null} if {@code s} was null
     */
    public String reverse(String s) {
        throw new UnsupportedOperationException("TODO: implement reverse");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Counts vowels ({@code a, e, i, o, u}, case-insensitive) in the string.
     * {@code null} returns {@code 0}.
     *
     * <p>Examples:
     * <pre>
     *   countVowels("hello")      → 2   (e, o)
     *   countVowels("AEIOU")      → 5
     *   countVowels("xyz")        → 0
     *   countVowels("")           → 0
     *   countVowels(null)         → 0
     *   countVowels("Hello World") → 3  (e, o, o)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Lowercase the input, then loop and check against {@code "aeiou"}.</li>
     *   <li>{@code "aeiou".indexOf(c) >= 0} is a compact check for a single char.</li>
     * </ul>
     *
     * @param s the string to scan (may be null)
     * @return number of vowels
     */
    public int countVowels(String s) {
        throw new UnsupportedOperationException("TODO: implement countVowels");
    }

    // ── Task 4 ─────────────────────────────────────────────────────────────────

    /**
     * Capitalises the first letter of each whitespace-separated word, leaving
     * the rest of each word unchanged. Preserves the original whitespace
     * structure. {@code null} or blank returns the input unchanged.
     *
     * <p>Examples:
     * <pre>
     *   capitalizeWords("hello world")   → "Hello World"
     *   capitalizeWords("java is fun")   → "Java Is Fun"
     *   capitalizeWords("")              → ""
     *   capitalizeWords(null)            → null
     *   capitalizeWords("  spaced  out ")→ "  Spaced  Out "
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Split on {@code "\\s+"} but this loses leading/trailing spaces —
     *       a regex-based {@code Matcher} (or manual scan) preserves them.</li>
     *   <li>For a first pass, splitting and re-joining with single spaces is
     *       acceptable if the Javadoc examples are honoured.</li>
     *   <li>{@code Character.toUpperCase} on the first char of each word.</li>
     * </ul>
     *
     * @param s the input string (may be null)
     * @return the capitalised version, or the input if null/blank
     */
    public String capitalizeWords(String s) {
        throw new UnsupportedOperationException("TODO: implement capitalizeWords");
    }
}