package com.vbforge.practice.compose.practice_2_then_apply_accept_run;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * The ends of a pipeline:
 *   thenAccept(consumer)  uses the result, returns CompletableFuture<Void>
 *   thenRun(runnable)     does not need the result at all, returns CompletableFuture<Void>
 */
public class ThenAcceptThenRunDemo {

    public static void main(String[] args) {

        List<String> log = Collections.synchronizedList(new ArrayList<>());

        CompletableFuture<Void> pipeline = CompletableFuture
                .supplyAsync(() -> "order-42")
                .thenAccept(id -> log.add("saved " + id))
                .thenRun(() -> log.add("notified"));

        pipeline.join();
        System.out.println(log); // [saved order-42, notified]
    }
}
