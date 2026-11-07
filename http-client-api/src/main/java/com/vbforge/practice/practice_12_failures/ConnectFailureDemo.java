package com.vbforge.practice.practice_12_failures;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.time.Duration;

/**
 * Nobody is listening on the port: send() throws (here a ConnectException). Failures to
 * connect, connect timeouts and request timeouts are all IOExceptions - unlike error statuses.
 */
public class ConnectFailureDemo {

    public static void main(String[] args) throws Exception {
        int closedPort;
        try (ServerSocket socket = new ServerSocket(0)) {
            closedPort = socket.getLocalPort();
        } // closed again: nothing listens on this port now

        HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
        HttpRequest request = HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + closedPort + "/")).build();

        try {
            client.send(request, BodyHandlers.ofString());
            System.out.println("unexpected: no exception");
        } catch (IOException e) {
            System.out.println("exception        = " + e.getClass().getName());
            System.out.println("is an IOException = " + (e instanceof IOException));
        }
    }
}
