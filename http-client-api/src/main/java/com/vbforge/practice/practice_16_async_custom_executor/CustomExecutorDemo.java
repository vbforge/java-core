package com.vbforge.practice.practice_16_async_custom_executor;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * HttpClient.Builder.executor(...) lets the client run its asynchronous work on threads you own
 * (named, bounded, shut down by you). The default is a client-managed cached pool.
 */
public class CustomExecutorDemo {

    public static void main(String[] args) throws Exception {
        AtomicInteger created = new AtomicInteger();
        ExecutorService executor = Executors.newFixedThreadPool(2, r -> {
            Thread t = new Thread(r, "practice-http-" + created.incrementAndGet());
            t.setDaemon(true);
            return t;
        });

        try (PracticeServer server = PracticeServer.start()) {
            HttpClient defaultClient = HttpClient.newHttpClient();
            HttpClient client = HttpClient.newBuilder().executor(executor).build();

            System.out.println("default client executor present = " + defaultClient.executor().isPresent());
            System.out.println("custom client uses our executor = " + (client.executor().orElse(null) == executor));

            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();
            String body = client.sendAsync(request, BodyHandlers.ofString()).join().body();

            System.out.println("body                            = " + body);
            System.out.println("our factory created threads     = " + (created.get() > 0));
        } finally {
            executor.shutdown(); // we own it, we shut it down
        }
    }
}
