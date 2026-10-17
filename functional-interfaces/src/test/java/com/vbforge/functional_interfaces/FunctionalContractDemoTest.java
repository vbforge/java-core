package com.vbforge.functional_interfaces;

import com.vbforge.functional_interfaces.FunctionalContractDemo.LegacyCallback;
import com.vbforge.functional_interfaces.FunctionalContractDemo.Labeled;
import com.vbforge.functional_interfaces.FunctionalContractDemo.TextOperation;
import com.vbforge.functional_interfaces.FunctionalContractDemo.TriFunction;
import com.vbforge.functional_interfaces.FunctionalContractDemo.Validator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Comparator;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

@DisplayName("FunctionalContractDemo — API contracts")
class FunctionalContractDemoTest {

    @Nested
    @DisplayName("1. One abstract method = a functional interface")
    class SingleAbstractMethod {

        @Test
        @DisplayName("TextOperation has exactly one abstract method")
        void oneAbstractMethod() {
            assertThat(FunctionalContractDemo.countAbstractMethods(TextOperation.class)).isEqualTo(1);
        }

        @Test
        @DisplayName("a lambda implements the single abstract method")
        void lambdaImplementation() {
            TextOperation shout = text -> text.toUpperCase() + "!";

            assertThat(shout.apply("hello")).isEqualTo("HELLO!");
        }

        @Test
        @DisplayName("a method reference with the same shape also fits")
        void methodReferenceImplementation() {
            TextOperation trim = String::trim;

            assertThat(trim.apply("  padded  ")).isEqualTo("padded");
        }

        @Test
        @DisplayName("an interface with several abstract methods is not functional")
        void listIsNotFunctional() {
            assertThat(FunctionalContractDemo.countAbstractMethods(List.class)).isGreaterThan(1);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(FunctionalContractDemo::singleAbstractMethodDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("2. Default and static methods do not count")
    class DefaultAndStatic {

        private final Validator<String> notEmpty = text -> !text.isEmpty();
        private final Validator<String> shortText = text -> text.length() <= 5;

        @Test
        @DisplayName("Validator is still functional despite default and static methods")
        void stillFunctional() {
            assertThat(FunctionalContractDemo.countAbstractMethods(Validator.class)).isEqualTo(1);
        }

        @Test
        @DisplayName("and() requires both validators to pass")
        void andCombines() {
            Validator<String> combined = notEmpty.and(shortText);

            assertThat(combined.validate("abc")).isTrue();
            assertThat(combined.validate("")).isFalse();
            assertThat(combined.validate("too long")).isFalse();
        }

        @Test
        @DisplayName("negate() flips the result")
        void negateFlips() {
            assertThat(notEmpty.negate().validate("abc")).isFalse();
            assertThat(notEmpty.negate().validate("")).isTrue();
        }

        @Test
        @DisplayName("and() returns a new validator and leaves the original unchanged")
        void originalUnchanged() {
            Validator<String> combined = notEmpty.and(shortText);

            assertThat(combined).isNotSameAs(notEmpty);
            assertThat(notEmpty.validate("a very long text")).isTrue();
        }

        @Test
        @DisplayName("static alwaysValid accepts anything")
        void alwaysValid() {
            assertThat(Validator.<String>alwaysValid().validate("anything")).isTrue();
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(FunctionalContractDemo::defaultAndStaticDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("3. Methods from Object do not count")
    class ObjectMethods {

        @Test
        @DisplayName("Labeled re-declares equals(Object) but has one counted abstract method")
        void labeledCount() {
            assertThat(FunctionalContractDemo.countAbstractMethods(Labeled.class)).isEqualTo(1);
        }

        @Test
        @DisplayName("a lambda implements Labeled")
        void labeledLambda() {
            Labeled item = () -> "inventory-item";

            assertThat(item.label()).isEqualTo("inventory-item");
        }

        @Test
        @DisplayName("Comparator, Runnable and Comparable are functional by the same rule")
        void jdkInterfaces() {
            assertThat(FunctionalContractDemo.countAbstractMethods(Comparator.class)).isEqualTo(1);
            assertThat(FunctionalContractDemo.countAbstractMethods(Runnable.class)).isEqualTo(1);
            assertThat(FunctionalContractDemo.countAbstractMethods(Comparable.class)).isEqualTo(1);
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(FunctionalContractDemo::objectMethodsDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("4. Generic functional interfaces")
    class GenericInterfaces {

        @Test
        @DisplayName("TriFunction takes three arguments")
        void threeArguments() {
            TriFunction<Integer, Integer, Integer, Integer> volume = (w, h, d) -> w * h * d;

            assertThat(volume.apply(2, 3, 4)).isEqualTo(24);
        }

        @Test
        @DisplayName("andThen post-processes the result and may change its type")
        void andThenChangesType() {
            TriFunction<Integer, Integer, Integer, Integer> volume = (w, h, d) -> w * h * d;

            TriFunction<Integer, Integer, Integer, String> described = volume.andThen(v -> v + " cm3");

            assertThat(described.apply(2, 3, 4)).isEqualTo("24 cm3");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(FunctionalContractDemo::genericInterfaceDemo).doesNotThrowAnyException();
        }
    }

    @Nested
    @DisplayName("5. The annotation is optional, the rule is not")
    class AnnotationOptional {

        @Test
        @DisplayName("TextOperation carries @FunctionalInterface, LegacyCallback does not")
        void annotationPresence() {
            assertThat(TextOperation.class.isAnnotationPresent(FunctionalInterface.class)).isTrue();
            assertThat(LegacyCallback.class.isAnnotationPresent(FunctionalInterface.class)).isFalse();
        }

        @Test
        @DisplayName("LegacyCallback has one abstract method and accepts a method reference")
        void legacyStillWorks() {
            StringBuilder log = new StringBuilder();
            LegacyCallback callback = log::append;

            callback.done("finished");

            assertThat(FunctionalContractDemo.countAbstractMethods(LegacyCallback.class)).isEqualTo(1);
            assertThat(log.toString()).isEqualTo("finished");
        }

        @Test
        @DisplayName("demo section runs without throwing")
        void demoRuns() {
            assertThatCode(FunctionalContractDemo::annotationDemo).doesNotThrowAnyException();
        }
    }
}
