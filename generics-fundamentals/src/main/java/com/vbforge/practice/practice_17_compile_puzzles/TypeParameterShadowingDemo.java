package com.vbforge.practice.practice_17_compile_puzzles;

/**
 * Will this compile? The constructor below has its own type parameter X that shadows
 * the class X. It compiles fine - legal but pointless. A reminder that type parameters
 * live in their own scope.
 */
public class TypeParameterShadowingDemo {

    public static void main(String[] args) {
        @SuppressWarnings("unused")
        X x = new X("anything");
    }

    static class X {
        // inside the constructor, X is the TYPE PARAMETER (inferred as String here), not the class X
        public <X> X(X x) {
            System.out.println("constructed with a " + x.getClass().getSimpleName());
        }
    }
}
