// IMPLEMENTATION
package com.vbforge.practice.practice_1_completable_future_basics;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Demonstrates CompletableFuture<T> — Java's composable async programming model.
 *
 * Problems with plain Future<T>:
 *   - get() BLOCKS — no way to attach a callback for when result is ready.
 *   - No composition — cannot chain "when A finishes, run B with its result".
 *   - No exception handling in the pipeline.
 *   - No way to combine multiple futures without blocking on each.
 *
 * CompletableFuture solves all of these:
 *   It is both a Future<T> and a CompletionStage<T>.
 *   CompletionStage defines a rich API for async pipeline construction.
 *
 * Creation:
 *   CompletableFuture.supplyAsync(supplier)          — async, returns value
 *   CompletableFuture.supplyAsync(supplier, executor)— on a specific pool
 *   CompletableFuture.runAsync(runnable)              — async, returns Void
 *   CompletableFuture.completedFuture(value)          — already-done future
 *   CompletableFuture.failedFuture(ex)                — already-failed future
 *   new CompletableFuture<>() + .complete() / .completeExceptionally()
 *
 * Transformation (non-blocking callbacks):
 *   thenApply(fn)       — transform result:  T → U        (same thread or pool)
 *   thenApplyAsync(fn)  — same, forces async execution
 *   thenAccept(consumer)— consume result, return Void
 *   thenRun(runnable)   — run side effect, no access to result
 *
 * Composition (flat-map — when result IS a future):
 *   thenCompose(fn)     — fn returns CompletableFuture<U>; avoids nesting
 *
 * Combining two futures:
 *   thenCombine(other, fn)     — wait for both, combine results
 *   thenAcceptBoth(other, fn)  — wait for both, consume both results
 *   runAfterBoth(other, fn)    — wait for both, then run side effect
 *   applyToEither(other, fn)   — use whichever completes first
 *   acceptEither(other, fn)    — consume whichever completes first
 *
 * Combining many futures:
 *   CompletableFuture.allOf(futures...)  — completes when ALL complete (result = Void)
 *   CompletableFuture.anyOf(futures...)  — completes when ANY completes (result = Object)
 *
 * Exception handling:
 *   exceptionally(fn)            — recover from exception: Throwable → T
 *   handle(fn)                   — always runs: (T result, Throwable ex) → U
 *   whenComplete(fn)             — always runs for side effects; re-throws exception
 *   exceptionallyCompose(fn)     — recover with another CompletableFuture (Java 12+)
 *
 * Timeouts (Java 9+):
 *   orTimeout(duration, unit)        — completes exceptionally with TimeoutException
 *   completeOnTimeout(value, d, u)   — completes with default value on timeout
 *
 * Thread pool:
 *   Without an explicit executor, async stages run on ForkJoinPool.commonPool().
 *   For I/O-bound work, always supply a dedicated executor to avoid starving the
 *   common pool (which is shared by parallel streams and other CF pipelines).
 */
public class CompletableFutureDemo {

    // ── simulated async services ──────────────────────────────────────────────

