package com.vbforge.practice.practice_15_parallel_stream_basics;

import java.util.stream.LongStream;

/** parallel() on a LongStream: the same sum as the sequential stream, computed in chunks. */
public class ParallelSumDemo {

    public static void main(String[] args) {
        long sequential = LongStream.rangeClosed(1, 1_000_000).sum();
        long parallel = LongStream.rangeClosed(1, 1_000_000).parallel().sum();

        System.out.println("sequential = " + sequential + ", parallel = " + parallel);
        // sequential = 500000500000, parallel = 500000500000
    }
}
