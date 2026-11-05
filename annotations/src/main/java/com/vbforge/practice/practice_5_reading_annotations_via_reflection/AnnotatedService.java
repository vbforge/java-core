package com.vbforge.practice.practice_5_reading_annotations_via_reflection;

/**
 * Example service class used as a target for annotation demos.
 *
 * This class is annotated at multiple levels:
 *   - class-level     : @Deprecated (built-in, RUNTIME retention)
 *   - field-level     : @NotNull (custom, RUNTIME)
 *   - method-level    : @Audit, @Retry (custom, RUNTIME)
 *   - parameter-level : @Validated (custom, RUNTIME)
 *
 * Note: @SuppressWarnings is also present on the class, but because it has
 * SOURCE retention, it will NOT be visible via reflection at runtime.
 */
@Deprecated(since = "2.0", forRemoval = true)
@SuppressWarnings("all")
public class AnnotatedService {

    @NotNull(message = "serviceId must not be null")
    private String serviceId;

    private int    requestCount;

    public AnnotatedService(String serviceId) {
        this.serviceId = serviceId;
    }

    // -----------------------------------------------------------------------
    // Methods with annotations
    // -----------------------------------------------------------------------

    /**
     * Process a payment — annotated with @Audit and @Retry.
     * A real framework would intercept this call, log the audit event,
     * and wrap the invocation in a retry loop.
     */
    @Audit(action = "PAYMENT_PROCESS", level = "WARN")
    @Retry(maxAttempts = 5, delayMs = 500)
    public String processPayment(double amount) {
        requestCount++;
        return "Processed payment of $" + amount + " (attempt #" + requestCount + ")";
    }

    /**
     * Fetch user data — annotated with @Audit only.
     */
    @Audit(action = "USER_FETCH")
    public String fetchUser(long userId) {
        return "User#" + userId;
    }

    /**
     * Plain method — no annotations.
     */
    public String healthCheck() {
        return "OK";
    }

    /**
     * Method with annotated parameters.
     * Demonstrates PARAMETER-level annotation inspection.
     */
    public String processRequest(
        @Validated("notBlank") String requestId,
        @Validated int         priority
    ) {
        return "Processing request=" + requestId + " priority=" + priority;
    }

    // Getters
    public String getServiceId()   { return serviceId; }
    public int    getRequestCount() { return requestCount; }
}