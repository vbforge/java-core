package com.vbforge.optional_and_null_handling;

import com.vbforge.optional_and_null_handling.model.User;

import java.util.List;
import java.util.Optional;

/**
 * 🎯 TASKS — Chaining Optionals with map / flatMap / filter
 *
 * <p>Implement every method so that all tests in {@code OptionalChainingTasksTest} pass.
 * Use the {@link User} model: {@code getName()} returns a {@code String},
 * {@code getAddress()} returns {@code Optional<Address>}, {@code Address.getCity()} returns a
 * {@code String} that may be {@code null}, and {@code Address.getZip()} returns {@code Optional<String>}.
 *
 * <p>Rules:
 * <ul>
 *   <li>Do NOT change method signatures or return types</li>
 *   <li>Replace the {@code throw new UnsupportedOperationException(...)} body</li>
 *   <li>Remove {@code @Disabled} from the matching {@code @Nested} test class when you start a task</li>
 * </ul>
 */
public class OptionalChainingTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────
    /**
     * Return the length of the text after trimming whitespace.
     * An empty Optional stays empty. A blank text gives {@code Optional[0]}.
     *
     * <p>Examples:
     * <pre>
     *   trimmedLength(Optional.of("  hello "))  → Optional[5]
     *   trimmedLength(Optional.of("   "))       → Optional[0]
     *   trimmedLength(Optional.empty())         → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Transform the value inside the Optional in two small steps</li>
     * </ul>
     *
     * @param text the optional text
     * @return the trimmed length, or empty when there was no text
     */
    public Optional<Integer> trimmedLength(Optional<String> text) {
        throw new UnsupportedOperationException("TODO: implement trimmedLength");
    }


    // ── Task 2 ─────────────────────────────────────────────────────────────────
    /**
     * Return the zip code of the user's address. The result is empty when the user
     * is {@code null}, has no address, or the address has no zip.
     *
     * <p>Examples:
     * <pre>
     *   zipOf(user with address zip "01001")      → Optional[01001]
     *   zipOf(user with no address)               → Optional.empty
     *   zipOf(user whose address has no zip)      → Optional.empty
     *   zipOf(null)                               → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Both {@code getAddress()} and {@code getZip()} already return an {@code Optional}</li>
     *   <li>Start by wrapping the possibly-{@code null} user</li>
     * </ul>
     *
     * @param user the user, may be {@code null}
     * @return the zip code, or empty
     */
    public Optional<String> zipOf(User user) {
        throw new UnsupportedOperationException("TODO: implement zipOf");
    }


    // ── Task 3 ─────────────────────────────────────────────────────────────────
    /**
     * Return the user's city only when it starts with the given prefix
     * (case-sensitive). Empty when there is no user, no address, no city, or the
     * city does not start with the prefix.
     *
     * <p>Examples:
     * <pre>
     *   cityStartingWith(user in "Kyiv", "Ky")     → Optional[Kyiv]
     *   cityStartingWith(user in "Kyiv", "Lv")     → Optional.empty
     *   cityStartingWith(user in "Kyiv", "ky")     → Optional.empty   (case-sensitive)
     *   cityStartingWith(user with no city, "K")   → Optional.empty
     *   cityStartingWith(null, "K")                → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@code Address.getCity()} may return {@code null} — one of the Optional methods tolerates that</li>
     *   <li>One stage can turn a present value into an empty one</li>
     * </ul>
     *
     * @param user   the user, may be {@code null}
     * @param prefix the required prefix, never {@code null}
     * @return the matching city, or empty
     */
    public Optional<String> cityStartingWith(User user, String prefix) {
        throw new UnsupportedOperationException("TODO: implement cityStartingWith");
    }


    // ── Task 4 ─────────────────────────────────────────────────────────────────
    /**
     * Build a one-line label for the user:
     * <ul>
     *   <li>{@code "<name> (<city>)"} when the user has a city</li>
     *   <li>{@code "<name>"} when the user has no address or no city</li>
     *   <li>{@code "nobody"} when the user is {@code null}</li>
     * </ul>
     *
     * <p>Examples:
     * <pre>
     *   mailingLabel(Ann, address city "Kyiv")   → "Ann (Kyiv)"
     *   mailingLabel(Bob, no address)            → "Bob"
     *   mailingLabel(Cid, address without city)  → "Cid"
     *   mailingLabel(null)                       → "nobody"
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The label depends on TWO optional layers: first the user, then the city</li>
     *   <li>An inner Optional can produce the " (city)" suffix, or an empty string</li>
     * </ul>
     *
     * @param user the user, may be {@code null}
     * @return the label
     */
    public String mailingLabel(User user) {
        throw new UnsupportedOperationException("TODO: implement mailingLabel");
    }


    // ── Task 5 ─────────────────────────────────────────────────────────────────
    /**
     * Add two optional numbers. The result is present ONLY when both inputs are present.
     *
     * <p>Examples:
     * <pre>
     *   sumIfBothPresent(Optional.of(2), Optional.of(3))    → Optional[5]
     *   sumIfBothPresent(Optional.of(2), Optional.empty())  → Optional.empty
     *   sumIfBothPresent(Optional.empty(), Optional.of(3))  → Optional.empty
     *   sumIfBothPresent(Optional.empty(), Optional.empty())→ Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Use the first Optional's value inside a function that also touches the second Optional</li>
     *   <li>Decide which of the two chaining methods the inner step needs, and which the outer one needs</li>
     * </ul>
     *
     * @param a the first number, optional
     * @param b the second number, optional
     * @return the sum, or empty when either input is empty
     */
    public Optional<Integer> sumIfBothPresent(Optional<Integer> a, Optional<Integer> b) {
        throw new UnsupportedOperationException("TODO: implement sumIfBothPresent");
    }


    // ── Task 6 ─────────────────────────────────────────────────────────────────
    /**
     * Collect the cities of all users that have one, in the original order.
     * Skip {@code null} entries, users without an address, and addresses without a city.
     * Duplicates are kept.
     *
     * <p>Examples:
     * <pre>
     *   citiesOf([Ann in Kyiv, Bob (no address), Cid in Lviv])  → ["Kyiv", "Lviv"]
     *   citiesOf([null, Bob (no address)])                      → []
     *   citiesOf([])                                            → []
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>A loop that feeds each user's city Optional into the result is perfectly fine</li>
     * </ul>
     *
     * @param users the users to scan, never {@code null} (elements may be)
     * @return a new list with every available city
     */
    public List<String> citiesOf(List<User> users) {
        throw new UnsupportedOperationException("TODO: implement citiesOf");
    }
}
