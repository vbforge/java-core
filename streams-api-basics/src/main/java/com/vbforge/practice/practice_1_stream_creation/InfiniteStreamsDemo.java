package com.vbforge.practice.practice_1_stream_creation;

import java.util.Random;
import java.util.stream.Stream;

/** Infinite streams (generate, iterate) must be cut with limit(). */
public class InfiniteStreamsDemo {

    public static void main(String[] args) {
        // Stream.generate - infinite, supplier based (seeded Random keeps the output deterministic)
        Random random = new Random(42);
        Stream<Integer> randoms = Stream.generate(() -> random.nextInt(100)).limit(3);
        System.out.print("generate x3:   ");
        randoms.forEach(n -> System.out.print(n + " "));
        System.out.println();

        // Stream.iterate - infinite sequence: 0, 2, 4, 6, 8
        Stream<Integer> evens = Stream.iterate(0, n -> n + 2).limit(5);
        System.out.print("iterate evens: ");
        evens.forEach(n -> System.out.print(n + " "));
        System.out.println();
    }
}
