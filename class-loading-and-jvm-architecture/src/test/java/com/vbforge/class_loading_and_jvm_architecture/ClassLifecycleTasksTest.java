package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.concurrent.atomic.AtomicBoolean;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ClassLifecycleTasks — your implementations")
class ClassLifecycleTasksTest {

    /** Flipped by LazyProbe's static initialiser. Only one test ever touches LazyProbe. */
    static final AtomicBoolean PROBE_INITIALISED = new AtomicBoolean(false);

    /** Never referenced except through its name and class literal, so it stays uninitialised. */
    public static class LazyProbe {
        static {
            PROBE_INITIALISED.set(true);
        }
    }

    /** Fails to initialise. Only one test ever touches BrokenProbe. */
    public static class BrokenProbe {
        public static int value = Integer.parseInt("nope");
    }

    private ClassLifecycleTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new ClassLifecycleTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement loadWithoutInitializing()")
    @DisplayName("loadWithoutInitializing")
    class LoadWithoutInitializingTest {

        @Test
        @DisplayName("Returns the class but leaves its static initialiser unrun")
        void doesNotInitialize() throws Exception {
            Class<?> loaded = tasks.loadWithoutInitializing(LazyProbe.class.getName(), LazyProbe.class.getClassLoader());

            assertThat(loaded.getName()).isEqualTo(LazyProbe.class.getName());
            assertThat(PROBE_INITIALISED.get()).isFalse();
        }

        @Test
        @DisplayName("A null loader means the bootstrap loader")
        void bootstrapLoader() throws Exception {
            assertThat((Object) tasks.loadWithoutInitializing("java.lang.String", null)).isSameAs(String.class);
        }

        @Test
        @DisplayName("The returned class is the one the loader already knows")
        void sameClassAsLoader() throws Exception {
            ClassLoader loader = ClassLifecycleTasks.class.getClassLoader();

            assertThat((Object) tasks.loadWithoutInitializing(ClassLifecycleTasks.class.getName(), loader))
                    .isSameAs(ClassLifecycleTasks.class);
        }

        @Test
        @DisplayName("An unknown class raises ClassNotFoundException")
        void unknownClass() {
            assertThatThrownBy(() -> tasks.loadWithoutInitializing("no.such.Type", null))
                    .isInstanceOf(ClassNotFoundException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement captureInitializationFailure()")
    @DisplayName("captureInitializationFailure")
    class CaptureInitializationFailureTest {

        @Test
        @DisplayName("Returns null when initialisation succeeds")
        void success() throws Exception {
            assertThat(tasks.captureInitializationFailure("java.lang.Integer", null)).isNull();
        }

        @Test
        @DisplayName("Returns the ExceptionInInitializerError of a failing static initialiser")
        void failure() throws Exception {
            LinkageError error = tasks.captureInitializationFailure(
                    BrokenProbe.class.getName(), BrokenProbe.class.getClassLoader());

            assertThat(error)
                    .isInstanceOf(ExceptionInInitializerError.class)
                    .hasCauseInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("An unknown class raises ClassNotFoundException")
        void unknownClass() {
            assertThatThrownBy(() -> tasks.captureInitializationFailure("no.such.Type", null))
                    .isInstanceOf(ClassNotFoundException.class);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement defineFromBytes()")
    @DisplayName("defineFromBytes")
    class DefineFromBytesTest {

        private final ClassLoader parent = ClassLoaderDemo.class.getClassLoader();
        private final String name = ClassLoaderDemo.Greeter.class.getName();

        @Test
        @DisplayName("Defines a class with the requested name")
        void definesClass() throws Exception {
            Class<?> defined = tasks.defineFromBytes(parent, name, ClassLifecycleDemo.greeterClassFile());

            assertThat(defined.getName()).isEqualTo(name);
        }

        @Test
        @DisplayName("The new class differs from the one the parent loader defined")
        void differsFromParentsClass() throws Exception {
            Class<?> defined = tasks.defineFromBytes(parent, name, ClassLifecycleDemo.greeterClassFile());

            assertThat((Object) defined).isNotSameAs(ClassLoaderDemo.Greeter.class);
        }

        @Test
        @DisplayName("The defining loader's parent is the one passed in")
        void parentIsPassedIn() throws Exception {
            Class<?> defined = tasks.defineFromBytes(parent, name, ClassLifecycleDemo.greeterClassFile());

            assertThat((Object) defined.getClassLoader().getParent()).isSameAs(parent);
        }

        @Test
        @DisplayName("Each call uses a fresh loader, so two calls give two classes")
        void freshLoaderPerCall() throws Exception {
            byte[] bytes = ClassLifecycleDemo.greeterClassFile();

            assertThat((Object) tasks.defineFromBytes(parent, name, bytes))
                    .isNotSameAs(tasks.defineFromBytes(parent, name, bytes));
        }

        @Test
        @DisplayName("Invalid bytes surface as ClassFormatError")
        void invalidBytes() throws Exception {
            byte[] bad = ClassLifecycleDemo.withBrokenMagic(ClassLifecycleDemo.greeterClassFile());

            assertThatThrownBy(() -> tasks.defineFromBytes(parent, name, bad))
                    .isInstanceOf(ClassFormatError.class);
        }
    }
}
