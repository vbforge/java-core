package com.vbforge.optional_and_null_handling;

import com.vbforge.optional_and_null_handling.model.Address;
import com.vbforge.optional_and_null_handling.model.User;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Demonstrates CHAINING on {@link Optional}: {@code map}, {@code flatMap}, {@code filter}.
 *
 * <p>Why it matters: this is what replaces the "pyramid of null checks".
 * You describe the path {@code user → address → city} once, and an absent link
 * anywhere simply turns the whole result into {@code Optional.empty()} —
 * no {@code if (x != null)} at every step.
 *
 * <p>Uses the tiny {@link User} / {@link Address} model:
 * {@code User.getAddress()} and {@code Address.getZip()} return {@code Optional};
 * {@code Address.getCity()} is a legacy getter that may return {@code null}.
 */
public class OptionalChainingDemo {

    // ── 1. map ────────────────────────────────────────────────────────────────
    /**
     * {@code map} transforms the value INSIDE the Optional; an empty Optional stays
     * empty and the function is not called.
     *
     * <p>Complexity: O(1) per stage (plus whatever the function itself costs).
     */
    public static Optional<String> upperDemo(Optional<String> name) {
        return name.map(String::toUpperCase);
    }

    /**
     * <b>Gotcha:</b> if the mapping function returns {@code null}, {@code map} does
     * NOT fail — the result is simply {@code Optional.empty()}.
     * That is why a legacy getter like {@code Address::getCity} is safe inside {@code map}.
     */
    public static Optional<String> cityOfDemo(Address address) {
        return Optional.ofNullable(address).map(Address::getCity);
    }


    // ── 2. map vs flatMap ─────────────────────────────────────────────────────
    /**
     * <b>Gotcha:</b> {@code map} with a function that itself returns an Optional
     * produces a NESTED Optional. The outer layer says "there is a user", the
     * inner layer says "there is an address" — two separate questions you must
     * now check separately.
     */
    public static Optional<Optional<Address>> nestedDemo(User user) {
        return Optional.ofNullable(user).map(User::getAddress);
    }

    /**
     * {@code flatMap} is {@code map} + "flatten one level": the function's own
     * Optional becomes the result, so you get a single {@code Optional<Address>}.
     * Rule: function returns a plain value → {@code map}; function returns an
     * Optional → {@code flatMap}.
     */
    public static Optional<Address> flattenedDemo(User user) {
        return Optional.ofNullable(user).flatMap(User::getAddress);
    }


    // ── 3. Chain instead of null-check pyramid ────────────────────────────────
    /**
     * The chain: nullable user → Optional address → nullable city → upper-case.
     * Any missing link yields {@code "UNKNOWN"}.
     */
    public static String cityChainDemo(User user) {
        return Optional.ofNullable(user)
                .flatMap(User::getAddress)
                .map(Address::getCity)
                .map(String::toUpperCase)
                .orElse("UNKNOWN");
    }

    /** Same behaviour as {@link #cityChainDemo(User)}, written the pre-Optional way. */
    public static String cityLegacyDemo(User user) {
        if (user != null) {
            Optional<Address> address = user.getAddress();
            if (address.isPresent()) {
                String city = address.get().getCity();
                if (city != null) {
                    return city.toUpperCase();
                }
            }
        }
        return "UNKNOWN";
    }


    // ── 4. filter ─────────────────────────────────────────────────────────────
    /**
     * {@code filter} keeps the value only when the predicate is true; otherwise the
     * result becomes empty. A present Optional can turn EMPTY here — that is the point.
     * Example: a zip code is only accepted when it has exactly 5 characters.
     */
    public static Optional<String> validZipDemo(User user) {
        return Optional.ofNullable(user)
                .flatMap(User::getAddress)
                .flatMap(Address::getZip)
                .filter(zip -> zip.length() == 5);
    }


    // ── 5. Chains are lazy ────────────────────────────────────────────────────
    /**
     * Every stage is skipped once the Optional is empty — the lambda below never runs
     * for an empty input. The {@code calls} counter proves it.
     */
    public static Optional<Integer> lazyMapDemo(Optional<String> text, AtomicInteger calls) {
        return text.map(s -> {
            calls.incrementAndGet();
            return s.length();
        });
    }


    // ── Main — quick smoke run ────────────────────────────────────────────────
    public static void main(String[] args) {
        User withAll = new User("Ann", new Address("Kyiv", "01001"));
        User noAddress = new User("Bob", null);
        User noCity = new User("Cid", new Address(null, "12"));

        System.out.println("=== 1. map ===");
        System.out.println(upperDemo(Optional.of("java")));
        System.out.println(upperDemo(Optional.<String>empty()));
        System.out.println(cityOfDemo(new Address(null, "01001")));   // null city -> empty

        System.out.println("---");
        System.out.println("=== 2. map vs flatMap ===");
        System.out.println(nestedDemo(noAddress));                     // Optional[Optional.empty]
        System.out.println(flattenedDemo(noAddress));                  // Optional.empty

        System.out.println("---");
        System.out.println("=== 3. chain vs pyramid ===");
        System.out.println(cityChainDemo(withAll) + " / " + cityLegacyDemo(withAll));
        System.out.println(cityChainDemo(noAddress) + " / " + cityLegacyDemo(noAddress));
        System.out.println(cityChainDemo(noCity) + " / " + cityLegacyDemo(noCity));
        System.out.println(cityChainDemo(null) + " / " + cityLegacyDemo(null));

        System.out.println("---");
        System.out.println("=== 4. filter ===");
        System.out.println(validZipDemo(withAll));
        System.out.println(validZipDemo(noCity));

        System.out.println("---");
        System.out.println("=== 5. lazy chain ===");
        AtomicInteger calls = new AtomicInteger();
        lazyMapDemo(Optional.<String>empty(), calls);
        System.out.println("calls after empty input: " + calls.get());
        lazyMapDemo(Optional.of("abc"), calls);
        System.out.println("calls after present input: " + calls.get());
    }
}
