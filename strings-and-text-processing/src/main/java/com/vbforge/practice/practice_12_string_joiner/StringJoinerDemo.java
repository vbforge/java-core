package com.vbforge.practice.practice_12_string_joiner;

import java.util.StringJoiner;

/** StringJoiner(delimiter, prefix, suffix): add() elements, toString() wraps them. */
public class StringJoinerDemo {

    public static void main(String[] args) {
        StringJoiner joiner = new StringJoiner(":", "<<", ">>");
        System.out.println(joiner.add("hello").add("Java").add("hello").toString()); // <<hello:Java:hello>>

        StringJoiner joiner2 = new StringJoiner(":", "<<", ">>");
        System.out.println(joiner2.add("blanc").add("rouge").add("blanc").toString()); // <<blanc:rouge:blanc>>
    }
}
