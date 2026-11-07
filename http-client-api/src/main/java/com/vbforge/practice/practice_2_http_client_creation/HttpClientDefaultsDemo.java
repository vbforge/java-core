package com.vbforge.practice.practice_2_http_client_creation;

import java.net.http.HttpClient;

/**
 * What HttpClient.newHttpClient() gives you: HTTP/2, Redirect.NEVER, NO connect timeout.
 * Nothing is sent, so no server is needed.
 */
public class HttpClientDefaultsDemo {

    public static void main(String[] args) {
        HttpClient client = HttpClient.newHttpClient();

        System.out.println("version         = " + client.version());
        System.out.println("followRedirects = " + client.followRedirects());
        System.out.println("connectTimeout  = " + client.connectTimeout());
        System.out.println("proxy           = " + client.proxy());
    }
}
