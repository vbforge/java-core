package com.vbforge.annotations;

import com.vbforge.annotations.RetentionAndTargetDemo.AllPolicies;
import com.vbforge.annotations.RetentionAndTargetDemo.Anywhere;
import com.vbforge.annotations.RetentionAndTargetDemo.ClassOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.Located;
import com.vbforge.annotations.RetentionAndTargetDemo.MethodOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.NoRetention;
import com.vbforge.annotations.RetentionAndTargetDemo.NonNull;
import com.vbforge.annotations.RetentionAndTargetDemo.OrderService;
import com.vbforge.annotations.RetentionAndTargetDemo.Registry;
import com.vbforge.annotations.RetentionAndTargetDemo.RuntimeVisible;
import com.vbforge.annotations.RetentionAndTargetDemo.SourceOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.Tag;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RetentionAndTargetTasks — your implementations")
class RetentionAndTargetTasksTest {

    // ---- fixtures -------------------------------------------------------------

    @Retention(RetentionPolicy.SOURCE)
    @interface Src {
    }

    @Retention(RetentionPolicy.CLASS)
    @interface Cls {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface RunA {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @interface RunB {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target({ElementType.FIELD, ElementType.METHOD})
    @interface FieldOrMethod {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Param {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.PARAMETER)
    @interface Extra {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    @interface TypeOnly {
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE_USE)
    @interface TypeOther {
    }

    /** Written in a different order than the sorted result. */
    @RunB
    @Src
    @RunA
    @Cls
    static class TwoRuntime {
    }

    static class Params {
        public void m(@Param @Extra String first, int second, @Param int third) {
        }
    }

    static class TypeAnnotated {
        public @TypeOther @TypeOnly String both() {
            return "";
        }

        @Deprecated
        public String old() {
            return "";
        }
    }

    private RetentionAndTargetTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new RetentionAndTargetTasks();
    }

    // ---- tasks ----------------------------------------------------------------

    @Nested
    @Disabled("Remove @Disabled when you implement retentionOf()")
    @DisplayName("retentionOf")
    class RetentionOfTest {

        @Test
        @DisplayName("RUNTIME (Javadoc example)")
        void runtime() {
            assertThat(tasks.retentionOf(RuntimeVisible.class)).isEqualTo(RetentionPolicy.RUNTIME);
        }

        @Test
        @DisplayName("SOURCE (Javadoc example)")
        void source() {
            assertThat(tasks.retentionOf(SourceOnly.class)).isEqualTo(RetentionPolicy.SOURCE);
        }

        @Test
        @DisplayName("CLASS declared explicitly (Javadoc example)")
        void classPolicy() {
            assertThat(tasks.retentionOf(ClassOnly.class)).isEqualTo(RetentionPolicy.CLASS);
        }

        @Test
        @DisplayName("No @Retention -> default CLASS (Javadoc example)")
        void defaultPolicy() {
            assertThat(tasks.retentionOf(NoRetention.class)).isEqualTo(RetentionPolicy.CLASS);
        }

