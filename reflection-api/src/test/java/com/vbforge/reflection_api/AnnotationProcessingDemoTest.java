package com.vbforge.reflection_api;

import com.vbforge.reflection_api.AnnotationProcessingDemo.Admin;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Audited;
import com.vbforge.reflection_api.AnnotationProcessingDemo.ClassOnly;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Column;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Command;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Entity;
import com.vbforge.reflection_api.AnnotationProcessingDemo.NotBlank;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Range;
import com.vbforge.reflection_api.AnnotationProcessingDemo.SourceOnly;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Tag;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Tags;
import com.vbforge.reflection_api.AnnotationProcessingDemo.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("AnnotationProcessingDemo — API contracts")
class AnnotationProcessingDemoTest {

    @Nested
    @DisplayName("1. Retention decides visibility")
    class Retention {

        @Test
        @DisplayName("SOURCE annotations are invisible at runtime")
        void sourceInvisible() {
            assertThat(User.class.isAnnotationPresent(SourceOnly.class)).isFalse();
        }

        @Test
        @DisplayName("CLASS retention (the default) is invisible at runtime as well")
        void classInvisible() {
            assertThat(User.class.isAnnotationPresent(ClassOnly.class)).isFalse();
        }

        @Test
        @DisplayName("RUNTIME annotations are visible")
        void runtimeVisible() {
            assertThat(User.class.isAnnotationPresent(Entity.class)).isTrue();
            assertThat(User.class.isAnnotationPresent(Audited.class)).isTrue();
        }

        @Test
        @DisplayName("of six written annotations only Entity, Audited and the Tags container remain")
        void onlyRuntimeOnesRemain() {
            assertThat(User.class.getAnnotations()).hasSize(3);
            assertThat(User.class.isAnnotationPresent(Tags.class)).isTrue();
        }
    }

    @Nested
    @DisplayName("2. Reading annotations")
    class Reading {

        @Test
        @DisplayName("element values are read by calling the annotation's methods")
        void elementValue() {
            Entity entity = User.class.getAnnotation(Entity.class);

            assertThat(entity.table()).isEqualTo("users");
            assertThat(entity.annotationType()).isEqualTo(Entity.class);
        }

        @Test
        @DisplayName("an annotation instance is a JDK dynamic proxy")
        void annotationIsProxy() {
            Entity entity = User.class.getAnnotation(Entity.class);

            assertThat(Proxy.isProxyClass(entity.getClass())).isTrue();
        }

        @Test
        @DisplayName("field annotations: explicit and default element values")
        void fieldAnnotations() throws Exception {
            Field name = User.class.getDeclaredField("name");
            Field age = User.class.getDeclaredField("age");

            assertThat(name.getAnnotation(Column.class).value()).isEqualTo("user_name");
            assertThat(name.getAnnotation(NotBlank.class).message()).isEqualTo("must not be blank");
            assertThat(age.getAnnotation(Range.class).min()).isEqualTo(18);
            assertThat(age.getAnnotation(Range.class).max()).isEqualTo(120);
        }

        @Test
        @DisplayName("getAnnotation returns null when the annotation is absent")
        void absentIsNull() throws Exception {
            Field email = User.class.getDeclaredField("email");

            assertThat(email.getAnnotation(Column.class)).isNull();
            assertThat(email.isAnnotationPresent(Column.class)).isFalse();
        }

        @Test
        @DisplayName("method annotations: @Command value and built-in @Deprecated")
        void methodAnnotations() throws Exception {
            Method greet = User.class.getMethod("greet");
            Method legacy = User.class.getMethod("legacy");
            Method plain = User.class.getMethod("plain");

            assertThat(greet.getAnnotation(Command.class).value()).isEqualTo("greet");
            assertThat(legacy.isAnnotationPresent(Deprecated.class)).isTrue();
            assertThat(plain.getAnnotations()).isEmpty();
        }

        @Test
        @DisplayName("getDeclaredAnnotations equals getAnnotations when nothing is inherited")
        void declaredEqualsAllWithoutInheritance() {
            assertThat(User.class.getDeclaredAnnotations()).hasSize(User.class.getAnnotations().length);
        }
    }

    @Nested
    @DisplayName("3. @Inherited")
    class InheritedAnnotations {

        @Test
        @DisplayName("@Audited is @Inherited, so Admin sees it")
        void inheritedVisible() {
            assertThat(Admin.class.isAnnotationPresent(Audited.class)).isTrue();
        }

