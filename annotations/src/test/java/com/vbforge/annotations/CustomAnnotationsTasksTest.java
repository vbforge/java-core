package com.vbforge.annotations;

import com.vbforge.annotations.CustomAnnotationsDemo.Annotated;
import com.vbforge.annotations.CustomAnnotationsDemo.Audited;
import com.vbforge.annotations.CustomAnnotationsDemo.AuditedContract;
import com.vbforge.annotations.CustomAnnotationsDemo.BaseService;
import com.vbforge.annotations.CustomAnnotationsDemo.ChildService;
import com.vbforge.annotations.CustomAnnotationsDemo.Info;
import com.vbforge.annotations.CustomAnnotationsDemo.MultiRole;
import com.vbforge.annotations.CustomAnnotationsDemo.NoRole;
import com.vbforge.annotations.CustomAnnotationsDemo.NotBlank;
import com.vbforge.annotations.CustomAnnotationsDemo.Person;
import com.vbforge.annotations.CustomAnnotationsDemo.Range;
import com.vbforge.annotations.CustomAnnotationsDemo.Role;
import com.vbforge.annotations.CustomAnnotationsDemo.Roles;
import com.vbforge.annotations.CustomAnnotationsDemo.SingleRole;
import com.vbforge.annotations.CustomAnnotationsDemo.Ticket;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;

@DisplayName("CustomAnnotationsTasks — your implementations")
class CustomAnnotationsTasksTest {

    // ---- fixtures -------------------------------------------------------------

    /** @Ticket present, labels left at the default. */
    @Ticket(id = "X-1")
    static class NoLabels {
    }

    /** @Ticket only on a method — the class itself carries none. */
    static class OnlyMethodTicket {
        @Ticket(id = "M-1", labels = "hidden")
        public void work() {
        }
    }

    /** @Role written through the explicit container annotation. */
    @Roles({@Role("x"), @Role("y"), @Role("z")})
    static class ExplicitContainer {
    }

    /** Three annotated fields in non-alphabetical declaration order plus a plain one. */
    static class ManyFields {
        @NotBlank
        private String zeta;
        @NotBlank
        private String alpha;
        @NotBlank
        private String mid;
        private String ignored;
        @Info("different annotation")
        private String other;
    }

    /** One explicit range and one that relies entirely on the defaults. */
    static class Scores {
        @Range(min = 1, max = 10)
        private int level;
        @Range
        private int points;
        private int untouched;
    }

    /** Declares the annotation itself AND extends an annotated class. */
    @Audited
    static class AuditedChild extends BaseService {
    }

