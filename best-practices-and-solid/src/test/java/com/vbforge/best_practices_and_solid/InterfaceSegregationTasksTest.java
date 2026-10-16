package com.vbforge.best_practices_and_solid;

import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Eatable;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Human;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Robot;
import com.vbforge.best_practices_and_solid.InterfaceSegregationDemo.Workable;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.Arrays;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("InterfaceSegregationTasks — your implementations")
class InterfaceSegregationTasksTest {

    private InterfaceSegregationTasks tasks;

    @BeforeEach
    void setUp() {
        tasks = new InterfaceSegregationTasks();
    }

    /** Plays both roles without being a Human — proves the signature asks for roles, not classes. */
    private static class Cyborg implements Workable, Eatable {
        @Override
        public int work() {
            return 3;
        }

        @Override
        public String eat() {
            return "bolts";
        }
    }

    @Nested
    @Disabled("Remove @Disabled when you implement totalWorkUnits()")
    @DisplayName("totalWorkUnits(workers)")
    class TotalWorkUnitsTest {

        @Test
        @DisplayName("adds up humans and robots")
        void humansAndRobots() {
            assertThat(tasks.totalWorkUnits(Arrays.<Workable>asList(new Human("Ann", 8, "soup"), new Robot(24))))
                    .isEqualTo(32);
        }

        @Test
        @DisplayName("an empty list produces zero units")
        void emptyList() {
            assertThat(tasks.totalWorkUnits(Collections.<Workable>emptyList())).isEqualTo(0);
        }

        @Test
        @DisplayName("works for lambdas too")
        void lambdas() {
            Workable five = () -> 5;
            Workable seven = () -> 7;

            assertThat(tasks.totalWorkUnits(Arrays.asList(five, seven))).isEqualTo(12);
        }

        @Test
        @DisplayName("accepts a list of a more specific type")
        void specificListType() {
            assertThat(tasks.totalWorkUnits(Arrays.asList(new Robot(1), new Robot(2)))).isEqualTo(3);
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement lunchBreak()")
    @DisplayName("lunchBreak(beings)")
    class LunchBreakTest {

        @Test
        @DisplayName("collects what everyone eats, in order")
        void collectsMeals() {
            Eatable snack = () -> "snack";

            assertThat(tasks.lunchBreak(Arrays.<Eatable>asList(new Human("Ann", 8, "soup"), snack)))
                    .containsExactly("Ann eats soup", "snack");
        }

        @Test
        @DisplayName("an empty list produces no meals")
        void emptyList() {
            assertThat(tasks.lunchBreak(Collections.<Eatable>emptyList())).isEmpty();
        }

        @Test
        @DisplayName("accepts a list of a more specific type")
        void specificListType() {
            assertThat(tasks.lunchBreak(Arrays.asList(new Human("Bob", 1, "rice")))).containsExactly("Bob eats rice");
        }
    }


    @Nested
    @Disabled("Remove @Disabled when you implement shift()")
    @DisplayName("shift(worker)")
    class ShiftTest {

        @ParameterizedTest(name = "{0}, {1} units, {2} -> {3}")
        @CsvSource({
                "Ann, 8,  soup,  '8:Ann eats soup'",
                "Bob, 0,  rice,  '0:Bob eats rice'",
                "Cy,  12, pasta, '12:Cy eats pasta'"
        })
        @DisplayName("joins work units and meal with a colon")
        void humans(String name, int units, String meal, String expected) {
            assertThat(tasks.shift(new Human(name, units, meal))).isEqualTo(expected);
        }

        @Test
        @DisplayName("works for any type that plays both roles")
        void anyTypeWithBothRoles() {
            assertThat(tasks.shift(new Cyborg())).isEqualTo("3:bolts");
        }
    }
}
