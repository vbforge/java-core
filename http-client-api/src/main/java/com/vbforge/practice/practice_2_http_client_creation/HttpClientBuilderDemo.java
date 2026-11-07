package com.vbforge.practice.practice_2_http_client_creation;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * HttpClient.newBuilder(): choose the HTTP version, redirect policy and connect timeout once;
 * the built client is immutable.
 */
public class HttpClientBuilderDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .connectTimeout(Duration.ofSeconds(2))
                .build();

        System.out.println("version         = " + client.version());
        System.out.println("followRedirects = " + client.followRedirects());
        System.out.println("connectTimeout  = " + client.connectTimeout());

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/hello")).build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("response version = " + response.version());
        }
    }
}
