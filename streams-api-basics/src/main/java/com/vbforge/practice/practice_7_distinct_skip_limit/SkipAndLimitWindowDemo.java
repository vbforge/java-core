package com.vbforge.practice.practice_7_distinct_skip_limit;

import java.util.Arrays;
import java.util.List;

/** skip + limit = a window over the stream (the idea behind pagination). */
public class SkipAndLimitWindowDemo {

    public static void main(String[] args) {
        List<String> words = Arrays.asList("stream", "api", "lambda", "java", "filter", "map", "api");

        System.out.print("skip2 limit3: ");
        words.stream()
                .skip(2)
                .limit(3)
                .forEach(w -> System.out.print(w + " "));
        System.out.println();
        // lambda java filter
    }
}
