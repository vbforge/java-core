package com.vbforge.practice.practice_11_covariance_and_invariance;

/**
 * Arrays are covariant: {@code Child1[]} IS-A {@code Parent[]} at the type level.
 * That lets {@code Parent[] = new Child1[3]} compile, but the compiler only sees a
 * Parent[]; the JVM remembers the real element type (Child1) and rejects a wrong
 * store at RUNTIME with {@link ArrayStoreException}.
 */
public class ArrayCovarianceDemo {

    public static void main(String[] args) {

        // ================== Scene 1: an array of the declared type takes everything ==================
        Parent[] array = new Parent[5];
        array[0] = new Parent();
        array[1] = new Child1();
        array[2] = new Child2();
        System.out.println("array: stored Parent, Child1, Child2");

        // ================== Scene 2: Child1[] viewed as Parent[] ==================
        Parent[] array1 = new Child1[3];   // compiles: arrays are covariant
        array1[1] = new Child1();          // fine
        System.out.println("array1: stored Child1");
        store(array1, 0, new Parent());    // the compiler allows it (array1 looks like Parent[]) - fails at runtime
        store(array1, 2, new Child2());    // same: Child2 is not a Child1

        // ================== Scene 3: Child2[] viewed as Parent[] ==================
        Parent[] array2 = new Child2[3];
        store(array2, 0, new Parent());
        store(array2, 1, new Child1());
        store(array2, 2, new Child2());    // fine
    }

    private static void store(Parent[] array, int index, Parent value) {
        try {
            array[index] = value;
            System.out.println("array of " + array.getClass().getComponentType().getSimpleName() + ", index " + index
                    + " <- " + value.getClass().getSimpleName() + ": ok");
        } catch (ArrayStoreException e) {
            System.out.println("array of " + array.getClass().getComponentType().getSimpleName() + ", index " + index
                    + " <- " + value.getClass().getSimpleName() + ": ArrayStoreException (runtime, not compile time)");
        }
    }

    static class Parent {
    }

    static class Child1 extends Parent {
    }

    static class Child2 extends Parent {
    }
}
