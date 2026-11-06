package com.vbforge.practice.practice_17_parallel_safe_accumulation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Never add to a shared non-thread-safe list from a parallel stream: elements get lost or duplicated and
 * exceptions can occur, differently on every run (so that variant is NOT run here):
 *     List&lt;Integer&gt; out = new ArrayList&lt;&gt;(); IntStream.range(0, 10_000).parallel().forEach(out::add);
 * collect builds a private container per chunk and merges them: ordered and complete.
 */
public class CollectInsteadOfSharedListDemo {

    public static void main(String[] args) {
        List<Integer> result = IntStream.range(0, 10_000).boxed().parallel().collect(Collectors.toList());

        System.out.println("size = " + result.size() + ", first = " + result.get(0) + ", last = " + result.get(9_999));
        // size = 10000, first = 0, last = 9999
    }
}
