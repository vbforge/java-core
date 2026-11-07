package com.vbforge.practice.practice_9_body_handlers;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * BodyHandlers.ofLines() gives a Stream<String> (one element per line); consume it and close it,
 * for example by collecting into a List.
 */
public class OfLinesDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/lines")).build();

            HttpResponse<Stream<String>> response = client.send(request, BodyHandlers.ofLines());
            List<String> lines;
            try (Stream<String> stream = response.body()) {
                lines = stream.collect(Collectors.toList());
            }
            System.out.println("lines = " + lines);
        }
    }
}
