package com.vbforge.mockito_and_tdd;

import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.ChargeStatus;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.GatewayException;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.Notifier;
import com.vbforge.mockito_and_tdd.MockitoBasicsDemo.PaymentGateway;

import java.util.List;
import java.util.Map;

/**
 * MockitoBasicsTasks — implement every method below to make {@code MockitoBasicsTasksTest} go GREEN.
 *
 * <p><b>Workflow:</b>
 * <ol>
 *   <li>Read {@code MockitoBasicsDemo.java} and {@code MockitoBasicsDemoTest.java}.</li>
 *   <li>Implement the method — remove the {@code UnsupportedOperationException}.</li>
 *   <li>Run {@code mvn test -Dtest=MockitoBasicsTasksTest} and watch it turn GREEN ✅</li>
 * </ol>
 *
 * <p>Each method has a Javadoc describing exactly what to implement. Do not change
 * method signatures or add external libraries — only the Java standard library is needed.
 * You are writing the <em>production</em> code; the tests drive it with Mockito mocks of
 * {@link PaymentGateway} and {@link Notifier} and check both results and interactions.
 */
public class MockitoBasicsTasks {

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Charges an account, but only if the gateway is up. A customer notification is
     * sent <em>only</em> when the charge is approved.
     *
     * <p>Examples:
     * <pre>
     *   gateway available, charge → APPROVED
     *       → returns APPROVED, notifier gets ("acc-1", "Charged 500 cents")
     *   gateway available, charge → DECLINED
     *       → returns DECLINED, notifier is never called
     *   gateway NOT available
     *       → throws IllegalStateException("gateway unavailable"),
     *         charge is never attempted, notifier is never called
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Consult {@link PaymentGateway#isAvailable()} before charging.</li>
     *   <li>Compare the status with the {@link ChargeStatus} enum constants.</li>
     * </ul>
     *
     * @param gateway   the payment gateway
     * @param notifier  tells the customer about successful charges
     * @param accountId the account to charge
     * @param cents     the amount in cents
     * @return the status returned by the gateway
     * @throws IllegalStateException if the gateway is not available
     */
    public ChargeStatus checkout(PaymentGateway gateway, Notifier notifier,
                                 String accountId, long cents) {
        throw new UnsupportedOperationException("TODO: implement checkout");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Charges an account, retrying when the gateway throws a {@link GatewayException}.
     * Returns the first status the gateway actually answers with. If every attempt
     * throws, the <em>last</em> exception is rethrown.
     *
     * <p>A {@code DECLINED} answer is a real answer, not an error — it is returned
     * immediately without retrying.
     *
     * <p>Examples:
     * <pre>
     *   chargeWithRetry(gw, "acc-1", 500, 3)
     *       gateway answers: APPROVED                         → APPROVED (1 call)
     *       gateway answers: throw, throw, APPROVED           → APPROVED (3 calls)
     *       gateway answers: throw("t1"), throw("t2"), throw("t3")
     *                                                         → throws GatewayException("t3") (3 calls)
     *       gateway answers: DECLINED                         → DECLINED (1 call)
     *   chargeWithRetry(gw, "acc-1", 500, 0)
     *                                       → throws IllegalArgumentException, gateway untouched
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Remember the exception from the latest failed attempt so you can rethrow it.</li>
     *   <li>Validate {@code maxAttempts} before the first call to the gateway.</li>
     * </ul>
     *
     * @param gateway     the payment gateway
     * @param accountId   the account to charge
     * @param cents       the amount in cents
     * @param maxAttempts the maximum number of charge attempts (must be at least 1)
     * @return the first status returned by the gateway
     * @throws IllegalArgumentException if {@code maxAttempts < 1}
     * @throws GatewayException         if every attempt threw
     */
    public ChargeStatus chargeWithRetry(PaymentGateway gateway, String accountId,
                                        long cents, int maxAttempts) {
        throw new UnsupportedOperationException("TODO: implement chargeWithRetry");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Charges every account in the map, in the map's iteration order, and returns the
     * ids of the accounts whose charge was {@code DECLINED} (in that same order).
     * A declined charge does not stop the run.
     *
     * <p>Examples:
     * <pre>
     *   chargeAll(gw, {"acc-1": 100, "acc-2": 200, "acc-3": 300})
     *       gateway: acc-1 → APPROVED, acc-2 → DECLINED, acc-3 → APPROVED
     *       → ["acc-2"]      (charges happen in the order acc-1, acc-2, acc-3)
     *
     *   chargeAll(gw, {})  → []   (gateway untouched)
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Iterate the map's entries — the tests pass a {@link java.util.LinkedHashMap}
     *       so the order is predictable.</li>
     *   <li>{@code Map.Entry<String, Long>} unboxes to the {@code long} the gateway wants.</li>
     * </ul>
     *
     * @param gateway          the payment gateway
     * @param amountsByAccount account id → amount in cents
     * @return ids of declined accounts, in charge order
     */
    public List<String> chargeAll(PaymentGateway gateway, Map<String, Long> amountsByAccount) {
        throw new UnsupportedOperationException("TODO: implement chargeAll");
    }
}
