package com.vbforge.practice.practice_2_array_value_vs_reference;

import java.util.Arrays;

/**
 * For-each gives you the element VALUE (a copy), not the slot.
 * Assigning to the loop variable does NOT modify the array - for primitives
 * and for immutable objects such as {@code String}.
 *
 * <p>Use an index loop when you need to replace elements in place.
 * For mutable objects see {@link ForEachMutatesReferencedObjectsDemo}.
 */
public class ForEachCannotReassignElementsDemo {

    public static void main(String[] args) {
        System.out.println("--- primitives ---");
        int[] nums = {1, 2, 3, 4, 5};
        for (int n : nums) n = n * n;                                   // changes the local copy only
        System.out.println("after for-each : " + Arrays.toString(nums)); // [1, 2, 3, 4, 5]
        for (int i = 0; i < nums.length; i++) nums[i] = nums[i] * nums[i];
        System.out.println("after index   : " + Arrays.toString(nums)); // [1, 4, 9, 16, 25]

        System.out.println("\n--- Strings (immutable) ---");
        String[] words = {"Java", "Docker", "Spring"};
        for (String w : words) w = w + "-11";                           // changes the local copy only
        System.out.println("after for-each : " + Arrays.toString(words)); // unchanged
        for (int i = 0; i < words.length; i++) words[i] = words[i] + "-11";
        System.out.println("after index   : " + Arrays.toString(words)); // [Java-11, Docker-11, Spring-11]
    }
}
