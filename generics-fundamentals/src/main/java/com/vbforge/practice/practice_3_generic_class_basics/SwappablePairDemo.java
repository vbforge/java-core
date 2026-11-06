package com.vbforge.practice.practice_3_generic_class_basics;

/**
 * swap() on a generic class returns a NEW pair whose type arguments are flipped:
 * SwappablePair&lt;String, Integer&gt; becomes SwappablePair&lt;Integer, String&gt;.
 */
public class SwappablePairDemo {

    public static void main(String[] args) {
        SwappablePair<String, Integer> sp = new SwappablePair<>("hello", 42);
        System.out.println("Pair: " + sp);
        System.out.println("first: " + sp.first() + ", second: " + sp.second());

        SwappablePair<Integer, String> swapped = sp.swap();
        System.out.println("Swapped: " + swapped);

        // Type safety - the compiler rejects: SwappablePair<String, Integer> bad = new SwappablePair<>(42, "x");
    }

    /**
     * A type-safe Pair - holds two values of potentially different types.
     * The compiler enforces types; no explicit casting needed by callers.
     */
    static class SwappablePair<A, B> {
        private final A first;
        private final B second;

        SwappablePair(A first, B second) {
            this.first = first;
            this.second = second;
        }

        A first() { return first; }

        B second() { return second; }

        // Swap returns a new Pair with types flipped
        SwappablePair<B, A> swap() {
            return new SwappablePair<>(second, first);
        }

        @Override
        public String toString() {
            return "(" + first + ", " + second + ")";
        }
    }
}
