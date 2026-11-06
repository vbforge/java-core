package com.vbforge.practice.practice_13_generic_record_patterns;

import com.vbforge.practice.practice_13_generic_record_patterns.Outcome.Err;
import com.vbforge.practice.practice_13_generic_record_patterns.Outcome.Ok;

/** Record patterns over a generic sealed type: case Ok<T>(var v) binds the value as a T. */
public class OutcomeUnwrapDemo {

    static <T> T unwrapOr(Outcome<T> outcome, T fallback) {
        return switch (outcome) {
            case Ok<T>(var v)  -> v;
            case Err<T>(var m) -> fallback;
        };
    }

    public static void main(String[] args) {
        Outcome<Integer> ok  = new Ok<>(42);
        Outcome<Integer> err = new Err<>("boom");
        System.out.println("unwrapOr(ok, -1)  = " + unwrapOr(ok, -1));
        System.out.println("unwrapOr(err, -1) = " + unwrapOr(err, -1));
    }
}
