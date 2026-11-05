package com.vbforge.practice.practice_17_regex_groups_and_lazy_replacement;

import java.util.regex.Pattern;

/** Lazy quantifier {@code .*?} stops at the first word boundary, so "L.*?\\b" matches a whole word that starts with a capital L (case-sensitive: "lana" is untouched). */
public class LazyQuantifierReplaceDemo {

    public static void main(String[] args) {
        String str = "Lama, Stiv, Lulu, Nata, lana ";
        System.out.println(Pattern.compile("L.*?\\b").matcher(str).replaceAll("XXX"));
    }
}
