# http-client-api

> **Phase 5 — I/O, Date-Time, Logging & HTTP** | Java 11+ | Package: `com.vbforge.http_client_api`

## What's covered

| Topic | Key points |
|---|---|
| `HttpClient` | Immutable, thread-safe, owns a connection pool — **create once, reuse**; `newHttpClient()` vs `newBuilder()`; defaults are HTTP/2, `Redirect.NEVER`, no connect timeout |
| Sync requests | `HttpRequest` → `client.send(request, handler)` → `HttpResponse<T>`; `statusCode`, `body`, `headers`, `uri`, `version` |
| `HttpHeaders` | Case-insensitive multimap; `firstValue` / `allValues` / `firstValueAsLong` return `Optional`s |
| Request building | `header` adds vs `setHeader` replaces; `copy()`; per-request `timeout`; restricted headers (`Content-Length`, `Host`, …) |
| Bodies | `BodyPublishers.ofString/ofByteArray/noBody`; length is in **bytes**; you must set `Content-Type` yourself |
| Query & form encoding | `URLEncoder.encode(value, UTF_8)` — space becomes `+`; encode names **and** values |
| HTTP methods | `GET()`, `POST(p)`, `PUT(p)`, `DELETE()`, `method("PATCH", p)` |
| Response handling | Status codes are **not** exceptions; `BodyHandlers.ofString/ofLines/ofFile/ofInputStream/ofByteArray/discarding`; `BodySubscribers.mapping` |
| Redirects | `Redirect.NEVER` / `NORMAL` / `ALWAYS`; `previousResponse()` |
| Failures | `ConnectException`, `HttpConnectTimeoutException`, `HttpTimeoutException` — all `IOException`s; restore the interrupt flag on `InterruptedException` |
| Async | `sendAsync` → `CompletableFuture`; `thenApply`, `thenCompose`, `thenCombine`, `allOf`, `anyOf`, `exceptionally`, `handle`; `join` vs `get`; custom `executor` |

> **No network needed.** Every demo and test talks to `DemoServer`, a tiny local server
> built on the JDK's own `com.sun.net.httpserver` and bound to `127.0.0.1` on a random free
> port. It is support code, not a lesson — you never have to read it to finish the tasks.

## Files

```
src/
├── main/java/com/vbforge/http_client_api/
│   ├── DemoServer.java                   ← local test server (support code, no tasks)
│   ├── HttpClientBasicsDemo.java         ← client, sync GET, headers, POST, body handlers
│   ├── HttpClientBasicsTasks.java        ← 🎯 your tasks — implement these methods
│   ├── RequestBuildingDemo.java          ← headers, query encoding, publishers, methods
│   ├── RequestBuildingTasks.java         ← 🎯 your tasks — implement these methods
│   ├── ResponseHandlingDemo.java         ← status, redirects, body handlers, failures
│   ├── ResponseHandlingTasks.java        ← 🎯 your tasks — implement these methods
│   ├── AsyncRequestsDemo.java            ← sendAsync, fan-out, chaining, errors, executor
│   └── AsyncRequestsTasks.java           ← 🎯 your tasks — implement these methods
└── test/java/com/vbforge/http_client_api/
    ├── HttpClientBasicsDemoTest.java     ← tests for the demo (already green)
    ├── HttpClientBasicsTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── RequestBuildingDemoTest.java      ← tests for the demo (already green)
    ├── RequestBuildingTasksTest.java     ← 🧪 tests for your tasks — all must pass
    ├── ResponseHandlingDemoTest.java     ← tests for the demo (already green)
    ├── ResponseHandlingTasksTest.java    ← 🧪 tests for your tasks — all must pass
    ├── AsyncRequestsDemoTest.java        ← tests for the demo (already green)
    └── AsyncRequestsTasksTest.java       ← 🧪 tests for your tasks — all must pass
```

## 🎯 Tasks

Four `*Tasks.java` files, 22 methods in total. Each has a detailed Javadoc.

**`HttpClientBasicsTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `buildClient(connectTimeout)` | `HttpClient.newBuilder()` — version, redirects, connect timeout |
| 2 | `fetchStatus(client, uri)` | Sync `send` + `BodyHandlers.discarding()` |
| 3 | `fetchBody(client, uri)` | Sync `send` + `BodyHandlers.ofString()` |
| 4 | `fetchHeader(client, uri, name)` | `HttpHeaders.firstValue` → `Optional` |
| 5 | `postText(client, uri, text)` | `POST` + `BodyPublishers.ofString` + `Content-Type` |

**`RequestBuildingTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `buildGet(uri, headers)` | Builder + adding headers from a map |
| 2 | `buildJsonPost(uri, json)` | JSON `Content-Type` / `Accept`; byte length of the body |
| 3 | `buildFormPost(uri, fields)` | `application/x-www-form-urlencoded` + `URLEncoder` |
| 4 | `buildUri(base, params)` | Safe, ordered query-string construction |
| 5 | `buildGetWithTimeout(uri, timeout)` | Per-request `timeout` |

