package com.vbforge.practice.practice_4_exception_handling;

import java.util.concurrent.CompletableFuture;

public class ExceptionHandlingDemo {

    public static void main(String[] args) {
        // exceptionally: Recover with a default value
        String recovered = CompletableFuture.supplyAsync(() -> {
            if (true) throw new IllegalStateException("Boom");
            return "Success";
        }).exceptionally(ex -> "Fallback Value").join();
        
        System.out.println("Recovered: " + recovered);

        // handle: Always runs (success or failure). Good for transforming.
        String handled = CompletableFuture.supplyAsync(() -> "Success")
            .handle((result, ex) -> {
                if (ex != null) return "Error handled";
                return result.toUpperCase();
            }).join();

        System.out.println("Handled: " + handled);

        // whenComplete: Side effect (logging/cleanup), re-throws exception
        try {
            CompletableFuture.supplyAsync(() -> {
                throw new RuntimeException("Fatal Error");
            }).whenComplete((res, ex) -> {
                System.out.println("Cleanup executed. Error was: " + (ex != null ? ex.getMessage() : "none"));
            }).join();
        } catch (Exception e) {
            System.out.println("Caught at the end: " + e.getCause());
        }
    }
}