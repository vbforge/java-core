package com.vbforge.practice.practice_9_body_handlers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

/**
 * BodyHandlers.ofInputStream() hands you the body as a stream to read yourself (good for big bodies);
 * close it with try-with-resources.
 */
public class OfInputStreamDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();

            HttpResponse<InputStream> response = client.send(request, BodyHandlers.ofInputStream());
            try (InputStream in = response.body()) {
                System.out.println("read from stream = " + new String(in.readAllBytes(), StandardCharsets.UTF_8));
            }
        }
    }
}
