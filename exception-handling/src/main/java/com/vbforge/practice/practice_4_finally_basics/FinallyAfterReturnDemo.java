package com.vbforge.practice.practice_4_finally_basics;

/**
 * `finally` runs after a `return` — but the returned value is already computed.
 *
 *   - voidReturnInTry: `return` in try does not skip finally
 *   - returnCapturedBeforeFinally: finally may change the variable, but the
 *     value that `return` captured earlier is what the caller receives
 */
public class FinallyAfterReturnDemo {

    public static void main(String[] args) {
        System.out.println("--- return in try: finally still runs ---");
        voidReturnInTry();

        System.out.println("\n--- the return value is captured BEFORE finally runs ---");
        System.out.println("  In main: " + returnCapturedBeforeFinally());   // 10
    }

    static void voidReturnInTry() {
        try {
            System.out.println("  try");
            return;
        } finally {
            System.out.println("  finally");
        }
    }

    static int returnCapturedBeforeFinally() {
        int returnVal = 10;
        try {
            String[] students = {"Harry", "Paul"};
            System.out.println(students[5]);          // AIOOBE
        } catch (Exception e) {
            System.out.println("  About to return: " + returnVal);    // 10
            return returnVal;                                         // captures 10 NOW
        } finally {
            returnVal += 10;
            System.out.println("  Return value is now: " + returnVal);  // 20, too late
        }
        return returnVal;                             // never reached: the catch always returns
    }
}
