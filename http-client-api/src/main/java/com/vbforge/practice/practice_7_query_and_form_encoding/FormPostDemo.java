package com.vbforge.practice.practice_7_query_and_form_encoding;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

/**
 * An HTML-form style POST: body "name=value&name=value" (each part URL-encoded) plus the
 * Content-Type application/x-www-form-urlencoded, which you must set yourself.
 */
public class FormPostDemo {

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        Map<String, String> fields = new LinkedHashMap<>();
        fields.put("user", "john doe");
        fields.put("comment", "50% & more");

        StringJoiner form = new StringJoiner("&");
        for (Map.Entry<String, String> e : fields.entrySet()) {
            form.add(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8)
                    + "=" + URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }

        try (PracticeServer server = PracticeServer.start()) {
            HttpRequest request = HttpRequest.newBuilder(server.uri("/echo"))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(BodyPublishers.ofString(form.toString()))
                    .build();

            System.out.println(client.send(request, BodyHandlers.ofString()).body());
        }
    }
}
