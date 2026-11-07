package com.vbforge.practice.practice_12_failures;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * send() also throws InterruptedException. Swallowing it hides the cancellation request from callers;
 * restore the interrupt flag (then return or rethrow).
 */
public class InterruptFlagDemo {

    static String fetchOrNull(HttpClient client, HttpRequest request) {
        try {
            return client.send(request, BodyHandlers.ofString()).body();
        } catch (IOException e) {
            return null;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // restore the flag, do not swallow it
            return null;
        }
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/slow?ms=500")).build();

            Thread.currentThread().interrupt(); // someone asked this thread to stop
            String result = fetchOrNull(client, request);

            System.out.println("result                = " + result);
            System.out.println("flag still set after  = " + Thread.currentThread().isInterrupted());
            Thread.interrupted(); // clear it so the rest of the program runs normally
        }
    }
}
