package com.vbforge.practice.practice_6_timeouts_and_racing;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class TimeoutsAndRacingDemo {

    public static void main(String[] args) {
        // Java 9+ Timeout feature
        System.out.println("--- Java 9+ Timeouts ---");
        
        CompletableFuture<String> slowTask = CompletableFuture.supplyAsync(() -> {
            sleep(1000);
            return "Slow Data";
        });

        // 1. completeOnTimeout: Returns a default value if time runs out
        String result1 = slowTask.completeOnTimeout("Default Data", 200, TimeUnit.MILLISECONDS).join();
        System.out.println("completeOnTimeout result: " + result1);

        // 2. orTimeout: Throws a TimeoutException if time runs out
        try {
            CompletableFuture.supplyAsync(() -> {
                sleep(1000);
                return "Slow Data";
            }).orTimeout(200, TimeUnit.MILLISECONDS).join();
        } catch (Exception e) {
            System.out.println("orTimeout caught: " + e.getCause().getClass().getSimpleName());
        }
        
        // Note: If you are targeting strictly Java 8, you will need to implement the
        // ScheduledExecutorService + applyToEither pattern here.
    }

    private static void sleep(long ms) {
        try { Thread.sleep(ms); } catch (InterruptedException e) {}
    }
}