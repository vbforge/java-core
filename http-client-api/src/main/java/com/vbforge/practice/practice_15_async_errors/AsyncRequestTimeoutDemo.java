package com.vbforge.practice.practice_15_async_errors;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * With sendAsync the request timeout does not throw at the call site: the future fails, and the
 * HttpTimeoutException shows up as the cause when somebody joins it. A failed async call is silent
 * until it is joined or has exceptionally/handle attached.
 */
public class AsyncRequestTimeoutDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/slow?ms=1500"))
                    .timeout(Duration.ofMillis(300))
                    .build();

            CompletableFuture<String> future = client.sendAsync(request, BodyHandlers.ofString())
                    .thenApply(r -> r.body());
            System.out.println("sendAsync returned, nothing thrown yet");

            try {
                future.join();
            } catch (CompletionException e) {
                System.out.println("join -> cause is HttpTimeoutException: "
                        + (e.getCause() instanceof HttpTimeoutException));
            }
        }
    }
}