        @Test
        @DisplayName("Built-in annotations: @Override is SOURCE, @Deprecated is RUNTIME")
        void builtIns() {
            assertThat(tasks.retentionOf(Override.class)).isEqualTo(RetentionPolicy.SOURCE);
            assertThat(tasks.retentionOf(Deprecated.class)).isEqualTo(RetentionPolicy.RUNTIME);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement targetsOf()")
    @DisplayName("targetsOf")
    class TargetsOfTest {

        @Test
        @DisplayName("Several targets, sorted by name (Javadoc example)")
        void several() {
            assertThat(tasks.targetsOf(Tag.class))
                    .containsExactly("CONSTRUCTOR", "FIELD", "METHOD", "PARAMETER", "TYPE");
        }

        @Test
        @DisplayName("Single target (Javadoc example)")
        void single() {
            assertThat(tasks.targetsOf(MethodOnly.class)).containsExactly("METHOD");
        }

        @Test
        @DisplayName("No @Target -> empty list (Javadoc example)")
        void none() {
            assertThat(tasks.targetsOf(Anywhere.class)).isEmpty();
        }

        @Test
        @DisplayName("Declaration order does not matter, result is sorted")
        void sortedRegardlessOfDeclaration() {
            assertThat(tasks.targetsOf(FieldOrMethod.class)).containsExactly("FIELD", "METHOD");
        }

        @Test
        @DisplayName("Java 8 element type TYPE_USE is reported by name")
        void typeUse() {
            assertThat(tasks.targetsOf(NonNull.class)).containsExactly("TYPE_USE");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement canApplyTo()")
    @DisplayName("canApplyTo")
    class CanApplyToTest {

        @Test
        @DisplayName("Listed location -> true (Javadoc example)")
        void listed() {
            assertThat(tasks.canApplyTo(MethodOnly.class, ElementType.METHOD)).isTrue();
        }

        @Test
        @DisplayName("Unlisted location -> false (Javadoc example)")
        void unlisted() {
            assertThat(tasks.canApplyTo(MethodOnly.class, ElementType.FIELD)).isFalse();
        }

        @Test
        @DisplayName("No @Target: any declaration is allowed (Javadoc example)")
        void noTargetDeclaration() {
            assertThat(tasks.canApplyTo(Anywhere.class, ElementType.FIELD)).isTrue();
        }

        @Test
        @DisplayName("No @Target: never a type annotation (Javadoc example)")
        void noTargetTypeUse() {
            assertThat(tasks.canApplyTo(Anywhere.class, ElementType.TYPE_USE)).isFalse();
        }

        @Test
        @DisplayName("TYPE_USE listed -> true (Javadoc example)")
        void typeUseListed() {
            assertThat(tasks.canApplyTo(NonNull.class, ElementType.TYPE_USE)).isTrue();
        }

        @ParameterizedTest(name = "@MethodOnly at {0} -> {1}")
        @CsvSource({
                "METHOD,      true",
                "FIELD,       false",
                "TYPE,        false",
                "PARAMETER,   false",
                "CONSTRUCTOR, false"
        })
        @DisplayName("@MethodOnly across several locations")
        void parametrized(ElementType location, boolean expected) {
            assertThat(tasks.canApplyTo(MethodOnly.class, location)).isEqualTo(expected);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement runtimeVisibleAnnotationNames()")
    @DisplayName("runtimeVisibleAnnotationNames")
    class RuntimeVisibleAnnotationNamesTest {

        @Test
        @DisplayName("Only the RUNTIME one of four survives (Javadoc example)")
        void onlyRuntime() {
            assertThat(tasks.runtimeVisibleAnnotationNames(AllPolicies.class)).containsExactly("RuntimeVisible");
        }

        @Test
        @DisplayName("Composed annotation (Javadoc example)")
        void composed() {
            assertThat(tasks.runtimeVisibleAnnotationNames(OrderService.class)).containsExactly("Service");
        }

        @Test
        @DisplayName("No annotations -> empty (Javadoc example)")
        void none() {
            assertThat(tasks.runtimeVisibleAnnotationNames(Registry.class)).isEmpty();
        }

        @Test
        @DisplayName("Several runtime annotations are sorted regardless of written order")
        void sorted() {
            assertThat(tasks.runtimeVisibleAnnotationNames(TwoRuntime.class)).containsExactly("RunA", "RunB");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement parameterAnnotationCounts()")
    @DisplayName("parameterAnnotationCounts")
    class ParameterAnnotationCountsTest {

        @Test
        @DisplayName("One annotated, one plain parameter (Javadoc example)")
        void annotatedAndPlain() throws Exception {
            Method method = Located.class.getMethod("method", String.class, int.class);
            assertThat(tasks.parameterAnnotationCounts(method)).containsExactly(1, 0);
        }

        @Test
        @DisplayName("No parameters -> empty array (Javadoc example)")
        void noParameters() throws Exception {
            assertThat(tasks.parameterAnnotationCounts(Registry.class.getMethod("plain"))).isEmpty();
        }

        @Test
        @DisplayName("Type annotation is not a parameter annotation (Javadoc example)")
        void typeAnnotationNotCounted() throws Exception {
            Method lookup = Registry.class.getMethod("lookup", String.class);
            assertThat(tasks.parameterAnnotationCounts(lookup)).containsExactly(0);
        }

        @Test
        @DisplayName("Several annotations on one parameter are all counted")
        void severalOnOne() throws Exception {
            Method m = Params.class.getMethod("m", String.class, int.class, int.class);
            assertThat(tasks.parameterAnnotationCounts(m)).containsExactly(2, 0, 1);
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement returnTypeAnnotationNames()")
    @DisplayName("returnTypeAnnotationNames")
    class ReturnTypeAnnotationNamesTest {

        @Test
        @DisplayName("@NonNull on the return type (Javadoc example)")
        void nonNull() throws Exception {
            Method lookup = Registry.class.getMethod("lookup", String.class);
            assertThat(tasks.returnTypeAnnotationNames(lookup)).containsExactly("NonNull");
        }

        @Test
        @DisplayName("No type annotations -> empty (Javadoc example)")
        void none() throws Exception {
            assertThat(tasks.returnTypeAnnotationNames(Registry.class.getMethod("plain"))).isEmpty();
        }

        @Test
        @DisplayName("Declaration annotation on the method is not a type annotation (Javadoc example)")
        void declarationAnnotationIgnored() throws Exception {
            Method method = Located.class.getMethod("method", String.class, int.class);
            assertThat(tasks.returnTypeAnnotationNames(method)).isEmpty();
        }

        @Test
        @DisplayName("Several type annotations are sorted regardless of written order")
        void sorted() throws Exception {
            assertThat(tasks.returnTypeAnnotationNames(TypeAnnotated.class.getMethod("both")))
                    .containsExactly("TypeOnly", "TypeOther");
        }

        @Test
        @DisplayName("@Deprecated on the method does not leak into the return type")
        void deprecatedNotCounted() throws Exception {
            assertThat(tasks.returnTypeAnnotationNames(TypeAnnotated.class.getMethod("old"))).isEmpty();
        }
    }
}
