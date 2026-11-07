package com.vbforge.practice.practice_6_request_bodies;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublisher;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;

/**
 * BodyPublishers.ofString / ofByteArray / noBody. The length of a body is counted in BYTES,
 * not characters: "héllo" has 5 characters but 6 bytes in UTF-8.
 */
public class BodyPublishersDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        String text = "héllo";

        BodyPublisher fromString = BodyPublishers.ofString(text);
        BodyPublisher fromBytes = BodyPublishers.ofByteArray("hello".getBytes(StandardCharsets.UTF_8));
        BodyPublisher empty = BodyPublishers.noBody();

        System.out.println("text.length()            = " + text.length());
        System.out.println("ofString contentLength   = " + fromString.contentLength());
        System.out.println("ofByteArray contentLength= " + fromBytes.contentLength());
        System.out.println("noBody contentLength     = " + empty.contentLength());

        try (PracticeServer server = PracticeServer.start()) {
            URI echo = server.uri("/echo");
            System.out.println("server received (ofString)   -> " + bodyBytesLine(client, echo, fromString));
            System.out.println("server received (ofByteArray)-> " + bodyBytesLine(client, echo, fromBytes));
            System.out.println("server received (noBody)     -> " + bodyBytesLine(client, echo, empty));
        }
    }

    private static String bodyBytesLine(HttpClient client, URI uri, BodyPublisher publisher) throws Exception {
        HttpRequest request = HttpRequest.newBuilder(uri).POST(publisher).build();
        String echoed = client.send(request, BodyHandlers.ofString()).body();
        return echoed.split("\n")[2]; // the body-bytes= line
    }
}
