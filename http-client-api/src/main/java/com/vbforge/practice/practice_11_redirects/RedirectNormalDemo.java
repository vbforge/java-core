package com.vbforge.practice.practice_11_redirects;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * Redirect.NORMAL follows the redirect for you: you receive the final response, and
 * previousResponse() still tells you what happened on the way.
 */
public class RedirectNormalDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/redirect")).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println("statusCode             = " + response.statusCode());
            System.out.println("body                   = " + response.body());
            System.out.println("final path             = " + response.uri().getPath());
            System.out.println("previous status        = " + response.previousResponse().map(HttpResponse::statusCode));
            System.out.println("previous path          = " + response.previousResponse().map(r -> r.uri().getPath()));
        }
    }
}
