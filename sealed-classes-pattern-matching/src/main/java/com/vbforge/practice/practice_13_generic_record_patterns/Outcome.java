package com.vbforge.practice.practice_13_generic_record_patterns;

/** A generic sealed type: Ok carries a value of type T, Err carries a message. */
public sealed interface Outcome<T> permits Outcome.Ok, Outcome.Err {
    record Ok<T>(T value) implements Outcome<T> {}
    record Err<T>(String message) implements Outcome<T> {}
}
