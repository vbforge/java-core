package com.vbforge.java_modules_jpms;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModulesTasks — your implementations")
class ModulesTasksTest {

    private ModulesTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ModulesTasks();
    }

    // ── Task 1: isNamedModule ─────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement isNamedModule()")
    @DisplayName("Task 1 — isNamedModule()")
    class IsNamedModuleTest {

        @Test
        @DisplayName("String.class → true")
        void stringNamed() {
            assertThat(tasks.isNamedModule(String.class)).isTrue();
        }

        @Test
        @DisplayName("java.sql.Connection.class → true")
        void connectionNamed() {
            assertThat(tasks.isNamedModule(Connection.class)).isTrue();
        }

        @Test
        @DisplayName("test class → false (classpath = unnamed)")
        void testClassUnnamed() {
            assertThat(tasks.isNamedModule(ModulesTasksTest.class)).isFalse();
        }
    }

    // ── Task 2: moduleNameOf ──────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement moduleNameOf()")
    @DisplayName("Task 2 — moduleNameOf()")
    class ModuleNameOfTest {

        @Test
        @DisplayName("String.class → java.base")
        void string() {
            assertThat(tasks.moduleNameOf(String.class)).isEqualTo("java.base");
        }

        @Test
        @DisplayName("Connection.class → java.sql")
        void connection() {
            assertThat(tasks.moduleNameOf(Connection.class)).isEqualTo("java.sql");
        }

        @Test
        @DisplayName("test class → null (unnamed)")
        void unnamed() {
            assertThat(tasks.moduleNameOf(ModulesTasksTest.class)).isNull();
        }
    }

    // ── Task 3: requiresOf ────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement requiresOf()")
    @DisplayName("Task 3 — requiresOf()")
    class RequiresOfTest {

        @Test
        @DisplayName("java.base requires nothing")
        void javaBase() {
            assertThat(tasks.requiresOf("java.base")).isEmpty();
        }

        @Test
        @DisplayName("java.sql requires java.base, java.logging, java.xml")
        void javaSql() {
            assertThat(tasks.requiresOf("java.sql"))
                .contains("java.base", "java.logging", "java.xml");
        }

        @Test
        @DisplayName("unknown module → empty set")
        void unknown() {
            assertThat(tasks.requiresOf("no.such.module")).isEmpty();
        }
    }

    // ── Task 4: exportsOf ─────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement exportsOf()")
    @DisplayName("Task 4 — exportsOf()")
    class ExportsOfTest {

        @Test
        @DisplayName("java.base exports java.lang, java.util, java.io")
        void javaBase() {
            assertThat(tasks.exportsOf("java.base"))
                .contains("java.lang", "java.util", "java.io");
        }

        @Test
        @DisplayName("java.sql exports the java.sql package")
        void javaSql() {
            assertThat(tasks.exportsOf("java.sql")).contains("java.sql");
        }

        @Test
        @DisplayName("unknown module → empty set")
        void unknown() {
            assertThat(tasks.exportsOf("no.such.module")).isEmpty();
        }
    }

    // ── Task 5: bootLayerModuleNames ──────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement bootLayerModuleNames()")
    @DisplayName("Task 5 — bootLayerModuleNames()")
    class BootLayerModuleNamesTest {

        @Test
        @DisplayName("contains java.base")
        void hasJavaBase() {
            assertThat(tasks.bootLayerModuleNames()).contains("java.base");
        }

        @Test
        @DisplayName("contains java.sql, java.logging, java.xml")
        void hasCommonModules() {
            assertThat(tasks.bootLayerModuleNames())
                .contains("java.sql", "java.logging", "java.xml");
        }

        @Test
        @DisplayName("result is non-empty")
        void notEmpty() {
            assertThat(tasks.bootLayerModuleNames()).isNotEmpty();
        }
    }
}