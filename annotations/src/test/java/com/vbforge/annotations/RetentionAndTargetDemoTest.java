package com.vbforge.annotations;

import com.vbforge.annotations.RetentionAndTargetDemo.AllPolicies;
import com.vbforge.annotations.RetentionAndTargetDemo.Anywhere;
import com.vbforge.annotations.RetentionAndTargetDemo.Box;
import com.vbforge.annotations.RetentionAndTargetDemo.ClassOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.LocalOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.Located;
import com.vbforge.annotations.RetentionAndTargetDemo.MethodOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.NoRetention;
import com.vbforge.annotations.RetentionAndTargetDemo.NonNull;
import com.vbforge.annotations.RetentionAndTargetDemo.OrderService;
import com.vbforge.annotations.RetentionAndTargetDemo.Published;
import com.vbforge.annotations.RetentionAndTargetDemo.Registry;
import com.vbforge.annotations.RetentionAndTargetDemo.RuntimeVisible;
import com.vbforge.annotations.RetentionAndTargetDemo.Service;
import com.vbforge.annotations.RetentionAndTargetDemo.SourceOnly;
import com.vbforge.annotations.RetentionAndTargetDemo.Stereotype;
import com.vbforge.annotations.RetentionAndTargetDemo.Tag;
import com.vbforge.annotations.RetentionAndTargetDemo.Unit;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.*;
import java.lang.reflect.AnnotatedParameterizedType;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("RetentionAndTargetDemo — API contracts")
class RetentionAndTargetDemoTest {

    @Nested
    @DisplayName("1. @Retention")
    class RetentionSection {

        @Test
        @DisplayName("Declared policies are SOURCE, CLASS and RUNTIME")
        void declaredPolicies() {
            assertThat(SourceOnly.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.SOURCE);
            assertThat(ClassOnly.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.CLASS);
            assertThat(RuntimeVisible.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME);
        }

        @Test
        @DisplayName("Only the RUNTIME annotation is visible to reflection")
        void onlyRuntimeVisible() {
            assertThat(AllPolicies.class.isAnnotationPresent(RuntimeVisible.class)).isTrue();
            assertThat(AllPolicies.class.isAnnotationPresent(SourceOnly.class)).isFalse();
            assertThat(AllPolicies.class.isAnnotationPresent(ClassOnly.class)).isFalse();
        }

        @Test
        @DisplayName("Four annotations written, one visible")
        void oneOfFour() {
            assertThat(AllPolicies.class.getAnnotations()).hasSize(1);
        }

        @Test
        @DisplayName("Without @Retention the default (CLASS) applies: invisible at runtime")
        void defaultRetentionIsClass() {
            assertThat(NoRetention.class.isAnnotationPresent(Retention.class)).isFalse();
            assertThat(AllPolicies.class.isAnnotationPresent(NoRetention.class)).isFalse();
        }

        @Test
        @DisplayName("@Retention itself is RUNTIME so it can be read by reflection")
        void retentionIsRuntime() {
            assertThat(Retention.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME);
        }
    }

    @Nested
    @DisplayName("2. @Target")
    class TargetSection {

        @Test
        @DisplayName("@Target lists the allowed element types")
        void targetValues() {
            assertThat(Tag.class.getAnnotation(Target.class).value()).containsExactlyInAnyOrder(
                    ElementType.TYPE, ElementType.FIELD, ElementType.METHOD,
                    ElementType.PARAMETER, ElementType.CONSTRUCTOR);
            assertThat(MethodOnly.class.getAnnotation(Target.class).value()).containsExactly(ElementType.METHOD);
        }

        @Test
        @DisplayName("An annotation without @Target has no Target meta-annotation")
        void noTarget() {
            assertThat(Anywhere.class.isAnnotationPresent(Target.class)).isFalse();
        }

        @Test
        @DisplayName("Read the annotation from type, field, constructor and method")
        void readFromEveryLocation() throws Exception {
            assertThat(Located.class.getAnnotation(Tag.class).value()).isEqualTo("type");
            assertThat(Located.class.getField("field").getAnnotation(Tag.class).value()).isEqualTo("field");
            assertThat(Located.class.getConstructor().getAnnotation(Tag.class).value()).isEqualTo("ctor");
            assertThat(Located.class.getMethod("method", String.class, int.class)
                    .getAnnotation(Tag.class).value()).isEqualTo("method");
        }

        @Test
        @DisplayName("Parameter annotations: one array per parameter, empty when unannotated")
        void parameterAnnotations() throws Exception {
            Method method = Located.class.getMethod("method", String.class, int.class);
            Annotation[][] perParameter = method.getParameterAnnotations();

            assertThat(perParameter.length).isEqualTo(2);
            assertThat(perParameter[0]).hasSize(1);
            assertThat(perParameter[1]).isEmpty();
            assertThat(((Tag) perParameter[0][0]).value()).isEqualTo("param");
        }

