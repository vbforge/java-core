package com.vbforge.practice.practice_1_flat_map;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/** flatMap(Collection::stream) flattens a List&lt;List&lt;Integer&gt;&gt; into one List&lt;Integer&gt;. */
public class FlatMapNestedListsDemo {

    public static void main(String[] args) {
        List<List<Integer>> nested = Arrays.asList(
                Arrays.asList(1, 2, 3),
                Arrays.asList(4, 5),
                Arrays.asList(6, 7, 8, 9)
        );

        List<Integer> flat = nested.stream()
                .flatMap(Collection::stream)
                .collect(Collectors.toList());

        System.out.println("flattened: " + flat);
        // flattened: [1, 2, 3, 4, 5, 6, 7, 8, 9]
    }
}
