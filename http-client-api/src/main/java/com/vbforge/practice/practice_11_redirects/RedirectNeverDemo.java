package com.vbforge.practice.practice_11_redirects;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * The default policy is Redirect.NEVER: a 302 is handed to you as a normal response
 * (look at the Location header yourself).
 */
public class RedirectNeverDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/redirect")).build();

            HttpResponse<String> response = client.send(request, BodyHandlers.ofString());
            System.out.println("statusCode         = " + response.statusCode());
            System.out.println("Location path      = " + response.headers().firstValue("Location")
                    .map(l -> java.net.URI.create(l).getPath()).orElse("<none>"));
            System.out.println("previousResponse   = " + response.previousResponse());
        }
    }
}
