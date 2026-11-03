package com.vbforge.java_modules_jpms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.module.ModuleDescriptor;
import java.sql.Connection;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ModulesDemo — API contracts")
class ModulesDemoTest {

    // ── 1. module-info.java syntax ────────────────────────────────────────────

    @Nested
    @DisplayName("1. module-info.java syntax")
    class ModuleInfoTest {

        @Test
        @DisplayName("example declares a module named after the package")
        void declaresModule() {
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE)
                .contains("module com.vbforge.java_modules_jpms {");
        }

        @Test
        @DisplayName("example requires java.sql and java.logging")
        void requires() {
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE).contains("requires java.sql");
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE).contains("requires java.logging");
        }

        @Test
        @DisplayName("example exports the public package")
        void exports() {
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE)
                .contains("exports com.vbforge.java_modules_jpms;");
        }

        @Test
        @DisplayName("example mentions opens, uses, provides as comments")
        void advancedDirectives() {
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE).contains("opens");
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE).contains("uses");
            assertThat(ModulesDemo.MODULE_INFO_EXAMPLE).contains("provides");
        }
    }

    // ── 2. Module runtime API ─────────────────────────────────────────────────

    @Nested
    @DisplayName("2. Module runtime API")
    class ModuleApiTest {

        @Test
        @DisplayName("boot layer contains java.base")
        void bootHasJavaBase() {
            Set<String> names = ModuleLayer.boot().modules().stream()
                .map(Module::getName).collect(Collectors.toSet());
            assertThat(names).contains("java.base");
        }

        @Test
        @DisplayName("boot layer contains java.sql, java.logging, java.xml")
        void bootHasCommonModules() {
            Set<String> names = ModuleLayer.boot().modules().stream()
                .map(Module::getName).collect(Collectors.toSet());
            assertThat(names).contains("java.sql", "java.logging", "java.xml");
        }

        @Test
        @DisplayName("String.class belongs to java.base")
        void stringInJavaBase() {
            assertThat(String.class.getModule().getName()).isEqualTo("java.base");
        }

        @Test
        @DisplayName("findModule returns Optional.empty for missing module")
        void findMissing() {
            assertThat(ModuleLayer.boot().findModule("no.such.module")).isEmpty();
        }
    }

    // ── 3. ModuleDescriptor ───────────────────────────────────────────────────

    @Nested
    @DisplayName("3. ModuleDescriptor")
    class ModuleDescriptorTest {

        @Test
        @DisplayName("java.base descriptor name is java.base")
        void descriptorName() {
            ModuleDescriptor d = String.class.getModule().getDescriptor();
            assertThat(d.name()).isEqualTo("java.base");
        }

        @Test
        @DisplayName("java.base requires nothing")
        void javaBaseRequiresNothing() {
            ModuleDescriptor d = String.class.getModule().getDescriptor();
            assertThat(d.requires()).isEmpty();
        }

        @Test
        @DisplayName("java.base exports java.lang, java.util, java.io")
        void javaBaseExports() {
            Set<String> exports = String.class.getModule().getDescriptor()
                .exports().stream()
                .map(ModuleDescriptor.Exports::source)
                .collect(Collectors.toSet());
            assertThat(exports).contains("java.lang", "java.util", "java.io");
        }

        @Test
        @DisplayName("java.sql requires java.base, java.logging, java.xml")
        void sqlRequires() {
            Set<String> requires = Connection.class.getModule().getDescriptor()
                .requires().stream()
                .map(ModuleDescriptor.Requires::name)
                .collect(Collectors.toSet());
            assertThat(requires).contains("java.base", "java.logging", "java.xml");
        }
    }

    // ── 4. Named vs unnamed ───────────────────────────────────────────────────

    @Nested
    @DisplayName("4. Named vs unnamed")
    class NamedModuleTest {

        @Test
        @DisplayName("String.class is in a named module")
        void stringNamed() {
            assertThat(String.class.getModule().isNamed()).isTrue();
        }

        @Test
        @DisplayName("this test class is in the unnamed module (classpath)")
        void testClassUnnamed() {
            // Test classes never have a module-info.java, so they are always
            // loaded from the classpath → unnamed module.
            assertThat(ModulesDemoTest.class.getModule().isNamed()).isFalse();
            assertThat(ModulesDemoTest.class.getModule().getName()).isNull();
        }

        @Test
        @DisplayName("named modules have a descriptor; unnamed ones do not")
        void descriptorPresence() {
            assertThat(String.class.getModule().getDescriptor()).isNotNull();
            assertThat(ModulesDemoTest.class.getModule().getDescriptor()).isNull();
        }
    }
}