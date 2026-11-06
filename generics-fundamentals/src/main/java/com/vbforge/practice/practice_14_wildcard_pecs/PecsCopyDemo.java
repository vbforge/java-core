package com.vbforge.practice.practice_14_wildcard_pecs;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * PECS mnemonic - Producer Extends, Consumer Super:
 *   If you GET from a generic collection -> use extends (produce values OUT)
 *   If you PUT into a generic collection -> use super (consume values IN)
 *   If you both GET and PUT -> use the exact type T
 */
public class PecsCopyDemo {

    public static void main(String[] args) {
        List<Integer> src = Arrays.asList(10, 20, 30);
        List<Number> dest = new ArrayList<>();
        copyTo(src, dest);
        System.out.println("copyTo result: " + dest);  // [10, 20, 30]
    }

    /**
     * PECS in action - copy from source (producer) to dest (consumer).
     * Same signature shape as Collections.copy (that one overwrites dest by index,
     * this one appends).
     */
    public static <T> void copyTo(List<? extends T> src, List<? super T> dest) {
        for (T element : src) {
            dest.add(element);
        }
    }
}
