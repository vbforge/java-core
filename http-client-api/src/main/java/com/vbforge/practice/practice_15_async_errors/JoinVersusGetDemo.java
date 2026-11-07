package com.vbforge.practice.practice_15_async_errors;

import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;

/**
 * join() throws the UNCHECKED CompletionException, get() throws the CHECKED ExecutionException
 * (and InterruptedException). In both, the real failure is getCause().
 */
public class JoinVersusGetDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        }
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + closedPort + "/")).build();

        CompletableFuture<?> future = client.sendAsync(request, BodyHandlers.ofString());

        try {
            future.join();
        } catch (CompletionException e) {
            System.out.println("join -> " + e.getClass().getSimpleName()
                    + ", cause = " + e.getCause().getClass().getSimpleName());
        }

        try {
            future.get();
        } catch (ExecutionException e) {
            System.out.println("get  -> " + e.getClass().getSimpleName()
                    + ", cause = " + e.getCause().getClass().getSimpleName());
        }
    }
}
