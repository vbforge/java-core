package com.vbforge.design_patterns.proxy_pattern;

import com.vbforge.design_patterns.proxy_pattern.ProxyDemo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProxyDemo — API contracts")
class ProxyDemoTest {

    @Nested
    @DisplayName("1. Real subject")
    class RealTest {

        @Test
        @DisplayName("returns the stored value")
        void returnsValue() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource r = new ProxyDemo.RealSource(Map.of("a", "A"), log);
            assertThat(r.fetch("a")).isEqualTo("A");
        }

        @Test
        @DisplayName("unknown key returns MISS")
        void missing() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource r = new ProxyDemo.RealSource(Map.of(), log);
            assertThat(r.fetch("z")).isEqualTo("MISS");
        }

        @Test
        @DisplayName("logs every request")
        void logs() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource r = new ProxyDemo.RealSource(Map.of("a", "A"), log);
            r.fetch("a");
            r.fetch("z");
            assertThat(log).containsExactly("a", "z");
        }
    }

    @Nested
    @DisplayName("2. Caching proxy")
    class CachingTest {

        @Test
        @DisplayName("second fetch of same key is a cache hit")
        void cacheHit() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource real = new ProxyDemo.RealSource(Map.of("a", "A"), log);
            ProxyDemo.DataSource cache = new ProxyDemo.CachingProxy(real);
            cache.fetch("a");
            cache.fetch("a");
            assertThat(log).containsExactly("a");  // delegate hit only once
        }

        @Test
        @DisplayName("different keys both hit the delegate")
        void differentKeys() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource real = new ProxyDemo.RealSource(Map.of("a", "A", "b", "B"), log);
            ProxyDemo.DataSource cache = new ProxyDemo.CachingProxy(real);
            cache.fetch("a");
            cache.fetch("b");
            assertThat(log).containsExactly("a", "b");
        }
    }

    @Nested
    @DisplayName("3. Protection proxy")
    class ProtectionTest {

        @Test
        @DisplayName("allowed prefix delegates normally")
        void allows() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource real = new ProxyDemo.RealSource(Map.of("public:x", "X"), log);
            ProxyDemo.DataSource guarded = new ProxyDemo.ProtectionProxy(real, "public:");
            assertThat(guarded.fetch("public:x")).isEqualTo("X");
            assertThat(log).containsExactly("public:x");
        }

        @Test
        @DisplayName("blocked key throws and does not hit delegate")
        void blocks() {
            List<String> log = new ArrayList<>();
            ProxyDemo.DataSource real = new ProxyDemo.RealSource(Map.of("private:y", "Y"), log);
            ProxyDemo.DataSource guarded = new ProxyDemo.ProtectionProxy(real, "public:");
            assertThatThrownBy(() -> guarded.fetch("private:y"))
                    .isInstanceOf(SecurityException.class);
            assertThat(log).isEmpty();
        }
    }
}