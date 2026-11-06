package com.vbforge.practice.practice_7_generic_key_value;

/**
 * Arrays of a parameterized type cannot be created directly. The workaround:
 * create the array of the raw class and cast it (unchecked, but safe while the
 * array only ever receives KeyValueImpl&lt;Integer, Double&gt; elements).
 */
public class GenericArrayCreationDemo {

    @SuppressWarnings({"unchecked", "rawtypes"})   // both are intentional: the raw array is the workaround
    public static void main(String[] args) {
        // problem:
        // KeyValue<Integer, Double>[] pairs = new KeyValueImpl<Integer, Double>[5]; // compile error: generic array creation

        // solution: create the array of the raw class and cast it
        KeyValue<Integer, Double>[] pairs = (KeyValue<Integer, Double>[]) new KeyValueImpl[5];

        // populate the array with KeyValueImpl instances
        pairs[0] = new KeyValueImpl<>(1, 1.1);
        pairs[1] = new KeyValueImpl<>(2, 2.2);
        pairs[2] = new KeyValueImpl<>(3, 3.3);
        pairs[3] = new KeyValueImpl<>(4, 4.4);
        pairs[4] = new KeyValueImpl<>(5, 5.5);

        // print the keys and values
        for (KeyValue<Integer, Double> pair : pairs) {
            System.out.println("Key: " + pair.getKey() + ", Value: " + pair.getValue());
        }
    }
}