**`ResponseHandlingTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `classifyStatus(client, uri)` | Status classes — error codes are values, not exceptions |
| 2 | `fetchFollowingRedirects(uri)` | `Redirect.NORMAL` on the client |
| 3 | `fetchLines(client, uri)` | `BodyHandlers.ofLines()` → `List` |
| 4 | `downloadToFile(client, uri, target)` | `BodyHandlers.ofFile` and its `OpenOption`s |
| 5 | `fetchOrDefault(client, uri, fallback)` | 2xx check, `IOException`, interrupt-flag handling |
| 6 | `fetchBodyLength(client, uri)` | Custom handler with `BodySubscribers.mapping` |

**`AsyncRequestsTasks`**

| # | Method | Topic |
|---|---|---|
| 1 | `fetchBodyAsync(client, uri)` | `sendAsync` + `thenApply` — never block |
| 2 | `fetchAllConcurrently(client, uris)` | Start all, then `allOf` — ordered results |
| 3 | `fetchBodyOrFallback(client, uri, fallback)` | `exceptionally` — failure vs error status |
| 4 | `fetchThenFetch(client, first, next)` | Dependent requests with `thenCompose` |
| 5 | `fastest(client, uris)` | Racing with `anyOf` |
| 6 | `fetchWithTimeout(client, uri, timeout)` | Request timeout surfacing in a future |

```bash
mvn test -Dtest=HttpClientBasicsTasksTest
mvn test -Dtest=RequestBuildingTasksTest
mvn test -Dtest=ResponseHandlingTasksTest
mvn test -Dtest=AsyncRequestsTasksTest

# or all four at once
mvn test -Dtest='*TasksTest'
```

All 22 task groups must go **GREEN** ✅

## Key things to remember

```java
// ❌ A 404 or 500 does NOT throw — you will happily parse an error page as data
HttpResponse<String> r = client.send(request, BodyHandlers.ofString());
parse(r.body());
// ✅ Always look at the status first
if (r.statusCode() / 100 == 2) { parse(r.body()); }

// ❌ New client per request — throws away the connection pool every time
HttpClient.newHttpClient().send(request, handler);
// ✅ One client, reused (it is immutable and thread-safe)
private static final HttpClient CLIENT = HttpClient.newHttpClient();

// Defaults to remember: HTTP/2, Redirect.NEVER, NO connect timeout, NO request timeout.
// A 302 is handed to you as a normal response unless you ask for it:
HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();

// ❌ Swallowing the interrupt hides a cancellation request from callers
catch (InterruptedException e) { /* ignore */ }
// ✅ Restore the flag (then return / rethrow)
catch (InterruptedException e) { Thread.currentThread().interrupt(); }

// The client never guesses Content-Type — a JSON body without it gets 415 from strict APIs
HttpRequest.newBuilder(uri)
        .header("Content-Type", "application/json")      // ✅ say what you are sending
        .POST(BodyPublishers.ofString(json));

// header() ADDS a value, setHeader() REPLACES; Content-Length / Host / Connection are
// restricted — the builder throws IllegalArgumentException, the client sets them itself.

// ❌ Raw user input in a query string — '&', '=', ' ' change its meaning (or crash URI.create)
URI.create(base + "?q=" + userInput);
// ✅ Encode every name and value. URLEncoder is FORM encoding: space → '+', not '%20'
base + "?q=" + URLEncoder.encode(userInput, StandardCharsets.UTF_8);

// ❌ ofFile(path) opens with CREATE, WRITE only — a shorter body over a longer file leaves stale bytes
client.send(request, BodyHandlers.ofFile(path));
// ✅ Say you want to overwrite
client.send(request, BodyHandlers.ofFile(path, CREATE, TRUNCATE_EXISTING, WRITE));

// ❌ Joining inside the loop that starts the requests = sequential, no speed-up
for (URI u : uris) results.add(client.sendAsync(req(u), ofString()).join());
// ✅ Start everything first, wait afterwards
List<CompletableFuture<...>> fs = uris.stream().map(u -> client.sendAsync(req(u), ofString())).toList();
CompletableFuture.allOf(fs.toArray(new CompletableFuture<?>[0])).join();

// ❌ thenApply with a future-returning function → CompletableFuture<CompletableFuture<T>>
a.thenApply(x -> fetch(next(x)));
// ✅ Dependent calls flatten with thenCompose
a.thenCompose(x -> fetch(next(x)));

// join() throws UNCHECKED CompletionException, get() throws CHECKED ExecutionException —
// in both the real failure (ConnectException, HttpTimeoutException, …) is getCause().
// A failed async call is silent until somebody joins it: attach exceptionally/handle.
```

## Run

```bash
# All tests (demo + tasks)
mvn test

# Demo tests only
mvn test -Dtest='*DemoTest'

# Task tests only
mvn test -Dtest='*TasksTest'

# Run main demos (one per concept)
mvn exec:java -Dexec.mainClass="com.vbforge.http_client_api.HttpClientBasicsDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.http_client_api.RequestBuildingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.http_client_api.ResponseHandlingDemo"
mvn exec:java -Dexec.mainClass="com.vbforge.http_client_api.AsyncRequestsDemo"
```
