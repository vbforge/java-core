package com.vbforge.practice.practice_10_anonymous_to_lambda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Comparator as a lambda (list sort) and as an anonymous class (array sort), then the same
 * comparator written both ways.
 */
public class ComparatorAnonymousToLambdaDemo {

    public static void main(String[] args) {
        sceneListSort();
        System.out.println();
        sceneArraySort();
    }

    private static void sceneListSort() {
        List<Integer> list = new ArrayList<>();
        list.add(37);
        list.add(-59);
        list.add(83);

        // natural order (ascending)
        Collections.sort(list);
        System.out.println(list);

        // descending, with a lambda
        list.sort((o1, o2) -> Integer.compare(o2, o1));
        System.out.println(list);
    }

    private static void sceneArraySort() {
        String[] tech = {"Java", "Go", "Python", "C++", "Javascript", "Sql", "C#", "Spring", "Angular", "React"};
        Arrays.sort(tech, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                return s1.compareTo(s2);
            }
        });
        System.out.println(Arrays.toString(tech));

        // the same ordering as a lambda, applied descending
        Arrays.sort(tech, (s1, s2) -> s2.compareTo(s1));
        System.out.println(Arrays.toString(tech));
    }
}
