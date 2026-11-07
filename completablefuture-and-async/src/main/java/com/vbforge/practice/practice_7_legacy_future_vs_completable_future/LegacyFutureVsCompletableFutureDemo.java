package com.vbforge.practice.practice_7_legacy_future_vs_completable_future;

import java.util.concurrent.*;

/**
 * Demonstrates the evolution from java.util.concurrent.Future to CompletableFuture.
 * 
 * Why was CompletableFuture needed?
 * 1. Future.get() is BLOCKING. You cannot say "do X when the result arrives".
 * 2. Future has NO COMPOSITION. You cannot chain "when A finishes, pass it to B".
 * 3. Future has NO CALLBACKS for exception handling. You must wait for .get() 
 *    and catch ExecutionException.
 * 
 * Key Takeaway for Junior/Middle Developers:
 * - Future is a "pull" model (you pull the result by waiting).
 * - CompletableFuture is a "push" model (the result pushes itself to callbacks).
 */
public class LegacyFutureVsCompletableFutureDemo {

    // A simulated blocking service call (e.g., HTTP request or DB query)
    private static String fetchData(String source) {
        sleep(500);
        if (source == null) throw new IllegalArgumentException("Source cannot be null");
        return "Data from " + source;
    }

    public static void main(String[] args) throws Exception {
        System.out.println("=== Legacy Future vs CompletableFuture ===\n");

        ExecutorService executor = Executors.newFixedThreadPool(4);

        // ──────────────────────────────────────────────────────────────────────
        // 1. LEGACY FUTURE: Blocking and hard to chain
        // ──────────────────────────────────────────────────────────────────────
        System.out.println("-- 1. Legacy Future (Blocking) --");
        
        Future<String> legacyFuture = executor.submit(() -> fetchData("Legacy API"));
        
        // The thread BLOCKS here until the result is ready.
        // We cannot do anything else in this pipeline.
        String legacyResult = legacyFuture.get(); 
        System.out.println("Legacy result: " + legacyResult.toUpperCase());
        System.out.println("Notice: The main thread was blocked while waiting.\n");


        // ──────────────────────────────────────────────────────────────────────
        // 2. BRIDGING: Wrapping a legacy Future into a CompletableFuture
        // ──────────────────────────────────────────────────────────────────────
        System.out.println("-- 2. Bridging Legacy to CompletableFuture --");
        
        Future<String> anotherLegacyFuture = executor.submit(() -> fetchData("Bridged API"));

        // If you have legacy code returning Future<T>, you can wrap it:
        CompletableFuture<String> bridgedFuture = CompletableFuture.supplyAsync(() -> {
            try {
                return anotherLegacyFuture.get(); // Still blocks, but inside a pool thread
            } catch (Exception e) {
                throw new CompletionException(e);
            }
        }, executor);

        // Now we can use the modern non-blocking API on the bridged future!
        bridgedFuture
            .thenApply(String::toUpperCase)
            .thenAccept(res -> System.out.println("Bridged result: " + res))
            .join(); // join() is like get() but throws unchecked CompletionException
        
        System.out.println("Notice: The main thread did NOT block for the transformation.\n");


        // ──────────────────────────────────────────────────────────────────────
        // 3. MODERN COMPLETABLEFUTURE: Non-blocking Pipeline
        // ──────────────────────────────────────────────────────────────────────
        System.out.println("-- 3. Modern CompletableFuture Pipeline --");
        
        CompletableFuture
            .supplyAsync(() -> fetchData("Modern API"), executor)
            .thenApply(String::toUpperCase)
            .thenApply(data -> data + " [Processed]")
            .thenAccept(result -> System.out.println("Modern result: " + result))
            .join();
        
        System.out.println("Notice: The pipeline was built declaratively.\n");


        // ──────────────────────────────────────────────────────────────────────
        // 4. EXCEPTION HANDLING COMPARISON
        // ──────────────────────────────────────────────────────────────────────
        System.out.println("-- 4. Exception Handling --");

        // ❌ Legacy: Verbose try-catch, often need to unwrap ExecutionException
        try {
            Future<String> failedLegacy = executor.submit(() -> fetchData(null));
            failedLegacy.get();
        } catch (ExecutionException e) {
            System.out.println("Legacy caught: " + e.getCause().getMessage());
        }

        // ✅ Modern: Declarative recovery in the pipeline (no try-catch needed)
        CompletableFuture
            .supplyAsync(() -> fetchData(null), executor)
            .exceptionally(ex -> "Fallback Data (Handled natively)")
            .thenAccept(result -> System.out.println("Modern handled: " + result))
            .join();

        // Cleanup
        executor.shutdown();
        System.out.println("\nMain done.");
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}