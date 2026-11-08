package com.vbforge.practice.practice_8_test_data_builders;

import java.util.List;

/**
 * Immutable invoice. VIP customers get 10% off the subtotal, VAT is charged on the discounted amount.
 * Real objects like this have many required fields: building them by hand in every test is noise.
 */
public final class Invoice {

    private final Customer customer;
    private final List<InvoiceLine> lines;
    private final int vatPercent;

    public Invoice(Customer customer, List<InvoiceLine> lines, int vatPercent) {
        if (customer == null) {
            throw new IllegalArgumentException("customer is required");
        }
        if (lines == null || lines.isEmpty()) {
            throw new IllegalArgumentException("at least one line is required");
        }
        if (vatPercent < 0 || vatPercent > 100) {
            throw new IllegalArgumentException("vat must be in 0..100");
        }
        this.customer = customer;
        this.lines = List.copyOf(lines);
        this.vatPercent = vatPercent;
    }

    public Customer customer() {
        return customer;
    }

    public List<InvoiceLine> lines() {
        return lines;
    }

    public long subtotalCents() {
        return lines.stream().mapToLong(InvoiceLine::totalCents).sum();
    }

    public long discountCents() {
        return customer.vip() ? subtotalCents() / 10 : 0;
    }

    public long vatCents() {
        long taxable = subtotalCents() - discountCents();
        return (taxable * vatPercent + 50) / 100;   // HALF_UP on whole cents
    }

    public long totalCents() {
        return subtotalCents() - discountCents() + vatCents();
    }
}
