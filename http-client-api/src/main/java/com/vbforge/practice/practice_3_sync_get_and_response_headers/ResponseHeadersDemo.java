package com.vbforge.practice.practice_3_sync_get_and_response_headers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpHeaders;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * HttpHeaders is a case-insensitive multimap; firstValue / allValues / firstValueAsLong
 * return Optionals (or an empty list) instead of null.
 */
public class ResponseHeadersDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();
            HttpHeaders headers = client.send(request, HttpResponse.BodyHandlers.discarding()).headers();

            System.out.println("firstValue(x-demo)   = " + headers.firstValue("x-demo"));
            System.out.println("firstValue(X-DEMO)   = " + headers.firstValue("X-DEMO"));
            System.out.println("allValues(X-Tag)     = " + headers.allValues("X-Tag"));
            System.out.println("firstValue(X-Tag)    = " + headers.firstValue("X-Tag"));
            System.out.println("firstValueAsLong     = " + headers.firstValueAsLong("X-Count"));
            System.out.println("firstValue(X-Nope)   = " + headers.firstValue("X-Nope"));
            System.out.println("allValues(X-Nope)    = " + headers.allValues("X-Nope"));
            System.out.println("content-type present = " + headers.firstValue("content-type").isPresent());
        }
    }
}
