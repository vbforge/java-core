package com.vbforge.practice.practice_10_rethrow_and_wrapping;

/**
 * Wrapping (translating) an exception keeps the original as the cause.
 *
 *   ✓ Wrap:        throw new ServiceException(msg, e);      — cause chain preserved
 *   ✗ Lose cause:  throw new ServiceException(e.getMessage()) — original trace lost
 *
 * The caller can walk the chain with getCause().
 */
public class WrapWithCauseDemo {

    static class DatabaseException extends RuntimeException {
        DatabaseException(String msg, Throwable cause) { super(msg, cause); }
    }

    static class ServiceException extends RuntimeException {
        ServiceException(String msg, Throwable cause) { super(msg, cause); }
    }

    public static void main(String[] args) {
        try {
            serviceCall();
        } catch (ServiceException e) {
            System.out.println("ServiceException: " + e.getMessage());
            System.out.println("Caused by:        " + e.getCause().getMessage());
            System.out.println("Root cause:       " + e.getCause().getCause().getMessage());
        }
    }

    static void dbQuery() {
        throw new DatabaseException("timeout", new RuntimeException("socket"));
    }

    static void serviceCall() {
        try {
            dbQuery();
        } catch (DatabaseException e) {
            throw new ServiceException("load profile failed", e);
        }
    }
}
