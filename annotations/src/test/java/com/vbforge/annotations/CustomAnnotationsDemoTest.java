package com.vbforge.annotations;

import com.vbforge.annotations.CustomAnnotationsDemo.Annotated;
import com.vbforge.annotations.CustomAnnotationsDemo.Audited;
import com.vbforge.annotations.CustomAnnotationsDemo.BaseService;
import com.vbforge.annotations.CustomAnnotationsDemo.ChildService;
import com.vbforge.annotations.CustomAnnotationsDemo.ContractImpl;
import com.vbforge.annotations.CustomAnnotationsDemo.Info;
import com.vbforge.annotations.CustomAnnotationsDemo.Marker;
import com.vbforge.annotations.CustomAnnotationsDemo.MultiRole;
import com.vbforge.annotations.CustomAnnotationsDemo.NoRole;
import com.vbforge.annotations.CustomAnnotationsDemo.Person;
import com.vbforge.annotations.CustomAnnotationsDemo.Priority;
import com.vbforge.annotations.CustomAnnotationsDemo.Role;
import com.vbforge.annotations.CustomAnnotationsDemo.Roles;
import com.vbforge.annotations.CustomAnnotationsDemo.SingleRole;
import com.vbforge.annotations.CustomAnnotationsDemo.Ticket;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("CustomAnnotationsDemo — API contracts")
class CustomAnnotationsDemoTest {

    @Nested
    @DisplayName("1. Declaring annotations")
    class DeclaringSection {

        @Test
        @DisplayName("Marker annotation: presence alone carries the information")
        void markerPresent() {
            assertThat(Annotated.class.getAnnotation(Marker.class)).isNotNull();
            assertThat(NoRole.class.getAnnotation(Marker.class)).isNull();
        }

        @Test
        @DisplayName("Explicit element values are returned as written")
        void explicitValues() {
            Ticket ticket = Annotated.class.getAnnotation(Ticket.class);
            assertThat(ticket.id()).isEqualTo("BUG-42");
            assertThat(ticket.priority()).isEqualTo(Priority.HIGH);
            assertThat(ticket.labels()).containsExactly("ui", "urgent");
            assertThat(ticket.estimateHours()).isEqualTo(5);
        }

        @Test
        @DisplayName("Omitted elements take their declared defaults")
        void defaultValues() throws Exception {
            Ticket ticket = Annotated.class.getMethod("work").getAnnotation(Ticket.class);
            assertThat(ticket.id()).isEqualTo("T-1");
            assertThat(ticket.priority()).isEqualTo(Priority.MEDIUM);
            assertThat(ticket.labels()).isEmpty();
            assertThat(ticket.estimateHours()).isEqualTo(1);
            assertThat(ticket.owner()).isEqualTo(Void.class);
        }

        @Test
        @DisplayName("A single array element may be written without braces")
        void singleElementArray() throws Exception {
            Ticket ticket = Annotated.class.getMethod("solo").getAnnotation(Ticket.class);
            assertThat(ticket.labels()).containsExactly("solo");
        }

        @Test
        @DisplayName("Defaults are available through Method.getDefaultValue()")
        void defaultsViaReflection() throws Exception {
            assertThat(Ticket.class.getMethod("priority").getDefaultValue()).isEqualTo(Priority.MEDIUM);
            assertThat(Ticket.class.getMethod("estimateHours").getDefaultValue()).isEqualTo(1);
            assertThat(Ticket.class.getMethod("id").getDefaultValue()).isNull();
        }
    }

    @Nested
    @DisplayName("2. Single-value shorthand")
    class ShorthandSection {

        @Test
        @DisplayName("@Info(\"text\") on a class sets the value element")
        void onClass() {
            assertThat(Annotated.class.getAnnotation(Info.class).value()).isEqualTo("Demo class");
        }

        @Test
        @DisplayName("The same annotation works on a field")
        void onField() throws Exception {
            assertThat(Annotated.class.getField("name").getAnnotation(Info.class).value())
                    .isEqualTo("a field");
        }

