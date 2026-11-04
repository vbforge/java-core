package com.vbforge.annotations;

import com.vbforge.annotations.BuiltInAnnotationsDemo.BrokenPoint;
import com.vbforge.annotations.BuiltInAnnotationsDemo.Calculator;
import com.vbforge.annotations.BuiltInAnnotationsDemo.Dog;
import com.vbforge.annotations.BuiltInAnnotationsDemo.FixedPoint;
import com.vbforge.annotations.BuiltInAnnotationsDemo.Transformer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.Serializable;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BuiltInAnnotationsTasks — your implementations")
class BuiltInAnnotationsTasksTest {

    // ---- fixtures -------------------------------------------------------------

    /** Not deprecated and declares no equals — nothing special. */
    static class Plain {
        public void run() {
        }
    }

    /** Declares a no-arg deprecated method and a private deprecated one. */
    static class Mixed {
        @Deprecated
        public void oldNoArg() {
        }

        public void fresh() {
        }

        @Deprecated
        private void hidden() {
        }
    }

    /** One abstract method inherited from the parent, none added. */
    interface Child extends Transformer {
    }

    /** Two abstract methods. */
    interface TwoMethods {
        void a();

        void b();
    }

    /** One abstract method plus a static and a default one. */
    interface WithExtras {
        void only();

        default void extra() {
        }

        static void util() {
        }
    }

    /** Extends a one-method interface and adds a second abstract method. */
    interface Widened extends WithExtras {
        void another();
    }

    /** Declares both the real equals(Object) and an equals(BothEquals) overload. */
    static class BothEquals {
        @Override
        public boolean equals(Object o) {
            return o == this;
        }

        @Override
        public int hashCode() {
            return 1;
        }

        public boolean equals(BothEquals other) {
            return other == this;
        }
    }

    /** Inherits a real equals from a parent but declares nothing itself. */
    static class InheritsEquals extends FixedPoint {
        InheritsEquals() {
            super(0, 0);
        }
    }

