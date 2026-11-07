package com.vbforge.practice.practice_1_local_test_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Shared fixture, not a concept: a tiny local HTTP server (JDK com.sun.net.httpserver) bound to
 * 127.0.0.1 on a random free port, so every practice class works without a network.
 *
 * Routes:
 *   /hello         200 "hello", headers X-Demo: yes, X-Count: 42, X-Tag: a and X-Tag: b
 *   /echo          200, text with: method=, content-type=, body-bytes=, body=   (what the server received)
 *   /query         200, the raw (still encoded) query string
 *   /lines         200, "one\ntwo\nthree\n"
 *   /pointer       200, "/hello" (a path to fetch next)
 *   /redirect      302 with Location -> /hello
 *   /slow?ms=N     sleeps N milliseconds, then 200 "slow-N"
 *   /status/CODE   answers with that status code and body "status CODE"
 *   anything else  404 "not found"
 */
public final class PracticeServer implements AutoCloseable {

    private final HttpServer server;
    private final ExecutorService executor;

    private PracticeServer(HttpServer server, ExecutorService executor) {
        this.server = server;
        this.executor = executor;
    }

    public static PracticeServer start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(InetAddress.getByName("127.0.0.1"), 0), 0);
        // daemon threads: the JVM can exit even if a demo forgets to close the server
        ExecutorService executor = Executors.newFixedThreadPool(8, r -> {
            Thread t = new Thread(r, "practice-server");
            t.setDaemon(true);
            return t;
        });
        server.createContext("/", PracticeServer::handle);
        server.setExecutor(executor);
        server.start();
        return new PracticeServer(server, executor);
    }

    /** Full URI for a path (and optional query), e.g. uri("/hello") or uri("/slow?ms=300"). */
    public URI uri(String pathAndQuery) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + pathAndQuery);
    }

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }

    private static void handle(HttpExchange ex) throws IOException {
        try {
            byte[] requestBody = ex.getRequestBody().readAllBytes();
            String path = ex.getRequestURI().getPath();
            switch (path) {
                case "/hello":
                    ex.getResponseHeaders().add("X-Demo", "yes");
                    ex.getResponseHeaders().add("X-Count", "42");
                    ex.getResponseHeaders().add("X-Tag", "a");
                    ex.getResponseHeaders().add("X-Tag", "b");
                    send(ex, 200, "hello");
                    break;
                case "/echo":
                    String contentType = ex.getRequestHeaders().getFirst("Content-Type");
                    send(ex, 200, "method=" + ex.getRequestMethod()
                            + "\ncontent-type=" + (contentType == null ? "<none>" : contentType)
                            + "\nbody-bytes=" + requestBody.length
                            + "\nbody=" + new String(requestBody, StandardCharsets.UTF_8));
                    break;
                case "/query":
                    String rawQuery = ex.getRequestURI().getRawQuery();
                    send(ex, 200, rawQuery == null ? "" : rawQuery);
                    break;
                case "/lines":
                    send(ex, 200, "one\ntwo\nthree\n");
                    break;
                case "/pointer":
                    send(ex, 200, "/hello");
                    break;
                case "/redirect":
                    ex.getResponseHeaders().add("Location", "http://" + ex.getRequestHeaders().getFirst("Host") + "/hello");
                    send(ex, 302, "");
                    break;
                case "/slow":
                    int ms = parseMs(ex.getRequestURI().getRawQuery());
                    try {
                        Thread.sleep(ms);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    send(ex, 200, "slow-" + ms);
                    break;
                default:
                    if (path.startsWith("/status/")) {
                        int code = Integer.parseInt(path.substring("/status/".length()));
                        send(ex, code, "status " + code);
                    } else {
                        send(ex, 404, "not found");
                    }
            }
        } catch (IOException e) {
            // the client may already have given up (timeout, cancel); nothing to do
        } finally {
            ex.close();
        }
    }

    private static int parseMs(String rawQuery) {
        if (rawQuery != null && rawQuery.startsWith("ms=")) {
            return Integer.parseInt(rawQuery.substring(3));
        }
        return 0;
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
        // length -1 means "no body" (required for an empty answer)
        ex.sendResponseHeaders(status, bytes.length == 0 ? -1 : bytes.length);
        if (bytes.length > 0) {
            try (OutputStream out = ex.getResponseBody()) {
                out.write(bytes);
            }
        }
    }
}
