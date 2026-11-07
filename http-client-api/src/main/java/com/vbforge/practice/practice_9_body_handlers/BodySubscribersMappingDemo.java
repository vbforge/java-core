package com.vbforge.practice.practice_9_body_handlers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandler;
import java.net.http.HttpResponse.BodySubscribers;
import java.nio.charset.StandardCharsets;

/**
 * A custom BodyHandler built with BodySubscribers.mapping: take a standard subscriber (text)
 * and convert its result, so send() returns HttpResponse<Integer> directly.
 */
public class BodySubscribersMappingDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        BodyHandler<Integer> lengthHandler = responseInfo -> BodySubscribers.mapping(
                BodySubscribers.ofString(StandardCharsets.UTF_8),
                String::length);

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/lines")).build();

            HttpResponse<Integer> response = client.send(request, lengthHandler);
            System.out.println("body length (characters) = " + response.body());
        }
    }
}
