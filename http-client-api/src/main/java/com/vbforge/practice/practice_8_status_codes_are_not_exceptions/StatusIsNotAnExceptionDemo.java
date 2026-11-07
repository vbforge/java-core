package com.vbforge.practice.practice_8_status_codes_are_not_exceptions;

import com.vbforge.practice.practice_1_local_test_server.PracticeServer;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

/**
 * A 404 or 500 does NOT throw: send() returns normally and the body is the error page.
 * Look at the status first, and only then treat the body as data.
 */
public class StatusIsNotAnExceptionDemo {

    static String classify(int status) {
        switch (status / 100) {
            case 2:
                return "success";
            case 3:
                return "redirect";
            case 4:
                return "client error";
            case 5:
                return "server error";
            default:
                return "other";
        }
    }

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        try (PracticeServer server = PracticeServer.start()) {
            int[] codes = {200, 404, 500};
            for (int code : codes) {
                HttpRequest request = HttpRequest.newBuilder(server.uri("/status/" + code)).build();
                HttpResponse<String> response = client.send(request, BodyHandlers.ofString()); // no exception
                System.out.println(code + " -> " + classify(response.statusCode())
                        + ", body = \"" + response.body() + "\"");
            }
        }
    }
}
