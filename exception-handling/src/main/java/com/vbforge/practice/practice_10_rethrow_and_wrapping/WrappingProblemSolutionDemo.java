package com.vbforge.practice.practice_10_rethrow_and_wrapping;

/**
 * Wrapping exceptions — the problem and the solution.
 *
 * PROBLEM: wrapping at every layer
 *   PersistenceException → BusinessException → ApiException
 *   Each layer adds nothing except another `catch + throw new`. The final caller
 *   sees `ApiException(code=1001)` with no context about what actually failed —
 *   and the original message "Failed to persist customer" is buried 3 wrappers deep.
 *
 * SOLUTION: wrap only when semantics change
 *   - Persistence layer throws PersistenceException — informative, let it propagate
 *   - Business layer catches it and wraps into BusinessException — semantic change
 *   - API layer doesn't wrap again — it's already a business error
 *
 * Rule of thumb: wrap when the exception crosses a *semantic boundary*
 * (persistence → business, business → API), not when it crosses a *layer*.
 */
public class WrappingProblemSolutionDemo {

    public static void main(String[] args) {
        System.out.println("=== PROBLEM: wrap at every layer ===\n");
        problem();

        System.out.println("\n=== SOLUTION: wrap only when semantics change ===\n");
        solution();
    }

    // ─────────────────────────────────────────────────────────────
    // PROBLEM: every layer wraps
    // ─────────────────────────────────────────────────────────────

    static class ProblemCustomer { }

    static class ProblemPersistenceException extends Exception {
        private final int code;
        ProblemPersistenceException(String message, int code) { super(message); this.code = code; }
        int getCode() { return code; }
    }

    static class ProblemBusinessException extends Exception {
        private final int code;
        ProblemBusinessException(Throwable cause, int code) { super(cause); this.code = code; }
        int getCode() { return code; }
    }

    static class ProblemApiException extends Exception {
        private final int code;
        ProblemApiException(Throwable cause, int code) { super(cause); this.code = code; }
        int getCode() { return code; }
    }

    static class ProblemCustomerService {
        void persist(ProblemCustomer c) throws ProblemPersistenceException {
            throw new ProblemPersistenceException("Failed to persist customer", 1001);
        }

        void manage(ProblemCustomer c) throws ProblemBusinessException {
            try {
                persist(c);
            } catch (ProblemPersistenceException ex) {
                throw new ProblemBusinessException(ex, ex.getCode());
            }
        }

        void create(ProblemCustomer c) throws ProblemApiException {
            try {
                manage(c);
            } catch (ProblemBusinessException ex) {
                throw new ProblemApiException(ex, ex.getCode());
            }
        }
    }

    static void problem() {
        ProblemCustomerService service = new ProblemCustomerService();
        try {
            service.create(new ProblemCustomer());
        } catch (ProblemApiException ex) {
            System.out.println("caller sees:       " + ex.getClass().getSimpleName()
                    + " (code=" + ex.getCode() + ")");
            System.out.println("message:           " + ex.getMessage());
            System.out.println("cause message:     " + ex.getCause().getMessage());
            System.out.println("root cause message: " + ex.getCause().getCause().getMessage());
            System.out.println("  ↑ buried 3 wrappers deep — no layer added real info");
        }
    }

    // ─────────────────────────────────────────────────────────────
    // SOLUTION: wrap only when semantic boundary is crossed
    // ─────────────────────────────────────────────────────────────

    static class Customer { }

    static class PersistenceException extends Exception {
        PersistenceException(String message) { super(message); }
    }

    static class BusinessException extends Exception {
        BusinessException(String message, Throwable cause) { super(message, cause); }
    }

    static class CustomerService {
        void persist(Customer c) throws PersistenceException {
            throw new PersistenceException("Failed to persist customer");
        }

        /** Let PersistenceException propagate — it's still informative here. */
        void manage(Customer c) throws PersistenceException {
            persist(c);
        }

        /** Wrap once, at the business boundary. The persistence cause is preserved. */
        void create(Customer c) throws BusinessException {
            try {
                manage(c);
            } catch (PersistenceException ex) {
                throw new BusinessException(
                        "Business error while creating customer", ex);
            }
        }
    }

    static void solution() {
        CustomerService service = new CustomerService();
        try {
            service.create(new Customer());
        } catch (BusinessException ex) {
            System.out.println("caller sees:       " + ex.getClass().getSimpleName());
            System.out.println("message:           " + ex.getMessage());
            System.out.println("cause message:     " + ex.getCause().getMessage());
            System.out.println("  ↑ single wrapper at a real semantic boundary — the cause is intact");
        }
    }
}
