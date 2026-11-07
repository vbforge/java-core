package com.vbforge.practice.practice_5_http_methods;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * GET(), POST(p), PUT(p), DELETE() and method("PATCH", p) for any other verb.
 * The /echo route reports the method the server actually received.
 */
public class HttpMethodsDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            URI echo = server.uri("/echo");

            send(client, HttpRequest.newBuilder(echo).GET().build());
            send(client, HttpRequest.newBuilder(echo).POST(BodyPublishers.ofString("new")).build());
            send(client, HttpRequest.newBuilder(echo).PUT(BodyPublishers.ofString("replace")).build());
            send(client, HttpRequest.newBuilder(echo).DELETE().build());
            send(client, HttpRequest.newBuilder(echo).method("PATCH", BodyPublishers.ofString("patch")).build());
        }
    }

    private static void send(HttpClient client, HttpRequest request) throws Exception {
        HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
        String firstLine = response.body().split("\n")[0];
        System.out.println("client sent " + request.method() + " -> server saw " + firstLine);
    }
}
