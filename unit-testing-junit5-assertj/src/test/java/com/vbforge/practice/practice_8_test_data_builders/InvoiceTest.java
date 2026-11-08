package com.vbforge.practice.practice_8_test_data_builders;

import static com.vbforge.practice.practice_8_test_data_builders.CustomerMother.regularCustomer;
import static com.vbforge.practice.practice_8_test_data_builders.CustomerMother.vipCustomer;
import static com.vbforge.practice.practice_8_test_data_builders.InvoiceBuilder.anInvoice;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;

import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * Technique: Test Data Builder + Object Mother.
 * Compare with `new Invoice(new Customer("x", "y", false), List.of(new InvoiceLine(...)), 0)` in every test:
 * the builder hides irrelevant details, so the reader sees only what the test is about,
 * and a new required constructor argument changes ONE place (the builder) instead of 40 tests.
 */
class InvoiceTest {

    @Test
    void subtotal_sumsQuantityTimesPriceOfAllLines() {
        Invoice invoice = anInvoice()
                .withLine("keyboard", 2, 5_000)
                .withLine("mouse", 1, 2_000)
                .build();

        assertThat(invoice.subtotalCents()).isEqualTo(12_000);
    }

    @Test
    void regularCustomer_getsNoDiscount() {
        Invoice invoice = anInvoice().forCustomer(regularCustomer()).withLine("book", 1, 10_000).build();

        assertThat(invoice.discountCents()).isZero();
    }

    @Test
    void vipCustomer_getsTenPercentOffSubtotal() {
        Invoice invoice = anInvoice().forCustomer(vipCustomer()).withLine("book", 1, 10_000).build();

        assertThat(invoice.discountCents()).isEqualTo(1_000);
        assertThat(invoice.totalCents()).isEqualTo(9_000);
    }

    @Test
    void vat_isChargedOnTheDiscountedAmount() {
        Invoice invoice = anInvoice()
                .forCustomer(vipCustomer())
                .withLine("keyboard", 2, 5_000)
                .withLine("mouse", 1, 2_000)
                .withVat(20)
                .build();

        assertThat(invoice.vatCents()).isEqualTo(2_160);     // 20% of (12000 - 1200)
        assertThat(invoice.totalCents()).isEqualTo(12_960);
    }

    @Test
    void vat_isRoundedHalfUpToWholeCents() {
        Invoice invoice = anInvoice().withLine("pen", 1, 25).withVat(10).build();   // 2.5 cents

        assertThat(invoice.vatCents()).isEqualTo(3);
    }

    @Test
    void defaultBuilder_producesAValidInvoice() {
        Invoice invoice = anInvoice().build();

        assertThat(invoice.lines()).hasSize(1);
        assertThat(invoice.totalCents()).isEqualTo(1_000);
    }

    @Test
    void invoice_doesNotExposeItsInternalList() {
        List<InvoiceLine> source = new java.util.ArrayList<>(List.of(new InvoiceLine("a", 1, 1)));
        Invoice invoice = new Invoice(regularCustomer(), source, 0);

        source.clear();

        assertThat(invoice.lines()).hasSize(1);     // defensive copy
    }

    @Test
    void constructor_withoutLines_isRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Invoice(regularCustomer(), List.of(), 0))
                .withMessageContaining("at least one line");
    }

    @Test
    void line_withZeroQuantity_isRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> new InvoiceLine("x", 0, 100));
    }

    @Test
    void constructor_withoutCustomer_isRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Invoice(null, List.of(new InvoiceLine("a", 1, 1)), 0))
                .withMessageContaining("customer");
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.ValueSource(ints = {-1, 101})
    void constructor_vatOutsideRange_isRejected(int vat) {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Invoice(regularCustomer(), List.of(new InvoiceLine("a", 1, 1)), vat))
                .withMessageContaining("vat");
    }

    @Test
    void line_withNegativePrice_isRejected() {
        assertThatIllegalArgumentException().isThrownBy(() -> new InvoiceLine("x", 1, -1));
    }

    @Test
    void constructor_withNullLines_isRejected() {
        assertThatIllegalArgumentException()
                .isThrownBy(() -> new Invoice(regularCustomer(), null, 0))
                .withMessageContaining("at least one line");
    }

    @Test
    void customer_returnsTheCustomerTheInvoiceWasBuiltFor() {
        Invoice invoice = anInvoice().forCustomer(vipCustomer()).build();

        assertThat(invoice.customer()).isEqualTo(vipCustomer());
    }
}
