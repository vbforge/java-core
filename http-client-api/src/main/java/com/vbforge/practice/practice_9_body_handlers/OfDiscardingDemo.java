package com.vbforge.practice.practice_9_body_handlers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * BodyHandlers.discarding(): when only the status or headers matter. The body is thrown away,
 * so HttpResponse<Void>.body() is null.
 */
public class OfDiscardingDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();

            HttpResponse<Void> response = client.send(request, BodyHandlers.discarding());
            System.out.println("statusCode = " + response.statusCode());
            System.out.println("body       = " + response.body());
        }
    }
}
