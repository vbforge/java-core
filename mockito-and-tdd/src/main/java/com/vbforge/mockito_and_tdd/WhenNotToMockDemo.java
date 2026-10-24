package com.vbforge.mockito_and_tdd;

import java.util.HashMap;
import java.util.Map;

/**
 * WhenNotToMockDemo — annotated reference for the judgement call that matters most:
 * <em>should this collaborator be a mock at all?</em>
 *
 * <p>Mockito makes mocking so easy that over-mocking is the most common testing smell.
 * A test that mocks everything tests nothing but its own wiring. The rules of thumb:
 * <ol>
 *   <li>Never mock value objects or pure functions — use the real thing</li>
 *   <li>Never mock types you do not own ({@code List}, {@code Clock}, JDBC) — wrap them
 *       in an interface you own, then fake or mock <em>that</em></li>
 *   <li>Assert outcomes, not call counts — over-specified {@code verify} breaks on every refactor</li>
 *   <li>Prefer a fake to a mock for stateful collaborators (repositories, caches)</li>
 * </ol>
 * Mocks are right at the <em>edges</em> of your system: slow, non-deterministic or
 * side-effecting boundaries (payment gateways, mail servers, the clock).
 *
 * <p>As in the other demos, Mockito is test-scoped. Each section here runs plain Java;
 * {@code WhenNotToMockDemoTest} shows what the mock-based alternative would do wrong.
 */
public class WhenNotToMockDemo {

    public static void main(String[] args) {
        valueObjectsDemo();
        System.out.println("---");
        typesYouDontOwnDemo();
        System.out.println("---");
        overSpecificationDemo();
        System.out.println("---");
        fakeVsMockDemo();
    }

    // ── Domain ────────────────────────────────────────────────────────────────

    /** An immutable amount of money in cents. A value object: cheap, pure, real. */
    public static final class Money {
        private final long cents;

        private Money(long cents) {
            this.cents = cents;
        }

        public static Money of(long cents) {
            return new Money(cents);
        }

        public long cents() {
            return cents;
        }

        public Money plus(Money other) {
            return new Money(cents + other.cents);
        }

        @Override
        public boolean equals(Object o) {
            return o instanceof Money && ((Money) o).cents == cents;
        }

        @Override
        public int hashCode() {
            return Long.hashCode(cents);
        }

        @Override
        public String toString() {
            return "Money(" + cents + ")";
        }
    }

    /** A boundary: in production this would call a remote tax API. */
    public interface TaxService {
        /** Tax rate in basis points (1 bp = 0.01%); 1900 means 19%. */
        int basisPointsFor(String region);
    }

    public interface TaxCalculator {
        Money withTax(Money net, String region);
    }

    /** Applies the region's tax rate, asking the tax service on every call. */
    public static class PriceCalculator implements TaxCalculator {
        private final TaxService taxService;

        public PriceCalculator(TaxService taxService) {
            this.taxService = taxService;
        }

        @Override
        public Money withTax(Money net, String region) {
            int bp = taxService.basisPointsFor(region);
            return Money.of(net.cents() * (10000L + bp) / 10000L);
        }
    }

    /** Same results as {@link PriceCalculator}, but remembers each region's rate. */
    public static class CachingPriceCalculator implements TaxCalculator {
        private final TaxService taxService;
        private final Map<String, Integer> cache = new HashMap<>();

        public CachingPriceCalculator(TaxService taxService) {
            this.taxService = taxService;
        }

        @Override
        public Money withTax(Money net, String region) {
            Integer bp = cache.get(region);
            if (bp == null) {
                bp = taxService.basisPointsFor(region);
                cache.put(region, bp);
            }
            return Money.of(net.cents() * (10000L + bp) / 10000L);
        }
    }

    /** FAKE tax service: a working in-memory lookup that also counts how often it is asked. */
    public static class FakeTaxService implements TaxService {
        private final Map<String, Integer> rates = new HashMap<>();
        private int lookups;

        public FakeTaxService with(String region, int basisPoints) {
            rates.put(region, basisPoints);
            return this;
        }

        @Override
        public int basisPointsFor(String region) {
            lookups++;
            Integer bp = rates.get(region);
            if (bp == null) {
                throw new IllegalArgumentException("unknown region: " + region);
            }
            return bp;
        }

        public int lookups() {
            return lookups;
        }
    }

    /** A boundary we own: the application's view of "what time is it". */
    public interface TimeSource {
        long nowMillis();
    }

    /** Production adapter around the JDK clock — the only place that touches it. */
    public static class SystemTimeSource implements TimeSource {
        @Override
        public long nowMillis() {
            return System.currentTimeMillis();
        }
    }

    /** Test adapter: time stands still. */
    public static class FixedTimeSource implements TimeSource {
        private final long now;

        public FixedTimeSource(long now) {
            this.now = now;
        }

        @Override
        public long nowMillis() {
            return now;
        }
    }

    /** Decides whether a token has expired, using only our own {@link TimeSource}. */
    public static class TokenPolicy {
        private final TimeSource timeSource;

        public TokenPolicy(TimeSource timeSource) {
            this.timeSource = timeSource;
        }

