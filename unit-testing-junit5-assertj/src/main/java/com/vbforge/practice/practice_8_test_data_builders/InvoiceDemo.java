package com.vbforge.practice.practice_8_test_data_builders;

import java.util.List;

public class InvoiceDemo {
    public static void main(String[] args) {
        Invoice invoice = new Invoice(
                new Customer("Alice", "alice@corp.com", true),
                List.of(new InvoiceLine("keyboard", 2, 5_000), new InvoiceLine("mouse", 1, 2_000)),
                20);
        System.out.println("subtotal " + invoice.subtotalCents());   // 12000
        System.out.println("discount " + invoice.discountCents());   // 1200
        System.out.println("vat      " + invoice.vatCents());        // 2160
        System.out.println("total    " + invoice.totalCents());      // 12960
    }
}
