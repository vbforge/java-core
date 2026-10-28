package com.vbforge.http_client_api;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * DemoServer — a tiny HTTP/1.1 server bound to {@code 127.0.0.1} on a random free port.
 *
 * <p>WHY it exists: learning {@code HttpClient} must not depend on the internet.
 * Public APIs go offline, change shape, rate-limit CI and make tests flaky. This
 * server is built on the JDK's own {@code com.sun.net.httpserver} package, so the
 * module still needs <em>no external libraries</em>, and every demo and test talks
 * only to the loopback interface.
 *
 * <p>This class is support code, not a lesson — you do not need to read it to
 * complete the tasks. The endpoints it exposes are:
 *
 * <pre>
 *   GET  /hello              → 200 "Hello, HTTP!"            (text/plain)
 *   GET  /json               → 200 {"id":1,"name":"Ada"}      (application/json)
 *   ANY  /echo               → 200 request body; header X-Method = request method
 *   GET  /query              → 200 the raw (still encoded) query string
 *   POST /form               → 200 echo of body, 415 unless Content-Type is x-www-form-urlencoded
 *   POST /json-echo          → 200 echo of body, 415 unless Content-Type is application/json
 *   GET  /echo-header/{Name} → 200 value of that request header, or "&lt;absent&gt;"
 *   GET  /status/{code}      → that status code, body "status {code}" (none for 204/304)
 *   GET  /redirect           → 302 with Location: /hello
 *   GET  /slow?ms=N          → 200 "slow done" after N milliseconds
 *   GET  /lines              → 200 "alpha\nbeta\ngamma\n"
 *   GET  /barrier/{id}?n=N   → blocks until N requests with the same id arrived
 *                              (then "released"), or "timeout" after 3 seconds
 * </pre>
 *
 * <p>Every response carries the header {@code X-Server: demo-server}.
 */
public final class DemoServer implements AutoCloseable {

    private static final String TEXT = "text/plain; charset=utf-8";
    private static final String JSON = "application/json";
    private static final String FORM = "application/x-www-form-urlencoded";

    private final HttpServer server;
    private final ExecutorService executor;
    private final ConcurrentMap<String, CountDownLatch> barriers = new ConcurrentHashMap<>();

    private DemoServer(HttpServer server, ExecutorService executor) {
        this.server = server;
        this.executor = executor;
    }

    /** Starts a server on a free loopback port. Close it (try-with-resources) when done. */
    public static DemoServer start() throws IOException {
        InetAddress loopback = InetAddress.getByAddress(new byte[]{127, 0, 0, 1});
        HttpServer httpServer = HttpServer.create(new InetSocketAddress(loopback, 0), 0);
        ExecutorService pool = Executors.newCachedThreadPool(r -> {
            Thread t = new Thread(r, "demo-server-worker");
            t.setDaemon(true);
            return t;
        });
        httpServer.setExecutor(pool);
        DemoServer demo = new DemoServer(httpServer, pool);
        httpServer.createContext("/", demo::route);
        httpServer.start();
        return demo;
    }

    /** Builds an absolute URI for a path (and optional query), e.g. {@code uri("/hello")}. */
    public URI uri(String pathAndQuery) {
        return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + pathAndQuery);
    }

    /**
     * Returns a URI on a loopback port where <em>nothing</em> is listening any more
     * (a server is started and immediately stopped). Connecting to it fails with
     * "connection refused" — handy for demonstrating failure handling offline.
     */
    public static URI deadUri(String pathAndQuery) throws IOException {
        try (DemoServer temp = start()) {
            return temp.uri(pathAndQuery);
        }
    }

    @Override
    public void close() {
        server.stop(0);
        executor.shutdownNow();
    }

    // ── routing ───────────────────────────────────────────────────────────────

    private void route(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String rawQuery = ex.getRequestURI().getRawQuery();

            if (path.equals("/hello")) {
                respond(ex, 200, TEXT, "Hello, HTTP!");
            } else if (path.equals("/json")) {
                respond(ex, 200, JSON, "{\"id\":1,\"name\":\"Ada\"}");
            } else if (path.equals("/echo")) {
                ex.getResponseHeaders().set("X-Method", ex.getRequestMethod());
                respond(ex, 200, TEXT, readBody(ex));
            } else if (path.equals("/query")) {
                respond(ex, 200, TEXT, rawQuery == null ? "" : rawQuery);
            } else if (path.equals("/form")) {
                echoIfContentType(ex, FORM);
            } else if (path.equals("/json-echo")) {
                echoIfContentType(ex, JSON);
            } else if (path.startsWith("/echo-header/")) {
                String value = ex.getRequestHeaders().getFirst(path.substring("/echo-header/".length()));
                respond(ex, 200, TEXT, value == null ? "<absent>" : value);
            } else if (path.startsWith("/status/")) {
                int code = Integer.parseInt(path.substring("/status/".length()));
                respond(ex, code, TEXT, "status " + code);
            } else if (path.equals("/redirect")) {
                ex.getResponseHeaders().set("Location", uri("/hello").toString());
                respond(ex, 302, TEXT, "");
            } else if (path.equals("/slow")) {
                Thread.sleep(Long.parseLong(queryParam(rawQuery, "ms", "500")));
                respond(ex, 200, TEXT, "slow done");
            } else if (path.equals("/lines")) {
                respond(ex, 200, TEXT, "alpha\nbeta\ngamma\n");
            } else if (path.startsWith("/barrier/")) {
                String id = path.substring("/barrier/".length());
                int n = Integer.parseInt(queryParam(rawQuery, "n", "1"));
                CountDownLatch latch = barriers.computeIfAbsent(id, k -> new CountDownLatch(n));
                latch.countDown();
                boolean released = latch.await(3, TimeUnit.SECONDS);
                respond(ex, 200, TEXT, released ? "released" : "timeout");
            } else {
                respond(ex, 404, TEXT, "not found: " + path);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        } catch (RuntimeException e) {
            respond(ex, 500, TEXT, "server error: " + e);
        } finally {
            ex.close();
        }
    }

    private static void echoIfContentType(HttpExchange ex, String expected) throws IOException {
        String contentType = ex.getRequestHeaders().getFirst("Content-Type");
        String body = readBody(ex);
        if (contentType != null && contentType.startsWith(expected)) {
            respond(ex, 200, expected, body);
        } else {
            respond(ex, 415, TEXT, "unsupported");
        }
    }

    private static String readBody(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    private static String queryParam(String rawQuery, String key, String fallback) {
        if (rawQuery == null) {
            return fallback;
        }
        for (String pair : rawQuery.split("&")) {
            int eq = pair.indexOf('=');
            if (eq > 0 && pair.substring(0, eq).equals(key)) {
                return pair.substring(eq + 1);
            }
        }
        return fallback;
    }

    private static void respond(HttpExchange ex, int status, String contentType, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", contentType);
        ex.getResponseHeaders().set("X-Server", "demo-server");
        boolean noBody = bytes.length == 0 || status == 204 || status == 304;
        if (noBody) {
            ex.sendResponseHeaders(status, -1);
        } else {
            ex.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = ex.getResponseBody()) {
                os.write(bytes);
            }
        }
    }
}
