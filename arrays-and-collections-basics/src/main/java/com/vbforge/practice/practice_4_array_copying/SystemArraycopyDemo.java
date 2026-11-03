package com.vbforge.practice.practice_4_array_copying;

import java.util.Arrays;

/**
 * {@code System.arraycopy(src, srcPos, dest, destPos, length)}
 *
 * <ul>
 *   <li>native and fast</li>
 *   <li>does NOT allocate - it copies into an existing destination array</li>
 *   <li>overlapping ranges (same array) are handled correctly</li>
 *   <li>throws IndexOutOfBoundsException on bad indices</li>
 * </ul>
 *
 * Compare with {@link Arrays#copyOf}, which allocates a fresh array.
 */
public class SystemArraycopyDemo {

    public static void main(String[] args) {
        int[] src = {1, 2, 3, 4, 5};
        int[] dest = {10, 20, 30, 40, 50, 60, 70, 80, 90, 100};

        System.arraycopy(src, 0, dest, 0, src.length);
        System.out.println("after arraycopy: " + Arrays.toString(dest));
        // [1, 2, 3, 4, 5, 60, 70, 80, 90, 100]

        System.out.println("\n--- self-overlap (shift right by 1) ---");
        int[] a = {1, 2, 3, 4, 5};
        System.arraycopy(a, 0, a, 1, 4);
        System.out.println("after shift: " + Arrays.toString(a));
        // [1, 1, 2, 3, 4]
    }
}
