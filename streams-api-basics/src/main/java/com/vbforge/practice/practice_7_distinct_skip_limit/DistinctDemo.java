package com.vbforge.practice.practice_7_distinct_skip_limit;

import java.util.Arrays;
import java.util.List;

/** distinct() removes duplicates using equals/hashCode and keeps the first occurrence. */
public class DistinctDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("distinct: ");
        words.stream()
                .distinct()
                .forEach(w -> System.out.print(w + " "));
        System.out.println();
        // stream api lambda java filter map
    }
}