        @Test
        @DisplayName("@MethodOnly is found on the method")
        void methodOnlyOnMethod() throws Exception {
            assertThat(Located.class.getMethod("method", String.class, int.class)
                    .isAnnotationPresent(MethodOnly.class)).isTrue();
        }

        @Test
        @DisplayName("LOCAL_VARIABLE target is declared even though locals cannot be read at runtime")
        void localVariableTarget() {
            assertThat(LocalOnly.class.getAnnotation(Target.class).value())
                    .containsExactly(ElementType.LOCAL_VARIABLE);
            assertThat(LocalOnly.class.getAnnotation(Retention.class).value()).isEqualTo(RetentionPolicy.RUNTIME);
        }
    }

    @Nested
    @DisplayName("3. Meta-annotations")
    class MetaAnnotationSection {

        @Test
        @DisplayName("A composed annotation carries the custom meta-annotation")
        void composedAnnotation() {
            assertThat(Service.class.isAnnotationPresent(Stereotype.class)).isTrue();
            assertThat(Service.class.getAnnotation(Stereotype.class).value()).isEqualTo("service");
        }

        @Test
        @DisplayName("Annotation types expose all their meta-annotations")
        void metaAnnotationCount() {
            assertThat(Service.class.getAnnotations()).hasSize(3); // Stereotype, Retention, Target
        }

        @Test
        @DisplayName("stereotypeOf finds the stereotype through the composed annotation")
        void stereotypeLookup() {
            assertThat(RetentionAndTargetDemo.stereotypeOf(OrderService.class)).isEqualTo(Optional.of("service"));
        }

        @Test
        @DisplayName("stereotypeOf is empty for a class without a composed annotation")
        void stereotypeAbsent() {
            assertThat(RetentionAndTargetDemo.stereotypeOf(Registry.class)).isEmpty();
        }

        @Test
        @DisplayName("@Stereotype may only be placed on annotation types")
        void stereotypeTarget() {
            assertThat(Stereotype.class.getAnnotation(Target.class).value())
                    .containsExactly(ElementType.ANNOTATION_TYPE);
        }

        @Test
        @DisplayName("@Documented is readable on annotation types that use it")
        void documented() {
            assertThat(Published.class.isAnnotationPresent(Documented.class)).isTrue();
            assertThat(Service.class.isAnnotationPresent(Documented.class)).isFalse();
        }
    }

    @Nested
    @DisplayName("4. Type annotations (Java 8)")
    class TypeAnnotationSection {

        @Test
        @DisplayName("TYPE_USE-only annotation is NOT a declaration annotation on the method")
        void notOnMethod() throws Exception {
            Method lookup = Registry.class.getMethod("lookup", String.class);
            assertThat(lookup.getAnnotation(NonNull.class)).isNull();
        }

        @Test
        @DisplayName("TYPE_USE annotation is found on the annotated return type")
        void onReturnType() throws Exception {
            Method lookup = Registry.class.getMethod("lookup", String.class);
            assertThat(lookup.getAnnotatedReturnType().isAnnotationPresent(NonNull.class)).isTrue();
        }

        @Test
        @DisplayName("TYPE_USE annotation is found on the annotated parameter type, not as a parameter annotation")
        void onParameterType() throws Exception {
            Method lookup = Registry.class.getMethod("lookup", String.class);
            assertThat(lookup.getAnnotatedParameterTypes()[0].isAnnotationPresent(NonNull.class)).isTrue();
            assertThat(lookup.getParameterAnnotations()[0]).isEmpty();
        }

        @Test
        @DisplayName("TYPE_USE annotation inside a generic type argument: List<@NonNull String>")
        void onTypeArgument() throws Exception {
            AnnotatedParameterizedType names = (AnnotatedParameterizedType)
                    Registry.class.getMethod("names").getAnnotatedReturnType();
            assertThat(names.getAnnotatedActualTypeArguments()[0].isAnnotationPresent(NonNull.class)).isTrue();
            assertThat(names.isAnnotationPresent(NonNull.class)).isFalse();
        }

        @Test
        @DisplayName("Unannotated method has no type annotations")
        void plainMethod() throws Exception {
            Method plain = Registry.class.getMethod("plain");
            assertThat(plain.getAnnotatedReturnType().getAnnotations()).isEmpty();
        }

        @Test
        @DisplayName("TYPE_PARAMETER annotation is readable from the TypeVariable")
        void onTypeParameter() {
            assertThat(Box.class.getTypeParameters()[0].isAnnotationPresent(Unit.class)).isTrue();
        }
    }
}
