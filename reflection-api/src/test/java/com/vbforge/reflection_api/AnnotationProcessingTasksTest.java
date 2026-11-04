package com.vbforge.reflection_api;

import com.vbforge.reflection_api.AnnotationProcessingDemo.Admin;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Column;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Entity;
import com.vbforge.reflection_api.AnnotationProcessingDemo.NotBlank;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Range;
import com.vbforge.reflection_api.AnnotationProcessingDemo.Tag;
import com.vbforge.reflection_api.AnnotationProcessingDemo.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("AnnotationProcessingTasks — your implementations")
class AnnotationProcessingTasksTest {

    /** Exactly one @Tag: stored directly, no container. */
    @Tag("solo")
    static class Single { }

    /** Columns: an empty @Column value, an explicit one, a static field and a plain field. */
    static class Mixed {
        @Column("")
        int a;

        @Column("bee")
        int b;

        @Column("ignored")
        static int counter;

        int c;
    }

    /** Methods with zero, one and two tags. */
    static class Tagged {
        @Tag("a")
        @Tag("b")
        public void two() { }

        @Tag("x")
        public void one() { }

        public void none() { }
    }

    private AnnotationProcessingTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new AnnotationProcessingTasks();
    }

    @Nested
    @Disabled("Remove @Disabled when you implement annotationNames()")
    @DisplayName("annotationNames")
    class AnnotationNamesTest {

        @Test
        @DisplayName("User → [Audited, Entity, Tags] (the repeated @Tag shows up as its container)")
        void user() {
            assertThat(tasks.annotationNames(User.class)).containsExactly("Audited", "Entity", "Tags");
        }

        @Test
        @DisplayName("Admin → [Audited] (only @Inherited annotations)")
        void admin() {
            assertThat(tasks.annotationNames(Admin.class)).containsExactly("Audited");
        }

        @Test
        @DisplayName("a method without annotations → empty list")
        void noAnnotations() throws Exception {
            assertThat(tasks.annotationNames(User.class.getMethod("plain"))).isEmpty();
        }

        @Test
        @DisplayName("works on fields: name → [Column, NotBlank]")
        void field() throws Exception {
            assertThat(tasks.annotationNames(User.class.getDeclaredField("name")))
                    .containsExactly("Column", "NotBlank");
        }

        @Test
        @DisplayName("works on methods: a @Deprecated method → [Deprecated]")
        void method() throws Exception {
            assertThat(tasks.annotationNames(User.class.getMethod("legacy"))).containsExactly("Deprecated");
        }

        @Test
        @DisplayName("a single @Tag appears as Tag, not Tags")
        void singleTag() {
            assertThat(tasks.annotationNames(Single.class)).containsExactly("Tag");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement fieldsWithAnnotation()")
    @DisplayName("fieldsWithAnnotation")
    class FieldsWithAnnotationTest {

        @Test
        @DisplayName("User + NotBlank → [name]")
        void notBlank() {
            assertThat(tasks.fieldsWithAnnotation(User.class, NotBlank.class)).containsExactly("name");
        }

        @Test
        @DisplayName("User + Range → [age]")
        void range() {
            assertThat(tasks.fieldsWithAnnotation(User.class, Range.class)).containsExactly("age");
        }

        @Test
        @DisplayName("User + Column → [name]")
        void column() {
            assertThat(tasks.fieldsWithAnnotation(User.class, Column.class)).containsExactly("name");
        }

        @Test
        @DisplayName("an annotation that targets types only → empty list")
        void typeOnlyAnnotation() {
            assertThat(tasks.fieldsWithAnnotation(User.class, Entity.class)).isEmpty();
        }

        @Test
        @DisplayName("only fields declared on the class itself are inspected")
        void inheritedFieldsIgnored() {
            assertThat(tasks.fieldsWithAnnotation(Admin.class, NotBlank.class)).isEmpty();
        }

        @Test
        @DisplayName("several matches are returned sorted")
        void sortedResult() {
            assertThat(tasks.fieldsWithAnnotation(Mixed.class, Column.class))
                    .containsExactly("a", "b", "counter");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement columnMapping()")
    @DisplayName("columnMapping")
    class ColumnMappingTest {

        @Test
        @DisplayName("User → {age=age, email=email, name=user_name}")
        void user() {
            Map<String, String> mapping = tasks.columnMapping(User.class);

            assertThat(mapping).containsOnlyKeys("age", "email", "name");
            assertThat(mapping).containsEntry("name", "user_name");
            assertThat(mapping).containsEntry("age", "age");
            assertThat(mapping).containsEntry("email", "email");
        }

        @Test
        @DisplayName("an empty @Column value falls back to the field name")
        void emptyValueFallsBack() {
            assertThat(tasks.columnMapping(Mixed.class)).containsEntry("a", "a");
        }

        @Test
        @DisplayName("an explicit @Column value is used")
        void explicitValue() {
            assertThat(tasks.columnMapping(Mixed.class)).containsEntry("b", "bee");
        }

        @Test
        @DisplayName("static fields are excluded even when annotated")
        void staticExcluded() {
            assertThat(tasks.columnMapping(Mixed.class)).containsOnlyKeys("a", "b", "c");
        }

        @Test
        @DisplayName("entries are sorted by field name")
        void sortedKeys() {
            assertThat(new ArrayList<String>(tasks.columnMapping(User.class).keySet()))
                    .containsExactly("age", "email", "name");
        }

        @Test
        @DisplayName("a class without fields → empty map")
        void noFields() {
            assertThat(tasks.columnMapping(Admin.class)).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement tagValues()")
    @DisplayName("tagValues")
    class TagValuesTest {

        @Test
        @DisplayName("repeated @Tag → both values in declaration order")
        void repeated() {
            assertThat(tasks.tagValues(User.class)).containsExactly("core", "public");
        }

        @Test
        @DisplayName("a single @Tag → one value")
        void single() {
            assertThat(tasks.tagValues(Single.class)).containsExactly("solo");
        }

        @Test
        @DisplayName("@Tag is not @Inherited → Admin has none")
        void notInherited() {
            assertThat(tasks.tagValues(Admin.class)).isEmpty();
        }

        @Test
        @DisplayName("works on methods with two, one and zero tags")
        void methods() throws Exception {
            assertThat(tasks.tagValues(Tagged.class.getMethod("two"))).containsExactly("a", "b");
            assertThat(tasks.tagValues(Tagged.class.getMethod("one"))).containsExactly("x");
            assertThat(tasks.tagValues(Tagged.class.getMethod("none"))).isEmpty();
        }
    }
}