    private BuiltInAnnotationsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new BuiltInAnnotationsTasks();
    }

    // ---- tasks ----------------------------------------------------------------

    @Nested
    @Disabled("Remove @Disabled when you implement isDeprecatedMethod()")
    @DisplayName("isDeprecatedMethod")
    class IsDeprecatedMethodTest {

        @Test
        @DisplayName("Deprecated method -> true (Javadoc example)")
        void deprecatedMethod() {
            assertThat(tasks.isDeprecatedMethod(Calculator.class, "sum", int.class, int.class)).isTrue();
        }

        @Test
        @DisplayName("Regular method -> false (Javadoc example)")
        void regularMethod() {
            assertThat(tasks.isDeprecatedMethod(Calculator.class, "add", int.class, int.class)).isFalse();
        }

        @Test
        @DisplayName("Missing method -> IllegalArgumentException (Javadoc example)")
        void missingMethod() {
            assertThatThrownBy(() -> tasks.isDeprecatedMethod(Calculator.class, "missing", int.class))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("No-arg method needs no parameter types")
        void noArgMethod() {
            assertThat(tasks.isDeprecatedMethod(Mixed.class, "oldNoArg")).isTrue();
            assertThat(tasks.isDeprecatedMethod(Mixed.class, "fresh")).isFalse();
        }

        @Test
        @DisplayName("Right name but wrong parameter types -> IllegalArgumentException")
        void wrongParameterTypes() {
            assertThatThrownBy(() -> tasks.isDeprecatedMethod(Calculator.class, "sum", String.class))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("Non-public method is not found by a public lookup")
        void privateMethodNotFound() {
            assertThatThrownBy(() -> tasks.isDeprecatedMethod(Mixed.class, "hidden"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest(name = "{0} deprecated? {1}")
        @CsvSource({
                "add,           false",
                "sum,           true",
                "multiply,      false",
                "legacyMultiply, true"
        })
        @DisplayName("All Calculator methods")
        void parametrized(String method, boolean expected) {
            assertThat(tasks.isDeprecatedMethod(Calculator.class, method, int.class, int.class))
                    .isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement deprecatedMethodNames()")
    @DisplayName("deprecatedMethodNames")
    class DeprecatedMethodNamesTest {

        @Test
        @DisplayName("Calculator -> [legacyMultiply, sum] (Javadoc example)")
        void calculator() {
            assertThat(tasks.deprecatedMethodNames(Calculator.class))
                    .containsExactly("legacyMultiply", "sum");
        }

        @Test
        @DisplayName("Class without deprecated members -> empty (Javadoc example)")
        void noneDeprecated() {
            assertThat(tasks.deprecatedMethodNames(Dog.class)).isEmpty();
        }

        @Test
        @DisplayName("Private deprecated methods are included")
        void includesPrivate() {
            assertThat(tasks.deprecatedMethodNames(Mixed.class)).containsExactly("hidden", "oldNoArg");
        }

        @Test
        @DisplayName("Inherited methods are not included")
        void ignoresInherited() {
            assertThat(tasks.deprecatedMethodNames(Plain.class)).isEmpty();
        }

        @Test
        @DisplayName("Result is a List<String>, sorted")
        void sortedList() {
            List<String> names = tasks.deprecatedMethodNames(Mixed.class);
            assertThat(names).isSorted();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isStructurallyFunctional()")
    @DisplayName("isStructurallyFunctional")
    class IsStructurallyFunctionalTest {

        @Test
        @DisplayName("Runnable -> true (Javadoc example)")
        void runnable() {
            assertThat(tasks.isStructurallyFunctional(Runnable.class)).isTrue();
        }

        @Test
        @DisplayName("Transformer with default + static methods -> true (Javadoc example)")
        void transformer() {
            assertThat(tasks.isStructurallyFunctional(Transformer.class)).isTrue();
        }

        @Test
        @DisplayName("List -> false (Javadoc example)")
        void list() {
            assertThat(tasks.isStructurallyFunctional(List.class)).isFalse();
        }

        @Test
        @DisplayName("Marker interface -> false (Javadoc example)")
        void marker() {
            assertThat(tasks.isStructurallyFunctional(Serializable.class)).isFalse();
        }

        @Test
        @DisplayName("Class -> false (Javadoc example)")
        void notAnInterface() {
            assertThat(tasks.isStructurallyFunctional(String.class)).isFalse();
        }

        @Test
        @DisplayName("Two abstract methods -> false")
        void twoAbstract() {
            assertThat(tasks.isStructurallyFunctional(TwoMethods.class)).isFalse();
        }

        @Test
        @DisplayName("Default and static methods do not count")
        void defaultAndStaticIgnored() {
            assertThat(tasks.isStructurallyFunctional(WithExtras.class)).isTrue();
        }

        @Test
        @DisplayName("Inherited abstract method counts: child with nothing new -> true")
        void inheritedSingle() {
            assertThat(tasks.isStructurallyFunctional(Child.class)).isTrue();
        }

        @Test
        @DisplayName("Inherited abstract method counts: child adding one -> false")
        void inheritedPlusOne() {
            assertThat(tasks.isStructurallyFunctional(Widened.class)).isFalse();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement overridesObjectEquals()")
    @DisplayName("overridesObjectEquals")
    class OverridesObjectEqualsTest {

        @Test
        @DisplayName("equals(Object) declared -> true (Javadoc example)")
        void realOverride() {
            assertThat(tasks.overridesObjectEquals(FixedPoint.class)).isTrue();
        }

        @Test
        @DisplayName("Only an overload declared -> false (Javadoc example)")
        void onlyOverload() {
            assertThat(tasks.overridesObjectEquals(BrokenPoint.class)).isFalse();
        }

        @Test
        @DisplayName("No equals at all -> false (Javadoc example)")
        void noEquals() {
            assertThat(tasks.overridesObjectEquals(Calculator.class)).isFalse();
        }

        @Test
        @DisplayName("Both overload and override declared -> true")
        void both() {
            assertThat(tasks.overridesObjectEquals(BothEquals.class)).isTrue();
        }

        @Test
        @DisplayName("Inherited equals(Object) does not count: only the class itself is inspected")
        void inheritedDoesNotCount() {
            assertThat(tasks.overridesObjectEquals(InheritsEquals.class)).isFalse();
        }
    }
}
