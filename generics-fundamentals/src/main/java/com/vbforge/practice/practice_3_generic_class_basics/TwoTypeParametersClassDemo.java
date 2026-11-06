package com.vbforge.practice.practice_3_generic_class_basics;

/**
 * A generic class with TWO independent type parameters: Pair&lt;T1, T2&gt;.
 */
public class TwoTypeParametersClassDemo {

    public static void main(String[] args) {
        Pair<Integer, String> worldCup = new Pair<>(2022, "Qatar");
        System.out.println("World Cup " + worldCup.getObj1() + " in " + worldCup.getObj2());

        // Type safety - the compiler rejects: Pair<Integer, String> bad = new Pair<>("Qatar", 2022);
    }

    static class Pair<T1, T2> {

        T1 obj1;
        T2 obj2;

        public Pair(T1 obj1, T2 obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
        }

        public T1 getObj1() {
            return obj1;
        }

        public T2 getObj2() {
            return obj2;
        }
    }
}