    /** Simulates a remote service call that takes time and may fail. */
    static CompletableFuture<String> fetchUser(int userId) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency(50);
            if (userId <= 0) throw new IllegalArgumentException("Invalid userId: " + userId);
            return "User-" + userId;
        });
    }

    static CompletableFuture<String> fetchOrders(String user) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency(40);
            return "Orders[" + user + "]";
        });
    }

    static CompletableFuture<Integer> fetchScore(String user) {
        return CompletableFuture.supplyAsync(() -> {
            simulateLatency(30);
            return user.length() * 10;
        });
    }

    private static void simulateLatency(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ── main ──────────────────────────────────────────────────────────────────

    public static void main(String[] args) throws Exception {
        System.out.println("=== CompletableFuture Demo ===\n");

        // --- 1. supplyAsync + thenApply (transform) ---
        System.out.println("-- supplyAsync + thenApply --");
        CompletableFuture<String> upper = CompletableFuture
                .supplyAsync(() -> "hello")
                .thenApply(String::toUpperCase)
                .thenApply(s -> "[" + s + "]");
        System.out.println("Result: " + upper.get());

        // --- 2. thenCompose (flat-map / chaining dependent async calls) ---
        System.out.println("\n-- thenCompose (pipeline) --");
        String orders = fetchUser(1)
                .thenCompose(user -> fetchOrders(user))   // depends on user result
                .get();
        System.out.println("Orders: " + orders);

        // --- 3. thenCombine (independent parallel calls, combine results) ---
        System.out.println("\n-- thenCombine (parallel independent) --");
        // Both run concurrently — total time ≈ max(fetchUser, fetchScore), not sum
        String combined = fetchUser(2)
                .thenCombine(fetchScore("User-2"),
                        (user, score) -> user + " score=" + score)
                .get();
        System.out.println("Combined: " + combined);

        // --- 4. allOf — wait for ALL ---
        System.out.println("\n-- allOf --");
        List<CompletableFuture<String>> all = List.of(
                fetchUser(1), fetchUser(2), fetchUser(3));

        CompletableFuture.allOf(all.toArray(new CompletableFuture[0]))
                .thenRun(() -> System.out.println("All users fetched"))
                .get();

        // Collect results after allOf
        List<String> users = all.stream()
                .map(CompletableFuture::join)   // join() = get() without checked exception
                .collect(Collectors.toList());
        System.out.println("Users: " + users);

        // --- 5. anyOf — take fastest ---
        System.out.println("\n-- anyOf (race) --");
        CompletableFuture<Object> fastest = CompletableFuture.anyOf(
                CompletableFuture.supplyAsync(() -> { simulateLatency(100); return "slow"; }),
                CompletableFuture.supplyAsync(() -> { simulateLatency(10);  return "fast"; }),
                CompletableFuture.supplyAsync(() -> { simulateLatency(200); return "slowest"; })
        );
        System.out.println("Fastest: " + fastest.get());

        // --- 6. exceptionally — recover from failure ---
        System.out.println("\n-- exceptionally (recovery) --");
        String recovered = fetchUser(-1)                      // will throw
                .exceptionally(ex -> "fallback-user(" + ex.getCause().getMessage() + ")")
                .get();
        System.out.println("Recovered: " + recovered);

        // --- 7. handle — always runs (success or failure) ---
        System.out.println("\n-- handle --");
        String handled = fetchUser(-99)
                .handle((result, ex) -> {
                    if (ex != null) return "ERROR: " + ex.getCause().getMessage();
                    return "OK: " + result;
                })
                .get();
        System.out.println("Handled: " + handled);

        // --- 8. whenComplete — side-effect, re-throws ---
        System.out.println("\n-- whenComplete --");
        fetchUser(3)
                .whenComplete((res, ex) -> {
                    if (ex != null) System.out.println("failed: " + ex.getMessage());
                    else            System.out.println("whenComplete saw: " + res);
                })
                .get();

        // --- 9. orTimeout (Java 9+) ---
        System.out.println("\n-- orTimeout --");
        try {
            CompletableFuture
                    .supplyAsync(() -> { simulateLatency(500); return "too slow"; })
                    .orTimeout(100, TimeUnit.MILLISECONDS)
                    .get();
        } catch (ExecutionException ex) {
            System.out.println("Timed out: " + ex.getCause().getClass().getSimpleName());
        }

        // --- 10. completeOnTimeout — default value fallback ---
        System.out.println("\n-- completeOnTimeout --");
        String withDefault = CompletableFuture
                .supplyAsync(() -> { simulateLatency(500); return "real-value"; })
                .completeOnTimeout("default-value", 100, TimeUnit.MILLISECONDS)
                .get();
        System.out.println("Result: " + withDefault);

        // --- 11. Manual completion ---
        System.out.println("\n-- manual complete() --");
        CompletableFuture<String> promise = new CompletableFuture<>();
        new Thread(() -> {
            simulateLatency(30);
            promise.complete("manually completed");
        }).start();
        System.out.println("Promise: " + promise.get());

        System.out.println("\nMain done.");
    }
}