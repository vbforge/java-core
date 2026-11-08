package com.vbforge.practice.practice_7_nested_tests_state_machine;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vbforge.practice.practice_7_nested_tests_state_machine.Order.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

/**
 * Technique: @Nested = one inner class per state ("given/when" structure).
 * The outer @BeforeEach runs first, the nested @BeforeEach then moves the order into the state
 * under test, so each test only contains the single action it is about.
 * The IDE shows the tree: Order > when new > can be paid ...
 */
@DisplayName("Order")
class OrderStateTest {

    Order order;

    @BeforeEach
    void createOrder() {
        order = new Order();
    }

    @Nested
    @DisplayName("when new")
    class WhenNew {

        @Test
        void startsInNewState() {
            assertThat(order.status()).isEqualTo(Status.NEW);
        }

        @Test
        void canBePaid() {
            order.pay();

            assertThat(order.status()).isEqualTo(Status.PAID);
        }

        @Test
        void canBeCancelled() {
            order.cancel();

            assertThat(order.status()).isEqualTo(Status.CANCELLED);
        }

        @Test
        void cannotBeShippedOrDelivered() {
            assertThatThrownBy(order::ship).isInstanceOf(IllegalStateException.class)
                    .hasMessage("cannot ship order in state NEW");
            assertThatThrownBy(order::deliver).hasMessage("cannot deliver order in state NEW");
        }
    }

    @Nested
    @DisplayName("when paid")
    class WhenPaid {

        @BeforeEach
        void pay() {
            order.pay();
        }

        @Test
        void canBeShipped() {
            order.ship();

            assertThat(order.status()).isEqualTo(Status.SHIPPED);
        }

        @Test
        void canStillBeCancelled() {
            order.cancel();

            assertThat(order.status()).isEqualTo(Status.CANCELLED);
        }

        @Test
        void cannotBePaidTwice() {
            assertThatThrownBy(order::pay).hasMessage("cannot pay order in state PAID");
        }
    }

    @Nested
    @DisplayName("when shipped")
    class WhenShipped {

        @BeforeEach
        void payAndShip() {
            order.pay();
            order.ship();
        }

        @Test
        void canBeDelivered() {
            order.deliver();

            assertThat(order.status()).isEqualTo(Status.DELIVERED);
        }

        @Test
        void cannotBeCancelledAnymore() {
            assertThatThrownBy(order::cancel).hasMessage("cannot cancel order in state SHIPPED");
            assertThat(order.status()).isEqualTo(Status.SHIPPED);   // failed action keeps the state
        }
    }

    @Nested
    @DisplayName("in a final state")
    class WhenFinished {

        @Test
        void delivered_acceptsNoFurtherAction() {
            order.pay();
            order.ship();
            order.deliver();

            assertThatThrownBy(order::pay).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(order::ship).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(order::deliver).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(order::cancel).isInstanceOf(IllegalStateException.class);
        }

        @Test
        void cancelled_acceptsNoFurtherAction() {
            order.cancel();

            assertThatThrownBy(order::pay).isInstanceOf(IllegalStateException.class);
            assertThatThrownBy(order::cancel).hasMessage("cannot cancel order in state CANCELLED");
        }
    }

    @Test
    void history_recordsEveryStateInOrder() {
        order.pay();
        order.ship();
        order.deliver();

        assertThat(order.history())
                .containsExactly(Status.NEW, Status.PAID, Status.SHIPPED, Status.DELIVERED);
    }
}
