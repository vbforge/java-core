package com.vbforge.practice.practice_5_finally_gotchas;

/**
 * When `finally` throws, the new exception REPLACES whatever was in flight.
 *
 * Trace:
 *   try    → s = "1"; throws Exception
 *   catch  → s = "12"
 *   finally → s = "123"; doStuff() throws ArithmeticException (divide by zero);
 *              s += "4" is SKIPPED, control leaves the method with ArithmeticException
 *
 * The caller only sees the ArithmeticException thrown from `finally`.
 * This is the worst failure mode of `finally` and is why the
 * "don't throw from finally" rule exists.
 *
 * Expected output: "123ArithmeticException / by zero"
 */
public class FinallyThrowMasksExceptionDemo {

    private static String s = "";

    public static void main(String[] args) {
        try {
            try {
                s += "1";
                throw new Exception("original exception");
            } catch (Exception e) {
                s += "2";
            } finally {
                s += "3";
                doStuff();               // throws ArithmeticException
                s += "4";                // never runs
            }
        } catch (ArithmeticException e) {
            System.out.println(s + e.getClass().getSimpleName() + " " + e.getMessage());
        }
        // s is "123" — the "4" was never appended
    }

    static void doStuff() {
        int x = 0;
        int y = 7 / x;                   // ArithmeticException
    }
}
