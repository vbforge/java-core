package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ClassLoaderTasks — your implementations")
class ClassLoaderTasksTest {

    private ClassLoaderTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ClassLoaderTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement loaderDepth()")
    @DisplayName("loaderDepth")
    class LoaderDepthTest {

        @Test
        @DisplayName("Bootstrap classes have depth 0")
        void bootstrapIsZero() {
            assertThat(tasks.loaderDepth(String.class)).isEqualTo(0);
        }

        @Test
        @DisplayName("Primitives and arrays of bootstrap types have depth 0")
        void primitivesAndArrays() {
            assertThat(tasks.loaderDepth(int.class)).isEqualTo(0);
            assertThat(tasks.loaderDepth(String[].class)).isEqualTo(0);
        }

        @Test
        @DisplayName("Application classes have at least the application loader")
        void applicationClass() {
            assertThat(tasks.loaderDepth(ClassLoaderTasks.class)).isGreaterThanOrEqualTo(1);
        }

        @Test
        @DisplayName("Depth equals the length of the loader chain without the bootstrap")
        void matchesChain() {
            int expected = ClassLoaderDemo.loaderChainNames(ClassLoaderTasks.class).size() - 1;

            assertThat(tasks.loaderDepth(ClassLoaderTasks.class)).isEqualTo(expected);
        }

        @Test
        @DisplayName("A class defined by a child loader is one level deeper than its parent's classes")
        void childLoaderIsDeeper() throws Exception {
            int parentDepth = tasks.loaderDepth(ClassLoaderDemo.Greeter.class);

            assertThat(tasks.loaderDepth(ClassLoaderDemo.isolatedGreeterClass())).isEqualTo(parentDepth + 1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement definingLoaderName()")
    @DisplayName("definingLoaderName")
    class DefiningLoaderNameTest {

        @Test
        @DisplayName("Bootstrap classes are named <bootstrap>")
        void bootstrap() {
            assertThat(tasks.definingLoaderName(String.class)).isEqualTo("<bootstrap>");
        }

        @Test
        @DisplayName("Primitives are named <bootstrap>")
        void primitive() {
            assertThat(tasks.definingLoaderName(int.class)).isEqualTo("<bootstrap>");
        }

        @Test
        @DisplayName("Application classes report their loader's class name")
        void applicationClass() {
            String expected = ClassLoaderTasks.class.getClassLoader().getClass().getName();

            assertThat(tasks.definingLoaderName(ClassLoaderTasks.class)).isEqualTo(expected);
        }

        @Test
        @DisplayName("Arrays report the loader of their element type")
        void arrays() {
            assertThat(tasks.definingLoaderName(String[].class)).isEqualTo("<bootstrap>");
            assertThat(tasks.definingLoaderName(ClassLoaderTasks[].class))
                    .isEqualTo(tasks.definingLoaderName(ClassLoaderTasks.class));
        }

        @Test
        @DisplayName("A class from an isolating loader names that loader's class")
        void isolatedClass() throws Exception {
            assertThat(tasks.definingLoaderName(ClassLoaderDemo.isolatedGreeterClass()))
                    .isEqualTo(ClassLoaderDemo.IsolatingClassLoader.class.getName());
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isSameRuntimeType()")
    @DisplayName("isSameRuntimeType")
    class IsSameRuntimeTypeTest {

        @Test
        @DisplayName("Two strings share a runtime type")
        void sameType() {
            assertThat(tasks.isSameRuntimeType("a", "b")).isTrue();
        }

        @Test
        @DisplayName("A string and an integer do not")
        void differentType() {
            assertThat(tasks.isSameRuntimeType("a", 42)).isFalse();
        }

        @Test
        @DisplayName("Same class name from different loaders is NOT the same type")
        void sameNameDifferentLoader() throws Exception {
            Object normal = new ClassLoaderDemo.Greeter();
            Object isolated = ClassLoaderDemo.newIsolatedGreeter();

            assertThat(tasks.isSameRuntimeType(normal, isolated)).isFalse();
        }

        @Test
        @DisplayName("Two instances of the same isolated class share a type")
        void twoIsolatedInstances() throws Exception {
            Object isolated = ClassLoaderDemo.newIsolatedGreeter();
            Object sameClass = isolated.getClass().getDeclaredConstructor().newInstance();

            assertThat(tasks.isSameRuntimeType(isolated, sameClass)).isTrue();
        }

        @Test
        @DisplayName("Different implementations of one interface are different types")
        void differentImplementations() {
            assertThat(tasks.isSameRuntimeType(new java.util.ArrayList<String>(), new java.util.LinkedList<String>()))
                    .isFalse();
        }

        @Test
        @DisplayName("Null on either side gives false")
        void nulls() {
            assertThat(tasks.isSameRuntimeType(null, "a")).isFalse();
            assertThat(tasks.isSameRuntimeType("a", null)).isFalse();
            assertThat(tasks.isSameRuntimeType(null, null)).isFalse();
        }
    }
}