        @Test
        @DisplayName("The same annotation works on a method")
        void onMethod() throws Exception {
            assertThat(Annotated.class.getMethod("work").getAnnotation(Info.class).value())
                    .isEqualTo("a method");
        }
    }

    @Nested
    @DisplayName("3. Reading annotations")
    class ReadingSection {

        @Test
        @DisplayName("isAnnotationPresent distinguishes annotated from plain methods")
        void presentVsAbsent() throws Exception {
            assertThat(Annotated.class.getMethod("work").isAnnotationPresent(Ticket.class)).isTrue();
            assertThat(Annotated.class.getMethod("plain").isAnnotationPresent(Ticket.class)).isFalse();
        }

        @Test
        @DisplayName("getAnnotation returns null for a missing annotation")
        void missingIsNull() throws Exception {
            assertThat(Annotated.class.getMethod("plain").getAnnotation(Ticket.class)).isNull();
        }

        @Test
        @DisplayName("getAnnotations returns every runtime annotation on the class")
        void allOnClass() {
            assertThat(Annotated.class.getAnnotations()).hasSize(3);
            assertThat(Annotated.class.getAnnotations())
                    .extracting(a -> a.annotationType().getSimpleName())
                    .containsExactlyInAnyOrder("Marker", "Info", "Ticket");
        }

        @Test
        @DisplayName("Annotations on different elements are independent")
        void independentElements() throws Exception {
            Ticket onClass = Annotated.class.getAnnotation(Ticket.class);
            Ticket onMethod = Annotated.class.getMethod("work").getAnnotation(Ticket.class);
            assertThat(onClass.id()).isNotEqualTo(onMethod.id());
        }
    }

    @Nested
    @DisplayName("4. @Repeatable")
    class RepeatableSection {

        @Test
        @DisplayName("getAnnotationsByType returns every repeat in order")
        void byTypeReturnsAll() {
            Role[] roles = MultiRole.class.getAnnotationsByType(Role.class);
            assertThat(roles).extracting(Role::value).containsExactly("admin", "auditor");
        }

        @Test
        @DisplayName("getAnnotation(Role) is null when the annotation is repeated")
        void getAnnotationNullWhenRepeated() {
            assertThat(MultiRole.class.getAnnotation(Role.class)).isNull();
        }

        @Test
        @DisplayName("Repeats are wrapped in the container annotation")
        void containerPresent() {
            Roles container = MultiRole.class.getAnnotation(Roles.class);
            assertThat(container).isNotNull();
            assertThat(container.value()).hasSize(2);
        }

        @Test
        @DisplayName("A single use is NOT wrapped: getAnnotation works and there is no container")
        void singleUseNotWrapped() {
            assertThat(SingleRole.class.getAnnotation(Role.class).value()).isEqualTo("viewer");
            assertThat(SingleRole.class.getAnnotation(Roles.class)).isNull();
        }

        @Test
        @DisplayName("getAnnotationsByType is empty when the annotation is absent")
        void absent() {
            assertThat(NoRole.class.getAnnotationsByType(Role.class)).isEmpty();
        }
    }

    @Nested
    @DisplayName("5. @Inherited")
    class InheritedSection {

        @Test
        @DisplayName("Subclass sees an @Inherited annotation through isAnnotationPresent")
        void subclassSeesAnnotation() {
            assertThat(BaseService.class.isAnnotationPresent(Audited.class)).isTrue();
            assertThat(ChildService.class.isAnnotationPresent(Audited.class)).isTrue();
        }

        @Test
        @DisplayName("getDeclaredAnnotation ignores inheritance")
        void declaredIgnoresInheritance() {
            assertThat(BaseService.class.getDeclaredAnnotation(Audited.class)).isNotNull();
            assertThat(ChildService.class.getDeclaredAnnotation(Audited.class)).isNull();
        }

        @Test
        @DisplayName("@Inherited has no effect through interfaces")
        void notInheritedFromInterfaces() {
            assertThat(ContractImpl.class.isAnnotationPresent(Audited.class)).isFalse();
        }

