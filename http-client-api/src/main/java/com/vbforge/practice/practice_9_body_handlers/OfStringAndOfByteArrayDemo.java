package com.vbforge.practice.practice_9_body_handlers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

/**
 * The handler chooses the body type T: ofString gives text, ofByteArray gives the raw bytes.
 */
public class OfStringAndOfByteArrayDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();

            HttpResponse<String> asString = client.send(request, BodyHandlers.ofString());
            HttpResponse<byte[]> asBytes = client.send(request, BodyHandlers.ofByteArray());

            System.out.println("ofString    -> " + asString.body());
            System.out.println("ofByteArray -> " + asBytes.body().length + " bytes, as text: "
                    + new String(asBytes.body(), StandardCharsets.UTF_8));
        }
    }
}
