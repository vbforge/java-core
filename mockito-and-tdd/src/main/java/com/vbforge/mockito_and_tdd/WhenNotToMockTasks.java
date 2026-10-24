package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.Money;
import com.vbforge.mockito_and_tdd.WhenNotToMockDemo.TaxService;

import java.util.List;
import java.util.Optional;

/**
 * WhenNotToMockTasks — implement every method below to make {@code WhenNotToMockTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code WhenNotToMockDemo.java} and {@code WhenNotToMockDemoTest.java}.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=WhenNotToMockTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Notice what the tests do <em>not</em> use: not a single mock. They pass real
 * {@link Money} values and an in-memory fake {@link TaxService}, and assert on results.
 */
public class WhenNotToMockTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Adds up the items and applies the region's tax rate to the sum.
     * The rate comes from {@link TaxService#basisPointsFor(String)} in basis points
     * (1900 = 19%). Result cents = {@code sumCents * (10000 + basisPoints) / 10000}
     * using integer division (rounds down).
     *
     * <p>If the tax service rejects the region it throws; let that exception propagate.
     *
     * <p>Examples:
     * <pre>
     *   totalWithTax(tax{"DE"=1900}, [1000, 500], "DE")  → Money(1785)   (1500 * 1.19)
     *   totalWithTax(tax{"US"=0},    [999],       "US")  → Money(999)
     *   totalWithTax(tax{"DE"=1900}, [1],         "DE")  → Money(1)      (1.19 rounds down)
     *   totalWithTax(tax{"DE"=1900}, [],          "DE")  → Money(0)
     *   totalWithTax(tax{"DE"=1900}, [1000],      "XX")  → throws IllegalArgumentException (from the service)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link Money#plus(Money)} and {@link Money#of(long)} build sums without any
     *       mutable state.</li>
     *   <li>Use {@code long} arithmetic — the multiplication can overflow an {@code int}.</li>
     * </ul>
     *
     * @param taxService the tax rate lookup
     * @param items      the net prices (may be empty)
     * @param region     the region code to look up
     * @return the gross total
     */
    public Money totalWithTax(TaxService taxService, List<Money> items, String region) {
        throw new UnsupportedOperationException("TODO: implement totalWithTax");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns the item with the fewest cents, or an empty {@link Optional} when the list
     * is empty.
     *
     * <p>Examples:
     * <pre>
     *   cheapest([Money(300), Money(150), Money(900)])  → Optional[Money(150)]
     *   cheapest([Money(42)])                           → Optional[Money(42)]
     *   cheapest([Money(5), Money(5)])                  → Optional[Money(5)]
     *   cheapest([])                                    → Optional.empty
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>This is a pure function of its input — nothing to mock, nothing to stub.</li>
     *   <li>{@code Comparator.comparingLong(...)} pairs well with {@code min(...)} on a stream.</li>
     * </ul>
     *
     * @param items the prices to inspect (may be empty)
     * @return the cheapest item, or empty if there are none
     */
    public Optional<Money> cheapest(List<Money> items) {
        throw new UnsupportedOperationException("TODO: implement cheapest");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Splits an amount into {@code ways} parts that differ by at most one cent and add
     * up to exactly the original. Any leftover cents go one each to the <em>first</em> parts.
     *
     * <p>Examples:
     * <pre>
     *   splitEvenly(Money(100), 3)  → [Money(34), Money(33), Money(33)]
     *   splitEvenly(Money(10),  2)  → [Money(5),  Money(5)]
     *   splitEvenly(Money(5),   1)  → [Money(5)]
     *   splitEvenly(Money(2),   5)  → [Money(1), Money(1), Money(0), Money(0), Money(0)]
     *   splitEvenly(Money(0),   3)  → [Money(0), Money(0), Money(0)]
     *   splitEvenly(Money(10),  0)  → throws IllegalArgumentException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Integer division gives the base share; the remainder (%) tells you how many
     *       parts get one extra cent.</li>
     *   <li>Never lose or invent a cent — the parts must sum to the original.</li>
     * </ul>
     *
     * @param total the amount to split
     * @param ways  the number of parts, at least 1
     * @return exactly {@code ways} amounts, in order
     * @throws IllegalArgumentException if {@code ways < 1}
     */
    public List<Money> splitEvenly(Money total, int ways) {
        throw new UnsupportedOperationException("TODO: implement splitEvenly");
    }
}
