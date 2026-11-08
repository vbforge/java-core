package com.vbforge.practice.practice_2_lifecycle_and_isolation;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;

/**
 * Technique: lifecycle callbacks and test isolation.
 * Default lifecycle = a NEW test-class instance per test method, so instance fields never leak.
 * Only static state is shared, which is why @BeforeAll / @AfterAll are static.
 */
class ShoppingCartLifecycleTest {

    /** Shared across tests on purpose, only to record the callback order. */
    private static final List<String> LOG = new ArrayList<>();

    private ShoppingCart cart;

    @BeforeAll
    static void beforeAll() {
        LOG.add("beforeAll");
    }

    @AfterAll
    static void afterAll() {
        LOG.add("afterAll");
        // beforeAll once, then (beforeEach, test, afterEach) once per test method
        assertThat(LOG).startsWith("beforeAll").endsWith("afterAll");
        assertThat(LOG.stream().filter("beforeEach"::equals).count())
                .isEqualTo(LOG.stream().filter("afterEach"::equals).count());
    }

    @BeforeEach
    void setUp(TestInfo info) {
        LOG.add("beforeEach");
        cart = new ShoppingCart();   // fresh state for every test
        assertThat(info.getTestMethod()).isPresent();
    }

    @AfterEach
    void tearDown() {
        LOG.add("afterEach");
        cart = null;
    }

    @Test
    void newCart_isEmpty() {
        assertThat(cart.isEmpty()).isTrue();
        assertThat(cart.size()).isZero();
    }

    @Test
    void add_oneItem_cartContainsIt() {
        cart.add("book");

        assertThat(cart.items()).containsExactly("book");
    }

    /**
     * Passes in any order and any number of times because setUp creates a new cart.
     * With a static (shared) cart this test would see the item added by add_oneItem_cartContainsIt.
     */
    @Test
    void stateFromOtherTests_isNeverVisible() {
        assertThat(cart.items()).isEmpty();
    }

    @Test
    void items_returnsReadOnlyView() {
        cart.add("book");

        List<String> view = cart.items();

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> view.add("hack"))
                .isInstanceOf(UnsupportedOperationException.class);
        assertThat(cart.size()).isEqualTo(1);
    }

    @Test
    void remove_missingItem_returnsFalseAndKeepsCart() {
        cart.add("book");

        boolean removed = cart.remove("pen");

        assertThat(removed).isFalse();
        assertThat(cart.items()).containsExactly("book");
    }

    @Test
    void clear_removesEverything() {
        cart.add("book");
        cart.add("pen");

        cart.clear();

        assertThat(cart.isEmpty()).isTrue();
    }

    @org.junit.jupiter.params.ParameterizedTest
    @org.junit.jupiter.params.provider.NullAndEmptySource
    @org.junit.jupiter.params.provider.ValueSource(strings = {"  "})
    void add_blankItem_isRejected(String item) {
        org.assertj.core.api.Assertions.assertThatIllegalArgumentException().isThrownBy(() -> cart.add(item));
        assertThat(cart.isEmpty()).isTrue();
    }
}
