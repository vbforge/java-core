package com.vbforge.practice.practice_1_stream_creation;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/** Stream sources: a collection, Stream.of(...) and an empty stream. */
public class CollectionAndValuesStreamDemo {

    public static void main(String[] args) {
        List<String> list = Arrays.asList("a", "b", "c");

        // From a collection
        Stream<String> fromList = list.stream();
        System.out.print("from list:  ");
        fromList.forEach(s -> System.out.print(s + " "));
        System.out.println();

        // Stream.of - varargs
        Stream<Integer> ofStream = Stream.of(1, 2, 3, 4, 5);
        System.out.print("Stream.of:  ");
        ofStream.forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Empty stream
        long size = Stream.<String>empty().count();
        System.out.println("empty stream count: " + size);
    }
}
