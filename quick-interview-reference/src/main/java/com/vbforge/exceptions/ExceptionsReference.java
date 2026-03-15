package com.vbforge.exceptions;

import java.io.*;

/**
 * ═══════════════════════════════════════════════════════════════
 *  EXCEPTIONS INTERVIEW REFERENCE
 *  Topics: hierarchy · checked vs unchecked · try-with-resources
 *          exception chaining · common gotchas
 * ═══════════════════════════════════════════════════════════════
 */
@SuppressWarnings("all")
public class ExceptionsReference {

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 1. EXCEPTION HIERARCHY
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Throwable
     *  ├── Error                      ← JVM-level, don't catch (OutOfMemoryError, StackOverflowError)
     *  └── Exception
     *      ├── RuntimeException       ← UNCHECKED — compiler doesn't require handling
     *      │   ├── NullPointerException
     *      │   ├── IllegalArgumentException
     *      │   ├── IllegalStateException
     *      │   ├── ClassCastException
     *      │   ├── ArrayIndexOutOfBoundsException
     *      │   ├── UnsupportedOperationException
     *      │   └── NumberFormatException
     *      └── (others)               ← CHECKED — must be declared or caught
     *          ├── IOException
     *          ├── SQLException
     *          └── InterruptedException
     *
     *  CHECKED vs UNCHECKED:
     *  Checked:   must be handled (try-catch) or declared (throws) — compile-time enforcement
     *  Unchecked: programmer error — fix the code, don't catch blindly
     *
     *  DESIGN RULE:
     *  Use checked   → for recoverable conditions caller can reasonably handle
     *  Use unchecked → for programming errors or unrecoverable conditions
     */


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 2. try-with-resources ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Any class implementing AutoCloseable can be used in try-with-resources.
     *  close() is called automatically — even if an exception is thrown.
     *  Multiple resources: closed in REVERSE order of declaration.
     *
     *  ⚠️ SUPPRESSED EXCEPTIONS:
     *  If both try body AND close() throw exceptions:
     *  → the try-body exception is PRIMARY
     *  → the close() exception is SUPPRESSED (attached to primary via addSuppressed)
     *  → Suppressed exceptions accessible via: e.getSuppressed()
     */
    static void tryWithResources() throws Exception {
        // ✅ Single resource
        try (BufferedReader reader = new BufferedReader(new FileReader("file.txt"))) {
            String line = reader.readLine();
        }
        // reader.close() called automatically

        // ✅ Multiple resources — closed in reverse: writer then reader
        try (var reader = new BufferedReader(new FileReader("in.txt"));
             var writer = new BufferedWriter(new FileWriter("out.txt"))) {
            writer.write(reader.readLine());
        }

        // ✅ Custom AutoCloseable
        try (ManagedResource res = new ManagedResource()) {
            res.doWork();
        }
        // ManagedResource.close() called automatically
    }

    static class ManagedResource implements AutoCloseable {
        void doWork() { System.out.println("working"); }
        @Override public void close() { System.out.println("closed"); }
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 3. EXCEPTION CHAINING ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  Always preserve the original cause when wrapping exceptions.
     *  Without cause: stack trace shows the wrapper, root cause is LOST.
     */
    static class ServiceException extends RuntimeException {
        ServiceException(String msg, Throwable cause) { super(msg, cause); }
    }

    static void exceptionChaining() {
        try {
            riskyOperation();
        } catch (IOException e) {
            // ✅ Wrap with cause — root IOException preserved in stack trace
            throw new ServiceException("Service operation failed", e);
            // ❌ NEVER: throw new ServiceException("failed");  — cause lost!
        }
    }

    static void riskyOperation() throws IOException {
        throw new IOException("disk error");
    }


    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // 4. COMMON GOTCHAS ⚠️
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    static void exceptionGotchas() {
        // ⚠️ finally always executes — even with return in try
        System.out.println(withReturn());  // prints "finally", returns "finally-value"

        // ⚠️ catch order matters — more specific FIRST
        try {
            throw new FileNotFoundException("not found");
        } catch (FileNotFoundException e) {   // ✅ specific first
            System.out.println("file not found");
        } catch (IOException e) {             // ✅ general after
            System.out.println("io error");
        }
        // ❌ Reversed order won't compile — FileNotFoundException is dead code after IOException

        // ⚠️ Multi-catch (Java 7+) — pipe syntax
        try {
            if (Math.random() > 0.5) throw new IOException();
            else throw new IllegalArgumentException();
        } catch (IOException | IllegalArgumentException e) {
            // e is effectively final in multi-catch — cannot reassign
            System.out.println("caught: " + e.getClass().getSimpleName());
        }

        // ⚠️ StackOverflowError = recursive call without base case — is an Error, not Exception
        // ⚠️ NullPointerException message in Java 14+ is helpful: "Cannot invoke X because Y is null"
    }

    static String withReturn() {
        try {
            return "try-value";
        } finally {
            System.out.println("finally");
            // return "finally-value";  // ⚠️ This would OVERRIDE the try return! Avoid.
        }
    }

    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    // ANTI-PATTERNS ❌
    // ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
    /*
     *  ❌ catch (Exception e) {}                   → swallowing exceptions silently
     *  ❌ catch (Exception e) { throw e; }         → pointless catch — remove it
     *  ❌ Catching Error or Throwable              → never catch JVM errors
     *  ❌ throw new Exception("msg") without cause → loses original stack trace
     *  ❌ Using exceptions for flow control        → exceptions are expensive (stack trace)
     *  ❌ Logging AND re-throwing same exception   → double logging in caller
     *  ❌ return inside finally                    → overrides try/catch return value
     *  ❌ Not closing resources manually           → use try-with-resources
     */

    public static void main(String[] args) throws Exception {
        exceptionGotchas();
        exceptionChaining();
    }
}
