package com.vbforge.practice.practice_2_sealed_result_type;

/** Algebraic type for functional error handling: a Result is either a Success or a Failure. */
public sealed interface Result<T> permits Result.Success, Result.Failure {

    record Success<T>(T value) implements Result<T> {}
    record Failure<T>(String error) implements Result<T> {}

    static <T> Result<T> success(T value)    { return new Success<>(value); }
    static <T> Result<T> failure(String msg) { return new Failure<>(msg); }
}
