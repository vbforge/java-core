package com.vbforge.practice.practice_2_primitive_streams;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/** boxed() converts an IntStream back to Stream&lt;Integer&gt;. */
public class BoxedStreamDemo {

    public static void main(String[] args) {
        List<Integer> boxed = IntStream.of(1, 2, 3).boxed().collect(Collectors.toList());
        System.out.println("boxed: " + boxed);
    }
}
