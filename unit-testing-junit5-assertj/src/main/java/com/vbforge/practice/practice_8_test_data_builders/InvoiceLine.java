package com.vbforge.practice.practice_8_test_data_builders;

public record InvoiceLine(String description, int quantity, long unitPriceCents) {

    public InvoiceLine {
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be positive");
        }
        if (unitPriceCents < 0) {
            throw new IllegalArgumentException("unit price must not be negative");
        }
    }

    public long totalCents() {
        return quantity * unitPriceCents;
    }
}
