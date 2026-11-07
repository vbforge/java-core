package com.vbforge.practice.practice_2_catch_order;

/**
 * Puzzle: catch order with a custom exception hierarchy.
 *
 * `ExceptionTwo` extends `ExceptionOne`, so `catch (ExceptionTwo)` must come
 * before `catch (ExceptionOne)` — otherwise it's unreachable.
 *
 * `method()` is declared to throw `ExceptionOne` in the abstract base, but
 * the override throws the narrower `ExceptionTwo` — legal per the override
 * throws rules (see practice_8_override_throws_rules).
 *
 * Output: 0 1 0 1
 *   a = 0 (not incremented — exception thrown first)
 *   b = 1 (ExceptionTwo catch runs)
 *   c = 0 (ExceptionOne catch never reached — javac even warns "unreachable catch clause")
 *   d = 1 (finally sums)
 */
public class CatchOrderPuzzleDemo {

    static class ExceptionOne extends Exception { }
    static class ExceptionTwo extends ExceptionOne { }

    static abstract class Abstract {
        abstract void method() throws ExceptionOne;
    }

    static int a, b, c, d;

    static class Concrete extends Abstract {
        @Override
        void method() throws ExceptionTwo {
            throw new ExceptionTwo();
        }
    }

    public static void main(String[] args) {
        Concrete concrete = new Concrete();
        try {
            concrete.method();
            a++;
        } catch (ExceptionTwo e) {        // must come first
            b++;
        } catch (ExceptionOne e) {
            c++;
        } finally {
            d = a + b + c;
        }
        System.out.println(a + " " + b + " " + c + " " + d);    // 0 1 0 1
    }
}
