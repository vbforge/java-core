package com.vbforge.practice.practice_8_test_data_builders;

import java.util.ArrayList;
import java.util.List;

/**
 * Test Data Builder: sensible defaults for everything, each test overrides ONLY what it is about.
 * Lives in src/test, it is test infrastructure, not production code.
 */
public final class InvoiceBuilder {

    private Customer customer = CustomerMother.regularCustomer();
    private final List<InvoiceLine> lines = new ArrayList<>();
    private int vatPercent = 0;

    private InvoiceBuilder() {
    }

    public static InvoiceBuilder anInvoice() {
        return new InvoiceBuilder();
    }

    public InvoiceBuilder forCustomer(Customer customer) {
        this.customer = customer;
        return this;
    }

    public InvoiceBuilder withLine(String description, int quantity, long unitPriceCents) {
        lines.add(new InvoiceLine(description, quantity, unitPriceCents));
        return this;
    }

    public InvoiceBuilder withVat(int percent) {
        this.vatPercent = percent;
        return this;
    }

    public Invoice build() {
        if (lines.isEmpty()) {
            lines.add(new InvoiceLine("default item", 1, 1_000));   // valid by default
        }
        return new Invoice(customer, lines, vatPercent);
    }
}
