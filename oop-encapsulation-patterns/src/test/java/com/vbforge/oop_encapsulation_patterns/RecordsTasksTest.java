package com.vbforge.oop_encapsulation_patterns;

import com.vbforge.oop_encapsulation_patterns.RecordsTasks.Email;
import com.vbforge.oop_encapsulation_patterns.RecordsTasks.Rectangle;
import com.vbforge.oop_encapsulation_patterns.RecordsTasks.Samples;
import com.vbforge.oop_encapsulation_patterns.RecordsTasks.Team;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * RecordsTasksTest — tests for every method in {@link RecordsTasks}.
 *
 * <p><b>Getting started:</b>
 * <ol>
 *   <li>Open {@code RecordsTasks.java} and implement a method.</li>
 *   <li>Remove the {@code @Disabled} annotation from the corresponding {@code @Nested} class.</li>
 *   <li>Run {@code mvn test -Dtest=RecordsTasksTest} — those tests should go GREEN ✅</li>
 *   <li>Repeat until all 7 task groups are GREEN.</li>
 * </ol>
 */
@DisplayName("RecordsTasks — your implementations")
class RecordsTasksTest {

    private RecordsTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new RecordsTasks();
    }

    // ── Task 1: Email ─────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Email()")
    @DisplayName("Task 1 — Email (compact constructor)")
    class EmailTest {

        @ParameterizedTest(name = "\"{0}\" → \"{1}\"")
        @CsvSource({
                "'  Bob@Example.COM ', bob@example.com",
                "a@b,                 a@b",
                "BOB@X.IO,            bob@x.io",
        })
        @DisplayName("parametrized: normalisation")
        void normalises(String raw, String expected) {
            assertThat(new Email(raw).value()).isEqualTo(expected);
        }

        @ParameterizedTest(name = "\"{0}\" is rejected")
        @CsvSource({
                "bob.example.com",
                "a@b@c",
                "@example.com",
                "bob@",
                "'   '",
        })
        @DisplayName("parametrized: invalid addresses")
        void rejectsInvalid(String raw) {
            assertThatThrownBy(() -> new Email(raw)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("null → IllegalArgumentException")
        void rejectsNull() {
            assertThatThrownBy(() -> new Email(null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("equality follows the normalised value")
        void equality() {
            assertThat(new Email(" Bob@Example.com ")).isEqualTo(new Email("bob@example.com"));
        }
    }

    // ── Task 2: Team ──────────────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement Team()")
    @DisplayName("Task 2 — Team (compact constructor)")
    class TeamTest {

        @Test
        @DisplayName("keeps name and members")
        void keepsValues() {
            Team team = new Team("Core", Arrays.asList("ann", "bob"));
            assertThat(team.name()).isEqualTo("Core");
            assertThat(team.members()).containsExactly("ann", "bob");
        }

        @Test
        @DisplayName("later changes to the caller's list do not change the team")
        void independentCopy() {
            List<String> list = new ArrayList<>(Arrays.asList("ann"));
            Team team = new Team("Core", list);
            list.add("bob");
            assertThat(team.members()).containsExactly("ann");
        }

        @Test
        @DisplayName("the members list is read-only")
        void readOnly() {
            Team team = new Team("Core", Arrays.asList("ann"));
            assertThatThrownBy(() -> team.members().add("x")).isInstanceOf(UnsupportedOperationException.class);
        }

        @Test
        @DisplayName("a blank or null name → IllegalArgumentException")
        void badName() {
            assertThatThrownBy(() -> new Team("  ", Arrays.asList("ann"))).isInstanceOf(IllegalArgumentException.class);
            assertThatThrownBy(() -> new Team(null, Arrays.asList("ann"))).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("a null members list → IllegalArgumentException")
        void nullMembers() {
            assertThatThrownBy(() -> new Team("Core", null)).isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        @DisplayName("an empty members list is allowed")
        void emptyMembers() {
            assertThat(new Team("Core", Collections.<String>emptyList()).members()).isEmpty();
        }
    }

    // ── Task 3: Rectangle.square ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement square()")
    @DisplayName("Task 3 — Rectangle.square()")
    class SquareTest {

        @ParameterizedTest(name = "square({0})")
        @CsvSource({"1", "3", "100"})
        @DisplayName("parametrized: width equals height equals the side")
        void squares(int side) {
            assertThat(Rectangle.square(side)).isEqualTo(new Rectangle(side, side));
        }

        @Test
        @DisplayName("toString shows both components")
        void toStringFormat() {
            assertThat(Rectangle.square(3).toString()).isEqualTo("Rectangle[width=3, height=3]");
        }

        @ParameterizedTest(name = "square({0}) → IllegalArgumentException")
        @CsvSource({"0", "-4"})
        @DisplayName("a non-positive side is rejected")
        void rejectsNonPositive(int side) {
            assertThatThrownBy(() -> Rectangle.square(side)).isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 4: Rectangle.scaledBy ────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement scaledBy()")
    @DisplayName("Task 4 — Rectangle.scaledBy()")
    class ScaledByTest {

        @ParameterizedTest(name = "2x3 scaled by {0} → {1}x{2}")
        @CsvSource({
                "1, 2, 3",
                "2, 4, 6",
                "10, 20, 30",
        })
        @DisplayName("parametrized: scaling")
        void scales(int factor, int width, int height) {
            assertThat(new Rectangle(2, 3).scaledBy(factor)).isEqualTo(new Rectangle(width, height));
        }

        @Test
        @DisplayName("returns a new record and leaves the original untouched")
        void returnsNew() {
            Rectangle original = new Rectangle(2, 3);
            Rectangle scaled = original.scaledBy(2);
            assertThat(scaled).isNotSameAs(original);
            assertThat(original).isEqualTo(new Rectangle(2, 3));
        }

        @ParameterizedTest(name = "scaledBy({0}) → IllegalArgumentException")
        @CsvSource({"0", "-1"})
        @DisplayName("a non-positive factor is rejected")
        void rejectsNonPositive(int factor) {
            assertThatThrownBy(() -> new Rectangle(2, 3).scaledBy(factor))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    // ── Task 5: Samples.equals ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement equals()")
    @DisplayName("Task 5 — Samples.equals()")
    class SamplesEqualsTest {

        @Test
        @DisplayName("same numbers in the same order → equal")
        void sameContent() {
            assertThat(new Samples(new int[] {1, 2}).equals(new Samples(new int[] {1, 2}))).isTrue();
        }

        @Test
        @DisplayName("different order → not equal")
        void differentOrder() {
            assertThat(new Samples(new int[] {1, 2}).equals(new Samples(new int[] {2, 1}))).isFalse();
        }

        @Test
        @DisplayName("different length → not equal")
        void differentLength() {
            assertThat(new Samples(new int[] {1, 2}).equals(new Samples(new int[] {1}))).isFalse();
        }

        @Test
        @DisplayName("two empty samples → equal")
        void bothEmpty() {
            assertThat(new Samples(new int[0]).equals(new Samples(new int[0]))).isTrue();
        }

        @Test
        @DisplayName("null and other types → not equal")
        void nullAndOtherTypes() {
            Samples samples = new Samples(new int[] {1});
            assertThat(samples.equals(null)).isFalse();
            assertThat(samples.equals("1")).isFalse();
        }

        @Test
        @DisplayName("an object equals itself")
        void reflexive() {
            Samples samples = new Samples(new int[] {1});
            assertThat(samples.equals(samples)).isTrue();
        }
    }

    // ── Task 6: Samples.hashCode ──────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement hashCode()")
    @DisplayName("Task 6 — Samples.hashCode()")
    class SamplesHashCodeTest {

        @Test
        @DisplayName("equal contents → equal hash codes")
        void equalContents() {
            assertThat(new Samples(new int[] {1, 2}).hashCode()).isEqualTo(new Samples(new int[] {1, 2}).hashCode());
        }

        @Test
        @DisplayName("two empty samples → equal hash codes")
        void bothEmpty() {
            assertThat(new Samples(new int[0]).hashCode()).isEqualTo(new Samples(new int[0]).hashCode());
        }

        @Test
        @DisplayName("the hash code is stable across calls")
        void stable() {
            Samples samples = new Samples(new int[] {4, 5, 6});
            assertThat(samples.hashCode()).isEqualTo(samples.hashCode());
        }
    }

    // ── Task 7: componentNames ────────────────────────────────────────────────

    @Nested
    @Disabled("Remove @Disabled when you implement componentNames()")
    @DisplayName("Task 7 — componentNames()")
    class ComponentNamesTest {

        @Test
        @DisplayName("Rectangle → [width, height]")
        void rectangle() {
            assertThat(tasks.componentNames(Rectangle.class)).containsExactly("width", "height");
        }

        @Test
        @DisplayName("Email → [value]")
        void email() {
            assertThat(tasks.componentNames(Email.class)).containsExactly("value");
        }

        @Test
        @DisplayName("Team → [name, members] (declaration order, not alphabetical)")
        void team() {
            assertThat(tasks.componentNames(Team.class)).containsExactly("name", "members");
        }

        @Test
        @DisplayName("a regular class → []")
        void regularClass() {
            assertThat(tasks.componentNames(String.class)).isEmpty();
        }

        @Test
        @DisplayName("an interface → []")
        void anInterface() {
            assertThat(tasks.componentNames(Runnable.class)).isEmpty();
        }
    }
}
