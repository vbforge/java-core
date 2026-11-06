package com.vbforge.practice.practice_12_wildcard_upper_bound;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Upper-bounded wildcard {@code ? extends Number}:
 *   - accepts List&lt;Integer&gt;, List&lt;Double&gt;, List&lt;Number&gt;
 *   - READ is fine (you get a Number); ADD is not (the exact element type is unknown)
 *   - methods that take Object (remove, contains) still work
 *   - PRODUCER - produces Number objects out of the list
 */
public class UpperBoundedWildcardDemo {

    public static void main(String[] args) {

        // ================== Scene 1: reading through ? extends Number ==================
        List<Integer> ints = Arrays.asList(1, 2, 3, 4, 5);
        List<Double> doubles = Arrays.asList(1.5, 2.5, 3.0);
        List<Number> numbers = Arrays.asList(1, 2.5, 3L);
        System.out.println("sum(ints):    " + sumList(ints));     // 15.0
        System.out.println("sum(doubles): " + sumList(doubles));  // 7.0
        System.out.println("sum(numbers): " + sumList(numbers));  // 6.5

        // ================== Scene 2: remove(Object) is allowed, add(E) is not ==================
        List<Integer> removable = new ArrayList<>(Arrays.asList(1, 2, 3));
        System.out.println("removed 2? " + removeOne(removable, 2) + " -> " + removable);
    }

    public static double sumList(List<? extends Number> list) {
        double sum = 0;
        for (Number n : list) sum += n.doubleValue();  // reading is OK
        // list.add(1.0);  // COMPILE ERROR - can't add to ? extends
        return sum;
    }

    // remove(Object) takes Object, not the unknown element type, so it compiles on a ? extends list
    public static boolean removeOne(List<? extends Number> list, Number n) {
        // list.add(n);  // COMPILE ERROR
        return list.remove(n);
    }
}
