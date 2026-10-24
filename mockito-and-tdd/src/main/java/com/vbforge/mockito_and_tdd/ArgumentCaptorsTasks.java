package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.EventPublisher;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.Order;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderLine;
import com.vbforge.mockito_and_tdd.ArgumentCaptorsDemo.OrderRepository;

import java.util.List;

/**
 * ArgumentCaptorsTasks — implement every method below to make {@code ArgumentCaptorsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code ArgumentCaptorsDemo.java} and {@code ArgumentCaptorsDemoTest.java}.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=ArgumentCaptorsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * Every method here returns {@code void}: the only way the tests can see your result is
 * by capturing the arguments you pass to the collaborator with an {@code ArgumentCaptor}.
 * The domain types ({@link Order}, {@link OrderLine}, …) live in {@code ArgumentCaptorsDemo}.
 */
public class ArgumentCaptorsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Saves <em>one order per line</em>: each saved {@link Order} belongs to
     * {@code customerId}, contains exactly that one line, and has a total equal to
     * that line's {@code lineTotalCents()}. Orders are saved in the order of the lines.
     *
     * <p>Examples:
     * <pre>
     *   splitAndSave(repo, "cust-1", [2x pen@100, 1x ink@300, 5x clip@10])
     *       → save(Order["cust-1", [2x pen@100], total=200])
     *       → save(Order["cust-1", [1x ink@300], total=300])
     *       → save(Order["cust-1", [5x clip@10], total=50])
     *
     *   splitAndSave(repo, "cust-1", [])  → repository is never called
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Build each {@link Order} with its constructor
     *       {@code Order(customerId, lines, totalCents)}.</li>
     *   <li>A single-element list is easy to make with {@link java.util.Collections}.</li>
     * </ul>
     *
     * @param repository where orders are saved
     * @param customerId the customer who owns every order
     * @param lines      the lines to split into separate orders (may be empty)
     */
    public void splitAndSave(OrderRepository repository, String customerId, List<OrderLine> lines) {
        throw new UnsupportedOperationException("TODO: implement splitAndSave");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Publishes exactly one summary event for the given lines.
     * Topic: {@code "orders.summary"}. Payload: {@code "<lineCount> lines, <totalCents> cents"}
     * where {@code totalCents} is the sum of every line's {@code lineTotalCents()}.
     * The word "lines" is not pluralised — a single line still reads "1 lines".
     *
     * <p>Examples:
     * <pre>
     *   publishSummary(pub, [2x pen@100, 1x ink@300])
     *       → publish("orders.summary", "2 lines, 500 cents")
     *
     *   publishSummary(pub, [4x clip@25])
     *       → publish("orders.summary", "1 lines, 100 cents")
     *
     *   publishSummary(pub, [])
     *       → publish("orders.summary", "0 lines, 0 cents")
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link EventPublisher#publish} is called once, always — even for an empty list.</li>
     *   <li>Keep the sum in a {@code long}; unit prices are in cents.</li>
     * </ul>
     *
     * @param publisher where the summary event goes
     * @param lines     the order lines to summarise (may be empty)
     */
    public void publishSummary(EventPublisher publisher, List<OrderLine> lines) {
        throw new UnsupportedOperationException("TODO: implement publishSummary");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Saves a <em>new</em> {@link Order} equal to {@code order} with {@code percent}
     * percent taken off every unit price. The original order is left untouched.
     *
     * <p>New unit price = {@code unitCents * (100 - percent) / 100} using integer
     * division (rounds down). The saved order keeps the customer id, and its SKUs and
     * quantities; its total is recomputed from the discounted lines.
     * {@code percent} must be between 0 and 100 inclusive; otherwise nothing is saved
     * and an {@link IllegalArgumentException} is thrown.
     *
     * <p>Examples:
     * <pre>
     *   order = Order["c1", [2x A@1000, 1x B@500], total=2500]
     *   applyDiscount(repo, order, 50)
     *       → save(Order["c1", [2x A@500, 1x B@250], total=1250])
     *   applyDiscount(repo, order, 0)    → saves an equal-valued but new Order
     *   applyDiscount(repo, order, 100)  → saves lines with unit price 0, total 0
     *
     *   line 1x A@999 with percent 10    → unit price 999 * 90 / 100 = 899 (rounded down)
     *   applyDiscount(repo, order, -1)   → throws IllegalArgumentException, nothing saved
     *   applyDiscount(repo, order, 101)  → throws IllegalArgumentException, nothing saved
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>{@link OrderLine} and {@link Order} are immutable — build new instances.</li>
     *   <li>Validate {@code percent} before touching the repository.</li>
     * </ul>
     *
     * @param repository where the discounted order is saved
     * @param order      the original order (not modified)
     * @param percent    the discount percentage, 0 to 100
     * @throws IllegalArgumentException if {@code percent} is outside 0..100
     */
    public void applyDiscount(OrderRepository repository, Order order, int percent) {
        throw new UnsupportedOperationException("TODO: implement applyDiscount");
    }
}
