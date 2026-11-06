package com.vbforge.practice.practice_10_partitioning_by;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/** partitioningBy ALWAYS has both keys; groupingBy only has the keys that occur. */
public class PartitioningEmptyStreamDemo {

    public static void main(String[] args) {
        System.out.println(Stream.<Integer>empty().collect(Collectors.partitioningBy(n -> n > 0)));
        // {false=[], true=[]}
        System.out.println(Stream.<Integer>empty().collect(Collectors.groupingBy(n -> n > 0)));
        // {}
    }
}