        public boolean isExpired(long issuedAtMillis, long ttlMillis) {
            return timeSource.nowMillis() - issuedAtMillis >= ttlMillis;
        }
    }

    // ── 1. Value objects and pure functions ───────────────────────────────────

    /**
     * Do not mock a value object. {@link Money} is immutable, fast and has no
     * dependencies — building a real one is shorter than configuring a mock, and the
     * real one actually <em>behaves</em>.
     *
     * <p>WHY this matters: a mock only knows what you teach it. {@code mock(Money.class).plus(x)}
     * returns {@code null}, not a sum. Mocking a value object means re-implementing it,
     * badly, inside the test.
     *
     * <p>Rule of thumb: if you can construct it in one line and it has no I/O, use it for real.
     */
    static void valueObjectsDemo() {
        System.out.println("=== Value objects ===");

        Money a = Money.of(250);
        Money b = Money.of(175);
        System.out.println("a + b         : " + a.plus(b));                       // Money(425)
        System.out.println("equals        : " + Money.of(425).equals(a.plus(b))); // true
    }

    // ── 2. Types you don't own ────────────────────────────────────────────────

    /**
     * Do not mock types you do not own — {@code List}, {@code Map}, the JDK clock, a
     * JDBC {@code Connection}. Your mock encodes <em>your guess</em> about how the library
     * behaves; when the guess is wrong the test stays green and production breaks.
     *
     * <p>The fix is to put your own small interface ({@link TimeSource}) in front of the
     * awkward type. The production adapter is one line; tests use a one-line fixed fake or
     * a mock of <em>your</em> interface.
     *
     * <p>WHY this matters: it also gives the application a seam it controls — nothing in
     * {@link TokenPolicy} knows what a {@code System} clock is.
     *
     * <p>Common gotcha: a {@code mock(List.class)} accepts {@code add("x")} and then still
     * reports {@code size() == 0}. It is not a list; it is a recorder shaped like one.
     */
    static void typesYouDontOwnDemo() {
        System.out.println("=== Types you don't own ===");

        TokenPolicy policy = new TokenPolicy(new FixedTimeSource(10_000));
        System.out.println("age 4s, ttl 5s: expired=" + policy.isExpired(6_000, 5_000));   // false
        System.out.println("age 5s, ttl 5s: expired=" + policy.isExpired(5_000, 5_000));   // true
    }

    // ── 3. Assert outcomes, not call counts ───────────────────────────────────

    /**
     * Over-specification: {@code verify(taxService, times(2)).basisPointsFor("DE")} pins
     * <em>how many times</em> the calculator asks. Introduce a cache — behaviour for
     * callers is identical — and the test fails.
     *
     * <p>WHY this matters: tests should fail when behaviour breaks, not when the
     * implementation is improved. A suite of over-specified tests punishes refactoring,
     * so people stop refactoring.
     *
     * <p>Below, both calculators give the same prices; only the number of lookups differs.
     * Assert on the price. Verify the interaction only when the interaction <em>is</em> the
     * requirement (e.g. "the customer must be emailed").
     */
    static void overSpecificationDemo() {
        System.out.println("=== Outcomes, not call counts ===");

        FakeTaxService plainRates = new FakeTaxService().with("DE", 1900);
        TaxCalculator plain = new PriceCalculator(plainRates);
        plain.withTax(Money.of(1000), "DE");
        Money plainResult = plain.withTax(Money.of(2000), "DE");

        FakeTaxService cachedRates = new FakeTaxService().with("DE", 1900);
        TaxCalculator caching = new CachingPriceCalculator(cachedRates);
        caching.withTax(Money.of(1000), "DE");
        Money cachedResult = caching.withTax(Money.of(2000), "DE");

        System.out.println("plain result  : " + plainResult + ", lookups=" + plainRates.lookups());   // 2380, 2
        System.out.println("cached result : " + cachedResult + ", lookups=" + cachedRates.lookups()); // 2380, 1
    }

    // ── 4. Fake vs mock ───────────────────────────────────────────────────────

    /**
     * For a collaborator with real behaviour, a fake beats a mock: configure it once,
     * reuse it in every test, and it fails <em>realistically</em>.
     *
     * <p>{@link FakeTaxService} throws for an unknown region, like the real service would.
     * A mock you forgot to stub returns {@code 0} — "no tax" — and the test happily passes
     * with a price that is wrong.
     *
     * <p>WHY this matters: silent defaults are how a mocked test goes green while the real
     * system charges the wrong price.
     *
     * <p>Rule of thumb: mock the <em>edge</em> to simulate failures and slowness; fake
     * the <em>middle</em> (repositories, caches) to keep tests short and state-based.
     */
    static void fakeVsMockDemo() {
        System.out.println("=== Fake vs mock ===");

        TaxCalculator calculator = new PriceCalculator(new FakeTaxService().with("DE", 1900));
        System.out.println("DE            : " + calculator.withTax(Money.of(1000), "DE"));   // Money(1190)
        try {
            calculator.withTax(Money.of(1000), "XX");
        } catch (IllegalArgumentException e) {
            System.out.println("unknown region: " + e.getMessage());
        }
    }
}
