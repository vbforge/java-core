package com.vbforge.practice.practice_1_custom_functional_interface;

/**
 * A custom functional interface.
 *
 * <p>{@code @FunctionalInterface} triggers a compile error if more than one abstract
 * method is declared: it is documentation AND enforcement.
 * Default and static methods are allowed; they do not count toward the limit.
 */
@FunctionalInterface
public interface Transformer<T> {

    T transform(T input);

    // Default method: fine, does not break the single-abstract-method rule
    default Transformer<T> andThen(Transformer<T> after) {
        return input -> after.transform(this.transform(input));
    }
}
