package com.vbforge.practice.practice_15_async_errors;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

/**
 * A FAILURE (no connection, timeout) completes the future exceptionally; an ERROR STATUS (500) does not.
 * exceptionally() supplies a fallback for failures only; handle() sees both result and exception.
 */
public class ExceptionallyAndHandleDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }
        URI unreachable = URI.create("http://127.0.0.1:" + closedPort + "/");

        try (PracticeServer server = PracticeServer.start()) {
            // failure -> exceptionally runs
            String viaExceptionally = bodyFuture(client, unreachable)
                    .exceptionally(ex -> "fallback")
                    .join();
            System.out.println("failure + exceptionally    -> " + viaExceptionally);

            // error status -> NOT a failure, exceptionally is skipped, the error page is the "result"
            String serverError = bodyFuture(client, server.uri("/status/500"))
                    .exceptionally(ex -> "fallback")
                    .join();
            System.out.println("status 500 + exceptionally -> " + serverError);

            // handle sees (result, exception) - exactly one of them is null
            String viaHandle = bodyFuture(client, unreachable)
                    .handle((body, ex) -> ex == null ? body : "failed with " + unwrap(ex).getClass().getSimpleName())
                    .join();
            System.out.println("failure + handle           -> " + viaHandle);
        }
    }

    private static CompletableFuture<String> bodyFuture(HttpClient client, URI uri) {
        HttpRequest request = HttpRequest.newBuilder(uri).build();
        return client.sendAsync(request, BodyHandlers.ofString()).thenApply(HttpResponse::body);
    }

    /** Dependent stages wrap the real failure in a CompletionException; the cause is what matters. */
    private static Throwable unwrap(Throwable ex) {
        while (ex instanceof CompletionException && ex.getCause() != null) {
            ex = ex.getCause();
        }
        return ex;
    }
}
