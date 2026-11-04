package com.vbforge.annotations;

import com.vbforge.annotations.BuiltInAnnotationsDemo.Animal;
import com.vbforge.annotations.BuiltInAnnotationsDemo.BrokenPoint;
import com.vbforge.annotations.BuiltInAnnotationsDemo.Calculator;
import com.vbforge.annotations.BuiltInAnnotationsDemo.Dog;
import com.vbforge.annotations.BuiltInAnnotationsDemo.Transformer;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("BuiltInAnnotationsDemo — API contracts")
class BuiltInAnnotationsDemoTest {

    @Nested
    @DisplayName("1. @Override")
    class OverrideSection {

        @Test
        @DisplayName("Overridden method is chosen at runtime (polymorphism)")
        void overriddenMethodWins() {
            Animal animal = new Dog();
            assertThat(animal.sound()).isEqualTo("Woof");
            assertThat(new Animal().sound()).isEqualTo("...");
        }

        @Test
        @DisplayName("@Override has SOURCE retention")
        void overrideIsSourceRetained() {
            assertThat(Override.class.getAnnotation(Retention.class).value())
                    .isEqualTo(RetentionPolicy.SOURCE);
        }

        @Test
        @DisplayName("@Override is erased: not visible on the overriding method")
        void overrideInvisibleAtRuntime() throws Exception {
            assertThat(Dog.class.getMethod("sound").isAnnotationPresent(Override.class)).isFalse();
        }

        @Test
        @DisplayName("equals(BrokenPoint) is an overload: direct call works")
        void overloadWorksWithStaticType() {
            assertThat(new BrokenPoint(1, 2).equals(new BrokenPoint(1, 2))).isTrue();
        }

        @Test
        @DisplayName("equals(BrokenPoint) is an overload: collections never use it")
        void overloadIgnoredByCollections() {
            assertThat(BuiltInAnnotationsDemo.brokenPointFoundInList()).isFalse();
        }

        @Test
        @DisplayName("equals(Object) with @Override works in collections")
        void realOverrideWorksInCollections() {
            assertThat(BuiltInAnnotationsDemo.fixedPointFoundInList()).isTrue();
        }
    }

    @Nested
    @DisplayName("2. @Deprecated")
    class DeprecatedSection {

        @Test
        @DisplayName("Deprecated method still works by delegating to its replacement")
        void deprecatedStillWorks() {
            Calculator calc = new Calculator();
            assertThat(calc.add(2, 3)).isEqualTo(5);
            assertThat(calc.sum(2, 3)).isEqualTo(calc.add(2, 3));
            assertThat(calc.legacyMultiply(4, 5)).isEqualTo(calc.multiply(4, 5));
        }

        @Test
        @DisplayName("@Deprecated is visible at runtime on the deprecated method")
        void deprecatedVisible() throws Exception {
            assertThat(Calculator.class.getMethod("sum", int.class, int.class)
                    .isAnnotationPresent(Deprecated.class)).isTrue();
        }

        @Test
        @DisplayName("@Deprecated is absent on the replacement method")
        void replacementNotDeprecated() throws Exception {
            assertThat(Calculator.class.getMethod("add", int.class, int.class)
                    .isAnnotationPresent(Deprecated.class)).isFalse();
        }

        @Test
        @DisplayName("@Deprecated has RUNTIME retention")
        void deprecatedIsRuntimeRetained() {
            assertThat(Deprecated.class.getAnnotation(Retention.class).value())
                    .isEqualTo(RetentionPolicy.RUNTIME);
        }
    }

    @Nested
    @DisplayName("3. @SuppressWarnings")
    class SuppressWarningsSection {

        @Test
        @DisplayName("Suppressed unchecked cast works when the contents really are Strings")
        void safeUncheckedCast() {
            assertThat(BuiltInAnnotationsDemo.uncheckedCast()).containsExactly("a", "b");
        }

        @Test
        @DisplayName("Suppressing a warning does not remove the risk: ClassCastException")
        void pollutedReadFails() {
            assertThatThrownBy(BuiltInAnnotationsDemo::pollutedRead)
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("@SuppressWarnings has SOURCE retention")
        void suppressWarningsIsSourceRetained() {
            assertThat(SuppressWarnings.class.getAnnotation(Retention.class).value())
                    .isEqualTo(RetentionPolicy.SOURCE);
        }

        @Test
        @DisplayName("@SuppressWarnings is invisible to reflection")
        void suppressWarningsInvisibleAtRuntime() throws Exception {
            assertThat(BuiltInAnnotationsDemo.class.getMethod("uncheckedCast")
                    .isAnnotationPresent(SuppressWarnings.class)).isFalse();
        }
    }

    @Nested
    @DisplayName("4. @FunctionalInterface")
    class FunctionalInterfaceSection {

        @Test
        @DisplayName("Method reference and lambda compose via the default method")
        void composeTransformers() {
            assertThat(BuiltInAnnotationsDemo.shout("hi")).isEqualTo("HI!");
        }

        @Test
        @DisplayName("Static method on the interface is callable")
        void staticFactory() {
            assertThat(Transformer.identity().apply("same")).isEqualTo("same");
        }

        @Test
        @DisplayName("Comparator re-declares equals(Object) yet works as a lambda target")
        void comparatorLambda() {
            List<String> sorted = BuiltInAnnotationsDemo.sortByLength(Arrays.asList("ccc", "a", "bb"));
            assertThat(sorted).containsExactly("a", "bb", "ccc");
        }

        @Test
        @DisplayName("@FunctionalInterface is visible at runtime")
        void visibleAtRuntime() {
            assertThat(Transformer.class.isAnnotationPresent(FunctionalInterface.class)).isTrue();
            assertThat(FunctionalInterface.class.getAnnotation(Retention.class).value())
                    .isEqualTo(RetentionPolicy.RUNTIME);
        }
    }

    @Nested
    @DisplayName("5. @SafeVarargs")
    class SafeVarargsSection {

        @Test
        @DisplayName("Safe generic varargs method builds a list")
        void listOfBuildsList() {
            assertThat(BuiltInAnnotationsDemo.listOf("x", "y", "z")).containsExactly("x", "y", "z");
        }

        @Test
        @DisplayName("Result is a defensive copy, unmodifiable")
        void listOfIsUnmodifiable() {
            List<String> list = BuiltInAnnotationsDemo.listOf("x");
            assertThatThrownBy(() -> list.add("y")).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("@SafeVarargs is visible at runtime on the annotated method")
        void safeVarargsVisible() throws Exception {
            assertThat(BuiltInAnnotationsDemo.class.getMethod("listOf", Object[].class)
                    .isAnnotationPresent(SafeVarargs.class)).isTrue();
        }

        @Test
        @DisplayName("Leaking the varargs array from a generic method causes ClassCastException")
        void leakedArrayFails() {
            assertThatThrownBy(BuiltInAnnotationsDemo::unsafeStringPair)
                    .isInstanceOf(ClassCastException.class);
        }

        @Test
        @DisplayName("Called directly with a concrete type the array is a real String[]")
        void directCallKeepsRealType() {
            String[] strings = BuiltInAnnotationsDemo.toArray("a", "b");
            assertThat(strings).containsExactly("a", "b");
        }
    }
}
