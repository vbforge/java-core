package com.vbforge.practice.practice_14_async_chaining;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Racing: anyOf(...) completes with the first future to finish. Its result type is Object, and
 * the losers keep running unless you cancel them.
 */
public class AnyOfRaceDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            int[] delays = {1000, 100, 500};

            List<CompletableFuture<String>> racers = new ArrayList<>();
            for (int ms : delays) {
                HttpRequest request = HttpRequest.newBuilder(server.uri("/slow?ms=" + ms)).build();
                racers.add(client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body));
            }

            Object winner = CompletableFuture.anyOf(racers.toArray(new CompletableFuture<?>[0])).join();
            System.out.println("winner = " + winner);

            for (CompletableFuture<String> racer : racers) {
                racer.cancel(true); // stop waiting for the slower ones (no effect on the finished winner)
            }
        }
    }
}
