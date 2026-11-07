package com.vbforge.practice.practice_5_finally_gotchas;

/**
 * System.exit() is the ONE thing that prevents `finally` from running.
 *
 * try/catch/finally is guaranteed to run `finally` in every other case:
 *   - exception thrown and caught → finally runs
 *   - exception thrown and not caught → finally runs, then propagates
 *   - return from try → finally runs, then returns
 *   - System.exit() called → JVM halts, finally does NOT run
 *
 * afterExit() terminates the JVM (exit status 1), so main() calls it only when
 * the program is started with the argument `--exit`.
 */
public class SystemExitSkipsFinallyDemo {

    public static void main(String[] args) {
        System.out.println("--- process BEFORE exit ---");
        beforeExit();

        System.out.println("\n--- process AFTER exit: finally never runs ---");
        if (args.length > 0 && args[0].equals("--exit")) {
            afterExit();
        } else {
            System.out.println("  skipped (it would terminate the JVM) — run with the argument --exit to see it");
        }
    }

    static void beforeExit() {
        try {
            System.out.println("  try");
            int[] arr = new int[1];
            System.out.println(arr[5]);       // AIOOBE
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("  catch");
        } finally {
            System.out.println("  finally — runs even though catch had an exception");
        }
    }

    static void afterExit() {
        try {
            System.out.println("  try");
            throw new RuntimeException("boom");
        } catch (RuntimeException e) {
            System.out.println("  catch (then System.exit — finally will NOT run)");
            System.exit(1);
        } finally {
            System.out.println("  finally — NOT REACHED, JVM has exited");
        }
    }
}
