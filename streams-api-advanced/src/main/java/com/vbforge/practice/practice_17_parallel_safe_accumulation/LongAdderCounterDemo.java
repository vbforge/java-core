package com.vbforge.practice.practice_17_parallel_safe_accumulation;

import java.util.concurrent.atomic.LongAdder;
import java.util.stream.IntStream;

/** A thread-safe LongAdder as a shared counter is correct even when updated from a parallel forEach. */
public class LongAdderCounterDemo {

    public static void main(String[] args) {
        LongAdder adder = new LongAdder();
        IntStream.range(0, 10_000).parallel().forEach(i -> adder.increment());

        System.out.println("count = " + adder.sum());
        // count = 10000
    }
}
