package com.vbforge.practice.practice_15_wildcard_unbounded;

/**
 * Same-type parameter vs wildcard parameter inside a generic class.
 * {@code equalsDivision(Division<T>)} needs the SAME T on both sides - it cannot compare
 * across parameterizations. {@code equalsDiv(Division<?>)} accepts ANY parameterization.
 */
public class WildcardParameterVsSameTypeDemo {

    public static void main(String[] args) {

        // ================== Scene 1: same-type parameter ==================
        Division<Integer> divInteger = new Division<>(10, 20);
        Division<Double> divDouble = new Division<>(11.5, 22.50);
        System.out.println("same T: " + divInteger.equalsDivision(new Division<>(1, 2)));
        // boolean res = divInteger.equalsDivision(divDouble); // ERROR: Division<Double> is not compatible with Division<Integer>

        // ================== Scene 2: wildcard parameter ==================
        Division<Integer> d1 = new Division<>(10, 20);
        Division<Double> d2 = new Division<>(5.5, 1.1);
        System.out.println("equalsDiv(10/20 vs 5.5/1.1): " + d1.equalsDiv(d2));                    // false: 0 vs 5
        System.out.println("equalsDiv(10/5 vs 4.0/2.0):  " + new Division<>(10, 5).equalsDiv(new Division<>(4.0, 2.0)));  // true: 2 vs 2
    }

    static class Division<T extends Number> {

        private final T x;
        private final T y;

        public Division(T x, T y) {
            this.x = x;
            this.y = y;
        }

        public int perform() {
            return (int) (x.doubleValue() / y.doubleValue());
        }

        // requires the SAME T on both sides -> cannot compare across parameterizations
        public boolean equalsDivision(Division<T> data) {
            return (this.perform() == data.perform());
        }

        // the wildcard parameter type accepts any parameterization
        public boolean equalsDiv(Division<?> data) {
            return (this.perform() == data.perform());
        }
    }
}
