package com.vbforge.practice.practice_6_request_bodies;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * The client never guesses Content-Type. A JSON body sent without it arrives with no content type
 * (strict APIs answer 415); say what you are sending yourself.
 */
public class ContentTypeMustBeSetDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String json = "{\"name\":\"John\"}";

        try (PracticeServer server = PracticeServer.start()) {
            URI echo = server.uri("/echo");

            HttpRequest without = HttpRequest.newBuilder(echo)
                    .POST(BodyPublishers.ofString(json))
                    .build();
            HttpRequest with = HttpRequest.newBuilder(echo)
                    .header("Content-Type", "application/json")
                    .POST(BodyPublishers.ofString(json))
                    .build();

            System.out.println("without header -> " + contentTypeLine(client, without));
            System.out.println("with header    -> " + contentTypeLine(client, with));
        }
    }

    private static String contentTypeLine(HttpClient client, HttpRequest request) throws Exception {
        return client.send(request, BodyHandlers.ofString()).body().split("\n")[1];
    }
}
