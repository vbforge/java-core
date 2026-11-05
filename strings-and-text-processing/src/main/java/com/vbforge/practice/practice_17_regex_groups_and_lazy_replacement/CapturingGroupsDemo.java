package com.vbforge.practice.practice_17_regex_groups_and_lazy_replacement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Capturing groups with greedy (*, +) and optional (?) quantifiers: who gets which characters of "java". */
public class CapturingGroupsDemo {

    public static void main(String[] args) {
        String base = "java";
        groupView(base, "([a-z]*)([a-z]+)");
        groupView(base, "([a-z]?)([a-z]+)");
        groupView(base, "([a-z]+)([a-z]*)");
        groupView(base, "([a-z]?)([a-z]?)");
    }

    static void groupView(String base, String regExp) {
        System.out.println("regex: " + regExp);
        Matcher matcher = Pattern.compile(regExp).matcher(base);
        if (matcher.matches()) {
            System.out.println("group 1: " + matcher.group(1));
            System.out.println("group 2: " + matcher.group(2));
            System.out.println("main group: " + matcher.group() + " [end]"); // same as group(0)
        } else {
            System.out.println("nothing matches");
        }
        System.out.println();
    }
}
