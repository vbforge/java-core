package com.vbforge.practice.practice_13_async_basics;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Start ALL requests first, wait afterwards: allOf(...) completes when every future has.
 * Results keep the order of the list, not the order of completion. (Calling join() inside the
 * loop that starts them would make the requests run one after another.)
 */
public class FanOutAllOfDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            int[] delays = {400, 300, 200, 100};

            long start = System.nanoTime();
            List<CompletableFuture<String>> futures = new ArrayList<>();
            for (int ms : delays) {
                HttpRequest request = HttpRequest.newBuilder(server.uri("/slow?ms=" + ms)).build();
                futures.add(client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body));
            }

            CompletableFuture.allOf(futures.toArray(new CompletableFuture<?>[0])).join();
            long elapsedMs = (System.nanoTime() - start) / 1_000_000;

            List<String> results = new ArrayList<>();
            for (CompletableFuture<String> f : futures) {
                results.add(f.join()); // already complete, does not block
            }

            System.out.println("results (list order)  = " + results);
            System.out.println("faster than one after another (1000 ms of sleeping): " + (elapsedMs < 1000));
        }
    }
}
