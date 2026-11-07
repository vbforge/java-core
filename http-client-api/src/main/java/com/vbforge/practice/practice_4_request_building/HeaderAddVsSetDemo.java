package com.vbforge.practice.practice_4_request_building;

import java.net.URI;
import java.net.http.HttpRequest;

/**
 * header() ADDS another value for the name; setHeader() REPLACES all existing values.
 * Nothing is sent: a built HttpRequest can be inspected on its own.
 */
public class HeaderAddVsSetDemo {

    public static void main(String[] args) {
        URI uri = URI.create("http://localhost/unused");

        HttpRequest added = HttpRequest.newBuilder(uri)
                .header("X-A", "1")
                .header("X-A", "2")
                .build();
        System.out.println("header + header       -> " + added.headers().allValues("X-A"));

        HttpRequest replaced = HttpRequest.newBuilder(uri)
                .header("X-A", "1")
                .setHeader("X-A", "2")
                .build();
        System.out.println("header + setHeader    -> " + replaced.headers().allValues("X-A"));
    }
}
