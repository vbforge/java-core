package com.vbforge.practice.practice_4_request_building;

import java.net.URI;
import java.net.http.HttpRequest;

/**
 * Content-Length, Host, Connection (and a few more) are restricted: the builder throws
 * IllegalArgumentException because the client sets them itself. Ordinary headers are fine.
 */
public class RestrictedHeadersDemo {

    public static void main(String[] args) {
        URI uri = URI.create("http://localhost/unused");

        String[] names = {"Content-Length", "Host", "Connection", "Authorization"};
        for (String name : names) {
            try {
                HttpRequest.newBuilder(uri).header(name, "x").build();
                System.out.println(name + " -> accepted");
            } catch (IllegalArgumentException e) {
                System.out.println(name + " -> rejected (" + e.getClass().getSimpleName() + ")");
            }
        }
    }
}
