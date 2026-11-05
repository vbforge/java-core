package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.vbforge.class_loading_and_jvm_architecture.ClassLoaderDemo.Greeter;
import com.vbforge.class_loading_and_jvm_architecture.ClassLoaderDemo.Greeting;
import com.vbforge.class_loading_and_jvm_architecture.ClassLoaderDemo.IsolatingClassLoader;
import com.vbforge.class_loading_and_jvm_architecture.ClassLoaderDemo.TracingClassLoader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ClassLoaderDemo — API contracts")
class ClassLoaderDemoTest {

    @Nested
    @DisplayName("1. The loader hierarchy")
    class HierarchyTest {

        @Test
        @DisplayName("String is loaded by the bootstrap loader, so its chain is just the bootstrap")
        void stringChain() {
            assertThat(ClassLoaderDemo.loaderChainNames(String.class)).containsExactly(ClassLoaderDemo.BOOTSTRAP);
        }

        @Test
        @DisplayName("Primitives report no loader and map to the bootstrap")
        void primitiveChain() {
            assertThat(ClassLoaderDemo.loaderChainNames(int.class)).containsExactly(ClassLoaderDemo.BOOTSTRAP);
        }

        @Test
        @DisplayName("An application class has at least one Java loader before the bootstrap")
        void applicationChain() {
            assertThat(ClassLoaderDemo.loaderChainNames(ClassLoaderDemo.class))
                    .hasSizeGreaterThanOrEqualTo(2)
                    .endsWith(ClassLoaderDemo.BOOTSTRAP)
                    .startsWith(ClassLoaderDemo.class.getClassLoader().getClass().getName());
        }

        @Test
        @DisplayName("isBootstrapClass is true for core classes, primitives and their arrays")
        void bootstrapClasses() {
            assertThat(ClassLoaderDemo.isBootstrapClass(String.class)).isTrue();
            assertThat(ClassLoaderDemo.isBootstrapClass(int.class)).isTrue();
            assertThat(ClassLoaderDemo.isBootstrapClass(String[].class)).isTrue();
        }

        @Test
        @DisplayName("isBootstrapClass is false for application classes and their arrays")
        void applicationClasses() {
            assertThat(ClassLoaderDemo.isBootstrapClass(ClassLoaderDemo.class)).isFalse();
            assertThat(ClassLoaderDemo.isBootstrapClass(ClassLoaderDemo[].class)).isFalse();
        }

        @Test
        @DisplayName("An array uses the loader of its element type")
        void arrayUsesElementLoader() {
            assertThat((Object) ClassLoaderDemo[].class.getClassLoader())
                    .isSameAs(ClassLoaderDemo.class.getClassLoader());
        }

