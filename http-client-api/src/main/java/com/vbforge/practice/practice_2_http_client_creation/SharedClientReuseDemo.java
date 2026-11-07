package com.vbforge.practice.practice_2_http_client_creation;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Create the client ONCE and reuse it: it owns the connection pool and is thread-safe.
 * (A new client per request throws the pool away every time.)
 */
public class SharedClientReuseDemo {

    private static final HttpClient CLIENT = HttpClient.newHttpClient();

    static String get(PracticeServer server) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();
        return CLIENT.send(request, HttpResponse.BodyHandlers.ofString()).body();
    }

    public static void main(String[] args) throws Exception {
        try (PracticeServer server = PracticeServer.start()) {
            for (int i = 1; i <= 3; i++) {
                System.out.println("sequential request " + i + " -> " + get(server));
            }

            // the same client used from several threads at once
            ExecutorService threads = Executors.newFixedThreadPool(4);
            try {
                List<Future<String>> futures = new ArrayList<>();
                for (int i = 0; i < 4; i++) {
                    futures.add(threads.submit(() -> get(server)));
                }
                for (int i = 0; i < futures.size(); i++) {
                    System.out.println("thread request " + (i + 1) + " -> " + futures.get(i).get());
                }
            } finally {
                threads.shutdown();
            }
        }
    }
}
