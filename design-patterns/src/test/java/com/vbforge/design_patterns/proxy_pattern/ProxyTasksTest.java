package com.vbforge.design_patterns.proxy_pattern;

import com.vbforge.design_patterns.proxy_pattern.ProxyTasks;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ProxyTasks — your implementations")
class ProxyTasksTest {

    private ProxyTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ProxyTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement realSource()")
    @DisplayName("Task 1 — realSource()")
    class RealSourceTest {

        @Test
        @DisplayName("returns the value from the map")
        void returnsValue() {
            ProxyTasks.DataSource r = tasks.realSource(Map.of("a", "A"), new ArrayList<>());
            assertThat(r.fetch("a")).isEqualTo("A");
        }

        @Test
        @DisplayName("unknown key returns MISS")
        void missing() {
            ProxyTasks.DataSource r = tasks.realSource(Map.of(), new ArrayList<>());
            assertThat(r.fetch("z")).isEqualTo("MISS");
        }

        @Test
        @DisplayName("appends every requested key to the log, in order")
        void logs() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource r = tasks.realSource(Map.of("a", "A"), log);
            r.fetch("a");
            r.fetch("z");
            assertThat(log).containsExactly("a", "z");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement cachingProxy()")
    @DisplayName("Task 2 — cachingProxy()")
    class CachingProxyTest {

        @Test
        @DisplayName("returns the underlying value")
        void returnsValue() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource real = tasks.realSource(Map.of("a", "A"), log);
            ProxyTasks.DataSource cache = tasks.cachingProxy(real);
            assertThat(cache.fetch("a")).isEqualTo("A");
        }

        @Test
        @DisplayName("repeated key hits cache — delegate called once")
        void cacheHit() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource real = tasks.realSource(Map.of("a", "A"), log);
            ProxyTasks.DataSource cache = tasks.cachingProxy(real);
            cache.fetch("a");
            cache.fetch("a");
            cache.fetch("a");
            assertThat(log).containsExactly("a");
        }

        @Test
        @DisplayName("MISS is cached — repeated unknown key hits delegate once")
        void cachesMiss() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource real = tasks.realSource(Map.of(), log);
            ProxyTasks.DataSource cache = tasks.cachingProxy(real);
            cache.fetch("z");
            cache.fetch("z");
            assertThat(log).containsExactly("z");
        }

        @Test
        @DisplayName("different keys both hit delegate once")
        void differentKeys() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource real = tasks.realSource(Map.of("a", "A", "b", "B"), log);
            ProxyTasks.DataSource cache = tasks.cachingProxy(real);
            cache.fetch("a");
            cache.fetch("b");
            cache.fetch("a");
            assertThat(log).containsExactly("a", "b");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement protectionProxy()")
    @DisplayName("Task 3 — protectionProxy()")
    class ProtectionProxyTest {

        @Test
        @DisplayName("allowed key is forwarded")
        void allows() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource real = tasks.realSource(Map.of("public:x", "X"), log);
            ProxyTasks.DataSource guarded = tasks.protectionProxy(real, "public:");
            assertThat(guarded.fetch("public:x")).isEqualTo("X");
            assertThat(log).containsExactly("public:x");
        }

        @Test
        @DisplayName("blocked key throws SecurityException and does not hit delegate")
        void blocks() {
            List<String> log = new ArrayList<>();
            ProxyTasks.DataSource real = tasks.realSource(Map.of("private:y", "Y"), log);
            ProxyTasks.DataSource guarded = tasks.protectionProxy(real, "public:");
            assertThatThrownBy(() -> guarded.fetch("private:y"))
                    .isInstanceOf(SecurityException.class);
            assertThat(log).isEmpty();
        }

        @Test
        @DisplayName("null key throws SecurityException (no NPE)")
        void nullKey() {
            ProxyTasks.DataSource real = tasks.realSource(Map.of(), new ArrayList<>());
            ProxyTasks.DataSource guarded = tasks.protectionProxy(real, "public:");
            assertThatThrownBy(() -> guarded.fetch(null))
                    .isInstanceOf(SecurityException.class);
        }
    }
}