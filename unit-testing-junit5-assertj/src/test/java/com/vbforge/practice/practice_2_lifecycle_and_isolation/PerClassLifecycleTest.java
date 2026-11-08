package com.vbforge.practice.practice_2_lifecycle_and_isolation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

/**
 * Technique: @TestInstance(PER_CLASS) = ONE instance for all tests, so fields are shared.
 * Useful for expensive setup (@BeforeAll can be non-static) but it removes isolation:
 * tests become order-dependent. This class makes the dependency explicit with @Order,
 * which is the price you pay. Prefer the default PER_METHOD unless setup is really costly.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PerClassLifecycleTest {

    private final ShoppingCart sharedCart = new ShoppingCart();

    @Test
    @Order(1)
    void first_addsItem() {
        sharedCart.add("book");

        assertThat(sharedCart.size()).isEqualTo(1);
    }

    @Test
    @Order(2)
    void second_seesItemFromFirst() {
        // only true because of PER_CLASS + explicit order: this is the smell to be aware of
        assertThat(sharedCart.items()).containsExactly("book");
    }
}