        @Test
        @DisplayName("Calling a method on the bootstrap 'loader' is a NullPointerException")
        void nullTrap() {
            assertThatThrownBy(() -> String.class.getClassLoader().getParent())
                    .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("2. Parent delegation")
    class DelegationTest {

        private final TracingClassLoader tracer = new TracingClassLoader(ClassLoaderDemo.class.getClassLoader());

        @Test
        @DisplayName("The tracer records the request and returns the parent's class")
        void recordsAndDelegates() throws Exception {
            Class<?> loaded = tracer.loadClass("java.util.ArrayList");

            assertThat(tracer.requests()).containsExactly("java.util.ArrayList");
            assertThat((Object) loaded).isSameAs(java.util.ArrayList.class);
        }

        @Test
        @DisplayName("An application class is defined by the parent, not by the tracer")
        void tracerIsNotDefiningLoader() throws Exception {
            Class<?> loaded = tracer.loadClass(ClassLoaderDemo.class.getName());

            assertThat((Object) loaded).isSameAs(ClassLoaderDemo.class);
            assertThat((Object) loaded.getClassLoader()).isNotSameAs(tracer);
        }

        @Test
        @DisplayName("A core class is still defined by the bootstrap loader")
        void coreClassStaysBootstrap() throws Exception {
            assertThat(ClassLoaderDemo.isBootstrapClass(tracer.loadClass("java.lang.Integer"))).isTrue();
        }

        @Test
        @DisplayName("An unknown class raises ClassNotFoundException, and the request is still recorded")
        void unknownClass() {
            assertThatThrownBy(() -> tracer.loadClass("no.such.Type")).isInstanceOf(ClassNotFoundException.class);
            assertThat(tracer.requests()).containsExactly("no.such.Type");
        }

        @Test
        @DisplayName("Requests accumulate in order")
        void requestsInOrder() throws Exception {
            tracer.loadClass("java.lang.String");
            tracer.loadClass("java.util.HashMap");

            assertThat(tracer.requests()).containsExactly("java.lang.String", "java.util.HashMap");
        }
    }

    @Nested
    @DisplayName("3. Same name, different loader")
    class IsolationTest {

        @Test
        @DisplayName("The isolated copy has the same name but is a different Class object")
        void sameNameDifferentClass() throws Exception {
            Class<?> copy = ClassLoaderDemo.isolatedGreeterClass();

            assertThat(copy.getName()).isEqualTo(Greeter.class.getName());
            assertThat((Object) copy).isNotSameAs(Greeter.class);
        }

        @Test
        @DisplayName("The copy is defined by an IsolatingClassLoader")
        void definedByIsolatingLoader() throws Exception {
            assertThat((Object) ClassLoaderDemo.isolatedGreeterClass().getClassLoader())
                    .isInstanceOf(IsolatingClassLoader.class);
        }

        @Test
        @DisplayName("Two isolating loaders define two unrelated copies")
        void twoLoadersTwoCopies() throws Exception {
            assertThat((Object) ClassLoaderDemo.isolatedGreeterClass())
                    .isNotSameAs(ClassLoaderDemo.isolatedGreeterClass());
        }

        @Test
        @DisplayName("An isolated instance is not a Greeter ...")
        void notAGreeter() throws Exception {
            assertThat(Greeter.class.isInstance(ClassLoaderDemo.newIsolatedGreeter())).isFalse();
        }

        @Test
        @DisplayName("... but it is a Greeting, because the interface is delegated to the parent")
        void isAGreeting() throws Exception {
            Object isolated = ClassLoaderDemo.newIsolatedGreeter();

            assertThat(isolated).isInstanceOf(Greeting.class);
            assertThat(((Greeting) isolated).greet()).isEqualTo("hello");
        }

        @Test
        @DisplayName("Casting an isolated instance to Greeter throws ClassCastException")
        void castFails() throws Exception {
            Object isolated = ClassLoaderDemo.newIsolatedGreeter();

            assertThatThrownBy(() -> ClassLoaderDemo.castToGreeter(isolated))
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("Casting a normal Greeter succeeds")
        void castSucceeds() {
            Greeter greeter = new Greeter();

            assertThat((Object) ClassLoaderDemo.castToGreeter(greeter)).isSameAs(greeter);
        }

        @Test
        @DisplayName("Names outside the isolated prefix are still delegated to the parent")
        void otherNamesDelegated() throws Exception {
            ClassLoader loader = new IsolatingClassLoader(ClassLoaderDemo.class.getClassLoader(), Greeter.class.getName());

            assertThat((Object) loader.loadClass(Greeting.class.getName())).isSameAs(Greeting.class);
            assertThat((Object) loader.loadClass("java.lang.String")).isSameAs(String.class);
        }
    }

    @Nested
    @DisplayName("4. Thread context class loader")
    class ContextLoaderTest {

        @Test
        @DisplayName("The temporary loader is visible while set")
        void temporaryVisible() {
            ClassLoader temporary = new TracingClassLoader(ClassLoaderDemo.class.getClassLoader());

            assertThat((Object) ClassLoaderDemo.contextClassLoaderDuring(temporary)).isSameAs(temporary);
        }

        @Test
        @DisplayName("The original loader is restored afterwards")
        void originalRestored() {
            ClassLoader original = Thread.currentThread().getContextClassLoader();

            ClassLoaderDemo.contextClassLoaderDuring(new TracingClassLoader(original));

            assertThat((Object) Thread.currentThread().getContextClassLoader()).isSameAs(original);
        }

        @Test
        @DisplayName("A new thread inherits its creator's context loader")
        void newThreadInherits() {
            assertThat(ClassLoaderDemo.newThreadInheritsContextLoader()).isTrue();
        }

        @Test
        @DisplayName("A new thread inherits a swapped-in loader too")
        void newThreadInheritsSwapped() {
            ClassLoader original = Thread.currentThread().getContextClassLoader();
            ClassLoader temporary = new TracingClassLoader(original);
            Thread.currentThread().setContextClassLoader(temporary);
            try {
                assertThat((Object) new Thread().getContextClassLoader()).isSameAs(temporary);
            } finally {
                Thread.currentThread().setContextClassLoader(original);
            }
        }
    }
}
