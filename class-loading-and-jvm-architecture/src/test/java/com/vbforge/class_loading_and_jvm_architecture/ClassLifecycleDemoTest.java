package com.vbforge.class_loading_and_jvm_architecture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("ClassLifecycleDemo — API contracts")
class ClassLifecycleDemoTest {

    @Nested
    @DisplayName("1. What triggers initialisation")
    class TriggersTest {

        @Test
        @DisplayName("Class.forName(name, false, loader) runs no static initialiser")
        void loadOnly() {
            assertThat(ClassLifecycleDemo.loadWithoutInitializing()).isEmpty();
        }

        @Test
        @DisplayName("new Child() runs parent static init, child static init, then both constructors")
        void newInstanceOrder() {
            assertThat(ClassLifecycleDemo.instantiateChild()).containsExactly(
                    "IsoParent static init",
                    "IsoChild static init",
                    "IsoParent constructor",
                    "IsoChild constructor");
        }

        @Test
        @DisplayName("Reading a compile-time constant does not initialise the class")
        void compileTimeConstant() {
            assertThat(ClassLifecycleDemo.readCompileTimeConstant()).isEmpty();
        }

        @Test
        @DisplayName("Reading a non-constant static field initialises the class")
        void runtimeStaticField() {
            assertThat(ClassLifecycleDemo.readRuntimeStaticField()).containsExactly("IsoHolder static init");
        }

        @Test
        @DisplayName("Creating an array of a class does not initialise it")
        void arrayCreation() {
            assertThat(ClassLifecycleDemo.createArrayOfChild()).isEmpty();
        }

        @Test
        @DisplayName("A class literal does not initialise the class")
        void classLiteral() {
            assertThat(ClassLifecycleDemo.classLiteral()).isEmpty();
        }

        @Test
        @DisplayName("A static field read through a subclass initialises only the declaring class")
        void fieldViaSubclass() {
            assertThat(ClassLifecycleDemo.readParentFieldViaChild()).containsExactly("IsoParent static init");
        }

        @Test
        @DisplayName("Scenarios are repeatable because each one uses a fresh loader")
        void repeatable() {
            assertThat(ClassLifecycleDemo.instantiateChild()).isEqualTo(ClassLifecycleDemo.instantiateChild());
            assertThat(ClassLifecycleDemo.readRuntimeStaticField())
                    .isEqualTo(ClassLifecycleDemo.readRuntimeStaticField());
        }
    }

    @Nested
    @DisplayName("2. Preparation vs initialisation")
    class PreparationTest {

        @Test
        @DisplayName("A field read during initialisation still has its default value")
        void defaultVisibleDuringInit() {
            assertThat(ClassLifecycleDemo.preparationVersusInitialization()[0]).isEqualTo(0);
        }

        @Test
        @DisplayName("After initialisation the assigned value is in place")
        void assignedAfterInit() {
            assertThat(ClassLifecycleDemo.preparationVersusInitialization()[1]).isEqualTo(5);
        }

        @Test
        @DisplayName("Both values together")
        void bothValues() {
            assertThat(ClassLifecycleDemo.preparationVersusInitialization()).containsExactly(0, 5);
        }
    }

    @Nested
    @DisplayName("3. A failing static initialiser")
    class FailingInitTest {

        @Test
        @DisplayName("The first use raises ExceptionInInitializerError")
        void firstAttempt() {
            Throwable first = ClassLifecycleDemo.initializeBrokenClassTwice()[0];

            assertThat(first).isInstanceOf(ExceptionInInitializerError.class);
        }

        @Test
        @DisplayName("The original exception is the cause of the first error")
        void causePreserved() {
            Throwable first = ClassLifecycleDemo.initializeBrokenClassTwice()[0];

            assertThat(first).hasCauseInstanceOf(NumberFormatException.class);
        }

        @Test
        @DisplayName("The second use raises NoClassDefFoundError")
        void secondAttempt() {
            Throwable second = ClassLifecycleDemo.initializeBrokenClassTwice()[1];

            assertThat(second).isInstanceOf(NoClassDefFoundError.class);
        }

        @Test
        @DisplayName("A fresh loader gets a fresh first failure")
        void freshLoaderFreshFailure() {
            Throwable again = ClassLifecycleDemo.initializeBrokenClassTwice()[0];

            assertThat(again).isInstanceOf(ExceptionInInitializerError.class);
        }
    }

    @Nested
    @DisplayName("4. Class-file checks during loading")
    class ClassFileChecksTest {

        @Test
        @DisplayName("Untouched bytes define a class with the expected name")
        void goodBytes() throws Exception {
            Class<?> defined = ClassLifecycleDemo.defineGreeterFrom(ClassLifecycleDemo.greeterClassFile());

            assertThat(defined.getName()).isEqualTo(ClassLoaderDemo.Greeter.class.getName());
            assertThat((Object) defined).isNotSameAs(ClassLoaderDemo.Greeter.class);
        }

        @Test
        @DisplayName("A broken magic number raises ClassFormatError")
        void brokenMagic() throws Exception {
            byte[] bad = ClassLifecycleDemo.withBrokenMagic(ClassLifecycleDemo.greeterClassFile());

            assertThatThrownBy(() -> ClassLifecycleDemo.defineGreeterFrom(bad))
                    .isInstanceOf(ClassFormatError.class)
                    .isNotInstanceOf(UnsupportedClassVersionError.class);
        }

        @Test
        @DisplayName("A too-new major version raises UnsupportedClassVersionError")
        void tooNewVersion() throws Exception {
            byte[] bad = ClassLifecycleDemo.withMajorVersion(ClassLifecycleDemo.greeterClassFile(), 99);

            assertThatThrownBy(() -> ClassLifecycleDemo.defineGreeterFrom(bad))
                    .isInstanceOf(UnsupportedClassVersionError.class);
        }

        @Test
        @DisplayName("UnsupportedClassVersionError is a ClassFormatError")
        void versionErrorIsFormatError() throws Exception {
            byte[] bad = ClassLifecycleDemo.withMajorVersion(ClassLifecycleDemo.greeterClassFile(), 99);

            assertThatThrownBy(() -> ClassLifecycleDemo.defineGreeterFrom(bad))
                    .isInstanceOf(ClassFormatError.class);
        }

        @Test
        @DisplayName("The patch helpers copy: the original array is not modified")
        void patchesCopy() throws Exception {
            byte[] original = ClassLifecycleDemo.greeterClassFile();
            byte first = original[0];
            byte seventh = original[7];

            byte[] patchedMagic = ClassLifecycleDemo.withBrokenMagic(original);
            byte[] patchedVersion = ClassLifecycleDemo.withMajorVersion(original, 99);

            assertThat((Object) patchedMagic).isNotSameAs(original);
            assertThat((Object) patchedVersion).isNotSameAs(original);
            assertThat(original[0]).isEqualTo(first);
            assertThat(original[7]).isEqualTo(seventh);
        }

        @Test
        @DisplayName("withMajorVersion writes the value big-endian into bytes 6 and 7")
        void majorVersionBigEndian() throws Exception {
            byte[] patched = ClassLifecycleDemo.withMajorVersion(ClassLifecycleDemo.greeterClassFile(), 0x0102);

            assertThat(patched[6]).isEqualTo((byte) 0x01);
            assertThat(patched[7]).isEqualTo((byte) 0x02);
        }
    }
}
