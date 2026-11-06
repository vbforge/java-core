package com.vbforge.practice.practice_15_parallel_stream_basics;

import java.util.Arrays;
import java.util.List;

/** parallelStream() / parallel() / sequential(): the LAST call decides for the whole pipeline. */
public class LastCallWinsDemo {

    public static void main(String[] args) {
        List<Integer> list = Arrays.asList(1, 2, 3);

        System.out.println("parallelStream():                " + list.parallelStream().isParallel());
        System.out.println("stream().parallel():             " + list.stream().parallel().isParallel());
        System.out.println("parallel().map().sequential():   "
                + list.stream().parallel().map(n -> n * 2).sequential().isParallel()); // even the map runs sequentially
        // true / true / false
    }
}
