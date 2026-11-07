package com.vbforge.practice.practice_3_sync_get_and_response_headers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Synchronous GET: HttpRequest -> client.send(request, handler) -> HttpResponse<T>.
 * The handler decides what T is (here String).
 */
public class SyncGetDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build(); // GET is the default

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            System.out.println("statusCode = " + response.statusCode());
            System.out.println("body       = " + response.body());
            System.out.println("path       = " + response.uri().getPath());
            System.out.println("version    = " + response.version());
        }
    }
}
