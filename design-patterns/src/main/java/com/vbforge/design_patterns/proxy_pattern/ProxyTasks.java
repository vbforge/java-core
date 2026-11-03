package com.vbforge.design_patterns.proxy_pattern;

import java.util.List;
import java.util.Map;

/**
 * ProxyTasks — implement every method below to make {@code ProxyTasksTest} go GREEN.
 */
public class ProxyTasks {

    public interface DataSource {
        String fetch(String key);
    }

    // ── Task 1 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a "real" {@link DataSource} backed by {@code data} that appends
     * every requested key to {@code log} before returning the value. Unknown
     * keys return {@code "MISS"}.
     *
     * <p>Examples:
     * <pre>
     *   data = {"a":"A"}, log = []
     *   fetch("a")   → "A",  log → ["a"]
     *   fetch("z")   → "MISS", log → ["a","z"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>The returned object captures {@code data} and {@code log} in a
     *       lambda or anonymous class.</li>
     *   <li>Use {@code data.getOrDefault(key, "MISS")}.</li>
     *   <li>Record <em>before</em> returning — order of log entries mirrors
     *       call order.</li>
     * </ul>
     *
     * @param data backing store
     * @param log  log to append requested keys to
     * @return a real data source
     */
    public DataSource realSource(Map<String, String> data, List<String> log) {
        throw new UnsupportedOperationException("TODO: implement realSource");
    }

    // ── Task 2 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a caching proxy around {@code delegate}. On each {@code fetch}:
     * if the key is already cached, return the cached value <em>without</em>
     * touching the delegate; otherwise fetch, cache, and return.
     *
     * <p>Examples:
     * <pre>
     *   real = realSource({"a":"A"}, log);
     *   proxy = cachingProxy(real);
     *   proxy.fetch("a");   // delegate called once, log = ["a"]
     *   proxy.fetch("a");   // cache hit, log still ["a"]
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Keep a {@code Map<String,String>} cache inside the proxy.</li>
     *   <li>Use {@code containsKey} (not {@code get != null}) so a legitimate
     *       {@code "MISS"} value is also cached.</li>
     * </ul>
     *
     * @param delegate the wrapped data source
     * @return a caching {@link DataSource}
     */
    public DataSource cachingProxy(DataSource delegate) {
        throw new UnsupportedOperationException("TODO: implement cachingProxy");
    }

    // ── Task 3 ─────────────────────────────────────────────────────────────────

    /**
     * Returns a protection proxy around {@code delegate} that only forwards
     * {@code fetch} calls whose key starts with {@code allowedPrefix}. Any
     * other key throws {@link SecurityException} and does <em>not</em> hit the
     * delegate.
     *
     * <p>Examples:
     * <pre>
     *   real = realSource({"public:x":"X","private:y":"Y"}, log);
     *   proxy = protectionProxy(real, "public:");
     *   proxy.fetch("public:x")   → "X"          (delegate sees "public:x")
     *   proxy.fetch("private:y")  → SecurityException
     *   proxy.fetch(null)         → SecurityException
     * </pre>
     *
     * <p>Hints:
     * <ul>
     *   <li>Check {@code key == null} first — {@code null.startsWith(...)} NPEs.</li>
     *   <li>Throw {@link SecurityException} with the offending key in the message.</li>
     *   <li>Only call {@code delegate.fetch(key)} after the prefix check passes.</li>
     * </ul>
     *
     * @param delegate      the wrapped data source
     * @param allowedPrefix only keys with this prefix are forwarded
     * @return a protection {@link DataSource}
     */
    public DataSource protectionProxy(DataSource delegate, String allowedPrefix) {
        throw new UnsupportedOperationException("TODO: implement protectionProxy");
    }
}