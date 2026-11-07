package com.vbforge.practice.practice_14_async_chaining;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

/**
 * Dependent requests: the second URL comes from the first answer. A function that returns a future
 * must go through thenCompose; with thenApply you get a CompletableFuture<CompletableFuture<T>>.
 */
public class ThenComposeDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            CompletableFuture<String> pointer = fetch(client, server.uri("/pointer").toString());

            // thenApply: nested future, one join() is not enough
            CompletableFuture<CompletableFuture<String>> nested =
                    pointer.thenApply(path -> fetch(client, server.uri(path).toString()));
            System.out.println("thenApply   -> " + nested.join().join());

            // thenCompose: flattened
            CompletableFuture<String> flat =
                    pointer.thenCompose(path -> fetch(client, server.uri(path).toString()));
            System.out.println("thenCompose -> " + flat.join());
        }
    }

    private static CompletableFuture<String> fetch(HttpClient client, String uri) {
        HttpRequest request = HttpRequest.newBuilder(java.net.URI.create(uri)).build();
        return client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }
}