        @Test
        @DisplayName("Without @Inherited an annotation stays on the class where it was written")
        void withoutInheritedNotPropagated() {
            // @Marker is not @Inherited; a subclass of Annotated would not have it.
            class SubOfAnnotated extends Annotated {
            }
            assertThat(Annotated.class.isAnnotationPresent(Marker.class)).isTrue();
            assertThat(SubOfAnnotated.class.isAnnotationPresent(Marker.class)).isFalse();
        }
    }

    @Nested
    @DisplayName("6. Annotation processing — mini validator")
    class ValidatorSection {

        @Test
        @DisplayName("Valid object produces no errors")
        void valid() {
            assertThat(CustomAnnotationsDemo.validate(new Person("Ann", 30))).isEmpty();
        }

        @Test
        @DisplayName("Blank String violates @NotBlank")
        void blankName() {
            assertThat(CustomAnnotationsDemo.validate(new Person("   ", 30)))
                    .containsExactly("name: must not be blank");
        }

        @Test
        @DisplayName("null violates @NotBlank")
        void nullName() {
            assertThat(CustomAnnotationsDemo.validate(new Person(null, 30)))
                    .containsExactly("name: must not be blank");
        }

        @Test
        @DisplayName("Value above max violates @Range")
        void tooOld() {
            assertThat(CustomAnnotationsDemo.validate(new Person("Ann", 70)))
                    .containsExactly("age: must be between 18 and 65 but was 70");
        }

        @Test
        @DisplayName("Boundaries of @Range are inclusive")
        void inclusiveBoundaries() {
            assertThat(CustomAnnotationsDemo.validate(new Person("Ann", 18))).isEmpty();
            assertThat(CustomAnnotationsDemo.validate(new Person("Ann", 65))).isEmpty();
            assertThat(CustomAnnotationsDemo.validate(new Person("Ann", 17))).hasSize(1);
        }

        @Test
        @DisplayName("Several violations are reported, sorted by field name")
        void severalViolations() {
            assertThat(CustomAnnotationsDemo.validate(new Person("", 10)))
                    .containsExactly(
                            "age: must be between 18 and 65 but was 10",
                            "name: must not be blank");
        }

        @Test
        @DisplayName("Object without validation annotations is always valid")
        void unannotatedObject() {
            assertThat(CustomAnnotationsDemo.validate(new NoRole())).isEmpty();
        }
    }

    @Nested
    @DisplayName("7. Annotations are interfaces")
    class InterfaceSection {

        @Test
        @DisplayName("An annotation type is an interface that extends Annotation")
        void annotationTypeIsInterface() {
            assertThat(Ticket.class.isAnnotation()).isTrue();
            assertThat(Ticket.class.isInterface()).isTrue();
            assertThat(Annotation.class.isAssignableFrom(Ticket.class)).isTrue();
        }

        @Test
        @DisplayName("An annotation instance reports its type via annotationType()")
        void annotationType() {
            Annotation ticket = Annotated.class.getAnnotation(Ticket.class);
            assertThat(ticket.annotationType()).isEqualTo(Ticket.class);
        }

        @Test
        @DisplayName("Two reads of the same annotation are equal")
        void equalInstances() {
            Ticket first = Annotated.class.getAnnotation(Ticket.class);
            Ticket second = Annotated.class.getAnnotation(Ticket.class);
            assertThat(first).isEqualTo(second);
            assertThat(first.hashCode()).isEqualTo(second.hashCode());
        }

        @Test
        @DisplayName("Annotations with different element values are not equal")
        void differentInstances() throws Exception {
            Ticket onClass = Annotated.class.getAnnotation(Ticket.class);
            Ticket onMethod = Annotated.class.getMethod("work").getAnnotation(Ticket.class);
            assertThat(onClass).isNotEqualTo(onMethod);
        }

        @Test
        @DisplayName("Every custom annotation in this demo has RUNTIME retention")
        void allRuntime() {
            for (Class<? extends Annotation> type : Arrays.asList(
                    Marker.class, Info.class, Ticket.class, Role.class, Roles.class, Audited.class)) {
                assertThat(type.getAnnotation(Retention.class).value())
                        .as(type.getSimpleName())
                        .isEqualTo(RetentionPolicy.RUNTIME);
            }
        }
    }
}
