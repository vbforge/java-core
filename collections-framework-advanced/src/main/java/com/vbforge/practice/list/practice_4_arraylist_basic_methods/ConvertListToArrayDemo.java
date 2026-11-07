package com.vbforge.practice.list.practice_4_arraylist_basic_methods;

import java.util.Arrays;
import java.util.List;

public class ConvertListToArrayDemo {
    public static void main(String[] args) {

        List<Integer> list = List.of(1,2,3,4,5);

        convert1(list);

        convert2(list);

        convert3(list);

    }

    private static void convert3(List<Integer> list) {
        Integer[] array = list.toArray(new Integer[0]);
        for (Integer integer : array) {
            System.out.print(integer + " ");
        }
        System.out.println();
    }

    private static void convert2(List<Integer> list) {
        Object[] array1 = list.toArray();
        System.out.println(Arrays.toString(array1));
    }

    private static void convert1(List<Integer> list) {
        int[] array = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            array[i] = list.get(i);
        }

        System.out.println(Arrays.toString(array));
    }
}
