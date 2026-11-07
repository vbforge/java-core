package com.vbforge.practice.practice_4_request_building;

import java.net.URI;
import java.net.http.HttpRequest;
import java.time.Duration;

/**
 * Builder.copy() starts a new, independent builder from the current state: share a common base
 * (URI, Accept header) and vary only what differs per request. Also shows the per-request timeout.
 */
public class RequestBuilderCopyDemo {

    public static void main(String[] args) {
        HttpRequest.Builder base = HttpRequest.newBuilder(URI.create("http://localhost/unused"))
                .header("Accept", "text/plain");

        HttpRequest.Builder extended = base.copy()
                .header("X-Extra", "1")
                .timeout(Duration.ofSeconds(5));

        HttpRequest fromBase = base.build();
        HttpRequest fromCopy = extended.build();

        System.out.println("base  X-Extra = " + fromBase.headers().firstValue("X-Extra"));
        System.out.println("copy  X-Extra = " + fromCopy.headers().firstValue("X-Extra"));
        System.out.println("copy  Accept  = " + fromCopy.headers().firstValue("Accept"));
        System.out.println("base  timeout = " + fromBase.timeout());
        System.out.println("copy  timeout = " + fromCopy.timeout());
    }
}