    private CustomAnnotationsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new CustomAnnotationsTasks();
    }

    // ---- tasks ----------------------------------------------------------------

    @Nested
    @Disabled("Remove @Disabled when you implement ticketId()")
    @DisplayName("ticketId")
    class TicketIdTest {

        @Test
        @DisplayName("Class with @Ticket -> its id (Javadoc example)")
        void present() {
            assertThat(tasks.ticketId(Annotated.class)).isEqualTo(Optional.of("BUG-42"));
        }

        @Test
        @DisplayName("Class without @Ticket -> empty (Javadoc example)")
        void absent() {
            assertThat(tasks.ticketId(NoRole.class)).isEmpty();
        }

        @Test
        @DisplayName("@Ticket with defaulted elements still yields its id")
        void defaultsElsewhere() {
            assertThat(tasks.ticketId(NoLabels.class)).contains("X-1");
        }

        @Test
        @DisplayName("A @Ticket on a method does not count for the class")
        void methodTicketIgnored() {
            assertThat(tasks.ticketId(OnlyMethodTicket.class)).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement ticketLabels()")
    @DisplayName("ticketLabels")
    class TicketLabelsTest {

        @Test
        @DisplayName("Labels in declaration order (Javadoc example)")
        void present() {
            assertThat(tasks.ticketLabels(Annotated.class)).containsExactly("ui", "urgent");
        }

        @Test
        @DisplayName("@Ticket with default labels -> empty (Javadoc example)")
        void defaultLabels() {
            assertThat(tasks.ticketLabels(NoLabels.class)).isEmpty();
        }

        @Test
        @DisplayName("No @Ticket at all -> empty (Javadoc example)")
        void noTicket() {
            assertThat(tasks.ticketLabels(NoRole.class)).isEmpty();
        }

        @Test
        @DisplayName("Labels of a method-level @Ticket are not reported for the class")
        void methodTicketIgnored() {
            assertThat(tasks.ticketLabels(OnlyMethodTicket.class)).isEmpty();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement roleNames()")
    @DisplayName("roleNames")
    class RoleNamesTest {

        @Test
        @DisplayName("Repeated @Role -> all values in order (Javadoc example)")
        void repeated() {
            assertThat(tasks.roleNames(MultiRole.class)).containsExactly("admin", "auditor");
        }

        @Test
        @DisplayName("Single @Role -> one value (Javadoc example)")
        void single() {
            assertThat(tasks.roleNames(SingleRole.class)).containsExactly("viewer");
        }

        @Test
        @DisplayName("No @Role -> empty (Javadoc example)")
        void none() {
            assertThat(tasks.roleNames(NoRole.class)).isEmpty();
        }

        @Test
        @DisplayName("Explicit @Roles container is handled the same way")
        void explicitContainer() {
            assertThat(tasks.roleNames(ExplicitContainer.class)).containsExactly("x", "y", "z");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement fieldsAnnotatedWith()")
    @DisplayName("fieldsAnnotatedWith")
    class FieldsAnnotatedWithTest {

        @Test
        @DisplayName("@NotBlank on Person -> [name] (Javadoc example)")
        void notBlank() {
            assertThat(tasks.fieldsAnnotatedWith(Person.class, NotBlank.class)).containsExactly("name");
        }

        @Test
        @DisplayName("@Range on Person -> [age] (Javadoc example)")
        void range() {
            assertThat(tasks.fieldsAnnotatedWith(Person.class, Range.class)).containsExactly("age");
        }

        @Test
        @DisplayName("Annotation not used on any field -> empty (Javadoc example)")
        void unused() {
            assertThat(tasks.fieldsAnnotatedWith(Person.class, Info.class)).isEmpty();
        }

        @Test
        @DisplayName("Private fields are found and the result is sorted alphabetically")
        void sortedPrivate() {
            assertThat(tasks.fieldsAnnotatedWith(ManyFields.class, NotBlank.class))
                    .containsExactly("alpha", "mid", "zeta");
        }

        @Test
        @DisplayName("Only the requested annotation matches")
        void onlyRequested() {
            assertThat(tasks.fieldsAnnotatedWith(ManyFields.class, Info.class)).containsExactly("other");
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement isDeclaredDirectly()")
    @DisplayName("isDeclaredDirectly")
    class IsDeclaredDirectlyTest {

        @Test
        @DisplayName("Written on the class itself -> true (Javadoc example)")
        void direct() {
            assertThat(tasks.isDeclaredDirectly(BaseService.class, Audited.class)).isTrue();
        }

        @Test
        @DisplayName("Only inherited from the superclass -> false (Javadoc example)")
        void inheritedOnly() {
            assertThat(tasks.isDeclaredDirectly(ChildService.class, Audited.class)).isFalse();
        }

        @Test
        @DisplayName("Annotation absent -> false (Javadoc example)")
        void absent() {
            assertThat(tasks.isDeclaredDirectly(NoRole.class, Audited.class)).isFalse();
        }

        @Test
        @DisplayName("Written on the subclass AND inherited -> true")
        void directAndInherited() {
            assertThat(tasks.isDeclaredDirectly(AuditedChild.class, Audited.class)).isTrue();
        }

        @Test
        @DisplayName("Works on interfaces too")
        void onInterface() {
            assertThat(tasks.isDeclaredDirectly(AuditedContract.class, Audited.class)).isTrue();
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement describeRanges()")
    @DisplayName("describeRanges")
    class DescribeRangesTest {

        @Test
        @DisplayName("Explicit min and max (Javadoc example)")
        void explicit() {
            assertThat(tasks.describeRanges(Person.class)).containsExactly(entry("age", "18..65"));
        }

        @Test
        @DisplayName("Defaults are reported and keys are sorted (Javadoc example)")
        void defaultsAndOrder() {
            assertThat(tasks.describeRanges(Scores.class))
                    .containsExactly(entry("level", "1..10"), entry("points", "0..2147483647"));
        }

        @Test
        @DisplayName("No @Range anywhere -> empty map (Javadoc example)")
        void none() {
            assertThat(tasks.describeRanges(NoRole.class)).isEmpty();
        }

        @Test
        @DisplayName("Fields without @Range are left out")
        void plainFieldsLeftOut() {
            assertThat(tasks.describeRanges(Scores.class)).doesNotContainKey("untouched");
        }
    }
}