        @Test
        @DisplayName("@Entity is not @Inherited, so Admin does not see it")
        void notInheritedInvisible() {
            assertThat(Admin.class.isAnnotationPresent(Entity.class)).isFalse();
            assertThat(Admin.class.getAnnotation(Entity.class)).isNull();
        }

        @Test
        @DisplayName("getDeclaredAnnotations ignores inheritance, getAnnotations includes it")
        void declaredVersusAll() {
            assertThat(Admin.class.getDeclaredAnnotations()).isEmpty();
            assertThat(Admin.class.getAnnotations()).hasSize(1);
        }
    }

    @Nested
    @DisplayName("4. Repeatable annotations")
    class RepeatableAnnotations {

        @Test
        @DisplayName("getAnnotation(Tag) is null when @Tag is repeated")
        void directLookupFails() {
            assertThat(User.class.getAnnotation(Tag.class)).isNull();
        }

        @Test
        @DisplayName("the hidden container @Tags holds both values")
        void containerHoldsBoth() {
            Tags container = User.class.getAnnotation(Tags.class);

            assertThat(container.value()).hasSize(2);
        }

        @Test
        @DisplayName("getAnnotationsByType returns the values in declaration order")
        void byTypeKeepsOrder() {
            Tag[] tags = User.class.getAnnotationsByType(Tag.class);

            assertThat(tags).hasSize(2);
            assertThat(tags[0].value()).isEqualTo("core");
            assertThat(tags[1].value()).isEqualTo("public");
        }

        @Test
        @DisplayName("getAnnotationsByType returns an empty array when there is no @Tag")
        void byTypeEmpty() {
            assertThat(Admin.class.getAnnotationsByType(Tag.class)).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. Mini validator")
    class Validator {

        @Test
        @DisplayName("a valid bean produces no violations")
        void valid() {
            assertThat(AnnotationProcessingDemo.validate(new User("Ann", 30, null))).isEmpty();
        }

        @Test
        @DisplayName("a blank name violates @NotBlank")
        void blankName() {
            assertThat(AnnotationProcessingDemo.validate(new User("  ", 30, null)))
                    .containsExactly("name must not be blank");
        }

        @Test
        @DisplayName("a null name violates @NotBlank")
        void nullName() {
            assertThat(AnnotationProcessingDemo.validate(new User(null, 30, null)))
                    .containsExactly("name must not be blank");
        }

        @ParameterizedTest(name = "age {0} valid = {1}")
        @CsvSource({"17, false", "18, true", "50, true", "120, true", "121, false"})
        @DisplayName("@Range boundaries are inclusive")
        void rangeBoundaries(int age, boolean valid) {
            assertThat(AnnotationProcessingDemo.validate(new User("Ann", age, null)).isEmpty())
                    .isEqualTo(valid);
        }

        @Test
        @DisplayName("several violations are all reported, sorted")
        void multipleViolations() {
            assertThat(AnnotationProcessingDemo.validate(new User(" ", 10, null)))
                    .containsExactly("age must be between 18 and 120", "name must not be blank");
        }

        @Test
        @DisplayName("fields without validation annotations are ignored")
        void unannotatedIgnored() {
            assertThat(AnnotationProcessingDemo.validate(new User("Ann", 30, "anything"))).isEmpty();
        }
    }

    @Nested
    @DisplayName("6. Mini command dispatcher")
    class Dispatcher {

        @Test
        @DisplayName("dispatch invokes the method annotated with the command name")
        void dispatchesByName() {
            assertThat(AnnotationProcessingDemo.dispatch(new User("Ann", 30, null), "greet"))
                    .isEqualTo("Hello, Ann");
        }

        @Test
        @DisplayName("the invoked method can change state")
        void stateChanges() {
            User user = new User("Ann", 30, null);

            assertThat(AnnotationProcessingDemo.dispatch(user, "birthday")).isEqualTo(31);
            assertThat(AnnotationProcessingDemo.dispatch(user, "birthday")).isEqualTo(32);
        }

        @Test
        @DisplayName("inherited commands work: Admin answers to greet")
        void inheritedCommand() {
            assertThat(AnnotationProcessingDemo.dispatch(new Admin(), "greet")).isEqualTo("Hello, root");
        }

        @Test
        @DisplayName("an unknown command → IllegalArgumentException")
        void unknownCommand() {
            assertThatThrownBy(() -> AnnotationProcessingDemo.dispatch(new User("Ann", 30, null), "fly"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("fly");
        }

        @Test
        @DisplayName("a method without @Command cannot be dispatched, even if its name matches")
        void unannotatedNotDispatchable() {
            assertThatThrownBy(() -> AnnotationProcessingDemo.dispatch(new User("Ann", 30, null), "plain"))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }
}
