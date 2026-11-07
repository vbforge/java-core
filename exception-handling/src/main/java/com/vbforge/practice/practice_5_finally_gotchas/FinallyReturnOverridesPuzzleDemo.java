package com.vbforge.practice.practice_5_finally_gotchas;

/**
 * Puzzle: `return` inside `finally` overrides everything else.
 *
 * methodB():
 *   - throws RuntimeException
 *   - catches it → tries to return 0
 *   - finally runs and returns 1 → this wins
 *   so methodB() returns 1.
 *
 * methodA() then sees methodB() > 0 → throws UnknownError,
 * but `catch (Exception)` doesn't catch Errors (UnknownError extends Error).
 * So UnknownError propagates up to main's `catch (Throwable)`.
 *
 * Output: "Main Catch"
 */
public class FinallyReturnOverridesPuzzleDemo {

    public static void main(String[] args) {
        try {
            System.out.println(methodA());
        } catch (Throwable e) {
            System.out.println("Main Catch");
        }
    }

    static int methodA() {
        if (methodB() > 0) {
            try {
                throw new UnknownError();
            } catch (Exception e) {   // never runs — UnknownError is not an Exception
                return 2;
            }
            // no return needed here: the UnknownError propagates out of methodA
        } else {
            return 3;
        }
    }

    static int methodB() {
        try {
            throw new RuntimeException();
        } catch (Exception e) {
            return 0;                 // would return 0...
        } finally {
            return 1;                 // ...but finally wins
        }
    }
}
