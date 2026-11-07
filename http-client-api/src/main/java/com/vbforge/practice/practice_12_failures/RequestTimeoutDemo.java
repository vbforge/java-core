package com.vbforge.practice.practice_12_failures;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.net.http.HttpTimeoutException;
import java.time.Duration;

/**
 * There is NO request timeout by default. Set one per request: when the server is too slow,
 * send() throws HttpTimeoutException (an IOException).
 */
public class RequestTimeoutDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest slow = HttpRequest.newBuilder(server.uri("/slow?ms=1500"))
                    .timeout(Duration.ofMillis(300))
                    .build();

            try {
                client.send(slow, BodyHandlers.ofString());
                System.out.println("unexpected: no exception");
            } catch (HttpTimeoutException e) {
                System.out.println("caught HttpTimeoutException, is an IOException = " + (e instanceof IOException));
            }

            HttpRequest fast = HttpRequest.newBuilder(server.uri("/slow?ms=50"))
                    .timeout(Duration.ofSeconds(5))
                    .build();
            System.out.println("fast request -> " + client.send(fast, BodyHandlers.ofString()).body());
        }
    }
}
