package com.vbforge.practice.practice_7_query_and_form_encoding;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * Build a query string safely: encode every name AND value, keep the order (LinkedHashMap).
 * Raw user input in the URI text changes its meaning or makes URI.create fail.
 */
public class QueryStringBuilderDemo {

    static URI buildUri(URI base, Map<String, String> params) {
        StringJoiner query = new StringJoiner("&");
        for (Map.Entry<String, String> e : params.entrySet()) {
            query.add(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                    + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }
        return URI.create(base + "?" + query);
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            Map<String, String> params = new LinkedHashMap<>();
            params.put("q", "java 11");
            params.put("lang", "c++&more");
            params.put("na me", "café");

            URI uri = buildUri(server.uri("/query"), params);
            System.out.println("built query  = " + uri.getRawQuery());

            HttpRequest request = HttpRequest.newBuilder(uri).build();
            System.out.println("server saw   = " + client.send(request, BodyHandlers.ofString()).body());

            try {
                URI.create(server.uri("/query") + "?q=" + "java 11");
            } catch (IllegalArgumentException e) {
                System.out.println("raw space in URI.create -> " + e.getClass().getSimpleName());
            }
        }
    }
}
