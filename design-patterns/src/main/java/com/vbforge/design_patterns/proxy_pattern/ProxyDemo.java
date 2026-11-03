package com.vbforge.design_patterns.proxy_pattern;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ProxyDemo — same interface as the real object, with extra behaviour added.
 *
 * <p>Topics:
 * <ol>
 *   <li>The real subject (records fetches so proxies can be verified)</li>
 *   <li>Caching proxy — second call is a cache hit, no delegate call</li>
 *   <li>Protection proxy — allowed-prefix check before delegating</li>
 * </ol>
 */
public class ProxyDemo {

    public static void main(String[] args) {
        realDemo();
        System.out.println("---");
        cachingProxyDemo();
        System.out.println("---");
        protectionProxyDemo();
    }

    /**
     * The real subject. Each {@code fetch} records the key it was asked for.
     *
     * <p><b>Why:</b> the log lets the demo (and tests) prove which calls
     * actually reached the delegate — the whole point of proxying.
     */
    static void realDemo() {
        System.out.println("=== Real subject ===");
        List<String> log = new ArrayList<>();
        DataSource real = new RealSource(Map.of("a", "A", "b", "B"), log);
        System.out.println("fetch(a) : " + real.fetch("a"));
        System.out.println("fetch(b) : " + real.fetch("b"));
        System.out.println("log      : " + log);
    }

    /**
     * Caching proxy — only hits the delegate on a cache miss.
     *
     * <p><b>Why:</b> hides an expensive operation behind a cheap one without
     * changing the interface the caller uses.
     */
    static void cachingProxyDemo() {
        System.out.println("=== Caching proxy ===");
        List<String> log = new ArrayList<>();
        DataSource real = new RealSource(Map.of("a", "A"), log);
        DataSource cache = new CachingProxy(real);

        System.out.println("call1 : " + cache.fetch("a"));
        System.out.println("call2 : " + cache.fetch("a"));
        System.out.println("delegate calls: " + log.size());   // 1, not 2
    }

    /**
     * Protection proxy — only allows keys with a specific prefix.
     *
     * <p><b>Why:</b> centralises an access rule that would otherwise be
     * scattered across every call site.
     */
    static void protectionProxyDemo() {
        System.out.println("=== Protection proxy ===");
        List<String> log = new ArrayList<>();
        DataSource real = new RealSource(Map.of("public:x", "X", "private:y", "Y"), log);
        DataSource guarded = new ProtectionProxy(real, "public:");

        System.out.println("public:x  : " + guarded.fetch("public:x"));
        try {
            guarded.fetch("private:y");
        } catch (SecurityException e) {
            System.out.println("private:y : blocked");
        }
        System.out.println("log       : " + log);   // only "public:x" made it through
    }

    // ── Shared types ─────────────────────────────────────────────────────────

    public interface DataSource {
        String fetch(String key);
    }

    public static class RealSource implements DataSource {
        private final Map<String, String> data;
        private final List<String> log;
        public RealSource(Map<String, String> data, List<String> log) {
            this.data = data;
            this.log = log;
        }
        @Override public String fetch(String key) {
            log.add(key);
            return data.getOrDefault(key, "MISS");
        }
    }

    public static class CachingProxy implements DataSource {
        private final DataSource delegate;
        private final Map<String, String> cache = new HashMap<>();
        public CachingProxy(DataSource delegate) { this.delegate = delegate; }
        @Override public String fetch(String key) {
            if (!cache.containsKey(key)) cache.put(key, delegate.fetch(key));
            return cache.get(key);
        }
    }

    public static class ProtectionProxy implements DataSource {
        private final DataSource delegate;
        private final String prefix;
        public ProtectionProxy(DataSource delegate, String prefix) {
            this.delegate = delegate;
            this.prefix = prefix;
        }
        @Override public String fetch(String key) {
            if (key == null || !key.startsWith(prefix))
                throw new SecurityException("Blocked: " + key);
            return delegate.fetch(key);
        }
    }
}