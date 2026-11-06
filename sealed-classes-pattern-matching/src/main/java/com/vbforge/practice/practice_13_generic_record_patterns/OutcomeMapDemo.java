package com.vbforge.practice.practice_13_generic_record_patterns;

import java.util.function.Function;
import com.vbforge.practice.practice_13_generic_record_patterns.Outcome.Err;
import com.vbforge.practice.practice_13_generic_record_patterns.Outcome.Ok;

/**
 * Rebuilding a generic sealed value with a NEW type argument: Outcome<T> becomes Outcome<R>.
 * An Err is re-created (its message is kept) because Err<T> is not an Err<R>.
 */
public class OutcomeMapDemo {

    static <T, R> Outcome<R> map(Outcome<T> outcome, Function<T, R> mapper) {
        return switch (outcome) {
            case Ok<T>(var v)  -> new Ok<>(mapper.apply(v));
            case Err<T>(var m) -> new Err<>(m);
        };
    }

    public static void main(String[] args) {
        Outcome<Integer> ok  = new Ok<>(21);
        Outcome<Integer> err = new Err<>("boom");

        Outcome<Integer> doubled = map(ok, n -> n * 2);
        Outcome<String>  text    = map(ok, n -> "value=" + n);
        Outcome<String>  failed  = map(err, n -> "value=" + n);

        System.out.println(doubled);
        System.out.println(text);
        System.out.println(failed);
    }
}
