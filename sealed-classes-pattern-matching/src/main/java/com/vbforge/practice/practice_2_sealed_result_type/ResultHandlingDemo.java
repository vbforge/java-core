package com.vbforge.practice.practice_2_sealed_result_type;

/**
 * Handling a sealed generic type with type patterns. A pattern switch STATEMENT must be
 * exhaustive too, so no default is needed here either.
 */
public class ResultHandlingDemo {

    static <T> void handleResult(Result<T> result) {
        switch (result) {
            case Result.Success<T> s -> System.out.println("  SUCCESS: " + s.value());
            case Result.Failure<T> f -> System.out.println("  FAILURE: " + f.error());
        }
    }

    public static void main(String[] args) {
        Result<Integer> ok  = Result.success(42);
        Result<Integer> err = Result.failure("Value not found");

        handleResult(ok);
        handleResult(err);
    }
}
