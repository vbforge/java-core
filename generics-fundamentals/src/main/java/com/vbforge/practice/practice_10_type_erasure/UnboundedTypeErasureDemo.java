package com.vbforge.practice.practice_10_type_erasure;

/**
 * Code cleanup (type erasure) on a static generic method: countTypes() counts the
 * elements of an array that are equal to value. The type parameter has no bounds, so the
 * compiler substitutes Object wherever T is used - the erased method is exactly countObjects().
 */
public class UnboundedTypeErasureDemo {

    public static void main(String[] args) {
        System.out.println("=== Object substitution ===");
        System.out.println("countObjects([a,b,a], a) = " + countObjects(new String[]{"a", "b", "a"}, "a"));
        System.out.println("countTypes  ([a,b,a], a) = " + countTypes(new String[]{"a", "b", "a"}, "a"));
    }

    private static int countObjects(Object[] array, Object value) {
        int num = 0;
        for (Object element : array) {
            if (element.equals(value)) {
                num++;
            }
        }
        return num;
    }

    private static <T> int countTypes(T[] array, T value) {
        int num = 0;
        for (T element : array) {
            if (element.equals(value)) {
                num++;
            }
        }
        return num;
    }
}
