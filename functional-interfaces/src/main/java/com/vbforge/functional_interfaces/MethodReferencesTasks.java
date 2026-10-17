package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.MethodReferencesDemo.Person;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Supplier;

/**
 * 🎯 TASKS — Method References
 *
 * <p>Implement every method so that {@code MethodReferencesTasksTest} passes.
 * {@link Person} comes from {@link MethodReferencesDemo}. Where a method reference fits,
 * use it instead of a lambda — that is the point of this exercise.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures</li>
 *   <li>Remove the {@code throw new UnsupportedOperationException(...)} line when you implement</li>
 * </ul>
 */
public class MethodReferencesTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return a NEW list in which every word is trimmed and then converted to upper case.
     *
     * <p>Examples:
     * <pre>
     *   trimAndUpper(["  ann ", "bob"])  → [ANN, BOB]
     *   trimAndUpper([])                 → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Both steps are instance methods of {@code String} — unbound references</li>
     *   <li>{@code MethodReferencesDemo.mapAll} may be reused, or write the loop yourself</li>
     * </ul>
     *
     * @param words the words (never null)
     * @return a new list of trimmed, upper-case words
     */
    public List<String> trimAndUpper(List<String> words) {
        throw new UnsupportedOperationException("TODO: implement trimAndUpper");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Create one {@link Person} per name, in the same order.
     *
     * <p>Examples:
     * <pre>
     *   toPeople(["Ann", "Bob"])  → [Person(Ann), Person(Bob)]
     *   toPeople([])              → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A constructor reference fits here</li>
     * </ul>
     *
     * @param names the names (never null)
     * @return a new list of people
     */
    public List<Person> toPeople(List<String> names) {
        throw new UnsupportedOperationException("TODO: implement toPeople");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Create an empty collection by calling {@code factory}, add all words to it
     * in order, and return it. The caller chooses the collection type through the factory.
     *
     * <p>Examples:
     * <pre>
     *   collectInto(["b", "a", "b"], ArrayList::new)  → [b, a, b]
     *   collectInto(["b", "a", "b"], TreeSet::new)    → [a, b]
     *   collectInto([], HashSet::new)                 → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Call the factory exactly once</li>
     * </ul>
     *
     * @param words   the words to add (never null)
     * @param factory creates the empty target collection
     * @param <C>     the collection type the factory produces
     * @return the filled collection
     */
    public <C extends Collection<String>> C collectInto(List<String> words, Supplier<C> factory) {
        throw new UnsupportedOperationException("TODO: implement collectInto");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Return a comparator that orders people alphabetically by name, ignoring case.
     *
     * <p>Examples:
     * <pre>
     *   sort [Person(bob), Person(Ann), Person(carl)]  → [Person(Ann), Person(bob), Person(carl)]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Comparator.comparing} accepts a key extractor — pass {@code Person::getName}</li>
     *   <li>{@code String.CASE_INSENSITIVE_ORDER} is a ready-made comparator for the key</li>
     * </ul>
     *
     * @return the comparator
     */
    public Comparator<Person> byNameIgnoringCase() {
        throw new UnsupportedOperationException("TODO: implement byNameIgnoringCase");
    }
}
