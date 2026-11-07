package com.vbforge.practice.practice_13_async_basics;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;

/**
 * sendAsync returns a CompletableFuture immediately; describe what to do with the result with
 * thenApply and only block (join) at the very end, if at all.
 */
public class SendAsyncThenApplyDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();

            CompletableFuture<String> body = client.sendAsync(request, BodyHandlers.ofString())
                    .thenApply(HttpResponse::body)
                    .thenApply(String::toUpperCase);

            // ... the calling thread is free to do other work here ...

            System.out.println("result = " + body.join());
        }
    }
}
