package com.vbforge.practice.practice_11_custom_exceptions;

import java.util.Locale;

/**
 * Well-designed custom exceptions.
 *
 * Guidelines (see the "Exceptions" chapter of Effective Java):
 *   - Extend RuntimeException for unchecked (programming/logic errors)
 *   - Extend Exception for checked (recoverable, caller must handle)
 *   - Add context fields for domain-specific data
 *   - Name clearly: ends in "Exception", describes what went wrong
 *   - Prefer unchecked in modern Java — checked exceptions are controversial
 */
public class CustomExceptionsDemo {

    static class InsufficientFundsException extends RuntimeException {

        private final double available;
        private final double requested;

        InsufficientFundsException(double available, double requested) {
            super(String.format(Locale.ROOT, "Insufficient funds: available=%.2f, requested=%.2f",
                    available, requested));
            this.available = available;
            this.requested = requested;
        }

        InsufficientFundsException(double available, double requested, Throwable cause) {
            super(String.format(Locale.ROOT, "Insufficient funds: available=%.2f, requested=%.2f",
                    available, requested), cause);
            this.available = available;
            this.requested = requested;
        }

        double getAvailable() { return available; }
        double getRequested() { return requested; }
        double getShortfall() { return requested - available; }
    }

    static class UserNotFoundException extends Exception {
        private final String userId;

        UserNotFoundException(String userId) {
            super("User not found: " + userId);
            this.userId = userId;
        }

        UserNotFoundException(String userId, Throwable cause) {
            super("User not found: " + userId, cause);
            this.userId = userId;
        }

        String getUserId() { return userId; }
    }

    static class OrderException extends RuntimeException {
        OrderException(String message)                  { super(message); }
        OrderException(String message, Throwable cause) { super(message, cause); }
    }

    static class OrderValidationException extends OrderException {
        private final String field;
        private final Object rejectedValue;

        OrderValidationException(String field, Object rejectedValue, String reason) {
            super("Validation failed for field '" + field + "': " + reason);
            this.field = field;
            this.rejectedValue = rejectedValue;
        }

        String getField()         { return field; }
        Object getRejectedValue() { return rejectedValue; }
    }

    static class OrderService {

        void placeOrder(String itemName, int quantity, double balance) {
            if (itemName == null || itemName.isBlank())
                throw new OrderValidationException("itemName", itemName, "cannot be blank");
            if (quantity <= 0)
                throw new OrderValidationException("quantity", quantity, "must be positive");

            double cost = quantity * 25.0;
            if (cost > balance)
                throw new InsufficientFundsException(balance, cost);

            System.out.println("  Order placed: " + quantity + "x " + itemName + " for $" + cost);
        }

        String findUser(String userId) throws UserNotFoundException {
            if (userId.startsWith("UNKNOWN"))
                throw new UserNotFoundException(userId);
            return "User[" + userId + "]";
        }
    }

    public static void main(String[] args) {
        OrderService service = new OrderService();

        System.out.println("--- unchecked: InsufficientFundsException ---");
        try {
            service.placeOrder("Widget", 10, 50.0);
        } catch (InsufficientFundsException e) {
            System.out.println("  caught: " + e.getMessage());
            System.out.printf(Locale.ROOT, "  shortfall: $%.2f%n", e.getShortfall());
        }

        System.out.println("\n--- validation exception with field context ---");
        try {
            service.placeOrder("", 3, 1000.0);
        } catch (OrderValidationException e) {
            System.out.println("  caught: " + e.getMessage());
            System.out.println("  field: " + e.getField()
                    + ", value: '" + e.getRejectedValue() + "'");
        }

        System.out.println("\n--- checked: UserNotFoundException ---");
        try {
            System.out.println("  " + service.findUser("UNKNOWN-123"));
        } catch (UserNotFoundException e) {
            System.out.println("  caught: " + e.getMessage());
            System.out.println("  userId: " + e.getUserId());
        }

        System.out.println("\n--- successful order ---");
        try {
            service.placeOrder("Gadget", 2, 500.0);
        } catch (OrderException e) {
            System.out.println("  unexpected: " + e.getMessage());
        }
    }
}
